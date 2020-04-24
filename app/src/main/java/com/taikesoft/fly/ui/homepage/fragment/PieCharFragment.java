package com.taikesoft.fly.ui.homepage.fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.taikesoft.fly.R;
import com.taikesoft.fly.business.common.utils.TLog;
import com.taikesoft.fly.business.context.AppContext;
import com.taikesoft.fly.ui.myinfo.bean.NurseItemBean;

import java.io.Serializable;
import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 护理类别的饼图，暂用不到
 */
public class PieCharFragment extends Fragment implements OnChartValueSelectedListener{
    public static final String TAG = "PieCharFragment";
    protected Typeface tfRegular;
    protected Typeface tfLight;
    @BindView(R.id.chart)
    PieChart chart;
    private Typeface tf;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pie_chart, container, false);
        ButterKnife.bind(this, view);
        if (getArguments() != null) {
            ArrayList<NurseItemBean> list = (ArrayList<NurseItemBean>) getArguments().getSerializable("items");
            tfRegular = Typeface.createFromAsset(AppContext.mContext.getAssets(), "OpenSans-Regular.ttf");
            tfLight = Typeface.createFromAsset(AppContext.mContext.getAssets(), "OpenSans-Light.ttf");
            //不显示百分比
            chart.setUsePercentValues(false);
            chart.getDescription().setEnabled(false);
            chart.setExtraOffsets(5, 10, 5, 5);

            chart.setDragDecelerationFrictionCoef(0.95f);

            tf = Typeface.createFromAsset(AppContext.mContext.getAssets(), "OpenSans-Regular.ttf");

            chart.setCenterTextTypeface(Typeface.createFromAsset(AppContext.mContext.getAssets(), "OpenSans-Light.ttf"));
            chart.setCenterText(generateCenterSpannableText());

            chart.setExtraOffsets(20.f, 0.f, 20.f, 0.f);

            chart.setDrawHoleEnabled(true);
            chart.setHoleColor(Color.WHITE);

            chart.setTransparentCircleColor(Color.WHITE);
            chart.setTransparentCircleAlpha(110);

            chart.setHoleRadius(58f);
            chart.setTransparentCircleRadius(61f);

            chart.setDrawCenterText(true);

            chart.setRotationAngle(0);
            // enable rotation of the chart by touch
            chart.setRotationEnabled(true);
            chart.setHighlightPerTapEnabled(true);
            chart.setRotationEnabled(false);//是否可以旋转
            // chart.setUnit(" €");
            // chart.setDrawUnitsInChart(true);

            // add a selection listener
            chart.setOnChartValueSelectedListener(this);

            chart.animateY(1400, Easing.EaseInOutQuad);
            // chart.spin(2000, 0, 360);

            Legend l = chart.getLegend();
            l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
            l.setOrientation(Legend.LegendOrientation.VERTICAL);
            l.setDrawInside(false);
            l.setEnabled(false);
            setData(list);
        }

        return view;
    }

    private void setData(ArrayList<NurseItemBean> list) {

        ArrayList<PieEntry> entries = new ArrayList<>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        if(list != null){
            for (NurseItemBean itemBean : list) {
                entries.add(new PieEntry(itemBean.getCount(), itemBean.getNurseItem()));
            }
        }
        PieDataSet dataSet = new PieDataSet(entries, "Election Results");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int n = (int)value;
                return n+"";
            }
        });
        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        //数据连接线距图形片内部边界的距离
        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setValueLinePart1Length(0.2f);
        dataSet.setValueLinePart2Length(0.4f);
        //dataSet.setUsingSliceColorAsValueLineColor(true);

        //dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);
        data.setValueTypeface(tf);
        chart.setData(data);

        // undo all highlights
        chart.highlightValues(null);

        chart.invalidate();
    }

    private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString("各项护理\n完成儿童数");
        s.setSpan(new RelativeSizeSpan(1.5f), 0, 4, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 4, s.length() - 3, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 4, s.length() - 3, 0);
        s.setSpan(new RelativeSizeSpan(.65f), 4, s.length() - 3, 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 4, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 3, s.length(), 0);
        return s;
    }


    public PieCharFragment() {
    }

    public static PieCharFragment newInstance(ArrayList<NurseItemBean> list) {
        PieCharFragment fragment = new PieCharFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("items", (Serializable) list);
        //fragment保存参数，传入一个Bundle对象
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

        if (e == null)
            return;
        TLog.log("VAL SELECTED",
                "Value: " + e.getY() + ", xIndex: " + e.getX()
                        + ", DataSet index: " + h.getDataSetIndex());
    }

    @Override
    public void onNothingSelected() {

    }
}