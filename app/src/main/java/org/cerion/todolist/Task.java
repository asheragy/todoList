package org.cerion.todolist;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

public class Task implements Serializable {
    public String id;
    public String listId;
    public String title = "";
    public Date updated;
    public String notes = "";
    public boolean completed;
    public Date due = new Date(0);
    public boolean deleted;
    private static final SimpleDateFormat mDateFormat = new SimpleDateFormat("EEE, MMM d, yyyy");

    private static String generateId() {
        Random rand = new Random();
        int i = rand.nextInt();
        return "temp_" + i;
    }

    public Task(String listId) {
        this(listId, generateId());
    }

    public Task(String listId, String id) {
        this.listId = listId;
        this.id = id;
    }

    public String toString() {
        return title + (deleted ? " (Deleted)" : "");
    }

    public void setDeleted(boolean del) {
        deleted = del;
        setModified();
    }

    public void setModified() {
        updated = new Date();
    }

    public boolean hasTempId() {
        return id.startsWith("temp_");
    }

    public String getDue() {
        mDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return mDateFormat.format(due);
    }

}
