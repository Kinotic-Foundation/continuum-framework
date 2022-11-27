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

package org.kinotic.git.util;

import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.DepthWalk;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.springframework.util.Assert;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GitUtils {

    /**
     * Provides a method to get a specific File in a specific commit. You can then read the InputStream or get the raw bytes.
     * @param repository - a built Repository that is not null
     * @param pathForNeededObject - path to file existing within the specified git commit tree. (i.e. src/main/groovy/com/zepath/File.groovy)
     * @param revString - git commit string -> HEAD(current state) or HEAD^(previous commit)
     * @return
     * @throws IOException if we have problems reading from the git directory.
     * @throws IllegalStateException if we are not able to find the provided file in the git commit.
     */
    public static ObjectLoader getGitObjectLoaderFromDiff(Repository repository, String pathForNeededObject, String revString) throws IOException {
        Assert.notNull(repository, "You must provide a valid Repository object.");
        Assert.isTrue(!pathForNeededObject.trim().isEmpty(), "You must provide a valid file path that exists within the git commit tree.");
        Assert.isTrue(!revString.trim().isEmpty(), "You must provide a valid git commit id");

        ObjectLoader neededObjectLoader = null;
        RevWalk neededRevWalk = null;
        try {
            neededRevWalk = new RevWalk(repository);
            RevCommit neededRevCommit = null;
            TreeWalk neededTreeWalk = null;
            try {
                neededTreeWalk = new TreeWalk(repository);
                // get the needed tree from the path provided by the DiffEntry and the right commit
                neededRevCommit = neededRevWalk.parseCommit(repository.resolve(revString));

                neededTreeWalk.addTree(neededRevCommit.getTree());
                neededTreeWalk.setRecursive(true);
                neededTreeWalk.setFilter(PathFilter.create(pathForNeededObject));// get file data at commit

                if (!neededTreeWalk.next()) {
                    throw new IllegalStateException("Did not find expected file "+pathForNeededObject);
                }

                ObjectId neededObjectId = neededTreeWalk.getObjectId(0);
                neededObjectLoader = repository.open(neededObjectId);
            }finally{
                if(neededTreeWalk != null){
                    neededTreeWalk.close();
                }
            }

        }finally{
            if(neededRevWalk != null){
                neededRevWalk.dispose();
            }
        }
        return neededObjectLoader;
    }


    /**
     *
     * @param repository
     * @param commit
     * @param path
     * @return
     * @throws IOException
     */
    public static List<String> getFilePathsWithinGitCommitPath(Repository repository, String commit, String path) throws IOException {
        RevCommit revCommit = buildRevCommit(repository, commit);

        RevTree tree = revCommit.getTree();

        List<String> items = new ArrayList<>();

        // shortcut for root-path
        if(path.isEmpty()) {
            TreeWalk treeWalk = null;
            try {
                treeWalk = new TreeWalk(repository);
                treeWalk.addTree(tree);
                treeWalk.setRecursive(true);
                treeWalk.setPostOrderTraversal(false);

                while(treeWalk.next()) {
                    items.add(treeWalk.getPathString());
                }
            }finally{
                if(treeWalk != null){
                    treeWalk.close();
                }
            }
        } else {
            // now try to find a specific file
            TreeWalk treeWalk = null;
            try {
                treeWalk = buildTreeWalk(repository, tree, path);
                if((treeWalk.getFileMode(0).getBits() & FileMode.TYPE_TREE) == 0) {
                    throw new IllegalStateException("Tried to read the elements of a non-tree for commit '" + commit + "' and path '" + path + "', had filemode " + treeWalk.getFileMode(0).getBits());
                }

                TreeWalk dirWalk = null;
                try {
                    dirWalk = new TreeWalk(repository);
                    dirWalk.addTree(treeWalk.getObjectId(0));
                    dirWalk.setRecursive(true);
                    while(dirWalk.next()) {
                        items.add(dirWalk.getPathString());
                    }
                }finally{
                    if(dirWalk != null){
                        dirWalk.close();
                    }
                }
            }finally{
                if(treeWalk != null){
                    treeWalk.close();
                }
            }
        }

        return items;
    }

    private static RevCommit buildRevCommit(Repository repository, String commit) throws IOException {
        DepthWalk.RevWalk revWalk = null;
        RevCommit revCommit = null;
        try {
            revWalk = new DepthWalk.RevWalk(repository, 3);
            revCommit = revWalk.parseCommit(ObjectId.fromString(commit));
        }finally{
            if(revWalk != null){
                revWalk.close();
            }
        }
        return revCommit;
    }

    private static TreeWalk buildTreeWalk(Repository repository, RevTree tree, final String path) throws IOException {
        TreeWalk treeWalk = TreeWalk.forPath(repository, path, tree);
        if(treeWalk == null) {
            throw new FileNotFoundException("Did not find expected file '" + path + "' in tree '" + tree.getName() + "'");
        }
        return treeWalk;
    }
}