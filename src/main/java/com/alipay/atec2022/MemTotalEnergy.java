/*
 * Ant Group
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.atec2022;

import java.util.concurrent.atomic.AtomicInteger;

public class MemTotalEnergy {
    public String userId;
    public AtomicInteger totalEnergy;

    public MemTotalEnergy(String userId, int totalEnergy) {
        this.userId = userId;
        this.totalEnergy = new AtomicInteger(totalEnergy);
    }
}
