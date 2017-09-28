package com.baidutest.passq.baidutest;

import com.baidutest.passq.baidutest.recognization.CommonRecogParams;
import com.baidutest.passq.baidutest.recognization.online.OnlineRecogParams;

/**
 * Created by ASUS on 2017/9/28.
 */

public class OnlineActivity extends RecogActivity {
    @Override
    protected CommonRecogParams getApiParams() {
        return new OnlineRecogParams(this);
    }
}
