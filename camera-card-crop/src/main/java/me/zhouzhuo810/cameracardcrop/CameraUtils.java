package me.zhouzhuo810.cameracardcrop;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.util.Log;

/**
 * camera tools
 * Created by zhouzhuo810 on 2017/6/15.
 */

public class CameraUtils {

    /**
     * Check if supports camera hardware.
     * @param context context
     * @return true/false
     */
    public static boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    /**
     * Get the Camera instance
     * @return camera
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    public static int findCameraId(boolean front) {
        final Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        final int cameraCount = Camera.getNumberOfCameras();
        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if ((front&&cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) || (!front&&cameraInfo.facing==Camera.CameraInfo.CAMERA_FACING_BACK)) {
                return i;
            }
        }
        return CameraConfig.NO_CAMERA;
    }


    /**
     * 打开摄像头
     *
     * @return Camera
     */
    public static Camera open() {
        int numCameras = Camera.getNumberOfCameras();
        if (numCameras == 0) {
            Log.e("CameraCardCrop", "No cameras!");
            return null;
        }
        int cameraId = findCameraId(false);
        Camera camera;
        if (cameraId < numCameras && cameraId >= 0) {
            Log.d("CameraCardCrop", "Opening camera #" + cameraId);
            camera = Camera.open(cameraId);
        } else {
            camera = Camera.open(0);
        }
        return camera;
    }


}
