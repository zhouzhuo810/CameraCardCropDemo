package me.zhouzhuo810.cameracardcrop;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Activity for taking photo and crop.
 * <p>
 * Created by zhouzhuo810 on 2017/6/15.
 */
public class CropActivity extends Activity {

    private FrameLayout framelayout;
    private String imagePath;
    private RectView rectView;

    private Camera camera;
    private ImageView ivTake;
    private int ratioWidth;
    private int ratioHeight;
    private float percentWidth;

    private boolean flashOpen = false;
    private CameraView cameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Android 5.0 +
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // Android 4.4 +
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        setContentView(R.layout.activity_crop);

        ratioWidth = getIntent().getIntExtra(CameraConfig.RATIO_WIDTH, CameraConfig.DEFAULT_RATIO_WIDTH);
        ratioHeight = getIntent().getIntExtra(CameraConfig.RATIO_HEIGHT, CameraConfig.DEFAULT_RATIO_HEIGHT);
        percentWidth = getIntent().getFloatExtra(CameraConfig.PERCENT_WIDTH, CameraConfig.DEFAULT_PERCENT_WIDTH);
        String noSupportHint = getIntent().getStringExtra(CameraConfig.NO_CAMERA_SUPPORT_HINT);
        imagePath = getIntent().getStringExtra(CameraConfig.IMAGE_PATH);
        String hint = getIntent().getStringExtra(CameraConfig.HINT_TEXT);
        int maskColor = getIntent().getIntExtra(CameraConfig.MASK_COLOR, CameraConfig.DEFAULT_MASK_COLOR);
        int rectCornerColor = getIntent().getIntExtra(CameraConfig.RECT_CORNER_COLOR, CameraConfig.DEFAULT_RECT_CORNER_COLOR);


        ivTake = (ImageView) findViewById(R.id.iv_take);
        ivTake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        framelayout = (FrameLayout) findViewById(R.id.camera);

        ImageView ivBack = (ImageView) findViewById(R.id.iv_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final ImageView ivFlash = (ImageView) findViewById(R.id.iv_flash);
        ivFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flashOpen) {
                    ivFlash.setBackgroundResource(R.drawable.flash_on);
                    offLight();
                } else {
                    ivFlash.setBackgroundResource(R.drawable.flash_off);
                    openLight();
                }
                flashOpen = !flashOpen;
            }
        });

        rectView = (RectView) findViewById(R.id.rect);
        rectView.setMaskColor(maskColor);
        rectView.setCornerColor(rectCornerColor);
        rectView.setHintTextAndTextSize((hint == null || hint.length() == 0 ? hint : CameraConfig.DEFAULT_HINT_TEXT), 30);
        rectView.setRatioAndWidthPercentOfScreen(ratioWidth, ratioHeight, percentWidth);
        rectView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (camera != null) {
                    camera.autoFocus(new Camera.AutoFocusCallback() {
                        @Override
                        public void onAutoFocus(boolean success, Camera camera) {
                        }
                    });
                }
            }
        });

        if (!CameraUtils.checkCameraHardware(this)) {
            Toast.makeText(CropActivity.this, noSupportHint == null ? CameraConfig.DEFAULT_NO_CAMERA_SUPPORT_HINT : noSupportHint, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void takePhoto() {

        try {
            camera.takePicture(null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    if (imagePath == null)
                        imagePath = CameraConfig.DEFAULT_IMAGE_PATH + System.currentTimeMillis() + ".jpg";
                    File file = new File(imagePath);
                    if (file.exists()) {
                        file.delete();
                    }
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }
                    try {
                        BitmapUtils.saveBitMap(CropActivity.this, imagePath, data, rectView.getCropLeft(), rectView.getCropTop(), rectView.getCropWidth(), rectView.getCropHeight(), true);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        Intent intent = new Intent();
                        intent.putExtra(CameraConfig.IMAGE_PATH, imagePath);
                        setResult(RESULT_OK, intent);
                        finish();
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setRectRatio(int w, int h) {
        rectView.updateRatio(w, h);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0x01);
            } else {
                resumeCamera();
            }
        } else {
            resumeCamera();
        }
    }

    private void resumeCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            if (cameraView != null) {
                cameraView.setReleased(true);
            }
            camera = null;
        }
        camera = CameraUtils.open();
        cameraView = new CameraView(this, camera);
        cameraView.setReleased(false);
        framelayout.removeAllViews();
        framelayout.addView(cameraView);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                resumeCamera();
            } else {
                Toast.makeText(CropActivity.this, "No Camera Permission.", Toast.LENGTH_SHORT).show();
            }
        } else {
            resumeCamera();
        }
    }


    /**
     * 打开闪光灯
     */
    public synchronized void openLight() {
        if (camera != null) {
            try {
                Camera.Parameters parameters = camera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(parameters);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭闪光灯
     */
    public synchronized void offLight() {
        if (camera != null) {
            try {
                Camera.Parameters parameters = camera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                camera.setParameters(parameters);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    private void releaseCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            if (cameraView != null) {
                cameraView.setReleased(true);
            }
            camera = null;
        }
    }


}
