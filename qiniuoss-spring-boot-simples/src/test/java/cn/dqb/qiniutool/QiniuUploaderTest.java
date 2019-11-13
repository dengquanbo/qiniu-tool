package cn.dqb.qiniutool;

import cn.dqb.qiniuoss.autoconfigure.upload.FileUploader;
import java.io.File;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

@SpringBootTest
public class QiniuUploaderTest {

    @Autowired
    private FileUploader fileUploader;

    @Value("${qiniu.host}")
    private String host;

    @Test
    public void upload() {
        File file = new File("D:\\封面新闻\\图片\\404665.png");
        try {
            String key = fileUploader.upload(file);
            Assert.notNull(key, "key is empty");
            System.out.println(host + "/" + key);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
