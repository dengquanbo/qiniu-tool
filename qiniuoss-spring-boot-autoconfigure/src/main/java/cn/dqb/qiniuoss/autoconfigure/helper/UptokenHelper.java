package cn.dqb.qiniuoss.autoconfigure.helper;

import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;

/**
 */
public class UptokenHelper {

    private Auth auth;

    private String bucket;

    public UptokenHelper(Auth auth, String bucket) {
        this.auth = auth;
        this.bucket = bucket;
    }

    public String getUploadToken() {
        return getUploadToken(null, 3600, null, true);
    }

    public String getUploadToken(String key) {
        return getUploadToken(key, 3600, null, true);
    }


    public String getUploadToken(String key, long expires) {
        return getUploadToken(key, expires, null, true);
    }

    public String getUploadToken(String key, long expires, StringMap policy) {
        return getUploadToken(key, expires, policy, true);
    }

    /**
     * 生成上传token
     *
     * 文档： https://developer.qiniu.com/kodo/manual/1208/upload-token
     *
     * @param key key，可为 null
     * @param expires 有效时长，单位秒。
     * @param policy 上传策略的其它参数，如 new StringMap().put("endUser", "uid").putNotEmpty("returnBody", "")。 scope通过
     * bucket、key间接设置，deadline 通过 expires 间接设置
     * @param strict 是否去除非限定的策略字段，默认true
     * @return 生成的上传token
     */
    public String getUploadToken(String key, long expires, StringMap policy, boolean strict) {
        return this.auth.uploadToken(this.bucket, key, expires, policy, strict);
    }
}
