package com.displayfort.dfortusb;

import android.app.Application;

import com.splunk.mint.Mint;

/**
 * Created by pc on 06/03/2019 15:59.
 * DFortUSB
 */
public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Mint.initAndStartSession(this, "633c5141");
    }
}
