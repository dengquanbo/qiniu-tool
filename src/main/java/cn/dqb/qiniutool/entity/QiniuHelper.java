package cn.dqb.qiniutool.entity;

import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.processing.OperationManager;
import com.qiniu.storage.Configuration;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.qiniu.util.UrlSafeBase64;
import java.nio.charset.StandardCharsets;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * @date 2019/10/23 17:49
 */
public class QiniuHelper {

    private String bucket;

    private String ak;

    private String sk;

    private Auth auth;


    private Configuration cfg;

    public QiniuHelper(QiniuProperty property) {
        this.bucket = property.getBucket();
        this.auth = Auth.create(property.getAk(), property.getSk());
        this.cfg = new Configuration(Zone.zone0());
        this.ak = property.getAk();
        this.sk = property.getSk();
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
