package com.hu.tran.xcomm.core;

import com.hu.tran.xcomm.common.Field;
import com.hu.tran.xcomm.common.LengthInfo;
import com.hu.tran.xcomm.common.Pack;
import com.hu.tran.xcomm.common.Str;
import lombok.extern.log4j.Log4j;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 报文缓存对象
 * @author hutiantian
 * @create 2018/6/9 9:21
 * @since 1.0.0
 */
@Log4j
public class PackMapper {

    private static PackMapper packMapper;
    private static Map<String,Pack> packMap = new HashMap();;
    public static String configBaseDir = "";						//组件配置文件基础目录

    //私有无参构造方法
    private PackMapper(){}

    private PackMapper(String configFilePath){
        File[] fileList = new File(configFilePath).listFiles();
        if(fileList!=null&&fileList.length>0){
            boolean ret = true;
            for (File file:fileList) {
                Document doc = null;
                try{
                    SAXReader sax = new SAXReader();
                    doc = sax.read(file);
                }catch (Exception e){
                    ret = false;
                    log.error("读取服务方报文"+file.getPath()+"异常！",e);
                    break;
                }
                Pack pack = getPack(doc.getRootElement());
                if(pack==null){
                    ret = false;
                    log.error("加载服务方报文:"+file.getPath()+"异常！");
                    break;
                }
                log.debug("加载服务方报文:"+file.getPath()+"成功！");
                packMap.put(pack.getPackCode(), pack);
            }
            //记录配置文件基础路径
            configBaseDir = new File(new File(configFilePath).getAbsolutePath()).getParentFile().getAbsolutePath()
                    + File.separator;
            if(ret){
                packMapper = this;
            }
        }
    }

    /**
     * 解析报文对象
     */
    private Pack getPack(Element root){
        ArrayList<Field> requestList = getField(root,"Request");
        ArrayList<Field> responseList = getField(root,"Response");
        if(requestList==null||requestList.size()==0||responseList==null||responseList.size()==0){
            log.error("服务方报文的XML字段集合异常！");
            return null;
        }
        //加载长度信息
        LengthInfo lengthInfo = getLengthINfo(root,"LengthInfo");
        if(lengthInfo==null){
            log.error("服务方报文的XML报文长度信息异常！");
            return null;
        }
        Element packCode = root.element("PackCode");
        if(packCode==null){
            log.error("未配置PackCode字段！");
            return null;
        }
        Element desc = root.element("Desc");
        if(desc==null){
            log.error("未配置Desc字段！");
            return null;
        }
        Element target = root.element("Target");
        if(target==null){
            log.error("未配置Target字段！");
            return null;
        }
        Element logFlag = root.element("LogFlag");
        if(logFlag==null){
            log.error("未配置LogFlag字段！");
            return null;
        }
        String flag = logFlag.getText();
        if(!flag.equals("true")&&!flag.equals("false")){
            log.error("LogFlag字段的值只能为true或者false！");
            return null;
        }
        Element encoding = root.element("Encoding");
        if(encoding==null){
            log.error("未配置Encoding字段！");
            return null;
        }
        Element rootE = root.element("Root");
        if(rootE==null){
            log.error("未配置Root字段！");
            return null;
        }
        //加载定长字段信息
        ArrayList<Str> strList = null;
        Element constant = root.element("Constant");
        if(constant!=null){
            List<Element> strEle = constant.elements("Str");
            if(strEle!=null){
                strList = new ArrayList<Str>();
                for(Element el : strEle){
                    Str str = new Str();
                    String name = el.attributeValue("name");
                    if(name==null||name.equals("")){
                        log.error("定长字段name不允许为空！");
                        return null;
                    }
                    str.setName(name);
                    String length = el.attributeValue("len");
                    if(length==null||length.equals("")){
                        log.error("定长字段name不允许为空！");
                        return null;
                    }
                    int len = 0;
                    try{
                        len = Integer.parseInt(length);
                    }catch(Exception e){
                        log.error("定长字段len配置错误"+e);
                        return null;
                    }
                    str.setLen(len);
                    String sub = el.attributeValue("sub");
                    if(sub==null||sub.length()!=1){
                        log.error("定长字段sub不允许为空,且补充字段只能为单个字符！");
                        return null;
                    }
                    str.setSub(sub);
                    strList.add(str);
                }
            }
        }
        Pack pack = new Pack();
        pack.setPackCode(packCode.getText());
        pack.setDesc(desc.getText());
        pack.setTarget(target.getText());
        pack.setLogFlag(Boolean.parseBoolean(flag));
        pack.setEncoding(encoding.getText());
        pack.setRoot(rootE.getText());
        pack.setRequestList(requestList);
        pack.setResponseList(responseList);
        pack.setLengthInfo(lengthInfo);
        pack.setConstant(strList);
        return pack;
    }

