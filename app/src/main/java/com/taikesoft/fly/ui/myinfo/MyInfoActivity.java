package com.taikesoft.fly.ui.myinfo;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.squareup.otto.BasicBus;
import com.taikesoft.fly.R;
import com.taikesoft.fly.business.base.BaseActivity;
import com.taikesoft.fly.business.common.view.CircleImageView;
import com.taikesoft.fly.business.common.view.SelectPhotoPopupWindow;
import com.taikesoft.fly.business.base.basebean.BaseData;
import com.taikesoft.fly.business.config.AppConfig;
import com.taikesoft.fly.business.config.ConfigPath;
import com.taikesoft.fly.business.constant.ResultStatus;
import com.taikesoft.fly.business.constant.SysCode;
import com.taikesoft.fly.business.context.AppContext;
import com.taikesoft.fly.ui.login.bean.LoginInfo;
import com.taikesoft.fly.business.storage.SharedPreferencesManager;
import com.taikesoft.fly.business.common.utils.CommonUtil;
import com.taikesoft.fly.business.common.utils.MessageBus;
import com.taikesoft.fly.business.common.utils.NetStateUtil;
import com.taikesoft.fly.business.common.utils.StringUtils;
import com.taikesoft.fly.business.common.utils.TDevice;
import com.taikesoft.fly.business.common.utils.TLog;
import com.taikesoft.fly.business.webapi.MainApi;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 */
public class MyInfoActivity extends BaseActivity implements View.OnClickListener {

    private CircleImageView mIvPortrait;
    private SelectPhotoPopupWindow selectPhotoPopupWindow;

