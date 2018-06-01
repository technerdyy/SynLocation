package com.example.administrator.synwifiandpdr;

import android.util.Log;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;

/**
 * Created by Administrator on 2017/11/1.
 * by Techenrdy 11.1
 * 卡尔曼滤波，综合WKNN和TRI三边
 * 目标：最终应该是输出一个经过综合滤波的定位值(x,y)，并且效果要优于单个WKNN和Tri
 * 输入：1》WKNN的定位点(x,y),Trilration的定位点(x,y),这两个可以组成观测变量
 *      2》还要给出初始定位点，即你要从哪一个点(x,y)开始走，才能预测下一步的值，看看到时候怎么规定行走的图形，走一个圆或者走一个方形都可以。
 *      3》我需要一个终止算法的条件，比如间隔T的次数等等
 */

public class KalmanFilter {

    /*------初始化参数-------------*/
    private  double x_TriMeasure;//来自三边和WKNN的观测值
    private  double y_TriMeasure;
    private  double x_WKnnMeasure;
    private  double y_WKnnMeasure;

    private double X_init;//可以设置一个EditText，取开始的初值
    private double Y_init;

    private  double Time;//WIFI扫描间隔，这里也就是v*time，表示尾翼增量
    private double Vehicel = 1;//速度设置

    /*-------中间可能用到的功能参数-------------*/
    private int MeasureWeishu = 2;//观测阶数
    private int ProcessWeishu = 4;//状态阶数
    private double one =1;//可以用于改噪声的大小

    /*--------噪声矩阵---------*/
    private double[][] measureNoiseR = new double[MeasureWeishu][MeasureWeishu];//观测噪声R  4*4
    private double[][] measureNoiseR_T = new double[ProcessWeishu][ProcessWeishu];//转置
    private double[][] stateNoiseQ = new double[ProcessWeishu][ProcessWeishu];//过程噪声Q 4*4
    private double[][] stateNoiseQ_T = new double[ProcessWeishu][ProcessWeishu];//转置

    /*-------协方差阵Pk以及Pk-1-------*/
    private double[][] CovLast = new double[ProcessWeishu][ProcessWeishu];//4*4          Pk-1
    private double[][] Cov_T = new double[ProcessWeishu][ProcessWeishu];//转置
    private double[][] CovMid = new double[ProcessWeishu][ProcessWeishu];//预测的P阵  Pk|k-1
    private double[][] CovNew = new double[ProcessWeishu][ProcessWeishu];//最有估计的协方差P阵   Pk

    /*-----状态方程中的Xk  Xk-1  Xk|k-1-----------*/
    private double[] StateVariableMid = new double[ProcessWeishu];
    private double[] StateVariableLast = new double[ProcessWeishu];
    private double[] StateVariableNew = new double[ProcessWeishu];

    /*---------观测方程中的Zk  Zk-1   Zk|k-1--------------*/
    private double[] MeasureVariableMid = new double[MeasureWeishu];
    private double[] MeasureVariableLast = new double[MeasureWeishu];
    private double[] MeasureVariableNew = new double[MeasureWeishu];

    /*-----------状态转移矩阵-------------------------*/
    private double[][] StateTrans = new double[ProcessWeishu][ProcessWeishu];//4*4的矩阵
    private double[][] StateTrans_T = new double[ProcessWeishu][ProcessWeishu];//转置

    /*-----------观测方程矩阵-------------------------*/
    private double[][] MeasureTrans = new double[MeasureWeishu][ProcessWeishu];//2*4的矩阵
    private double[][] MeasureTrans_T = new double[MeasureWeishu][ProcessWeishu];//转置
    /*--------中间可能用到的过度全局矩阵--------------*/
    private double[][] StateVariable_Expect = new double[ProcessWeishu][ProcessWeishu];//H*Xk|k-1
    private double[][] Cov_Expect = new double[ProcessWeishu][ProcessWeishu];//H*Pk|k-1  *H_T转置
    private double[][] Cov_Expect_qiuni = new double[ProcessWeishu][ProcessWeishu];//H*Pk|k-1  *H_T  +  R  的逆
    private double[][] Kg = new double[ProcessWeishu][MeasureWeishu];//卡尔曼增益
    private double[][] Kg_guodu_1 = new double[ProcessWeishu][MeasureWeishu];//卡尔曼增益过度镇1
    private double[][] Guodu_1 = new double[2][1];//Zk -  H* Xk|k-1
    private double[][] Guodu_2 = new double[4][1];//Kk  * (Zk -  H* Xk|k-1)
    private double[][] Guodu_3 = new double[4][4];//最后算Pk时,(I-Kk * H)

