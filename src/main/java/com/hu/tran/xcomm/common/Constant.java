package com.hu.tran.xcomm.common;

/**
 * 常量类
 * @author hutiantian
 * @create 2018/6/11 9:22
 * @since 1.0.0
 */
public class Constant {

    public static final String constantMap = "constantMap";

    public static class Target{
        public static final String socketServer= "socket";

    }

    public static class Log{
        public static final String logType= "logPack";          //按每次报文记录日志
        public static final String logPath= "/logs/packLog";         //报文日志路径

        public static final int send = 0;
        public static final int recv = 1;
    }

}
