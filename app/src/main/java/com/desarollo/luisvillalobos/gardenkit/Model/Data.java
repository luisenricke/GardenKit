package com.desarollo.luisvillalobos.gardenkit.Model;

import java.util.Date;

public class Data {
    private int wet1, wet2, wet3, wet4, wet5;
    private double ph;
    private Date date;

    public Data(Date date, int wet1, int wet2, int wet3, int wet4, int wet5, double ph) {
        this.date = date;
        this.wet1 = wet1;
        this.wet2 = wet2;
        this.wet3 = wet3;
        this.wet4 = wet4;
        this.wet5 = wet5;
        this.ph = ph;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getWet1() {
        return wet1;
    }

    public void setWet1(int wet1) {
        this.wet1 = wet1;
    }

    public int getWet2() {
        return wet2;
    }

    public void setWet2(int wet2) {
        this.wet2 = wet2;
    }

    public int getWet3() {
        return wet3;
    }

    public void setWet3(int wet3) {
        this.wet3 = wet3;
    }

    public int getWet4() {
        return wet4;
    }

    public void setWet4(int wet4) {
        this.wet4 = wet4;
    }

    public int getWet5() {
        return wet5;
    }

    public void setWet5(int wet5) {
        this.wet5 = wet5;
    }

    public double getPh() {
        return ph;
    }

    public void setPh(double ph) {
        this.ph = ph;
    }
}
