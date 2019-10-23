package cn.dqb.qiniutool.task;

import cn.dqb.qiniutool.entity.QiniuHelper;
import org.apache.commons.lang3.StringUtils;

/**
 * 七牛视频帧缩略图
 *
 * 文档：https://developer.qiniu.com/dora/api/1313/video-frame-thumbnails-vframe
 *
 * @date 2019/10/18 14:00
 */
public class QiniuVframeTask extends AbstractQiniuBaseTask {

    private String vframeRule;

    private String format;

    private String width;

    private String height;

    private String rotate;

    private String offset;

    /**
     * 是否持久化数据
     */
    private boolean isSaveas;

    /**
     * 预留字段，用来代表：预转持久化数据处理、持久化数据处理
     */
    private int saveasType;

    private AbstractQiniuBaseTask qiniuTask;

    public QiniuVframeTask(String url, QiniuHelper qiniuHelper, boolean isSaveas) throws Exception {
        super(url, qiniuHelper);
        this.isSaveas = isSaveas;
    }

    public QiniuVframeTask setFromat(String format) {
        this.format = format;
        return this;
    }

    public QiniuVframeTask setWidth(String width) {
        this.width = width;
        return this;
    }

    public QiniuVframeTask setHeight(String height) {
        this.height = height;
        return this;
    }

    public QiniuVframeTask setRoate(String rotate) {
        this.rotate = rotate;
        return this;
    }

    public QiniuVframeTask setOffset(String offset) {
        this.offset = offset;
        return this;
    }


    @Override
    public String generateRule() throws Exception {
        if (StringUtils.isBlank(this.format)) {
            throw new Exception("format is empty");
        }
        if (StringUtils.isBlank(this.offset)) {
            throw new Exception("offset is empty");
        }

        StringBuilder ruleBuilder = new StringBuilder("vframe/").append(this.format).append("/offset/")
            .append(this.offset);

        if (StringUtils.isNotBlank(this.width)) {
            ruleBuilder.append("/w/").append(this.width);
        }

        if (StringUtils.isNotBlank(this.height)) {
            ruleBuilder.append("/h/").append(this.height);
        }

        if (StringUtils.isNotBlank(this.rotate)) {
            ruleBuilder.append("/rotate/").append(this.rotate);
        }

        this.vframeRule = ruleBuilder.toString();

        return this.vframeRule;
    }

    /**
     * @return isSaveas = true,返回新的文件名，否则返回完整的图片地址
     * @date 2019/10/18 14:16
     */
    @Override
    public String selfExecute(String rule) throws Exception {
        String url = this.url + "?" + this.vframeRule;
        if (isSaveas) {
            this.qiniuTask = new QiniuSyncSaveasTask(url, qiniuHelper);
            return this.qiniuTask.execute();
        }
        return url;
    }
}
