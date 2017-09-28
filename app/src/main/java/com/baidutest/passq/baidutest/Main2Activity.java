package com.baidutest.passq.baidutest;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidutest.passq.baidutest.Util.Logger;

public abstract class Main2Activity extends AppCompatActivity implements View.OnClickListener {
    protected TextView textView;
    protected Button button_stop;
    protected Button button_start;
    protected Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        initView();
        handler = new Handler() {

            /*
             * @param msg
             */
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                handleMsg(msg);
            }

        };
        Logger.setHandler(handler);
        initRecog();
    }


    protected abstract void initRecog();

    protected void initView() {
        textView = (TextView) findViewById(R.id.textView2);
        button_stop = (Button) findViewById(R.id.button_stop);
        button_start = (Button) findViewById(R.id.button_start);
    }

    @Override
    public void onClick(View v) {

    }

    protected void handleMsg(Message msg) {
        if (textView != null && msg.obj != null) {
            textView.append(msg.obj.toString() + "\n");
        }
    }
}
