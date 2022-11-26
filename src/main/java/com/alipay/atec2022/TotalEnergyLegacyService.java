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

/**
 * @author khotyn
 * @version TotalEnergyLegacyService.java, v 0.1 2022年11月23日 00:08 khotyn
 */
@Component
public class TotalEnergyLegacyService {

    private static final Logger LOG = LoggerFactory.getLogger(TotalEnergyLegacyService.class);

    private final RepoService repoService;
    private final ToCollectEnergyRepository toCollectEnergyRepository;
    private final TotalEnergyRepository totalEnergyRepository;
    private final MemToCollect[] memToCollects;
    private final Map<String, AtomicInteger> memTotalEnergyMap;


    public TotalEnergyLegacyService(
            ToCollectEnergyRepository toCollectEnergyRepository,
            TotalEnergyRepository totalEnergyRepository,
            RepoService repoService
    ) {
        this.repoService = repoService;
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

    public void doCollectEnergy(String userId, Integer toCollectEnergyId) {
        //内存过滤
        MemToCollect memToCollect = memToCollects[toCollectEnergyId];
        //  todo 只出现了一次 null，可以优化
        if (memToCollect == null || memToCollect.status == utils.Status.ALL_COLLECTED ||
                (!Objects.equals(memToCollect.user_id, userId)
                        && (memToCollect.status != utils.Status.EMPTY || memToCollect.total_energy <= 3)
                )
        ) {
            return;
        }
        synchronized (memToCollects[toCollectEnergyId]) {
            AtomicInteger memTotalEnergy = memTotalEnergyMap.get(userId);
            if (memToCollect.user_id.equals(userId)) {
                memTotalEnergy.addAndGet(memToCollect.total_energy);
                memToCollect.total_energy = 0;
                memToCollect.status = utils.Status.ALL_COLLECTED; //("all_collected");
            } else {
                int toCollectEnergyCount = (int) Math.floor((double) memToCollect.total_energy * 0.3);
                memTotalEnergy.addAndGet(toCollectEnergyCount);
                memToCollect.total_energy -= toCollectEnergyCount;
                memToCollect.status = utils.Status.COLLECTED_BY_OTHER;
            }
//            this.totalEnergyRepository.update(curTotalEnergy, userId);
            repoService.updateToCollect(toCollectEnergyId, memToCollect);
        }
    }

    @Transactional
    public void executeUpdate() {
        memTotalEnergyMap.forEach((user_id, total_energy) -> {
            this.totalEnergyRepository.update(total_energy.get(), user_id);
        });
//        LOG.info("stats: {}, {}, {}", memToCollectNull.get(), memTotalEnergyNull.get(), syncReturnCount.get()); //  stats: 1, 0, 0
    }
}
