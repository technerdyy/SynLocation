package com.example.administrator.synwifiandpdr;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.Variance;
import org.apache.commons.math3.stat.descriptive.rank.Max;
import org.apache.commons.math3.stat.descriptive.rank.Min;
import org.litepal.crud.DataSupport;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Administrator on 2017/10/1.
 */

public class Calculate_OnlineScan extends AppCompatActivity implements Runnable {

    private WifiManager wifi_manager_2;
    private int Scan_Num = 3;//10遍
    private int AP_Num = 7;//10个AP
    private int AP_Scan_Num=15;         //如果AP节点到了晚上没这么多时，会发生ARRAY  INDEX超出的错误  要注意

    private String[] ssid_from_DB=new String[AP_Num];//从数据库传入SSID，放入这个类里面的SSID，用于排序，好在后面就不用再考虑排序的问题了（改过）
    private double[] AP_final_mean = new double[AP_Num];
    private double[] AP_final_var = new double[AP_Num];
    private String[] AP_final_ssid = new String[AP_Num];
    private String x_editview_string;
    private String y_editview_string;
    private int y_editview_int;
    private int x_editview_int;
    private int flag_1;
    private int kit;
    private double MaxRSSIVaule;
    private double MinRSSIVaule;

    private int Time = 5;//扫描间隔时间



    public Calculate_OnlineScan(WifiManager wifi_manager_2) {
        this.wifi_manager_2 = wifi_manager_2;
    }

