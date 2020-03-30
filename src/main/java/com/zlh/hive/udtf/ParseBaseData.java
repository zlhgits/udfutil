package com.zlh.hive.udtf;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDTF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;

import java.util.ArrayList;

/**
 * -- 添加udtf
 * add jar /root/behave/XXXXX.jar;
 * -- 创建函数
 * create temporary function get_parseBase as 'com.zlh.hive.udtf.ParseBaseData';
 * -- lateral view方式使用
 * select t1.pk,t2.userid,t2.deviceid,t2.date_time,t2.device,t2.vender,t2.os,t1.score,t1.judge,t1.fact
 * from ods_behave_device_di t1
 * lateral view get_parseBase(t1.base) t2 as userid,deviceid,date_time,device,vender,os;
 *
 * Author zlh
 * Package com.dacheng.udtf
 * Date 2019/12/31
 */
public class ParseBaseData extends GenericUDTF {
    @Override
    public StructObjectInspector initialize(StructObjectInspector argOIs) throws UDFArgumentException {
        if (argOIs.getAllStructFieldRefs().size() != 1){
            throw new UDFArgumentException("There can only be one param");
        }
        //字段
        ArrayList<String> fieldname = new ArrayList<String>(6);
        fieldname.add("userid");
        fieldname.add("deviceid");
        fieldname.add("date_time");
        fieldname.add("device");
        fieldname.add("vender");
        fieldname.add("os");
        //对应字段类型
        ArrayList<ObjectInspector> fieldio = new ArrayList<ObjectInspector>(6);
        fieldio.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
        fieldio.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
        fieldio.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
        fieldio.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
        fieldio.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
        fieldio.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);

        StructObjectInspector soi = ObjectInspectorFactory.getStandardStructObjectInspector(fieldname,fieldio);
        return soi;
    }

    public void process(Object[] objects) throws HiveException {
        //如果参数传递多了或不传都不做解析
        if(objects.length == 1){
            String obj = objects[0].toString();
            try {
                super.forward(parseBaseJson(obj));
            }catch (Exception e){
                throw new HiveException(e);
            }
        }
    }

    @Override
    public void close() throws HiveException {}

    /**
     * 对原始json数据解析，1对多*/
    public String[] parseBaseJson(String obj){
        String[] forwards = new String[6];

        JSONObject jsonObject = JSON.parseObject(obj);
        forwards[0] = jsonObject.getString("USERID");
        forwards[1] = jsonObject.getString("DEVICEID");
        forwards[2] = jsonObject.getString("TIME_STAMP");
        JSONObject devicedata= jsonObject.getJSONObject("DEVICEDATA");

        //解析samsung/dream2qltezc/dream2qltechn:8.0.0/R16NW/G9550ZCU2CRE
        forwards[3] = StringUtils.split(devicedata.getString("K13"),"/")[0];
        forwards[4] = devicedata.getString("K19");
        forwards[5] = "android";

        //如果厂家是apple 则为ios系统
        if ("apple".equals(forwards[4].toLowerCase())) {forwards[5]="ios";}

        return forwards;
    }
}
