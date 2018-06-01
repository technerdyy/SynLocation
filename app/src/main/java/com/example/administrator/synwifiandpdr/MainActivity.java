package com.example.administrator.synwifiandpdr;

import android.graphics.Bitmap;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import java.lang.Math;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.Variance;
import org.apache.commons.math3.stat.descriptive.rank.Max;
import org.apache.commons.math3.stat.descriptive.rank.Min;
import org.apache.commons.math3.stat.descriptive.summary.Sum;
import org.litepal.crud.DataSupport;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;



public class MainActivity extends AppCompatActivity {


    /*XML定义*/
    private EditText X_REF = null;
    private EditText Y_REF = null;
    private EditText AP_Refrence;
    private Button XY_CONFIR = null;
    private Button Init = null;
    private ImageView Picture= null;
    private Button Scan_offline=null;
    private TextView offline1 =null;
    private TextView offline2=null;
    private Button deleteDB = null;
    private Button ToOnline = null;

    /*定义功能值*/
    private WifiManager wifi_manager;
    private int Scan_Num = 30;//扫描次数
    private int AP_Num = 7;     //扫描节点数
    private int SomeNum = 0;//用于Online.setText,offline.setText显示，查看按键了屏幕的反应
    private int Scan_Ref_Num = 60;//扫描显示WIFI节点，这里是总的WIFI节点，因为扫描时可能会扫描到别的节点，太小的话会把我需要的wifi_location节点挤下去，导致均值为0
    private int PictureLenth = 615; //图片像素长度
    private int PictureWideth = 585; //图片像素宽度
    private int PicatureMiaoDianDistance =6; //描点的半径

    private int Tri_Method = 1;//对于Tri方法，我是选择什么方法：1:连续扫描模式，正常模式  2:根据59个参考点模式，这个模式用于算总体的方差，用于定权值，站在指纹点测看看误差有多少
    private int LF_Method = 2;//对于指纹方法，我是选择什么方法：1:连续扫描模式  2:根据59个参考点模式


    private WifiManager wifiManagerTri;
    private WifiManager wifiManagerLF;


    private double[] AP_final_mean_main = new double[AP_Num];//离线采集用
    private double[] AP_final_var_main = new double[AP_Num];
    private String[] AP_final_ssid_main=new String[AP_Num];



    private String[]  x_editview_string = new String[Scan_Ref_Num];//编辑窗口
    private String[]  y_editview_string = new String[Scan_Ref_Num];


    private Double[]  y_editview_int = new Double[Scan_Ref_Num];
    private Double[]  x_editview_int = new Double[Scan_Ref_Num];


    private  int flag_count=0;//offline Scan
    private int flag_count_1=0;//Online Scan
    private int flag_2;         //第一次按SCAN和第二次按SCAN保持SSID一致

    private  boolean flag_inmain=false;

    /*------------BitMap画图--------------------*/

    private Bitmap bp1;
    private Bitmap new_bp1;
    private Bitmap test_bp1;
    private int x_picture;      //指纹画点变量  x_picture定义为全局是因为我Clear函数里面也要用到
    private int y_picture;

    private int x_picture_tri;      //三边的画点变量
    private int y_picture_tri;
    /*-----Trilateration中用到的全局---------*/
    double Errors[] = new double[59];//为什么放在这里，因为要和WKNN综合误差取权，所以必须全局
    double X_Tri[] = new double[59];
    double Y_Tri[] = new double[59];

