package com.quranreading.model;

/**
 * Created by cyber on 11/30/2016.
 */

public class GridItem {
    public int id;
    public String name;

    public GridItem(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public GridItem() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
