package com.displayfort.RetroFit;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by Husain on 12/26/2016.
 */

public class BaseRequestParser {
    public String message = "Please check your network settings.";
    public String mResponseCode = "0";
    public static String newtWorkMessage = "Please check your network settings.";
    private JSONObject mRespJSONObject = null;

    public boolean parseJson(String json) {
        if (!TextUtils.isEmpty(json)) {
            try {
                mRespJSONObject = new JSONObject(json);
                if (null != mRespJSONObject) {

                    mResponseCode = mRespJSONObject.optString("response_code",
                            "Response code not found");
                    message = mRespJSONObject.optString("message",
                            message);

                    if (mResponseCode.equalsIgnoreCase("200")) {
                        return true;
                    } else {
                        if (message.length() <= 0 && mRespJSONObject.has("error")) {
                            JSONObject error = mRespJSONObject.optJSONObject("error");
                            if (error != null) {
                                Iterator iterator = error.keys();
                                if (iterator.hasNext()) {
                                    String key = (String) iterator.next();
                                    message = error.optString(key);
                                }
                            } else if (mRespJSONObject.has("global_error")) {
                                message = mRespJSONObject.optString("global_error");
                            }
                        }
                        return false;
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public JSONArray getDataArray() {
        if (null == mRespJSONObject) {
            return null;
        }
        try {
            return mRespJSONObject.optJSONArray("data");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public Object getDataObject() {
        if (null == mRespJSONObject) {
            return null;
        }
        try {
            return mRespJSONObject.optJSONObject("data");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getValues(String key) {
        if (mRespJSONObject != null) {
            return mRespJSONObject.optString(key);
        }
        return "";
    }

}
