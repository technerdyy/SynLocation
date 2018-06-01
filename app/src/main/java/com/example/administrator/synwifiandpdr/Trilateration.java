package com.example.administrator.synwifiandpdr;

import android.util.Log;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/10/24.
 */

/*--------基于最小二乘（LSM）  三边定位--------
        by Technerdy  10.25----------*/
/*--------------目标实现--------------------
  1 因为这里和指纹库无关了，只需要在线RSSI数据便OK，所以可以不用涉及到数据库，但是要输入7个D值，重写输入
  2 因为7个方程式算最小二乘计算量感觉还是挺大的，所以最好也重开线程计算，while（.getflag）等待------------------------*/
/*---------算法流程-------------------------
*           1：根据7个AP的距离D可以有7个圆的方程式，因为距离是一个圆
*           2：因为这7个方程式都是非线性的，所以需要线性化，用泰勒公式近似，展开到一阶项，这样不会损失精度
*           3：有了7个线性方程，再用最小二乘进行运算，得到最佳估计值（x_point，y_point）,再返回给main*/


public class Trilateration implements Runnable{

    /*-------功能值-------------*/
    private double fen_mu;
    private int NumOfAPs=7;         //AP点个数
    private int NumOfEuqtion=5;     //方程式个数，我这里只取最近的三个AP点
    private int Flag;
    private int NumOfChudazhi=3;    //用于定位误差太大时，进行加权KNN的数量
    private long Time;

    /*------d通过RSSI信号传播公式给出，这里减少耦合，我只用d，不用RSSI-------------*/
    private int D_ID[] = new int[NumOfAPs]; //第一维我装入排序用的ID序号，
    private double D_value[] = new double[NumOfAPs];//第二维我装入ID序号对应的D值，ID序号其实就是AP点的序号
    private double D_paixu[] = new double[NumOfAPs];//用于排序的数组,传进来后通过getPicLen转换成D，再给Location里面用
    /*----------7个AP点的物理坐标----------------------*/
    private double[] x_ap = new double[NumOfAPs];
    private double[] y_ap = new double[NumOfAPs];
    /*----------选取三个AP点的坐标存放数组--------------*/
    private double[] x_3ap = new double[NumOfAPs];
    private double[] y_3ap = new double[NumOfAPs];

    /*---------输出定位点的坐标--------------*/
    private double x_point;
    private double y_point;
    private double Location_XY[] = new double[2];//装X Y定位点的数组  Location_XY[0]装X  Location_XY[1]装Y

    /*----------------------运算过程中储存数组----------------------------*/
    /*---第一轮：各平方项方程同时减去第一个方程得到的结果----*/
    private double X_coefficient[] = new double [NumOfEuqtion] ;   //2(x1-x2)   第一轮X前面系数
    private double Y_coefficient[] = new double[NumOfEuqtion];   //2(y1-y2)     第一轮Y前面系数
    private double Y_coefficient_2[] = new double[NumOfEuqtion];    //第二轮Y前面系数:2(y1-y2)/2(x1-x2)
    private double D_difference[]  = new double[NumOfEuqtion];   //d2^2-d1^2  D_difference代表D之间的差值  常量
    private double D_difference_2[]  = new double[NumOfEuqtion];   //第二轮D_difference代表D之间的差值 (d2^2-d1^2) /  2(x1-x2)
    private double XY_all[] = new double[NumOfEuqtion];   //x1^2+y1^2-x2^2-y2^2     常量
    private double XY_all_2[] = new double[NumOfEuqtion];   //第二轮XY_all_2   (x1^2+y1^2-x2^2-y2^2)/2(x1-x2)
    private double[][] algorithm = new double[NumOfAPs][NumOfEuqtion];  //最小二乘导入函数

    /*-------构造方法传入D  (X,Y)坐标在这里面赋值----------*/
    public Trilateration(double[] D_paixu,long Time){
        this.D_paixu = D_paixu;
        Flag=0;
        this.Time = Time;

        this.x_ap[0]=0.5;
        this.y_ap[0]=0.5;

        this.x_ap[1]=7.2;
        this.y_ap[1]=0.3;

        this.x_ap[2]=7.4;
        this.y_ap[2]=8.2;

        this.x_ap[3]=0;
        this.y_ap[3]=8.4;

        this.x_ap[4]=0.3;
        this.y_ap[4]=15.8;

        this.x_ap[5]=7.1;
        this.y_ap[5]=15.9;

        this.x_ap[6]=13.2;
        this.y_ap[6]=16;

    }

    /*--------------定位方法------------------*/

