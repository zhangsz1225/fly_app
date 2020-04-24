package com.taikesoft.fly.business.retrofit;

import com.taikesoft.fly.business.base.basebean.BaseData;
import com.taikesoft.fly.business.entity.ChildInfoEntity;
import com.taikesoft.fly.ui.login.bean.LoginInfo;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 */

public interface Engine {
    @GET("book/search")
    Call<Book> getSearchBook(@Query("q") String name,
                             @Query("tag") String tag,
                             @Query("start") int start,
                             @Query("count") int count);

    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("login")
    Call<ResponseBody> getMessage(@Body RequestBody info);   // 请求体味RequestBody 类型
    //上传图片
    @Multipart
    @POST("client/img/upload")
    Observable<BaseData<String>> upload(@Part MultipartBody.Part file);

    @POST("client/user/mod")
    Observable<BaseData<LoginInfo>> requestChangePortrait(@Body RequestBody info);

}
