package com.example.community.utils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baidubce.http.ApiExplorerClient;
import com.baidubce.http.HttpMethodName;
import com.baidubce.model.ApiExplorerRequest;
import com.baidubce.model.ApiExplorerResponse;
import org.junit.Test;

import java.util.Map;

public class RequestDemo {

    //文本审查
    @Test
    public String RequestDemo1(String text) {

        String path = "https://aip.baidubce.com/rest/2.0/solution/v1/text_censor/v2/user_defined";
        ApiExplorerRequest request = new ApiExplorerRequest(HttpMethodName.POST, path);


        // 设置header参数
        request.addHeaderParameter("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

        // 设置query参数
        request.addQueryParameter("access_token", "24.63b2bf86ee480077d346c2fcb8ee9076.2592000.1671100642.282335-28399383");

        // 设置jsonBody参数
        String jsonBody = text;
        request.setJsonBody(jsonBody);

        ApiExplorerClient client = new ApiExplorerClient();

        try {
            ApiExplorerResponse response = client.sendRequest(request);
            // 返回结果格式为Json字符串
            return response.getResult();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "失败";
    }
}
