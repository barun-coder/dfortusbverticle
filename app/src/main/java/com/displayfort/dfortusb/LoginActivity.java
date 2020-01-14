package com.displayfort.dfortusb;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.displayfort.RetroFit.BaseRequest;
import com.displayfort.RetroFit.RequestReceiver;
import com.displayfort.dfortusb.validation.ValidationUtils;
import com.github.glomadrian.codeinputlib.CodeInput;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private Context context;
    public EditText securityPinEt;
    public Button mLoginBtn;
    public TextView mUniqueIdTv;
    private String uniqueId = "", securityPin = "";
    private CodeInput cInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;
        init();
        setUpt();

    }

    private void setUpt() {
        mUniqueIdTv.setText(ObtainMAC().trim());
    }

    private void init() {
        cInput = (CodeInput) findViewById(R.id.codeinput);
        securityPinEt = (EditText) findViewById(R.id.security_pin_et);
        mLoginBtn = (Button) findViewById(R.id.email_sign_in_button);
        mUniqueIdTv = (TextView) findViewById(R.id.uniquid_tv);
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid()) {
                    loginRequest();
                }
            }
        });

    }

    private boolean isValid() {
        uniqueId = mUniqueIdTv.getText().toString();
        securityPin = securityPinEt.getText().toString();
        if (!ValidationUtils.isValidString(uniqueId, 2)) {
            ShowToast("Not a valid Username.", "Please enter valid Username");
            return false;
        }

        if (!ValidationUtils.isValidString(securityPin, 8)) {
            ShowToast("Not a valid Password.", "Please enter valid password");
            return false;
        }

        return true;

    }

    private void ShowToast(String s, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public String ObtainMAC() {
        WifiManager manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();

        return (info.getMacAddress().toUpperCase());
    }

    private void loginRequest() {
        BaseRequest baseRequest = new BaseRequest(this);
        baseRequest.setBaseRequestListner(new RequestReceiver() {

            @Override
            public void onSuccess(int requestCode, String fullResponse, Object dataObject, int StatusCode) {
                if (StatusCode == 200) {
                    try {
                        JSONObject object = new JSONObject(fullResponse);
                        new DFPrefrence(context).setIsLogin(true);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(context, PotraitPlayAdsFromUsbUniversalActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    String message = "Something went wrong.";
                    try {
                        JSONObject jsonObject = new JSONObject(fullResponse);
                        message = jsonObject.optString("message");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    AlertDialog alertDialog = new AlertDialog.Builder(context)
                            .setTitle("Not Allowed")
                            .setMessage(message)

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })

                            // A null listener allows the button to dismiss the dialog and take no further action.
//                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                }
            }

            @Override
            public void onFailure(int requestCode, String errorCode, String message) {

            }

            @Override
            public void onNetworkFailure(int requestCode, String message) {

            }
        });
        final com.google.gson.JsonObject jsonObject = getJsonObject(
                "mac_id", uniqueId,
                "spin", securityPin
        );

        baseRequest.callAPIPost(1, jsonObject, "login");
    }

    public static JsonObject getJsonObject(String... nameValuePair) {
        JsonObject HashMap = null;
        if (null != nameValuePair && nameValuePair.length % 2 == 0) {
            HashMap = new JsonObject();
            int i = 0;
            while (i < nameValuePair.length) {
                HashMap.addProperty(nameValuePair[i], nameValuePair[i + 1]);
                i += 2;
            }

        }

        return HashMap;
    }
}

