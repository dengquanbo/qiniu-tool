package cn.dqb.qiniuoss.autoconfigure;

import cn.dqb.qiniuoss.autoconfigure.helper.QiniuHelper;
import cn.dqb.qiniuoss.autoconfigure.helper.ResourceManagerHelper;
import cn.dqb.qiniuoss.autoconfigure.helper.UploadHelper;
import cn.dqb.qiniuoss.autoconfigure.helper.UptokenHelper;
import com.qiniu.common.Zone;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(QiniuProperty.class)
public class QiniuOssAutoConfiguration {

    @Autowired
    private QiniuProperty qiniuProperty;

    @Bean
    public Auth auth() {
        return Auth.create(qiniuProperty.getAccessKey(), qiniuProperty.getSecretKey());
    }

    @Bean
    public com.qiniu.storage.Configuration cfg() {
        return new com.qiniu.storage.Configuration(Zone.zone0());
    }

    @Bean
    public BucketManager bucketManager(Auth auth, com.qiniu.storage.Configuration cfg) {
        return new BucketManager(auth, cfg);
    }


    @Bean
    public UploadManager uploadManager(com.qiniu.storage.Configuration cfg) {
        return new UploadManager(cfg);
    }

    /**
     * 自定义工具类
     */

    @Bean
    public UptokenHelper uptokenHelper(Auth auth) {
        return new UptokenHelper(auth, qiniuProperty.getBucket());
    }

    @Bean
    public ResourceManagerHelper resourceManagerHelper(BucketManager bucketManager) {
        return new ResourceManagerHelper(bucketManager, qiniuProperty.getBucket());
    }

    @Bean
    public QiniuHelper qiniuHelper() {
        return new QiniuHelper(qiniuProperty);
    }

    @Bean
    public UploadHelper uploadHelper(UploadManager uploadManager, BucketManager bucketManager,
        UptokenHelper uptokenHelper) {
        return new UploadHelper(uploadManager, bucketManager, uptokenHelper, qiniuProperty.getBucket());
    }
}
