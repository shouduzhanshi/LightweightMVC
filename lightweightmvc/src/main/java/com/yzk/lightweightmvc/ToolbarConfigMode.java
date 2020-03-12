package com.yzk.lightweightmvc;


import android.view.View;

/**
 * 配置项目通用Activity 标题栏返回按钮
 */
public class ToolbarConfigMode {

    private static DefaultToolBarSetting defaultToolBarSetting;

    public static void setDefaultToolBarSetting(DefaultToolBarSetting defaultToolBarSetting) {
        ToolbarConfigMode.defaultToolBarSetting = defaultToolBarSetting;
    }

    public static void configToolbar(View fragmentView) {
        if (fragmentView != null && defaultToolBarSetting != null) {
            defaultToolBarSetting.configToolbar(fragmentView);
        }
    }

    public interface DefaultToolBarSetting {
        void configToolbar(View view);
    }


}
