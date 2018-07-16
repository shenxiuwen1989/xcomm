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
public class CPAY001Application {

    public static final String pack = "/pack";
    public static final String targetFile = "/TargetServer.xml";

    public static void main(String[] args) throws Exception{
        String packPath = URLDecoder.decode(CPAY001Application.class.getResource(pack).getPath(),"utf-8");
        if(!PackMapper.init(packPath)){
            return;
        }
        String targetPath = URLDecoder.decode(CPAY001Application.class.getResource(targetFile).getPath(),"utf-8");
        if(!TargetMapper.init(targetPath)){
            return;
        }
        Map<String,Object> sendMap = new HashMap<String, Object>();
        Map<String,Object> returnMap = new HashMap<String, Object>();
        sendMap.put("Mac","0000000000001");
        sendMap.put("MsgId","2bc7f1cc-3757-4381-b9c8-5ba787de39aa");
        sendMap.put("SourceSysId","200510");
        sendMap.put("ConsumerId","200510");
        sendMap.put("ServiceCode","02001000001");
        sendMap.put("ServiceScene","02");
        sendMap.put("TranDate","20180702");
        sendMap.put("TranTime","152510");
        sendMap.put("TranTellerNo","0104");
        Calendar now = Calendar.getInstance(TimeZone.getDefault());
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd");
        sdf.setTimeZone(TimeZone.getDefault());

        String requestNo = "200510" + sdf.format(now.getTime()) + getRandString(16, 1);
//        sendMap.put("TranSeqNo","3000501506240286ad09080063e8");
//        sendMap.put("GlobalSeqNo","3000501506240286ad09080063e8");
        sendMap.put("TranSeqNo",requestNo);
        sendMap.put("GlobalSeqNo",requestNo);
        sendMap.put("AuthrTellerNo","0104");
        sendMap.put("CstNm","李龙云");
        sendMap.put("IdntTp","101");
        sendMap.put("IdentNo","110108195607175419");
        sendMap.put("SumchkInd","");
        sendMap.put("CstMgrNo","");
        sendMap.put("CstMgrInstNo","");
        sendMap.put("CrCstNo","");
        sendMap.put("CrExnCstFlg","");
        sendMap.put("ThrdPartyCstFlg","");
        sendMap.put("ThrdPartyCstTp","");
        sendMap.put("GndInd","");
        sendMap.put("BirthDate","");
        sendMap.put("IdentExpDt","20280630");
        sendMap.put("NtntyCd","");
        sendMap.put("Nation","");
        sendMap.put("MarriageCd","");
        sendMap.put("FrmrFlg","");
        sendMap.put("HshdRgstKnd","");
        sendMap.put("DomcLo","");
        sendMap.put("LclPolcStnNm","");
        sendMap.put("HighEdct","");
        sendMap.put("DgrCd","");
        sendMap.put("OcpCd","");
        sendMap.put("PosTtlCd","");
        sendMap.put("PosCd","");
        sendMap.put("HltSt","");
        sendMap.put("PstKnd","");
        sendMap.put("CorpNm","");
        sendMap.put("OffcTel","");
        sendMap.put("CoWrkTrm","");
        sendMap.put("FamPpnNum","");
        sendMap.put("FamAdr","");
        sendMap.put("HsTp","");
        sendMap.put("MblNo","");
        sendMap.put("IdyInvlCd","");
        sendMap.put("ImgTplCd","");
        sendMap.put("ImgBsnNo","");
//        sendMap.put("RltPrsnInfArray","");//TODO
//        sendMap.put("RltPrsnNm","");//TODO
//        sendMap.put("CstTp","");//TODO
//        sendMap.put("IdntTp","101");//TODO
//        sendMap.put("IdentNo","");//TODO
//        sendMap.put("RltnpTp","");//TODO
//        sendMap.put("Rmk","");//TODO
//        sendMap.put("AplyAmt","");//TODO
//        sendMap.put("RltPrsnInfArray","");//TODO
//        sendMap.put("CrInfArray","");//TODO
//        sendMap.put("SrcNm","");//TODO
//        sendMap.put("QryDt","");//TODO
//        sendMap.put("CrCardCtnsOdueTms","");//TODO
//        sendMap.put("CrCardAcmOdueTms","");//TODO
//        sendMap.put("LoanCtnsOdueTms","");//TODO
//        sendMap.put("LoanAcmOdueTms","");//TODO
//        sendMap.put("CrExnPnpOdueDsc","");//TODO
//        sendMap.put("CrInfArray","");//TODO
        sendMap.put("CtrNo","");
        sendMap.put("CoprBsnNo","");
        sendMap.put("BsnTp","02006014");
        sendMap.put("CtrAmt","100");
        sendMap.put("CtrIttDt","20180423");
        sendMap.put("CtrExpDt","20210430");
        sendMap.put("DbtAmt","");
        sendMap.put("DbtExpDt","");
        sendMap.put("LoanPpsDsc","贷款");
        sendMap.put("IntRate","20");
        sendMap.put("IntSetlMth","4");
        sendMap.put("LndCardPsbkFlag","");
        sendMap.put("LndOpenAcctBnkNo","");
        sendMap.put("LndOpenAcctBnkNm","");
        sendMap.put("LndAcctNo","727100100100006842");
        sendMap.put("LndAcctNm","华林******公司");
        sendMap.put("RpymntCardPsbkFlag","");
        sendMap.put("RpymntOpenAcctBnkNo","");
        sendMap.put("RpymntOpenAcctBnkNm","");
        sendMap.put("RepymtAcctNo","");
        sendMap.put("RepymtAcctNm","");
        sendMap.put("SplmtInf","");
        sendMap.put("RepymtMth","");
        String result = XCommService.tran("CPAY001",sendMap,returnMap);
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
