package com.byids.hy.testpro.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.byids.hy.testpro.MyEventBus;
import com.byids.hy.testpro.R;
import com.byids.hy.testpro.utils.CommandJsonUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by gqgz2 on 2016/12/24.
 */

public class ControlLightBaseAdapter extends BaseAdapter{
    private Context context;
    private LayoutInflater inflater;
    private Typeface typeFace;
    private int[] lightLoopIconList;
    private String[] lightLoopNameList;
    private String roomDBName;
    private String token;
    private String uname;
    private String pwd;
    private String protocol;
    private boolean allClose = false;
    private boolean all1 = false;
    private boolean all2 = false;
    private boolean all3 = false;

    public ControlLightBaseAdapter(Context context, int[] lightLoopIconList, String[] lightLoopNameList,String roomDBName,String token,String uname,String pwd,String protocol) {
        this.context = context;
        this.lightLoopIconList = lightLoopIconList;
        this.lightLoopNameList = lightLoopNameList;
        this.roomDBName = roomDBName;
        this.token = token;
        this.uname = uname;
        this.pwd = pwd;
        this.protocol = protocol;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return lightLoopNameList.length;
    }

    @Override
    public Object getItem(int position) {
        return lightLoopNameList[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView==null){
            convertView = inflater.inflate(R.layout.control_light_huilu_item,null);
            holder = new ViewHolder();
            typeFace = Typeface.createFromAsset(context.getAssets(),"fonts/xiyuanti.ttf");
            holder.ivLightControlX = (ImageView) convertView.findViewById(R.id.iv_light_control_x);
            holder.tvLightControlX = (TextView) convertView.findViewById(R.id.tv_light_control_x);
            holder.rgLightControlX = (RadioGroup) convertView.findViewById(R.id.rg_control_light_channel_x);
            holder.rbLightControlClose = (RadioButton) convertView.findViewById(R.id.rb_control_light_channel_x_close);
            holder.rbLightControl1 = (RadioButton) convertView.findViewById(R.id.rb_control_light_channel_x_1);
            holder.rbLightControl2 = (RadioButton) convertView.findViewById(R.id.rb_control_light_channel_x_2);
            holder.rbLightControl3 = (RadioButton) convertView.findViewById(R.id.rb_control_light_channel_x_3);
            holder.tvLightControlX.setTypeface(typeFace);
            holder.rbLightControlClose.setTypeface(typeFace);
            holder.rbLightControl1.setTypeface(typeFace);
            holder.rbLightControl2.setTypeface(typeFace);
            holder.rbLightControl3.setTypeface(typeFace);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();
        holder.ivLightControlX.setImageResource(lightLoopIconList[position]);
        holder.tvLightControlX.setText(lightLoopNameList[position]);
        holder.rgLightControlX.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_control_light_channel_x_close:

                        break;
                    case R.id.rb_control_light_channel_x_1:
                        break;
                    case R.id.rb_control_light_channel_x_2:
                        break;
                    case R.id.rb_control_light_channel_x_3:
                        break;
                    default:
                        break;
                }
            }
        });
        holder.rbLightControlClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controlLight(protocol,"light","0","0",""+(position+1));
            }
        });
        holder.rbLightControl1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controlLight(protocol,"light","30","0",""+(position+1));
            }
        });
        holder.rbLightControl2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controlLight(protocol,"light","60","0",""+(position+1));
            }
        });
        holder.rbLightControl3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controlLight(protocol,"light","100","0",""+(position+1));
            }
        });

        if (allClose){
            holder.rbLightControlClose.setChecked(true);
        }else {
            holder.rbLightControlClose.setChecked(false);
        }
        if (all1){
            holder.rbLightControl1.setChecked(true);
        }else {
            holder.rbLightControl1.setChecked(false);
        }
        if (all2){
            holder.rbLightControl2.setChecked(true);
        }else {
            holder.rbLightControl2.setChecked(false);
        }
        if (all3){
            holder.rbLightControl3.setChecked(true);
        }else {
            holder.rbLightControl3.setChecked(false);
        }
        return convertView;
    }


    /*
    * 传入不同房间的灯光回路信息
    * */
    public void setDiffRoomLight(int[] lightLoopIconList,String[] lightLoopNameList,String roomDBName){
        this.lightLoopIconList = lightLoopIconList;
        this.lightLoopNameList = lightLoopNameList;
        this.roomDBName = roomDBName;
        notifyDataSetChanged();
    }

    //传入改变各回路灯光选择的值  0:关闭
    public void changeLightLoopValue(int value){
        switch (value){
            case 0:
                allClose = true;
                all1 = false;
                all2 = false;
                all3 = false;
                break;
            case 1:
                all1 = true;
                all2 = false;
                all3 = false;
                allClose = false;
                break;
            case 2:
                all2 = true;
                all1 = false;
                all3 = false;
                allClose = false;
                break;
            case 3:
                all3 = true;
                allClose = false;
                all1 = false;
                all2 = false;
                break;
            case 10:        //全部按钮设置为未选中状态
                allClose = false;
                all1 = false;
                all2 = false;
                all3 = false;
                break;
            default:
                break;
        }
        notifyDataSetChanged();
    }



    //控制灯光
    private void controlLight(String controlProtocol, String machineName, String value, String isServerAUTO, String controlSence) {
        JSONObject CommandData = new JSONObject();
        JSONObject controlData = new JSONObject();
        try {
            CommandData.put("controlProtocol", controlProtocol);
            CommandData.put("machineName", machineName);
            CommandData.put("controlData", controlData);
            controlData.put("lightValue", value);
            controlData.put("isServerAUTO", isServerAUTO);
            CommandData.put("controlSence", controlSence);
            CommandData.put("houseDBName", roomDBName);
            String lightJson = CommandJsonUtils.getCommandJson(0, CommandData, token, uname, pwd, String.valueOf(System.currentTimeMillis()));
            EventBus.getDefault().post(new MyEventBus(lightJson));
            Log.e("result", "onClick: ------lightjson------" + lightJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class ViewHolder{
        ImageView ivLightControlX;
        TextView tvLightControlX;
        RadioGroup rgLightControlX;
        RadioButton rbLightControlClose;
        RadioButton rbLightControl1;
        RadioButton rbLightControl2;
        RadioButton rbLightControl3;
    }
}
