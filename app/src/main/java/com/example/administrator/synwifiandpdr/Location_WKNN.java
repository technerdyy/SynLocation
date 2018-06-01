package com.example.administrator.synwifiandpdr;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.apache.commons.math3.stat.descriptive.summary.Sum;
import org.apache.commons.math3.stat.descriptive.summary.SumOfSquares;
import org.litepal.crud.DataSupport;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/10/1.
 */

/*---------------------------Location---------------------------------------*/
        /*在Location里面，我们首先要创建一个定位的线程，里面要进行1：欧几里得加权距离公式算出测试点和参考点距离d（测试点从Scan_online获得，参考点在数据库内）。
        2：得到距离d后，选出几个d最小的数据，放入数组。
        3：进行加权取均值，最终得到定位点。
        4：在图片上进行描点*/

        /*-------------------Coding Explain--------------------------
        * 在线的数据通过构造方法传进来，离线的参考点数据通过getData类获取，先全部存起来
        * 在线的数据要定义在run内，运行完run就释放内存，离线的数据可以放在外面，因为离线数据是不会变的*/

public class Location_WKNN extends AppCompatActivity implements Runnable {


    /*-------参考点的数据（离线数据库内,可以一列一列的取值）---*/
    private int RefrenceNum = 59;//参考点数量
    private int ApNum = 7;//AP数量
    private int WKnnNum = 4;//最后WKNN定点的数量
    private long ThreadSleepTime =2000; //SleepTime

    private List<my_knn_db> offline_ssid0_list;
    private List<my_knn_db> offline_ssid1_list;
    private List<my_knn_db> offline_ssid2_list;
    private List<my_knn_db> offline_ssid3_list;
    private List<my_knn_db> offline_ssid4_list;
    private List<my_knn_db> offline_ssid5_list;
    private List<my_knn_db> offline_ssid6_list;


    private List<my_knn_db> offline_mean0_list;
    private List<my_knn_db> offline_mean1_list;
    private List<my_knn_db> offline_mean2_list;
    private List<my_knn_db> offline_mean3_list;
    private List<my_knn_db> offline_mean4_list;
    private List<my_knn_db> offline_mean5_list;
    private List<my_knn_db> offline_mean6_list;

    private List<my_knn_db> offline_var0_list;
    private List<my_knn_db> offline_var1_list;
    private List<my_knn_db> offline_var2_list;
    private List<my_knn_db> offline_var3_list;
    private List<my_knn_db> offline_var4_list;
    private List<my_knn_db> offline_var5_list;
    private List<my_knn_db> offline_var6_list;

    private List<my_knn_db> offline_x_list;
    private List<my_knn_db> offline_y_list;

    private double[] off_mean0= new double[RefrenceNum];
    private double[] off_mean1= new double[RefrenceNum];
    private double[] off_mean2= new double[RefrenceNum];
    private double[] off_mean3= new double[RefrenceNum];
    private double[] off_mean4= new double[RefrenceNum];
    private double[] off_mean5= new double[RefrenceNum];
    private double[] off_mean6= new double[RefrenceNum];

    private double[] off_var0= new double[RefrenceNum];
    private double[] off_var1= new double[RefrenceNum];
    private double[] off_var2= new double[RefrenceNum];
    private double[] off_var3= new double[RefrenceNum];
    private double[] off_var4= new double[RefrenceNum];
    private double[] off_var5= new double[RefrenceNum];
    private double[] off_var6= new double[RefrenceNum];

    private String[] off_ssid0= new String[RefrenceNum];
    private String[] off_ssid1= new String[RefrenceNum];
    private String[] off_ssid2= new String[RefrenceNum];
    private String[] off_ssid3= new String[RefrenceNum];
    private String[] off_ssid4= new String[RefrenceNum];
    private String[] off_ssid5= new String[RefrenceNum];
    private String[] off_ssid6= new String[RefrenceNum];

    private double[] off_x0= new double[RefrenceNum];
    private double[] off_y0= new double[RefrenceNum];

    private double[][] mean_all = new double[ApNum][RefrenceNum];
    private double[][] var_all = new double[ApNum][RefrenceNum];
    private String[][] ssid_all = new String[ApNum][RefrenceNum];

