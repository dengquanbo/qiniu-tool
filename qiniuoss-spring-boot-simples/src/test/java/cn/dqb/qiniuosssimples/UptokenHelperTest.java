package cn.dqb.qiniuosssimples;

import cn.dqb.qiniuoss.autoconfigure.helper.UptokenHelper;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UptokenHelperTest {

    @Autowired
    private UptokenHelper uptokenHelper;

    @Test
    public void getUploadToken() {
        String token = uptokenHelper.getUploadToken();
        Assert.assertNotNull(token, "token is empty");

        token = uptokenHelper.getUploadToken("image1.png");
        Assert.assertNotNull(token, "token is empty");
    }

}
