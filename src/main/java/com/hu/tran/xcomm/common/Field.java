package com.hu.tran.xcomm.common;

import lombok.Data;

/**
 *  xml字段对象
 * @author hutiantian
 * @create 2018/6/8 19:29
 * @since 1.0.0
 */
@Data
public class Field {
    private String name;			//字段名称
    private String desc;			//字段描述
    private String loop;			//循环域名称
    private String tag;				//对应xml的标签
}
