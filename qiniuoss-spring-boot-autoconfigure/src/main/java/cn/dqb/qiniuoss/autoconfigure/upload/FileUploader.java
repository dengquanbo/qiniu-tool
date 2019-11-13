package cn.dqb.qiniuoss.autoconfigure.upload;


import cn.dqb.qiniuoss.autoconfigure.QiniuProperty;
import cn.dqb.qiniuoss.autoconfigure.entity.QiniuHelper;
import cn.dqb.qiniuoss.autoconfigure.exception.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import java.io.File;
import java.util.UUID;

public class FileUploader {

    private UploadManager uploadManager;

    private QiniuHelper qiniuHelper;

    private String host;

    public FileUploader(QiniuProperty property, QiniuHelper qiniuHelper) {
        Configuration cfg = new Configuration(property.getQiniuZone());
        this.uploadManager = new UploadManager(cfg);
        this.qiniuHelper = qiniuHelper;
        this.host = property.getHost();
    }

    public String upload(File file) throws Exception {
        return upload(file, UUID.randomUUID().toString());
    }

    public String upload(File file, String newKey) throws Exception {
        Response response = uploadManager.put(file, newKey, qiniuHelper.getUploadToken());
        if (response.isOK() && response.isJson()) {
            return (String) response.jsonToMap().get("key");
        }
        throw new QiniuException(response.error);
    }
}
