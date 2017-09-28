package com.baidutest.passq.baidutest;

import com.baidutest.passq.baidutest.recognization.CommonRecogParams;
import com.baidutest.passq.baidutest.recognization.all.AllRecogParams;
import com.baidutest.passq.baidutest.setting.AllSetting;

/**
 * Created by fujiayi on 2017/6/24.
 */

public class ActivityAllRecog extends ActivityRecog {
    {
        DESC_TEXT = "所有识别参数一起的示例。请先参照之前的3个识别示例。\n";

        enableOffline = true; // 请确认不使用离线语法功能后，改为false
    }

    public ActivityAllRecog() {
        super();
        settingActivityClass = AllSetting.class;
    }

    @Override
    protected CommonRecogParams getApiParams() {
        return new AllRecogParams(this);
    }

}
