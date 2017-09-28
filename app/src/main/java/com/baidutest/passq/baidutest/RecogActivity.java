package com.baidutest.passq.baidutest;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import com.baidutest.passq.baidutest.control.MyRecognizer;
import com.baidutest.passq.baidutest.recognization.CommonRecogParams;
import com.baidutest.passq.baidutest.recognization.IStatus;
import com.baidutest.passq.baidutest.recognization.MessageStatusRecogListener;
import com.baidutest.passq.baidutest.recognization.StatusRecogListener;
import com.baidutest.passq.baidutest.recognization.offline.OfflineRecogParams;

import java.util.Map;
import java.util.concurrent.BrokenBarrierException;

/**
 *
 * 语音识别基类
 * Created by ASUS on 2017/9/28.
 */



public abstract class RecogActivity extends Main2Activity implements IStatus {

    /**
     * 识别控制器，控制识别的流程
     */
    protected MyRecognizer myRecognizer;

    /**
     * Api参数类，仅仅用于生成start的json字符
     */

    protected CommonRecogParams apiParams;

        /*
        * 本Activity中是否需要调用离线语法功能。根据此参数，判断是否需要调用SDK的ASR_KWS_LOAD_ENGINE事件
        */
    protected boolean enableOffline = false;


    /**
     * 控制UI按钮的状态
     */
    private int status;


    /**
     * 日志使用
     */
    private static final String TAG = "ActivityRecog";


    /**
     * 在onCreate 中使用。 初始化控制类MyRecognizer
     */

    protected  void  initRecog(){
        StatusRecogListener listener = new MessageStatusRecogListener(handler);
        myRecognizer = new MyRecognizer(this,listener);
        apiParams = getApiParams();
        status = STATUS_NONE;
        if(enableOffline){
            myRecognizer.loadOfflineEngine(OfflineRecogParams.fetchOfflineParams());
        }
    }


    /**
     * 销毁时释放资源
     */

    @Override
    protected void onDestroy() {
        myRecognizer.release();
        super.onDestroy();
    }



    /**
     * 开始录音，点击开始按钮后调用
     */
    protected  void  start(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(RecogActivity.this);
        Map<String,Object> params = apiParams.fetch(sp);
        myRecognizer.start(params);
    }

    /**
     * 开始录音后，手动停止录音
     */
    private  void  stop(){myRecognizer.stop();}


    /**
     * 开始录音后，取消这次录音，会取消本次识别，回到原始状态
     */
    private void cancel(){myRecognizer.cancel();}


    @Override
    protected void initView() {
        super.initView();
        button_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (status) {
                    case STATUS_NONE://初始状态
                        start();
                        status = STATUS_WAITING_READY;
                        updateBtnTextByStatus();
                        textView.setText("");
                        break;
                    case STATUS_WAITING_READY://等待引擎准备完毕
                    case STATUS_READY://引擎准备完毕
                    case STATUS_SPEAKING://
                    case STATUS_FINISHED://长语音情况
                    case STATUS_RECOGNITION://
                        stop();
                        status = STATUS_STOPPED;//引擎识别中
                        updateBtnTextByStatus();
                        break;
                    case STATUS_STOPPED://引擎识别中
                        cancel();
                        status = STATUS_NONE;//识别结束，回到初始状态
                        updateBtnTextByStatus();
                        break;

                }
            }
        });

    }


    private void updateBtnTextByStatus() {
        switch (status) {
            case STATUS_NONE:
                button_start.setText("开始录音");
                button_start.setEnabled(true);

                break;
            case STATUS_WAITING_READY:
            case STATUS_READY:
            case STATUS_SPEAKING:
            case STATUS_RECOGNITION:
                button_start.setText("停止录音");
                button_start.setEnabled(true);

                break;

            case STATUS_STOPPED:
                button_start.setText("取消整个识别过程");
                button_start.setEnabled(true);
                break;
        }
    }


    /**
     *
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * @return
     */
    protected abstract CommonRecogParams getApiParams();

    protected void handleMsg(Message msg) {
        super.handleMsg(msg);

        switch (msg.what) { // 处理MessageStatusRecogListener中的状态回调
            case STATUS_FINISHED:
                if (msg.arg2 == 1)
//                    textView.setText(msg.obj.toString());
                     Toast.makeText(RecogActivity.this,msg.obj.toString(),Toast.LENGTH_LONG);
                //故意不写break
            case STATUS_NONE:
            case STATUS_READY:
            case STATUS_SPEAKING:
            case STATUS_RECOGNITION:
                status = msg.what;
                updateBtnTextByStatus();
                break;

        }
    }


}
