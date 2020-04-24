package com.taikesoft.fly.business.common.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.taikesoft.fly.business.config.ConfigPath;
import com.taikesoft.fly.business.constant.ResultStatus;
import com.taikesoft.fly.business.entity.ChildInfoEntity;
import com.taikesoft.fly.business.storage.SharedPreferencesManager;
import com.taikesoft.fly.ui.homepage.entity.LineEntity;
import com.taikesoft.fly.ui.myinfo.bean.NurseItemBean;
import com.taikesoft.fly.ui.myinfo.bean.RecordBean;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *
 */

public class CommonUtil {

    private static CommonUtil instance = null;

    public Calendar mStartDate;
    public Calendar mEndDate;

    public synchronized static CommonUtil getInstance() {
        if (instance == null) {
            instance = new CommonUtil();
        }
        return instance;
    }

    public CommonUtil() {
        mStartDate = Calendar.getInstance();
        mStartDate.set(2019, 10, 1);
        //系统当前时间
        mEndDate = Calendar.getInstance();
    }

    public String initStartTime() {
        Calendar startDate = Calendar.getInstance();
        startDate.setTime(new Date());
        startDate.add(Calendar.MONTH, -1);
        return getDate(startDate);
    }

    public String initTime() {
        return getDate(mEndDate);
    }

    public String getDate(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(calendar.getTime());
        return dateStr;
    }

    public Date initBeforeDateSix() {
        Calendar startDate = Calendar.getInstance();
        startDate.setTime(new Date());
        startDate.add(Calendar.DATE, -6);
        Date date = startDate.getTime();
        return date;
    }

