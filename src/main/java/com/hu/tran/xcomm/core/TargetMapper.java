package com.hu.tran.xcomm.core;

import com.hu.tran.xcomm.common.Constant;
import com.hu.tran.xcomm.common.SocketServer;
import com.hu.tran.xcomm.common.Target;
import lombok.extern.log4j.Log4j;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hutiantian
 * @create 2018/6/9 11:03
 * @since 1.0.0
 */
@Log4j
public class TargetMapper {

    private static TargetMapper targetMapper;
    private static Map<String,Target> targetMap = new HashMap();;

    private TargetMapper(){}

    private TargetMapper(String configFilePath){
        //读取服务方配置文件
        Document doc = null;
        try{
            SAXReader sax = new SAXReader();
            doc = sax.read(new File(configFilePath));
        }catch (Exception e){
            log.error("读取服务方配置"+configFilePath+"异常！",e);
        }
        if(doc!=null){
            List<Element> targetList = doc.getRootElement().elements("Server");
            if(targetList==null||targetList.size()==0){
                log.error("服务方配置"+configFilePath+"未指定server");
                return;
            }
            boolean ret = true;
            for(Element targetElement : targetList) {
                Target target = getTarget(targetElement);
                if(null == target) {
                    ret = false;
                    break;
                } else {
                    if(targetMap.containsKey(target.getId())) {
                        //已存在该编号通讯目标
                        log.warn("通讯目标[" + target.getId() + "]重复配置");
                        ret = false;
                        break;
                    } else {
                        targetMap.put(target.getId(), target);
                    }
                }
            }
            if(ret) {
                targetMapper = this;
            }
        }
    }

    /**
     * 解析服务方配置
     */
    private Target getTarget(Element targetElement){
        Element id = targetElement.element("id");
        if(id==null){
            log.error("未配置id字段！");
            return null;
        }
        Element typeEle = targetElement.element("type");
        if(typeEle==null){
            log.error("未配置type字段！");
            return null;
        }
        String type = typeEle.getText();
        if(!type.equals(Constant.Target.socketServer)) {
            log.error("通讯目标[" + id.getText() + "]类型[" + type + "]不存在");
            return null;
        }
        Element desc = targetElement.element("desc");
        if(type==null){
            log.error("未配置desc字段！");
            return null;
        }
        Object serverObj = null;
        if(type.equals(Constant.Target.socketServer)){
            serverObj = getSocketServer(targetElement);
        }
        Target target = new Target();
        target.setId(id.getText());
        target.setType(type);
        target.setDesc(desc.getText());
        target.setServer(serverObj);
        return target;
    }

    /**
     * 获取socketServer对象
     */
    private SocketServer getSocketServer(Element targetElement){
        Element host = targetElement.element("host");
        if(host==null){
            log.error("未配置host字段！");
            return null;
        }
        Element port = targetElement.element("port");
        if(port==null){
            log.error("未配置port字段！");
            return null;
        }
        Element connectTimeout = targetElement.element("connecttimeout");
        if(host==null){
            log.error("未配置connecttimeout字段！");
            return null;
        }
        Element connectTryTime = targetElement.element("connecttrytime");
        if(port==null){
            log.error("未配置connecttrytime字段！");
            return null;
        }
        Element transTimeout = targetElement.element("transtimeout");
        if(host==null){
            log.error("未配置transtimeout字段！");
            return null;
        }
        Element countOnNoMsg = targetElement.element("countonnomsg");
        if(port==null){
            log.error("未配置countonnomsg字段！");
            return null;
        }
        SocketServer socketServer = new SocketServer();
        socketServer.setHost(host.getText());
        socketServer.setPort(Integer.parseInt(port.getText()));
        socketServer.setConnectTimeout(Integer.parseInt(connectTimeout.getText()));
        socketServer.setConnectTryTime(Integer.parseInt(connectTryTime.getText()));
        socketServer.setTransTimeout(Integer.parseInt(transTimeout.getText()));
        socketServer.setCountOnNoMsg(Integer.parseInt(countOnNoMsg.getText()));
        return socketServer;
    }

    /**
     * 初始化实例对象
     */
    public static boolean init(String configFilePath){
        if(targetMapper==null){
            new TargetMapper(configFilePath);
        }
        if(targetMapper==null){
            log.debug("服务方配置初始化失败！");
            return false;
        }else {
            log.debug("服务方配置初始化成功！");
            return true;
        }
    }

    /**
     * 获取实例化对象
     */
    public static TargetMapper getInstance(){
        return targetMapper;
    }

    /**
     * 获取服务方对象
     */
    public Target getTarget(String targetId){
        if(null != targetId && !"".equals(targetId) && targetMap.containsKey(targetId)) {
            return targetMap.get(targetId);
        } else {
            return null;
        }
    }
}
