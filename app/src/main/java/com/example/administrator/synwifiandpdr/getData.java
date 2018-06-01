package com.example.administrator.synwifiandpdr;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/10/1.
 */

public class getData {


    public my_knn_db getRSSI(int i) {
        my_knn_db RSSI = DataSupport.find(my_knn_db.class,i);//
        return RSSI;
    }
}
