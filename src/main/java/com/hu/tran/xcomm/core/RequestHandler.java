package com.hu.tran.xcomm.core;

import com.hu.tran.xcomm.common.*;
import com.hu.tran.xcomm.log.LogUtil;
import lombok.extern.log4j.Log4j;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 发送报文前处理
 * @author hutiantian
 * @create 2018/6/22 17:44
 * @since 1.0.0
 */
@Log4j
public class RequestHandler {

    /**
     * 将sendMap的值映成待发送的xml
     * @param msgId 随机数用于生成报文日志,若为空，则不记录日志
     * @param pack 请求对应的xml配置对象
     * @param sendMap 请求map，key为xml的name，value为xml对应的tag标签
     * @param packBaos 待发送报文的字节流
     */
    public static void packRequest(String msgId, Pack pack, Map<String,Object> sendMap,ByteArrayOutputStream packBaos) throws Exception{
        Document reqDoc =  DocumentHelper.createDocument();             //待拼装并发送的xml报文
        reqDoc.setXMLEncoding(pack.getEncoding());
        Element root = reqDoc.addElement(pack.getRoot());
        //遍历请求配置字段，
        for(int i=0;i<pack.getRequestList().size();i++){
            Element tempRoot = root;
            Field field = pack.getRequestList().get(i);
            String loop = field.getLoop();
            if(loop.equals("")){							//非循环域字段
                if(sendMap.get(field.getName())!=null){
                    putValueToXml(tempRoot,field.getTag(),sendMap.get(field.getName()).toString());
                }else{
                    putValueToXml(tempRoot,field.getTag(),"");
                }
            }else{											//循环域字段一次性处理完
                ArrayList<Field> fieldList = new ArrayList<Field>();
                for(Field field1:pack.getRequestList()){
                    if(field1.getLoop().equals(loop)){
                        fieldList.add(field1);
                    }
                }
                List<Map<String,String>> mapList = null;
                try{
                    mapList = (List<Map<String,String>>)sendMap.get(loop);
                }catch(Exception e){
                    mapList = new ArrayList<Map<String,String>>();
                    log.info("拼接处理结果异常,循环域结果为空！",e);
                }
                if(mapList!=null&&mapList.size()>0){
                    putLoopToXml(tempRoot,mapList,fieldList);
                }
                i = i+fieldList.size()-1;
            }
        }
        //定长字段处理，先拼装，不写入流（报文构成=长度字段+定常部分+xml部分）
        String con = "";
        if(pack.getConstant()!=null){
            HashMap<String,String> constantMap = (HashMap<String,String>)sendMap.get(Constant.constantMap);
            ArrayList<Str> strList = pack.getConstant();
            StringBuilder sb = new StringBuilder();             //用于拼接定长字符串
            sb.append("{H:");
            for(Str str:strList){
                sb.append(spilt(constantMap.get(str.getName()),str.getLen(),str.getSub()));
            }
            sb.append("}");
            con = sb.toString();
        }
        //报文长度字段处理
        LengthInfo lengthInfo = pack.getLengthInfo();
        packBaos.write(reqDoc.asXML().getBytes(pack.getEncoding()));
        String packLen = "";
        if(lengthInfo.isEnable()){                      //长度字段启用，需要计算报文长度
            int length = packBaos.size()+con.getBytes(pack.getEncoding()).length;
            if(lengthInfo.isSelfFlag()){
                length = length + lengthInfo.getInfoLen();
            }
            if(lengthInfo.getFormat()==10){         //十进制表示
                packLen = String.format("%0"+lengthInfo.getInfoLen()+"d",length);
            }
            packBaos.reset();
            packBaos.write((packLen+con+reqDoc.asXML()).getBytes(pack.getEncoding()));
        }
        if(msgId!=null){            //记录发送的报文日志
            //格式化一下，便于阅读
            OutputFormat format = new OutputFormat();
            format.setEncoding(pack.getEncoding());						//设置编码
            format.setNewlines(true);									//是否换行
            format.setIndent(true);										//缩进
            format.setIndent("    ");									//用4个空格缩进
            StringWriter sw = new StringWriter();
            XMLWriter xmlWriter = new XMLWriter(sw,format);
            xmlWriter.write(reqDoc);
            ByteArrayOutputStream logByte = new ByteArrayOutputStream();
            logByte.write((packLen+con+sw.toString()).getBytes(pack.getEncoding()));
            sw.close();
            xmlWriter.close();
            LogUtil.tracePack(msgId,pack.getPackCode(),0,logByte);
        }
    }

    /**
     * 定常字段处理
     */
    private static String spilt(String value,int len,String sub){
        String result = "";
        if(value.length()<len){
            result = value;
            for(int i=0;i<len-value.length();i++ ){
                result = result+sub;
            }
        }else if(value.length()==len){
            result = value;
        }else {
            result = value.substring(0,len);
        }
        return result;
    }

    /**
     * 将request的非循环域结果放入xml
     * @param elem 根元素
     * @param tag 标签集合
     * @param value 最后一个标签的值
     */
    private static void putValueToXml(Element elem,String tag,String value){
        String[] strAarry = tag.split("/");
        for(int i=1;i<strAarry.length;i++){
            if(elem.element(strAarry[i])==null){
                elem = elem.addElement(strAarry[i]);
            }else{
                elem = elem.element(strAarry[i]);
            }
            if(i==strAarry.length-1){
                elem.setText(value);
            }
        }
    }

    /**
     * 将request的循环域结果放入xml
     * @param elem 根元素
     * @param mapList 响应结果map集合
     * @param fieldList 同一loop的标签集合
     */
    private static void putLoopToXml(Element elem,List<Map<String,String>> mapList,List<Field> fieldList){
        String[] strAarry = fieldList.get(0).getTag().split("/");
        //这里tag标签的倒数第二层为循环标签，且标签长度大于3
        for(int i=1;i<strAarry.length-2;i++){
            if(elem.element(strAarry[i])==null){
                elem = elem.addElement(strAarry[i]);
            }else{
                elem = elem.element(strAarry[i]);
            }
            if(i==strAarry.length-3){
                Element e = elem;
                for(Map<String,String> map:mapList){
                    Element eArray = e.addElement(strAarry[i+1]);
                    for(Field field:fieldList){
                        String temp = map.get(field.getName());
                        //tag最后一层为循环内标签，且只支持一层
                        String[] sArray = field.getTag().split("/");
                        if(temp!=null){
                            eArray.addElement(sArray[sArray.length-1]).setText(temp);
                        }else{
                            eArray.addElement(sArray[sArray.length-1]);
                        }
                    }
                }
            }
        }
    }
}
