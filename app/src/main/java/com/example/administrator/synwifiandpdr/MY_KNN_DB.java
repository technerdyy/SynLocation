package com.example.administrator.synwifiandpdr;

import android.database.sqlite.SQLiteDatabase;

import org.litepal.crud.DataSupport;

import static org.litepal.tablemanager.Connector.getWritableDatabase;

/**
 * Created by Administrator on 2017/9/20.
 */

 class my_knn_db
        extends DataSupport{


    /* public String getMac() {return mac;}
    public void setMac(String mac) {this.mac = mac;}*/
    //private int rssi;
   // private String mac;

    private int id;
    private double x_0;
    private double y_0;



    private double mean_0;
    private double var_0;
    private String ssid_0;

    private double mean_1;
    private double var_1;
    private String ssid_1;

    private double mean_2;
    private double var_2;
    private String ssid_2;

    private double mean_3;
    private double var_3;
    private String ssid_3;

    private double mean_4;
    private double var_4;
    private String ssid_4;

    private double mean_5;
    private double var_5;
    private String ssid_5;

    private double mean_6;
    private double var_6;
    private String ssid_6;





    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public double getMean(int i)
    {
        if(i==0) {return mean_0;}
        if(i==1) {return mean_1;}
        if(i==2) {return mean_2;}
        if(i==3) {return mean_3;}
        if(i==4) {return mean_4;}
        if(i==5) {return mean_5;}
        if(i==6) {return mean_6;}
        return 0;

    }





    public double getVar(int i)
    {
        if(i==0) {return var_0;}
        if(i==1) {return var_1;}
        if(i==2) {return var_2;}
        if(i==3) {return var_3;}
        if(i==4) {return var_4;}
        if(i==5) {return var_5;}
        if(i==6) {return var_6;}
        return 0;
    }





    public String getSsid(int i) {
        if(i==0) {return ssid_0;}
        if(i==1) {return ssid_1;}
        if(i==2) {return ssid_2;}
        if(i==3) {return ssid_3;}
        if(i==4) {return ssid_4;}
        if(i==5) {return ssid_5;}
        if(i==6) {return ssid_6;}
        return "0";
    }





    public void setMean_0(double mean_0) {
        this.mean_0 = mean_0;
    }

    public void setMean_1(double mean_1) {
        this.mean_1 = mean_1;
    }

    public void setMean_2(double mean_2) {
        this.mean_2 = mean_2;
    }

    public void setMean_3(double mean_3) {
        this.mean_3 = mean_3;
    }

    public void setMean_4(double mean_4) {
        this.mean_4 = mean_4;
    }

    public void setMean_5(double mean_5) {
        this.mean_5 = mean_5;
    }

    public void setMean_6(double mean_6) {
        this.mean_6 = mean_6;
    }



    public void setSsid_0(String ssid_0) {
        this.ssid_0 = ssid_0;
    }

    public void setSsid_1(String ssid_1) {
        this.ssid_1 = ssid_1;
    }

    public void setSsid_2(String ssid_2) {
        this.ssid_2 = ssid_2;
    }

    public void setSsid_3(String ssid_3) {
        this.ssid_3 = ssid_3;
    }

    public void setSsid_4(String ssid_4) {
        this.ssid_4 = ssid_4;
    }

    public void setSsid_5(String ssid_5) {
        this.ssid_5 = ssid_5;
    }

    public void setSsid_6(String ssid_6) {
        this.ssid_6 = ssid_6;
    }


    public void setVar_0(double var_0) {
        this.var_0 = var_0;
    }

    public void setVar_1(double var_1) {
        this.var_1 = var_1;
    }

    public void setVar_2(double var_2) {
        this.var_2 = var_2;
    }

    public void setVar_3(double var_3) {
        this.var_3 = var_3;
    }

    public void setVar_4(double var_4) {
        this.var_4 = var_4;
    }

    public void setVar_5(double var_5) {
        this.var_5 = var_5;
    }

    public void setVar_6(double var_6) {
        this.var_6 = var_6;
    }



    public double getX_0() {
        return x_0;
    }


    public double getY_0() {
        return y_0;
    }



    public void setX_0(double x_0) {
        this.x_0 = x_0;
    }

    public void setY_0(double y_0) {
        this.y_0 = y_0;
    }



    public void deleteAll() {
        getWritableDatabase().execSQL("delete from my_knn_db");
    }


    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


    }


}
