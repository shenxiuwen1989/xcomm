package com.hu.tran.xcomm.common;

import lombok.Data;

/**
 * socket类型服务器
 * @author hutiantian
 * @create 2018/6/9 11:01
 * @since 1.0.0
 */
@Data
public class SocketServer{
    private String host;						//通讯目标服务器
    private int port;							//通讯目标服务器监听端口
    private int connectTimeout;					//通讯连接超时时间，单位为毫秒
    private int connectTryTime;					//通讯连接尝试次数
    private int transTimeout;					//通讯响应超时时间，单位为毫秒
    private int countOnNoMsg;                   //通讯返回完毕计数

}
