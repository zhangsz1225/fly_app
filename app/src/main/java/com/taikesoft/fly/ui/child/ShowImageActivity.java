package com.taikesoft.fly.ui.child;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.taikesoft.fly.R;
import com.taikesoft.fly.business.base.BaseActivity;
import com.taikesoft.fly.business.common.utils.StringUtils;
import com.taikesoft.fly.business.config.AppConfig;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;


/**
 * 儿童大图展示
 */

public class ShowImageActivity extends BaseActivity {
    private ImageView ivClose;
    private TextView tvTitle;
    private PhotoView mPhotoView;
    private PhotoViewAttacher mAttacher;
    private String curPhotoPath;
    private String entry;
    @Override
    public void setView() {

    }
    @Override
    public void setListener() {
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    @Override
    protected void initView() {
        super.initView();
        setContentView(R.layout.activity_show_image);
        ivClose = findViewById(R.id.iv_close);
        tvTitle = findViewById(R.id.tv_title);
        mPhotoView = findViewById(R.id.photoView);
        curPhotoPath = getIntent().getStringExtra("photoPath");
        entry = getIntent().getStringExtra("entry");
        if(StringUtils.equals(entry,"notice")){
            ImageLoader.getInstance().displayImage(curPhotoPath, mPhotoView);
        }else{
            ImageLoader.getInstance().displayImage(AppConfig.RICH_IMAGE__URL + curPhotoPath, mPhotoView);
        }

        mAttacher = new PhotoViewAttacher(mPhotoView);
        /*mPhotoView.setImageBitmap(mBitmap);*/
        mAttacher.update();
    }
}
