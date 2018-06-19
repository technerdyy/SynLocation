package com.example.administrator.synwifiandpdr;

import android.hardware.SensorEvent;

import Jama.Matrix;

/**
 * Created by Administrator on 2018/6/11.
 * by Technerdy
 * IMU这个类输入应该为经过低通滤波后的陀螺仪的三轴角速度以及加计的三轴加速度，IMU_Navigator给出解算过程，最终输出位置和速度信息，
 * 在这里应该从IMU_Filter读数输入信息，出去的信息应该给到卡尔曼滤波，进行与WIFI的融合
 *
 * 整体流程：
 * PS：陀螺输出的是载体系相对于惯性系的旋转速度，而我们需要的是载体系相对于局部坐标系的旋转速度，所以
 *     我们需要用一个矩阵，把惯性系转成局部坐标系。
 *     在惯导中是把惯性系转成导航系，也就是东北天，这里我们得转成局部坐标系
 *
 *
 * 0：将载体系的三轴陀螺数据通过上一步求得的姿态矩阵转换到地理系的陀螺数据，但是！这里：因为我们不像大飞机一样那么精确，
 * 1：陀螺仪三轴数据通过构造函数输入进来，储存在Filtered_Gyo中
 * 2：从Filtered_Gyo中的数据得到Gyo_Reciv_4Matrix数据。
 * 3：dk = (1/2)  *  Gyo_Reciv_4Matrix  *  k(t-1)       k(t-1)  为上一时刻的四元数
 * 4:  k(t) = k(t-1)+dk  * T                     T为采样周期，可以有timestamp得到
 * 5： k(t) = k(t)/norm      归一化处理，norm由三轴数据平方根得到
 * 6： 更新后的四元数OK  k(t),所以姿态矩阵也可以更新
 * 7：姿态阵C具体去看书，均由k(t)中数值组成
 */

public class IMU_Navigator {

       //传进来的9轴数据
       private double[] Filtered_Acc = new double[3];//低通滤波后的加速度计三轴值
       private double[] Filtered_Gyo = new double[3];//高通滤波后的陀螺三轴值
       private double[] Filtered_Magn = new double[3];//磁场值，为后面改进算法做准备，先留一个接口
       //四元数更新用到的
       private double[][] Quaternion_Updata_shuzu = new double[4][4];//dk向量
       private Matrix Quaternion_Updata_Matrix = new Matrix(Quaternion_Updata_shuzu);//更新四元数的矩阵

       private Matrix Q_kk = new Matrix(1,4);//Q(k+1)时刻的四元数向量
       private Matrix Q_k = new Matrix(1,4);//Q(k)时刻的四元数向量


       //时间戳，求陀螺增量相关
       private SensorEvent sensorEvent;

       private float dT = 0;//时间戳
       private static final float NS2S = 1.0f / 1000000000.0f;//将纳秒转为秒为单位
       private float gx = 0,gy = 0,gz = 0;


       public IMU_Navigator(double[] Filtered_Acc,double[] Filtered_Gyo, double[] Filtered_Magn, float dT){
              this.Filtered_Acc = Filtered_Acc;
              this.Filtered_Gyo = Filtered_Gyo;
              this.Filtered_Magn = Filtered_Magn;
              this.dT = dT;
       }
       public IMU_Navigator(double[] Filtered_Acc,double[] Filtered_Gyo){
              this.Filtered_Acc = Filtered_Acc;
              this.Filtered_Gyo = Filtered_Gyo;
       }



       public IMU_Navigator(double[] Filtered_Acc){
              this.Filtered_Acc = Filtered_Acc;
       }


       //四元数更新
       /*具体步骤：
       * 1：传入1*3的陀螺数据，转成4*4的陀螺增量数组，再转成矩阵形式
       * 2：根据时间戳，算出陀螺增量，怎么算的看惯性导航一书中的公式9.2.51
       * 3：进行姿态更新
       *
       * */
       public Matrix Quaternion_Updata(){

              double[][] W_receive_shuzu = new double[4][4];//接受陀螺数据的4维数组
              double[][] I_Matrix = new double[4][4];
              //当我的初始四元数不为零才进行更新，否则先进性初始化四元数
              if((Q_k.get(0,1)!=0)&&(Q_k.get(0,2)!=0)&&(Q_k.get(0,3)!=0)&&(Q_k.get(0,4)!=0)) {
                     //当陀螺数组进来了值我才进行计算
                     if ((Filtered_Gyo[0] != 0) && (Filtered_Gyo[1] != 0) && (Filtered_Gyo[2] != 0)) {

                            W_receive_shuzu = GyoData_To_Matrix(Filtered_Gyo);//把陀螺增量数据储存到4维数组
                            Matrix buf_1 = new Matrix(W_receive_shuzu);//4维数组转换为矩阵得到buf_1
                            //Q(k) = (I + 1/2  *  W_receive_Matrix)*  Q(k-1)
                            //乘1/2得到buf_2
                            Matrix buf_2 = buf_1.times(1 / 2);
                            //加单位阵得到buf_3
                            Matrix I_Matrix_buf = new Matrix(I_Matrix);
                            Quaternion_Updata_Matrix = buf_2.plus(I_Matrix_buf);

                            //进行四元数更新
                            Q_kk = Quaternion_Updata_Matrix.times(Q_k);
                     }
              }



              return Q_kk;
       }

       //把陀螺仪数据储存到4维数组中的方法,输入1*3的数组，输出4*4的数组
       //同时，把陀螺增量值求出来
       protected double[][] GyoData_To_Matrix(double[] GyoData){
              double[][]  GyoBuf = new double[4][4];
              double angle[] = new double[3];//储存角度值


              //如果时间戳不为零，就开始算陀螺增量
              if(dT != 0) {
                  angle[0] += GyoData[0] * dT;
                  angle[1] += GyoData[1] * dT;
                  angle[2] += GyoData[2] * dT;


                  angle[0] = (float) Math.toDegrees(angle[0]);
                  angle[1] = (float) Math.toDegrees(angle[1]);
                  angle[2] = (float) Math.toDegrees(angle[2]);
                  //Log.i("Tian", "angle[0] is :       " + angle[0]);
                  //Log.i("Tian", "angle[1] is :       " + angle[1]);
                  //Log.i("Tian", "angle[2] is :       " + angle[2]);


              }





              //为什么这样，看四元数微分方程毕卡法
              GyoBuf[0][0] = 0;GyoBuf[0][1] = (-angle[0]);GyoBuf[0][2] = (-angle[1]);GyoBuf[0][3] = (-angle[2]);
              GyoBuf[1][0] = angle[0];GyoBuf[1][1] = 0;GyoBuf[1][2] = angle[2];GyoBuf[1][3] = (-angle[1]);
              GyoBuf[2][0] = angle[1];GyoBuf[2][1] = (-angle[2]);GyoBuf[2][2] = 0;GyoBuf[2][3] = angle[0];
              GyoBuf[3][0] = (-angle[2]);GyoBuf[3][1] = angle[1];GyoBuf[3][2] = (-angle[0]);GyoBuf[3][3] = 0;


              return GyoBuf ;
       }


}
