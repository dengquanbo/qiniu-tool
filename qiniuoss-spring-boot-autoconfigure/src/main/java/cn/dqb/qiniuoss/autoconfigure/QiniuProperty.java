package cn.dqb.qiniuoss.autoconfigure;

import com.qiniu.common.Zone;
import javax.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 七牛配置
 */
@ConfigurationProperties(prefix = QiniuProperty.PREFIX)
@Data
public class QiniuProperty {

    public static final String PREFIX = "qiniu";

    private String accessKey;

    private String secretKey;

    private String bucket;

    private ZoneEnum zone;

    private String host;

    private Zone qiniuZone;

    @PostConstruct
    public void init() {
        if (zone == null) {

        }
        System.out.println(toString());

    }

    @Getter
    @AllArgsConstructor
    public enum ZoneEnum {
        Z0("华东"), Z1("华北"), Z2("华南"), NA0("北美"), AS0("东南亚");
        private String desc;
    }
}
