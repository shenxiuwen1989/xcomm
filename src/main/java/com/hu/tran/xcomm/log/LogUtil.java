package com.hu.tran.xcomm.log;

import com.hu.tran.xcomm.common.Constant;
import com.hu.tran.xcomm.core.PackMapper;
import lombok.extern.log4j.Log4j;
import org.apache.log4j.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hutiantian
 * @create 2018/6/24 11:45
 * @since 1.0.0
 */
@Log4j
public class LogUtil {

    private static Map<String, Logger> loggerMap = new HashMap<String, Logger>();		//缓存日志输出类表
    private static byte[] creationLock = new byte[0];									//logger创建同步锁

    /**
     * 记录报文信息
     * @param msgId				报文处理唯一编号
     * @param packCode			报文类型/编号
     * @param packStatus		报文记录类型，0代表发送报文，1代表返回报文
     * @param baos				报文内容
     */
    public static boolean tracePack(String msgId, String packCode, int packStatus, ByteArrayOutputStream baos) {
        //判断报文记录方式
        if(Constant.Log.logType.equals("logPack")) {
            //其余按每个报文独立文件的形式记录
            return tracePackByMsgPack(msgId, packCode, packStatus, baos);
        } else {
            //按报文全天整合文件
            return tracePackByMsgType(msgId, packCode, packStatus, baos);
        }
    }

    /**
     * 按报文类型全天整合文件
     * @param msgId				报文处理唯一编号
     * @param packCode			报文类型/编号
     * @param packStatus		报文记录类型，0代表发送报文，1代表返回报文
     * @param baos				报文内容
     */
    private static boolean tracePackByMsgType(String msgId, String packCode, int packStatus, ByteArrayOutputStream baos) {
        //判断是否已生成logger
        Logger traceLogger = loggerMap.get(packCode);
        if(null == traceLogger) {
            //未生成logger
            String tracePath = getTracePath(packCode);
            if(null == tracePath) {
                //全局不记录日志，直接返回成功
                return true;
            }
            traceLogger = createLogger(tracePath, packCode);
        }
        if(null == traceLogger) {
            return false;
        } else {
            String msgInfo = "通讯报文[" + msgId + "]";
            if(packStatus == Constant.Log.send) {
                msgInfo += "发送报文";
            }else{
                msgInfo += "返回报文";
            }
            msgInfo += "\n" + baos.toString() + "\n----------------------------你瞅啥-------------------------------\n";
            traceLogger.info(msgInfo);
            return true;
        }
    }

    /**
     * 按报文记录独立文件
     * @param msgId				报文处理唯一编号
     * @param msgType			报文类型/编号
     * @param packStatus		报文记录类型，0代表发送报文，1代表返回报文
     * @param baos				报文内容
     */
    private static boolean tracePackByMsgPack(String msgId,String msgType, int packStatus, ByteArrayOutputStream baos) {
        //生成缓存路径
        String tracePath = getTracePath(msgType);
        if(null == tracePath) {
            //全局不记录日志，直接返回成功
            return true;
        }
        //生成文件名
        String fileName = "";
        String fileDesc = "";
        if(packStatus == Constant.Log.send) {
            fileName = new SimpleDateFormat("HH_mm_sss[SSS]").format(Calendar.getInstance().getTime())+"_"+msgId + "_"  + "[send]";
            fileDesc = "发送报文";
        }else {
            fileName = new SimpleDateFormat("HH_mm_sss[SSS]").format(Calendar.getInstance().getTime())+"_"+msgId + "_"  + "[recv]";
            fileDesc = "返回报文";
        }
        try {
            FileOutputStream fos = new FileOutputStream(tracePath + fileName);

            fos.write(baos.toByteArray());
            fos.close();
            return true;
        } catch(Exception e) {
            log.info("记录" + fileDesc + "异常[" + e.getMessage() + "]", e);
            return false;
        }
    }

    /**
     * 创建logger
     * @param tracePath			报文缓存目录
     * @param msgType			报文类型
     * @return					返回创建的logger
     */
    private static Logger createLogger(String tracePath, String msgType) {
        synchronized(creationLock) {
            Logger traceLogger = loggerMap.get(msgType);
            if(null == loggerMap.get(msgType)) {
                traceLogger = Logger.getLogger("packtace[" + msgType + "]");
                Layout layout = new PatternLayout("%d - %m%n");
                Appender appender = null;
                try {
                    appender = new DailyRollingFileAppender(layout, tracePath + msgType, "_yyyy-MM-dd");
                    traceLogger.addAppender(appender);
                    traceLogger.setAdditivity(false);
                    loggerMap.put(msgType, traceLogger);
                } catch(Exception e) {
                    log.info("创建报文[" + msgType + "]缓存适配器异常[" + e.getMessage() + "]", e);
                    traceLogger = null;
                }
            }
            return traceLogger;
        }
    }

    /**
     * 获取通讯报文缓存路径
     * @param msgType			报文类型
     * @return					返回报文缓存目录路径
     */
    private static String getTracePath(String msgType) {
        String tracePath = Constant.Log.logPath;
        if(!tracePath.startsWith("/")) {
            //相对路径
            tracePath = PackMapper.configBaseDir + tracePath;
        }
        if(Constant.Log.logType.equals("logPack")) {
            //按每个报文独立文件的形式记录
            tracePath += File.separator + new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
            tracePath += File.separator + new SimpleDateFormat("HH").format(Calendar.getInstance().getTime());
            tracePath += File.separator + msgType + File.separator;
        } else {
            //按报文全天整合文件
            tracePath += File.separator + msgType + File.separator;
        }
        //建立目录
        File f = new File(tracePath);
        if(!f.exists()) {
            f.mkdirs();
        }
        return tracePath;
    }
}
