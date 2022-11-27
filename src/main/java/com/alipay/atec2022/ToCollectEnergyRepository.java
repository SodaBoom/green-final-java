/*
 * Ant Group
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.atec2022;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.LockModeType;
import java.util.List;

/**
 * @author khotyn
 * @version ToCollectEnergyRepository.java, v 0.1 2022年08月25日 15:14 khotyn
 */
public interface ToCollectEnergyRepository extends CrudRepository<ToCollectEnergy, Integer> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT toCollectEnergy FROM ToCollectEnergy toCollectEnergy" +
            " WHERE toCollectEnergy.id = ?1 and " +
            "((toCollectEnergy.status = '') or (toCollectEnergy.userId=?2 and toCollectEnergy.status <> 'all_collected'))")
    ToCollectEnergy findByUserId(Integer id, String userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT toCollectEnergy FROM ToCollectEnergy toCollectEnergy WHERE toCollectEnergy.id = ?1")
    ToCollectEnergy findById2(Integer id);

    @Query("SELECT toCollectEnergy FROM ToCollectEnergy toCollectEnergy")
    List<ToCollectEnergy> findAll();

    @Modifying
    @Query("UPDATE ToCollectEnergy SET toCollectEnergy = ?1, status = ?2 WHERE id = ?3")
    void update(Integer toCollectEnergy, String status, Integer id);

    @Query(value = "LOCK TABLE to_collect_energy WRITE", nativeQuery = true)
    void lock();

    @Query(value = "UNLOCK TABLES", nativeQuery = true)
    void unlock();

}
