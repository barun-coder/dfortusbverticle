package com.displayfort.dfortusb.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.displayfort.dfortusb.PlayAdsFromUsbActivity;
import com.displayfort.dfortusb.SplashActivity;

/**
 * Created by pc on 18/12/2018 10:40.
 * MyApplication
 */
public class MediaMountReciever extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        Toast.makeText(context, "Boot Received", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(context, PlayAdsFromUsbActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
