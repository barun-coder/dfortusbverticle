/**
 * this interface exists just to allow the WebserviceHelper to make callback.
 */

package com.displayfort.RetroFit;

public interface RequestReceiver {
    void onSuccess(int requestCode, String fullResponse, Object dataObject, int StatusCode);
    void onFailure(int requestCode, String errorCode, String message);
    void onNetworkFailure(int requestCode, String message);
}