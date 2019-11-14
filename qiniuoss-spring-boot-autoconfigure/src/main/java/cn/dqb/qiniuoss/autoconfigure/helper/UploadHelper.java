package cn.dqb.qiniuoss.autoconfigure.helper;


import cn.dqb.qiniuoss.autoconfigure.exception.QiniuOssException;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.FetchRet;
import java.io.File;
import java.util.UUID;

public class UploadHelper {

    private UploadManager uploadManager;

    private BucketManager bucketManager;

    private UptokenHelper uptokenHelper;

    private String bucket;

    public UploadHelper(UploadManager uploadManager, BucketManager bucketManager, UptokenHelper uptokenHelper,
        String bucket) {
        this.uploadManager = uploadManager;
        this.bucketManager = bucketManager;
        this.uptokenHelper = uptokenHelper;
        this.bucket = bucket;
    }

    public String upload(String url) {
        return upload(url, bucket, null);
    }

    public String upload(String url, String key) {
        return upload(url, bucket, key);
    }

    public String upload(String url, String bucket, String key) {
        try {
            FetchRet fetch = bucketManager.fetch(url, bucket, key);
            return fetch.key;
        } catch (com.qiniu.common.QiniuException e) {
            throw new QiniuOssException(e, e.code(), e.error());
        }
    }

    public String upload(File file) {
        return upload(file, UUID.randomUUID().toString());
    }

    public String upload(File file, String newKey) {
        try {
            Response response = uploadManager.put(file, newKey, this.uptokenHelper.getUploadToken());
            if (response.isOK() && response.isJson()) {
                return (String) response.jsonToMap().get("key");
            }
            throw new QiniuOssException(response.error, response.statusCode);
        } catch (QiniuException e) {
            throw new QiniuOssException(e, e.code(), e.error());
        }
    }
}
