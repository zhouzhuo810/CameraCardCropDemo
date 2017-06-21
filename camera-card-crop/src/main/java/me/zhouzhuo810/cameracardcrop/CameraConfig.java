package me.zhouzhuo810.cameracardcrop;

import android.os.Environment;

import java.io.File;

/**
 * Config of camera
 * Created by zhouzhuo810 on 2017/6/15.
 */

public class CameraConfig {
    public static final String RATIO_WIDTH = "ratio_width";
    public static final String RATIO_HEIGHT = "ratio_height";
    public static final String PERCENT_WIDTH = "percent_width";
    public static final String NO_CAMERA_SUPPORT_HINT = "no_camera_support_hint";
    public static final String RECT_CORNER_COLOR = "rect_corner_color";
    public static final String TEXT_COLOR = "text_color";
    public static final String MASK_COLOR = "mask_color";
    public static final String HINT_TEXT = "hint_text";
    public static final String IMAGE_PATH = "IMAGE_PATH";

    public static final int DEFAULT_RATIO_WIDTH = 3;
    public static final int DEFAULT_RATIO_HEIGHT= 4;
    public static final float DEFAULT_PERCENT_WIDTH = 0.7f;
    public static final int DEFAULT_RECT_CORNER_COLOR = 0xffffffff;
    public static final int DEFAULT_TEXT_COLOR = 0xffffffff;
    public static final int DEFAULT_MASK_COLOR = 0x3f000000;
    public static final String DEFAULT_NO_CAMERA_SUPPORT_HINT = "No Camera Support.";
    public static final String DEFAULT_HINT_TEXT = "请将方框对准证件拍摄";
    public static final String DEFAULT_IMAGE_PATH = Environment.getExternalStorageDirectory()+ File.separator+"CameraCardCrop/";

    public static final int NO_CAMERA = -1;
}