    private double[][] Fu1 = new double[2][2];//全为-1的阵
    private double[][] Fu2 = new double[4][4];//全为-1的阵
    private double[][] I_danwei = new double[4][4];//单位阵
    /*--------返回时用到的数组----------------*/
    private double[][] Pk = new double[4][4];//最有估计协方差
    private double[][] Xk = new double[4][1];//最有估计状态变量



    /*---------构造方法，初始化变量-----------*/
    public KalmanFilter(double x_TriMeasure,
                             double y_TriMeasure,
                             double x_WKnnMeasure,
                             double y_WKnnMeasure,
                             double[][] measureNoiseR,
                             double[][] stateNoiseQ,
                             double T,
                             double X_init,
                             double Y_init)
    {
            this.x_TriMeasure = x_TriMeasure;
            this.y_TriMeasure = y_TriMeasure;
            this.x_WKnnMeasure = x_WKnnMeasure;
            this.y_WKnnMeasure = y_WKnnMeasure;
            this.measureNoiseR = measureNoiseR;
            this.stateNoiseQ = stateNoiseQ;
            this.Time = T;
            this.X_init = X_init;
            this.Y_init = Y_init;

    }

    /*--------求逆矩阵方法---------*/
    public static RealMatrix inverseMatrix(RealMatrix A) {
        RealMatrix result = new LUDecomposition(A).getSolver().getInverse();
        return result;
    }
    /*------获得Pk  Xk的方法------*/
    public double[][] getXk(){
        return Xk;
    }
    public double[][] getPk(){
        return Pk;
    }


