package com.taikesoft.fly.business.widget.lineChart;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.taikesoft.fly.R;
import com.taikesoft.fly.ui.homepage.entity.LineEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * created by yezhengyu on 2017/9/25 17:04
 */

public class LineChartLayout extends LinearLayout {

    private Context mContext;
    private List<LineEntity> mDatas;
    private LineChartView mLineChartView;
    private RelativeLayout relativeLayout;
    private View chartPop;
    private int maxValue;

    public LineChartLayout(Context context) {
        this(context, null);
    }

    public LineChartLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineChartLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public void refreshLineView(List<LineEntity> datas) {
        mDatas = datas;
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_line_chart, null);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
                dp2px(215));
        setOrientation(VERTICAL);
        addView(view, lp);
        relativeLayout = (RelativeLayout) view.findViewById(R.id.rl_chart_contain);
        mLineChartView = (LineChartView) view.findViewById(R.id.my_line_chart);
        relativeLayout.removeView(chartPop);
        initMaxDatas();
        initLineChartView();
        postInvalidate();
    }

    private void initMaxDatas() {
        //初始化datas
        for (int i = 0; i < mDatas.size(); i++) {
            String time = mDatas.get(i).getTime();
            String subTime = time.substring(time.length() - 5, time.length());
            if (subTime != null) {
                mDatas.get(i).setTime(subTime);
            }
        }
        List<Integer> list = new ArrayList<>();

        for (int i = 0; i < mDatas.size(); i++) {
            list.add(mDatas.get(i).getInHospital());
            list.add(mDatas.get(i).getIll());
        }
        Collections.sort(list);
        maxValue = list.get(list.size() - 1).intValue();
        if (maxValue <= 0) {
            maxValue = 5;
        }
    }

    private void initLineChartView() {
        mLineChartView.setDatas(mDatas, maxValue);
        mLineChartView.setListener(new LineChartView.getNumberListener() {
            @Override
            public void getNumber(int number, int x, int y) {
                relativeLayout.removeView(chartPop);
                chartPop = LayoutInflater.from(mContext).inflate(R.layout.view_line_chart_pop, null);
                //生病
                TextView tvIll = (TextView) chartPop.findViewById(R.id.tv_ill);
                //住院
                TextView tvInHospital = (TextView) chartPop.findViewById(R.id.tv_in_hospital);
                int selectHeights = 0;
                if (mDatas != null) {
                    for (int i = 0; i < mDatas.size(); i++) {
                        int ill = mDatas.get(number).getIll();
                        int hospital = mDatas.get(number).getInHospital();
                        tvIll.setText(mContext.getResources().getString(R.string.number_ill, ill + ""));
                        tvInHospital.setText(mContext.getResources().getString(R.string.number_in_hospital, hospital + ""));
                        if (ill >= hospital) {
                            selectHeights = ill;
                        } else {
                            selectHeights = hospital;
                        }
                    }
                }
                chartPop.measure(0, 0);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.leftMargin = x - chartPop.getMeasuredWidth() / 2;
                float division = (float) (maxValue - selectHeights) / maxValue;
                layoutParams.topMargin = (int) ((division * 6 + 2) * y / 9 - dp2px(8) - chartPop.getMeasuredHeight());
                chartPop.setLayoutParams(layoutParams);
                relativeLayout.addView(chartPop);
            }
        });
    }


    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}
