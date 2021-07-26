package com.regrx.trade.data;

import com.regrx.trade.constant.Constant;

public class Status {
    private int count;
    private int status;
    private int interval;

    public Status() {
        count = 0;
        status = Constant.EMPTY;
        interval = 0;
    }

    public Status(int count, int status, int interval) {
        this.count = count;
        this.status = status;
        this.interval = interval;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    @Override
    public String toString() {
        if(status == Constant.EMPTY) {
            return "Empty";
        } else if(status == Constant.SHORT_SELLING) {
            return "Short Selling";
        } else if(status == Constant.PUT_BUYING) {
            return "Put Buying";
        } else if(status == Constant.BOTH) {
            return "Both";
        }
        return "";
    }
}
