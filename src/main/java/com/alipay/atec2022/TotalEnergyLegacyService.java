/*
 * Ant Group
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.atec2022;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
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
    private final MemToCollect[] memToCollects;
    private final Map<String, AtomicInteger> memTotalEnergyMap;

    private final AtomicLong request_count = new AtomicLong(0);

    public TotalEnergyLegacyService(ToCollectEnergyRepository toCollectEnergyRepository, TotalEnergyRepository totalEnergyRepository) {
        this.toCollectEnergyRepository = toCollectEnergyRepository;
        this.totalEnergyRepository = totalEnergyRepository;
        List<ToCollectEnergy> allToCollectEnergyList = toCollectEnergyRepository.findAll();
        memToCollects = new MemToCollect[allToCollectEnergyList.size() + 1];
        for (ToCollectEnergy energy : allToCollectEnergyList) {
            memToCollects[energy.getId()] = new MemToCollect(energy.getUserId(), energy.getToCollectEnergy());
        }
        List<TotalEnergy> allTotalEnergyList = totalEnergyRepository.findAll();
        memTotalEnergyMap = new HashMap<>(allTotalEnergyList.size());
        for (TotalEnergy energy : allTotalEnergyList) {
            memTotalEnergyMap.put(energy.getUserId(), new AtomicInteger(energy.getTotalEnergy()));
        }
        LOG.info("ALL IN MEM!");
    }

    @Transactional
    public void doCollectEnergy(String userId, Integer toCollectEnergyId) {
        long request_idx = request_count.getAndAdd(1);
        if (request_idx % 10000 == 0) {
            //统计线上总请求次数
            LOG.info(String.valueOf(request_idx));
        }
        //内存过滤
        MemToCollect memToCollect = memToCollects[toCollectEnergyId];
        if (memToCollect == null || memToCollect.status == utils.Status.ALL_COLLECTED ||
                (!Objects.equals(memToCollect.user_id, userId)
                        && (memToCollect.status != utils.Status.EMPTY || memToCollect.total_energy <= 3)
                )
        ) {
            return;
        }
        synchronized (memToCollects[toCollectEnergyId]) {
            if (memToCollect.status == utils.Status.ALL_COLLECTED ||
                    (!Objects.equals(memToCollect.user_id, userId)
                            && (memToCollect.status != utils.Status.EMPTY || memToCollect.total_energy <= 3)
                    )
            ) {
                return;
            }
            AtomicInteger memTotalEnergy = memTotalEnergyMap.get(userId);
            if (memTotalEnergy == null) {
                return;
            }
            int curTotalEnergy;
            if (memToCollect.user_id.equals(userId)) {
                curTotalEnergy = memTotalEnergy.addAndGet(memToCollect.total_energy);
                memToCollect.total_energy = 0;
                memToCollect.status = utils.Status.ALL_COLLECTED; //("all_collected");
            } else {
                int toCollectEnergyCount = (int) Math.floor((double) memToCollect.total_energy * 0.3);
                curTotalEnergy = memTotalEnergy.addAndGet(toCollectEnergyCount);
                memToCollect.total_energy = memToCollect.total_energy - toCollectEnergyCount;
                memToCollect.status = utils.Status.COLLECTED_BY_OTHER;
            }
            this.totalEnergyRepository.update(curTotalEnergy, userId);
//            this.toCollectEnergyRepository.update(
//                    memToCollect.total_energy,
//                    memToCollect.status == utils.Status.ALL_COLLECTED ? "all_collected" : "collected_by_other",
//                    toCollectEnergyId
//            );
        }
    }
}
