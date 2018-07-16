package com.hu.tran.xcomm.common;

import lombok.Data;

import java.util.ArrayList;

/**
 * xml报文对象
 * @author hutiantian
 * @create 2018/6/8 19:30
 * @since 1.0.0
 */
@Data
public class Pack {
    private String packCode;				    //通讯报文交易码
    private String desc;					    //通讯报文描述
    private String target;					    //通讯报文目的端编号
    private Boolean logFlag;                    //是否记录日志标识
    private String encoding;				    //通讯报文编码格式
    private String root;                        //通讯报文根节点
    private ArrayList<Field> requestList;       //请求字段集合
    private ArrayList<Field> responseList;      //响应字段集合
    private ArrayList<Str> constant;               //定长字段集合
    private LengthInfo lengthInfo;              //报文长度信息
}
