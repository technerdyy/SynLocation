package com.example.administrator.synwifiandpdr;

/**
 * Created by Administrator on 2017/10/6.
 */

public class ConvertEachOther {

    private double value_1;

    public ConvertEachOther(double value){ //输入一个真实的物理坐标
        this.value_1 = value;
    }

    public int XiangsuToMi(){        //然后在这里把坐标转化为像素距离，每25个像素点代表1米
        int a = (int)(value_1*25);
        return a;                   //a代表的就是像素距离
    }


}
