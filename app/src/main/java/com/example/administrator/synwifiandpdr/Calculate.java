package com.example.administrator.synwifiandpdr;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.Variance;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Administrator on 2017/9/26.
 */

public class Calculate extends AppCompatActivity implements Runnable {
    /*定义功能值*/
    private WifiManager wifi_manager_2;
    private int Scan_Num = 50;
    private int AP_Num = 7;
    private int AP_Scan_Num=22;         //如果AP节点到了晚上没这么多时，会发生ARRAY  INDEX超出的错误  要注意
    private int TimeSleep=400;          //扫描间隔时间

    private double[] AP_final_mean = new double[AP_Num];
    private double[] AP_final_var = new double[AP_Num];
    private String[] AP_final_ssid=new String[AP_Num];
    private String[] AP_final_ssid_fromMain=new String[AP_Num];

    private String  x_editview_string;
    private String  y_editview_string;
    private int  y_editview_int;
    private int  x_editview_int;
    private int flag_1;//用于和Main交互，取值判断要不要跳过while死循环
    private int flag_2;//用于保持第一次SCAN和第二次按下SCAN时，AP_final_ssid数组保持一致，好让我在KNN时好运算
    private int kaiguan=1;//定义是自己往AP_final_ssid里面写数据，还是SCAN.0为扫描，1位预定义的


    private String[] kaiguan_String = new String[AP_Num];


    public Calculate(WifiManager wifi_manager_2,int flag_2,String[] AP_final_ssid){
        this.wifi_manager_2=wifi_manager_2;
        this.flag_2=flag_2;
        this.AP_final_ssid_fromMain=AP_final_ssid;

    }

