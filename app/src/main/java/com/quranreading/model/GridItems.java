package com.quranreading.model;

import java.io.Serializable;

/**
 * Created by cyber on 11/30/2016.
 */

public class GridItems implements Serializable {

    public int id;
    public String title;

    public GridItems(int id, String address) {
        this.id = id;
        this.title = address;
    }
}
