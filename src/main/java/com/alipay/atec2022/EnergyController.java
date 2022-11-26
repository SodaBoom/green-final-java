/*
 * Ant Group
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.atec2022;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.Optional;

/**
 * @author khotyn
 * @version EnergyController.java, v 0.1 2022年08月28日 21:27 khotyn
 */
@Controller
public class EnergyController {

    private final TotalEnergyLegacyService totalEnergyLegacyService;

    public EnergyController(TotalEnergyLegacyService totalEnergyLegacyService) {
        this.totalEnergyLegacyService = totalEnergyLegacyService;
    }

    @RequestMapping(value = "collect_energy/{userId}/{toCollectEnergyId}", method = RequestMethod.POST)
    @ResponseBody
    public Boolean collectEnergy(@PathVariable String userId, @PathVariable Integer toCollectEnergyId) {
        try {
            totalEnergyLegacyService.doCollectEnergy(userId, toCollectEnergyId);
        } catch (Error e) {
            System.err.println("Error while processing request userId=" + userId + ";toCollectEnergyId=" + toCollectEnergyId);
            e.printStackTrace();
        }
        return true;
    }
}