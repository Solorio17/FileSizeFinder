package com.csc161j.Exam2;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

public class FileTree implements Iterable<FileNode> {

    private FileNode root;

    public FileTree(String path) {
        root = new FileNode(path);
        buildTree(root);
    }

    /** Return a depth-first post-order traversal iterator. */
    @Override
    public Iterator<FileNode> iterator() {
        return new DepthFirstIterator();
    }

    /** Recursively build the file and directory tree. */
    private void buildTree(FileNode fileNode) {
        File[] children = fileNode.getFile().listFiles();
        if (children == null) {
            return;  // Regular file, empty/unreadable directory, or I/O error
        }
        for (File child : children) {
            FileNode childNode = new FileNode(child);
            fileNode.getChildNodes().add(childNode);
            // Recurse only for actual directories.
            if (child.isDirectory()) {
                buildTree(childNode);
            }
        }
    }

    /** Two-stack depth-first post-order traversal. */
    private class DepthFirstIterator implements Iterator<FileNode> {
        private final Deque<FileNode> postOrder = new ArrayDeque<>();

        public DepthFirstIterator() {
            Deque<FileNode> stack = new ArrayDeque<>();
            stack.push(root);
            while (!stack.isEmpty()) {
                FileNode current = stack.pop();
                postOrder.push(current);
                // Push children in their stored order. The second stack
                // reverses this, producing left-to-right post-order.
                for (FileNode child : current.getChildNodes()) {
                    stack.push(child);
                }
            }
        }

        @Override
        public boolean hasNext() {
            return !postOrder.isEmpty();
        }

        @Override
        public FileNode next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return postOrder.pop();
        }
    }

    /** Returns a breadth-first traversal iterator using a queue. */
    public Iterator<FileNode> breadthFirstIterator() {
        return new BreadthFirstIterator();
    }

    private class BreadthFirstIterator implements Iterator<FileNode> {
        private final Queue<FileNode> queue = new ArrayDeque<>();

        public BreadthFirstIterator() {
            queue.add(root);
        }

        @Override
        public boolean hasNext() {
            return !queue.isEmpty();
        }

        @Override
        public FileNode next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            FileNode current = queue.remove();
            queue.addAll(current.getChildNodes());
            return current;
        }
    }
}
