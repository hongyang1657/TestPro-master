package com.byids.hy.testpro.utils;

import com.byids.hy.testpro.newBean.AllJsonData;
import com.byids.hy.testpro.newBean.CommandData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by gqgz2 on 2016/10/25.
 */

public class NewJsonParseUtils {

    private String TAG = "result";
    private String json;

    public NewJsonParseUtils(String json) {
        this.json = json;
    }

    //内网获取房间json数据的Gson解析
    public AllJsonData newJsonParseLAN(){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        AllJsonData allJsonData = gson.fromJson(json,AllJsonData.class);      //把解析的数据保存在homeData中

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject obj = jsonObject.getJSONObject("CommandData");
            String Package = obj.getString("package");
            allJsonData.getCommandData().setPackage(Package);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return allJsonData;
    }

    //外网获取房间json数据的Gson解析
    public CommandData newJsonParseWAN(){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        CommandData commandData = gson.fromJson(json,CommandData.class);      //把解析的数据保存在homeData中

        try {
            JSONObject jsonObject = new JSONObject(json);
            String Package = jsonObject.getString("package");
            commandData.setPackage(Package);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return commandData;
    }
}
