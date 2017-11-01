package com.ljt.zhihunews.support.lib;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Administrator on 2017/10/6/006.
 */

public class Http {
    public static final String CHARSET = "UTF-8";
    public static String TAG= Http.class.getSimpleName();

    //根据url拿到链接数据--->>>JSON字符串
    /*
    * {"date":"20171031","stories":[{"images":["https:\/\/pic3.zhimg.com\/v2-fc4a8b2f412699488e548f94ea330c32.jpg"],
    * "type":0,"id":9654792,"ga_prefix":"103122","title":"小事 · 时间太快了，也太慢了"}]}
    *
    * */
    public static String get(String address) throws IOException {

         Log.d(TAG,TAG+" get(----->>>address= "+address.toString());
        URL url = new URL(address);

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");

        try {
            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                in.close();

                return response.toString();
            } else {
                throw new IOException("Network Error - response code: " + con.getResponseCode());
            }
        } finally {
            con.disconnect();
        }
    }

    public static String get(String baseUrl, String key, String value) throws IOException {
        return get(baseUrl + "?" + concatKeyValue(key, value));
    }

    public static String get(String baseUrl, int suffix) throws IOException {
        return get(baseUrl + suffix);
    }

    /*
    *
    * 给url编码
    * */
    public static String get(String baseUrl, String suffix) throws IOException {
        return get(baseUrl + encodeString(suffix));
    }

    private static String concatKeyValue(String key, String value) {
        return encodeString(key) + "=" + encodeString(value).replace("+", "%20");
    }

    /*
    * 对字符串进行编码
    * */
    private static String encodeString(String str) {

        try {
            return URLEncoder.encode(str, CHARSET);
        } catch (UnsupportedEncodingException ignored) {
            return "";
        }
    }
}
