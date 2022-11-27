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

package org.kinotic.git;

import org.kinotic.git.event.GitCommitOccurred;
import org.kinotic.git.util.GitUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class GitRepositoryWatcher  implements ApplicationListener<GitCommitOccurred>, Runnable {

    private static final Logger log = LoggerFactory.getLogger(GitRepositoryWatcher.class);

    private final String repositoryURL;
    private final List<Consumer<GitRecord>> gitRecordConsumers;

    private final BlockingQueue<String> queue = new ArrayBlockingQueue<>(10);

    public GitRepositoryWatcher(String repositoryURL, Consumer<GitRecord>... gitRecordConsumers){
        this.repositoryURL = repositoryURL;
        this.gitRecordConsumers = Arrays.asList(gitRecordConsumers);
        Executors.newSingleThreadExecutor().execute(this);
    }

    public void initialize() {
        try {

            queue.offer("initial");

        }catch(Exception ex){
            log.error("Exception occurred while getting and/or building git repo/tree.", ex);
        }
    }

    @Override
    public void onApplicationEvent(GitCommitOccurred event) {

        log.info("GitRepositoryWatcher: We have received a GitCommitOccurred event.");
        queue.offer("event");

    }

    @Override
    public void run() {
        while(true) {
            try {
                String message = queue.poll(60, TimeUnit.SECONDS);
                if(message != null){

                    FileRepositoryBuilder repoBuilder = new FileRepositoryBuilder();
                    repoBuilder.setGitDir(new File(this.repositoryURL+"/.git"));
                    Repository repository = repoBuilder.build();

                    try {

                        if(message.equals("initial")){
                            buildInitialDataSet(repository);
                        }else if(message.equals("event")){
                            processEvent(repository);
                        }else{
                            log.error("We encountered a message from the queue that doesn't belong. message : "+ message);
                        }

                    }catch(Exception e){
                        log.error("Exception occurred during processing of initial/event.", e);
                    }finally{
                        if(repository != null){
                            repository.close();
                        }
                    }
                }

            } catch (Exception e) {
                log.error("encountered a queue or repository build error. ", e);
            }
        }
    }

    private void buildInitialDataSet(final Repository repository) throws IOException {
        ObjectId master = repository.resolve(Constants.HEAD);
        List<String> paths = GitUtils.getFilePathsWithinGitCommitPath(repository, master.getName(), "");

        for(String path : paths){
            try {

                ObjectLoader loader = GitUtils.getGitObjectLoaderFromDiff(repository, path, Constants.HEAD);
                processGitRecord(new GitRecord(this.repositoryURL, GitOperation.INITIAL, Path.of(this.repositoryURL, path), loader, null));

            } catch (IOException e) {
                log.error("error reading git object  -> ", e);
            }
        }
    }

    private void processEvent(final Repository repository) throws IOException {
        log.info("GitRepositoryWatcher: starting reaction to git commit event.");

        ObjectReader reader;
        ObjectId headTree = repository.resolve("HEAD^{tree}");
        ObjectId previousHeadTree = repository.resolve("HEAD^^{tree}");

        reader = repository.newObjectReader();
        CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
        oldTreeIter.reset(reader, previousHeadTree);
        CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
        newTreeIter.reset(reader, headTree);

        Git git = null;
        try {
            git = new Git(repository);
            final List<DiffEntry> diffs = git.diff()
                    .setNewTree(newTreeIter)
                    .setOldTree(oldTreeIter)
                    .call();

            for(DiffEntry entry : diffs){
                String pathForNeededObject = entry.getPath(DiffEntry.Side.NEW);
                String revString = Constants.HEAD;

                if(entry.getChangeType() == DiffEntry.ChangeType.DELETE){
                    pathForNeededObject = entry.getPath(DiffEntry.Side.OLD);
                    revString = "HEAD^";
                }

                try {
                    ObjectLoader objectLoader = GitUtils.getGitObjectLoaderFromDiff(repository, pathForNeededObject, revString);

                    // We pass the ObjectLoader down so that we don't have to open an InputStream.  If nobody picks up the message
                    // then the input stream will never be opened.
                    if(entry.getChangeType() == DiffEntry.ChangeType.ADD){

                        processGitRecord(new GitRecord(this.repositoryURL, GitOperation.ADD, Path.of(this.repositoryURL, pathForNeededObject), objectLoader, null));

                    }else if(entry.getChangeType() == DiffEntry.ChangeType.MODIFY){
                        // here we pass in a diff text for this modification and file.
                        // this way if the consumer wants to do something with it they can.
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        try (DiffFormatter formatter = new DiffFormatter(outputStream)) {
                            formatter.setRepository(repository);
                            formatter.format(entry);
                        }
                        String diff = outputStream.toString(StandardCharsets.UTF_8);

                        ObjectLoader oldObjectLoader = GitUtils.getGitObjectLoaderFromDiff(repository, pathForNeededObject, "HEAD^");

                        GitRecord record = new GitRecord(this.repositoryURL, GitOperation.MODIFY, Path.of(this.repositoryURL, pathForNeededObject), objectLoader, oldObjectLoader);
                        record.setDiff(diff);
                        processGitRecord(record);

                    }else if(entry.getChangeType() == DiffEntry.ChangeType.DELETE){

                        processGitRecord(new GitRecord(this.repositoryURL, GitOperation.DELETE, Path.of(this.repositoryURL, pathForNeededObject), objectLoader, null));

                    }else if(entry.getChangeType() == DiffEntry.ChangeType.RENAME){
                        ObjectLoader oldObjectLoader = GitUtils.getGitObjectLoaderFromDiff(repository, pathForNeededObject, "HEAD^");

                        processGitRecord(new GitRecord(this.repositoryURL, GitOperation.RENAME, Path.of(this.repositoryURL, pathForNeededObject), objectLoader, oldObjectLoader));

                    }else if(entry.getChangeType() == DiffEntry.ChangeType.COPY){
                        ObjectLoader oldObjectLoader = GitUtils.getGitObjectLoaderFromDiff(repository, pathForNeededObject, "HEAD^");

                        processGitRecord(new GitRecord(this.repositoryURL, GitOperation.COPY, Path.of(this.repositoryURL, pathForNeededObject), objectLoader, oldObjectLoader));

                    }
                } catch (IOException e) {
                    log.error("error reading git object -> ", e);
                }
            }

            log.info("GitRepositoryWatcher: done reacting to git commit event.");

        }catch(Exception e){
            log.error("Exception occurred while diffing two git trees.", e);
        }finally{
            if(git != null){
                git.close();
            }
            // since we may have encountered other git commits after this iteration started
            // we clear the queue and wait for next commit.  Expectation is commits happen often.
            queue.clear();
        }
    }

    private void processGitRecord(GitRecord record){
        try {
            for(Consumer<GitRecord> consumer : this.gitRecordConsumers){
                consumer.accept(record);
            }
        }catch (Exception e){
            log.error("GitRecord consumer threw exception.", e);
        }
    }

    public enum GitOperation {
        /** Files that exist when inital walk through happens **/
         INITIAL,
         /** Add a new file to the project */
        ADD,

        /** Modify an existing file in the project (content and/or mode) */
        MODIFY,

        /** Delete an existing file from the project */
        DELETE,

        /** Rename an existing file to a new location */
        RENAME,

        /** Copy an existing file to a new location, keeping the original */
        COPY
    }
    public static class GitRecord {

        private final String repository;
        private final GitOperation operation;
        private final Path path;
        private final ObjectLoader currentObjectLoader;
        private final ObjectLoader previousObjectLoader;
        private String diff;

        public GitRecord(String repository, GitOperation operation, Path path, ObjectLoader currentObjectLoader, ObjectLoader previousObjectLoader) {
            this.repository = repository;
            this.operation = operation;
            this.path = path;
            this.currentObjectLoader = currentObjectLoader;
            this.previousObjectLoader = previousObjectLoader;
        }

        public String getRepository() {
            return repository;
        }

        public Path getPath() {
            return path;
        }

        public ObjectLoader getCurrentObjectLoader() {
            return currentObjectLoader;
        }

        public ObjectLoader getPreviousObjectLoader() {
            return previousObjectLoader;
        }

        public GitOperation getOperation() {
            return operation;
        }

        public String getDiff() {
            return diff;
        }

        public void setDiff(String diff) {
            this.diff = diff;
        }
    }
}
