package com.hu.tran.xcomm.core;

import com.hu.tran.xcomm.adapter.SocketAdapter;
import com.hu.tran.xcomm.common.Constant;
import com.hu.tran.xcomm.common.Pack;
import com.hu.tran.xcomm.common.SocketServer;
import com.hu.tran.xcomm.common.Target;
import lombok.extern.log4j.Log4j;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.UUID;

/**
 * xComm服务入口
 * @author hutiantian
 * @create 2018/6/7 18:58
 * @since 1.0.0
 */
@Log4j
public class XCommService {

    public static String tran(String packCode, Map<String,Object> sendMap,Map<String,Object> returnMap){
        if(packCode==null||packCode.equals("")||sendMap==null||returnMap==null){
            log.debug("请求参数非法！");
            return "9001";
        }
        Pack pack = PackMapper.getInstance().getPack(packCode);
        if(pack==null){
            log.debug(packCode+"报文不存在！");
            return "9002";
        }
        String msgId = null;
        //是否需要记录报文日志
        if(pack.getLogFlag()){
            msgId = getRandomId();           //获取一个随机编号，用于记录日志
        }
        ByteArrayOutputStream packBaos = new ByteArrayOutputStream();
        //拼接请求报文
        try{
            RequestHandler.packRequest(msgId,pack,sendMap,packBaos);
        }catch (Exception e){
            log.debug(packCode+"拼接请求报文异常！",e);
            return "9003";
        }
        //发送请求报文+接受响应报文
        try{
            xtran(pack,packBaos);
        }catch (Exception e){
            packBaos.reset();
            log.debug(packCode+"与服务器通讯异常！",e);
            return "9004";
        }
        if(packBaos.size()==0){
            log.debug(packCode+"应答报文为空！");
            return "9999";
        }
        //解析响应报文
        try{
            ResponseHandler.handlerResponse(msgId,pack,returnMap,packBaos);
        }catch (Exception e){
            log.debug(packCode+"解析请求报文异常！",e);
            return "9005";
        }
        return "0000";
    }

    /**
     * 生成随机数
     */
    private static String getRandomId() {
        String result = UUID.randomUUID().toString();
        result = result.replaceAll("-", "");
        return result.toUpperCase();
    }

    /**
     * 发送报文并接受报文
     * @param pack
     * @param packBaos
     */
    private static void xtran(Pack pack,ByteArrayOutputStream packBaos){
        Target target = TargetMapper.getInstance().getTarget(pack.getTarget());
        //目标系统的通讯方式为socket
        if(target.getType().equals(Constant.Target.socketServer)){
            SocketAdapter.sendMsg((SocketServer)target.getServer(),packBaos);
        }
        //其它通讯方式，消息队列等等
    }
}
