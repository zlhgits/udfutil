package com.zlh.udf.hive.ua;

import java.text.SimpleDateFormat;
import java.util.Date;

/** 解析移动端的UA信息
 * Author zlh
 * Date 2019-01-03
 * Version 1.0
 */
public class UAUdf {
    private static final String[] arrOSCN="安卓,IOS,WinPhone,黑莓,塞班,PC,其它".split(",");//0安卓,1IOS,2WinPhone,3黑莓,4诺基亚,5PC,6其它
    private static final String[] arrOSEN = "Android,Mac OS,Windows Phone,BlackBerry,Symbian,Windows NT".split(",");
    private static final String[] arrBrandCN="三星,苹果,小米,华为,魅族,联想,OPPO,vivo,酷派,诺基亚,摩托罗拉,索尼,HTC,飞利浦,一加,黑莓,金立,中兴,LG,TCL,微软,天语,锤子,海信,康佳,朵唯,米智,聆韵,波导,大显,索爱,爱我,酷比,长虹,海尔,自由客,诺亚信,纽曼,金来,果米,努比亚,乐视,奇酷".split(",");
    private static final String[] arrBrandEN="Samsung,Apple,Xiaomi,HUAWEI,MEIZU,Lenovo,oppo,vivo,Coolpad,NOKIA,Motorola,Sony,htc,PHILIPS,ONEPLUS,BlackBerry,Gionee,ZTE,LG,TCL,Microsoft,K-Touch,smartisan,Hisense,KONKA,DOOV,MiZhi,Lingwin,BiRD,DaXian,Ericsson,lovme,koobee,CHANGHONG,Haier,ZUK,NOAIN,Newman,goly,GUOMI,nubia,Letv,qiku".split(",");
    private static final String[] arrPhoneModel = "Samsung,SM-,GT-,Galaxy,Nexus,SCH-,SGH;iPhone,iPad,iPod,Macintosh;MI UI,Redmi,HM,m1 note,m2 note,MI ,MI-;PE-,H30,H60,Hol,HUAWEI,Huawei,HW-HUAWEI,-CL00,Che,Honor,PLK; MX,M35;Lenovo,Lenovo_;oppo,X90;vivo;Coolpad;NOKIA;Motorola,XT;Sony, SO,L3,L5,XM,MT27,LT2;HTC,htc,M6,M7,M8,M9,HTL;PHILIPS;ONEPLUS;BlackBerry;Gionee,GN;ZTE;LG;TCL;Microsoft;K-Touch;smartisan;Hisense,HS-;KONKA;DOOV;MiZhi;Lingwin;BiRD;DaXian;Ericsson;lovme;koobee;changhong;Haier,HW-W;ZUK;NOAIN;Newman;goly;GUOMI;nubia, NX;Letv;qiku".split(";");
    private static final String[] arrLanguageCN="简体(中国),简体(中国),简体(新加坡),繁体(香港),繁体(台湾),英语(香港),英语(美国),英语(英国),英语(加拿大),日语版".split(",");
    private static final String[] arrLanguage="zh-cn,zh_cn,zh-sg,zh-hk,zh-tw,en-hk,en-us,en-gb,en-ca,ja-jp".split(",");

    public static void main(String[] args) {
        String ua= "Mozilla/4.0 (compatible; MSIE 4.01; Windows CE)";
        //String pram="version";
        String pram="os";
        System.out.println(UAUdf.getUmPram(ua,pram));
    }

