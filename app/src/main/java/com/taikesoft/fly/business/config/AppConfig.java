package com.taikesoft.fly.business.config;

import android.content.Context;
import android.os.Environment;

public class AppConfig {
    public static final int BANNER_TIME_SPAN = 200;
    public static final String CONTENT_TYPE = "application/json";
    public static final String HOST = "192.168.0.252";
    public static final String API_PORT = "9090";
    public static final String BASE_SERVER_URL = "http://" + HOST + ":" + API_PORT + "/";
    public static final String WEBSERVICE_URL = "http://192.168.0.252:9090/api/api/";
    public static final String IMAGE_FONT_URL = "http://192.168.0.252:9090/web";
    public static final String RICH_IMAGE__URL = "http://192.168.0.252:9090/";
    // apiKey
    public static final String API_KEY = "xCP6Rm3npGkp0XtpYIAr3TtP";
    //获取指定儿童信息
    public static final String CHILD_INFO = "childInfo";
    //获取儿童信息
    public static final String CHILDREN = "children";
    //获取药品信息
    public static final String LIST_MEDICINE = "listMedicine";
    //查询记录时：获取护理项目
    public static final String LIST_NURSE_ITEM = "listNurseItem";
    //护理主页查询生成的护理项
    public static final String PAGE_NURSE_ITEM = "pageNurseItem";
    //获取字典
    public static final String LIST_TYPES = "listTypes";
    //根据指定儿童和当日日期获取任务项目
    public static final String LIST_NURSE_TASK = "listNurseTask";
    //获取所有更换类别
    public static final String LIST_CLOTHES_TYPES = "listClothesTypes";
    //获取所有水果点心类别
    public static final String LIST_REFRESHMENTS_TYPES = "listRefreshmentsTypes";
    //获取所有夜间状态
    public static final String LIST_PATROL_TYPES = "listPatrolTypes";
    //分页获取护理记录
    public static final String PAGE_NURSE_RECORD = "pageNurseRecord";
    // 首页获取任务数量
    public static final String LIST_SITUAITON = "listSituation";
    //根据护理项查询任务儿童
    public static final String TASK_CHILDREN = "taskChildren";
    //保存
    public static final String INSERT_NURSE_NOTE = "insertNurseNote";
    //保存巡夜-从主页进去
    public static final String INSERT_PATROL = "insertPatrol";
    //修改
    public static final String UPDATE_NURSE_NOTE = "updateNurseNote";
    //删除
    public static final String DEL_NURSE_NOTE = "delNurseNote";
    //修改密码
    public static final String MODDIFY_PWD = "moddifyPwd";
    //查询通知公告
    public static final String PAGE_NOTICE = "pageNotice";
    //分页获取消息列表
    public static final String PAGE_MESSAGE = "pageMessage";
    //置消息已读
    public static final String WARN_MSG_READ = "toIsRead";
    //儿童状况近一周统计
    public static final String LIST_CHILDREN_SITUATION = "listChildrenSituation";

    public static final int BITMAP_COMPRESS_PERCENT = 60;
    public static int pageSize = 6;
    public static final int PHOTO_COUNT = 100;
    public static final String PICTURE_PATH = Environment
            .getExternalStorageDirectory() + "/FLY/pictures/";

    public static final String LOCAL_PATH = "https://appstore.crc.com.cn/MDM_SERVICE/appInfoDetail/getAppInfo?appPackageName=cn.com.crcement.cctgic&devicefamily=a";//cctgps
    public static final String APK_NAME = "cn_com_crcement_ccterp.apk";//cn_com_crcement_ccterp
    public static final String LOCAL_PATH1 = Environment
            .getExternalStorageDirectory().getPath()
            + "/cn/com/crcement/ccterp/update/";
    public static final int MAX_PAGE_SIZE = 10000;
    public static final int DEFAULT_CURRENT_PAGE = 1;
    public static boolean MANAGER = false;
    public static int OFFLINE_DOWNLOAD_TITLE = 13;

    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE = "FLY";
    /**
     * 根目录
     */
    public static final String ROOT_FILE = "fly_tk";
    /**
     * 临时文件文件夹
     */
    public static final String FILE_TEMP = "temp";

    public static final String FILE_IMG = "img";

    //照相机
    public static final int CAMERA = 0x3005;
    public static final int PICTURE_PICK = 0x3006;
    public static final int CAMERA_CROP = 0x3007;

    /**
     * 字符串
     */
    public interface STRING {
        String HEAD_IMAGE_FAIL = "头像获取失败";
        String NET_NOT_CONNECT = "网络未连接，请先连接网络！";
    }

    /**
     * 字符串
     */
    public enum Mode {
        Single, Multiple
    }

    public static int getPageSize(Context context) {
        /*int height = TDevice.getHeightPixels(context);
        return (int)(height/(50*TDevice.getDensity(context)));*/
        return 15;
    }

    public static void init(Context context) {
        pageSize = getPageSize(context);
    }
}
