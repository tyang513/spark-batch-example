package com.github.tyang513.batch.probe.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by loong on 4/22/16.
 */
public class WifiData implements Serializable {

    private String apmac;
    private int num;
    private long tssend;
    private String typeid;


    private String count;
    private List<WifiTa> wifitalist;

    public String getApmac() {
        return apmac;
    }

    public void setApmac(String apmac) {
        this.apmac = apmac;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public long getTssend() {
        return tssend;
    }

    public void setTssend(long tssend) {
        this.tssend = tssend;
    }

    public List<WifiTa> getWifitalist() {
        return wifitalist;
    }

    public void setWifitalist(List<WifiTa> wifitalist) {
        this.wifitalist = wifitalist;
    }

    public String getTypeid() {
        return typeid;
    }

    public void setTypeid(String typeid) {
        this.typeid = typeid;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "WifiData{" + "apmac='" + apmac + '\'' + ", num=" + num + ", tssend=" + tssend + ", wifitalist=" + wifitalist + '}';
    }
}
