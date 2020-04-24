package com.taikesoft.fly.business.receiver;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.baidu.android.pushservice.PushMessageReceiver;
import com.taikesoft.fly.business.common.utils.StringUtils;
import com.taikesoft.fly.business.common.utils.TLog;
import com.taikesoft.fly.business.config.AppConfig;
import com.taikesoft.fly.business.constant.ResultStatus;
import com.taikesoft.fly.business.storage.SharedPreferencesManager;
import com.taikesoft.fly.ui.login.LoginActivity;
import com.taikesoft.fly.ui.myinfo.WarnmsgDetailActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MyReceiver extends PushMessageReceiver {
    /**
     * TAG to Log
     */
    public static final String TAG = MyReceiver.class
            .getSimpleName();


    /**
     * 调用PushManager.startWork后，sdk将对push
     * server发起绑定请求，这个过程是异步的。绑定请求的结果通过onBind返回。 如果您需要用单播推送，需要把这里获取的channel
     * id和user id上传到应用server中，再调用server接口用channel id和user id给单个手机或者用户推送。
     *
     * @param context   BroadcastReceiver的执行Context
     * @param errorCode 绑定接口返回值，0 - 成功
     * @param appid     应用id。errorCode非0时为null
     * @param userId    应用user id。errorCode非0时为null
     * @param channelId 应用channel id。errorCode非0时为null
     * @param requestId 向服务端发起的请求id。在追查问题时有用；
     * @return none
     */
    @Override
    public void onBind(Context context, int errorCode, String appid,
                       String userId, String channelId, String requestId) {
        String responseString = "onBind errorCode=" + errorCode + " appid="
                + appid + " USER_ID=" + userId + " CHANNEL_ID=" + channelId
                + " requestId=" + requestId;
        TLog.log(TAG, responseString);
        if (errorCode == 0) {
            // 绑定成功
            TLog.log(TAG, "绑定成功");
            SharedPreferencesManager.putString(ResultStatus.CHANNEL_ID, channelId);
        }
    }

    @Override
    public void onUnbind(Context context, int i, String s) {

    }

    @Override
    public void onSetTags(Context context, int i, List<String> list, List<String> list1, String s) {

    }

    @Override
    public void onDelTags(Context context, int i, List<String> list, List<String> list1, String s) {

    }

    @Override
    public void onListTags(Context context, int i, List<String> list, String s) {

    }

    /**
     * 接收透传消息的函数。
     *
     * @param context             上下文
     * @param message             推送的消息
     * @param customContentString 自定义内容,为空或者json字符串
     */
    @Override
    public void onMessage(Context context, String message, String customContentString) {

    }

    /**
     * 接收通知点击的函数。
     *
     * @param context             上下文
     * @param title               推送的通知的标题
     * @param description         推送的通知的描述
     * @param customContentString 自定义内容，为空或者json字符串
     */
    @Override
    public void onNotificationClicked(Context context, String title, String description, String customContentString) {
        String notifyString = "通知点击 onNotificationClicked title=\"" + title + "\" description=\""
                + description + "\" customContent=" + customContentString;
        TLog.log(TAG, notifyString);
        if (StringUtils.equals(SharedPreferencesManager.getString(ResultStatus.LOGIN_STATUS), ResultStatus.UNLOGIN)) {
            //跳到登录
            Intent intent = new Intent();
            intent.setClass(context, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            if (!TextUtils.isEmpty(customContentString)) {
                JSONObject customJson = null;
                try {
                    customJson = new JSONObject(customContentString);
                    if (!customJson.isNull("sendTime")) {
                        Intent intent = new Intent();
                        intent.putExtra("type", title);
                        intent.putExtra("content", description);
                        intent.putExtra("time", customJson.get("sendTime").toString());
                        intent.setClass(context, WarnmsgDetailActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 接收通知到达的函数。
     *
     * @param context             上下文
     * @param title               推送的通知的标题
     * @param description         推送的通知的描述
     * @param customContentString 自定义内容，为空或者json字符串
     */
    @Override
    public void onNotificationArrived(Context context, String title, String description, String customContentString) {
        String notifyString = "通知到达 onNotificationArrived  title=\"" + title
                + "\" description=\"" + description + "\" customContent="
                + customContentString;
        TLog.log(TAG, notifyString);
    }
}
