package com.hu.tran.xcomm.common;

import lombok.Data;

/**
 * @author hutiantian
 * @create 2018/6/29 9:45
 * @since 1.0.0
 */
@Data
public class Str {
    private String name;        //定长字段名称
    private int len;            //定长字段长度
    private String sub;         //定长字段补充
}
