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
 * @version TotalEnergyRepository.java, v 0.1 2022年08月25日 14:43 khotyn
 */
public interface TotalEnergyRepository extends CrudRepository<TotalEnergy, Integer> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT totalEnergy FROM TotalEnergy totalEnergy WHERE totalEnergy.userId = ?1")
    TotalEnergy findByUserId(String userId);

    @Query("SELECT totalEnergy FROM TotalEnergy totalEnergy WHERE totalEnergy.userId = ?1")
    TotalEnergy findByUserIdNotUpdate(String userId);

    @Query("SELECT totalEnergy FROM TotalEnergy totalEnergy")
    List<TotalEnergy> findAll();

    @Modifying
    @Query("UPDATE TotalEnergy SET totalEnergy = ?1 WHERE userId = ?2")
    void update(Integer toCollectEnergy, String userId);

    @Modifying
    @Query("UPDATE TotalEnergy SET totalEnergy = ?1 WHERE userId IN (?2)")
    void update(Integer toCollectEnergy, List<String> userIds);
}