    public static String getUmPram(String ua,String pram){
        String arrmd[] = null;
        String arr[] = null;
        String tmp = "";
        String version = "";
        int iret = 0;
        int ibegin = 0;
        int iend = 0;
        if(ua==null||ua.trim().equals("")){
            return "无UA";
        }

        if ("os".equals(pram)) {
            String osys = "未识别";
            try {
                if(ua.contains("ARM")||ua.contains("NOKIA")||ua.contains("IEMobile")){
                    return arrOSCN[2];   //系统
                }else{
                    // 判断手机型号，并反向获取品牌
                    String bra = "";
                    for (int i = 0; i < arrPhoneModel.length; i++) {
                        arrmd = arrPhoneModel[i].split(",");
                        for (int j = 0; j < arrmd.length; j++) {
                            iret = ua.toLowerCase().indexOf(arrmd[j].toLowerCase());
                            if (iret >= 0) {
                                bra = arrBrandCN[i];
                                break;
                            }
                        }
                    }
                    // 判断os
                    for (int i = 0; i < arrOSEN.length; i++) {
                        iret = ua.indexOf(arrOSEN[i]);
                        if (iret >= 0) {
                            osys = arrOSCN[i];
                            break;
                        } else if (ua.indexOf("iOS") >= 0) {
                            osys = arrOSCN[1];
                            break;
                        }
                    }
                    if(!bra.equals("苹果")){//苹果系统
                        osys = "安卓";
                    }
                }
            } catch (Exception e) {
                showinfo(e.getMessage());
                e.printStackTrace();
            }
            return osys;
        }else if ("brand".equals(pram)) {
            String bra = "未识别";
            try {
                if(ua.contains("ARM")||ua.contains("NOKIA")||ua.contains("IEMobile")){
                    bra = "诺基亚";   //品牌
                }else{
                    // 判断手机型号，并反向获取品牌
                    for (int i = 0; i < arrPhoneModel.length; i++) {
                        arrmd = arrPhoneModel[i].split(",");
                        for (int j = 0; j < arrmd.length; j++) {
                            iret = ua.toLowerCase().indexOf(arrmd[j].toLowerCase());
                            if (iret >= 0) {
                                bra = arrBrandCN[i];
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                showinfo(e.getMessage());
                e.printStackTrace();
            }
            return bra;
        }else if ("phonemodel".equals(pram)) {
            String pm = "未识别";
            try {
                if(ua.contains("ARM")||ua.contains("NOKIA")||ua.contains("IEMobile")){
                    ibegin = ua.indexOf("NOKIA");
                    if(ibegin>0){
                        tmp = ua.substring(ibegin+5);
                        if(tmp.startsWith(";")){
                            tmp=tmp.substring(1);
                        }
                        iend = tmp.indexOf(")");
                        if(iend>0){
                            pm = tmp.substring(0, iend).trim();//型号
                        }
                    }
                }else{
                    // 判断手机型号，并反向获取品牌
                    for (int i = 0; i < arrPhoneModel.length; i++) {
                        arrmd = arrPhoneModel[i].split(",");
                        for (int j = 0; j < arrmd.length; j++) {
                            iret = ua.toLowerCase().indexOf(arrmd[j].toLowerCase());
                            if (iret >= 0) {
                                String bra = arrBrandCN[i];      //手机品牌
                                tmp = ua.substring(iret);
                                if (tmp.toLowerCase().contains("build")) {
                                    tmp = tmp.substring(0, tmp.toLowerCase().indexOf("build")).trim();
                                    if (ua.contains(arrBrandEN[i])) {
                                        tmp = tmp.replaceAll(arrBrandEN[i], "")
                                                .replaceAll("\\)", "");
                                    }
                                } else if (tmp.contains("MIUI")) {
                                    tmp = tmp.substring(0, tmp.indexOf("MIUI")).trim();
                                } else if (tmp.contains("Mac OS X")) {
                                    if (tmp.contains("iPhone")) {
                                        tmp = "iPhone";
                                    } else if (tmp.contains("iPad")) {
                                        tmp = "iPad";
                                    } else if (tmp.contains("iPod")) {
                                        tmp = "iPod";
                                    } else if (tmp.contains("Macintosh")) {
                                        tmp = "Macintosh";
                                    }
                                    ibegin = ua.indexOf("OS X");///改
                                    iend = ua.indexOf("like");

                                } else {
                                    tmp = getSubString(tmp);
                                }
                                tmp = getSubString(tmp);  //型号
                                if(tmp.length()>9&&tmp.contains("SogouMSE,")){
                                    tmp = tmp.substring(9);
                                }
                                //排除HTC手机的型号
                                if(!bra.equals("HTC")){//HTC品牌排除型号
                                    arr=tmp.split(" ");
                                    if(arr.length>2){
                                        tmp = arr[0]+arr[1];
                                    }
                                }
                                if(tmp.length()>50){
                                    tmp=tmp.substring(0, 49);
                                }
                                pm = tmp.trim();
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                showinfo(e.getMessage());
                e.printStackTrace();
            }
            return pm;
        }else if ("version".equals(pram)) {
            String vers = "未识别";
            try {
                //判断是否为Windows phone
                if(ua.contains("ARM")||ua.contains("NOKIA")||ua.contains("IEMobile")){
                    //获取版本信息
                    ibegin = ua.indexOf("Windows Phone");
                    if (ibegin > 0) {
                        version=ua.substring(ibegin+13);
                        iend = version.indexOf(";");
                        version = version.substring(0,iend).trim();
                        return version;
                    }
                }else{
                    // 判断手机型号，并反向获取品牌
                    for (int i = 0; i < arrPhoneModel.length; i++) {
                        arrmd = arrPhoneModel[i].split(",");
                        for (int j = 0; j < arrmd.length; j++) {
                            iret = ua.toLowerCase().indexOf(arrmd[j].toLowerCase());
                            if (iret >= 0) {
                                tmp = ua.substring(iret);
                                if (tmp.toLowerCase().contains("build")) {
                                    tmp = tmp.substring(0, tmp.toLowerCase().indexOf("build")).trim();
                                    if (ua.contains(arrBrandEN[i])) {
                                        tmp = tmp.replaceAll(arrBrandEN[i], "")
                                                .replaceAll("\\)", "");
                                    }
                                } else if (tmp.contains("MIUI")) {
                                } else if (tmp.contains("Mac OS X")) {
                                    ibegin = ua.indexOf("OS X");
                                    iend = ua.indexOf("like");
                                    if (ibegin > 0 && iend > 0 && iend > ibegin) {
                                        version = ua.substring(ibegin + 4, iend).trim();
                                        version = version.replaceAll("_", ".");
                                        iend = version.indexOf(")");
                                        if(iend>0){
                                            version = version.substring(0, iend).trim();
                                        }
                                    } else {
                                        ibegin = ua.indexOf("Mac OS X");
                                        if (ibegin > 0) {
                                            version = ua.substring(ibegin + 8);
                                            iend = version.indexOf(";");
                                            if (iend > 0) {
                                                version = version.substring(0, iend)
                                                        .trim();
                                            }
                                            if (version.startsWith(";")) {
                                                version = version.substring(1);
                                            }
                                            ibegin = ua.indexOf("OS");
                                            iend = ua.indexOf("like");
                                            if(iend>ibegin){
                                                version = ua.substring(ibegin+2, iend);
                                            }
                                            version = version.replaceAll("_", ".");
                                        }
                                    }
                                }
                                if (version.equals("")) {
                                    ibegin = ua.toLowerCase().indexOf("android");
                                    if (ibegin > 0) {
                                        version = ua.substring(ibegin + 7).trim();
                                        //把内容前端的分号去掉（部分机型会存在这种情况）
                                        if (version.startsWith(";")) {
                                            version = version.substring(1);
                                        }
                                        version = getSubString(version);
                                    }
                                }
                                if(version.length()>10){
                                    version.substring(0, 9);
                                }
                                ibegin = version.indexOf("OS");
                                iend = version.indexOf(",");
                                if(iend>ibegin){
                                    version = version.substring(ibegin + 2, iend);
                                }
                                version = version.replaceAll("_", ".");
                                version = getSubString(version);
                                if(!version.contains(".")){
                                    version = "未识别";
                                }
                                vers = version;  //版本
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                showinfo(e.getMessage());
                e.printStackTrace();
            }
            if(vers.contains("%")){//版本
                vers = "未识别";
            }
            return vers;
            //语言
        }else if ("language".equals(pram)) {
            String langu = "未识别";
            try {
                // 判断语言
                for (int i = 0; i < arrLanguage.length; i++) {
                    iret = ua.toLowerCase().indexOf(arrLanguage[i]);
                    if (iret >= 0) {
                        langu = arrLanguageCN[i];
                        break;
                    }
                }
            } catch (Exception e) {
                showinfo(e.getMessage());
                e.printStackTrace();
            }
            return langu;
            //浏览器
        }else if ("browser".equals(pram)) {
            String brow = "未识别";
            try {
                String uaBrowser = ua.toLowerCase();
                int indexUA = uaBrowser.indexOf("gecko)");
                String substring = uaBrowser.substring(indexUA+7);
                String subUA = substring.replaceAll("[^a-zA-Z]+", "");
                if(uaBrowser.contains("mozilla")){
                    if (uaBrowser.contains("miui")) {
                        if(uaBrowser.contains("miuibrowser")) {
                            brow = "小米自带浏览器";
                        }else if (uaBrowser.contains("miuivideo")){
                            brow = "小米视频";
                        }
                    }else if(subUA.contains("oppobrowser")){
                        brow = "OPPO自带浏览器";
                    }else if(subUA.contains("CriOS")){
                        brow = "Chrome浏览器";
                    }else if(uaBrowser.contains("firefox")){
                        brow = "火狐浏览器";
                    }else if(uaBrowser.contains("mxios") || uaBrowser.contains("mxbrowser")){
                        brow = "遨游浏览器";
                    }else if(subUA.contains("360aphonebrowser")){
                        brow = "360浏览器";
                    }else if(subUA.contains("hao123")){
                        brow = "hao123浏览器";
                    }else if(uaBrowser.contains("opera")){
                        brow = "欧朋浏览器";
                    }else if(subUA.contains("mqqbrowser")){
                        brow = "QQ浏览器";
                    }else if(uaBrowser.contains("ucweb") || subUA.contains("ucbrowser")){
                        brow = "UC浏览器";
                    }else if(subUA.contains("sogoumobilebrowser") || uaBrowser.contains("metasr")){
                        brow = "搜狗浏览器";
                    }else if(subUA.contains("baidubrowser")){
                        brow = "百度浏览器";
                    }else if(uaBrowser.contains("android")){
                        if(subUA.equals("versionmobilesafari") || subUA.equals("versionchromemobilesafari") || subUA.equals("versionsafari")){
                            brow = "安卓自带浏览器";
                        }else if(subUA.equals("chromemobilesafari")){
                            brow = "Chrome浏览器";
                        }
                    }else if(uaBrowser.contains("iphone")){
                        if(subUA.equals("versionmobilesafari") || subUA.contains("mobile")){
                            brow = "Safari浏览器";
                        }
                    }else if(uaBrowser.contains("macintosh")){
                        if(subUA.equals("chromesafari")){
                            brow = "Safari浏览器";
                        }else if(subUA.equals("versionsafari")){
                            brow = "Chrome浏览器";
                        }
                    }else if(uaBrowser.contains("windows nt")){
                        if(subUA.equals("chromesafariedge") || uaBrowser.contains("msie")){
                            brow = "IE浏览器";
                        }else if(subUA.equals("chromesafari")){
                            brow = "Chrome浏览器";
                        }
                    }else if(uaBrowser.contains("blackberry")){
                        if(subUA.equals("versionmobilesafari")){
                            brow = "黑莓自带浏览器";
                        }
                    }else if(uaBrowser.contains("hp-tablet")){
                        if(subUA.equals("wosbrowsersafaritouchpad")){
                            brow = "HPTouchPad自带浏览器";
                        }
                    }else if(uaBrowser.contains("blackberry")){
                        if(subUA.equals("versionmobilesafari")){
                            brow = "黑莓自带浏览器";
                        }
                    }else if(uaBrowser.contains("symbianos")){
                        if(subUA.equals("browserng")){
                            brow = "Nokia自带浏览器";
                        }
                    }else if(uaBrowser.contains("windows phone")){
                        if(uaBrowser.contains("iemobile")){
                            brow = "Windows Phone浏览器";
                        }
                    }
                }else{
                    brow = "非浏览器";
                }
            } catch (Exception e) {
                showinfo(e.getMessage());
                e.printStackTrace();
            }
            return brow;
        }else{
            return "pram参数有误";
        }
    }

    private static String getSubString(String str){
        String arr[]=";| |,|(|)| B|/".split("|");
        int iend = 0;
        for(int i=0;i<arr.length;i++){
            iend = str.indexOf(arr[i]);
            if(iend>0){
                str = str.substring(0, iend);
            }
        }
        return str.trim();
    }

    private static void showinfo(String content){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(sdf.format(new Date ())+" "+content);
    }
}
