package me.zhouzhuo810.cameracardcrop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * image tools
 *
 * @author zhouzhuo810
 * @date 2017/6/16
 */
public class BitmapUtils {

    public static Bitmap setBitmapSize(Bitmap bitmap, int newWidth, int newHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = (newWidth * 1.0f) / width;
        float scaleHeight = (newHeight * 1.0f) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }

    public static File saveBitMap(Context context, String filePath, final byte[] data, int rectLeft, int rectTop, int rectWidth, int rectHeight, boolean isNeedCut) throws IOException {
        File file = new File(filePath);
        try {
            FileOutputStream fos = new FileOutputStream(file, true);
            fos.write(data);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        int degrees = getExifRotateDegree(filePath);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap photo = BitmapFactory.decodeByteArray(data, 0, data.length, options);
        //图片竖屏
        photo = rotateBitmap(photo, degrees);
        int scW = ScreenUtils.getScreenWidth(context);
        int scH = ScreenUtils.getScreenHeight(context);
        if (scW > scH) {
            int c = scW;
            scW = scH;
            scH = c;
        }
        //减去状态栏的高度
        int statusBarHeight = (int) Math.ceil(20 * context.getResources().getDisplayMetrics().density);
        scH = scH - statusBarHeight;
        //将图片宽度缩放到屏幕宽度一致
        float ratio = scW * 1.0f / photo.getWidth();
        photo = Bitmap.createScaledBitmap(photo, (int) (photo.getWidth() * ratio), (int) (photo.getHeight() * ratio), true);
        if (isNeedCut && rectLeft > 0) {
            float widthScale = photo.getWidth() * 1.0f / scW;
            float heightScale = photo.getHeight() * 1.0f / scH;
            photo = Bitmap.createBitmap(photo, (int) (rectLeft * widthScale), (int) (rectTop * heightScale), (int) (rectWidth * widthScale), (int) (rectHeight * heightScale));
        }
        if (photo != null) {
            compressImageToFile(photo, file);
            return file;
        } else {
            return null;
        }

    }

    private static int getExifRotateDegree(String path) {
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            return getExifRotateDegrees(orientation);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static int getExifRotateDegrees(int exifOrientation) {
        int degrees = 0;
        switch (exifOrientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                degrees = 0;
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                degrees = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                degrees = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                degrees = 270;
                break;
            default:
                break;
        }
        return degrees;
    }

    private static Bitmap rotateBitmap(Bitmap origin, float degrees) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees);
        Bitmap bitmap = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (bitmap.equals(origin)) {
            return bitmap;
        }
        origin.recycle();
        return bitmap;
    }


    public static void compressImageToFile(Bitmap bmp, File file) {
        int options = 100;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
