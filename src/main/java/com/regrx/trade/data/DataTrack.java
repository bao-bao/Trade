package com.regrx.trade.data;

import com.regrx.trade.constant.Constant;
import com.regrx.trade.file.CsvReader;
import com.regrx.trade.network.HistoryDataDownloader;
import com.regrx.trade.network.PriceDataDownloader;
import com.regrx.trade.statistic.MovingAverage;
import com.regrx.trade.strategy.MA5MA20;
import com.regrx.trade.util.Time;
import com.regrx.trade.util.Utils;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.lang.Thread.sleep;

public class DataTrack {
    public int interval;
    public String type;
    public MinutesData minutesData;
    public Status status;
    public int breed;

    public DataTrack(String type, int interval) {
        this.type = type;
        this.interval = interval;
        minutesData = new MinutesData(interval);
        status = new Status(0, Constant.EMPTY);
        breed = Utils.getBreed(type);
    }

    public void track() {
        System.out.println("Start tracking " + type + " for an interval of " + interval + " minute(s)");

        String url = "https://hq.sinajs.cn/list=nf_" + type;

        // read data from csv
//        minutesData = CsvReader.readFromCsv("Minute_" + interval);
        minutesData = HistoryDataDownloader.getHistoryData(type, interval);
        status = CsvReader.readTradeHistory("Trade_" + type + "_" + interval);
        while(true) {
            if(Utils.isTrading(breed)) {
                long current = System.currentTimeMillis();
                Date currentDate = new Date(System.currentTimeMillis());
                long nextPoint = Time.getNextMillisEveryNMinutes(currentDate, interval);
                try {
                    sleep(nextPoint - current);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                PriceData newPrice = PriceDataDownloader.getPriceDataForStockFutures(url);
                minutesData.update(newPrice, true);
                this.trade(minutesData.getMovingAverages(), status, url);
            } else {
                try {
                    sleep(60000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void trade(LinkedList<MovingAverage> ma, Status status, String url) {
        ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();
        Future<Status> future = newCachedThreadPool.submit(new MA5MA20(ma, status, url, interval, breed));
        try {
            System.out.println("Trade Status: " + future.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            newCachedThreadPool.shutdown();
        }
    }
}
