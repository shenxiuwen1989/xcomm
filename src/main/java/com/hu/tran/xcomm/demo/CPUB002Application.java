package com.hu.tran.xcomm.demo;

import com.hu.tran.xcomm.core.PackMapper;
import com.hu.tran.xcomm.core.TargetMapper;
import com.hu.tran.xcomm.core.XCommService;
import lombok.extern.log4j.Log4j;

import java.net.URLDecoder;
import java.util.*;

/**
 * @author hutiantian
 * @create 2018/6/21 11:29
 * @since 1.0.0
 */
@Log4j
public class CPUB002Application {

    public static final String pack = "/pack";
    public static final String targetFile = "/TargetServer.xml";

    public static void main(String[] args) throws Exception{
        String packPath = URLDecoder.decode(CPUB002Application.class.getResource(pack).getPath(),"utf-8");
        if(!PackMapper.init(packPath)){
            return;
        }
        String targetPath = URLDecoder.decode(CPUB002Application.class.getResource(targetFile).getPath(),"utf-8");
        if(!TargetMapper.init(targetPath)){
            return;
        }
        Calendar now = Calendar.getInstance(TimeZone.getDefault());
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd");
        java.text.SimpleDateFormat sdfY = new java.text.SimpleDateFormat("yyyyMMddhhmmss");
        sdf.setTimeZone(TimeZone.getDefault());
        sdfY.setTimeZone(TimeZone.getDefault());

        Map<String,Object> sendMap = new HashMap<String, Object>();
        Map<String,Object> returnMap = new HashMap<String, Object>();
        sendMap.put("Mac","0000000000001");
        sendMap.put("MsgId","2bc7f1cc-3757-4381-b9c8-5ba787de39aa");
        sendMap.put("SourceSysId","200510");
        sendMap.put("ConsumerId","200510");
        sendMap.put("ServiceCode","02002000007");
        sendMap.put("ServiceScene","01");
        sendMap.put("TranDate",sdf.format(now.getTime()));
        sendMap.put("TranTime","115110");
        sendMap.put("TranTellerNo","0104");

        String requestNo = "200510" + sdf.format(now.getTime()) + getRandString(16, 1);
        sendMap.put("TranSeqNo",requestNo);
        sendMap.put("GlobalSeqNo",requestNo);

        sendMap.put("PushTm",sdfY.format(now.getTime()));
        sendMap.put("BtchNo","200510" + sdf.format(now.getTime())+ getRandString(2, 1));
        Map<String,String> map1  = new HashMap<String, String>();
        map1.put("FileNm","test1");
        map1.put("FileTp","B0206");//B0206 还款结果文件 B0207 理赔结果文件 B0208 还款计划文件
        map1.put("BsnAplyNo","200510" + sdf.format(now.getTime())+ getRandString(6, 1));
        map1.put("RcrdNum","1");
        ArrayList list = new ArrayList<Map<String,String>>();
        list.add(map1);
        sendMap.put("list",list);


        sendMap.put("FmSysInd","200510");
        sendMap.put("TrgtSysInd","300050");
        String result = XCommService.tran("CPUB002",sendMap,returnMap);
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
