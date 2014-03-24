package ru.eternalkaif.soundsofnature.tools;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.jetbrains.annotations.NotNull;

public class SongsHttpClient {

    private static final String BASE_URL = "http://www.puturlthere.com/json/";

    @NotNull
    private static AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        asyncHttpClient.get(getAbsoluteUrl(url), params, responseHandler);
    }

    @NotNull
    private static String getAbsoluteUrl(String relativeURL) {
        return BASE_URL + relativeURL;
    }

}
