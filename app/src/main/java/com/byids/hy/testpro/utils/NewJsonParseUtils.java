package com.byids.hy.testpro.utils;

import android.util.Log;

import com.byids.hy.testpro.newBean.AllJsonData;
import com.byids.hy.testpro.newBean.CommandUser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by gqgz2 on 2016/10/25.
 */

public class NewJsonParseUtils {
    private String json;
    private String TAG = "result";

    public NewJsonParseUtils(String json) {
        this.json = json;
    }

    public void newJsonParse(){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        AllJsonData allJsonData = gson.fromJson(json,AllJsonData.class);      //把解析的数据保存在homeData中

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject obj = jsonObject.getJSONObject("CommandData");
            String Package = obj.getString("package");
            Log.i(TAG, "newJsonParse: ！！！！！！！！！！！！！！！！！！！"+Package);
            Log.i(TAG, "newJsonParse: @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+allJsonData.getCommandData());
            allJsonData.getCommandData().setPackage(Package);
            JSONObject profile = obj.getJSONObject("profile");
            String profileStr = profile.toString();
            Log.i(TAG, "newJsonParse: ------------------------"+profileStr);

            Log.i(TAG, "newJsonParse: ----ower"+allJsonData.getCommandData().getOwer()+"----comments"+allJsonData.getCommandData().getComments()
                    +"-----address"+allJsonData.getCommandData().getAddress()+"----telephone"+allJsonData.getCommandData().getTelephone()+"----package"
                    +allJsonData.getCommandData().getPackage()+"------"+allJsonData.getCommandData().getProfile().getCamera().getActive()+"------"
                    +allJsonData.getCommandData().getProfile().getRooms().getArray().get(0).getActive()+"-------"
                    +allJsonData.getCommandData().getProfile().getRooms().getArray().get(0).getRoom_db_name()+"-----"
                    +allJsonData.getCommandData().getProfile().getRooms().getArray().get(0).getRoom_dev_mesg().getCurtain().getArray().get(0).getSoftware_mesg().getDisplayer_name());

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