    @Override
    public void run() {

        Log.i("Tian", "-------------Trilateration_Location-----------------");

        for(int i=0; i<NumOfAPs ; i++){
            Log.i("Tian", "-------------D_paixu前 的距离值-----------------"+D_paixu[i]);
        }
        /*-------对D先进行MAP加一个ID序号，再进行排序----------------------*/
        HashMap<Integer,Double> Dmap = new HashMap<Integer,Double>();
        for(int i=0;i<NumOfAPs;i++) {
            Dmap.put(i, D_paixu[i]);
        }
        List<Map.Entry<Integer,Double>> list = new ArrayList<Map.Entry<Integer,Double>>(Dmap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {
            @Override
            public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
                return o1.getValue().compareTo(o2.getValue());//升序排列，降序的话吧o1和o2换一下
            }
        });
        /*for (Map.Entry<Integer,Double> mapping : list) {
            Log.i("Tian", "~~~~~~~~~~~~D排序后的距离值~~~~~~~~~~"+mapping.getKey() + ":" + mapping.getValue());
        }*/
        for(int u=0;u<NumOfAPs;u++){
            Map.Entry<Integer,Double> mapping = list.get(u);
            D_value[u] = mapping.getValue();
            D_ID[u] = mapping.getKey();
            Log.i("Tian", "~~~~~~~~~~~~D排序后的ID:"+D_ID[u] + "以及距离值：" + D_value[u]);
        }
        Log.i("Tian", "~~~~~~~     排序OK，可以进行选取前三个D的操作了           ~~~~~");

        /*----------然后去D的前三个值，然后进行三边定位，这样可以把粗差值抛弃，比如算出来D为20多米的那种D----------------------------*/
        for(int q=0;q<NumOfAPs;q++){

            switch (D_ID[q]){
                case 0: //即我排完序后，排在第一位的AP为第0个AP，也就是坐标点为（0.0）的AP点，为什么？因为我装进去MAP的时候就是按照0-6顺序来装的，拍完序后当然仍然对应
                    x_3ap[q] = 0.5;
                    y_3ap[q]=0.5;
                    break;
                case 1:
                    x_3ap[q] = 7.2;
                    y_3ap[q]=0.3;
                    break;
                case 2:
                    x_3ap[q] = 7.4;
                    y_3ap[q]=8.2;
                    break;
                case 3:
                    x_3ap[q] = 0.1;
                    y_3ap[q]=8.4;
                    break;
                case 4:
                    x_3ap[q] = 0.3;
                    y_3ap[q]=15.8;
                    break;
                case 5:
                    x_3ap[q] = 7.1;
                    y_3ap[q]=15.9;
                    break;
                case 6:
                    x_3ap[q] = 13.2;
                    y_3ap[q]=16;
                    break;
            }

        }
        Log.i("Tian", "~~~~~~~   把排完序的D_ID    D_value装入 x_3ap   y_3ap也执行完毕       ~~~~~");
        for(int q=0;q<NumOfAPs;q++){
            Log.i("Tian", "~~~~x_3ap["+q+"]为"+x_3ap[q]+"-----y_3ap["+q+"]为"+y_3ap[q]);//这里打印出来的是坐标值，对应一下ID值就行了

        }


        /*-------------------第一轮：各平方项方程同时减去第一个方程得到的结果------------------*/
        /*-----X_coefficient==2(x1-x2)-------*/
        for(int i=0; i<NumOfEuqtion ; i++){
            int j=i+1;
            X_coefficient[i]=2*(x_3ap[0]-x_3ap[j]); //X_coefficient为6个，x_3ap，正好对应i j
        }

        /*-----Y_coefficient==2(y1-y2)-------*/
        for(int i=0; i<NumOfEuqtion ; i++){
            int j=i+1;
            Y_coefficient[i]=2*(y_3ap[0]-y_3ap[j]); //Y_coefficient为6个，y_3ap，正好对应i j
        }
        /*-----D_difference==d2^2-d1^2-------*/
        for(int i=0; i<NumOfEuqtion ; i++){
            int j=i+1;
            D_difference[i]=(D_value[j])*(D_value[j])-(D_value[0])*(D_value[0])   ; //D_difference为6个，D_value，正好对应i j
        }
        /*-----XY_all==x1^2+y1^2-x2^2-y2^2-------*/
        for(int i=0; i<NumOfEuqtion ; i++){
            int j=i+1;
            XY_all[i]=((x_3ap[0]*x_3ap[0])+(y_3ap[0]*y_3ap[0]))-((x_3ap[j]*x_3ap[j])+(y_3ap[j]*y_3ap[j])); //X_coefficient为6个，x_ap为7个，正好对应i j
        }
        Log.i("Tian", "--------------Trilateration_Location ***（1）*** Finished--------------");

