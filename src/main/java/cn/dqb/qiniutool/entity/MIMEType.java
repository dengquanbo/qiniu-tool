package cn.dqb.qiniutool.entity;

import java.util.HashMap;
import java.util.Map;

public class MIMEType {

    public static final Map<String, String> types = new HashMap<String, String>() {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        {
            put("image/gif", ".gif");
            put("image/jpeg", ".jpeg");
            put("image/jpg", ".jpg");
            put("image/png", ".png");
            put("image/bmp", ".bmp");
            put("image/webp", ".webp");
            put("video/mp4", ".mp4");
            put("audio/mp3", ".mp3");
            put("audio/x-ms-wma", ".wma");
            put("audio/wav", ".wav");
            put("audio/mpegurl", ".m3u");
            put("audio/mpeg", ".mp3");
        }
    };

    public static String getSuffix(String mime) {
        return MIMEType.types.get(mime);
    }

}