package com.hu.tran.xcomm.common;

import lombok.Data;

/**
 * 目表服务器对象
 * @author hutiantian
 * @create 2018/6/9 10:09
 * @since 1.0.0
 */
@Data
public class Target {
    private String id;					//通讯目标编号
    private String type;				//通讯目标类型
    private String desc;				//通讯目标描述
    private Object server;              //通讯目标对象

}
