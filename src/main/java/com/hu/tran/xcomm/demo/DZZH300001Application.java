package com.hu.tran.xcomm.demo;

import com.hu.tran.xcomm.common.Constant;
import com.hu.tran.xcomm.core.PackMapper;
import com.hu.tran.xcomm.core.TargetMapper;
import com.hu.tran.xcomm.core.XCommService;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

@Log4j
public class DZZH300001Application {
    public static final String pack = "/pack";
    public static final String targetFile = "/TargetServer.xml";

    public static void main(String[] args) throws Exception{
        String packPath = URLDecoder.decode(Test10010Application.class.getResource(pack).getPath(),"utf-8");
        if(!PackMapper.init(packPath)){
            return;
        }
        String targetPath = URLDecoder.decode(Test10010Application.class.getResource(targetFile).getPath(),"utf-8");
        if(!TargetMapper.init(targetPath)){
            return;
        }
        Map<String,Object> sendMap = new HashMap<String, Object>();
        Map<String,Object> returnMap = new HashMap<String, Object>();
        sendMap.put("firstsysname","");
        sendMap.put("firstsysmemucode","");
        sendMap.put("firstsysmemuname","");
        sendMap.put("firstsysdate","");
        sendMap.put("firstsystime","");
        sendMap.put("firstsysseq","");
        sendMap.put("requesttrancode","");
        sendMap.put("requestseq","ZXYH201807020000014511");
        sendMap.put("brno","");
        sendMap.put("tellerno","");
        sendMap.put("authtellerno","");
        sendMap.put("reviewtellrno","");
        sendMap.put("pageflag","");
        sendMap.put("currpage","");
        sendMap.put("pagenum","");
        sendMap.put("smssendyn","N");
        //私有域
        sendMap.put("accno","6231460599000008705");

        Map<String,String> constantMap  = new HashMap<String, String>();
        constantMap.put("xmlVer","01");
        constantMap.put("requestsysid","ZXYH");
        constantMap.put("servicesysid","DZZH");
        constantMap.put("trancode","DZZH300001");
        constantMap.put("msgSendDate","20180702");
        constantMap.put("msgSendTime","135210");
        constantMap.put("msgId","ZXYH201807020000014511");
        constantMap.put("msgRefId","");
        constantMap.put("direction","1");
        constantMap.put("reserve","");
        /*-----------------！私有域字段的name不能和constantMap重名，否则会被覆盖并导致异常！-----------------------------*/
        sendMap.put(Constant.constantMap,constantMap);              //若有定长字段填充，固定用constantMap来送
//        sendMap.put("ntwrkchkyn","Y");
//        sendMap.put("chkanswercd","123");
//        sendMap.put("chkanswerdesc","test");
        String result = XCommService.tran("300001",sendMap,returnMap);
        if(result.equals("0000")){              //通讯成功
            for(String str:returnMap.keySet()){
                System.out.println(str+": "+returnMap.get(str));
            }
        }
    }
}