    @Override
    public void run() {
        Log.i("Tian", "进入Calculate");


        Log.i("Tian", "wifiManagerOK");

        int buf2Num_AP0=0;
        int buf2Num_AP1=0;
        int buf2Num_AP2=0;
        int buf2Num_AP3=0;
        int buf2Num_AP4=0;
        int buf2Num_AP5=0;
        int buf2Num_AP6=0;
        /*-------------先验SSID--------------*/
        kaiguan_String[0]="wifi_location0";
        kaiguan_String[1]="wifi_location2";
        kaiguan_String[2]="wifi_location3";
        kaiguan_String[3]="wifi_location4";
        kaiguan_String[4]="wifi_location5";
        kaiguan_String[5]="wifi_location6";
        kaiguan_String[6]="wifi_location7";

        double[][] AP_buf = new double[AP_Num][Scan_Num];//存储求均值方差的AP  WIFI值
        double[][] AP_vi = new double[AP_Num][Scan_Num];//存储残差，用于求西格玛
        double[][] AP_data = new double[AP_Num][Scan_Num];//存储求均值方差的AP  WIFI值,10个AP，每个AP有100平均的均值和方差

        double[][] AP_vi_sum = new double[AP_Num][1];//用于残差求取均方根误差用
        double[][] AP_vi_sum_chu100 = new double[AP_Num][1];//用于残差求取均方根误差用
        double[][] AP_3XG = new double[AP_Num][1];//装3西格玛的数组

        for(int j=0;j<AP_Num;j++)  {
            for(int i=0;i<Scan_Num;i++){


                wifi_manager_2.startScan();
                List<ScanResult> results = wifi_manager_2.getScanResults();//results含有很多AP信息
                        /*WiFi RSSI信号从大到小排序*/

                if((j==0)&&(i==0)&&(flag_2==0))//如果第二次按键SCAN，就会不排序，按照第一次排序成功后，传进来的AP_final_ssid放置
                {
                    Collections.sort(results, new Comparator<ScanResult>() {
                    @Override
                    public int compare(ScanResult o1, ScanResult o2) {
                        if(o1.level>o2.level){
                            return -1;
                        }
                        else if(o1.level<o2.level){//为1就把02丢到01前面去
                            return 1;
                        }
                        else{
                            return 0;
                        }
                    }
                    });
                }
                /*--经过上面的放到第一次排序，会把信号强度按大到小放到results内---*/



                Log.i("Tian", "第"+j+"个AP点"+"的第"+i+"次Scan内排序完毕");
                /*----------------------j==0时因为是第一次，所以什么都不用管------------------*/
                if(j==0) {
                    /*---------第一个if-----------------*/
                    if ((i == 0)&&(flag_2==0)&&(kaiguan==0))          //第一次SCAN
                    {                               //i代表扫描的次数，应该为100次扫描，这里代表第一次扫描
                        for (int q = 0; q < AP_Num; q++)
                        {
                            ScanResult AP_info = results.get(q);//results是已经排好序存放所有AP的组
                                                                //把名称也排好了
                            AP_final_ssid[q] = AP_info.SSID;             //AP_final_ssid就是我最终要的名称序列
                            AP_buf[q][i] =Math.abs(AP_info.level);  //相对应的RSSI值放进去，q是代表不同的AP点
                        }
                        Log.i("Tian", "(i == 0)&&(flag_2==0)&&(kaiguan==0)扫描模式，AP_final_ssid分别是"+AP_final_ssid[0]+"="+AP_final_ssid[1]+"="+AP_final_ssid[2]+"="+AP_final_ssid[3]+"="+AP_final_ssid[4]+"="+AP_final_ssid[5]+"="+AP_final_ssid[6]);
                    }

                    /*---------第二个if-----------------*/
                    if((i==0)&&(flag_2==0)&&(kaiguan==1))
                    {
                        for (int q = 0; q < AP_Num; q++)
                        {
                            AP_final_ssid[q] = kaiguan_String[q];//人为定义我要接受哪些AP源

                        }

                        Log.i("Tian", "(i==0)&&(flag_2==0)&&(kaiguan==1)固定模式");
                        /*完成对AP_buf[0][0]的赋值*/
                        for(int p=0;p<AP_Scan_Num;p++)
                        {
                            Log.i("Tian", " ScanResult AP_info大小为："+results.size());
                            ScanResult AP_info = results.get(p);//results是已经排好序存放所有AP的组
                            if (AP_final_ssid[j].equals(AP_info.SSID)) {
                                AP_buf[j][i] = Math.abs(AP_info.level);  //相对应的RSSI值放进去，q是代表不同的AP点
                                Log.i("Tian", "第" + j + "个AP点" + "的第" + i + "次扫描结果放入了AP_buf[0][i]："+AP_buf[j][i]);
                            } else {
                                Log.i("Tian", "AP_final_ssid[0]！=当前AP_info.SSID，即：" + AP_final_ssid[j] + "不等于" + AP_info.SSID);
                            }

                        }


                    }
                    /*---------第三个if-----------------*/
                    if ((i == 0)&&(flag_2!=0))          //第二次SCAN
                    {                               //i代表扫描的次数，应该为100次扫描，这里代表第一次扫描

                        AP_final_ssid[0] = AP_final_ssid_fromMain[0];
                        AP_final_ssid[1] = AP_final_ssid_fromMain[1];
                        AP_final_ssid[2] = AP_final_ssid_fromMain[2];
                        AP_final_ssid[3] = AP_final_ssid_fromMain[3];
                        AP_final_ssid[4] = AP_final_ssid_fromMain[4];
                        AP_final_ssid[5] = AP_final_ssid_fromMain[5];
                        AP_final_ssid[6] = AP_final_ssid_fromMain[6];
                        Log.i("Tian", "(i == 0)&&(flag_2!=0)的SSID存放完毕，分别是"+AP_final_ssid[0]+"="+AP_final_ssid[1]+"="+AP_final_ssid[2]+"="+AP_final_ssid[3]+"="+AP_final_ssid[4]+"="+AP_final_ssid[5]+"="+AP_final_ssid[6]);
                        for (int q = 0; q < AP_Scan_Num; q++)
                        {
                            ScanResult AP_info = results.get(q);//results是已经排好序存放所有AP的组
                            if (AP_final_ssid[j].equals(AP_info.SSID))

                            AP_buf[q][i] =Math.abs(AP_info.level);  //相对应的RSSI值放进去，q是代表不同的AP点
                        }
                        Log.i("Tian", "j=0,i=0的SSID存放完毕，分别是"+AP_final_ssid[0]+"="+AP_final_ssid[1]+"="+AP_final_ssid[2]+"="+AP_final_ssid[3]+"="+AP_final_ssid[4]+"="+AP_final_ssid[5]+"="+AP_final_ssid[6]);
                    }

                    if(i>0)  {                               //当扫描第二次，三次时：
                        ScanResult AP_info = results.get(0);//已经排好顺序的WIFI,第2-100次取第一个AP的信息，第2次排序时可能导致WIFI节点变化，而我RSSI还是按照第一次来的，所以得判断一下
                       // Log.i("Tian", "第"+j+"个AP第"+i+"次扫描查看ssid_buf（这次扫描到的j）："+ssid_buf[i - 1]+"以及查看等号(j地址存放的SSID)："+ssid[j]);
                        if (AP_final_ssid[0].equals(AP_info.SSID)) //如果第二次接收到的名称和第一次存好的名称一致，也就是说，信号强度没变，第一还是第一个
                        {
                            AP_buf[0][i] = Math.abs(AP_info.level);     //并且把这一个的RSSI放入数组中为后面用
                            Log.i("Tian", "第" + j + "个AP点" + "的第" + i + "次扫描结果放入了AP_buf[j][i]："+AP_buf[j][i]);
                        }

                        else                                //如果不相同，就得找一下第二个SSID到底在哪儿了，目的是相对应的RSSI，也就是ssid[i-1]！=ssid_buf[i-1]，而我的ssid[i-1]必须存放与第一次一致的
                        {
                            for (int p = 1; p <(AP_Scan_Num); p++)//思想为：我按照AP_final_ssid的标准来轮询，总可以找到对应的SSID，然后取出RSSI值，放入对应的 AP_buf[j][i]
                            {
                                ScanResult AP_info_3 = results.get(p);  //然后对照着第一个SSID,一个个去查10个AP
                                if (AP_final_ssid[0].equals(AP_info_3.SSID))
                                {

                                    AP_buf[0][i] = Math.abs(AP_info_3.level);
                                    Log.i("Tian", "第" + j + "个AP点" + "的第" + i + "次扫描结果放入了AP_buf[j][i]："+AP_buf[j][i]);
                                }
                                else {
                                    Log.i("Tian", "第"+0+"个AP第"+i+"次扫描内的第"+p+"次AP_info_3轮询，没有找到对应的ssid，AP_final_ssid[j]不等于AP_info_3.SSID即："+AP_final_ssid[j]+"不等于"+AP_info_3.SSID);
                                }
                                //Log.i("Tian", "第"+j+"个AP第"+p+"次扫描："+"没有找到乱序的SSID");
                            }
                        }
                    }
                    /*---------第三个if结束-----------------*/
                }


                        /*----------------j>0时还要考虑录入第二个AP信息时，扫描后会不会由j造成的误差，即j==1时扫描时会不会排序发生翻转------------------*/
                if(j>0) {
                    if (i == 0) {
                        ScanResult AP_info = results.get(j);//results是已经排好序存放所有AP的组，第一次取第一个AP的信息，并且把名称放入ssid[]中
                        Log.i("Tian", "打印AP_info信息，看看是否为空:   "+AP_info.SSID);
                        Log.i("Tian", "打印AP_final_ssid信息，看看是否为空:   "+AP_final_ssid[j]);
                        if(AP_final_ssid[j].equals(AP_info.SSID))
                        {

                            AP_buf[j][i] = Math.abs(AP_info.level);
                            Log.i("Tian", "第" + j + "个AP点" + "的第" + i + "次扫描结果放入了AP_buf[j][i]："+AP_buf[j][i]);
                        }

                        else {          //这列虽然也是第一个，但是j!=0，也可能在扫描错位时进不来，也就是j==1，i==0时，扫描发生错位，你怎么办
                            for (int v = 0; v < AP_Scan_Num; v++)
                            {
                                ScanResult AP_info_1 = results.get(v);  //然后对照着第一个SSID,一个个去查10个AP
                                Log.i("Tian", "打印AP_info_1信息，看看是否为空:   "+AP_info_1.SSID);
                                Log.i("Tian", "打印AP_final_ssid信息，看看是否为空:   "+AP_final_ssid[j]);
                                if (AP_final_ssid[j].equals(AP_info_1.SSID))
                                {            //这里虽然是i-1，但是只要我对齐前面一个，后面的就都对了

                                    AP_buf[j][i] = Math.abs(AP_info_1.level);
                                    Log.i("Tian", "第" + j + "个AP点" + "的第" + i + "次扫描结果放入了AP_buf[j][i]："+AP_buf[j][i]);

                                }

                                else {
                                    Log.i("Tian", "第"+j+"个AP第"+i+"次扫描内的第"+v+"次AP_info_3轮询，没有找到对应的ssid，AP_final_ssid[j]AP_info_1.SSID即："+AP_final_ssid[j]+"不等于"+AP_info_1.SSID);
                                }
                                //Log.i("Tian", "第"+j+"个AP第"+p+"次扫描："+"没有找到乱序的SSID");
                            }
                        }

                    }



                    if(i>0) {
                        ScanResult AP_info = results.get(j);//已经排好顺序的WIFI,第2-100次取第一个AP的信息，第2次排序时可能导致WIFI节点变化，而我RSSI还是按照第一次来的，所以得判断一下
                        if (AP_final_ssid[j].equals(AP_info.SSID))  //如果第二次接收到的名称和第一次存好的名称一致，也就是说，信号强度没变，第一还是第一个
                        {

                            AP_buf[j][i] = Math.abs(AP_info.level);     //并且把这一个的RSSI放入数组中为后面用

                            Log.i("Tian", "第"+j+"个AP第"+i+"次扫描："+AP_final_ssid[0]+"="+AP_final_ssid[1]+"="+AP_final_ssid[2]+"="+AP_final_ssid[3]+"="+AP_final_ssid[4]+"="+AP_final_ssid[5]+"="+AP_final_ssid[6]);
                        }
                        else
                            {                              //如果不相同，就得找一下第二个SSID到底在哪儿了，目的是相对应的RSSI，也就是ssid[i-1]！=ssid_buf[i-1]，而我的ssid[i-1]必须存放与第一次一致的
                            for (int l = 0; l < AP_Scan_Num; l++) {          //如果我第二次第一个AP的SSID和第一次第一个的AP不同，那我就要看第二次第二个AP的相不相同，然后找到对应RSSI值
                                ScanResult AP_info_2 = results.get(l);  //然后对照着第一个SSID,一个个去查10个AP
                                if (AP_final_ssid[j].equals(AP_info_2.SSID))
                                {            //这里虽然是i-1，但是只要我对齐前面一个，后面的就都对了

                                    AP_buf[j][i] = Math.abs(AP_info_2.level);

                                    Log.i("Tian", AP_final_ssid[j]+"从第"+j+"位排到了第"+l+"位去了");

                                }
                                else {
                                    Log.i("Tian", "第"+j+"个AP第"+i+"次扫描内的第"+l+"AP_info_2，没有找到对应的ssid，AP_final_ssid[j]AP_info_2.SSID即："+AP_final_ssid[j]+"不等于"+AP_info_2.SSID);
                                }

                            }
                        }
                    }
                }
                //AP_buf[j][i]=AP_info.level;

                try
                {

                    Thread.currentThread().sleep(TimeSleep);//400毫秒延时A
                    // Log.i("Tian", "第"+j+"个AP第"+i+"次延时");
                }
                catch(Exception e){}

            }
        }






                /*--------------------到这里for执行完毕-下面对AP_buf算均值方差--------------------------*/
        Mean means = new Mean();
        Variance var = new Variance();

        for(int k=0;k<AP_Num;k++){

            AP_data[k][0]=means.evaluate(AP_buf[k]);//计算均值，k代表第k个AP点，AP_buf[k]为100个值求均值，AP_data为均值
            AP_data[k][1]=var.evaluate(AP_buf[k]);
            Log.i("Tian", "第"+k+"个AP点算出含有粗大均值了:"+AP_data[k][0]);
            Log.i("Tian", "第"+k+"个AP点算出含有粗大方差了:"+AP_data[k][1]);
        }
        /*--------------------这里是用3西格玛原则剔除粗大点，运用RMSE均方根误差判断精度，需要知道残差和均值，残差得重新算，上面算的是方差--------------------------*/
        Log.i("Tian", "---------------开始算3西格玛--------------------");
        for(int m=0;m<AP_Num;m++){
            for(int n=0;n<Scan_Num;n++) {
                AP_vi[m][n] = AP_buf[m][n]-AP_data[m][0];//AP_vi[m]为10个AP点的残差
                AP_vi[m][n] = Math.sqrt(AP_vi[m][n]*AP_vi[m][n]);        //虽然下面平方可以去掉负号，但是再往下面判断时AP_vi[q][p])>(3*AP_3XG[q][0])要用到正号的AP_vi.不能用abs：int会丢数据
                AP_vi_sum[m][0] += (AP_vi[m][n])*(AP_vi[m][n]);//所有的残差平方取和，再除以n数量，最后开方


                Log.i("Tian", "第"+m+"个AP点第"+n+"次的AP_buf（每个数据）:"+AP_buf[m][n]);
                Log.i("Tian", "第"+m+"个AP点第"+n+"次的AP_data（也就是均值）:"+AP_data[m][0]);
                Log.i("Tian", "第"+m+"个AP点第"+n+"次算出含有粗大残差（均值与数据的差）:"+AP_vi[m][n]);


            }
            Log.i("Tian", "AP_vi_sum:"+AP_vi_sum[m][0]);
            AP_vi_sum_chu100[m][0] = AP_vi_sum[m][0]/Scan_Num;
            Log.i("Tian", "AP_vi_sum_chu100:"+AP_vi_sum_chu100[m][0]);
            AP_3XG[m][0] =Math.sqrt(AP_vi_sum_chu100[m][0]) ;        //AP_3XG就是我们用于判断的3西格玛
            Log.i("Tian", "第"+m+"个AP点"+"AP_3XG（也就是3西格玛）:"+AP_3XG[m][0]);
        }
                /*--------------------上面知道了残差，这里可以用残差来进行3西格玛运算，再抛弃无用点--------------------------*/
        Log.i("Tian", "---------------开始抛弃无用点--------------------");
        for(int q=0;q<AP_Num;q++){
            for(int p=0;p<Scan_Num;p++){

                if((AP_vi[q][p])>(3*AP_3XG[q][0]))
                {
                    Log.i("Tian", "第"+q+"个AP点第"+p+"次AP_vi数据（残差）："+AP_vi[q][p]);
                    Log.i("Tian", "第"+q+"个AP点第"+p+"AP_data（均值）："+AP_data[q][0]);
                   // AP_data[q][p]=0;          //把粗大值置零，然后再算均值时价格if语句判断值是否为零，为零则不运算这个值
                    AP_buf[q][p]=0;
                    if(q==0) buf2Num_AP0++;     //这里用于剔除粗大值后AP_buf2数组的重建
                    if(q==1) buf2Num_AP1++;
                    if(q==2) buf2Num_AP2++;
                    if(q==3) buf2Num_AP3++;
                    if(q==4) buf2Num_AP4++;
                    if(q==5) buf2Num_AP5++;
                    if(q==6) buf2Num_AP6++;
                  //  if(q==7) buf2Num_AP7++;
                  //  if(q==8) buf2Num_AP8++;
                   // if(q==9) buf2Num_AP9++;

                    Log.i("Tian", "第"+q+"个AP点第"+p+"次数据因为大于3倍而被抛弃置零");
                }

            }
        }
                /*--------------------下面要做的就是把剔除粗大点后的值从新算均值方差--------------------------*/
        Log.i("Tian", "------------------开始算最后的均值方差-----------------");
        int a=0;

        //Log.i("Tian", "------------------定义好了AP_buf2--------------buf2Num为："+buf2Num);
        for(int d=0;d<AP_Num;d++){

            switch (d){

                case 0:
                    double[][] AP_buf2_0 = new double[AP_Num][50-buf2Num_AP0];
                    for(int f=0;f<Scan_Num;f++){
                        Log.i("Tian", "进入最后case_0000内了，"+"第"+d+"个AP点"+AP_final_ssid[d]+"第"+f+"次数据"+AP_buf[d][f]);

                        if(AP_buf[d][f]!=0)
                        {
                            AP_buf2_0[d][a]=AP_buf[d][f];
                            Log.i("Tian","第"+d+"个AP点第"+f+"次数据"+"AP_buf2_0[d][a]: "+AP_buf2_0[d][a]);
                            Log.i("Tian","a的值:"+a);
                            Log.i("Tian","100-buf2Num_AP的值:"+(50-buf2Num_AP0));
                            a++;
                            //Log.i("Tian","a++的值:"+a);
                            //Log.i("Tian","AP_buf2[d][a++]: "+AP_buf2[d][a]);
                            if(f==49){

                                Log.i("Tian", "---------------最后提炼的if内--------------------");
                                AP_final_mean[d]=means.evaluate(AP_buf2_0[d]);//去除粗大误差后的均值与方差,RSSI的均值
                                AP_final_var[d] =var.evaluate(AP_buf2_0[d]);
                                Log.i("Tian", "第"+d+"个AP点提炼后的均值为"+AP_final_mean[d]);
                                Log.i("Tian", "第"+d+"个AP点提炼后的方差为"+AP_final_var[d]);
                            }
                        }

                        if(f==49){a=0;}     //将a清零
                    }
                    break;
                  /*-----------------------------------*/
                case 1:
                    double[][] AP_buf2_1 = new double[AP_Num][50-buf2Num_AP1];
                    for(int f=0;f<Scan_Num;f++){
                        Log.i("Tian", "进入最后case_1111内了，"+"第"+d+"个AP点"+AP_final_ssid[d]+"第"+f+"次数据"+AP_buf[d][f]);

                        if(AP_buf[d][f]!=0)
                        {
                            AP_buf2_1[d][a]=AP_buf[d][f];
                            Log.i("Tian","第"+d+"个AP点第"+f+"次数据"+"AP_buf2_1[d][a]: "+AP_buf2_1[d][a]);
                            Log.i("Tian","a的值:"+a);
                            Log.i("Tian","100-buf2Num_AP的值:----"+d+"---"+(50-buf2Num_AP1));
                            a++;

                            if(f==49){

                                Log.i("Tian", "---------------最后提炼的if内--------------------");
                                AP_final_mean[d]= means.evaluate(AP_buf2_1[d]);//去除粗大误差后的均值与方差,RSSI的均值
                                AP_final_var[d] =var.evaluate(AP_buf2_1[d]);
                                Log.i("Tian", "第"+d+"个AP点提炼后的均值为"+AP_final_mean[d]);
                                Log.i("Tian", "第"+d+"个AP点提炼后的方差为"+AP_final_var[d]);
                            }
                        }

                        if(f==49){a=0;}     //将a清零


                    }
                    break;
                  /*-----------------------------------*/
                case 2:
                    double[][] AP_buf2_2 = new double[AP_Num][50-buf2Num_AP2];
                    for(int f=0;f<Scan_Num;f++){
                        Log.i("Tian", "进入最后case_2222内了，"+"第"+d+"个AP点"+AP_final_ssid[d]+"第"+f+"次数据"+AP_buf[d][f]);

                        if(AP_buf[d][f]!=0)
                        {
                            AP_buf2_2[d][a]=AP_buf[d][f];
                            Log.i("Tian","第"+d+"个AP点第"+f+"次数据"+"AP_buf2_2[d][a]: "+AP_buf2_2[d][a]);
                            Log.i("Tian","a的值:"+a);
                            Log.i("Tian","100-buf2Num_AP的值:"+(50-buf2Num_AP2));
                            a++;

                            if(f==49){

                                Log.i("Tian", "---------------最后提炼的if内--------------------");
                                AP_final_mean[d]=means.evaluate(AP_buf2_2[d]);//去除粗大误差后的均值与方差,RSSI的均值
                                AP_final_var[d] =var.evaluate(AP_buf2_2[d]);

                                a=0;
                                Log.i("Tian", "第"+d+"个AP点提炼后的SSID为"+AP_final_ssid[d]);
                                Log.i("Tian", "第"+d+"个AP点提炼后的均值为"+AP_final_mean[d]);
                                Log.i("Tian", "第"+d+"个AP点提炼后的方差为"+AP_final_var[d]);
                            }
                        }


                        if(f==49){a=0;}     //将a清零
                    }
                    break;
                  /*-----------------------------------*/
                case 3:
                    double[][] AP_buf2_3 = new double[AP_Num][50-buf2Num_AP3];
                    for(int f=0;f<Scan_Num;f++){
                        Log.i("Tian", "进入最后case_3333内了，"+"第"+d+"个AP点"+AP_final_ssid[d]+"第"+f+"次数据"+AP_buf[d][f]);

                        if(AP_buf[d][f]!=0)
                        {
                            AP_buf2_3[d][a]=AP_buf[d][f];
                            Log.i("Tian","第"+d+"个AP点第"+f+"次数据"+"AP_buf2_2[d][a]: "+AP_buf2_3[d][a]);
                            Log.i("Tian","a的值:"+a);
                            Log.i("Tian","100-buf2Num_AP的值:"+(50-buf2Num_AP2));
                            a++;

                            if(f==49){

                                Log.i("Tian", "---------------最后提炼的if内--------------------");
                                AP_final_mean[d]=means.evaluate(AP_buf2_3[d]);//去除粗大误差后的均值与方差,RSSI的均值
                                AP_final_var[d] =var.evaluate(AP_buf2_3[d]);

                                a=0;
                                /*-------------要对里面所有的值进行初始化，不然数组容易出错：AP_buf2-------------*/


                                Log.i("Tian", "第"+d+"个AP点提炼后的SSID为"+AP_final_ssid[d]);
                                Log.i("Tian", "第"+d+"个AP点提炼后的均值为"+AP_final_mean[d]);
                                Log.i("Tian", "第"+d+"个AP点提炼后的方差为"+AP_final_var[d]);
                            }
                        }


                        if(f==49){a=0;}     //将a清零
                    }
                    break;
                  /*-----------------------------------*/
                case 4:
                    double[][] AP_buf2_4 = new double[AP_Num][50-buf2Num_AP4];
                    for(int f=0;f<Scan_Num;f++){
                        Log.i("Tian", "进入最后case_4444内了，"+"第"+d+"个AP点"+AP_final_ssid[d]+"第"+f+"次数据"+AP_buf[d][f]);

                        if(AP_buf[d][f]!=0)
                        {
                            AP_buf2_4[d][a]=AP_buf[d][f];
                            Log.i("Tian","第"+d+"个AP点第"+f+"次数据"+"AP_buf2_2[d][a]: "+AP_buf2_4[d][a]);
                            Log.i("Tian","a的值:"+a);
                            Log.i("Tian","100-buf2Num_AP的值:"+(100-buf2Num_AP2));
                            a++;

                            if(f==49){

                                Log.i("Tian", "---------------最后提炼的if内--------------------");
                                AP_final_mean[d]=means.evaluate(AP_buf2_4[d]);//去除粗大误差后的均值与方差,RSSI的均值
                                AP_final_var[d] =var.evaluate(AP_buf2_4[d]);

                                a=0;
                                Log.i("Tian", "第"+d+"个AP点提炼后的SSID为"+AP_final_ssid[d]);
                                Log.i("Tian", "第"+d+"个AP点提炼后的均值为"+AP_final_mean[d]);
                                Log.i("Tian", "第"+d+"个AP点提炼后的方差为"+AP_final_var[d]);
                            }
                        }


                        if(f==49){a=0;}     //将a清零
                    }
                    break;
                  /*-----------------------------------*/
                case 5:
                    double[][] AP_buf2_5 = new double[AP_Num][50-buf2Num_AP5];
                    for(int f=0;f<Scan_Num;f++){
                        Log.i("Tian", "进入最后case_5555内了，"+"第"+d+"个AP点"+AP_final_ssid[d]+"第"+f+"次数据"+AP_buf[d][f]);

                        if(AP_buf[d][f]!=0)
                        {
                            AP_buf2_5[d][a]=AP_buf[d][f];
                            Log.i("Tian","第"+d+"个AP点第"+f+"次数据"+"AP_buf2_2[d][a]: "+AP_buf2_5[d][a]);
                            Log.i("Tian","a的值:"+a);
                            Log.i("Tian","100-buf2Num_AP的值:"+(100-buf2Num_AP2));
                            a++;

                            if(f==49){

                                Log.i("Tian", "---------------最后提炼的if内--------------------");
                                AP_final_mean[d]=means.evaluate(AP_buf2_5[d]);//去除粗大误差后的均值与方差,RSSI的均值
                                AP_final_var[d] =var.evaluate(AP_buf2_5[d]);

                                a=0;
                                Log.i("Tian", "第"+d+"个AP点提炼后的SSID为"+AP_final_ssid[d]);
                                Log.i("Tian", "第"+d+"个AP点提炼后的均值为"+AP_final_mean[d]);
                                Log.i("Tian", "第"+d+"个AP点提炼后的方差为"+AP_final_var[d]);
                            }
                        }


                        if(f==49){a=0;}     //将a清零
                    }
                    break;
                  /*-----------------------------------*/
                case 6:
                    double[][] AP_buf2_6 = new double[AP_Num][50-buf2Num_AP6];
                    for(int f=0;f<Scan_Num;f++){
                        Log.i("Tian", "进入最后case_6666内了，"+"第"+d+"个AP点"+AP_final_ssid[d]+"第"+f+"次数据"+AP_buf[d][f]);

                        if(AP_buf[d][f]!=0)
                        {
                            AP_buf2_6[d][a]=AP_buf[d][f];
                            Log.i("Tian","第"+d+"个AP点第"+f+"次数据"+"AP_buf2_2[d][a]: "+AP_buf2_6[d][a]);
                            Log.i("Tian","a的值:"+a);
                            Log.i("Tian","100-buf2Num_AP的值:"+(50-buf2Num_AP2));
                            a++;

                            if(f==49){

                                Log.i("Tian", "---------------最后提炼的if内--------------------");
                                AP_final_mean[d]=means.evaluate(AP_buf2_6[d]);//去除粗大误差后的均值与方差,RSSI的均值
                                AP_final_var[d] =var.evaluate(AP_buf2_6[d]);

                                a=0;
                                Log.i("Tian", "第"+d+"个AP点提炼后的SSID为"+AP_final_ssid[d]);
                                Log.i("Tian", "第"+d+"个AP点提炼后的均值为"+AP_final_mean[d]);
                                Log.i("Tian", "第"+d+"个AP点提炼后的方差为"+AP_final_var[d]);
                            }
                        }
                        else {
                            Log.i("Tian","第"+d+"个AP点第"+f+"次数据为"+AP_buf[d][f]+"!!!!!!");
                        }


                        if(f==49){a=0;}     //将a清零
                    }
                    break;



            }

            Log.i("Tian", " switch   d的值为："+d);

            if(d==(AP_Num-1)){       //因为最后for循环内还有一次d++，所以此时d==7
                flag_1=1;
                flag_2=1;
               // offline.setText("ClacuOK");
                Log.i("Tian", " if(d==6)    flag_1="+flag_1);
                Log.i("Tian", " 并且唤醒主线程");
                try {

                        Thread.currentThread().sleep(30);
                        Log.i("Tian", " 子线程休息50毫秒");
                } catch (Exception e) {
                }
            }
        }

    }

    /*---------------------run外面了-----------------------*/

    public double[] getAP_final_mean() {
        return AP_final_mean;
    }

    public double[] getAP_final_var() {
        return AP_final_var;
    }

    public String[] getAP_final_ssid() {
        return AP_final_ssid;
    }

    public int getFlag_1() {
        return flag_1;
    }

    public int getFlag_2() {
        return flag_2;
    }
}
