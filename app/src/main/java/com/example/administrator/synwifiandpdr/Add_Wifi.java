package com.example.administrator.synwifiandpdr;
import android.util.Log;

/**
 * Created by Administrator on 2017/9/17.
 */

public class Add_Wifi {

    /*----id是数据库内的编号，ssid是WIFI的名字，level是RSSI的强度，x,y是参考点的坐标--------*/
    public void Add_DB_Way(int id,
                           String ssid_0, String ssid_1,String ssid_2,String ssid_3,String ssid_4,String ssid_5,String ssid_6,
                           double x_0,
                           double y_0,
                           double mean_0,double mean_1,double mean_2,double mean_3,double mean_4,double mean_5,double mean_6,
                           double var_0,double var_1,double var_2,double var_3,double var_4,double var_5,double var_6)
    {

        my_knn_db w1 = new my_knn_db();

        if(id==1) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);


            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id0==0存入数据库");
        }

        if(id==2) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);


            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id1==0存入数据库");
        }

        if(id==3) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);


            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id2==0存入数据库");
        }

        if(id==4) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);


            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);




            w1.save();
            Log.i("Tian", "id3==0存入数据库");
        }

        if(id==5) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);


            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id4==0存入数据库");
        }


        if(id==6) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);

            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id"+id+"存入数据库");
        }

        if(id==7) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);


            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id6==0存入数据库");
        }

        if(id==8) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);


            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id7==0存入数据库");
        }


        if(id==9) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);


            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id8==0存入数据库");
        }

        if(id==10) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);


            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id9==0存入数据库");
        }

        if(id==11) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);

            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id"+id+"存入数据库");
        }

        if(id==12) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);

            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id"+id+"存入数据库");
        }

        if(id==13) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);

            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id"+id+"存入数据库");
        }

        if(id==14) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);

            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id"+id+"存入数据库");
        }


        if(id==15) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);

            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id"+id+"存入数据库");
        }

        if(id==16) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);

            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id"+id+"存入数据库");
        }

        if(id==17) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);

            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id"+id+"存入数据库");
        }

        if(id==18) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);

            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id"+id+"存入数据库");
        }

        if(id==19) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);

            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id"+id+"存入数据库");
        }

        if(id==20) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);

            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id"+id+"存入数据库");
        }


        if(id==21) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);

            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id"+id+"存入数据库");
        }

        if(id==22) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);

            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id"+id+"存入数据库");
        }

        if(id==23) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);

            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id"+id+"存入数据库");
        }

        if(id==24) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);

            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id"+id+"存入数据库");
        }

        if(id==25) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);

            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id"+id+"存入数据库");
        }

        if(id==26) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);

            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id"+id+"存入数据库");
        }

        if(id==27) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);

            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id"+id+"存入数据库");
        }

        if(id==28) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);

            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id"+id+"存入数据库");
        }

        if(id==29) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);

            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id"+id+"存入数据库");
        }

        if(id==30) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);

            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id"+id+"存入数据库");
        }

        if(id==31) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);

            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id"+id+"存入数据库");
        }

        if(id==32) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);

            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id"+id+"存入数据库");
        }

        if(id==33) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);

            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id"+id+"存入数据库");
        }


        if(id==34) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);

            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id"+id+"存入数据库");
        }

        if(id==35) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);

            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id"+id+"存入数据库");
        }


        if(id==36) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);

            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id"+id+"存入数据库");
        }

        if(id==37) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);

            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id"+id+"存入数据库");
        }

        if(id==38) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);

            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id"+id+"存入数据库");
        }

        if(id==39) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);

            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id"+id+"存入数据库");
        }
        if(id==40) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);

            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id"+id+"存入数据库");
        }

        if(id==41) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);

            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id"+id+"存入数据库");
        }

        if(id==42) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);

            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id"+id+"存入数据库");
        }

        if(id==43) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);

            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id"+id+"存入数据库");
        }

        if(id==44) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);

            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id"+id+"存入数据库");
        }

        if(id==45) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);

            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id"+id+"存入数据库");
        }

        if(id==46) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);

            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id"+id+"存入数据库");
        }

        if(id==47) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);

            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id"+id+"存入数据库");
        }

        if(id==48) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);

            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id"+id+"存入数据库");
        }

        if(id==49) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);

            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id"+id+"存入数据库");
        }

        if(id==50) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);

            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id"+id+"存入数据库");
        }

        if(id==51) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);

            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id"+id+"存入数据库");
        }

        if(id==52) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);

            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id"+id+"存入数据库");
        }
        if(id==53) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);

            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id"+id+"存入数据库");
        }
        if(id==54) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);

            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id"+id+"存入数据库");
        }
        if(id==55) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);

            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id"+id+"存入数据库");
        }

        if(id==56) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);

            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id"+id+"存入数据库");
        }
        if(id==57) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);

            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id"+id+"存入数据库");
        }
        if(id==58) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);

            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id"+id+"存入数据库");
        }
        if(id==59) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);

            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id"+id+"存入数据库");
        }
        if(id==60) {
            w1.setId(id);
            w1.setSsid_0(ssid_0);
            w1.setSsid_1(ssid_1);
            w1.setSsid_2(ssid_2);
            w1.setSsid_3(ssid_3);
            w1.setSsid_4(ssid_4);
            w1.setSsid_5(ssid_5);
            w1.setSsid_6(ssid_6);

            w1.setX_0(x_0);
            w1.setY_0(y_0);



            w1.setMean_0(mean_0);
            w1.setVar_0(var_0);
            w1.setMean_1(mean_1);
            w1.setVar_1(var_1);
            w1.setMean_2(mean_2);
            w1.setVar_2(var_2);
            w1.setMean_3(mean_3);
            w1.setVar_3(var_3);
            w1.setMean_4(mean_4);
            w1.setVar_4(var_4);
            w1.setMean_5(mean_5);
            w1.setVar_5(var_5);
            w1.setMean_6(mean_6);
            w1.setVar_6(var_6);





            w1.save();
            Log.i("Tian", "id"+id+"存入数据库");
        }


    }
}