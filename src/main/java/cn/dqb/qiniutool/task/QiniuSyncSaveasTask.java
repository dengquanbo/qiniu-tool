package cn.dqb.qiniutool.task;

import cn.dqb.qiniutool.entity.OkHttpUtil;
import cn.dqb.qiniutool.entity.QiniuHelper;
import cn.dqb.qiniutool.exception.QiniuException;
import com.alibaba.fastjson.JSON;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 七牛处理结果另存，同步的方式
 *
 * 文档：https://developer.qiniu.com/dora/api/1305/processing-results-save-saveas
 *
 * @date 2019/10/18 9:26
 */
public class QiniuSyncSaveasTask extends AbstractQiniuBaseTask {

    private static final Logger logger = LoggerFactory.getLogger(QiniuSyncSaveasTask.class);
    /**
     * saveAs 基本格式
     */
    private String saveAsRule = "<URL>|saveas/<EncodedEntryURI>/sign/<SIGN>";

    public QiniuSyncSaveasTask(String url, QiniuHelper qiniuHelper) throws Exception {
        super(url, qiniuHelper);
    }

    @Override
    public String generateRule() throws Exception {
        String url = generateURL();

        String encodedEntryUri = generateEncodedEntryURI();

        String sign = generateSign(encodedEntryUri);

        // 最后替换 sign
        saveAsRule = saveAsRule.replace("<URL>", url).replace("<EncodedEntryURI>", encodedEntryUri)
            .replace("<SIGN>", sign);

        System.out.println(saveAsRule);

        return saveAsRule;
    }


    /**
     * 生成 <URL>
     */
    private String generateURL() {
        return url;
    }


    /**
     * 生成 <EncodedEntryURI>
     */
    private String generateEncodedEntryURI() {
        if (StringUtils.isBlank(this.targetKey)) {
            this.targetKey = generateUniqueTargetKey();
        }
        return qiniuHelper.generateEncodedEntryURI(targetKey);
    }

    /**
     * 生成 <SIGN>，格式为 url|saveas/encodedEntryUri
     *
     * 注意：在 savesa 命令中，生成 sign 时， url 为不含 Scheme 部分，即去除 http:// 或者 https://
     */
    private String generateSign(String encodedEntryUri) throws Exception {
        String urlWithoutScheme = url.replaceAll("https://", "").replaceAll("http://", "");
        String signStr = urlWithoutScheme + "|saveas/" + encodedEntryUri;
        return qiniuHelper.getSign(signStr);
    }

    /**
     * @return 另存为的文件名
     */
    @Override
    public String selfExecute(String rule) throws IOException {
        logger.info("七牛另存为rule = {}", rule);
        String result = OkHttpUtil.get(rule);
        String key = JSON.parseObject(result).getString("key");
        logger.info("七牛另存为新key = {}", key);
        if (StringUtils.isBlank(key)) {
            throw new QiniuException("key is empty");
        }
        return key;
    }
}
