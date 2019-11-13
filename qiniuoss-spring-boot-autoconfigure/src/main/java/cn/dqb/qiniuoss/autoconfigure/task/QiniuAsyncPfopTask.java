package cn.dqb.qiniuoss.autoconfigure.task;

import cn.dqb.qiniuoss.autoconfigure.entity.QiniuHelper;
import cn.dqb.qiniuoss.autoconfigure.exception.QiniuException;
import com.qiniu.util.StringMap;
import org.apache.commons.lang3.StringUtils;

/**
 * 对已保存在空间中的资源进行云处理并将结果持久化
 *
 * 文档：https://developer.qiniu.com/dora/api/1291/persistent-data-processing-pfop
 *
 * @date 2019/10/18 14:45
 */
public class QiniuAsyncPfopTask extends AbstractQiniuBaseTask {

    /**
     * fops 分隔符
     */
    private final String fopSeparator = ";";
    /**
     * 转码的队列，为空则表示使用默认队列
     */
    private String pipeline;
    /**
     * 处理结果通知接收 URL，七牛将会向你设置的 URL 发起请求，非必填
     */
    private String notifyURL;
    /**
     * 强制执行数据处理
     */
    private boolean force;
    /**
     * 云处理操作列表，用 ; 分隔，当不指定时，尝试从给定的 url 中查询 fops 参数
     */
    private StringBuilder fopsBuilder;

    public QiniuAsyncPfopTask(String url, QiniuHelper qiniuManager) throws Exception {
        super(url, qiniuManager);
    }

    public QiniuAsyncPfopTask addFops(String fop) {
        if (this.fopsBuilder == null) {
            this.fopsBuilder = new StringBuilder(fop);
        }
        this.fopsBuilder.append(this.fopSeparator).append(fop);
        return this;
    }

    public QiniuAsyncPfopTask setPipeLine(String pipeline) {
        this.pipeline = pipeline;
        return this;
    }

    public QiniuAsyncPfopTask setNotifyURL(String notifyURL) {
        this.notifyURL = notifyURL;
        return this;
    }

    public QiniuAsyncPfopTask setForce(boolean force) {
        this.force = force;
        return this;
    }

    @Override
    public String generateRule() throws Exception {
        String fops = generateFops();
        if (StringUtils.isBlank(fops)) {
            throw new QiniuException("fops is empty");
        }
        String encodedEntryUri = generateEncodedEntryURI();
        String pfops = fops + "|saveas/" + encodedEntryUri;
        return pfops;
    }

    /**
     * @return persistentId 任务ID
     * @date 2019/10/21 10:39
     */
    @Override
    public String selfExecute(String rule) throws Exception {
        //设置pipeline参数
        StringMap params = new StringMap().putWhen("force", 1, this.force).putNotEmpty("pipeline", this.pipeline);
        return qiniuHelper.pfop(getSourceBucketKey(), rule, params);
    }

    /**
     * 生成文件名
     */
    private String generateKey() {
        if (StringUtils.isBlank(this.targetKey)) {
            this.targetKey = generateUniqueTargetKey();
        }
        return this.targetKey;
    }

    /**
     * 生成 <EncodedEntryURI>
     */
    private String generateEncodedEntryURI() {
        return qiniuHelper.generateEncodedEntryURI(generateKey());
    }

    private String generateFops() {
        // 说明并没有手动传入 fops，则需要尝试从 url 中提取 fops
        if (this.fopsBuilder == null) {
            return trySearchFops();
        }
        return this.fopsBuilder.toString();
    }

    private String trySearchFops() {
        int index = this.url.indexOf("?");
        if (index != -1) {
            return this.url.substring(index + 1);
        }
        return null;
    }

}
