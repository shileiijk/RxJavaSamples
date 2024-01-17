// (c)2016 Flipboard Inc, All Rights Reserved.

package com.rengwuxian.rxjavasamples;

import android.app.Application;
import android.media.audiofx.DynamicsProcessing;

import me.weishu.reflection.Reflection;
import com.wanjian.sak.SAK;

public class App extends Application {
    private static App INSTANCE;

    public static App getInstance() {
        return INSTANCE;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        me.weishu.reflection.Reflection.unseal(this);
//        SAK.init(this, null);
    }
}
