package com.example.community.utils;

import okhttp3.*;
import org.json.JSONObject;

import java.io.*;

/**
 * 图片审核工具类
 */

public class ImageDemo {
    public static final String API_KEY = "iQuPxfWNZGNXwFbZxPPXEm4P";
    public static final String SECRET_KEY = "z1uTejw1VAeIsg9zYEKOGYGTiEKzNVTt";

    static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();

    public String ImageDemo1(String text) throws IOException {
        String text1 = "imgUrl="+text;
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, text1);
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rest/2.0/solution/v1/img_censor/v2/user_defined?access_token=" + getAccessToken())
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Accept", "application/json")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        String string = response.body().string();
        return string;
    }


    /**
     * 从用户的AK，SK生成鉴权签名（Access Token）
     *
     * @return 鉴权签名（Access Token）
     * @throws IOException IO异常
     */
    public String getAccessToken() throws IOException {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "grant_type=client_credentials&client_id=" + API_KEY
                + "&client_secret=" + SECRET_KEY);
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/oauth/2.0/token")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        return new JSONObject(response.body().string()).getString("access_token");
    }

}