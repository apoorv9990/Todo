package com.example.patel.todo.models;

import java.io.Serializable;
import java.sql.Date;

/**
 * Created by patel on 6/13/2016.
 */
public class Item implements Serializable, Comparable<Item>{

    @Override
    public int compareTo(Item another) {
        if(this.getPriority() == another.getPriority())
        {
            return this.getFinishDate().compareTo(another.getFinishDate());
        }

        return new Integer(this.getPriority().ordinal()).compareTo(new Integer(another.getPriority().ordinal()));
    }

    public enum Priority{
        HIGH, MEDIUM, LOW
    }

    private int id;
    private String itemTitle;
    private Date addedDate;
    private Date finishDate;
    private Priority priority;
    private boolean done;
    private String notes;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public Date getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(Date addedDate) {
        this.addedDate = addedDate;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
