package com.gnest.remember;

import android.app.Application;
import android.content.Context;

/**
 * Created by DFedonnikov on 11.08.2017.
 */

public class App extends Application {

    private static App sSelf;

    public static Context self() {
        return sSelf;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sSelf = this;
    }
}
