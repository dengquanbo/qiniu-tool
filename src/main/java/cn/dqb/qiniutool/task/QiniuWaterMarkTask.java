package cn.dqb.qiniutool.task;


import cn.dqb.qiniutool.entity.OkHttpUtil;
import cn.dqb.qiniutool.entity.QiniuHelper;
import cn.dqb.qiniutool.exception.QiniuException;
import com.alibaba.fastjson.JSON;
import com.qiniu.util.UrlSafeBase64;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 图片水印处理
 *
 * 文档： https://developer.qiniu.com/dora/api/1316/image-watermarking-processing-watermark
 *
 * @date 2019/10/21 17:46
 */
@Slf4j
@Accessors(chain = true)
public class QiniuWaterMarkTask extends AbstractQiniuBaseTask {

    private static final String[] POS = {"NorthWest", "North", "NorthEast", "West", "Center", "East", "SouthWest",
        "South", "SouthEast"};
    /**
     * 水印源图片网址
     */
    private String waterMaskUrl;

    /**
     * 透明度，取值范围1-100，默认值为100（完全不透明）。
     */
    private String dissolve;

    /**
     * 水印位置
     */
    private GravityEnum gravityEnum;

    /**
     * 横轴边距，单位:像素(px)，默认值为10。
     */
    private String distanceX;

    /**
     * 纵轴边距，单位:像素(px)，默认值为10。
     */
    private String distanceY;

    /**
     * 水印图片自适应原图的短边比例，ws的取值范围为0-1。具体是指水印图片保持原比例，并短边缩放到原图短边＊ws。
     */
    private String watermarkScale;

    /**
     * 水印图片自适应原图的类型，取值0、1、2、3分别表示为自适应原图的短边、长边、宽、高，默认值为0
     */
    private String watermarkScaleType;

    /**
     * 是否持久化数据
     */
    private boolean isSaveas;

    /**
     * 预留字段，用来代表：预转持久化数据处理、持久化数据处理
     */
    private int saveasType;

    public QiniuWaterMarkTask(String url, QiniuHelper qiniuHelper) throws Exception {
        super(url, qiniuHelper);
    }

    public QiniuWaterMarkTask waterMarkUrl(String waterMaskUrl) {
        this.waterMaskUrl = waterMaskUrl;
        return this;
    }

    public QiniuWaterMarkTask isSaveas(boolean isSaveas) {
        this.isSaveas = isSaveas;
        return this;
    }

    @Override
    public String generateRule() throws Exception {
        if (StringUtils.isBlank(this.waterMaskUrl)) {
            throw new QiniuException("七牛水印图无效");
        }
        StringBuilder builder = new StringBuilder("watermark/1");
        builder.append("/image/").append(UrlSafeBase64.encodeToString(this.waterMaskUrl));
        if (StringUtils.isNotBlank(this.dissolve)) {
            builder.append("/dissolve/").append(this.dissolve);
        }
        if (this.gravityEnum != null) {
            builder.append("/gravity/").append(this.gravityEnum.getGravity());
        }
        if (StringUtils.isNotBlank(this.distanceX)) {
            builder.append("/dx/").append(this.distanceX);
        }
        if (StringUtils.isNotBlank(this.distanceY)) {
            builder.append("/dy/").append(this.distanceY);
        }
        if (StringUtils.isNotBlank(this.watermarkScale)) {
            builder.append("/ws/").append(this.watermarkScale);
        }
        if (StringUtils.isNotBlank(this.watermarkScaleType)) {
            builder.append("/wst/").append(this.watermarkScaleType);
        }
        if (isSaveas) {
            builder.append("|saveas/").append(qiniuHelper.generateEncodedEntryURI(
                StringUtils.isBlank(getTargetKey()) ? generateUniqueTargetKey() : getTargetKey()));
        }

        return builder.toString();
    }

    @Override
    public String selfExecute(String rule) throws Exception {
        String finalUrl = this.url + "?" + rule;
        if (!isSaveas) {
            return finalUrl;
        }
        String result = OkHttpUtil.get(finalUrl);
        String key = JSON.parseObject(result).getString("key");
        log.info("七牛另存为新key = {}", key);
        if (StringUtils.isBlank(key)) {
            throw new QiniuException("key is empty");
        }
        return key;
    }

    @AllArgsConstructor
    @Getter
    enum GravityEnum {
        NORTH_WEST("NorthWest"),
        NORTH("North"),
        NORTH_EAST("NorthEast"),
        WEST("West"),
        CENTER("Center"),
        EAST("East"),
        SouthWest("SouthWest"),
        SOUTH("South"),
        SOUTH_EAST("SouthEast");
        private String gravity;
    }

}
