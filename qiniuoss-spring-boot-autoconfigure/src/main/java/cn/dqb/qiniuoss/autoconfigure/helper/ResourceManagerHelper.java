package cn.dqb.qiniuoss.autoconfigure.helper;

import com.alibaba.fastjson.JSON;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.storage.model.FileListing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 资源管理
 */
public class ResourceManagerHelper {

    private static final Logger logger = LoggerFactory.getLogger(ResourceManagerHelper.class);

    private BucketManager bucketManager;

    private String bucket;

    public ResourceManagerHelper(BucketManager bucketManager, String bucket) {
        this.bucketManager = bucketManager;
        this.bucket = bucket;
    }

    public boolean isExistBucket(String fileName) throws QiniuException {
        return this.isExistBucket(this.bucket, fileName);
    }

    public void listFiles(String fileName) throws QiniuException {
        FileListing fileListing = bucketManager.listFiles(bucket, fileName, null, 100, null);
        FileInfo[] items = fileListing.items;
        for (FileInfo item : items) {
            System.out.println(item.key);
        }
    }

    public boolean isExistBucket(String bucket, String fileName) throws QiniuException {
        FileListing fileListing = bucketManager.listFiles(bucket, fileName, null, 100, null);
        FileInfo[] items = fileListing.items;
        logger.info("listFilesV2,fileName = {},result = {}", fileListing, JSON.toJSONString(items));
        for (FileInfo item : items) {
            System.out.println(item.key);
            if (item.key.equals(fileName)) {
                return true;
            }
        }
        return false;
    }

    public String move(String fromBucket, String fromKey, String toBucket) throws Exception {
        Response response = bucketManager.move(fromBucket, fromKey, toBucket, fromKey);
        logger.info("fromKey = {},result = {}", fromKey, JSON.toJSONString(response));
        if (response.isOK() && response.isJson()) {
            return fromKey;
        }
        throw new Exception(fromKey + "移动失败");
    }
}
