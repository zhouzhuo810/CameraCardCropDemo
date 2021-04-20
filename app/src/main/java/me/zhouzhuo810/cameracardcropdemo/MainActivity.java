package me.zhouzhuo810.cameracardcropdemo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import me.zhouzhuo810.cameracardcrop.CameraConfig;
import me.zhouzhuo810.cameracardcrop.CropActivity;

/**
 * @author zhouzhuo810
 */
public class MainActivity extends AppCompatActivity {

    private ImageView ivPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivPic = (ImageView) findViewById(R.id.iv_pic);
    }

    public void takePhoto(View v) {
        Intent intent = new Intent(MainActivity.this, CropActivity.class);
//        intent.putExtra(CameraConfig.RATIO_WIDTH, 855);
//        intent.putExtra(CameraConfig.RATIO_HEIGHT, 541);
        intent.putExtra(CameraConfig.NEED_WRITE_STORAGE_PERMISSION, false);
        intent.putExtra(CameraConfig.RATIO_WIDTH, 4);
        intent.putExtra(CameraConfig.RATIO_HEIGHT, 3);
        intent.putExtra(CameraConfig.PERCENT_LARGE, 0.8f);
        intent.putExtra(CameraConfig.MASK_COLOR, 0x2f000000);
        intent.putExtra(CameraConfig.TOP_OFFSET, 0);
        intent.putExtra(CameraConfig.RECT_CORNER_COLOR, 0xff00ff00);
        intent.putExtra(CameraConfig.TEXT_COLOR, 0xffffffff);
        intent.putExtra(CameraConfig.HINT_TEXT, "请将方框对准证件拍照");
        intent.putExtra(CameraConfig.IMAGE_PATH, getApplicationContext().getExternalCacheDir().getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        startActivityForResult(intent, 0x01);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 0x01) {
                String path = data.getStringExtra(CameraConfig.IMAGE_PATH);
                Uri uriForFile = FileProvider.getUriForFile(MainActivity.this,
                    BuildConfig.APPLICATION_ID + ".fileprovider", new File(path));
                ivPic.setImageURI(uriForFile);
            }
        }
    }


}
