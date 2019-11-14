package cn.dqb.qiniuoss.autoconfigure.helper;

import cn.dqb.qiniuoss.autoconfigure.QiniuProperty;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.processing.OperationManager;
import com.qiniu.storage.Configuration;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.qiniu.util.UrlSafeBase64;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import lombok.Getter;
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

    @Getter
    private String host;

    public QiniuHelper(QiniuProperty property) {
        this.bucket = property.getBucket();
        this.auth = Auth.create(property.getAccessKey(), property.getSecretKey());
        this.cfg = new Configuration(Zone.zone0());
        this.ak = property.getAccessKey();
        this.sk = property.getSecretKey();
        /**
         * 用于文件上传
         */
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


//    public void upload(String url) {
//        bucketManager.fetch()
//    }



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
