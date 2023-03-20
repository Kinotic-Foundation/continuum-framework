package org.kinotic.structures.config;

import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class TestElasticsearchConfiguration {
    public static final ElasticsearchTestContainer ELASTICSEARCH_CONTAINER;
    static {
        ELASTICSEARCH_CONTAINER = ElasticsearchTestContainer.create();
        ELASTICSEARCH_CONTAINER.start();
    }
    @Bean
    @Profile("test")
    public RestHighLevelClient elasticsearchClient() {
        RestClientBuilder builder = RestClient.builder(ELASTICSEARCH_CONTAINER.getHttpHostAddress());
        return new RestHighLevelClient(builder);
    }
    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of("spring.data.elasticsearch.cluster-nodes=" + ELASTICSEARCH_CONTAINER.getHttpHostAddress())
                    .applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
