package com.arori4.airhockey;

import java.util.Iterator;
import java.util.function.Consumer;

/**
 * Created by Christopher Cabreros on 01-May-16.
 * Describes a linked list of a particular type.
 * TODO: implementation is very primitive, no delete
 * TODO: implement iterator
 */
public class LinkedList<T> implements Iterator{

    private ListNode<T> mHead;
    private int mSize;

    private ListNode mPointer;

    /**
     * Describes the ListNode within the linked list
     * @param <T> - type
     */
    private class ListNode<T>{

        private T contents;
        private ListNode next;
        private ListNode previous;
        private LinkedList parent; //for internal use

        public ListNode(T content){
            this.contents = content;
        }

        public T getContents() {
            return contents;
        }

        public void setContents(T contents) {
            this.contents = contents;
        }

        public ListNode getNext() {
            return next;
        }

        public void setNext(ListNode next) {
            this.next = next;
        }

        public ListNode getPrevious() {
            return previous;
        }

        public void setPrevious(ListNode previous) {
            this.previous = previous;
        }

        public LinkedList getParent() {
            return parent;
        }

        public void setParent(LinkedList parent) {
            this.parent = parent;
        }

    }//end class ListNode


    /**
     * Creates a new linked list
     */
    public LinkedList(){
        mSize = 0;
    }


    public void insert(T item){
        //create a new ListNode and add it into the list
        ListNode<T> newNode = new ListNode<T>(item);

        //check if first
        if (mSize == 0){
            mHead = newNode;
            //set the next and previous to itself, so next works
            mHead.setPrevious(mHead);
            mHead.setNext(mHead);
            mHead.setParent(this);
            mPointer = mHead; //set pointer to the first
        }
        else { //insert behind the head
            //set previous, next, and parent members
            newNode.setPrevious(mHead.getPrevious());
            newNode.getPrevious().setNext(newNode);

            newNode.setNext(mHead);
            mHead.setPrevious(newNode);

            newNode.setParent(this);
        }

        //increment size
        mSize++;
    }


    @Override
    /**
     * Will always have a next
     */
    public boolean hasNext() {
        return mSize > 0;
    }

    @Override
    public Object next() {
        mPointer = mPointer.getNext();
        return mPointer.getContents();
    }

    public Object previous(){
        mPointer = mPointer.getPrevious();
        return mPointer.getContents();
    }

    @Override
    public void remove() {

    }

    @Override
    public void forEachRemaining(Consumer action) {

    }


}//end class LinkedList
