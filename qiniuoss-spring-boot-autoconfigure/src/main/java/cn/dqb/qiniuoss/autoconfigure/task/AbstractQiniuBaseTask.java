package cn.dqb.qiniuoss.autoconfigure.task;

import cn.dqb.qiniuoss.autoconfigure.entity.MIMEType;
import cn.dqb.qiniuoss.autoconfigure.entity.QiniuHelper;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * @date 2019/10/18 10:44
 */
public abstract class AbstractQiniuBaseTask implements IQiniuBaseTask {

    private static String REGEX_DOMAIN = "^((http://)|(https://))?[^//]*?\\.(com|cn|net|org|biz|info|cc|tv)";
    private static Pattern domainPattern;

    static {
        domainPattern = Pattern.compile(REGEX_DOMAIN, Pattern.CASE_INSENSITIVE);
    }

    protected String url;

    protected QiniuHelper qiniuHelper;
    /**
     * 新的文件名，不指定时，使用默认的生成规则
     */
    @Getter
    @Setter
    protected String targetKey;
    /**
     * 原始文件名
     */
    @Getter
    @Setter
    private String sourceBucketKey;

    public AbstractQiniuBaseTask(String url, QiniuHelper qiniuHelper) throws Exception {
        if (StringUtils.isBlank(url)) {
            throw new Exception("url is empty");
        }
        this.url = url;
        this.qiniuHelper = qiniuHelper;
    }

    public static void main(String[] args) {
        String url = "http://demoimg.thecover.cn/video1569745251037404688.mp4";
        String domain = getDomain(url);
        System.out.println(domain);
    }

    public static String getDomain(String url) {
        //Pattern p = Pattern.compile("(?<=http://|\\.)[^.]*?\\.(com|cn|net|org|biz|info|cc|tv)", Pattern.CASE_INSENSITIVE);
        // 获取完整的域名
        Matcher matcher = domainPattern.matcher(url);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    /**
     * 重新生成一个新的文件名
     *
     * @date 2019/10/18 10:47
     */
    public String generateUniqueTargetKey() {
        return "newscontent-" + UUID.randomUUID().toString() + "-" + System.currentTimeMillis() + verifyFileType();
    }

    /**
     * 查找原始的key
     */
    public String getSourceBucketKey() {
        if (StringUtils.isBlank(this.sourceBucketKey)) {
            this.sourceBucketKey = trySearchBucketSourceKey();
        }
        return this.sourceBucketKey;
    }

    private String trySearchBucketSourceKey() {
        // 没有查询参数的地址
        String urlWithoutQuery = this.url.split("\\?")[0];
        String domain = getDomain(urlWithoutQuery);
        if (StringUtils.isNotBlank(domain)) {
            return urlWithoutQuery.replace(domain + "/", "");
        }
        return null;
    }

    /**
     * 确认文件类型
     *
     * @return 返回文件类型
     * @date 2019/10/18 10:50
     */
    private String verifyFileType() {
        try {
            URL httpUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) httpUrl.openConnection();
            connection.setInstanceFollowRedirects(true);
            connection.setUseCaches(true);
            if (HttpURLConnection.HTTP_OK != connection.getResponseCode()) {
                return "";
            }
            String suffix = MIMEType.getSuffix(connection.getContentType().split(";")[0]);
            connection.disconnect();
            return suffix;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public String execute() throws Exception {
        String rule = generateRule();
        return selfExecute(rule);
    }

    abstract String selfExecute(String rule) throws Exception;

}
