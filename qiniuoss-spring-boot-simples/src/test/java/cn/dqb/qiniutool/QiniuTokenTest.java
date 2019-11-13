package cn.dqb.qiniutool;

import cn.dqb.qiniuoss.autoconfigure.entity.QiniuHelper;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @date 2019/10/25 9:17
 */
@SpringBootTest
public class QiniuTokenTest {

    @Autowired
    private QiniuHelper qiniuHelper;

    @Test
    public void getUploadToken() {
        String token = qiniuHelper.getUploadToken();
        Assert.assertNotNull(token, "token is empty");

        token = qiniuHelper.getUploadToken("image1.png");
        Assert.assertNotNull(token, "token is empty");

    }

}