        /*-----------------第二轮：同除X前面的系数，得到一元二次方程，为最小二乘准备-----------------*/
        /*----Y前面的系数为Y_coefficient数组----*/
        for(int i=0; i<NumOfEuqtion ; i++){
            /*-------当我的AP点平行是在，这里的X_coefficient[i]会等于0，所以会无意义---------------*/
            if(X_coefficient[i]!=0) {
                Y_coefficient_2[i] = Y_coefficient[i] / X_coefficient[i];
                D_difference_2[i] = D_difference[i] / X_coefficient[i];
                XY_all_2[i] = XY_all[i] / X_coefficient[i];

                Log.i("Tian", "2(y1-y2)  Y_coefficient[" + i + "][0]==" + Y_coefficient[i]);
                Log.i("Tian", "2(x1-x2)  X_coefficient[" + i + "][0]==" + X_coefficient[i]);
                Log.i("Tian", "d2^2-d1^2  D_difference[" + i + "][0]==" + D_difference[i]);
                Log.i("Tian", "x1^2+y1^2-x2^2-y2^2  XY_all[" + i + "][0]==" + XY_all[i]);
            }
            /*-------如果AP平行，那么就是0*X+BY=C   AX+0Y=C形式，我的输入两个值不就是为B和C吗---------------*/
            if(X_coefficient[i]==0){
                Y_coefficient_2[i] = Y_coefficient[i];
                D_difference_2[i] = D_difference[i];
                XY_all_2[i] = XY_all[i];
            }
            if(Y_coefficient[i]==0){
                Y_coefficient_2[i] = Y_coefficient[i];
                D_difference_2[i] = D_difference[i];
                XY_all_2[i] = XY_all[i];
            }

        }
        Log.i("Tian", "--------------Trilateration_Location ***（2）*** Finished--------------");
        /*-------对最小二乘函数    AX+BY=C---》  X+(B/A)Y=(C/A)  的注释------------*/
        /*最小二乘函数需要输入一个数组algorithm[2]，其中algorithm[0]存放的是Y的系数也就是(B/A)   algorithm[1]存放(C/A)*/
        for(int i=0; i<NumOfEuqtion ; i++){
            algorithm[i][0] = Y_coefficient_2[i];
            algorithm[i][1] = D_difference_2[i]+XY_all_2[i];
            Log.i("Tian", "第"+i+"组方程 ---Y的系数也就是(B/A)   algorithm["+i+"][0]=="+ algorithm[i][0]);
            Log.i("Tian", "第"+i+"组方程 ---存放(C/A)           algorithm["+i+"][1]=="+ algorithm[i][1]);
        }
        /*------------------------------------调最小二乘---------------------------------------*/
        SimpleRegression reg = new SimpleRegression(true);
        reg.addData(algorithm);
        Log.d("Tian", "---------------最小二乘法执行完毕-----------------------");
        x_point=reg.getIntercept();
        y_point=reg.getSlope();
        Log.i("Tian", "x_point，y_point为"+x_point+";"+y_point);


        /*-----如果x_point或者y_point太大太离谱的话，就丢掉此值，取加权取平均，权值根据D来定---------------*/
        if((x_point<(0))||(x_point>12))
        {
            double sum_x_3ap=0;
            double x_weight_poing=0;
            double[] weight_x_3ap = new double[NumOfEuqtion];



            for(int i=0;i<NumOfChudazhi;i++){
                sum_x_3ap = sum_x_3ap+ (1/D_value[i])*(1/D_value[i]);//取权要成倒数,sum_x_3ap为距离倒数的平方
            }
            for(int i=0;i<NumOfChudazhi;i++){
                weight_x_3ap[i] = ((1/D_value[i])*(1/D_value[i]))/sum_x_3ap;//权值，求出每个距离倒数的权值
            }
            for(int i=0;i<NumOfChudazhi;i++){
                x_weight_poing = x_weight_poing + weight_x_3ap[i]*x_3ap[i];//x_3ap为AP点的X值坐标
            }
            x_point = x_weight_poing;
            Log.i("Tian", "if(Math.abs(x_point)>20)--------------x_point为"+x_point);
        }

        if((y_point<(0))||(y_point>19))
        {

            double sum_y_3ap=0;
            double y_weight_poing=0;
            double[] weight_y_3ap = new double[NumOfEuqtion];

            for(int i=0;i<NumOfChudazhi;i++){
                sum_y_3ap = sum_y_3ap+ (1/D_value[i])*(1/D_value[i]);//取权要成倒数,平方权
            }
            for(int i=0;i<NumOfChudazhi;i++){
                weight_y_3ap[i] = ((1/D_value[i])*(1/D_value[i]))/sum_y_3ap;
            }
            for(int i=0;i<NumOfChudazhi;i++){
                y_weight_poing = y_weight_poing + weight_y_3ap[i]*y_3ap[i];
            }
            y_point = y_weight_poing;
            Log.i("Tian", "if(Math.abs(y_point)>20)--------------y_point为"+y_point);

        }
        try {
            Thread.currentThread().sleep(Time);//毫秒延时A
            // Log.i("Tian", "第"+j+"个AP第"+i+"次延时");
        } catch (Exception e) {
        }

        /*--------最小二乘结果组成数组返回------------------*/
        Location_XY[0] = x_point;
        Location_XY[1] = y_point;
        Flag=1;



    }

    public double[] getLocation_XY() {
        return Location_XY;
    }

    public int getFlag() {
        return Flag;
    }
}
