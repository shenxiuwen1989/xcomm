package com.hu.tran.xcomm.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ExcelUtils {

    //根据excel生成对应的xml信息
    public void getFromExcel(File file){
        try{
            // 创建输入流，读取Excel
            InputStream is = new FileInputStream(file.getAbsolutePath());
            Workbook workbook = new XSSFWorkbook(is);
            //从第1行开始读取
            Sheet sheet = workbook.getSheetAt(0);
            //获取excel总行数
            int totalRows = sheet.getPhysicalNumberOfRows();
            Map<String,String> mapXml = new HashMap<String, String>();
            StringBuffer sb = new StringBuffer();
            for (int i = 0;i< totalRows;i++){
                //获取每一行的数据
                Row row = sheet.getRow(i);
                Cell name = row.getCell(0);
                Cell desc = row.getCell(1);
                Cell status = row.getCell(2);
                String statu = status + "";
                if (!"1.0".equals(statu.trim())) {
                    if ("".equals(status)||status==null){
                        sb.append("<Field name = \"" + name + "\" desc = \"" + desc + "\" loop=\"\"  tag = \"Document/Content/" + name + "\"/>" + "\n");
                    }else{
                        sb.append("<Field name = \"" + name + "\" desc = \"" + desc + "\" loop=\""+ status +"\"  tag = \"Document/Content/" +status + "/" + name + "\"/>" + "\n");
                    }
                }
            }
            System.out.println(sb);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getMapFromExcel(File file){
        try{
            // 创建输入流，读取Excel
            InputStream is = new FileInputStream(file.getAbsolutePath());
            Workbook workbook = new XSSFWorkbook(is);
            //从第1行开始读取
            Sheet sheet = workbook.getSheetAt(0);
            //获取excel总行数
            int totalRows = sheet.getPhysicalNumberOfRows();
            Map<String,String> mapXml = new HashMap<String, String>();
            StringBuffer sb = new StringBuffer();
            for (int i = 0;i< totalRows;i++){
                //获取每一行的数据
                Row row = sheet.getRow(i);
                Cell name = row.getCell(0);
                Cell desc = row.getCell(1);
                Cell status = row.getCell(2);
                String statu = status + "";
                if ("".equals(statu.trim())||status==null) {
                    sb.append("sendMap.put(\"" + name + "\",\"\");" + "\n");
                } else {
                    sb.append("sendMap.put(\"" + name + "\",\"\");//TODO" + "\n");
                }
            }
            System.out.println(sb);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ExcelUtils obj = new ExcelUtils();
        // 此处为我创建Excel路径：E:/zhanhj/studysrc/jxl下
//        File fileXml = new File("C:/Users/DELL111/Desktop/test.xlsx");
        File fileXml = new File("D:/work/xztech/九江-信真-趣店消费信贷/接口/九江信贷系统/联调报文/wenjianchuanshutongzhi.xlsx");

        File fileMap = new File("D:/work/xztech/九江-信真-趣店消费信贷/接口/九江信贷系统/联调报文/wenjianchuanshutongzhi.xlsx");
        obj.getFromExcel(fileXml);
        System.out.println("----------------------------------------------------------");
        obj.getMapFromExcel(fileMap);
    }
}
