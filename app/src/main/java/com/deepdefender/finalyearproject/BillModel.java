package com.deepdefender.finalyearproject;

public class BillModel {

    public String month;
    public int vegetables, kirana, gas, workers, total;
    public String status;
    public String invoicePath;   // 👈 NEW
    public long timestamp;

    public BillModel() {}

    public BillModel(String m, int v, int k, int g, int w,
                     int t, String s, String invoice, long ts) {
        month = m;
        vegetables = v;
        kirana = k;
        gas = g;
        workers = w;
        total = t;
        status = s;
        invoicePath = invoice;
        timestamp = ts;
    }
}
