package com.hu.tran.xcomm.demo;

import com.hu.tran.xcomm.common.Constant;
import com.hu.tran.xcomm.core.PackMapper;
import com.hu.tran.xcomm.core.TargetMapper;
import com.hu.tran.xcomm.core.XCommService;
import lombok.extern.log4j.Log4j;

import java.net.URLDecoder;
import java.util.*;

@Log4j
public class DZZH300022Application {
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

        Calendar now = Calendar.getInstance(TimeZone.getDefault());
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd");
        sdf.setTimeZone(TimeZone.getDefault());

        Map<String,Object> sendMap = new HashMap<String, Object>();
        Map<String,Object> returnMap = new HashMap<String, Object>();
        sendMap.put("firstsysname","");
        sendMap.put("firstsysmemucode","");
        sendMap.put("firstsysmemuname","");
        sendMap.put("firstsysdate","");
        sendMap.put("firstsystime","");
        sendMap.put("firstsysseq","");
        sendMap.put("requesttrancode","");
        sendMap.put("requestseq","ZXYH201807030000014513");
        sendMap.put("brno","");
        sendMap.put("tellerno","");
        sendMap.put("authtellerno","");
        sendMap.put("reviewtellrno","");
        sendMap.put("pageflag","");
        sendMap.put("currpage","");
        sendMap.put("pagenum","");
        sendMap.put("smssendyn","N");
        sendMap.put("xmlplatformnbr","1");
        //私有域
        sendMap.put("exttxnid","ZXYH201807020000024524");
        sendMap.put("exttxndate","20180702");

        Map<String,String> constantMap  = new HashMap<String, String>();
        constantMap.put("xmlVer","01");
        constantMap.put("requestsysid","ZXYH");
        constantMap.put("servicesysid","DZZH");
        constantMap.put("trancode","DZZH300022");
        constantMap.put("msgSendDate","20180703");
        constantMap.put("msgSendTime","135210");
        constantMap.put("msgId","ZXYH201807030000014513");
        constantMap.put("msgRefId","");
        constantMap.put("direction","1");
        constantMap.put("reserve","");
        /*-----------------！私有域字段的name不能和constantMap重名，否则会被覆盖并导致异常！-----------------------------*/
        sendMap.put(Constant.constantMap,constantMap);              //若有定长字段填充，固定用constantMap来送
        String result = XCommService.tran("DZZH300022",sendMap,returnMap);
        if(result.equals("0000")){              //通讯成功
            for(String str:returnMap.keySet()){
                System.out.println(str+": "+returnMap.get(str));
            }
        }
    }

    /**
     * 根据长度与类型生成随机字符串
     * @param length 长度
     * @param type 类型(1：纯数字；2：纯字母；3：数字字母混合)
     * @return
     */
    public static String getRandString(int length, int type){
        String retStr = "";
        String strTable = "";
        if(type == 1)
        {
            strTable = "123456789";
        }
        else if(type == 2)
        {
            strTable = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz";
        }
        else if(type == 3)
        {
            strTable = "123456789ABCDEFGHJKLMN123456789PQRSTUVWXYZabcde123456789fghijkmnpqrs123456789tuvwxyz";
        }
        Random r = new Random();
        for (int i = 0; i < length; i++)
        {
            int rand = r.nextInt(strTable.length());
            int n = rand > 1? rand - 1 : rand;
            retStr += strTable.toCharArray()[n];
        }
        return retStr;
    }
}