    private DisplayImageOptions mOption = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.my_leftbar_pho_header)
            .showImageForEmptyUri(R.drawable.my_leftbar_pho_header)
            .cacheOnDisk(false).bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.EXACTLY).build();

    private static String PICTURE_NAME = "";
    private TextView tvPersonName;
    private TextView tvTelephoneNo;
    private TextView tvPersonPlace;
    private TextView tvDept;

    public BasicBus mBasicBus = MessageBus.getBusInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBasicBus.register(this);
    }

    @Override
    public void setView() {
        setContentView(R.layout.activity_person_message);
    }

    @Override
    protected void initView() {
        super.initView();
        setTitle(AppContext.mResource.getString(R.string.person_message));
        mIvPortrait = (CircleImageView) findViewById(R.id.iv_portrait);
        tvPersonName = (TextView) findViewById(R.id.tv_person_name);
        tvTelephoneNo = (TextView) findViewById(R.id.tv_telephone_number);
        tvPersonPlace = (TextView) findViewById(R.id.tv_person_place);
        tvDept = (TextView) findViewById(R.id.tv_person_dept);
        initViewDatas();
    }

    private void initViewDatas() {
        String realName = SharedPreferencesManager.getString(ResultStatus.REAL_NAME);
        String telephoneNo = SharedPreferencesManager.getString(ResultStatus.PHONE);
        String poritrait = SharedPreferencesManager.getString(ResultStatus.PORTRAIT_IMG);
        tvPersonName.setText(realName);
        tvTelephoneNo.setText(telephoneNo);
        tvDept.setText("");
        ImageLoader.getInstance().displayImage(AppConfig.IMAGE_FONT_URL + poritrait, mIvPortrait, mOption);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        doNext(requestCode, grantResults);
    }

    private void doNext(int requestCode, int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(this, "请在应用管理中打开“相机”访问权限！", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    public void setListener() {
        mIvPortrait.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.iv_portrait:
                //创建头像弹出框
                if (selectPhotoPopupWindow == null) {
                    selectPhotoPopupWindow = new SelectPhotoPopupWindow(this, new PhotoLinstener());
                }
                if (!selectPhotoPopupWindow.isShowing()) {
                    View contentView2 = LayoutInflater.from(this).inflate(
                            R.layout.popupwindow_select_photo, null);
                    selectPhotoPopupWindow.showAtLocation(contentView2, Gravity.BOTTOM, 0, 0);
                }
                break;
        }
    }

    /**
     * 照相弹出框监听器
     */
    private class PhotoLinstener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                //点击 拍照上传
                case R.id.take_photo:
                    takePic();
                    selectPhotoPopupWindow.dismiss();
                    break;
                //点击 选择图片上传
                case R.id.select_photo:
                    Intent albumIntent = new Intent(Intent.ACTION_PICK, null);
                    albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(albumIntent, AppConfig.PICTURE_PICK);
                    selectPhotoPopupWindow.dismiss();
                    break;
                default:
                    break;
            }
        }
    }

    private void takePic() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File dir0 = new File(ConfigPath.demsPath);
            if (!dir0.exists()) {
                dir0.mkdir();
            }
            File file = new File(AppConfig.PICTURE_PATH);
            if (!file.exists()) {
                file.mkdirs();
            }
            PICTURE_NAME = String.valueOf(System.currentTimeMillis());
            File photo = new File(file, PICTURE_NAME + ".jpg");
            if (TDevice.getOSVersion() < 24) {
                Uri outPutFileUri = Uri.fromFile(photo);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutFileUri);
                startActivityForResult(intent, AppConfig.CAMERA);
            } else {
                ContentValues contentValues = new ContentValues(1);
                contentValues.put(MediaStore.Images.Media.DATA, photo.getAbsolutePath());
                Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent, AppConfig.CAMERA);
            }
        } else {
            new AlertDialog.Builder(MyInfoActivity.this)
                    .setTitle("提示")
                    .setMessage("请检查SD卡是否可用")
                    .setIcon(android.R.drawable.stat_sys_warning)
                    .setPositiveButton("",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0,
                                                    int arg1) {
                                    arg0.dismiss();
                                }
                            })
                    .setNegativeButton("",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0,
                                                    int arg1) {
                                    arg0.dismiss();
                                }
                            }).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                //拍照返回结果
                case AppConfig.CAMERA:
                    getCropImg(Uri.fromFile(new File(AppConfig.PICTURE_PATH + PICTURE_NAME + ".jpg")));
                    break;
                case AppConfig.CAMERA_CROP:
                    //得到 剪切后的数据
                    //Bundle extras = data.getExtras();
                    //if (extras != null) {
                    //如果图片不为空，提交；否则，提示用户信息
                    //如果有网络，提交；否则，提示用户信息
                    if (NetStateUtil.isNetworkConnected(this)) {
                        submitToWeb();
                    } else {
                        AppContext.showToast(AppConfig.STRING.NET_NOT_CONNECT);
                    }
                    //}
                    break;
                //从相册中挑选图片
                case AppConfig.PICTURE_PICK:
                    if (selectPhotoPopupWindow != null && selectPhotoPopupWindow.isShowing()) {
                        selectPhotoPopupWindow.dismiss();
                    }
                    Uri uri = data.getData();
                    getCropImg(uri);
                    break;
            }
        }
    }

    public static boolean IMG_STATUS = false;

    private void submitToWeb() {
        //AppContext.getInstance().initWebApiUploadImg();
        File headFile = new File(AppConfig.PICTURE_PATH + "cutUserProait" + ".jpg");
        if (!headFile.exists()) {
            return;
        }

        //retrofit上传头像实现
        IMG_STATUS = true;
        AppContext.getInstance().initRetrofit();
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), headFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", headFile.getName(), requestBody);
        Observable<BaseData<String>> upload = AppContext.getInstance().getApiService().upload(body);

        upload.flatMap(new Function<BaseData<String>, ObservableSource<BaseData<LoginInfo>>>() {
            @Override
            public ObservableSource<BaseData<LoginInfo>> apply(BaseData<String> basedata) throws Exception {
                IMG_STATUS = false;
                AppContext.getInstance().initRetrofit();
                if (StringUtils.equals(basedata.getState(), SysCode.SUCCESS)) {
                    TLog.log("getUploadPortrait : " + basedata.getData());
                    String imgPath = basedata.getData();
                    AppContext.getInstance().reinitWebApi();
                    JSONObject obj = new JSONObject();
                    JSONObject obj0 = new JSONObject();
                    try {
                        obj.put("nimg", imgPath);
                        obj0.put("params", obj);
                        RequestBody body = RequestBody.create(MediaType.parse("application/json"), obj0.toString());
                        return AppContext.getInstance().getApiService().requestChangePortrait(body);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return null;
                } else {
                    AppContext.showToast(basedata.getMessage());
                    return null;
                }
            }
        }).subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseData<LoginInfo>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }
                    @Override
                    public void onNext(BaseData<LoginInfo> basedata) {
                        if (StringUtils.equals(basedata.getState(), ResultStatus.SUCCESS)) {
                            AppContext.showToast(R.string.change_portrait_success);
                            if (!StringUtils.isEmpty(basedata.getData().getImg())) {
                                SharedPreferencesManager.putString(ResultStatus.PORTRAIT_IMG, basedata.getData().getImg());
                            }
                            CommonUtil.getInstance().deleteDir(AppConfig.PICTURE_PATH);
                            //提示侧滑修改头像
                            mBasicBus.post(SysCode.CHANGE_PORTRAIT_SUCCESS);
                            ImageLoader.getInstance().displayImage(AppConfig.IMAGE_FONT_URL + basedata.getData().getImg(), mIvPortrait, mOption);
                            return;
                        } else {
                            AppContext.showToast(basedata.getMessage());
                            return;
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        AppContext.showToast(R.string.change_portrait_failed);
                    }

                    @Override
                    public void onComplete() {

                    }
                });

        //简单封装实现
        /*AppContext.getInstance().doHttpRequest(upload, new Observer<BaseData<String>>() {

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(BaseData<String> stringBaseData) {
                submitPsw(stringBaseData);
            }


            @Override
            public void onError(Throwable e) {
                IMG_STATUS = false;
                AppContext.getInstance().reinitWebApi();
                AppContext.showToast("上传图片失败！");
            }

            @Override
            public void onComplete() {

            }
        });*/

        /*MainApi.uploadFile(headFile, AppConfig.IMG_UPLOAD_URL, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    String httpResult = new String(bytes);
                    TLog.log("getUploadPortrait : " + httpResult);
                    BaseData<String> basedata = new Gson().fromJson(httpResult,
                            new TypeToken<BaseData<String>>() {
                            }.getType());
                    if (StringUtils.equals(basedata.getCode(), SysCode.SUCCESS)) {
                        TLog.log("getUploadPortrait : " + basedata.getData());
                        String imgPath = basedata.getData();
                        //上传成功，修改头像
                        submitToWebPsw(imgPath);
                        AppContext.getInstance().reinitWebApi();
                        return;
                    } else {
                        AppContext.showToast(basedata.getMsg());
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                AppContext.showToast("上传图片失败！");
                AppContext.getInstance().reinitWebApi();
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                AppContext.getInstance().reinitWebApi();
                AppContext.showToast("上传图片失败！");
            }
        });*/
    }

    private void submitPsw(BaseData<String> basedata) {
        IMG_STATUS = false;
        try {
            if (StringUtils.equals(basedata.getState(), SysCode.SUCCESS)) {
                TLog.log("getUploadPortrait : " + basedata.getData());
                String imgPath = basedata.getData();
                //上传成功，修改头像
                submitToWebPsw(imgPath);
                AppContext.getInstance().reinitWebApi();
                return;
            } else {
                AppContext.showToast(basedata.getMessage());
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        AppContext.showToast("上传图片失败！");
        AppContext.getInstance().reinitWebApi();
    }

    private void submitToWebPsw(String imgPth) {
        JSONObject obj = new JSONObject();
        JSONObject obj0 = new JSONObject();
        try {
            obj.put("nimg", imgPth);
            obj0.put("params", obj);
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), obj0.toString());
            Observable<BaseData<LoginInfo>> dataCall = AppContext.getInstance().getApiService().requestChangePortrait(body);
            AppContext.getInstance().doHttpRequest(dataCall, new Observer<BaseData<LoginInfo>>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(BaseData<LoginInfo> basedata) {
                    if (StringUtils.equals(basedata.getState(), ResultStatus.SUCCESS)) {
                        AppContext.showToast(R.string.change_portrait_success);
                        if (!StringUtils.isEmpty(basedata.getData().getImg())) {
                            SharedPreferencesManager.putString(ResultStatus.PORTRAIT_IMG, basedata.getData().getImg());
                        }
                        CommonUtil.getInstance().deleteDir(AppConfig.PICTURE_PATH);
                        //提示侧滑修改头像
                        mBasicBus.post(SysCode.CHANGE_PORTRAIT_SUCCESS);
                        ImageLoader.getInstance().displayImage(AppConfig.IMAGE_FONT_URL + basedata.getData().getImg(), mIvPortrait, mOption);
                        return;
                    } else {
                        AppContext.showToast(basedata.getMessage());
                        return;
                    }
                }

                @Override
                public void onError(Throwable e) {
                    AppContext.showToast(R.string.change_portrait_failed);
                }

                @Override
                public void onComplete() {

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /*JSONObject obj = new JSONObject();
        HttpEntity entity = null;
        try {
            JSONObject obj0 = new JSONObject();
            obj.put("nimg", imgPth);
            obj0.put("params", obj);
            entity = new StringEntity(obj0.toString(), "UTF-8");
            requestChangePortrait(entity);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }*/
    }

    private void requestChangePortrait(HttpEntity entity) {
        MainApi.requestCommon(MyInfoActivity.this, AppConfig.MODDIFY_PWD, entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    String httpResult = new String(bytes);
                    TLog.log("getChangePortrait : " + httpResult);
                    BaseData<LoginInfo> basedata = new Gson().fromJson(httpResult,
                            new TypeToken<BaseData<LoginInfo>>() {
                            }.getType());
                    if (StringUtils.equals(basedata.getState(), ResultStatus.SUCCESS)) {
                        AppContext.showToast(R.string.change_portrait_success);
                        if (!StringUtils.isEmpty(basedata.getData().getImg())) {
                            SharedPreferencesManager.putString(ResultStatus.PORTRAIT_IMG, basedata.getData().getImg());
                        }
                        CommonUtil.getInstance().deleteDir(AppConfig.PICTURE_PATH);
                        //提示侧滑修改头像
                        mBasicBus.post(SysCode.CHANGE_PORTRAIT_SUCCESS);
                        ImageLoader.getInstance().displayImage(AppConfig.IMAGE_FONT_URL + basedata.getData().getImg(), mIvPortrait, mOption);
                        return;
                    } else {
                        AppContext.showToast(basedata.getMessage());
                        return;
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
                AppContext.showToast(R.string.change_portrait_failed);
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                AppContext.showToast(R.string.change_portrait_failed);
            }
        });
    }

    /**
     * 剪切图片
     *
     * @param uri
     */
    private void getCropImg(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        File dir0 = new File(ConfigPath.demsPath);
        if (!dir0.exists()) {
            dir0.mkdir();
        }
        File dir1 = new File(AppConfig.PICTURE_PATH);
        if (!dir1.exists()) {
            dir1.mkdir();
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File(AppConfig.PICTURE_PATH + "cutUserProait" + ".jpg")));
        intent.putExtra("return-data", false);
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 320);
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, AppConfig.CAMERA_CROP);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mBasicBus.unregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