    public String initBeforeSix() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar startDate = Calendar.getInstance();
        startDate.setTime(new Date());
        startDate.add(Calendar.DATE, -6);
        Date d = startDate.getTime();
        String day = sdf.format(d);
        return day;
    }

    public List<String> initAreaTime() {
        List<String> dates = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar startDate = Calendar.getInstance();
        for (int i = 6; i >= 0; i--) {
            startDate.setTime(new Date());
            startDate.add(Calendar.DATE, -i);
            Date d = startDate.getTime();
            String day = sdf.format(d);
            dates.add(day);
        }
        return dates;
    }

    public List<LineEntity> initObjectTime() {
        List<LineEntity> situation = new ArrayList<>();
        List<String> initAreaTimes = initAreaTime();
        LineEntity listEntity;
        for (int i = 0; i < initAreaTimes.size(); i++) {
            listEntity = new LineEntity();
            listEntity.setTime(initAreaTimes.get(i));
            situation.add(listEntity);
        }
        return situation;
    }

    public Calendar initCustomeTimer(String time) {
        Calendar selectedDate;
        if (!StringUtils.isEmpty(time)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;
            try {
                date = sdf.parse(time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            selectedDate = Calendar.getInstance();
            selectedDate.setTime(date);
        } else {
            selectedDate = Calendar.getInstance();
        }
        return selectedDate;
    }

    public List<LineEntity> initStartEndObject(Date d, int days) {
        List<LineEntity> situation = new ArrayList<>();
        List<String> initAreaTimes = initStartEndTime(d, days);
        LineEntity listEntity;
        for (int i = 0; i < initAreaTimes.size(); i++) {
            listEntity = new LineEntity();
            listEntity.setTime(initAreaTimes.get(i));
            situation.add(listEntity);
        }
        return situation;
    }

    public List<String> initStartEndTime(Date d, int days) {
        List<String> dates = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar now = Calendar.getInstance();
        for (int i = days; i >= 0; i--) {
            now.setTime(d);
            now.set(Calendar.DATE, now.get(Calendar.DATE) - i);
            Date date = now.getTime();
            String day = sdf.format(date);
            dates.add(day);
        }
        return dates;
    }

    /**
     * date2比date1多的天数
     */
    public static int differentDays(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        int day1 = cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if (year1 != year2)   //同一年
        {
            int timeDistance = 0;
            for (int i = year1; i < year2; i++) {
                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0)    //闰年
                {
                    timeDistance += 366;
                } else    //不是闰年
                {
                    timeDistance += 365;
                }
            }

            return timeDistance + (day2 - day1);
        } else    //不同年
        {
            return day2 - day1;
        }
    }

    public int getIntMode(int maxvalue) {
        int mode;
        if (maxvalue > 0 && maxvalue <= 10) {
            mode = 1;
        } else if (maxvalue > 10 && maxvalue <= 20) {
            mode = 2;
        } else if (maxvalue > 20 && maxvalue <= 30) {
            mode = 3;
        } else if (maxvalue > 30 && maxvalue <= 40) {
            mode = 4;
        } else if (maxvalue > 40 && maxvalue <= 50) {
            mode = 5;
        } else if (maxvalue > 50 && maxvalue <= 60) {
            mode = 6;
        } else if (maxvalue > 60 && maxvalue <= 70) {
            mode = 7;
        } else if (maxvalue > 70 && maxvalue <= 80) {
            mode = 8;
        } else if (maxvalue > 80 && maxvalue <= 90) {
            mode = 9;
        } else {
            mode = 20;
        }
        return mode;
    }

    public static boolean startAfter(Date date1, Date date2) {
        SimpleDateFormat f = new SimpleDateFormat("hhmmss"); //格式化为 hhmmss
        int d1Number = Integer.parseInt(f.format(date1).toString()); //将第一个时间格式化后转为int
        int d2Number = Integer.parseInt(f.format(date2).toString()); //将第二个时间格式化后转为int
        return d1Number > d2Number;
    }

    /**
     * 读取图片的旋转的角度
     *
     * @param path 图片绝对路径
     * @return 图片的旋转角度
     */
    public static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 将图片按照某个角度进行旋转
     *
     * @param bm     需要旋转的图片
     * @param degree 旋转角度
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;

        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }

    /**
     * 将Bitmap转换成文件
     * 保存文件
     *
     * @param bm
     * @param fileName
     * @throws IOException
     */
    public static File saveFile(Bitmap bm, String path, String fileName) throws IOException {
        File dir0 = new File(ConfigPath.demsPath);
        if (!dir0.exists()) {
            dir0.mkdir();
        }
        File dirFile = new File(path);
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }
        File myCaptureFile = new File(path, fileName);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        bos.flush();
        bos.close();
        Log.i("SaveBitmap", "已经保存");
        return myCaptureFile;
    }

    public static String getPicNameFromPath(String picturePath) {
        String temp[] = picturePath.replaceAll("\\\\", "/").split("/");
        String fileName = "";
        if (temp.length > 1) {
            fileName = temp[temp.length - 1];
        }
        return fileName;
    }

    //删除文件夹和文件夹里面的文件
    public void deleteDir(String deletePath) {
        File dir = new File(deletePath);
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;

        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                file.delete(); // 删除所有文件
            }
        }
        dir.delete();// 删除目录本身
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 通过位置找到ListView中的某个item的View
     *
     * @param pos
     * @param listView
     * @return
     */
    public View getViewByPosition(int pos, ListView listView) {
        int firstListItemPosition = listView.getFirstVisiblePosition();
        int lastListItemPosition = firstListItemPosition
                + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    public HttpEntity fillPatrolHttpEntity(ChildInfoEntity child, String nurseTime, String nurseContent, String remark) {
        HttpEntity entity = null;
        JSONObject obj = new JSONObject();
        try {
            JSONObject obj0 = new JSONObject();
            obj.put("child", JSON.toJSON(child));
            //note主表
            obj0.put("nurses", SharedPreferencesManager.getString(ResultStatus.REAL_NAME));
            obj0.put("createId", SharedPreferencesManager.getString(ResultStatus.USER_NAME));
            obj0.put("createName", SharedPreferencesManager.getString(ResultStatus.REAL_NAME));
            obj0.put("isRepair", "否");
            obj0.put("isValid", "1");
            obj.put("note", obj0);
            //note明细
            JSONObject obj1 = new JSONObject();
            obj1.put("createId", SharedPreferencesManager.getString(ResultStatus.USER_NAME));
            obj1.put("createName", SharedPreferencesManager.getString(ResultStatus.REAL_NAME));
            obj1.put("nurseTime", nurseTime);
            obj1.put("mark", remark);
            obj1.put("nurseContent", nurseContent);
            obj1.put("isRepair", "否");
            obj.put("noteDetail", obj1);
            //note emp
            JSONObject obj2 = new JSONObject();
            obj2.put("empId", SharedPreferencesManager.getString(ResultStatus.EMPLOYEE_ID));
            obj2.put("empName", SharedPreferencesManager.getString(ResultStatus.REAL_NAME));
            obj.put("noteEmp", obj2);
            String s = obj.toString();
            entity = new StringEntity(s, "UTF-8");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return entity;
    }

    public HttpEntity noNeedfillHttpEntity(List<ChildInfoEntity> mChildren, NurseItemBean itemBean, String spirit) {
        HttpEntity entity = null;
        JSONObject obj = new JSONObject();
        try {
            JSONObject obj0 = new JSONObject();
            obj.put("children", JSON.toJSON(mChildren));
            //note主表
            obj0.put("nurses", SharedPreferencesManager.getString(ResultStatus.REAL_NAME));
            obj0.put("createId", SharedPreferencesManager.getString(ResultStatus.USER_NAME));
            obj0.put("createName", SharedPreferencesManager.getString(ResultStatus.REAL_NAME));
            obj0.put("isRepair", "否");
            obj0.put("isValid", "1");
            obj.put("note", obj0);
            //note明细
            JSONObject obj1 = new JSONObject();
            obj1.put("nurseItemId", itemBean.getId());
            obj1.put("nurseItem", itemBean.getNurseItem());
            //儿童出发;yyyy-MM-dd HH:mm:ss格式
            //护理出发,格式yyyy-MM-dd HH:mm:ss；
            obj1.put("nurseBeginTime", itemBean.getNurseBeginTime());
            obj1.put("nurseEndTime", itemBean.getNurseEndTime());
            obj1.put("timeType", itemBean.getTimeType());
            obj1.put("nurseType", itemBean.getNurseType());
            obj1.put("createId", SharedPreferencesManager.getString(ResultStatus.USER_NAME));
            obj1.put("createName", SharedPreferencesManager.getString(ResultStatus.REAL_NAME));
            obj1.put("mark", null);
            obj1.put("nurseContent", itemBean.getNurseItem());
            obj1.put("nurseValue", null);
            obj1.put("nurseValueUnit", null);
            obj1.put("nurseTime", DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
            obj1.put("spirit", spirit);
            obj1.put("isRepair", "否");
            obj1.put("source", "app");
            obj1.put("isValid", "1");
            obj.put("noteDetail", obj1);
            //note emp
            JSONObject obj2 = new JSONObject();
            obj2.put("empId", SharedPreferencesManager.getString(ResultStatus.EMPLOYEE_ID));
            obj2.put("empName", SharedPreferencesManager.getString(ResultStatus.REAL_NAME));
            obj.put("noteEmp", obj2);
            String s = obj.toString();
            entity = new StringEntity(s, "UTF-8");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return entity;
    }

    public HttpEntity fillHttpEntity(List<ChildInfoEntity> mChildren, NurseItemBean itemBean, String nurseTime, String nurseContent, BigDecimal nurseValue, String nurseValueUnit, String remark, String spirit) {
        HttpEntity entity = null;
        JSONObject obj = new JSONObject();
        try {
            JSONObject obj0 = new JSONObject();
            obj.put("children", JSON.toJSON(mChildren));
            //note主表
            obj0.put("nurses", SharedPreferencesManager.getString(ResultStatus.REAL_NAME));
            obj0.put("createId", SharedPreferencesManager.getString(ResultStatus.USER_NAME));
            obj0.put("createName", SharedPreferencesManager.getString(ResultStatus.REAL_NAME));
            obj0.put("isRepair", "否");
            obj0.put("isValid", "1");
            obj.put("note", obj0);
            //note明细
            JSONObject obj1 = new JSONObject();
            obj1.put("nurseItemId", itemBean.getId());
            obj1.put("nurseItem", itemBean.getNurseItem());
            //儿童出发;yyyy-MM-dd HH:mm:ss格式
            //护理出发,格式yyyy-MM-dd HH:mm:ss；
            obj1.put("nurseBeginTime", itemBean.getNurseBeginTime());
            obj1.put("nurseEndTime", itemBean.getNurseEndTime());
            obj1.put("timeType", itemBean.getTimeType());
            obj1.put("nurseType", itemBean.getNurseType());
            obj1.put("createId", SharedPreferencesManager.getString(ResultStatus.USER_NAME));
            obj1.put("createName", SharedPreferencesManager.getString(ResultStatus.REAL_NAME));
            obj1.put("mark", remark);
            obj1.put("nurseContent", nurseContent);
            obj1.put("nurseValue", nurseValue);
            obj1.put("nurseValueUnit", nurseValueUnit);
            obj1.put("nurseTime", nurseTime);
            obj1.put("spirit", spirit);
            obj1.put("isRepair", "否");
            obj1.put("source", "app");
            obj1.put("isValid", "1");
            obj.put("noteDetail", obj1);
            //note emp
            JSONObject obj2 = new JSONObject();
            obj2.put("empId", SharedPreferencesManager.getString(ResultStatus.EMPLOYEE_ID));
            obj2.put("empName", SharedPreferencesManager.getString(ResultStatus.REAL_NAME));
            obj.put("noteEmp", obj2);
            String s = obj.toString();
            entity = new StringEntity(s, "UTF-8");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return entity;
    }

    public HttpEntity fillDelHttpEntity(String noteDetaiId) {
        HttpEntity entity = null;
        JSONObject obj = new JSONObject();
        try {
            //note明细
            JSONObject obj1 = new JSONObject();
            obj1.put("id", noteDetaiId);
            obj1.put("updateId", SharedPreferencesManager.getString(ResultStatus.USER_NAME));
            obj1.put("updateName", SharedPreferencesManager.getString(ResultStatus.REAL_NAME));
            obj.put("noteDetail", obj1);
            String s = obj.toString();
            entity = new StringEntity(s, "UTF-8");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return entity;
    }

    public HttpEntity fillModifyHttpEntity(String noteDetailId, String nurseTime, String nurseContent, BigDecimal nurseValue, String mark,String spirit) {
        HttpEntity entity = null;
        JSONObject obj = new JSONObject();
        try {
            //note明细
            JSONObject obj1 = new JSONObject();
            obj1.put("id", noteDetailId);
            obj1.put("nurseTime", nurseTime);
            obj1.put("updateId", SharedPreferencesManager.getString(ResultStatus.USER_NAME));
            obj1.put("updateName", SharedPreferencesManager.getString(ResultStatus.REAL_NAME));
            obj1.put("nurseContent", nurseContent);
            obj1.put("nurseValue", nurseValue);
            obj1.put("spirit", spirit);
            obj1.put("mark", mark);
            obj.put("noteDetail", obj1);
            String s = obj.toString();
            entity = new StringEntity(s, "UTF-8");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return entity;
    }
}
