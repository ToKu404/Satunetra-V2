package com.example.satunetra_bot_test.local.table;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "user_entity")
public class UserEntity implements Serializable {
    @PrimaryKey
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "gender")
    private String gender;

    @ColumnInfo(name = "first")
    private Boolean first;

    @ColumnInfo(name = "old")
    private int old;

    @ColumnInfo(name = "statusKebutaan")
    private String statusKebutaaan;

    public int getOld() {
        return old;
    }

    public void setOld(int old) {
        this.old = old;
    }

    public String getStatusKebutaaan() {
        return statusKebutaaan;
    }

    public void setStatusKebutaaan(String statusKebutaaan) {
        this.statusKebutaaan = statusKebutaaan;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getFirst() {
        return first;
    }

    public void setFirst(Boolean first) {
        this.first = first;
    }

    public UserEntity(){}
    public UserEntity(int id, String name, String gender, int old, String statusKebutaan) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.first = false;
        this.old = old;
        this.statusKebutaaan = statusKebutaan;
    }

}