    @Override
    public void run() {
        Log.i("Tian", "Calculate_OnlineScan");

        /*------------------定义功能值----------------*/
        double[][] AP_buf = new double[AP_Num][Scan_Num];//存储求均值方差的AP  WIFI值
        double[][] AP_vi = new double[AP_Num][Scan_Num];//存储残差，用于求西格玛
        double[][] AP_data = new double[AP_Num][2];//存储求均值方差的AP  WIFI值,10个AP，每个AP有100平均的均值和方差
        //double[][] AP_buf2 = new double[10][Scan_Num];
        double[][] AP_vi_sum = new double[AP_Num][1];//用于残差求取均方根误差用
        double[][] AP_vi_sum_chu100 = new double[AP_Num][1];//用于残差求取均方根误差用
        double[][] AP_3XG = new double[AP_Num][1];//装3西格玛的数组
        String[] InputDB_ssid = new String[AP_Num];



        /*-----------------------数据库的SSID引用储存---------------*/
        /*--2017.11.4为什么还要从数据库中导入数据？不是在线的吗？  因为我需要按照指定的SSID名称来对应RSSI，不然SSID全是乱的你怎么取值
           这里从数据库中只取SSID值
        -----*/

        Log.i("Tian", "-----------------DB to Calculate_OnlineScan Finished-----------------");
        Log.i("Tian","--------------------InputDB---------DbTest------------------------");
        DbManager manager = new DbManager(Calculate_OnlineScan.this);
        manager.openDataBase();//把路径赋值给了db
        SQLiteDatabase db = manager.getDb();//db中包含数据库路径
        Cursor cursor = db.query("my_knn_db", null, null, null, null, null, null);
        Log.i("Tian","--------------------InputDB---------DbTest------------------------");
        if (cursor.moveToFirst()) {

            int ssid_0,ssid_1,ssid_2,ssid_3,ssid_4,ssid_5,ssid_6;

            ssid_0 = cursor.getColumnIndex("ssid_0");
            ssid_1 = cursor.getColumnIndex("ssid_1");
            ssid_2 = cursor.getColumnIndex("ssid_2");
            ssid_3 = cursor.getColumnIndex("ssid_3");
            ssid_4 = cursor.getColumnIndex("ssid_4");
            ssid_5 = cursor.getColumnIndex("ssid_5");
            ssid_6 = cursor.getColumnIndex("ssid_6");



            Log.i("Tian","复制中-------------:");
            do {
                InputDB_ssid[0] = cursor.getString(ssid_0);
                InputDB_ssid[1] = cursor.getString(ssid_1);
                InputDB_ssid[2] = cursor.getString(ssid_2);
                InputDB_ssid[3] = cursor.getString(ssid_3);
                InputDB_ssid[4]= cursor.getString(ssid_4);
                InputDB_ssid[5] = cursor.getString(ssid_5);
                InputDB_ssid[6] = cursor.getString(ssid_6);


                //使用Log查看数据,未在界面展示
              //  Log.i("Tian","name:"+InputDB_ssid[0]+" name1:"+InputDB_ssid[1]+" name2:"+InputDB_ssid[2]+" name3:"+InputDB_ssid[3]+" name4:"+InputDB_ssid[4]+" name5:"+InputDB_ssid[5]+" name6:"+InputDB_ssid[6]);


            }while(cursor.moveToNext());
        }
        manager.closeDataBase();
        Log.i("Tian","--------------------InputDB----DbTest-----Finished-------------------");
        for(int y=0;y<AP_Num;y++){

            ssid_from_DB[y] = InputDB_ssid[y];

        }
        Log.i("Tian","--------------------InputDB_ssid----to----ssid_from_DB----Finished-------------------");

        /*------------SSID  finished----------------*/


        for (int j = 0; j < AP_Num; j++) {
            for (int i = 0; i < Scan_Num; i++) {
                wifi_manager_2.startScan();
                List<ScanResult> results = wifi_manager_2.getScanResults();//results含有很多AP信息
                        /*WiFi RSSI信号从大到小排序*/
                Collections.sort(results, new Comparator<ScanResult>() {
                    @Override
                    public int compare(ScanResult o1, ScanResult o2) {
                        if (o1.level > o2.level) {
                            return -1;
                        } else if (o1.level < o2.level) {//为1就把02丢到01前面去
                            return 1;
                        } else {
                            return 0;
                        }
                    }
                });
                //Log.i("Tian", "第" + j + "个AP点" + "的第" + i + "次Scan内排序完毕");
                //Log.i("Tian", "ssid_from_main是"+ssid_from_DB[0]+"=="+ssid_from_DB[1]+"=="+ssid_from_DB[2]+"=="+ssid_from_DB[3]+"=="+ssid_from_DB[4]+"=="+ssid_from_DB[5]+"=="+ssid_from_DB[6]);

                /*-----------先判断一下传进来的SSID是否非空------------------*/

                /*----------------------j==0时因为是第一次，所以什么都不用管------------------*/
                if (j == 0) {
                    if (i == 0) {                               //i代表扫描的次数，应该为100次扫描，这里代表第一次扫描
                        for (int q = 0; q < AP_Scan_Num; q++)
                        {
                            ScanResult AP_info = results.get(q);//results是已经排好序存放所有AP的组
                           // Log.i("Tian", "AP_info.SSID为"+AP_info.SSID);
                            if (ssid_from_DB[j].equals(AP_info.SSID))
                            {       //如果第二次接收到的名称和第一次存好的名称一致，也就是说，信号强度没变，第一还是第一个

                                AP_buf[j][i] = Math.abs(AP_info.level);     //并且把这一个的RSSI放入数组中为后面用

                            }
                            else//否则就查找对应SSID的RSSI的值，这里RSSI值已经做绝对值处理了
                                {                              //如果不相同，就得找一下第二个SSID到底在哪儿了，目的是相对应的RSSI，也就是ssid[i-1]！=ssid_buf[i-1]，而我的ssid[i-1]必须存放与第一次一致的
                                for (int p = 0; p < AP_Scan_Num; p++)
                                {          //如果我第二次第一个AP的SSID和第一次第一个的AP不同，那我就要看第二次第二个AP的相不相同，然后找到对应RSSI值
                                    ScanResult AP_info_1 = results.get(p);  //然后对照着第一个SSID,一个个去查10个AP
                                    if (ssid_from_DB[j].equals(AP_info_1.SSID))
                                    {            //这里虽然是i-1，但是只要我对齐前面一个，后面的就都对了

                                        AP_buf[j][i] = Math.abs(AP_info_1.level);
                                       // Log.i("Tian", "找到了乱序的ssid为" + ssid_from_DB[j] + "排到了第" + p + "位去了");
                                    }
                                    else{
                                       // Log.i("Tian", "ssid_from_main应该是没有值" + ssid_from_DB[j]);
                                    }
                                    //Log.i("Tian", "第"+j+"个AP第"+p+"次扫描："+"没有找到乱序的SSID");
                                }
                            }
                        }


                    }
                    if (i > 0) {                               //当扫描第二次，三次时：
                        ScanResult AP_info = results.get(j);//已经排好顺序的WIFI,第2-100次取第一个AP的信息，第2次排序时可能导致WIFI节点变化，而我RSSI还是按照第一次来的，所以得判断一下

                        if (ssid_from_DB[j].equals(AP_info.SSID)) {       //如果第二次接收到的名称和第一次存好的名称一致，也就是说，信号强度没变，第一还是第一个

                            AP_buf[j][i] = Math.abs(AP_info.level);     //并且把这一个的RSSI放入数组中为后面用

                        } else {                              //如果不相同，就得找一下第二个SSID到底在哪儿了，目的是相对应的RSSI，也就是ssid[i-1]！=ssid_buf[i-1]，而我的ssid[i-1]必须存放与第一次一致的
                            for (int p = 0; p < AP_Scan_Num; p++) {          //如果我第二次第一个AP的SSID和第一次第一个的AP不同，那我就要看第二次第二个AP的相不相同，然后找到对应RSSI值
                                ScanResult AP_info_1 = results.get(p);  //然后对照着第一个SSID,一个个去查10个AP
                                if (ssid_from_DB[j].equals(AP_info_1.SSID)) {            //这里虽然是i-1，但是只要我对齐前面一个，后面的就都对了

                                    AP_buf[j][i] = Math.abs(AP_info_1.level);
                                  //  Log.i("Tian", "找到了乱序的ssid为" + ssid_from_DB[j] + "排到了第" + p + "位去了");
                                }
                                else{
                                 //   Log.i("Tian", "ssid_from_main应该是没有值" + ssid_from_DB[j]);
                                }
                                //Log.i("Tian", "第"+j+"个AP第"+p+"次扫描："+"没有找到乱序的SSID");
                            }
                        }
                    }
                }


                        /*----------------j>0时还要考虑录入第二个AP信息时，扫描后会不会由j造成的误差，即j==1时扫描时会不会排序发生翻转------------------*/
                if (j > 0) {
                    if (i == 0) {
                        ScanResult AP_info = results.get(j);//results是已经排好序存放所有AP的组，第一次取第一个AP的信息，并且把名称放入ssid[]中
                        if (ssid_from_DB[j].equals(AP_info.SSID)) {
                            AP_buf[j][i] = Math.abs(AP_info.level);

                        } else {          //这列虽然也是第一个，但是j!=0，也可能在扫描错位时进不来，也就是j==1，i==0时，扫描发生错位，你怎么办
                            for (int v = 0; v < AP_Scan_Num; v++) {
                                ScanResult AP_info_1 = results.get(v);  //然后对照着第一个SSID,一个个去查10个AP
                                if (ssid_from_DB[j].equals(AP_info_1.SSID)) {            //这里虽然是i-1，但是只要我对齐前面一个，后面的就都对了

                                    AP_buf[j][i] = Math.abs(AP_info_1.level);
                                   // Log.i("Tian", "找到了乱序的ssid为" + ssid_from_DB[j] + "排到了第" + v + "位去了");
                                }
                                else{
                                   // Log.i("Tian", "ssid_from_main应该是没有值" + ssid_from_DB[j]);
                                }

                            }
                        }

                    }


                    if (i > 0) {
                        ScanResult AP_info = results.get(j);//已经排好顺序的WIFI,第2-100次取第一个AP的信息，第2次排序时可能导致WIFI节点变化，而我RSSI还是按照第一次来的，所以得判断一下


                        if (ssid_from_DB[j].equals(AP_info.SSID)) {       //如果第二次接收到的名称和第一次存好的名称一致，也就是说，信号强度没变，第一还是第一个

                            AP_buf[j][i] = Math.abs(AP_info.level);     //并且把这一个的RSSI放入数组中为后面用

                        } else {                              //如果不相同，就得找一下第二个SSID到底在哪儿了，目的是相对应的RSSI，也就是ssid[i-1]！=ssid_buf[i-1]，而我的ssid[i-1]必须存放与第一次一致的
                            for (int p = 0; p < AP_Scan_Num; p++) {          //如果我第二次第一个AP的SSID和第一次第一个的AP不同，那我就要看第二次第二个AP的相不相同，然后找到对应RSSI值
                                ScanResult AP_info_1 = results.get(p);  //然后对照着第一个SSID,一个个去查10个AP
                                if (ssid_from_DB[j].equals(AP_info_1.SSID)) {            //这里虽然是i-1，但是只要我对齐前面一个，后面的就都对了

                                    AP_buf[j][i] = Math.abs(AP_info_1.level);

                                   // Log.i("Tian", "找到了乱序的ssid为" + ssid_from_DB[j] + "排到了第" + p + "位去了");
                                }
                                else{
                                  //  Log.i("Tian", "ssid_from_main应该是没有值" + ssid_from_DB[j]);
                                }
                            }
                        }
                    }
                }
                //AP_buf[j][i]=AP_info.level;

                try {
                    Thread.currentThread().sleep(Time);//毫秒延时A
                    // Log.i("Tian", "第"+j+"个AP第"+i+"次延时");
                } catch (Exception e) {
                }

            }
        }
        for(int g=0;g<AP_Num;g++) {
           // Log.i("Tian", "第"+g+"个SSID为" + ssid_from_DB[g]);
            //Log.i("Tian", "第"+g+"个SSID的RSSI值为" +"=="+AP_buf[g][0]+ AP_buf[g][1]+"=="+ AP_buf[g][2]+"=="+ AP_buf[g][3]+"=="+ AP_buf[g][4]+"=="+ AP_buf[g][5]+"=="+ AP_buf[g][6]);
        }

         /*--------------------到这里for执行完毕-下面对AP_buf算均值方差--------------------------*/
        Mean means = new Mean();
        Variance var = new Variance();

        for(kit=0;kit<AP_Num;kit++){

            AP_data[kit][0]=means.evaluate(AP_buf[kit]);//计算均值，k代表第k个AP点，AP_buf[k]为100个值求均值，AP_data为均值
            AP_data[kit][1]=var.evaluate(AP_buf[kit]);
            Log.i("Tian", "第"+kit+"个AP点算出含有粗大均值了:"+AP_data[kit][0]);
            Log.i("Tian", "第"+kit+"个AP点算出含有粗大方差了:"+AP_data[kit][1]);//这里因为扫描时间太快了，方差大部分可能为零
        }


        /*--------------------执行一些数据的放置--------------------------*/
        for(int u=0;u<AP_Num;u++){

            AP_final_mean[u] = AP_data[u][0];
            AP_final_var[u] = AP_data[u][1];
        }

        if(kit==AP_Num){
            flag_1=1;
            Log.i("Tian", " if(d==2)    flag_1="+flag_1);
            Log.i("Tian", " 并且唤醒主线程");

        }
        Max max = new Max();
        MaxRSSIVaule = max.evaluate(AP_final_mean);
        Min min = new Min();
        MinRSSIVaule = min.evaluate(AP_final_mean);

    }

    public double[] getAP_final_Online_mean() {
        return AP_final_mean;
    }

    public double[] getAP_final_Online_var() {
        return AP_final_var;
    }

    public String[] getAP_final_Online_ssid() {
        return ssid_from_DB;
    }

    public int getFlag_1() {
        return flag_1;
    }

    public double getMaxRSSIVaule() {
        return MaxRSSIVaule;
    }

    public double getMinRSSIVaule() {
        return MinRSSIVaule;
    }
}


