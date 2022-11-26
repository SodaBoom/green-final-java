/*
 * Ant Group
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.atec2022;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

/**
 * @author khotyn
 * @version TotalEnergyLegacyService.java, v 0.1 2022年11月23日 00:08 khotyn
 */
@Component
public class RepoService {

    private static final Logger LOG = LoggerFactory.getLogger(RepoService.class);

    private final ToCollectEnergyRepository toCollectEnergyRepository;
    private final TotalEnergyRepository totalEnergyRepository;


    public RepoService(ToCollectEnergyRepository toCollectEnergyRepository, TotalEnergyRepository totalEnergyRepository) {
        this.toCollectEnergyRepository = toCollectEnergyRepository;
        this.totalEnergyRepository = totalEnergyRepository;
    }


    @Transactional
    public void updateToCollect(Integer toCollectEnergyId, MemToCollect memToCollect) {
        this.toCollectEnergyRepository.update(
                memToCollect.total_energy,
                memToCollect.status == utils.Status.ALL_COLLECTED ? "all_collected" : "collected_by_other",
                toCollectEnergyId
        );
    }

}
