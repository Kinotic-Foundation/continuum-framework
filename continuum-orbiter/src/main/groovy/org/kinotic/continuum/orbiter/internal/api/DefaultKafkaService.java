/*
 *
 * Copyright 2008-2021 Kinotic and the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kinotic.continuum.orbiter.internal.api;

import org.kinotic.continuum.api.config.ContinuumProperties;
import org.kinotic.continuum.orbiter.api.KafkaService;
import org.kinotic.continuum.orbiter.api.domain.KafkaConsumerGroupInfo;
import org.kinotic.continuum.orbiter.api.domain.KafkaConsumerInstanceAssignment;
import org.kinotic.continuum.orbiter.api.domain.KafkaConsumerInstanceInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PreDestroy;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Monitors Kafka Consumers and ensures they are not to far behind current offset
 *
 *
 * Created by Navid Mitchell on 11/23/20
 */
@Component
public class DefaultKafkaService implements ApplicationListener<ApplicationReadyEvent>, KafkaService {

    private static final Logger log = LoggerFactory.getLogger(DefaultKafkaService.class);

    private static final String CONSUMERS_TO_MONITOR_PATH = "/orbiter/consumerToMonitor";

    @Autowired
    private ContinuumProperties continuumProperties;

    @Autowired
    private CuratorFramework curator;

    private Admin kafkaAdmin;

    private final ScheduledExecutorService monitorExecutor = Executors.newSingleThreadScheduledExecutor();

    private final AtomicBoolean shutdown = new AtomicBoolean(false);

    private TreeCache consumersToMonitorCache;

    private ConcurrentHashMap<String, KafkaConsumerGroupInfo> monitoredConsumerStats = new ConcurrentHashMap<>();



