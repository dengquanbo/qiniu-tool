package cn.dqb.qiniuoss.autoconfigure.entity;

import cn.dqb.qiniuoss.autoconfigure.QiniuProperty;
import com.alibaba.fastjson.JSON;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.processing.OperationManager;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.storage.model.FileListing;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.qiniu.util.UrlSafeBase64;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @date 2019/10/23 17:49
 */
public class QiniuHelper {

    private static final Logger logger = LoggerFactory.getLogger(QiniuHelper.class);
    private static Pattern domainPattern;
    private static String REGEX_DOMAIN = "^((http://)|(https://))?[^//]*?\\.(com|cn|net|org|biz|info|cc|tv)";

    static {
        domainPattern = Pattern.compile(REGEX_DOMAIN, Pattern.CASE_INSENSITIVE);
    }

    private String bucket;
    private String ak;
    private String sk;
    private Auth auth;
    private Configuration cfg;
    private String host;
    /**
     * 用于文件上传
     */
    private BucketManager bucketManager;

    public QiniuHelper(QiniuProperty property) {
        this.bucket = property.getBucket();
        this.auth = Auth.create(property.getAccessKey(), property.getSecretKey());
        this.cfg = new Configuration(Zone.zone0());
        this.ak = property.getAccessKey();
        this.sk = property.getSecretKey();
        this.bucketManager = new BucketManager(auth, cfg);
        this.host = property.getHost();
    }

    /**
     * 获取七牛资源名
     */
    public static String trySearchBucketSourceKey(String url) {
        String urlWithoutQuery = url.split("\\?")[0];
        String domain = getDomain(urlWithoutQuery);
        if (StringUtils.isNotBlank(domain)) {
            return urlWithoutQuery.replace("/@/", "/").replace(domain + "/", "");
        }
        return null;
    }

    private static String getDomain(String url) {
        // 获取完整的域名
        Matcher matcher = domainPattern.matcher(url);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    public String getUploadToken() {
        return getUploadToken(null, 3600, null, true);
    }

    public String getUploadToken(String key) {
        return getUploadToken(key, 3600, null, true);
    }

//    public void upload(String url) {
//        bucketManager.fetch()
//    }

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

    public String pfop(String key, String fops, StringMap params) throws QiniuException {
        return pfop(bucket, key, fops, params);
    }

    public String pfop(String bucket, String key, String fops, StringMap params) throws QiniuException {
        String persistentId = null;
        OperationManager opt = new OperationManager(auth, cfg);
        persistentId = opt.pfop(bucket, key, fops, params);
        return persistentId;
    }

    /**
     * 使用默认配置的 bucket 生成 EncodedEntryURI
     */
    public String generateEncodedEntryURI(String name) {
        return generateEncodedEntryURI(bucket, name);
    }

    /**
     * https://developer.qiniu.com/kodo/api/1276/data-format
     */
    public String generateEncodedEntryURI(String bucketName, String name) {
        return UrlSafeBase64.encodeToString(bucketName + ":" + name);
    }

    public String getSign(String url) throws Exception {
        byte[] h;
        h = hmacSHA1Encrypt(url, sk);
        return ak + ":" + UrlSafeBase64.encodeToString(h);
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
            return this.host + fromKey;
        }
        throw new Exception(fromKey + "移动失败");
    }

    public byte[] hmacSHA1Encrypt(String encryptText, String encryptKey) throws Exception {
        byte[] data = encryptKey.getBytes(StandardCharsets.UTF_8);
        // 根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
        SecretKey secretKey = new SecretKeySpec(data, "HmacSHA1");
        // 生成一个指定 Mac 算法 的 Mac 对象
        Mac mac = Mac.getInstance("HmacSHA1");
        // 用给定密钥初始化 Mac 对象
        mac.init(secretKey);

        byte[] text = encryptText.getBytes(StandardCharsets.UTF_8);
        // 完成 Mac 操作
        return mac.doFinal(text);
    }
}
