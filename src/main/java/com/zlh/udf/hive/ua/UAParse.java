package com.zlh.udf.hive.ua;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;

/**
 * Author zlh
 * Date 2019-01-17
 * Version 1.0
 */
public class UAParse extends UDF {
    public Text evalueate( String ua,String pram ){
        String filed;
        if(ua==null||ua.trim().equals("")){
            filed = "无UA" ;
        }else if ("os".equals(pram)) {
            filed = UAUtil.getOS ( ua ) ;
        }else if ("brand".equals(pram)) {
            filed = UAUtil.getBrand ( ua ) ;
        }else if ("phonemodel".equals(pram)) {
            filed = UAUtil.getPhonemodel ( ua );
        }else if ("version".equals(pram)) {
            filed = UAUtil.getVersion ( ua ) ;
        }else if ("language".equals(pram)) {
            filed = UAUtil.getLanguage ( ua ) ;
        }else if ("browser".equals(pram)) {
            filed = UAUtil.getBrowser ( ua ) ;
        }else{
            filed = "pram参数有误";
        }
        return new Text ( filed );
    }

    public static void main(String[] args) {
        String ua= "Mozilla/4.0 (compatible; MSIE 4.01; Windows CE)";
//        String pram="brand";//未识别
        String pram="os";//安卓
        System.out.println(new UAParse().evalueate(ua,pram));
    }
}
