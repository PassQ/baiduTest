package com.baidutest.passq.baidutest;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.DialogPreference;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;

import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,EventListener {
    TextView textView;
    Button button;
    private EventManager asr ;
    private boolean logTime = true;
    private boolean enableOffline = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        asr = EventManagerFactory.create(this, "asr");
        asr.registerListener(this); //  EventListener 中 onEvent方法

    }


    /**
     * 测试参数填在这里
     */
    private void start() {
        textView.setText("");
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        String event = null;
        event = SpeechConstant.ASR_START; // 替换成测试的event

        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
        params.put(SpeechConstant.VAD,SpeechConstant.VAD_DNN);
          params.put(SpeechConstant.NLU, "enable");
         params.put(SpeechConstant.VAD_ENDPOINT_TIMEOUT, 800);
         params.put(SpeechConstant.VAD, SpeechConstant.VAD_DNN);
          params.put(SpeechConstant.PROP ,20000);
        String json = null; //可以替换成自己的json
        json = new JSONObject(params).toString(); // 这里可以替换成你需要测试的json
        asr.send(event, json, null, 0, 0);
        //unloadOfflineEngine(); //测试离线语法开启
        printLog("输入参数：" + json);
    }



    private void printLog(String text) {
        if (logTime) {
            text += "  ;time=" + System.currentTimeMillis();
        }
        text += "\n";
        Log.i(getClass().getName(), text);
        textView.append(text + "\n");
    }



    /**
     * 控件初始化
     */
    private void initView() {
        textView = (TextView) findViewById(R.id.textView);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);

    }

    /**
     * onClick监听
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button :
                boolean isAllGranted = checkPermissionAllGranted(
                        new String[]{
                                Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.ACCESS_NETWORK_STATE,
                                Manifest.permission.INTERNET,
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        }
                );

                if(isAllGranted){
                    Toast.makeText(MainActivity.this,"权限授予完毕!",Toast.LENGTH_LONG).show();
                    start();
                    return;
                }
                /**
                 * 请求权限
                 */
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{
                                Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.ACCESS_NETWORK_STATE,
                                Manifest.permission.INTERNET,
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, 123);


                break;
            default:
                break;

        }
    }

    /**
     * 检查是否拥有指定的所有权限
     */
    private boolean checkPermissionAllGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                return false;
            }
        }
        return true;
    }


    //权限授予
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 123){
            //所有权限被授予
            boolean isAllGranted = true;

            //判断是否所有权限被授予
            for(int grant : grantResults){
                if(grant != PackageManager.PERMISSION_GRANTED){
                    isAllGranted = false;
                    break;
                }
            }

            if(isAllGranted){
                Toast.makeText(MainActivity.this,"权限授予完毕!",Toast.LENGTH_LONG).show();
                return;
            }else {
                openAppDetails();
            }

        }
    }


    /**
     * 第二次授权弹窗
     */
    public void openAppDetails(){
       AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
       builder.setMessage("应用需要访问权限，请到 “应用信息 -> 权限” 中授予！");
       builder.setPositiveButton("手动授权", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {
               Intent intent = new Intent();
               intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
               intent.addCategory(Intent.CATEGORY_DEFAULT);
               intent.setData(Uri.parse("package:" + getPackageName()));
               intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
               intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
               intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
               startActivity(intent);
           }
       });
       builder.setNegativeButton("取消",null);
       builder.show();


   }

    //   EventListener  回调方法
    @Override
    public void onEvent(String name, String params, byte[] data, int offset, int length) {
        String logTxt = "name: " + name;


        if (params != null && !params.isEmpty()) {
            logTxt += " ;params :" + params;
        }
        if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL)) {
            if (params.contains("\"nlu_result\"")) {
                if (length > 0 && data.length > 0) {
                    logTxt += ", 语义解析结果：" + new String(data, offset, length);
                }
            }
        } else if (data != null) {
            logTxt += " ;data length=" + data.length;
        }
        printLog(logTxt);
    }

    private void stop() {
        asr.send(SpeechConstant.ASR_STOP, null, null, 0, 0); //
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        asr.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0);
        if (enableOffline) {
//            unloadOfflineEngine(); //测试离线语法请开启, 测试 ASR_OFFLINE_ENGINE_GRAMMER_FILE_PATH 参数时开启
        }
    }
}
