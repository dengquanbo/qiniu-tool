package cn.dqb.qiniutool;

import cn.dqb.qiniuoss.autoconfigure.entity.QiniuHelper;
import com.qiniu.common.QiniuException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

/**
 * @date 2019/11/12 18:49
 */
@SpringBootTest
public class QiniuHelperTest {

    @Autowired
    private QiniuHelper qiniuHelper;

    @Test
    public void exist() throws QiniuException {
        Assert.isTrue(qiniuHelper.isExistBucket(
            "fmh_v20191112_GMWPS3BmU2Xkenx2p3VLga337fN4hSJO0rknIy7uHcp6O6WTQMYUXdqfD9MOjtLt614wmlci+qaftBXK19DLwQ==.mp4"));

        Assert.isTrue(qiniuHelper.isExistBucket(
            "fmh_m20191024_CP0KlnY87zjcq722KQrxcJIdvPNlZDO0KBQyn/Fj0pl46FRDTYePuWg2wcv0EJ4CbEp58wxT2/n2puqXjSMc7w=="));

        Assert.isTrue(qiniuHelper.isExistBucket(
            "fmh_m20191112_GMWPS3BmU2W9g9dl+/DMxCKUqwAS4wyOoD1KMo+oVEGZvPy4wPoI0jJ8Luu0TGERti3+2VRjx70lT18c7s+MHw=="));
    }

    @Test
    public void listFiles() throws QiniuException {
        qiniuHelper.listFiles("fmh_m20191113");
    }

    @Test
    public void trySearchBucketSourceKey() {
        String s = QiniuHelper.trySearchBucketSourceKey(
            "https://rmhcdn.kangyanghongya.com/fmh_m20191024_CP0KlnY87zjcq722KQrxcJIdvPNlZDO0KBQyn/Fj0pl46FRDTYePuWg2wcv0EJ4CbEp58wxT2/n2puqXjSMc7w==?imageMogr2/crop/!810x608a0a688/format/jpg");
        System.out.println(s);
    }

    @Test
    public void move() throws Exception {
        String url = qiniuHelper.move("hongya-pgc", "d2a98f48-ea56-40d4-afcc-5d23aaf83d1e", "hongya");
        System.out.println(url);
    }
}
