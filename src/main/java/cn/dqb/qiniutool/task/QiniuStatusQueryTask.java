package cn.dqb.qiniutool.task;

import com.alibaba.fastjson.JSON;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 文档： https://developer.qiniu.com/dora/api/1294/persistent-processing-status-query-prefop
 *
 * @date 2019/10/21 10:56
 */
public class QiniuStatusQueryTask implements IQiniuBaseTask {

    private String persistentId;

    private OkHttpClient client = new OkHttpClient();

    private String queryUrl = "http://api.qiniu.com/status/get/prefop?id=";

    public QiniuStatusQueryTask(String persistentId) {
        this.persistentId = persistentId;
    }

    @Override
    public String generateRule() throws Exception {
        return null;
    }

    @Override
    public String execute() throws Exception {
        String result = null;
        String url = queryUrl + persistentId;
        Request request = new Request.Builder().url(url).build();
        Response resp = client.newCall(request).execute();
        if (resp.isSuccessful()) {
            result = resp.body().string();
//            QiNiuYunCallbackBody qiNiuYunCallbackBody = JSON.parseObject(result, QiNiuYunCallbackBody.class);
////////            CallbackBodyItems item = qiNiuYunCallbackBody.getItems().get(0);
////////            return String.valueOf(item.getCode());
            return "1";
        }
        return "3";
    }
}
