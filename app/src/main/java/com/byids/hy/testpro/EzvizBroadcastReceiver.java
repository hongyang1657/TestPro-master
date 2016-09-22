package com.byids.hy.testpro;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.byids.hy.testpro.activity.CameraActivity;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.bean.EZAccessToken;

/**
 * Created by gqgz2 on 2016/9/21.
 */
public class EzvizBroadcastReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        if (TextUtils.equals(intent.getAction(),"com.videogo.action.OAUTH_SUCCESS_ACTION")){
            Intent toIntent = new Intent(context, CameraActivity.class);
            toIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //保存token，获取超时时间，在token过期时重新获取
            EZAccessToken accessToken = EZOpenSDK.getInstance().getEZAccessToken();
            Log.i("result", "onReceive:----accessToken----- "+accessToken.getAccessToken());

            //储存AccessToken
            SharedPreferences.Editor editor = context.getSharedPreferences("data",Context.MODE_PRIVATE).edit();
            editor.putString("token",accessToken.getAccessToken());
            editor.commit();

            //EZOpenSDK.getInstance().setAccessToken(accessToken.getAccessToken());
            context.startActivity(toIntent); //启动camera list
        }
    }
}