    /*-------AP点数据（在线Scan传过来的）-------------*/


    double[] AP_Online_mean_location = new double[ApNum];//在线
    double[] AP_Online_var_location = new double[ApNum];
    String[] AP_Online_ssid_location = new String[ApNum];

    /*----------功能定义--------------------------------*/
    private int flag_1;


    /*----------加权定义--------------------------------*/
    private double[] w_1= new double[ApNum];//第一次权
    private double[] d_match = new double[RefrenceNum]; //d加权,一共会有RefrenceNum个权点，因为你指纹点有RefrenceNum个
    private double[] d_match_array = new double[RefrenceNum];//排序后的d_weight
    private double[] v_weight = new double[RefrenceNum]; //v加权，1/d  的加权

    /*---------方法和构造方法,把在线数据传过来----------------------------*/
    public int getFlag_1() {
        return flag_1;
    }

    public Location_WKNN(double[] AP_Online_mean_location, double[] AP_Online_var_location, String[] AP_Online_ssid_location,long ThreadSleepTime) {
        this.AP_Online_ssid_location = AP_Online_ssid_location;
        this.AP_Online_mean_location = AP_Online_mean_location;
        this.AP_Online_var_location = AP_Online_var_location;
        this.ThreadSleepTime =ThreadSleepTime;
    }
    /*---------最终定位的X  Y值----------------------------*/
    private double[] X_final_location_array =new double[RefrenceNum];
    private double[] Y_final_location_array =new double[RefrenceNum];
    private double X_final_location;
    private double Y_final_location;
    public double getX_final_location(){return X_final_location;}
    public double getY_final_location(){return Y_final_location;}

