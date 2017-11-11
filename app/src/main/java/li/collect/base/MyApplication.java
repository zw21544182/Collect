package li.collect.base;

import android.app.Application;

/**
 * 创建时间: 2017/11/7
 * 创建人: Administrator
 * 功能描述:初始化数据,设置一些全局变量
 */

public class MyApplication extends Application {
    private boolean isRecord = false;

    public boolean isRecord() {
        return isRecord;
    }

    public void setRecord(boolean record) {
        isRecord = record;
    }
}