    private Button b11 = null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);





        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        b11 = (Button)findViewById(R.id.b11);

        X_REF=(EditText) findViewById(R.id.X_REF);
        Y_REF=(EditText) findViewById(R.id.Y_REF);
        AP_Refrence = (EditText)findViewById(R.id.AP_refrence);

        XY_CONFIR=(Button)findViewById(R.id.XY_Confir);
        Init=(Button)findViewById(R.id.Init);
        Scan_offline=(Button)findViewById(R.id.scan_offline);
        ToOnline = (Button)findViewById(R.id.toOnline);

        offline1=(TextView)findViewById(R.id.offline1);
        offline2=(TextView)findViewById(R.id.offline2);

        Picture=(ImageView)findViewById(R.id.picture);
        bp1= BitmapFactory.decodeResource(getResources(),R.drawable.rtoom);
        new_bp1=bp1.copy(Bitmap.Config.ARGB_8888,true);


        b11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DataSupport.delete(my_knn_db.class,99);
                Log.i("Tian", "deleted 99");

            }
        });


         /*--------------------Activity转向在现阶段
            1:把
         -------------------------*/
        ToOnline.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                Intent int1 = new Intent(MainActivity.this, OnlineLocationActivity.class);

                Intent inttents[] = new Intent[]{int1};

                startActivities(inttents);

            }
        });




        /*--------------------初始化数据库-------------------------*/
        Init.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                int wifi_num=0;
                WifiManager wifi_manager_init= null;

                Log.i("Tian", "Init开始");
                Creat_Wifi db1 = new Creat_Wifi();

                db1.Creat_DB_Way();
                Log.i("Tian", "数据库建立成功");

                wifi_manager_init=(WifiManager) MainActivity.this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                wifi_manager_init.startScan();
                List<ScanResult> results_init = wifi_manager_init.getScanResults();
                wifi_num=results_init.size();
                offline1.setText("WifiNum"+wifi_num);

                BitmapFactory.Options bfoOptions = new BitmapFactory.Options();//缩放关闭，让图片保持源像素
                bfoOptions.inScaled = false;
                int PictureHeight;int PictureWidth;int new_PictureHeight;int new_PictureWidth;
                PictureHeight = bp1.getHeight();
                PictureWidth = bp1.getWidth();
                new_PictureHeight = new_bp1.getHeight();
                new_PictureWidth = new_bp1.getWidth();
                /*------检测底图能不能描点-----*/
                int aaaa= new_bp1.getPixel(3,5);
                Log.i("Tian", "Color Value"+aaaa);
                for(int i=40;i<50;i++){
                    for(int j=360;j<370;j++){
                        new_bp1.setPixel(i,j,aaaa);
                        Log.i("Tian", "Color Value"+aaaa);

                    }
                }
                Picture.setImageBitmap(new_bp1);
                offline2.setText("H:"+PictureHeight+"W"+PictureWidth);
                Log.i("Tian", "H:"+PictureHeight+"W"+PictureWidth);
                Log.i("Tian", "NH:"+new_PictureHeight+"NW"+new_PictureWidth);
                //new_bp1=bp1.copy(Bitmap.Config.ARGB_8888,true);

            }
        });
        /*--------------------浏览WIFI节点，并且进行100次均值方差计算，用3西格玛原则-------------------------*/
        Scan_offline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                wifi_manager = (WifiManager) MainActivity.this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                wifi_manager.startScan();
                Calculate ct2 = new Calculate(wifi_manager,flag_2,AP_final_ssid_main);

                int testInScan = new_bp1.getPixel(120,20);
                //假如点为白色，就描黑
                if(testInScan == -1){
                    for (int m = 120; m < 125; m++) {           //既然TextView不显示，那我描点总可以吧
                        for (int n = 20; n < 25; n++) {
                            new_bp1.setPixel(m, n, -16777216);
                            Log.i("Tian", "m:"+m+"n"+n);
                        }
                    }
                }
                else {
                    //假如点为黑色，就苗白
                    for (int m = 120; m < (120 + 5); m++) {           //既然TextView不显示，那我描点总可以吧
                        for (int n = 20; n < (20 + 5); n++) {
                            new_bp1.setPixel(m, n, -1);
                        }
                    }
                }
                Picture.setImageBitmap(new_bp1);                   //一定要启动涂层才会显示！！！！！

                if (flag_count==0) {

                    Thread ct222 = new Thread(ct2);
                    ct222.start();


                    while(ct2.getFlag_1()!=1);//等待线程结束

                    Log.i("Tian", "主线程跳过了while)");
                    flag_count = ct2.getFlag_1();
                    flag_2 = ct2.getFlag_2();


                    Log.i("Tian", "线程开始运行flag_count==" + flag_count);
                    Log.i("Tian", "flag_2==" + flag_2);

                }


                if (flag_count==1)
                {

                    AP_final_mean_main = ct2.getAP_final_mean();
                    AP_final_var_main = ct2.getAP_final_var();
                    AP_final_ssid_main=ct2.getAP_final_ssid();
                    for (int y = 0; y < AP_Num; y++) {
                        Log.i("Tian", "AP_final_ssid:" + "-[" + y + "]------" + AP_final_ssid_main[y]);
                        Log.i("Tian", "AP_final_mean_main:" + "-[" + y + "]------" + AP_final_mean_main[y]);
                        Log.i("Tian", "AP_final_var_main:" + "-[" + y + "]-------" + AP_final_var_main[y]);
                    }
                    flag_inmain=true;//flag_inmain用于和XY_CONFIR互动
                    Log.i("Tian", "flag==" + flag_count);
                    Log.i("Tian", "子线程结束运行");
                    flag_count++;
                }
                if(flag_count>1){
                    Log.i("Tian", "请不要重复按Scan");
                    flag_count++;
                    offline1.setText("Scan Finish"+flag_count);
                    if(flag_count>5){flag_count=0;}
                }


            }
        });

        /*---------------------下面是把AP_data与参考点x,y值配对一起存入数据库中-------------------------------*/
        XY_CONFIR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Add_Wifi aw1 = new Add_Wifi();
                String ap_ref = AP_Refrence.getText().toString();//第几个AP参考点
                int ap_ref_int = Integer.parseInt(ap_ref);
                int convinence = ap_ref_int;
                Log.i("Tian", "--------------convinence------------"+convinence);
                Log.i("Tian", "--------------flag_inmain------------"+flag_inmain);
                /*-------------设置了多少个AP指纹点，这里就有多少个 if(ap_ref_int==0)----------------------*/
                /*想了一想，我觉得离线阶段还是把每个AP点的物理坐标直接在程序里面写好(根本不需要AP点的物理坐标，只要参考点的就行了)，我这里getText获得的XY值用于我参考点的物理坐标。
                * 不然10个AP点的物理坐标很难输入*/

                if(flag_inmain==true) {
                                 /*-----------------获取AP参考点，XY-----------------------*/
                                /*这里的0是因为ap_ref_int==0，也就是第0个参考AP点，看自己需要多少个指纹点*/
                                /*x_editview_int是参考点的物理坐标，十分重要，到时候要取平均的*/

                    switch (ap_ref_int){

                        case 1:
                            Log.i("Tian", "-----------------第0个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(1,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;

                        case 2:
                            Log.i("Tian", "-----------------第1个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(2,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;


                        case 3:
                            Log.i("Tian", "-----------------第2个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(3,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;

                        case 4:
                            Log.i("Tian", "-----------------第3个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(4,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;

                        case 5:
                            Log.i("Tian", "-----------------第4个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(5,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;

                        case 6:
                            Log.i("Tian", "-----------------第5个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(6,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;

                        case 7:
                            Log.i("Tian", "-----------------第6个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(7,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;

                        case 8:
                            Log.i("Tian", "-----------------第7个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(8,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;

                        case 9:
                            Log.i("Tian", "-----------------第8个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(9,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;


                        case 10:
                            Log.i("Tian", "-----------------第9个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(10,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;

                        case 11:
                            Log.i("Tian", "-----------------第10个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(11,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;

                        case 12:
                            Log.i("Tian", "-----------------第11个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(12,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;


                        case 13:
                            Log.i("Tian", "-----------------第11个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(13,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;


                        case 14:
                            Log.i("Tian", "-----------------第11个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(14,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;


                        case 15:
                            Log.i("Tian", "-----------------第11个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(15,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;


                        case 16:
                            Log.i("Tian", "-----------------第11个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(16,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;


                        case 17:
                            Log.i("Tian", "-----------------第11个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(17,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;


                        case 18:
                            Log.i("Tian", "-----------------第11个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(18,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;


                        case 19:
                            Log.i("Tian", "-----------------第11个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(19,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;


                        case 20:
                            Log.i("Tian", "-----------------第11个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(20,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;


                        case 21:
                            Log.i("Tian", "-----------------第11个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(21,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;


                        case 22:
                            Log.i("Tian", "-----------------第11个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(22,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;


                        case 23:
                            Log.i("Tian", "-----------------第11个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(23,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;


                        case 24:
                            Log.i("Tian", "-----------------第11个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(24,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;


                        case 25:
                            Log.i("Tian", "-----------------第11个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(25,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;


                        case 26:
                            Log.i("Tian", "-----------------第11个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(26,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;


                        case 27:
                            Log.i("Tian", "-----------------第11个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(27,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;


                        case 28:
                            Log.i("Tian", "-----------------第11个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(28,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;


                        case 29:
                            Log.i("Tian", "-----------------第11个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(29,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;


                        case 30:
                            Log.i("Tian", "-----------------第11个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(30,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;


                        case 31:
                            Log.i("Tian", "-----------------第11个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(31,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;


                        case 32:
                            Log.i("Tian", "-----------------第11个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(32,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;


                        case 33:
                            Log.i("Tian", "-----------------第11个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(33,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;


                        case 34:
                            Log.i("Tian", "-----------------第11个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(34,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;


                        case 35:
                            Log.i("Tian", "-----------------第11个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(35,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;


                        case 36:
                            Log.i("Tian", "-----------------第11个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(36,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;


                        case 37:
                            Log.i("Tian", "-----------------第11个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(37,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;


                        case 38:
                            Log.i("Tian", "-----------------第11个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(38,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;


                        case 39:
                            Log.i("Tian", "-----------------第11个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(39,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;


                        case 40:
                            Log.i("Tian", "-----------------第11个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(40,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;


                        case 41:
                            Log.i("Tian", "-----------------第11个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(41,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;



                        case 42:
                            Log.i("Tian", "-----------------第11个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(42,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;



                        case 43:
                            Log.i("Tian", "-----------------第11个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(43,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;


                        case 44:
                            Log.i("Tian", "-----------------第11个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(44,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;


                        case 45:
                            Log.i("Tian", "-----------------第11个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(45,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;


                        case 46:
                            Log.i("Tian", "-----------------第11个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(46,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;


                        case 47:
                            Log.i("Tian", "-----------------第11个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(47,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;


                        case 48:
                            Log.i("Tian", "-----------------第11个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(48,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;


                        case 49:
                            Log.i("Tian", "-----------------第11个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(49,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;


                        case 50:
                            Log.i("Tian", "-----------------第11个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(50,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;


                        case 51:
                            Log.i("Tian", "-----------------第11个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(51,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;


                        case 52:
                            Log.i("Tian", "-----------------第11个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(52,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;


                        case 53:
                            Log.i("Tian", "-----------------第11个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(53,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;


                        case 54:
                            Log.i("Tian", "-----------------第11个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(54,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;


                        case 55:
                            Log.i("Tian", "-----------------第11个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(55,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;

                        case 56:
                            Log.i("Tian", "-----------------第11个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(56,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;

                        case 57:
                            Log.i("Tian", "-----------------第11个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(57,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;

                        case 58:
                            Log.i("Tian", "-----------------第11个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(58,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;

                        case 59:
                            Log.i("Tian", "-----------------第11个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(59,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;


                        case 60:
                            Log.i("Tian", "-----------------第11个AP参考点------------ap_ref为:"+ap_ref);


                            if((X_REF.getText().toString()!=null)&&(Y_REF.getText().toString()!=null))
                            {
                                x_editview_string[convinence] = X_REF.getText().toString();
                                x_editview_int[convinence] = Double.valueOf(x_editview_string[convinence]);
                                y_editview_string[convinence] = Y_REF.getText().toString();
                                y_editview_int[convinence] = Double.valueOf(y_editview_string[convinence]);
                                Log.i("Tian", "参考点"+convinence+"的物理坐标已经准备好为"+ x_editview_int[convinence]+"----"+y_editview_int[convinence]);
                            }
                            else { Log.i("Tian", "请输入X以及Y的值");}

                            aw1.Add_DB_Way(60,
                                    AP_final_ssid_main[0],AP_final_ssid_main[1],AP_final_ssid_main[2],AP_final_ssid_main[3],AP_final_ssid_main[4],AP_final_ssid_main[5],AP_final_ssid_main[6],
                                    x_editview_int[convinence],
                                    y_editview_int[convinence],
                                    AP_final_mean_main[0], AP_final_mean_main[1], AP_final_mean_main[2], AP_final_mean_main[3], AP_final_mean_main[4], AP_final_mean_main[5], AP_final_mean_main[6],
                                    AP_final_var_main[0], AP_final_var_main[1], AP_final_var_main[2], AP_final_var_main[3], AP_final_var_main[4], AP_final_var_main[5], AP_final_var_main[6]);
                            Log.i("Tian", "参考点"+convinence+"的数据已经加入数据库-----------------------------");
                            break;



                    }
                    flag_inmain=false;
                    offline2.setText("");
                    offline2.setText("XY confer is ok"+SomeNum);
                    SomeNum++;
                }
                else {
                    offline2.setText("");
                    offline2.setText("Ples Scan first"+SomeNum);
                    SomeNum++;
                }
            }
        });






    }
}
