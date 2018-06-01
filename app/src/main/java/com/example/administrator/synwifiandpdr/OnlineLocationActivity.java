package com.example.administrator.synwifiandpdr;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.Variance;
import org.litepal.crud.DataSupport;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class OnlineLocationActivity extends AppCompatActivity {

    /*-----------定义XML--------------*/
    private TextView online_1;
    private TextView online_2;
    private Button LocationTri=null;
    private Button Kalman=null;
    private Button Scan_online=null;
    private Button InputDB=null;
    private Button Clear;
    private Button Location = null;
    private ImageView Picture= null;


    /*定义功能值*/
    private WifiManager wifi_manager;
    private WifiManager wifiManagerTri;
    private WifiManager wifiManagerLF;

    private double MaxRSSIOnline =0;
    private double MinRSSIOnline =0;

    private long ThreadSleepTime = 1000;//线程休息1秒
    private int TimeNum = 26;//在线连续扫描的次数



    /*-----------Location X  Y----------*/
    private double X_location_final;
    private double Y_location_final;



    private int Scan_Num = 30;//扫描次数
    private int AP_Num = 7;     //扫描节点数
    private int SomeNum = 0;//用于Online.setText,offline.setText显示，查看按键了屏幕的反应
    private int Scan_Ref_Num = 60;//扫描显示WIFI节点，这里是总的WIFI节点，因为扫描时可能会扫描到别的节点，太小的话会把我需要的wifi_location节点挤下去，导致均值为0
    private int PictureLenth = 615; //图片像素长度
    private int PictureWideth = 585; //图片像素宽度
    private int PicatureMiaoDianDistance =6; //描点的半径

    private int Tri_Method = 1;//对于Tri方法，我是选择什么方法：1:连续扫描模式，正常模式  2:根据59个参考点模式，这个模式用于算总体的方差，用于定权值，站在指纹点测看看误差有多少
    private int LF_Method = 2;//对于指纹方法，我是选择什么方法：1:连续扫描模式  2:根据59个参考点模式


    private double[] AP_Online_mean_main = new double[AP_Num];//在线
    private double[] AP_Online_var_main = new double[AP_Num];
    private String[] AP_Online_ssid_main=new String[AP_Num];

    private double[] AP_Online_mean_main_Tri = new double[AP_Num];//Tri连续在线储存
    private double[] AP_Online_var_main_Tri = new double[AP_Num];
    private String[] AP_Online_ssid_main_Tri=new String[AP_Num];


    private double[] AP_Online_mean_main_LF = new double[AP_Num];//指纹连续在线储存
    private double[] AP_Online_var_main_LF = new double[AP_Num];
    private String[] AP_Online_ssid_main_LF=new String[AP_Num];

    private  int flag_count=0;//offline Scan
    private int flag_count_1=0;//Online Scan
    private int flag_2;         //第一次按SCAN和第二次按SCAN保持SSID一致


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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_location);




        /*-------赋值XML的ID------------*/
        Location=(Button)findViewById(R.id.location);
        online_1 = (TextView)findViewById(R.id.Online_1);
        online_2 = (TextView)findViewById(R.id.Online_2);
        InputDB = (Button)findViewById(R.id.InputDB);
        Clear = (Button)findViewById(R.id.clear);
        LocationTri = (Button)findViewById(R.id.locationTr);
        Kalman = (Button)findViewById(R.id.Kalman);
        Scan_online=(Button)findViewById(R.id.Scan_online) ;

        Picture=(ImageView)findViewById(R.id.picture);
        bp1= BitmapFactory.decodeResource(getResources(),R.drawable.rtoom);
        new_bp1=bp1.copy(Bitmap.Config.ARGB_8888,true);



          /*---------------------------Clear 所有的描点---------------------------------------*/
        Clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int color_null = new_bp1.getPixel(x_picture,y_picture);//-1为白色-16777216为黑色，如果为黑色（即之前瞄过点），那么就把他们画成白色。指纹的
                int color_null2 = new_bp1.getPixel(x_picture_tri,y_picture_tri);//三边的

                if(color_null==-16777216){
                    for(int m=x_picture;m<(x_picture+PicatureMiaoDianDistance);m++){
                        for(int n=y_picture;n<(y_picture+PicatureMiaoDianDistance);n++){
                            new_bp1.setPixel(m,n,-1);
                        }
                    }
                }

                if(color_null2==-16777216){
                    for(int p=x_picture_tri;p<(x_picture_tri+PicatureMiaoDianDistance);p++){
                        for(int q=y_picture_tri;q<(y_picture_tri+PicatureMiaoDianDistance);q++){
                            new_bp1.setPixel(p,q,-1);
                        }
                    }
                }

            }
        });


          /*--------------------------Scan_online---------------------------------*/
        /*-------------在这里面我们要在线搜寻几次来确保数据准确性，在线阶段这里是，过程和离线采集数据的代码差不多*/
        /*---2017.11.4更新，我觉得还应该加一下滤波处理，防止出现一些大于60RSSI的点---------------*/
        Scan_online.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifi_manager = (WifiManager) OnlineLocationActivity.this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                wifi_manager.startScan();

                online_1.setText("On_line is Running"+SomeNum);
                SomeNum++;
                Calculate_OnlineScan ct3 = new Calculate_OnlineScan(wifi_manager);

                if (flag_count_1==0) {

                    Thread ct223 = new Thread(ct3);
                    ct223.start();

                    Log.i("Tian", "Scan_online线程开始运行flag==" + flag_count_1);
                    Log.i("Tian", "主线程开始阻塞)");
                    try {
                        ct223.join();
                        flag_count_1 = ct3.getFlag_1();
                        // Log.i("Tian", "第"+j+"个AP第"+i+"次延时");
                    } catch (Exception e) {
                    }



                }

                if(flag_count_1==1){
                    Log.i("Tian", "阻塞结束，已经按下了第二次Scan_online,flag_count_1==1，开始打印在线AP_final_main值");
                    AP_Online_ssid_main = ct3.getAP_final_Online_ssid();
                    AP_Online_mean_main = ct3.getAP_final_Online_mean();
                    AP_Online_var_main = ct3.getAP_final_Online_var();
                    MaxRSSIOnline = ct3.getMaxRSSIVaule();
                    MinRSSIOnline = ct3.getMinRSSIVaule();
                    if(MaxRSSIOnline>58){
                        online_1.setText("MaxRSSIOnline:"+MaxRSSIOnline);
                    }

                    for (int y = 0; y < AP_Num; y++) {
                        Log.i("Tian", "AP_Online_ssid_main:" + "-[" + y + "]------" + AP_Online_ssid_main[y]);
                        Log.i("Tian", "AP_Online_mean_main:" + "-[" + y + "]------" + AP_Online_mean_main[y]);
                        Log.i("Tian", "AP_Online_var_main:" + "-[" + y + "]-------" + AP_Online_var_main[y]);
                    }
                    flag_count_1++;

                }
                if(flag_count_1>1){
                    online_2.setText("0");
                    online_1.setText("On_line"+flag_count_1);
                    flag_count_1++;
                    if(flag_count_1>5){
                        flag_count_1=0;
                        online_1.setText("On_line"+flag_count_1+"已经置零");
                    }
                }

            }

        });


        /*---------------------------卡尔曼滤波计算---------------------------------------------------*/
        Kalman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*----double x_TriMeasure,double y_TriMeasure,double x_WKnnMeasure,double y_WKnnMeasure,
                             double[][] measureNoiseR,double[][] stateNoiseQ,double T,double X_init,double Y_init*/

                double measureNoiseR1[][]  = new double[4][4];
                for (int i = 0; i < 4; i++) {
                    for (int j = 0; j < 4; j++) {
                        measureNoiseR1[i][j] =1;
                    }
                }

                double stateNoiseQ2[][] = new double[4][4];
                for (int i = 0; i < 4; i++) {
                    for (int j = 0; j < 4; j++) {
                        measureNoiseR1[i][j] =2;
                    }
                }

                KalmanFilter k1 = new KalmanFilter(1,2,3,4,measureNoiseR1,stateNoiseQ2,6,7,8);
                k1.KalmanFilterMethod();

            }
        });




        /*---------------------------指纹Location---------------------------------------*/
        /*在Location里面，我们首先要创建一个定位的线程，里面要进行1：欧几里得加权距离公式算出测试点和参考点距离d（测试点从Scan_online获得，参考点在数据库内）。
        2：得到距离d后，选出几个d最小的数据，放入数组。
        3：进行加权取均值，最终得到定位点。
        4：在图片上进行描点*/
        Location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            /*------------自动扫描模式----------------*/
                for(int i=0;i<TimeNum;i++) {



                    if (LF_Method == 1) {

                        // LFtimer.schedule(LFTextFreshTask,1000,1000);

                        int RefrenceNum = 59;//参考点数量

                        //AP数量
                        int ApNum = 7;
                        int WKnnNum = 4;//最后WKNN定点的数量
                        double[] locationXY = new double[2];

                      /*------------WIFI扫描-------------*/
                        wifiManagerTri = (WifiManager) OnlineLocationActivity.this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        wifiManagerTri.startScan();

                        Calculate_OnlineScan ct_Tri = new Calculate_OnlineScan(wifiManagerTri);
                        double[] Distance_TriBuffer = new double[ApNum];


                        Thread ctTri = new Thread(ct_Tri);


                        ctTri.start();
                        while (ct_Tri.getFlag_1()!=1) ;//等待线程结束


                        if (ct_Tri.getFlag_1() == 1) {
                            Log.i("Tian", "ct_Tri.getFlag_1(): " + ct_Tri.getFlag_1());
                            AP_Online_ssid_main_LF = ct_Tri.getAP_final_Online_ssid();
                            AP_Online_mean_main_LF = ct_Tri.getAP_final_Online_mean();
                            AP_Online_var_main_LF = ct_Tri.getAP_final_Online_var();
                        }


                        Location_WKNN lw1 = new Location_WKNN(AP_Online_mean_main_LF, AP_Online_var_main_LF, AP_Online_ssid_main_LF,ThreadSleepTime);


                        Thread ctt2 = new Thread(lw1);
                        ctt2.start();

                        while (lw1.getFlag_1() != 1) ;//等待线程结束
                        flag_count = lw1.getFlag_1();


                        Log.i("Tian", "---------------------------------------------------------------------------主线程跳过了LF  while)----------------------------------------------------------------");
                        Log.i("Tian", TimeNum+"-------------------------------------------主线程跳过了LF  while)-----------------------------------------"+TimeNum);
                        locationXY[0] = lw1.getX_final_location();
                        locationXY[1] = lw1.getY_final_location();

             /*-----------把消息传送出去----------------------------*/
                        Message message2 = new Message();
                        message2.obj = locationXY;
                        handler_Tri.sendMessage(message2);


                    }

                }

             /*--------------参考点模式----------------------*/

                if(LF_Method == 2) {
                    Location_WKNN lw1 = new Location_WKNN(AP_Online_mean_main, AP_Online_var_main, AP_Online_ssid_main,ThreadSleepTime);


                    Thread ctt2 = new Thread(lw1);
                    ctt2.start();

                    while (lw1.getFlag_1() != 1) ;//等待线程结束
                    flag_count = lw1.getFlag_1();
                    Log.i("Tian", "主线程跳过了while)");
                    X_location_final = lw1.getX_final_location();
                    Y_location_final = lw1.getY_final_location();

                    String x11 = Double.toString(X_location_final);
                    String y11 = Double.toString(Y_location_final);
                    online_1.setText(x11);                          //把实时坐标显示在TEXT中
                    online_2.setText(y11);

                    if ((X_location_final != 0) && (Y_location_final != 0)) {
                        X_location_final = lw1.getX_final_location();
                        Y_location_final = lw1.getY_final_location();
                        Log.i("Tian", "------得到定位的X-------：" + X_location_final);
                        Log.i("Tian", "------得到定位的Y-------：" + Y_location_final);
                        SomeNum++;
                        online_1.setText("Location" + SomeNum);
                    /*------------------------把X_location_final实际物理坐标转换为像素坐标------------------------*/
                    /*------------------------把locXY实际物理坐标转换为像素坐标，这里要注意：------------------------*/
                    /*------像素坐标(0.0)在左上角，我的物理坐标(0.0)-在右上角，要转换一下！！！！----------------*/
                        ConvertEachOther ceo1 = new ConvertEachOther(X_location_final);
                        ConvertEachOther ceo2 = new ConvertEachOther(Y_location_final);
                        x_picture = ceo1.XiangsuToMi();
                        x_picture = PictureWideth - x_picture;        //像素宽度减去我的对称定位X值
                        y_picture = ceo2.XiangsuToMi();
                        Log.i("Tian", "------得到指纹定位的X的像素点-------：" + x_picture);
                        Log.i("Tian", "------得到指纹定位的Y的像素点-------：" + y_picture);
                        for (int i = x_picture; i < (x_picture + PicatureMiaoDianDistance); i++) {
                            for (int j = y_picture; j < (y_picture + PicatureMiaoDianDistance); j++) {
                                new_bp1.setPixel(i, j, -16777216);
                            }
                        }
                        Picture.setImageBitmap(new_bp1);

                    } else {
                        Log.i("Tian", "定位的X-------Y值为-----");

                        online_1.setText("Please SO");
                    }


                }

            }
        });

        /*----------三边定位按钮----------------*/
        LocationTri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*--------之前是想在线搜集RSSI值进行三边定位，现在我有指纹库啊，直接调指纹数据就OK了，有这么多，都可以直接算---------------*/

                    /*-所以下面这一段注释掉--*/
                    /* for(int i=0; i<AP_Num;i++){
                    RssiToDistance rs1 = new RssiToDistance();
                    Distance_Trilateration[i] = rs1.getPicLen_way(AP_Online_mean_main[i]);
                    }*/

               /*-------------------------------改用下面这一段-----------------------------*/
                /*----------------定义局部变量，每次用完消失，毕竟是在MAIN里面----------*/
                List<my_knn_db> offline_mean0_list;
                List<my_knn_db> offline_mean1_list;
                List<my_knn_db> offline_mean2_list;
                List<my_knn_db> offline_mean3_list;
                List<my_knn_db> offline_mean4_list;
                List<my_knn_db> offline_mean5_list;
                List<my_knn_db> offline_mean6_list;

                List<my_knn_db> offline_x_list;    //真实坐标
                List<my_knn_db> offline_y_list;

                int RefrenceNum = 59;//参考点数量

                //AP数量
                int ApNum = 7;
                int WKnnNum = 4;//最后WKNN定点的数量

                double[] off_mean0 = new double[RefrenceNum];
                double[] off_mean1 = new double[RefrenceNum];
                double[] off_mean2 = new double[RefrenceNum];
                double[] off_mean3 = new double[RefrenceNum];
                double[] off_mean4 = new double[RefrenceNum];
                double[] off_mean5 = new double[RefrenceNum];
                double[] off_mean6 = new double[RefrenceNum];
                double[] off_x = new double[RefrenceNum];
                double[] off_y = new double[RefrenceNum];

                double[][] mean_all = new double[RefrenceNum][ApNum];

                double Distance_Trilateration[][] = new double[RefrenceNum][ApNum]; //存D



                Log.i("Tian", "---------LocationTri Begin----------：");
                /*-----------------从数据库中取出来数据,这里三边定位和指纹定位的维数正好是相反地-----------------------------*/
                //X
                offline_x_list = DataSupport.select("x_0").find(my_knn_db.class);
                for (int i = 0; i < RefrenceNum; i++) {
                    my_knn_db d1 = offline_x_list.get(i);//把mean_0这一列数据取出来放在 off_mean0[i]内，i内存 for循环结束就被收回

                    off_x[i] = d1.getX_0();            //为什么是0？因为d1也是一个标准只有斜对角线有值得矩阵，所以会是0,1，2,3,4====，并且其他位置都为0
                    //Log.i("Tian", " off_mean0[0]==="+ off_mean0[i]);//谁叫你把d1设置成了一个LIST，所以当你把0换成i时，便会出现很多输出0

                    //  Log.i("Tian", " off_x[" + i + "]===" + off_x[i]);// mean_all[0][i]就时第一列数据
                }
                //Y
                offline_y_list = DataSupport.select("y_0").find(my_knn_db.class);
                for (int i = 0; i < RefrenceNum; i++) {
                    my_knn_db d2 = offline_y_list.get(i);//把mean_0这一列数据取出来放在 off_mean0[i]内，i内存 for循环结束就被收回

                    off_y[i] = d2.getY_0();            //为什么是0？因为d1也是一个标准只有斜对角线有值得矩阵，所以会是0,1，2,3,4====，并且其他位置都为0
                    //Log.i("Tian", " off_mean0[0]==="+ off_mean0[i]);//谁叫你把d1设置成了一个LIST，所以当你把0换成i时，便会出现很多输出0

                    // Log.i("Tian", " off_y[" + i + "]===" + off_y[i]);// mean_all[0][i]就时第一列数据
                }


                //0
                offline_mean0_list = DataSupport.select("mean_0").find(my_knn_db.class);
                for (int i = 0; i < RefrenceNum; i++) {
                    my_knn_db d1 = offline_mean0_list.get(i);//把mean_0这一列数据取出来放在 off_mean0[i]内，i内存 for循环结束就被收回

                    off_mean0[i] = d1.getMean(0);            //为什么是0？因为d1也是一个标准只有斜对角线有值得矩阵，所以会是0,1，2,3,4====，并且其他位置都为0
                    //Log.i("Tian", " off_mean0[0]==="+ off_mean0[i]);//谁叫你把d1设置成了一个LIST，所以当你把0换成i时，便会出现很多输出0
                    mean_all[i][0] = d1.getMean(0);
                    //  Log.i("Tian", " mean_all[" + i + "][0]===" + mean_all[i][0]);// mean_all[0][i]就时第一列数据
                }
                //1
                offline_mean1_list = DataSupport.select("mean_1").find(my_knn_db.class);
                for (int i = 0; i < RefrenceNum; i++) {
                    my_knn_db d1 = offline_mean1_list.get(i);
                    off_mean1[i] = d1.getMean(1);
                    mean_all[i][1] = d1.getMean(1);
                    // Log.i("Tian", " mean_all[" + i + "][1]===" + mean_all[i][1]);
                }
                //2
                offline_mean2_list = DataSupport.select("mean_2").find(my_knn_db.class);
                for (int i = 0; i < RefrenceNum; i++) {
                    my_knn_db d1 = offline_mean2_list.get(i);
                    off_mean2[i] = d1.getMean(2);
                    mean_all[i][2] = d1.getMean(2);
                    // Log.i("Tian", " mean_all[" + i + "][2]===" + mean_all[i][2]);
                }

                //3
                offline_mean3_list = DataSupport.select("mean_3").find(my_knn_db.class);
                for (int i = 0; i < RefrenceNum; i++) {
                    my_knn_db d1 = offline_mean3_list.get(i);
                    off_mean3[i] = d1.getMean(3);
                    mean_all[i][3] = d1.getMean(3);
                    // Log.i("Tian", " mean_all[" + i + "][3]===" + mean_all[i][3]);
                }
                //4
                offline_mean4_list = DataSupport.select("mean_4").find(my_knn_db.class);
                for (int i = 0; i < RefrenceNum; i++) {
                    my_knn_db d1 = offline_mean4_list.get(i);
                    off_mean4[i] = d1.getMean(4);
                    mean_all[i][4] = d1.getMean(4);
                    //  Log.i("Tian", " mean_all[" + i + "][4]===" + mean_all[i][4]);
                }
                //5
                offline_mean5_list = DataSupport.select("mean_5").find(my_knn_db.class);
                for (int i = 0; i < RefrenceNum; i++) {
                    my_knn_db d1 = offline_mean5_list.get(i);
                    off_mean5[i] = d1.getMean(5);
                    mean_all[i][5] = d1.getMean(5);
                    //  Log.i("Tian", " mean_all[" + i + "][5]===" + mean_all[i][5]);
                }
                //6
                offline_mean6_list = DataSupport.select("mean_6").find(my_knn_db.class);
                for (int i = 0; i < RefrenceNum; i++) {
                    my_knn_db d1 = offline_mean6_list.get(i);
                    off_mean6[i] = d1.getMean(6);
                    mean_all[i][6] = d1.getMean(6);
                    //  Log.i("Tian", " mean_all[" + i + "][6]===" + mean_all[i][6]);
                }

                /*-------------RSSI trans D------------------*/
                /*mean_all内存着[59][7]的RSSI数据，转换成D存入Distance_Trilateration*/

            /*--------------------下面是进行每隔1S的搜索  我要开始进行连续测量了--------------------------------*/
                if (Tri_Method == 1) {

                    //Tri_timer.schedule(TriTextFreshTask,1000,1000);


                /*--------------------下面是进行每隔1S的搜索  我要开始进行连续测量了--------------------------------*/

                    double locXY[] = new double[2];
                    for(int i=0;i<TimeNum;i++) {


                        if (Tri_Method == 1) {

                   /*------------WIFI扫描-------------*/
                            wifiManagerTri = (WifiManager) OnlineLocationActivity.this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                            wifiManagerTri.startScan();

                            Calculate_OnlineScan ct_Tri = new Calculate_OnlineScan(wifiManagerTri);
                            double[] Distance_TriBuffer = new double[ApNum];


                            Thread ctTri = new Thread(ct_Tri);
                            ctTri.start();
                            try {
                                ctTri.join();    //这里要堵3ms的样子

                                // Log.i("Tian", "第"+j+"个AP第"+i+"次延时");
                            } catch (Exception e) {
                            }
                            if (ct_Tri.getFlag_1() == 1) {
                                Log.i("Tian", "ct_Tri.getFlag_1(): " + ct_Tri.getFlag_1());
                                AP_Online_ssid_main_Tri = ct_Tri.getAP_final_Online_ssid();
                                AP_Online_mean_main_Tri = ct_Tri.getAP_final_Online_mean();
                                AP_Online_var_main_Tri = ct_Tri.getAP_final_Online_var();
                            }
                            for (int j = 0; j < ApNum; j++) {//RSSI转换为距离D
                       /*------对穿墙点进行赋值1----------*/
                                if (j == 6) {
                                    RssiToDistance rtd = new RssiToDistance(AP_Online_mean_main_Tri[j], 0);
                                    Distance_TriBuffer[j] = rtd.getPicLen_way();
                                } else {
                                    RssiToDistance rtd = new RssiToDistance(AP_Online_mean_main_Tri[j], 0);
                                    Distance_TriBuffer[j] = rtd.getPicLen_way();
                                }
                            }
                    /*----------把D输入Trilateration类------------*/
                            Trilateration tr1 = new Trilateration(Distance_TriBuffer, ThreadSleepTime);
                            Log.i("Tian", "------进入D到Trilateration的循环，定位循环，结果在Errors[]中-------");


                            Thread ct22 = new Thread(tr1);
                            ct22.start();
                            while (tr1.getFlag() != 1) ;
                            locXY = tr1.getLocation_XY();
                            Log.i("Tian", "-------------------------------------locXY-------------------------------: " + locXY[0] + "and" + locXY[1]);

                            Message m1 =new Message();
                            m1.obj = locXY;
                            handler_Tri.sendMessage(m1);
                        }

                    }




                }

    /*------------------------------------------------------------------------------------------------------------------------*/
            /*---------------------------进行定位的误差计算，也就是真实坐标和三边定位坐标差值---------------------------
            * 这里的程序是根据59个参考点来的  和连续的测量程序原理完全不同*/
                if(Tri_Method == 2)//根据59个参考点模式
                {

                    int id_num2=0;

                    for (int i = 0; i < RefrenceNum; i++) {
                        for (int j = 0; j < ApNum; j++) {
                        /*-----------------------------------------*/
                            if (mean_all[i][j] != 0)
                            {
                            /*------对穿墙点进行赋值1----------*/
                                if((i==1)&&(j==6)||(i==4)&&(j==5)||(i==4)&&(j==6)||(i==5)&&(j==6)||(i==6)&&(j==6)||(i==7)&&(j==6)||
                                        (i==28)&&(j==1)||(i==27)&&(j==0)||(i==27)&&(j==1)||(i==27)&&(j==2)||(i==28)&&(j==2)||(i==30)&&(j==1)|| (i==41)&&(j==0)||
                                        (i==41)&&(j==1)||(i==41)&&(j==3)||(i==42)&&(j==0)||(i==42)&&(j==1)||(i==42)&&(j==2)||(i==43)&&(j==0)||
                                        (i==43)&&(j==2)||(i==44)&&(j==0)||(i==44)&&(j==2)||(i==45)&&(j==1))
                                {
                                    id_num2=1;
                                }

                                RssiToDistance rtd = new RssiToDistance(mean_all[i][j],id_num2);
                                Distance_Trilateration[i][j] = rtd.getPicLen_way();
                                id_num2=0;

                                Log.i("Tian", " Distance_Trilateration[" + i + "][" + j + "]===" + Distance_Trilateration[i][j]+"id_num(也就是我判别要不要穿墙)为"+id_num2);
                            }
                        /*-----------------------------------------*/
                            else {
                                Log.i("Tian", " mean_all[" + i + "][" + j + "]好像===" + Distance_Trilateration[i][j]);
                            }
                        }
                    }




                    for (int u = 0; u < RefrenceNum; u++)//U计算误差
                    {
                    /*----------把D输入Trilateration类------------*/
                        Trilateration tr2 = new Trilateration(Distance_Trilateration[u],id_num2);
                        Log.i("Tian", "------进入D到Trilateration的循环，定位循环，结果在Errors[]中-------U的值为" + u);

                        Thread ct24 = new Thread(tr2);
                        ct24.start();
                        while (tr2.getFlag() != 1) ;

                        double locXY2[] = new double[2];
                        locXY2 = tr2.getLocation_XY();


                        X_Tri[u] = locXY2[0];
                        Y_Tri[u] = locXY2[1];//Tri定位的X Y点，放入一个数组，便于MATLAB计算

                        Errors[u] = Math.sqrt((off_x[u] - locXY2[0]) * (off_x[u] - locXY2[0]) + (off_y[u] - locXY2[1]) * (off_y[u] - locXY2[1]));//残差平方和开根号

                    }
                    for (int i = 0; i < RefrenceNum; i++) {
                        Log.i("Tian", "------得到三边定位的误差Errors[" + i + "]-------：" + Errors[i]);
                    }

                    Mean means = new Mean();
                    Variance var = new Variance();

                    double a = means.evaluate(Errors);
                    double b = var.evaluate(Errors);

                    Log.i("Tian", "------ Errors[i]平均值为-------：" + a);
                    Log.i("Tian", "------ Errors[i]方差为-------：" + b);

                    for (int i = 0; i < RefrenceNum; i++) {
                        Log.i("Tian", "------TRI的X[" + i + "]-------：" + X_Tri[i]);
                        Log.i("Tian", "------TRI的Y[" + i + "]-------：" + Y_Tri[i]);
                    }
                /*-------打印TriDistance------*/
                    for (int i = 0; i < RefrenceNum; i++) {
                        for (int j = 0; j < ApNum; j++) {

                            Log.i("Tian", " Distance_Trilateration[" + i + "][" + j + "]===" + Distance_Trilateration[i][j]);

                        }
                    }
                }




            }







        });


        InputDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Tian","--------------------InputDB---------DbTest------------------------");

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        DbManager manager = new DbManager(OnlineLocationActivity.this);
                        manager.openDataBase();//把路径赋值给了db
                        SQLiteDatabase db = manager.getDb();//db中包含数据库路径

                        Cursor cursor = db.query("my_knn_db", null, null, null, null, null, null);




                        Log.i("Tian","--------------------InputDB---------DbTest------------------------");

                        if (cursor.moveToFirst()) {
                            int mean_0, mean_1, mean_2,mean_3,mean_4,mean_5,mean_6,var_0,var_1,var_2,var_3,var_4,var_5,var_6,x_0,y_0;
                            int ssid_0,ssid_1,ssid_2,ssid_3,ssid_4,ssid_5,ssid_6;

                            mean_0 = cursor.getColumnIndex("mean_0");
                            mean_1 = cursor.getColumnIndex("mean_1");
                            mean_2 = cursor.getColumnIndex("mean_2");
                            mean_3 = cursor.getColumnIndex("mean_3");
                            mean_4 = cursor.getColumnIndex("mean_4");
                            mean_5 = cursor.getColumnIndex("mean_5");
                            mean_6 = cursor.getColumnIndex("mean_6");

                            var_0 = cursor.getColumnIndex("var_0");
                            var_1 = cursor.getColumnIndex("var_1");
                            var_2 = cursor.getColumnIndex("var_2");
                            var_3 = cursor.getColumnIndex("var_3");
                            var_4 = cursor.getColumnIndex("var_4");
                            var_5 = cursor.getColumnIndex("var_5");
                            var_6 = cursor.getColumnIndex("var_6");

                            x_0 = cursor.getColumnIndex("x_0");
                            y_0 = cursor.getColumnIndex("y_0");

                            ssid_0=cursor.getColumnIndex("ssid_0");
                            ssid_1=cursor.getColumnIndex("ssid_1");

                            Log.i("Tian","复制中:");
                            do {
                                String name = cursor.getString(ssid_0);
                                String name1 = cursor.getString(ssid_1);

                                int mean_0_0 = cursor.getInt(mean_0);
                                int mean_0_1 = cursor.getInt(mean_1);

                                String var_0_0  = cursor.getString(var_0);
                                String var_0_1  = cursor.getString(var_1);
                                //使用Log查看数据,未在界面展示
                                Log.i("Tian","name:"+name+" mean_0_1:"+mean_0_0+" var_0_1:"+var_0_0);
                                Log.i("Tian","name:"+name1+" mean_0_1:"+mean_0_1+" var_0_1:"+var_0_1);

                            }while(cursor.moveToNext());
                        }
                        manager.closeDataBase();
                    }
                }).start();

            }
        });



    }



    /*---------好像和TRI WKNN融合相关---------------------*/
     /*---------定义Tri定时器---------------*/
    private final Timer Tri_timer = new Timer();
    private Looper mainlooper;


    private int TimeNumDefinition;//定时器跑的次数
    private final TimerTask TriTextFreshTask  = new TimerTask() {
        @Override
        public void run() {

             /*-------判断定时器的次数-----------------*/
            if(TimeNumDefinition ==  TimeNum)
            {
                TimeNumDefinition =0;
                Tri_timer.cancel();
            }

            /*-----------搜索过程--------------*/
             /*----------------定义局部变量，每次用完消失，毕竟是在MAIN里面----------*/
            List<my_knn_db> offline_mean0_list;
            List<my_knn_db> offline_mean1_list;
            List<my_knn_db> offline_mean2_list;
            List<my_knn_db> offline_mean3_list;
            List<my_knn_db> offline_mean4_list;
            List<my_knn_db> offline_mean5_list;
            List<my_knn_db> offline_mean6_list;

            List<my_knn_db> offline_x_list;    //真实坐标
            List<my_knn_db> offline_y_list;

            int RefrenceNum = 59;//参考点数量

            //AP数量
            int ApNum = 7;
            int WKnnNum = 4;//最后WKNN定点的数量

            double[] off_mean0 = new double[RefrenceNum];
            double[] off_mean1 = new double[RefrenceNum];
            double[] off_mean2 = new double[RefrenceNum];
            double[] off_mean3 = new double[RefrenceNum];
            double[] off_mean4 = new double[RefrenceNum];
            double[] off_mean5 = new double[RefrenceNum];
            double[] off_mean6 = new double[RefrenceNum];
            double[] off_x = new double[RefrenceNum];
            double[] off_y = new double[RefrenceNum];

            double[][] mean_all = new double[RefrenceNum][ApNum];

            double Distance_Trilateration[][] = new double[RefrenceNum][ApNum]; //存D



            Log.i("Tian", "---------LocationTri Begin----------：");
                /*-----------------从数据库中取出来数据,这里三边定位和指纹定位的维数正好是相反地-----------------------------*/
            //X
            offline_x_list = DataSupport.select("x_0").find(my_knn_db.class);
            for (int i = 0; i < RefrenceNum; i++) {
                my_knn_db d1 = offline_x_list.get(i);//把mean_0这一列数据取出来放在 off_mean0[i]内，i内存 for循环结束就被收回

                off_x[i] = d1.getX_0();            //为什么是0？因为d1也是一个标准只有斜对角线有值得矩阵，所以会是0,1，2,3,4====，并且其他位置都为0
                //Log.i("Tian", " off_mean0[0]==="+ off_mean0[i]);//谁叫你把d1设置成了一个LIST，所以当你把0换成i时，便会出现很多输出0

                // Log.i("Tian", " off_x[" + i + "]===" + off_x[i]);// mean_all[0][i]就时第一列数据
            }
            //Y
            offline_y_list = DataSupport.select("y_0").find(my_knn_db.class);
            for (int i = 0; i < RefrenceNum; i++) {
                my_knn_db d2 = offline_y_list.get(i);//把mean_0这一列数据取出来放在 off_mean0[i]内，i内存 for循环结束就被收回

                off_y[i] = d2.getY_0();            //为什么是0？因为d1也是一个标准只有斜对角线有值得矩阵，所以会是0,1，2,3,4====，并且其他位置都为0
                //Log.i("Tian", " off_mean0[0]==="+ off_mean0[i]);//谁叫你把d1设置成了一个LIST，所以当你把0换成i时，便会出现很多输出0

                //  Log.i("Tian", " off_y[" + i + "]===" + off_y[i]);// mean_all[0][i]就时第一列数据
            }


            //0
            offline_mean0_list = DataSupport.select("mean_0").find(my_knn_db.class);
            for (int i = 0; i < RefrenceNum; i++) {
                my_knn_db d1 = offline_mean0_list.get(i);//把mean_0这一列数据取出来放在 off_mean0[i]内，i内存 for循环结束就被收回

                off_mean0[i] = d1.getMean(0);            //为什么是0？因为d1也是一个标准只有斜对角线有值得矩阵，所以会是0,1，2,3,4====，并且其他位置都为0
                //Log.i("Tian", " off_mean0[0]==="+ off_mean0[i]);//谁叫你把d1设置成了一个LIST，所以当你把0换成i时，便会出现很多输出0
                mean_all[i][0] = d1.getMean(0);
                //Log.i("Tian", " mean_all[" + i + "][0]===" + mean_all[i][0]);// mean_all[0][i]就时第一列数据
            }
            //1
            offline_mean1_list = DataSupport.select("mean_1").find(my_knn_db.class);
            for (int i = 0; i < RefrenceNum; i++) {
                my_knn_db d1 = offline_mean1_list.get(i);
                off_mean1[i] = d1.getMean(1);
                mean_all[i][1] = d1.getMean(1);
                // Log.i("Tian", " mean_all[" + i + "][1]===" + mean_all[i][1]);
            }
            //2
            offline_mean2_list = DataSupport.select("mean_2").find(my_knn_db.class);
            for (int i = 0; i < RefrenceNum; i++) {
                my_knn_db d1 = offline_mean2_list.get(i);
                off_mean2[i] = d1.getMean(2);
                mean_all[i][2] = d1.getMean(2);
                //Log.i("Tian", " mean_all[" + i + "][2]===" + mean_all[i][2]);
            }

            //3
            offline_mean3_list = DataSupport.select("mean_3").find(my_knn_db.class);
            for (int i = 0; i < RefrenceNum; i++) {
                my_knn_db d1 = offline_mean3_list.get(i);
                off_mean3[i] = d1.getMean(3);
                mean_all[i][3] = d1.getMean(3);
                //Log.i("Tian", " mean_all[" + i + "][3]===" + mean_all[i][3]);
            }
            //4
            offline_mean4_list = DataSupport.select("mean_4").find(my_knn_db.class);
            for (int i = 0; i < RefrenceNum; i++) {
                my_knn_db d1 = offline_mean4_list.get(i);
                off_mean4[i] = d1.getMean(4);
                mean_all[i][4] = d1.getMean(4);
                //Log.i("Tian", " mean_all[" + i + "][4]===" + mean_all[i][4]);
            }
            //5
            offline_mean5_list = DataSupport.select("mean_5").find(my_knn_db.class);
            for (int i = 0; i < RefrenceNum; i++) {
                my_knn_db d1 = offline_mean5_list.get(i);
                off_mean5[i] = d1.getMean(5);
                mean_all[i][5] = d1.getMean(5);
                //  Log.i("Tian", " mean_all[" + i + "][5]===" + mean_all[i][5]);
            }
            //6
            offline_mean6_list = DataSupport.select("mean_6").find(my_knn_db.class);
            for (int i = 0; i < RefrenceNum; i++) {
                my_knn_db d1 = offline_mean6_list.get(i);
                off_mean6[i] = d1.getMean(6);
                mean_all[i][6] = d1.getMean(6);
                // Log.i("Tian", " mean_all[" + i + "][6]===" + mean_all[i][6]);
            }

                /*-------------RSSI trans D------------------*/
                /*mean_all内存着[59][7]的RSSI数据，转换成D存入Distance_Trilateration*/

            /*--------------------下面是进行每隔1S的搜索  我要开始进行连续测量了--------------------------------*/
            double locXY[] = new double[2];
            if (Tri_Method == 1) {

                   /*------------WIFI扫描-------------*/
                wifiManagerTri = (WifiManager) OnlineLocationActivity.this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                wifiManagerTri.startScan();

                Calculate_OnlineScan ct_Tri = new Calculate_OnlineScan(wifiManagerTri);
                double[] Distance_TriBuffer = new double[ApNum];


                Thread ctTri = new Thread(ct_Tri);
                ctTri.start();
                try {
                    ctTri.join();    //这里要堵3ms的样子

                    // Log.i("Tian", "第"+j+"个AP第"+i+"次延时");
                } catch (Exception e) {
                }
                if(ct_Tri.getFlag_1()==1) {
                    Log.i("Tian", "ct_Tri.getFlag_1(): "+ct_Tri.getFlag_1());
                    AP_Online_ssid_main_Tri = ct_Tri.getAP_final_Online_ssid();
                    AP_Online_mean_main_Tri = ct_Tri.getAP_final_Online_mean();
                    AP_Online_var_main_Tri = ct_Tri.getAP_final_Online_var();
                }
                for (int j = 0; j < ApNum; j++) {//RSSI转换为距离D
                       /*------对穿墙点进行赋值1----------*/
                    if (j == 6) {
                        RssiToDistance rtd = new RssiToDistance(AP_Online_mean_main_Tri[j], 0);
                        Distance_TriBuffer[j] = rtd.getPicLen_way();
                    }
                    else {
                        RssiToDistance rtd = new RssiToDistance(AP_Online_mean_main_Tri[j], 0);
                        Distance_TriBuffer[j] = rtd.getPicLen_way();
                    }
                }
                    /*----------把D输入Trilateration类------------*/
                Trilateration tr1 = new Trilateration(Distance_TriBuffer, ThreadSleepTime);
                Log.i("Tian", "------进入D到Trilateration的循环，定位循环，结果在Errors[]中-------");


                Thread ct22 = new Thread(tr1);
                ct22.start();
                while (tr1.getFlag() != 1) ;


                locXY = tr1.getLocation_XY();



            }
            /*-----------把消息传送出去----------------------------*/
            Message message = new Message();
            message.obj = locXY;
            //handler_Tri.sendMessage(message);

        }



    };
    /*-------Tri消息处理---------------------*/
    public Handler handler_Tri = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            double locXY[] = new double[2];
            locXY  =  (double[]) msg.obj;

            Log.i("Tian", "------得到三边定位的X-------：" + locXY[0]);
            Log.i("Tian", "------得到三边定位的Y-------：" + locXY[1]);

            String abc = Double.toString(locXY[0]);
            String cba = Double.toString(locXY[1]);
            online_2.setText("TriX:"+abc);
            online_1.setText("TriY:"+cba);

            if ((locXY[0] != 0) && (locXY[1] != 0)) {
                    /*------------------------把locXY实际物理坐标转换为像素坐标，这里要注意：------------------------*/
                    /*------像素坐标(0.0)在左上角，我的物理坐标(0.0)-在右上角，要转换一下！！！！----------------*/
                ConvertEachOther ceo3 = new ConvertEachOther(locXY[0]);
                ConvertEachOther ceo4 = new ConvertEachOther(locXY[1]);
                x_picture_tri = ceo3.XiangsuToMi();
                x_picture_tri = PictureWideth - x_picture_tri;        //像素宽度减去我的对称定位X值
                y_picture_tri = ceo4.XiangsuToMi();

                Log.i("Tian", "------得到三边像素的x_picture_tri-------：" + x_picture_tri);
                Log.i("Tian", "------得到三边像素的y_picture_tri-------：" + y_picture_tri);


                if ((x_picture_tri > 0) && (x_picture_tri < 385) && (y_picture_tri < 405) && (y_picture_tri > 0)) {
                    for (int d = x_picture_tri; d < (x_picture_tri + PicatureMiaoDianDistance); d++) {
                        for (int j = y_picture_tri; j < (y_picture_tri + PicatureMiaoDianDistance); j++) {

                            new_bp1.setPixel(d, j, -16777216);

                        }
                    }
                    Log.i("Tian", "------画点完毕-------：");
                } else {
                    Log.i("Tian", "------x_picture_tri或者y_picture_tri小于0或者大于390 410------");
                }


            } else {
                Log.i("Tian", "定位的X-Y值为空--------Please SO" + x_picture_tri + "  and  " + y_picture_tri);
            }

            Picture.setImageBitmap(new_bp1);


        }
    };



    /*-------------LF定时器---------------------*/
    private final Timer LFtimer = new Timer();
    //定时器跑的次数   private int TimeNumDefinition;
    private final TimerTask LFTextFreshTask  = new TimerTask() {
        @Override
        public void run() {


            int RefrenceNum = 59;//参考点数量

            //AP数量
            int ApNum = 7;
            int WKnnNum = 4;//最后WKNN定点的数量
            double[] locationXY = new double[2];

            /*-------判断定时器的次数-----------------*/
            if(TimeNumDefinition ==  TimeNum)
            {
                TimeNumDefinition =0;
                LFtimer.cancel();
            }

               /*------------WIFI扫描-------------*/
            wifiManagerTri = (WifiManager) OnlineLocationActivity.this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            wifiManagerTri.startScan();

            Calculate_OnlineScan ct_Tri = new Calculate_OnlineScan(wifiManagerTri);
            double[] Distance_TriBuffer = new double[ApNum];


            Thread ctTri = new Thread(ct_Tri);
            ctTri.start();
            try {
                ctTri.join();    //这里要堵3ms的样子

                // Log.i("Tian", "第"+j+"个AP第"+i+"次延时");
            } catch (Exception e) {
            }
            if(ct_Tri.getFlag_1()==1) {
                Log.i("Tian", "ct_Tri.getFlag_1(): "+ct_Tri.getFlag_1());
                AP_Online_ssid_main_LF = ct_Tri.getAP_final_Online_ssid();
                AP_Online_mean_main_LF = ct_Tri.getAP_final_Online_mean();
                AP_Online_var_main_LF = ct_Tri.getAP_final_Online_var();
            }




            Location_WKNN lw1 = new Location_WKNN(AP_Online_mean_main_LF, AP_Online_var_main_LF, AP_Online_ssid_main_LF,ThreadSleepTime);


            Thread ctt2 = new Thread(lw1);
            ctt2.start();

            while (lw1.getFlag_1() != 1) ;//等待线程结束
            flag_count = lw1.getFlag_1();


            Log.i("Tian", "主线程跳过了LF  while)");
            locationXY[0] = lw1.getX_final_location();
            locationXY[1] = lw1.getY_final_location();

             /*-----------把消息传送出去----------------------------*/
            Message message2 = new Message();
            message2.obj = locationXY;
            handler_Tri.sendMessage(message2);


        }
    };
    /*-------------LF  Handle---------------------*/
    Handler handler_LF = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);


            double[] locationXY = new double[2];
            locationXY  =  (double[]) msg.obj;

            String x11 = Double.toString(locationXY[0]);
            String y11 = Double.toString(locationXY[1]);
            online_1.setText("LFX:"+x11);                          //把实时坐标显示在TEXT中
            online_2.setText("LFY:"+y11);

            if ((locationXY[0] != 0) && (locationXY[1] != 0)) {

                Log.i("Tian", "------得到定位的X-------：" + locationXY[0]);
                Log.i("Tian", "------得到定位的Y-------：" + locationXY[1]);


                    /*------------------------把X_location_final实际物理坐标转换为像素坐标------------------------*/
                    /*------------------------把locXY实际物理坐标转换为像素坐标，这里要注意：------------------------*/
                    /*------像素坐标(0.0)在左上角，我的物理坐标(0.0)-在右上角，要转换一下！！！！----------------*/
                ConvertEachOther ceo1 = new ConvertEachOther(X_location_final);
                ConvertEachOther ceo2 = new ConvertEachOther(Y_location_final);
                x_picture = ceo1.XiangsuToMi();
                x_picture = PictureWideth - x_picture;        //像素宽度减去我的对称定位X值
                y_picture = ceo2.XiangsuToMi();
                Log.i("Tian", "------得到指纹定位的X的像素点-------：" + x_picture);
                Log.i("Tian", "------得到指纹定位的Y的像素点-------：" + y_picture);
                for (int i = x_picture; i < (x_picture + PicatureMiaoDianDistance); i++) {
                    for (int j = y_picture; j < (y_picture + PicatureMiaoDianDistance); j++) {
                        new_bp1.setPixel(i, j, -16777216);
                    }
                }
                Picture.setImageBitmap(new_bp1);

            } else {
                Log.i("Tian", "定位的X-------Y值为-----");

                online_1.setText("Please SO");
            }

        }
    };



}
