package com.hu.tran.xcomm.common;

import lombok.Data;

/**
 * 报文长度字段
 * @author hutiantian
 * @create 2018/6/22 9:51
 * @since 1.0.0
 */
@Data
public class LengthInfo {
    private boolean enable;					//是否启用报文长度字段标识位
    private int infoLen;					//报文长度字段长度
    private boolean selfFlag;				//长度信息是否包含本身标识位
    private int format;                   //进制(八进制、十进制、十六进制)
    private int returnLen;               //返回报文长度
}