    /**
     *  解析请求、响应描述字段
     */
    private ArrayList<Field> getField(Element root,String listName){
        ArrayList<Field> list = new ArrayList<Field>();
        Element request = root.element(listName);
        if(request==null){
            log.error("服务方报文未配置"+listName+"标签！");
            return null;
        }
        List<Element> elist = request.elements("Field");
        if(elist==null||elist.size()==0){
            log.error("获取服务方报文"+listName+"的Field标签失败！");
            return null;
        }
        for(Element element:elist){
            Field field = new Field();
            String name = element.attributeValue("name");
            if(name==null||name.equals("")){
                log.error(listName+"未配置[name]字段或名称为空！");
                return null;
            }
            field.setName(name);
            String desc = element.attributeValue("desc");
            if(desc==null||desc.equals("")){
                log.error(name+"标签未配置[desc]字段或描述为空！");
                return null;
            }
            field.setDesc(desc);
            String loop = element.attributeValue("loop");
            if(loop==null){
                log.error(name+"标签未配置[loop]字段！");
                return null;
            }
            field.setLoop(loop);
            String tag = element.attributeValue("tag");
            if(tag==null||tag.equals("")){
                log.error(name+"标签未配置[tag]字段或值为空！");
                return null;
            }
            field.setTag(tag);
            list.add(field);
        }
        return list;
    }

    private LengthInfo getLengthINfo(Element root,String eleName){
        Element request = root.element(eleName);
        if(request==null){
            log.error("服务方报文未配置"+eleName+"标签！");
            return null;
        }
        Element enable = request.element("Enable");
        if(enable==null){
            log.error("服务方报文lengthInfo未配置Enable标签！");
            return null;
        }
        Element infoLen = request.element("InfoLen");
        if(enable==null){
            log.error("服务方报文lengthInfo未配置InfoLen标签！");
            return null;
        }
        Element selfFlag = request.element("SelfFlag");
        if(enable==null){
            log.error("服务方报文lengthInfo未配置SelfFlag标签！");
            return null;
        }
        Element format = request.element("Format");
        if(enable==null){
            log.error("服务方报文lengthInfo未配置Format标签！");
            return null;
        }
        LengthInfo lengthInfo = new LengthInfo();
        lengthInfo.setEnable(Boolean.parseBoolean(enable.getText()));
        lengthInfo.setInfoLen(Integer.parseInt(infoLen.getText()));
        lengthInfo.setSelfFlag(Boolean.parseBoolean(selfFlag.getText()));
        lengthInfo.setFormat(Integer.parseInt(format.getText()));
        Element returnLen = request.element("ReturnLen");
        if(returnLen==null){
            lengthInfo.setReturnLen(0);
        }else {
            lengthInfo.setReturnLen(Integer.parseInt(returnLen.getText()));
        }
        return lengthInfo;
    }

    /**
     * 初始化实例对象
     */
    public static boolean init(String configFilePath){
        if(packMapper==null){
            new PackMapper(configFilePath);
        }
        if(packMapper==null){
            log.debug("报文缓存失败！");
            return false;
        }else {
            log.debug("报文缓存成功！");
            return true;
        }
    }

    /**
     * 获取实例化对象
     */
    public static PackMapper getInstance(){
        return packMapper;
    }

    /**
     * 获取报文对象
     */
    public Pack getPack(String packCode){
        if(null != packCode && !"".equals(packCode) && packMap.containsKey(packCode)) {
            return packMap.get(packCode);
        } else {
            return null;
        }
    }
}
