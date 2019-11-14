package cn.dqb.qiniuosssimples;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import cn.dqb.qiniuoss.autoconfigure.exception.QiniuOssException;
import cn.dqb.qiniuoss.autoconfigure.helper.UploadHelper;
import java.io.File;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

@SpringBootTest
public class UploadHelperTest {

    @Autowired
    private UploadHelper uploadHelper;
    @Value("${qiniu.host}")
    private String host;

    @Test
    public void upload() {
        File file = new File("C:\\Users\\baes\\Desktop\\1089769-20180314132152524-362531288.png");
        String key = uploadHelper.upload(file);
        Assert.notNull(key, "key is empty");
        System.out.println(host + "/" + key);
    }

    @Test
    public void upload1() {
        String key = uploadHelper.upload(
            "http://b.hiphotos.baidu.com/image/pic/item/0eb30f2442a7d9337119f7dba74bd11372f001e0.jpg");
        System.out.println(host + "/" + key);
    }

    @Test
    public void upload2() {
        String key1 = UUID.randomUUID().toString();
        String key2 = uploadHelper.upload(
            "http://b.hiphotos.baidu.com/image/pic/item/0eb30f2442a7d9337119f7dba74bd11372f001e0.jpg",
            key1);
        assertEquals(key1, key2);
        System.out.println(host + "/" + key1);
    }

    /**
     * 地址不完整
     */
    @Test
    public void qiniuOssException1() {
        // http://tu.duoduocdn.com/uploads/day_191114/201911140811059825.jpg
        Throwable exception = assertThrows(QiniuOssException.class, () -> uploadHelper.upload(
            ".com/uploads/day_191114/201911140811059825.jpg"));
        assertEquals("httpGet url failed: E502", exception.getMessage());
    }


    /**
     * 不存在的地址
     */
    @Test
    public void qiniuOssException2() {
        // http://tu.duoduocdn.com/uploads/day_191114/201911140811059825.jpg
        Throwable exception = assertThrows(QiniuOssException.class, () -> uploadHelper.upload(
            "http://tu.duoduocdn.com/uploads/day_191114/201911140811059825哈哈哈.jpg"));
        assertEquals("httpGet url failed and meet 404", exception.getMessage());
    }
}