    @Override
    public void run() {
        Log.i("Tian", "Enter WKNN run");

        /*--------------从数据库中取出一列列数据，放入一个数组内----------------------------------*/
        /*----String[] array = (String[])list.toArray(new String[size]); -----数组和LIST的转换-*/
        Log.i("Tian", "----------------------Data From DataBase------------------");
        Log.i("Tian", "----------------------Mean Data From DataBase-------------");

        //TEST



        //0
        offline_mean0_list = DataSupport.select("mean_0").find(my_knn_db.class);
        for(int i=0;i<RefrenceNum;i++){
            my_knn_db d1 =  offline_mean0_list.get(i);//把mean_0这一列数据取出来放在 off_mean0[i]内，i内存 for循环结束就被收回

            off_mean0[i]  =  d1.getMean(0);            //为什么是0？因为d1也是一个标准只有斜对角线有值得矩阵，所以会是0,1，2,3,4====，并且其他位置都为0
           //  Log.i("Tian", " off_mean0[0]==="+ off_mean0[i]);//谁叫你把d1设置成了一个LIST，所以当你把0换成i时，便会出现很多输出0
             mean_all[0][i] = d1.getMean(0);
            // Log.i("Tian", " mean_all[0]["+i+"]==="+ mean_all[0][i]);// mean_all[0][i]就时第一列数据
        }
        //1
        offline_mean1_list = DataSupport.select("mean_1").find(my_knn_db.class);
        for(int i=0;i<RefrenceNum;i++){
            my_knn_db d1 =  offline_mean1_list.get(i);//把mean_0这一列数据取出来放在 off_mean0[i]内，i内存 for循环结束就被收回
            off_mean1[i]  =  d1.getMean(1);
          //  Log.i("Tian", " off_mean1[1]==="+ off_mean1[i]);
            mean_all[1][i] = d1.getMean(1);
          //  Log.i("Tian", " mean_all[1]["+i+"]==="+ mean_all[1][i]);
        }
        //2
        offline_mean2_list = DataSupport.select("mean_2").find(my_knn_db.class);
        for(int i=0;i<RefrenceNum;i++){
            my_knn_db d1 =  offline_mean2_list.get(i);//把mean_0这一列数据取出来放在 off_mean0[i]内，i内存 for循环结束就被收回
            off_mean2[i]  =  d1.getMean(2);
           // Log.i("Tian", " off_mean2[2]==="+ off_mean2[i]);
            mean_all[2][i] = d1.getMean(2);
          //  Log.i("Tian", " mean_all[2]["+i+"]==="+ mean_all[2][i]);
        }

        //3
        offline_mean3_list = DataSupport.select("mean_3").find(my_knn_db.class);
        for(int i=0;i<RefrenceNum;i++){
            my_knn_db d1 =  offline_mean3_list.get(i);//把mean_0这一列数据取出来放在 off_mean0[i]内，i内存 for循环结束就被收回
            off_mean3[i]  =  d1.getMean(3);
           // Log.i("Tian", " off_mean3[3]==="+ off_mean3[i]);
            mean_all[3][i] = d1.getMean(3);
           // Log.i("Tian", " mean_all[3]["+i+"]==="+ mean_all[3][i]);// mean_all[0][i]就时第一列数据
        }
        //4
        offline_mean4_list = DataSupport.select("mean_4").find(my_knn_db.class);
        for(int i=0;i<RefrenceNum;i++){
            my_knn_db d1 =  offline_mean4_list.get(i);//把mean_0这一列数据取出来放在 off_mean0[i]内，i内存 for循环结束就被收回
            off_mean4[i]  =  d1.getMean(4);
           // Log.i("Tian", " off_mean4[4]==="+ off_mean4[i]);
            mean_all[4][i] = d1.getMean(4);
          //  Log.i("Tian", " mean_all[4]["+i+"]==="+ mean_all[4][i]);// mean_all[0][i]就时第一列数据
        }
        //5
        offline_mean5_list = DataSupport.select("mean_5").find(my_knn_db.class);
        for(int i=0;i<RefrenceNum;i++){
            my_knn_db d1 =  offline_mean5_list.get(i);//把mean_0这一列数据取出来放在 off_mean0[i]内，i内存 for循环结束就被收回
            off_mean5[i]  =  d1.getMean(5);
          //  Log.i("Tian", " off_mean5[5]==="+ off_mean5[i]);
            mean_all[5][i] = d1.getMean(5);
           // Log.i("Tian", " mean_all[5]["+i+"]==="+ mean_all[5][i]);// mean_all[0][i]就时第一列数据
        }
        //6
        offline_mean6_list = DataSupport.select("mean_6").find(my_knn_db.class);
        for(int i=0;i<RefrenceNum;i++){
            my_knn_db d1 =  offline_mean6_list.get(i);//把mean_0这一列数据取出来放在 off_mean0[i]内，i内存 for循环结束就被收回
            off_mean6[i]  =  d1.getMean(6);
         //   Log.i("Tian", " off_mean6[6]==="+ off_mean6[i]);
            mean_all[6][i] = d1.getMean(6);
          //  Log.i("Tian", " mean_all[6]["+i+"]==="+ mean_all[6][i]);// mean_all[0][i]就时第一列数据
        }
        Log.i("Tian", "--------------Mean Data From DataBase is OK------------------");
        /*--------------------------------------------------------------------------*/
        Log.i("Tian", "----------------------Var Data From DataBase-------------");
        //0
        offline_var0_list = DataSupport.select("var_0").find(my_knn_db.class);
        for(int i=0;i<RefrenceNum;i++){
            my_knn_db d1 =  offline_var0_list.get(i);//把var_0这一列数据取出来放在 off_mean0[i]内，i内存 for循环结束就被收回
            off_var0[i]  =  d1.getVar(0);
          //  Log.i("Tian", " off_var0[i]==="+ off_var0[i]);
            var_all[0][i] = d1.getVar(0);
        }
        //1
        offline_var1_list = DataSupport.select("var_1").find(my_knn_db.class);
        for(int i=0;i<RefrenceNum;i++){
            my_knn_db d1 =  offline_var1_list.get(i);//把var_0这一列数据取出来放在 off_mean0[i]内，i内存 for循环结束就被收回
            off_var1[i]  =  d1.getVar(1);
          //  Log.i("Tian", " off_var1[i]==="+ off_var1[i]);
            var_all[1][i] = d1.getVar(1);
        }
        //2
        offline_var2_list = DataSupport.select("var_2").find(my_knn_db.class);
        for(int i=0;i<RefrenceNum;i++){
            my_knn_db d1 =  offline_var2_list.get(i);//把var_0这一列数据取出来放在 off_mean0[i]内，i内存 for循环结束就被收回
            off_var2[i]  =  d1.getVar(2);
          //  Log.i("Tian", " off_var2[i]==="+ off_var2[i]);
            var_all[2][i] = d1.getVar(2);
        }
        //3
        offline_var3_list = DataSupport.select("var_3").find(my_knn_db.class);
        for(int i=0;i<RefrenceNum;i++){
            my_knn_db d1 =  offline_var3_list.get(i);//把var_0这一列数据取出来放在 off_mean0[i]内，i内存 for循环结束就被收回
            off_var3[i]  =  d1.getVar(3);
          //  Log.i("Tian", " off_var3[i]==="+ off_var3[i]);
            var_all[3][i] = d1.getVar(3);
        }
        //4
        offline_var4_list = DataSupport.select("var_4").find(my_knn_db.class);
        for(int i=0;i<RefrenceNum;i++){
            my_knn_db d1 =  offline_var4_list.get(i);//把var_0这一列数据取出来放在 off_mean0[i]内，i内存 for循环结束就被收回
            off_var4[i]  =  d1.getVar(4);
           // Log.i("Tian", " off_var4[i]==="+ off_var4[i]);
            var_all[4][i] = d1.getVar(4);
        }
        //5
        offline_var5_list = DataSupport.select("var_5").find(my_knn_db.class);
        for(int i=0;i<RefrenceNum;i++){
            my_knn_db d1 =  offline_var5_list.get(i);//把var_0这一列数据取出来放在 off_mean0[i]内，i内存 for循环结束就被收回
            off_var5[i]  =  d1.getVar(5);
          //  Log.i("Tian", " off_var5[i]==="+ off_var5[i]);
            var_all[5][i] = d1.getVar(5);
        }
        //6
        offline_var6_list = DataSupport.select("var_6").find(my_knn_db.class);
        for(int i=0;i<RefrenceNum;i++){
            my_knn_db d1 =  offline_var6_list.get(i);//把var_0这一列数据取出来放在 off_mean0[i]内，i内存 for循环结束就被收回
            off_var6[i]  =  d1.getVar(6);
           // Log.i("Tian", " off_var6[i]==="+ off_var6[i]);
            var_all[6][i] =d1.getVar(6);
        }
        Log.i("Tian", "--------------var Data From DataBase is OK------------------");
        /*------------------------------------------------------------------------*/

        Log.i("Tian", "----------------------SSid Data From DataBase-------------");
        //0
        offline_ssid0_list = DataSupport.select("ssid_0").find(my_knn_db.class);
        for(int i=0;i<RefrenceNum;i++){
            my_knn_db d1 =  offline_ssid0_list.get(i);//把ssid_0这一列数据取出来放在 off_mean0[i]内，i内存 for循环结束就被收回
            off_ssid0[i]  =  d1.getSsid(0);
           // Log.i("Tian", " off_ssid0[i]==="+ off_ssid0[i]);
            ssid_all[0][i] = d1.getSsid(0);
        }
        //1
        offline_ssid1_list = DataSupport.select("ssid_1").find(my_knn_db.class);
        for(int i=0;i<RefrenceNum;i++){
            my_knn_db d1 =  offline_ssid1_list.get(i);//把ssid_0这一列数据取出来放在 off_mean0[i]内，i内存 for循环结束就被收回
            off_ssid1[i]  =  d1.getSsid(1);
          //  Log.i("Tian", " off_ssid1[i]==="+ off_ssid1[i]);
            ssid_all[1][i] = d1.getSsid(1);
        }
        //2
        offline_ssid2_list = DataSupport.select("ssid_2").find(my_knn_db.class);
        for(int i=0;i<RefrenceNum;i++){
            my_knn_db d1 =  offline_ssid2_list.get(i);//把ssid_0这一列数据取出来放在 off_mean0[i]内，i内存 for循环结束就被收回
            off_ssid0[i]  =  d1.getSsid(2);
          //  Log.i("Tian", " off_ssid2[i]==="+ off_ssid2[i]);
            ssid_all[2][i] = d1.getSsid(2);
        }
        //3
        offline_ssid3_list = DataSupport.select("ssid_3").find(my_knn_db.class);
        for(int i=0;i<RefrenceNum;i++){
            my_knn_db d1 =  offline_ssid3_list.get(i);//把ssid_0这一列数据取出来放在 off_mean0[i]内，i内存 for循环结束就被收回
            off_ssid3[i]  =  d1.getSsid(3);
          //  Log.i("Tian", " off_ssid3[i]==="+ off_ssid3[i]);
            ssid_all[3][i] = d1.getSsid(3);
        }
        //4
        offline_ssid4_list = DataSupport.select("ssid_4").find(my_knn_db.class);
        for(int i=0;i<RefrenceNum;i++){
            my_knn_db d1 =  offline_ssid4_list.get(i);//把ssid_0这一列数据取出来放在 off_mean0[i]内，i内存 for循环结束就被收回
            off_ssid4[i]  =  d1.getSsid(4);
          //  Log.i("Tian", " off_ssid4[i]==="+ off_ssid4[i]);
            ssid_all[4][i] = d1.getSsid(4);
        }
        //5
        offline_ssid5_list = DataSupport.select("ssid_5").find(my_knn_db.class);
        for(int i=0;i<RefrenceNum;i++){
            my_knn_db d1 =  offline_ssid5_list.get(i);//把ssid_0这一列数据取出来放在 off_mean0[i]内，i内存 for循环结束就被收回
            off_ssid5[i]  =  d1.getSsid(5);
          //  Log.i("Tian", " off_ssid5[i]==="+ off_ssid5[i]);
            ssid_all[5][i] = d1.getSsid(5);
        }
        //6
        offline_ssid6_list = DataSupport.select("ssid_6").find(my_knn_db.class);
        for(int i=0;i<RefrenceNum;i++){
            my_knn_db d1 =  offline_ssid6_list.get(i);//把ssid_0这一列数据取出来放在 off_mean0[i]内，i内存 for循环结束就被收回
            off_ssid6[i]  =  d1.getSsid(6);
          //  Log.i("Tian", " off_ssid6[i]==="+ off_ssid6[i]);
            ssid_all[6][i] = d1.getSsid(6);
        }
        Log.i("Tian", "--------------Ssid Data From DataBase is OK------------------");
        /*--------------------------------------------------------------------------*/
        Log.i("Tian", "--------------X  Y Data From DataBase ------------------");
        //X
        offline_x_list = DataSupport.select("x_0").find(my_knn_db.class);
        for(int i=0;i<RefrenceNum;i++){
            my_knn_db d1 =  offline_x_list.get(i);//把ssid_0这一列数据取出来放在 off_mean0[i]内，i内存 for循环结束就被收回
            off_x0[i]  =  d1.getX_0();
         //   Log.i("Tian", " off_x0[i]==="+ off_x0[i]);
        }
        //Y
        offline_y_list = DataSupport.select("y_0").find(my_knn_db.class);
        for(int i=0;i<RefrenceNum;i++){
            my_knn_db d1 =  offline_y_list.get(i);//把ssid_0这一列数据取出来放在 off_mean0[i]内，i内存 for循环结束就被收回
            off_y0[i]  = d1.getY_0();
          //  Log.i("Tian", " off_y0[i]==="+ off_y0[i]);
        }
        Log.i("Tian", "-------------- Data From DataBase are Finished ------------------");
        /*----------------------------数据重组完毕----------------------------------------------*/


        /*----------------------------开始进行对每个AP点的加权，即第一次加权----------------------------------------------*/
        Log.i("Tian", "-------------- First  Weighting ------------------");
        /*思路：把在线的数据拿过来，然后求和，最后单个除以综合。注意点：1：权要定义为全局，因为后面还要用  2：放到数组里面去
        * 3:第一次加权定义为w_1[ApNum],因为是对AP加权，所以这里是ApNum  4:在线数据为：AP_Online_mean_location  AP_Online_var_location
        * AP_Online_ssid_location  */
        /*---2017.11.4更新  这里的AP_Online_mean_location都是已经取了绝对值的，所以我要再取平方权，之前一直是一次权---
        * 另外把欧式距离D，也就是理解为匹配度更好理解
        * */

        /*----对所有的在线过来的RSSI取倒数---------*/
        double[] AP_Online_mean2 = new double[ApNum];
        for(int i=0;i<ApNum;i++) {

            AP_Online_mean2[i] = 1/AP_Online_mean_location[i];
            AP_Online_mean2[i] =  (AP_Online_mean2[i])*(AP_Online_mean2[i]);
        }
        /*---AP_Online_mean_location为进来时的倒数的平方了，因为我要加平方权--------*/
        for(int i=0;i<ApNum;i++) {
            Sum cal_sum1 = new Sum();
            double sum = cal_sum1.evaluate(AP_Online_mean2);
         //   Log.i("Tian", "AP_Online_mean_location "+ AP_Online_mean2[i]);

            w_1[i] =AP_Online_mean2[i]/sum;
         //   Log.i("Tian", "First  Weighting Running "+ w_1[i]);
        }

         /*----------------------------加权以后再算基于RSSI的距离d，这个d也可以看做是一个权值，一个基于RSSI大小的，给物理坐标加权的权值----------------------------------------------*/
        Log.i("Tian", "--------------D  Weighting ------------------");
        /*思路：根据公式来  注意点：1：d_weight[ApNum]数组来存放数组，d_weight[0]代表我在线RSS和第一个参考指纹点的基于RSSI的欧氏距离，同理d_weight[1]代表
        * 第二个参考指纹点的基于RSSI的欧氏距离*/

        double[] d_value = new double[RefrenceNum];         //求RSS-rssi的值，也就是在线值与第j个指纹点的差值，下面再去求平方和
        double[] d_value_2 = new double[RefrenceNum];         //d_value平方后的值
        double[] d_value_3 = new double[RefrenceNum];

        for(int i=0;i<RefrenceNum;i++) {
            for (int j = 0; j < ApNum; j++)            //内for循环，用于算第j个，单个参考指纹点的匹配
            {
                //SumOfSquares  sum_2 = new SumOfSquares();   //求平方和,没用了，因为我要加权
                d_value[j] = AP_Online_mean_location[j] - mean_all[j][i];//mean_all[0][i]代表第一列数据，也就是mean_0,这里指的是id==1的所有mean
                                                                        //RSSI(j)-rss(ij)  前者为在线第J个AP点的信号强度  后者为第i个指纹点对第j个AP的指纹信号
                d_value_2[j] = d_value[j] * d_value[j];//这里平方的原因是后面要相加进行开根号
                d_value_3[j] = w_1[j] * d_value_2[j]; //d_weight[j]为乘了w_1权重之后的值
            }

            Sum s1 = new Sum();
            double d1 = s1.evaluate(d_value_3);//d1为所有d_weight[j]的和，现在只差乘以ApNum再开根号了
            d_match[i] = Math.sqrt(ApNum * d1);//待测点信号与每一个指纹点的欧式距离存放在d_weight这个数组内
          //  Log.i("Tian", "--------------D  Weighting：    "+d_match[i]);

        }
         /*----------------------------D  Weighting 排序----------------------------------------------*/
        Map<Integer,Double> map1 = new HashMap<Integer,Double>();
        List<Map.Entry<Integer, Double>> list1 = new ArrayList<>();

        for(int i=0;i<RefrenceNum;i++) {//把d_weight这个数组进行排序，也就是对待测点到每一个指纹点的距离进行排序，并记录一个ID，最小的为0，依次

           map1.put(i,d_match[i]);          //为什么i+1?因为后面数据库取值时  如果在AP0附近，会有一个ID=0,导致数据库出错，因为数据库从1开始
        //    Log.i("Tian", " Map<Integer,Double> map1：    " + map1.get(i));
        }
        for(Map.Entry<Integer, Double> entry : map1.entrySet()){
            list1.add(entry); //将map中的元素放入list中
        }

        /*----------------------------D  Weighting 排序  Finished---------------------------------------------*/
        Collections.sort(list1, new Comparator<Map.Entry<Integer, Double>>() {
            @Override
            public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {

                double q1=o1.getValue();
                double q2=o2.getValue();
                double p=q2-q1;
                if(p>0){            //升序
                    return -1;
                }
                else if(p==0){
                    return 0;
                }
                else
                    return 1;
            }
        });
        for(Map.Entry<Integer, Double> set:list1){          //对
           // System.out.println(set.getKey() +" "+set.getValue());
           // Log.i("Tian", "Collections.sort(list1, new Comparator:     "+set.getKey() +" "+set.getValue());
        }
        Log.i("Tian", "--------------D  Weighting  Finished and Value ");
    /*----------------------------D  Weighting  Finished----------------------------------------------*/


    /*----------------------------D  Weighting  into  Array to Calculate----------------------------------------------*/

        int[] idNum = new int[WKnnNum];
        double[] d_MaxMatch_array = new double[WKnnNum];
        double[] x_point = new double[WKnnNum];
        double[] y_point = new double[WKnnNum];


        for(int t=0;t<WKnnNum;t++){
            Map.Entry<Integer, Double> set =  list1.get(t);
            idNum[t] = set.getKey();
            d_MaxMatch_array[t] = set.getValue();
        }
        my_knn_db mk1 = DataSupport.find(my_knn_db.class,idNum[0]);//从数据库中找ID对应的参考点的真实物理坐标（X,Y）
        x_point[0] = mk1.getX_0();
        y_point[0] = mk1.getY_0();

        my_knn_db mk2 = DataSupport.find(my_knn_db.class,idNum[1]);
        x_point[1] = mk2.getX_0();
        y_point[1] = mk2.getY_0();

        my_knn_db mk3 = DataSupport.find(my_knn_db.class,idNum[2]);
        x_point[2] = mk3.getX_0();
        y_point[2] = mk3.getY_0();

        my_knn_db mk4 = DataSupport.find(my_knn_db.class,idNum[3]);
        x_point[3] = mk4.getX_0();
        y_point[3] = mk4.getY_0();
        for(int l=0;l<WKnnNum;l++){
          //  Log.i("Tian", "X值为"  +x_point[l] +"并且Y值为：   "+y_point[l]);
        }

        Log.i("Tian", "--------------D  Weighting  into  Array to Calculate-  Finished ");


         /*----------------------------V  Weighting----------------------------------------------*/
        /*思路：因为前面算好了d_weight[RefrenceNum],这里是对所有的d_weight再进行第一次对AP加权的过程一样，求和加权，d越小的距离越小，更可靠
        * v_weight[0]权值是指纹点0的权值，v_weight[1]权值是指纹点1的权值*/
        Log.i("Tian", "--------------V  Weighting adn Location Begin -----------------");
        double[] d_value_4 = new double[WKnnNum];
        for(int i=0;i<WKnnNum;i++) {
            d_value_4[i] = 1/d_MaxMatch_array[i];
            d_value_4[i] = ( d_value_4[i])*( d_value_4[i]);
        }

        Sum cal_sum1 = new Sum();
        double sum_1 = cal_sum1.evaluate(d_value_4);  //分母  所有1/d 的和

        for(int o=0;o<WKnnNum;o++)
        {
            v_weight[o] =  d_value_4[o]/sum_1;  //v权，用于最后物理坐标定位
        }

        for(int u=0;u<WKnnNum;u++){

            X_final_location_array[u]=v_weight[u]*x_point[u];
            Y_final_location_array[u]=v_weight[u]*y_point[u];
        }
        Sum sum_2 = new Sum();
        X_final_location = sum_2.evaluate(X_final_location_array);
        Y_final_location = sum_2.evaluate(Y_final_location_array);
        Log.i("Tian", "X_final_location---Y_final_location:"+X_final_location+"===="+Y_final_location);

        Log.i("Tian", "--------------V  Weighting adn Location Finished -----------------");
 /*----------------------------V  Weighting  Finished----------------------------------------------*/

        /*----------------------------退出条件----------------------------------------------*/
        if (true) {       //结束条件
                                      //执行完RUN后，内存释放
            // offline.setText("ClacuOK");
            Log.i("Tian", "Location_WKNN运行完毕，可以让while1过去");
            Log.i("Tian", " 并且唤醒主线程");
            try {

                Thread.currentThread().sleep(500);
                flag_1 = 1;
                Log.i("Tian", " 子线程休息50毫秒");
            } catch (Exception e) {
            }

        }
    }
}
