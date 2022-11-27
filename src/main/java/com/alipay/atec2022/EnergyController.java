/*
 * Ant Group
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.atec2022;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author khotyn
 * @version EnergyController.java, v 0.1 2022年08月28日 21:27 khotyn
 */
@Controller
public class EnergyController {

    private static final Logger LOG = LoggerFactory.getLogger(EnergyController.class);

    private final TotalEnergyLegacyService totalEnergyLegacyService;
    private final AtomicLong request_count = new AtomicLong(0);

    public EnergyController(TotalEnergyLegacyService totalEnergyLegacyService) {
        this.totalEnergyLegacyService = totalEnergyLegacyService;
    }

    @RequestMapping(value = "collect_energy/{userId}/{toCollectEnergyId}", method = RequestMethod.POST)
    @ResponseBody
    public Boolean collectEnergy(@PathVariable String userId, @PathVariable Integer toCollectEnergyId) {
        totalEnergyLegacyService.doCollectEnergy(userId, toCollectEnergyId);
        long request_idx = request_count.addAndGet(1);
        if (request_idx == 100_0000 - 1) {
            LOG.info("UpdateToCollect start {}", request_idx);
            new Thread(totalEnergyLegacyService::executeUpdateToCollect).start();
        } else if (request_idx == 100_0000) {
            LOG.info("UpdateTotal start {}", request_idx);
            totalEnergyLegacyService.executeUpdateTotal();
            LOG.info("UpdateTotal end {}", request_idx);
        }
        return true;
    }
}
