package com.hu.tran.xcomm.adapter;

import com.hu.tran.xcomm.common.SocketServer;
import lombok.extern.log4j.Log4j;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * socket报文处理类
 * @author hutiantian
 * @create 2018/6/24 17:44
 * @since 1.0.0
 */
@Log4j
public class SocketAdapter {

    /**
     * 发送socket报文消息
     */
    public static void sendMsg(SocketServer socketServer, ByteArrayOutputStream packBaos){
        Socket socket = connect(socketServer);          //获取服务器连接
        if(socket==null){
            packBaos.reset();
            return;
        }
        if(send(socket,packBaos)){                       //发送消息
            log.debug("---准备接收响应报文");
            receive(socket,packBaos,socketServer.getConnectTimeout(),socketServer.getCountOnNoMsg());
            if(packBaos.size()>0){
                log.debug("----接收响应报文成功！");
            }
        }
        try {
            socket.close();
        } catch (IOException e) {
            log.debug("关闭Socket连接异常",e);
        }
    }

    /**
     * 连接服务器
     */
    private static Socket connect(SocketServer socketServer){
        Socket socket = null;
        //多次尝试连接
        for(int i = 0; i < socketServer.getConnectTryTime();i ++) {
            try {
                socket = new Socket();
                socket.setSoTimeout(socketServer.getTransTimeout());
                socket.connect(new InetSocketAddress(socketServer.getHost(), socketServer.getPort()), socketServer.getConnectTimeout());
                break;
            } catch (Exception e) {
                String msg = "第" + (i + 1) + " 次尝试连接服务器[" + socketServer.getHost() + ":" + socketServer.getPort()
                        + "]发生异常[" + e.getMessage() + "]";
                log.debug(msg);
                socket = null;
            }
        }
        return socket;
    }

    /**
     * 发送消息
     */
    private static boolean send(Socket socket, ByteArrayOutputStream baos) {
        boolean flag = false;
        try {
            //BufferedOutputStream输出，在极端情况下可提升一些效率
            BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
            bos.write(baos.toByteArray());
            bos.flush();
            flag = true;
        } catch(Exception e) {
            log.debug("发送消息失败！", e);
        }
        baos.reset();
        return flag;
    }

    /**
     * 接收消息
     */
    private static void receive(Socket socket, ByteArrayOutputStream baos, int timeout, int countOnNoMsg) {
        try {
            //尝试读取返回消息报文
            long startTime = System.nanoTime();				//开始接收时间
            InputStream is = socket.getInputStream();
            boolean readingFlag = false;				//是否已开始接收报文内容
            long lastReadTime = System.nanoTime();		//上次读取报文内容时间
            while(true) {
                if(is.available() > 0) {
                    byte[] tmpBytes = new byte[1024];
                    int ret = is.read(tmpBytes);
                    if(ret == -1) {
                        break;
                    } else {
                        if(ret > 0) {
                            //读取到有效字节
                            lastReadTime = System.nanoTime();
                            readingFlag = true;
                            baos.write(tmpBytes, 0, ret);
                            continue;
                        }
                    }
                }
                long nowTime = System.nanoTime();
                //判断是否已超时
                if(readingFlag) {
                    //已开始接收报文
                    if((nowTime - lastReadTime) / 1000000l > countOnNoMsg) {
                        //指定时间内没有再读取到内容，认为接收完毕
                        break;
                    }
                } else {
                    //未开始接收报文
                    if((nowTime - startTime) > (1000000l * timeout)) {
                        log.debug("---接口响应超时，未接收到响应报文！");
                        break;
                    }
                }
                Thread.sleep(0, 1);							//延时处理
            }
        } catch(Exception e) {
            log.debug("接收消息报文异常", e);
        }
    }
}
