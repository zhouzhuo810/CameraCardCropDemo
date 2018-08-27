# CameraCardCropDemo
一个卡片（证件）拍照裁剪框架。

## Gradle

```
compile 'me.zhouzhuo810.cameracardcrop:camera-card-crop:1.0.5'
```

## Features

- Support Android 6.0 permissions.

## What does it look like ?

![image1](https://github.com/zhouzhuo810/CameraCardCropDemo/blob/master/cameracrop2.png)

![image1](https://github.com/zhouzhuo810/CameraCardCropDemo/blob/master/cameracrop1.png)


## Notice

```
card

---------------------
|       width       |
|                   |
|                   |height
|                   |
---------------------
phone
------------------------------------
|                                   |
|                                   |
|                                   |
|                                   |
|                                   |
|      mask                         |
|                                   |
|                width              |
|    ------------------------       |
|    |                       |      |
|    |                height |      | screen height
|    |         rect          |      |
|    |                       |      |
|    ------------------------       |
|                                   |
|                                   |
|                                   |
|                                   |
|                                   |
|            screen width           |
-------------------------------------
CameraConfig.RATIO_WIDTH = card's width
CameraConfig.RATIO_HEIGHT = card's height
if CameraConfig.RATIO_WIDTH >= CameraConfig.RATIO_HEIGHT {
    CameraConfig.PERCENT_LARGE = rect's width / screen's width
} else {
    CameraConfig.PERCENT_LARGE = rect's height / screen's height
}
```

## Usage

### step 1. Add Activity in your AndroidManifest.xml file.

```xml
    <activity android:name="me.zhouzhuo810.cameracardcrop.CropActivity"
        android:screenOrientation="portrait"
        android:theme="@style/Theme.AppCompat.NoActionBar">
    </activity>
```

### step 2. Add permissions in your AndroidManifest.xml file.

```xml
    <uses-permission android:name="android.permission.CAMERA"/>
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS"/>

    <uses-feature android:name="android.hardware.camera" />
    <uses-feature android:name="android.hardware.camera.autofocus" />
```

### step 3. Example for use.

```java
    public void takePhoto(View v) {
        Intent intent = new Intent(MainActivity.this, CropActivity.class);
        intent.putExtra(CameraConfig.RATIO_WIDTH, 855);
        intent.putExtra(CameraConfig.RATIO_HEIGHT, 541);
        intent.putExtra(CameraConfig.PERCENT_LARGE, 0.8f);
        intent.putExtra(CameraConfig.MASK_COLOR, 0x2f000000);
        intent.putExtra(CameraConfig.RECT_CORNER_COLOR, 0xff00ff00);
        intent.putExtra(CameraConfig.TEXT_COLOR, 0xffffffff);
        intent.putExtra(CameraConfig.HINT_TEXT, "请将方框对准证件拍照");
        intent.putExtra(CameraConfig.IMAGE_PATH, Environment.getExternalStorageDirectory().getAbsolutePath()+"/CameraCardCrop/"+System.currentTimeMillis()+".jpg");
        startActivityForResult(intent, 0x01);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 0x01) {
                String path = data.getStringExtra(CameraConfig.IMAGE_PATH);
                ivPic.setImageURI(Uri.parse("file://"+path));
            }
        }
    }

```

## Log

- 1.0.3 Fix camera.cancelAutoFocus() Exception after camera.release().
- 1.0.2 Fix flashlight not support bugs.
- 1.0.1 Revise layout size.

## License

```
Copyright © zhouzhuo810

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
