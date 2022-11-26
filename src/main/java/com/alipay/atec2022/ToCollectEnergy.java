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
 * @author khotyn
 * @version ToCollectEnergy.java, v 0.1 2022年08月25日 15:08 khotyn
 */
@Entity
@Table(name = "to_collect_energy")
public class ToCollectEnergy {
    @Id
    private int id;
    @Column(name = "gmt_create")
    private Timestamp gmtCreate;
    @Column(name = "gmt_modified")
    private Timestamp gmtModified;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "to_collect_energy")
    private int toCollectEnergy;
    @Column(name = "status")
    private String status;

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

    public int getToCollectEnergy() {
        return toCollectEnergy;
    }

    public void setToCollectEnergy(int toCollectEnergy) {
        this.toCollectEnergy = toCollectEnergy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
