/*
 * Ant Group
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.atec2022;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * 每个用户的总能量
 *
 * @author khotyn
 * @version TotalEnergy.java, v 0.1 2022年08月25日 13:46 khotyn
 */
@Entity
@Table(name = "total_energy")
public class TotalEnergy {
    @Id
    private int id;
    @Column(name = "gmt_create")
    private Timestamp gmtCreate;
    @Column(name = "gmt_modified")
    private Timestamp gmtModified;
    @Column(name = "user_id", length = 32)
    private String userId;
    @Column(name = "total_energy")
    private int totalEnergy;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Timestamp gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Timestamp getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Timestamp gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getTotalEnergy() {
        return totalEnergy;
    }

    public void setTotalEnergy(int totalEnergy) {
        this.totalEnergy = totalEnergy;
    }

    @Override
    public String toString() {
        return "TotalEnergy{" +
                "id=" + id +
                ", gmtCreate=" + gmtCreate +
                ", gmtModified=" + gmtModified +
                ", userId='" + userId + '\'' +
                ", totalEnergy=" + totalEnergy +
                '}';
    }
}