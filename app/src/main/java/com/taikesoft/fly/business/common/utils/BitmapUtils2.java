package com.taikesoft.fly.business.common.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class BitmapUtils2 {
    public static int getBitmapSize(Bitmap bitmap){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){    //API 19
            return bitmap.getAllocationByteCount();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1){//API 12
            return bitmap.getByteCount();
        }
        return bitmap.getRowBytes() * bitmap.getHeight();                //earlier version
    }

    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 100;
        if (BitmapUtils2.getBitmapSize(image) > 1024*1024*8) {
            options = 10;
        } if (BitmapUtils2.getBitmapSize(image) > 1024*1024*4) {
            options = 20;
        } else if (BitmapUtils2.getBitmapSize(image) > 1024*1024*3) {
            options = 30;
        } else if (BitmapUtils2.getBitmapSize(image) > 1024*1024*1) {
            options = 50;
        }
        image.compress(Bitmap.CompressFormat.JPEG, options, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        byte[] bitmapArr = baos.toByteArray();
        while ( bitmapArr.length / 1024 > 300) {    //循环判断如果压缩后图片是否大于300kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            options -= 5;//每次都减少10
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            bitmapArr = baos.toByteArray();
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(bitmapArr);//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }
}