    @Override
    public void onApplicationEvent(ApplicationReadyEvent event){
        try {
            initialization();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void initialization() throws Exception{
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, continuumProperties.getKafkaBootstrapServers());

        kafkaAdmin = Admin.create(configs);

        monitorExecutor.scheduleWithFixedDelay(new ConsumerMonitorWorker(), 10, 600, TimeUnit.SECONDS);

        // create root path in zookeeper if it does not exist
        curator.create().orSetData().creatingParentsIfNeeded().forPath(CONSUMERS_TO_MONITOR_PATH);

        curator.create().orSetData().creatingParentsIfNeeded().forPath(CONSUMERS_TO_MONITOR_PATH+"/"+"gpsFixGateway", "test".getBytes());
        curator.create().orSetData().creatingParentsIfNeeded().forPath(CONSUMERS_TO_MONITOR_PATH+"/"+"emberCountsGateway", "test".getBytes());

        consumersToMonitorCache = TreeCache.newBuilder(curator, CONSUMERS_TO_MONITOR_PATH)
                                           .build();
        consumersToMonitorCache.getListenable()
                               .addListener((client, event) -> {

                                   if(event.getType() == TreeCacheEvent.Type.NODE_ADDED){

                                       String consumerGroupId = getConsumerGroupId(event.getData());

                                       monitoredConsumerStats.put(consumerGroupId, new KafkaConsumerGroupInfo(consumerGroupId));

                                   }else if(event.getType() == TreeCacheEvent.Type.NODE_REMOVED){

                                       String consumerGroupId = getConsumerGroupId(event.getData());
                                       monitoredConsumerStats.remove(consumerGroupId);
                                   }
                               });
        consumersToMonitorCache.start();
    }

    @PreDestroy
    void cleanup() {
        shutdown.set(true);
        monitorExecutor.shutdownNow();
        if(kafkaAdmin != null) {
            kafkaAdmin.close(Duration.of(1, ChronoUnit.MINUTES));
        }

        if(consumersToMonitorCache != null){
            consumersToMonitorCache.close();
        }
    }

    @Override
    public Flux<KafkaConsumerGroupInfo> findAllKafkaConsumers() {
        return Flux.create(sink -> {
            ListConsumerGroupsResult result = kafkaAdmin.listConsumerGroups();
            result.all()
                  .whenComplete((consumerGroupListings, throwable) -> {
                      if(throwable == null){
                          List<Mono<KafkaConsumerGroupInfo>> stats = new ArrayList<>(consumerGroupListings.size());
                          for(ConsumerGroupListing consumerGroupListing: consumerGroupListings){
                              stats.add(lookupStats(consumerGroupListing.groupId()));
                          }
                          Flux<KafkaConsumerGroupInfo> allStatsFlux = Flux.concat(stats);
                          Disposable disposable = allStatsFlux
                                                    .doOnNext(sink::next)
                                                    .doOnComplete(sink::complete)
                                                    .doOnError(sink::error)
                                                    .subscribe();
                          sink.onCancel(disposable);
                      }else{
                          sink.error(throwable);
                      }
                  });
        });
    }

    /**
     * Gets the consumer group id from the {@link ChildData} which is always the last segment of the path after CONSUMERS_TO_MONITOR_PATH
     * @param childData to get the id from
     * @return the consumer group id
     */
    private String getConsumerGroupId(ChildData childData){
        return StringUtils.substringAfter(childData.getPath(), CONSUMERS_TO_MONITOR_PATH+"/");
    }

    private Mono<KafkaConsumerGroupInfo> lookupStats(String groupId){
        return Mono.create(sink -> {
            DescribeConsumerGroupsResult describeResult = kafkaAdmin.describeConsumerGroups(List.of(groupId));
            describeResult.describedGroups()
                          .get(groupId)
                          .whenComplete((consumerGroupDescription, throwable) -> {
                              if(throwable == null){
                                  // The KafkaConsumerGroupStats will contain all of the consumer instance info for the requested Consumer GroupId
                                  KafkaConsumerGroupInfo ret = new KafkaConsumerGroupInfo();
                                  ret.setGroupId(groupId);
                                  ret.setGroupState(consumerGroupDescription.state());
                                  ret.setCoordinator(getCoordinatorLabel(consumerGroupDescription.coordinator()));
                                  ret.setPartitionAssignor(consumerGroupDescription.partitionAssignor());

                                  // Build a KafkaConsumerInstanceAssignment for every TopicPartition that the consumer group has an instance assignment for
                                  // This is used to easily add all needed data to the KafkaConsumerInstanceAssignment by reference in the async calls below
                                  Map<TopicPartition, KafkaConsumerInstanceAssignment> topicConsumerInstanceMap = new HashMap<>();

                                  for(MemberDescription memberDescription: consumerGroupDescription.members()){

                                      KafkaConsumerInstanceInfo consumerInstanceStats = new KafkaConsumerInstanceInfo();
                                      consumerInstanceStats.setMemberId(memberDescription.consumerId());
                                      consumerInstanceStats.setGroupInstanceId(memberDescription.groupInstanceId().orElse(null));
                                      consumerInstanceStats.setClientId(memberDescription.clientId());
                                      consumerInstanceStats.setHost(memberDescription.host());

                                      ret.getConsumerInstances().add(consumerInstanceStats);

                                      for(TopicPartition topicPartition: memberDescription.assignment().topicPartitions()){
                                          KafkaConsumerInstanceAssignment instanceAssignment = new KafkaConsumerInstanceAssignment();
                                          instanceAssignment.setTopic(topicPartition.topic());
                                          instanceAssignment.setPartition(topicPartition.partition());

                                          consumerInstanceStats.getInstanceAssignments().add(instanceAssignment);
                                          topicConsumerInstanceMap.put(topicPartition, instanceAssignment);
                                      }
                                  }

                                  // Now get the current topic position for all consumer instances
                                  ListConsumerGroupOffsetsResult result = kafkaAdmin.listConsumerGroupOffsets(groupId);
                                  result.partitionsToOffsetAndMetadata()
                                        .whenComplete((topicOffsetMap, throwable1) -> {
                                            if(throwable1 == null){

                                                // Update all of the KafkaConsumerInstanceAssignment for any TopicPartition found
                                                for(Map.Entry<TopicPartition, OffsetAndMetadata> topicOffsetEntry: topicOffsetMap.entrySet()){

                                                    TopicPartition topicPartition = topicOffsetEntry.getKey();
                                                    OffsetAndMetadata offsetAndMetadata = topicOffsetEntry.getValue();
                                                    KafkaConsumerInstanceAssignment instanceAssignment = topicConsumerInstanceMap.get(topicPartition);
                                                    if(instanceAssignment != null){
                                                        instanceAssignment.setCurrentOffset(offsetAndMetadata.offset());
                                                        instanceAssignment.setOffsetMetadata(offsetAndMetadata.metadata());
                                                    }else{
                                                        // Maybe this could happen due to some timing condition not sure..
                                                        log.warn("No KafkaConsumerInstanceAssignment found for topic: " +topicPartition.topic() + " partition: "+topicPartition.partition()+ " when getting consumer offsets");
                                                    }
                                                }

                                                // Now Get the End offset for all TopicPartitions the Consumer group is active for
                                                Map<TopicPartition, OffsetSpec> listOffsetRequest = new HashMap<>(topicConsumerInstanceMap.size());
                                                for(TopicPartition topicPartition: topicConsumerInstanceMap.keySet()){
                                                    listOffsetRequest.put(topicPartition, OffsetSpec.latest());
                                                }
                                                ListOffsetsResult listOffsetsResult = kafkaAdmin.listOffsets(listOffsetRequest);
                                                listOffsetsResult.all()
                                                                 .whenComplete((offsetInfoMap, throwable2) -> {
                                                                     if(throwable2 == null){
                                                                         for(Map.Entry<TopicPartition,ListOffsetsResult.ListOffsetsResultInfo> offsetInfoEntry : offsetInfoMap.entrySet()){
                                                                             TopicPartition topicPartition = offsetInfoEntry.getKey();
                                                                             ListOffsetsResult.ListOffsetsResultInfo offsetInfo = offsetInfoEntry.getValue();
                                                                             KafkaConsumerInstanceAssignment instanceAssignment = topicConsumerInstanceMap.get(topicPartition);
                                                                             if(instanceAssignment != null){
                                                                                 instanceAssignment.setLogEndOffset(offsetInfo.offset());
                                                                                 instanceAssignment.setLag(instanceAssignment.getLogEndOffset() - instanceAssignment.getCurrentOffset());
                                                                             }else{
                                                                                 // Maybe this could happen due to some timing condition not sure..
                                                                                 log.warn("No KafkaConsumerInstanceAssignment found for topic: " +topicPartition.topic() + " partition: "+topicPartition.partition() +" When getting Topic End offset");
                                                                             }
                                                                         }

                                                                         // KafkaConsumerGroupStats is finished send to the client
                                                                         sink.success(ret);

                                                                     }else{
                                                                         sink.error(throwable2);
                                                                     }
                                                                 });

                                            }else{
                                                sink.error(throwable1);
                                            }
                                        });
                              }else{
                                  sink.error(throwable);
                              }
                          });
        });
    }

    private String getCoordinatorLabel(Node coordinator){
        return coordinator != null ? coordinator.idString() + " ("+coordinator.host()+":"+coordinator.port()+")" : "Unknown";
    }

    class ConsumerMonitorWorker implements Runnable {

        private void checkShutdown() throws InterruptedException{
            if(shutdown.get()){
                throw new InterruptedException("Worker was shutdown");
            }
        }


        @Override
        public void run() {
//            if(!shutdown.get()){
//                try {
//
//                    Map<String, ChildData> consumersToMonitor = consumersToMonitorCache.getCurrentChildren(CONSUMERS_TO_MONITOR_PATH);
//                    if(consumersToMonitor != null){
//
//
//
//                    }else {
//                        log.debug("No kafka consumers configured for monitoring");
//                    }
//
//
//                } catch (InterruptedException e) {
//                    // we do nothing will happen on shutdown
//                } catch (ExecutionException e) {
//                    log.error("ExecutionException in ConsumerMonitorWorker", e.getCause());
//                } catch (RuntimeException e){
//                    log.error("Unexpected Exception in ConsumerMonitorWorker", e);
//                }
//
//            }
        }

    }



}
