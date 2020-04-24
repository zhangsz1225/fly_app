package com.taikesoft.fly.business.entity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.taikesoft.fly.business.common.utils.BitmapUtils2;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/**
 * Desction:图片信息
 */
public class PhotoInfoEntity implements Serializable {

    private int photoId;
    private String photoPath;
    //private String thumbPath;
    private int width;
    private int height;

    private String photoPathId;
    private Bitmap bitmap;

    public String getPhotoPathId() {
        return photoPathId;
    }

    public void setPhotoPathId(String photoPathId) {
        this.photoPathId = photoPathId;
    }

    public PhotoInfoEntity() {}

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    public Bitmap getBitmap() {
        if (bitmap == null) {
            try {
                bitmap = revitionImageSize(photoPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public boolean equals(Object o) {
        if ( o == null || !(o instanceof PhotoInfoEntity)) {
            return false;
        }
        PhotoInfoEntity info = (PhotoInfoEntity) o;
        if (info == null) {
            return false;
        }

        return TextUtils.equals(info.getPhotoPath(), getPhotoPath());
    }

    private Bitmap revitionImageSize(String path) throws IOException {
        InputStream is = null;
        try {
            is = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inTempStorage = new byte[100 * 1024];
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        opts.inPurgeable = true;
        opts.inSampleSize = 4;
        opts.inInputShareable = true;
        Bitmap bm = BitmapFactory.decodeStream(is, null, opts);
        bm = BitmapUtils2.compressImage(bm);
        return bm;
    }
}
