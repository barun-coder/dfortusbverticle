package com.displayfort.dfortusb;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.HashSet;

/**
 * Created by pc on 10/01/2019 13:14.
 * MyApplication
 */
public class BaseSupportActivity extends AppCompatActivity {
    public static HashSet<String> hashSet = new HashSet<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        Log.d("keyCode", "" + keyCode + " : " + getCurrentFocus());
//        keycodeTv.append(keyCode + " , ");
//
//
//        return super.onKeyDown(keyCode, event);
//
//    }
}
