package com.alipay.atec2022;

public class MemToCollect {
    String user_id;
    utils.Status status;
    int total_energy;
    boolean modified = false;

    public MemToCollect(String user_id, int total_energy) {
        this.user_id = user_id;
        this.total_energy = total_energy;
        this.status = utils.Status.EMPTY;
    }
}
