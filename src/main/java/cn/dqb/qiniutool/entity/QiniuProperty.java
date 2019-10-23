package cn.dqb.qiniutool.entity;

import javax.annotation.PostConstruct;
import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @date 2019/10/23 17:45
 */
@Component
@ConfigurationProperties(prefix = "qiniu")
@Data
public class QiniuProperty {

    private String ak;

    private String sk;

    private String bucket;

    @PostConstruct
    public void init() {
        System.out.println(ak);
    }
}
