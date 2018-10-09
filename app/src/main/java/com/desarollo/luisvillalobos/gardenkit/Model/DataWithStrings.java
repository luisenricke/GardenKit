package com.desarollo.luisvillalobos.gardenkit.Model;

import java.util.Date;

public class DataWithStrings {
    private String wet1, wet2, wet3, wet4, wet5, v;
    private double ph, h20;
    private Date date;

    public DataWithStrings(Date date,String wet1, String wet2, String wet3, String wet4, String wet5, double ph) {
        this.wet1 = wet1;
        this.wet2 = wet2;
        this.wet3 = wet3;
        this.wet4 = wet4;
        this.wet5 = wet5;
        this.ph = ph;
        this.date = date;
    }

    public String getWet1() {
        return wet1;
    }

    public void setWet1(String wet1) {
        this.wet1 = wet1;
    }

    public String getWet2() {
        return wet2;
    }

    public void setWet2(String wet2) {
        this.wet2 = wet2;
    }

    public String getWet3() {
        return wet3;
    }

    public void setWet3(String wet3) {
        this.wet3 = wet3;
    }

    public String getWet4() {
        return wet4;
    }

    public void setWet4(String wet4) {
        this.wet4 = wet4;
    }

    public String getWet5() {
        return wet5;
    }

    public void setWet5(String wet5) {
        this.wet5 = wet5;
    }

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }

    public double getPh() {
        return ph;
    }

    public void setPh(double ph) {
        this.ph = ph;
    }

    public double getH20() {
        return h20;
    }

    public void setH20(double h20) {
        this.h20 = h20;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