    /*--------滤波方法------------------*/
    public void  KalmanFilterMethod(){
        /*--------------------------------一些预处理----------------------------------------------*/
        StateVariableLast[0] = X_init;//从外面传进来一个X坐标，设置为初值
        StateVariableLast[2] = Y_init;
        StateVariableLast[1] = Vehicel;//X轴  1米 1S的速度
        StateVariableLast[3] = Vehicel;//Y轴  1米  1S的速度
        Log.i("Tian", "StateVariableLast: "+StateVariableLast[0]+"="+StateVariableLast[1]+"="+StateVariableLast[2]+"="+StateVariableLast[3]);
        for(int i = 0;i<MeasureWeishu;i++)
            for(int j=0;j<MeasureWeishu;j++)
            {
                Fu1[i][j] =-1;
            }

        for(int i = 0;i<ProcessWeishu;i++)
            for(int j=0;j<ProcessWeishu;j++)
            {
                Fu2[i][j] =-1;
            }

        for(int i = 0;i<ProcessWeishu;i++)

        {
            I_danwei[i][i] = 1;
        }

        /*---状态变量数组转化矩阵--*/
        RealMatrix StateVariableLastMatrix = new Array2DRowRealMatrix(StateVariableLast);//上一次最有估计阵 Xk-1

        RealMatrix StateVariableMidMatrix = new Array2DRowRealMatrix(StateVariableMid);//预测估计阵      Xk|k-1

        RealMatrix StateVariableNewMatrix = new Array2DRowRealMatrix(StateVariableNew);//本次最优估计阵    Xk

        /*-------4个状态方程，用  Math3  从数组形式转化为矩阵形式-----------
        *StateEquationMid为预测值 ，StateEquationLast为上一时刻最优估计值，T为时间间隔*/
            /*------卡尔曼增益矩阵化------*/
        RealMatrix KgMatrix = new Array2DRowRealMatrix(Kg);//Kg  卡尔曼增益
        RealMatrix Kg_guodu_1_Matrix = new Array2DRowRealMatrix(Kg_guodu_1);//Kg  卡尔曼增益
            /*-----过度镇矩阵化-------*/
        RealMatrix Guodu_1Matrix = new Array2DRowRealMatrix(Guodu_1);//Zk -  H* Xk|k-1
        RealMatrix Fu1_Matrix = new Array2DRowRealMatrix(Fu1);//2阶-1
        RealMatrix Fu2_Matrix = new Array2DRowRealMatrix(Fu2);//4阶-1
        RealMatrix Guodu_2Matrix = new Array2DRowRealMatrix(Guodu_2);//Kk  *  (Zk -  H* Xk|k-1)
        RealMatrix I_danweiMatrix = new Array2DRowRealMatrix(I_danwei);//单位阵
        RealMatrix Guodu_3Matrix = new Array2DRowRealMatrix(Guodu_3);//最后算Pk时,(I-Kk * H)

            /*----状态转移矩阵与观测矩阵初始化以及矩阵化------*/
        StateTrans[0][0] =1;StateTrans[1][1] =1;StateTrans[2][2] =1;StateTrans[3][3] =1;StateTrans[0][1] =Time;StateTrans[2][3] =Time;
        MeasureTrans[0][0] =1;MeasureTrans[1][1] =1;MeasureTrans[0][2] =Time;MeasureTrans[1][3] =Time;

        RealMatrix StateTransMatrix = new Array2DRowRealMatrix(StateTrans);//状态转移矩阵 Fk
        RealMatrix StateTransMatrix_T = new Array2DRowRealMatrix(StateTrans_T);//状态转移转置矩阵 F^T
        RealMatrix MeasureTransMatrix = new Array2DRowRealMatrix(MeasureTrans);//观测映射矩阵 H
        RealMatrix MeasureTransMatrix_T = new Array2DRowRealMatrix(MeasureTrans_T);//观测映射转置矩阵 H^T
            /*---协方差矩阵转化以及初始化----*/
        CovLast[0][0] = one;CovLast[1][1] = one;CovLast[2][2] = one;CovLast[3][3] = one;
        RealMatrix CovLastMatrix = new Array2DRowRealMatrix(CovLast);// Pk-1
        RealMatrix CovMidMatrix = new Array2DRowRealMatrix(CovMid);// Pk|k-1
        RealMatrix CovNewMatrix = new Array2DRowRealMatrix(CovNew);// Pk
            /*------噪声矩阵转化以及初始化----------------*/
        RealMatrix measureNoiseRMatrix = new Array2DRowRealMatrix(measureNoiseR);// Rk-1
        RealMatrix stateNoiseQMatrix = new Array2DRowRealMatrix(stateNoiseQ);// Qk-1
        RealMatrix Cov_Expect_qiuniMatrix = new Array2DRowRealMatrix(Cov_Expect_qiuni);//H*Pk|k-1  *H_T  +  R  的逆阵

        /*--------------------------正式开始卡尔曼滤波------------------------------------------*/
        Log.i("Tian", "--------------------正式开始卡尔曼滤波---------------------------");
        /*---状态预测----multiply为矩阵乘法的方法     Xk = Fk  Xk-1--*/
        StateVariableMidMatrix = StateTransMatrix.multiply(StateVariableLastMatrix);//Fk  Xk-1

        /*----状态协方差阵预测-    Pk|k-1 = Fk*  Pk-1  *Fk转置       ------*/
        StateTransMatrix_T = StateTransMatrix.transpose();//状态转移矩阵转置赋值给StateTransMatrix_T  也就是Fk转置

        double[][] ArrayMatrix1 = new double[4][4];
        RealMatrix Matrix1 = new Array2DRowRealMatrix(ArrayMatrix1);//把过度数组ArrayMatrix1转化为矩阵
        Matrix1 = CovLastMatrix.multiply(StateTransMatrix_T);//Matrix1  =  Pk-1  *Fk_T转置
        CovMidMatrix = StateTransMatrix.multiply(Matrix1);//Pk|k-1  = Fk  *   Matrix1
        /*-------Pk = Fk*  Pk-1  *Fk转置  +    Qk   也就是还要加上一个噪声阵Qk--------------*/
        CovMidMatrix  =  CovMidMatrix.add(stateNoiseQMatrix);
        /*----------------到这一步，已经有了预测的Xk以及Pk------------------------*/
        Log.i("Tian", "预测的Xk：StateVariableMidMatrix:    "+StateVariableMidMatrix);
        Log.i("Tian", "预测的Pk：CovMidMatrix:    "+CovMidMatrix);
        Log.i("Tian", "------------------开始算卡尔曼增益-------------------------");
        /*-------------------根据观测矩阵H，可以写出我根据状态方程预测的一个我期待的下一步的值-----------------------------------*/
        /*----求出来H*Xk|k-1,也就是H*预测的Xk-----
        * 可以理解为我根据运动方程预测的下一步值Xk|k-1，我要把它映射到观测量纲Zk上做的准备
        * StateVariable_ExpectMatrix是通过H*Xk|k-1映射过去
        * Cov_ExpectMatrix通过H*Pk|k-1  *H_T转置映射过去
        * 不过Cov_ExpectMatrix通过H还要加上一个观测噪声Q*/
        RealMatrix StateVariable_ExpectMatrix = new Array2DRowRealMatrix(StateVariable_Expect);//H*Xk|k-1
        /*----求出来H*Pk|k-1  *H_T转置-----*/
        RealMatrix Cov_ExpectMatrix = new Array2DRowRealMatrix(Cov_Expect);//H*Pk|k-1  *H_T转置
        /*-----H*Pk|k-1  *H_T转置  再加上一个观测噪声R------*/
        Cov_ExpectMatrix  =  Cov_ExpectMatrix.add(measureNoiseRMatrix);
        /*------对Cov_ExpectMatrix也就是H*Pk|k-1  *H_T进行求逆运算----------*/
        Cov_Expect_qiuniMatrix  =  inverseMatrix(Cov_ExpectMatrix);//Cov_Expect_qiuniMatrix为H*Pk|k-1  *H_T  + R的逆阵
        /*------因为卡尔曼增益 Kg = Pk|k-1  *  H_T转置  *  (H*Pk|k-1  *H_T+R)的逆矩阵-----------
        * Pk|k-1:CovMidMatrix
        *  H_T转置:MeasureTransMatrix_T
        * (H*Pk|k-1  *H_T+R)的逆矩阵: Cov_Expect_qiuniMatrix 这些在上面都已经求出来了 */
        Kg_guodu_1_Matrix  =  CovMidMatrix.multiply(MeasureTransMatrix_T);//卡尔曼过度矩阵1，Pk|k-1  *  H_T转置
        KgMatrix = Kg_guodu_1_Matrix.multiply(Cov_Expect_qiuniMatrix);//Pk|k-1  *  H_T转置   *  (H*Pk|k-1  *H_T+R)的逆矩阵
        /*----------到这一步，已经有了卡尔曼增益----------------*/
        Log.i("Tian", "卡尔曼增益：KgMatrix:    "+KgMatrix);
        /*-------------------下面算最优估计值Xk以及最有估计协方差阵Pk--------------------------------*/
        Log.i("Tian", "------------开始算最优估计值Xk以及最有估计协方差阵Pk---------------");
        /*---Zk -  H* Xk|k-1---
        * 已知StateVariable_ExpectMatrix： H* Xk|k-1
        * */
        /*----先表示Zk  =  H *  Xk-1  +  Q---------*/
        MeasureTransMatrix  =  MeasureTransMatrix.multiply(StateVariableLastMatrix);//Zk  =  H *  Xk-1  +  R
        MeasureTransMatrix  =  MeasureTransMatrix.add(measureNoiseRMatrix);//Zk  =  H *  Xk-1  +  R
        /*-----再算 Zk -  H* Xk|k-1  --------------*/
        Guodu_1Matrix =  MeasureTransMatrix.multiply(StateVariableMidMatrix);// H* Xk|k-1
        Guodu_1Matrix =  Fu1_Matrix.multiply(Guodu_1Matrix); //-1* H* Xk|k-1
        Guodu_1Matrix =  MeasureTransMatrix.add(Guodu_1Matrix);//Guodu_1Matrix:  Zk -  H* Xk|k-1  2*1
        /*----计算最优估计值StateVariableNewMatrix以及协方差CovNewMatrix-------*/
        Guodu_2Matrix  =  Guodu_1Matrix.multiply(Guodu_1Matrix);//Kk  *  (Zk -  H* Xk|k-1  2*1)
        StateVariableNewMatrix  =  StateVariableMidMatrix.add(Guodu_2Matrix);//Xk = Xk|k-1  +  Kk  *  (Zk -  H* Xk|k-1  2*1)
        Log.i("Tian", "最有估计状态值：StateVariableNewMatrix:    "+StateVariableNewMatrix);
        /*------继续算最有估计协方差CovNewMatrix--------
        * Pk  =  (I  -  Kk  *H)* Pk|k-1
         * Pk|k-1:CovMidMatrix
         * Kk:KgMatrix
         * H:MeasureTransMatrix
         * I:I_danweiMatrix    */
        Guodu_3Matrix  =  KgMatrix.multiply(MeasureTransMatrix);//Kk*H  4*4
        Guodu_3Matrix  =    Fu2_Matrix.multiply(Guodu_3Matrix);//-Kk*H  4*4
        Guodu_3Matrix  =  I_danweiMatrix.add(Guodu_3Matrix);//I  -  Kk  *H
        CovNewMatrix  =  Guodu_3Matrix.multiply(CovMidMatrix);
        Log.i("Tian", "最有估计协方差：CovNewMatrix:    "+CovNewMatrix);
        /*-----把最优估计得两个向量变成数组并且返回-------*/
        Pk = CovNewMatrix.getData();  //4 *4
        Xk = StateVariableNewMatrix.getData();//2*1

        for(int i = 0;i<MeasureWeishu;i++) {
            for (int j = 0; j < 1; j++)

            {
                Log.i("Tian", "最有估计状态值：Xk:    " + Xk[i][j]);
            }
        }
        for(int i = 0;i<ProcessWeishu;i++)
            for(int j=0;j<ProcessWeishu;j++)
            {
                Log.i("Tian", "最有估计协方差：Pk:    "+Pk[i][j]);
            }
    }


}
