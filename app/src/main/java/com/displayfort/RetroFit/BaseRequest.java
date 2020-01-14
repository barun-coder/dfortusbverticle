package com.displayfort.RetroFit;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.displayfort.dfortusb.DFPrefrence;
import com.displayfort.dfortusb.LoginActivity;
import com.displayfort.dfortusb.R;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class BaseRequest<T> extends BaseRequestParser {
    private Context mContext;
    private ApiInterface apiInterface;
    private RequestReceiver requestReciever;
    private boolean runInBackground = false;
    private Dialog dialog;
    private View loaderView = null;
    private int RequestCode = 1;
    Fragment fragment;

    private boolean cacheEnabled = false;
    private boolean isAlreadyTaken = false;
    private boolean duringCacheLoader = false;
    String fileName = null;
    private boolean hideloader = true;

    public boolean isDuringCacheLoader() {
        return duringCacheLoader;
    }

    public void setDuringCacheLoader(boolean duringCacheLoader) {
        this.duringCacheLoader = duringCacheLoader;
    }

    public boolean isRunInBackground() {
        return runInBackground;
    }

    public void setRunInBackground(boolean runInBackground) {
        this.runInBackground = runInBackground;
    }

    public BaseRequest(Context context) {
        mContext = context;

        apiInterface =
                ApiClient.getClient().create(ApiInterface.class);
        if (context != null && !runInBackground) {
            dialog = getProgressesDialog(context);
        }
    }

    public BaseRequest(Context context, int esxternal) {
        mContext = context;


        apiInterface =
                ApiClient.getExternalClient().create(ApiInterface.class);
        if (context != null && !runInBackground) {
            dialog = getProgressesDialog(context);
        }
    }

    public BaseRequest(Context context, boolean runInBackground) {
        mContext = context;
        this.runInBackground = runInBackground;
        apiInterface =
                ApiClient.getClient().create(ApiInterface.class);
        if (context != null && !runInBackground) {
            dialog = getProgressesDialog(context);
        }
    }


    public boolean isAlreadyTaken() {
        return isAlreadyTaken;
    }

    public void setAlreadyTaken(boolean alreadyTaken) {
        isAlreadyTaken = alreadyTaken;
    }

    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

    public void setCacheEnabled(boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }

    public BaseRequest(Context context, Fragment fragment) {
        mContext = context;
        this.fragment = fragment;
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        if (context != null && !runInBackground) {
            dialog = getProgressesDialog(context);
        }
    }

    public void setBaseRequestListner(RequestReceiver requestListner) {
        this.requestReciever = requestListner;

    }

    public void setLoaderView(View loaderView_) {
        this.loaderView = loaderView_;
        loaderView.setVisibility(View.GONE);

    }

    public ArrayList<Object> getDataList(JSONArray mainArray, Class<T> t) {
        Gson gsm = null;
        ArrayList<Object> list = null;
        list = new ArrayList<>();
        if (null != mainArray) {

            for (int i = 0; i < mainArray.length(); i++) {
                gsm = new Gson();
                Object object = gsm.fromJson(mainArray.optJSONObject(i).toString(), t);
                list.add(object);
            }
        }
        return list;
    }

    public ArrayList<String> getStringList(JSONArray mainArray) {
        Gson gsm = null;
        ArrayList<String> list = null;
        list = new ArrayList<>();
        if (null != mainArray) {
            for (int i = 0; i < mainArray.length(); i++) {
                String object = mainArray.optString(i);
                list.add(object);
            }
        }
        return list;
    }

    public Object getDataModels(JSONObject jsonObject, Class<T> t) {
        Gson gsm = null;
        gsm = new Gson();
        Object object = gsm.fromJson(jsonObject.toString(), t);
        return object;
    }

    public ArrayList<Object> getList(JSONArray mainArray, Class<T> t) {
        Gson gsm = null;
        ArrayList<Object> list = null;
        list = new ArrayList<>();
        if (null != mainArray) {

            for (int i = 0; i < mainArray.length(); i++) {
                gsm = new Gson();
                Object object = gsm.fromJson(mainArray.optJSONObject(i).toString(), t);
                list.add(object);
            }
        }
        return list;
    }

    public Callback<JsonElement> responseCallback = new Callback<JsonElement>() {
        @Override
        public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
            String responseServer = "";
            hideLoader();
            //TODO
            if (null != response.body()) {
                JsonElement jsonElement = (JsonElement) response.body();
                if (null != jsonElement) {
                    responseServer = jsonElement.toString();
                }
            } else if (response.errorBody() != null) {
                if (response.code() == 403) {
                    new DFPrefrence(mContext).setIsLogin(false);
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                } else {
                    responseServer = readStreamFully(response.errorBody().contentLength(),
                            response.errorBody().byteStream());
                }
            }//{"status":true,"message":"Profile created"}
            logFullResponse(response.code() + "  : " + responseServer, "OUTPUT");
            if (response.code() != 403) {
                if (!(mContext instanceof Activity)) {
                    if (null != getDataArray()) {
                        requestReciever.onSuccess(RequestCode, responseServer, getDataArray(), response.code());
                    } else if (null != getDataObject()) {
                        requestReciever.onSuccess(RequestCode, responseServer, getDataObject(), response.code());
                    } else {
                        requestReciever.onSuccess(RequestCode, responseServer, message, response.code());
                    }
                } else if (null != requestReciever && !((Activity) mContext).isDestroyed()) {
                    if (fragment != null) {
                        if (fragment.isAdded()) {
                            if (null != getDataArray()) {
                                requestReciever.onSuccess(RequestCode, responseServer, getDataArray(), response.code());
                            } else if (null != getDataObject()) {
                                requestReciever.onSuccess(RequestCode, responseServer, getDataObject(), response.code());
                            } else {
                                requestReciever.onSuccess(RequestCode, responseServer, message, response.code());
                            }
                        }
                    } else {
                        if (null != getDataArray()) {
                            requestReciever.onSuccess(RequestCode, responseServer, getDataArray(), response.code());
                        } else if (null != getDataObject()) {
                            requestReciever.onSuccess(RequestCode, responseServer, getDataObject(), response.code());
                        } else {
                            requestReciever.onSuccess(RequestCode, responseServer, message, response.code());
                        }

                    }
                }
            }

        }

        @Override
        public void onFailure(Call<JsonElement> call, Throwable t) {

            handler.removeCallbacksAndMessages(null);
            // First condition is for connection problem

            if (!TextUtils.isEmpty(t.getMessage()) && t.getMessage().startsWith("Unable to resolve") || t.getMessage().startsWith("Failed to connect")) {
                handler.postDelayed(r, 1000);
            } else {
                hideLoader();
                requestReciever.onFailure(1, "" + mResponseCode, message);
            }

        }
    };


    Handler handler = new Handler();
    Runnable r = new Runnable() {
        @Override
        public void run() {
            hideLoader();
            if (null != requestReciever) {
//                requestReciever.onNetworkFailure(RequestCode, mContext.getString(R.string.MSG_INTERNETERROR));
//
//                Dialogs.showOkCancelDialog(
//                        mContext,
//                        mContext.getString(R.string.MSG_INTERNETERROR),
//                        new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                ((Dialog) v.getTag()).dismiss();
//                                showLoader();
//                                callAPI.clone().enqueue(responseCallback);
//                            }
//
//
//                        });
            }
        }
    };
    Call<JsonElement> callAPI;

    public void callAPIGet(final int requestCode, String remainingURL) {
        isAlreadyTaken = false;
        RequestCode = requestCode;
        showLoader();
        Log.d("BaseReq", "Input URL : " + ApiClient.getClient().baseUrl() + ApiClient.getMidUrl() + remainingURL);
        callAPI = apiInterface.getData(ApiClient.getMidUrl() + remainingURL, new DFPrefrence(mContext).getLoginSessionKey());
        callAPI.enqueue(responseCallback);

    }


    public void callAPIDelete(final int requestCode, String remainingURL) {
        isAlreadyTaken = false;
        RequestCode = requestCode;
        showLoader();
        Log.d("BaseReq", "Input URL : " + ApiClient.getClient().baseUrl() + ApiClient.getMidUrl() + remainingURL);
        callAPI = apiInterface.deleteData(ApiClient.getMidUrl() + remainingURL, new DFPrefrence(mContext).getLoginSessionKey());
        callAPI.enqueue(responseCallback);
        callAPI.request();
    }


    public void callAPIPost(final int requestCode, JsonObject jsonObject, String remainingURL) {
        isAlreadyTaken = false;
        RequestCode = requestCode;
        showLoader();
        if (jsonObject == null) {
            jsonObject = new JsonObject();
        }
        Log.d("BaseReq", "Input URL : " + ApiClient.getClient().baseUrl() + remainingURL);
        logFullResponse(jsonObject.toString(), "INPUT");
        callAPI = apiInterface.postData(remainingURL, jsonObject);
        callAPI.enqueue(responseCallback);

    }

    public void callAPIPut(final int requestCode, JsonObject jsonObject, String remainingURL) {
        isAlreadyTaken = false;
        RequestCode = requestCode;
        showLoader();
        if (jsonObject == null) {
            jsonObject = new JsonObject();
        }
        Log.d("BaseReq", "Input URL : " + ApiClient.getClient().baseUrl() + ApiClient.getMidUrl() + remainingURL);
        logFullResponse(jsonObject.toString(), "INPUT");
        callAPI = apiInterface.putData(ApiClient.getMidUrl() + remainingURL, jsonObject);
        callAPI.enqueue(responseCallback);

    }


    public void callAPIPostHeader(final int requestCode, JsonObject jsonObject, String remainingURL) {
        isAlreadyTaken = false;
        RequestCode = requestCode;
        showLoader();
        if (jsonObject == null) {
            jsonObject = new JsonObject();
        }
        Log.d("BaseReq", "Input URL : " + ApiClient.getClient().baseUrl() + ApiClient.getMidUrl() + remainingURL);
        logFullResponse(jsonObject.toString(), "INPUT");
        callAPI = apiInterface.postData(ApiClient.getMidUrl() + remainingURL, jsonObject, new DFPrefrence(mContext).getLoginSessionKey());
        callAPI.enqueue(responseCallback);
    }

    public void callAPIPutHeader(final int requestCode, JsonObject jsonObject, String remainingURL) {
        isAlreadyTaken = false;
        RequestCode = requestCode;
        showLoader();
        if (jsonObject == null) {
            jsonObject = new JsonObject();
        }
        Log.d("BaseReq", "Input URL : " + ApiClient.getClient().baseUrl() + ApiClient.getMidUrl() + remainingURL);
        logFullResponse(jsonObject.toString(), "INPUT");
        callAPI = apiInterface.putData(ApiClient.getMidUrl() + remainingURL, jsonObject, new DFPrefrence(mContext).getLoginSessionKey());
        callAPI.enqueue(responseCallback);
    }

    public void logFullResponse(String response, String inout) {
        final int chunkSize = 2000;

        if (null != response && response.length() > chunkSize) {
            int chunks = (int) Math.ceil((double) response.length()
                    / (double) chunkSize);

            for (int i = 1; i <= chunks; i++) {
                if (i != chunks) {
                    Log.i("BaseReq",
                            inout + " : "
                                    + response.substring((i - 1) * chunkSize, i
                                    * chunkSize));
                } else {
                    Log.i("BaseReq",
                            inout + " : "
                                    + response.substring((i - 1) * chunkSize,
                                    response.length()));
                }
            }
        } else {
            Log.i("BaseReq",
                    inout + " : "
                            + response);
        }
    }

    private String readStreamFully(long len, InputStream inputStream) {
        if (inputStream == null) {
            return null;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public Dialog getProgressesDialog(Context ct) {
        Dialog dialog = new Dialog(ct);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.progress_dialog_loader);
        dialog.setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);
        return dialog;
    }

    public void showLoader() {
        if (mContext != null && !((Activity) mContext).isDestroyed()) {

            if (!runInBackground) {
                if (null != loaderView) {
                    loaderView.setVisibility(View.VISIBLE);
                } else if (null != dialog) {
                    dialog.show();
                }
            }
        }
    }

    public void hideLoader() {

        if (mContext != null && (mContext instanceof Activity) && !((Activity) mContext).isDestroyed()) {

            if (!runInBackground) {
                if (null != loaderView) {
                    loaderView.setVisibility(View.GONE);
                } else if (null != dialog) {
                    dialog.dismiss();
                    dialog.cancel();
                }
            }
        }


    }

    public int TYPE_NOT_CONNECTED = 0;
    public int TYPE_WIFI = 1;
    public int TYPE_MOBILE = 2;

    String CHARSET = "UTF-8";
    private static final String BOUNDARY = "===" + System.currentTimeMillis()
            + "===";
    private static final String LINE_FEED = "\r\n";

    private static final String ACCEPT = "application/json";
    private static final String CONTENT_TYPE = "application/json";
    private static final String USER_AGENT = "";
    private HttpURLConnection mHttpURLConnection;

    public class MultipartConn {
        private HttpURLConnection httpURLConnection;
        private OutputStream outputStream;
        private PrintWriter printWriter;

        /**
         * This constructor initializes a new HTTP POST request with content
         * type is set to multipart/form-data
         *
         * @param requestURL
         */
        public MultipartConn(String requestURL) throws IOException {
            URL url = new URL(requestURL);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setDoOutput(true); // indicates POST method
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + BOUNDARY);
//
//            httpURLConnection.setRequestProperty("User-Agent",
//                    HttpConnector.USER_AGENT);

            outputStream = httpURLConnection.getOutputStream();
            printWriter = new PrintWriter(new OutputStreamWriter(outputStream,
                    CHARSET), true);
        }

        /**
         * Adds a form field to the request
         *
         * @param name  field lastName
         * @param value field value
         */
        public void addFormField(String name, String value) {
            printWriter.append("--" + BOUNDARY).append(LINE_FEED);
            printWriter.append(
                    "Content-Disposition: form-data; lastName=\"" + name + "\"")
                    .append(LINE_FEED);
            printWriter.append("Content-Type: text/plain; charset=" + CHARSET)
                    .append(LINE_FEED);

            printWriter.append(LINE_FEED);
            printWriter.append(value).append(LINE_FEED);
            printWriter.flush();
        }

        /**
         * Adds a upload file section to the request
         *
         * @param fieldName  lastName attribute in <input type="file" lastName="..." />
         * @param uploadFile a File to be uploaded
         * @throws IOException
         */
        public void addFilePart(String fieldName, File uploadFile)
                throws IOException {
            String fileName = uploadFile.getName();
            printWriter.append("--" + BOUNDARY).append(LINE_FEED);
            printWriter.append(
                    "Content-Disposition: form-data; lastName=\"" + fieldName
                            + "\"; filename=\"" + fileName + "\"").append(
                    LINE_FEED);
            printWriter.append(
                    "Content-Type: "
                            + URLConnection.guessContentTypeFromName(fileName))
                    .append(LINE_FEED);

            printWriter.append("Content-Transfer-Encoding: binary").append(
                    LINE_FEED);
            printWriter.append(LINE_FEED);
            printWriter.flush();

            FileInputStream inputStream = new FileInputStream(uploadFile);
            byte[] buffer = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
//                System.out.println(" bytesRead "+bytesRead);
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
            inputStream.close();

            printWriter.append(LINE_FEED);
            printWriter.flush();
        }

        /**
         * Adds a header field to the request.
         *
         * @param name  - lastName of the header field
         * @param value - value of the header field
         */
        public void addHeaderField(String name, String value) {
            printWriter.append(name + ": " + value).append(LINE_FEED);
            printWriter.flush();
        }

        /**
         * Completes the request and receives response from the com.server.
         *
         * @return a list of Strings as response in case the com.server returned
         * status OK, otherwise an exception is thrown.
         * @throws IOException
         */
        public HttpURLConnection prepareConnection() {
            printWriter.append(LINE_FEED).flush();
            printWriter.append("--" + BOUNDARY + "--").append(LINE_FEED);
            printWriter.close();

            return httpURLConnection;
        }
    }


}