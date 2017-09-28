package com.baidutest.passq.baidutest.upload;


import com.baidu.speech.EventListener;
import com.baidutest.passq.baidutest.Util.Logger;

/**
 * Created by fujiayi on 2017/6/20.
 */

public class UploadEventAdapter implements EventListener {

    private static final int ERROR_NONE = 0;



    public UploadEventAdapter(){

    }

    private static final String TAG = "WakeupEventAdapter";
    @Override
    public void onEvent(String name, String params, byte[] data, int offset, int length) {
        Logger.error(TAG,"name:"+ name+"; params:"+params);

    }

}
