/*
 * Ant Group
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.atec2022;

import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author khotyn
 * @version TotalEnergyLegacyService.java, v 0.1 2022年11月23日 00:08 khotyn
 */
@Component
public class TotalEnergyLegacyService {
    static class ToCollect {
        String user_id;
        utils.Status status;
        int total_energy;

        public ToCollect(String user_id, int total_energy) {
            this.user_id = user_id;
            this.total_energy = total_energy;
            this.status = utils.Status.EMPTY;
        }
    }

    private final ToCollectEnergyRepository toCollectEnergyRepository;
    private final TotalEnergyRepository totalEnergyRepository;
    private final ToCollect[] toCollects = new ToCollect[105 * 10000];

    private final AtomicLong request_count = new AtomicLong(0);

    public TotalEnergyLegacyService(ToCollectEnergyRepository toCollectEnergyRepository, TotalEnergyRepository totalEnergyRepository) {
        this.toCollectEnergyRepository = toCollectEnergyRepository;
        this.totalEnergyRepository = totalEnergyRepository;
        for (ToCollectEnergy energy : toCollectEnergyRepository.findAll()) {
            toCollects[energy.getId()] = new ToCollect(energy.getUserId(), energy.getToCollectEnergy());
        }
    }

    @Transactional
    public void doCollectEnergy(String userId, Integer toCollectEnergyId) {
        long request_idx = request_count.getAndAdd(1);
        if (request_idx > 100 * 10000) {
            //统计线上总请求次数
            System.out.println(request_idx);
        }
        //内存过滤
        ToCollect toCollect = toCollects[toCollectEnergyId];
        if (toCollect == null || toCollect.status == utils.Status.ALL_COLLECTED || (!Objects.equals(toCollect.user_id, userId) && toCollect.status != utils.Status.EMPTY)) {
            return;
        }
        ToCollectEnergy toCollectEnergy = this.toCollectEnergyRepository.findById2(toCollectEnergyId);
        TotalEnergy totalEnergy = this.totalEnergyRepository.findByUserId(userId);
        if (toCollectEnergy != null && totalEnergy != null) {
            if (toCollectEnergy.getUserId().equals(userId)) {
                totalEnergy.setTotalEnergy(totalEnergy.getTotalEnergy() + toCollectEnergy.getToCollectEnergy());
                toCollectEnergy.setToCollectEnergy(0);
                toCollectEnergy.setStatus("all_collected");
                toCollect.total_energy = 0;
                toCollect.status = utils.Status.ALL_COLLECTED;
            } else {
                int toCollectEnergyCount = (int) Math.floor((double) toCollectEnergy.getToCollectEnergy() * 0.3);
                totalEnergy.setTotalEnergy(totalEnergy.getTotalEnergy() + toCollectEnergyCount);
                toCollectEnergy.setToCollectEnergy(toCollectEnergy.getToCollectEnergy() - toCollectEnergyCount);
                toCollectEnergy.setStatus("collected_by_other");
                toCollect.total_energy = toCollectEnergy.getToCollectEnergy();
                toCollect.status = utils.Status.COLLECTED_BY_OTHER;
            }
            this.totalEnergyRepository.save(totalEnergy);
            this.toCollectEnergyRepository.save(toCollectEnergy);
        }
    }
}