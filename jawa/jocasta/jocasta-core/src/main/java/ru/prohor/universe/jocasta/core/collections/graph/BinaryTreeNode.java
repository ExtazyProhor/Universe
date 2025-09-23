package ru.prohor.universe.jocasta.core.collections.graph;

/**
 * Mutable implementation of binary tree
 *
 * @param <T> type of node value
 */
public class BinaryTreeNode<T> {
    public T value;
    public BinaryTreeNode<T> left;
    public BinaryTreeNode<T> right;

    public BinaryTreeNode(T value) {
        this.value = value;
    }

    public BinaryTreeNode(T value, BinaryTreeNode<T> left, BinaryTreeNode<T> right) {
        this.value = value;
        this.left = left;
        this.right = right;
    }
}
