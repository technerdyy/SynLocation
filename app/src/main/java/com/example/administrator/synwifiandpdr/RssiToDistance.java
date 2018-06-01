package com.example.administrator.synwifiandpdr;

import android.util.Log;

/**
 * Created by Administrator on 2017/10/25.
 */

/*----通过信号传播模型，把RSSI信号转换成距离，提供给最小二乘定位算法
    这里只提供方法，不进行7个RSSI转换成D的所有运算，运算放到main中去，再调用这里的方法，一样
    所以，1：我需要输入RSSI   2：输入一个D----------*/

public class RssiToDistance {

    private double RssiValue;   //输入进来的实时RSSI值
    private double rssi0=30;       //1米处的典型RSSI值
    private double picture_weight=1;       //有时候会出现图片像素点不匹配的问题,比如手机像素点是程序里面的两倍
    private double rssi;
    private int id_num;
    private double ChuanqiangN =6.5;

    public RssiToDistance(double rssi,int id_num){
        this.rssi = rssi;
        this.id_num = id_num;
    }

    public double getPicLen_way () {

        if(id_num==0){
        Log.i("Tian", "---------RssiToDistance非穿墙 Begin----------：");
            double f=(-rssi0+rssi)/20;       //20代表10*n，n取得2.0，代表衰减因子
            double d=picture_weight*Math.pow (10,f);             //Math.pow (10,f)代表10的f次方
            Log.i("Tian", "rssi：" + rssi);
            Log.i("Tian", "f：" + f);
            Log.i("Tian", "d：" + d);
        Log.i("Tian", "---------RssiToDistance非穿墙 Finished----------：");
            return d;
        }

        else {

            Log.i("Tian", "---------RssiToDistance穿墙 Begin----------：");
            double f=(-rssi0+rssi-ChuanqiangN)/20;       //20代表10*n，n取得2.0，代表衰减因子
            double d=picture_weight*Math.pow (10,f);             //Math.pow (10,f)代表10的f次方
            Log.i("Tian", "rssi：" + rssi);
            Log.i("Tian", "f：" + f);
            Log.i("Tian", "d：" + d);
            Log.i("Tian", "---------RssiToDistance穿墙 Finished----------：");
            id_num=0;
            return d;

        }
    }


}
