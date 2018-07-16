package com.hu.tran.xcomm.demo;

import com.hu.tran.xcomm.common.Constant;
import com.hu.tran.xcomm.core.PackMapper;
import com.hu.tran.xcomm.core.TargetMapper;
import com.hu.tran.xcomm.core.XCommService;
import lombok.extern.log4j.Log4j;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hutiantian
 * @create 2018/6/21 11:29
 * @since 1.0.0
 */
@Log4j
public class Application {

    public static final String pack = "/pack";
    public static final String targetFile = "/TargetServer.xml";

    public static void main(String[] args) throws Exception{
        String packPath = URLDecoder.decode(Application.class.getResource(pack).getPath(),"utf-8");
        if(!PackMapper.init(packPath)){
            return;
        }
        String targetPath = URLDecoder.decode(Application.class.getResource(targetFile).getPath(),"utf-8");
        if(!TargetMapper.init(targetPath)){
            return;
        }
        Map<String,Object> sendMap = new HashMap<String, Object>();
        Map<String,Object> returnMap = new HashMap<String, Object>();
        sendMap.put("Mac","0000000000001");
        sendMap.put("TargetSysId","300050");
        sendMap.put("MsgId","2bc7f1cc-3757-4381-b9c8-5ba787de39aa");
        sendMap.put("ServiceCode","10001");
        sendMap.put("ReturnMsg","交易成功");
        sendMap.put("TranDate","20180508");
        sendMap.put("TranTime","20180508");
        sendMap.put("GlobalSeqNo","3000501506240286ad09080063e7");
        Map<String,String> map1  = new HashMap<String, String>();
        map1.put("ip","123456");
        map1.put("host","123456");
        Map<String,String> map2  = new HashMap<String, String>();
        map2.put("ip","654321");
        map2.put("host","654321");
        ArrayList list = new ArrayList<Map<String,String>>();
        list.add(map1);
        list.add(map2);
        sendMap.put("list",list);
        Map<String,String> constantMap  = new HashMap<String, String>();
        constantMap.put("test1","abc");
        constantMap.put("test2","cba");
        /*-----------------！私有域字段的name不能和constantMap重名，否则会被覆盖并导致异常！-----------------------------*/
        sendMap.put(Constant.constantMap,constantMap);              //若有定长字段填充，固定用constantMap来送
        String result = XCommService.tran("10001",sendMap,returnMap);
        if(result.equals("0000")){              //通讯成功
            for(String str:returnMap.keySet()){
                System.out.println(str+": "+returnMap.get(str));
            }
        }
    }
}
