/*
 * Ant Group
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.atec2022;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author khotyn
 * @version TotalEnergyLegacyService.java, v 0.1 2022年11月23日 00:08 khotyn
 */
@Component
public class TotalEnergyLegacyService {

    private static final Logger LOG = LoggerFactory.getLogger(TotalEnergyLegacyService.class);

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
        if (request_idx % 10000 == 0) {
            //统计线上总请求次数
            LOG.info(String.valueOf(request_idx));
        }
        //内存过滤
        ToCollect toCollect = toCollects[toCollectEnergyId];
        if (toCollect == null || toCollect.status == utils.Status.ALL_COLLECTED ||
                (!Objects.equals(toCollect.user_id, userId)
                        && (toCollect.status != utils.Status.EMPTY || toCollect.total_energy <= 3)
                )
        ) {
            return;
        }
        synchronized (toCollects[toCollectEnergyId]) {
            if (toCollect.status == utils.Status.ALL_COLLECTED ||
                    (!Objects.equals(toCollect.user_id, userId)
                            && (toCollect.status != utils.Status.EMPTY || toCollect.total_energy <= 3)
                    )
            ) {
                return;
            }
            TotalEnergy totalEnergy = this.totalEnergyRepository.findByUserId(userId);
            if (totalEnergy == null) {
                return;
            }
            if (toCollect.user_id.equals(userId)) {
                totalEnergy.setTotalEnergy(totalEnergy.getTotalEnergy() + toCollect.total_energy);
                toCollect.total_energy = 0;
                toCollect.status = utils.Status.ALL_COLLECTED; //("all_collected");
            } else {
                int toCollectEnergyCount = (int) Math.floor((double) toCollect.total_energy * 0.3);
                totalEnergy.setTotalEnergy(totalEnergy.getTotalEnergy() + toCollectEnergyCount);
                toCollect.total_energy = toCollect.total_energy - toCollectEnergyCount;
                toCollect.status = utils.Status.COLLECTED_BY_OTHER;
            }
            this.totalEnergyRepository.update(totalEnergy.getTotalEnergy(), userId);
            this.toCollectEnergyRepository.update(
                    toCollect.total_energy,
                    toCollect.status == utils.Status.ALL_COLLECTED ? "all_collected" : "collected_by_other",
                    toCollectEnergyId
            );
        }
    }
}
