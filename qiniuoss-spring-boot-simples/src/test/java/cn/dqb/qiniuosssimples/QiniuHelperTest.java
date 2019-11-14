package cn.dqb.qiniuosssimples;

import cn.dqb.qiniuoss.autoconfigure.helper.QiniuHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @date 2019/11/12 18:49
 */
@SpringBootTest
public class QiniuHelperTest {

    @Autowired
    private QiniuHelper qiniuHelper;


    @Test
    public void trySearchBucketSourceKey() {
        String s = QiniuHelper.trySearchBucketSourceKey(
            "");
        System.out.println(s);
    }


}
