package cn.dqb.qiniuoss.autoconfigure.entity;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OkHttpUtil {

    private static final Logger LOG = LoggerFactory.getLogger(OkHttpUtil.class);
    private static OkHttpClient client;

    static {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(5, TimeUnit.SECONDS).readTimeout(5, TimeUnit.SECONDS).writeTimeout(5, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true).sslSocketFactory(overlockCard());
        client = builder.build();

    }

    public static String get(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            String result = response.body().string();
            LOG.info("get请求地址url = {},result = {}", url, result);
            return result;
        }
    }

    public static String post(String url, String json) throws IOException {
        final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON_TYPE, json);
        Request request = new Request.Builder().url(url).post(body).addHeader("Connection", "keep-alive").build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    /**
     * 以表单的形式提交数据
     */
    public static String post(String url, FormBody formBody) throws IOException {
        Request request = new Request.Builder().url(url).post(formBody).build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    /**
     * 异步 以表单的形式提交数据
     */
    public static void post(String url, RequestBody requestBody, final Callback callback) {
        Request request = new Request.Builder().url(url).post(requestBody).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (callback != null) {
                    callback.onFailure(call, e);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (callback != null) {
                    callback.onResponse(call, response);
                }
            }
        });
    }

    /**
     * 忽略所有https证书
     */
    private static SSLSocketFactory overlockCard() {
        SSLContext ssl;
        final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }};

        try {
            ssl = SSLContext.getInstance("SSL");
            ssl.init(null, trustAllCerts, new java.security.SecureRandom());
            return ssl.getSocketFactory();
        } catch (Exception e) {
            LOG.error("ssl出现异常", e);
        }
        return null;
    }

}
