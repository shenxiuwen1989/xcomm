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
public class Test10010Application {

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
        sendMap.put("Mac","0000000000001");
        sendMap.put("MsgId","2bc7f1cc-3757-4381-b9c8-5ba787de39aa");
        sendMap.put("SourceSysId","200510");
        sendMap.put("ConsumerId","200510");
        sendMap.put("ServiceCode","02002000005");
        sendMap.put("ServiceScene","02");
        sendMap.put("TranDate","20180702");
        sendMap.put("TranTime","143710");
        sendMap.put("TranTellerNo","6427");
        Calendar now = Calendar.getInstance(TimeZone.getDefault());
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd");
        sdf.setTimeZone(TimeZone.getDefault());

        String requestNo = "200510" + sdf.format(now.getTime()) + getRandString(16, 3);
//        sendMap.put("TranSeqNo","3000501506240286ad09080063e8");
//        sendMap.put("GlobalSeqNo","3000501506240286ad09080063e8");
        sendMap.put("TranSeqNo",requestNo);
        sendMap.put("GlobalSeqNo",requestNo);
        sendMap.put("AuthrTellerNo","6427");
        sendMap.put("IdntTp","101");
        sendMap.put("IdentNo","110108195607175419");
        sendMap.put("CstNm","李龙云");
        sendMap.put("QryRsn","02");
        sendMap.put("QryFmtCd","30");
        sendMap.put("QryTp","0");
        sendMap.put("QryPolcyCd","2");
        sendMap.put("RsltTp","2");
        sendMap.put("GtFlMth","2");
        sendMap.put("IntfId","zhengxin_QuryCustCreditReport");
        sendMap.put("UsrKey","cc5b01fc9d4f4df3acde14dbd07d7493 ");
        sendMap.put("IntVrNo","1");
        String result = XCommService.tran("10010",sendMap,returnMap);
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
