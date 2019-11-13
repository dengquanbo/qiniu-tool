package cn.dqb.qiniuoss.autoconfigure;

import cn.dqb.qiniuoss.autoconfigure.entity.QiniuHelper;
import cn.dqb.qiniuoss.autoconfigure.upload.FileUploader;
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
    public QiniuHelper qiniuHelper() {
        return new QiniuHelper(qiniuProperty);
    }

    @Bean
    public FileUploader fileUploader(QiniuHelper qiniuHelper) {
        return new FileUploader(qiniuProperty, qiniuHelper);
    }
}
