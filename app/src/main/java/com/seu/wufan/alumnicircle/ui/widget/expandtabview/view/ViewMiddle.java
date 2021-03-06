package com.seu.wufan.alumnicircle.ui.widget.expandtabview.view;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import android.content.Context;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.seu.wufan.alumnicircle.R;
import com.seu.wufan.alumnicircle.ui.widget.dateprovider.DataProvider;
import com.seu.wufan.alumnicircle.ui.widget.expandtabview.adapter.TextAdapter;


public class ViewMiddle extends LinearLayout implements ViewBaseAction {

    private ListView regionListView;
    private ListView plateListView;
    private ArrayList<String> groups = new ArrayList<String>();
    private LinkedList<String> childrenItem = new LinkedList<String>();
    private SparseArray<LinkedList<String>> children = new SparseArray<LinkedList<String>>();
    private TextAdapter plateListViewAdapter;
    private TextAdapter earaListViewAdapter;
    private OnSelectListener mOnSelectListener;
    private int tEaraPosition = 0;
    private int tBlockPosition = 0;
    private String showString = "不限";
    private String showStringLR = "不限";

    private ArrayList<String> industryName = new ArrayList<>();
    private ArrayList<ArrayList<String>> industryCareer = new ArrayList<ArrayList<String>>();

    public ViewMiddle(Context context) {
        super(context);
        init(context);
    }

    public ViewMiddle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void updateShowText(String showArea, String showBlock) {
        if (showArea == null || showBlock == null) {
            return;
        }
        for (int i = 0; i < groups.size(); i++) {
            if (groups.get(i).equals(showArea)) {
                earaListViewAdapter.setSelectedPosition(i);
                childrenItem.clear();
                if (i < children.size()) {
                    childrenItem.addAll(children.get(i));
                }
                tEaraPosition = i;
                break;
            }
        }
        for (int j = 0; j < childrenItem.size(); j++) {
            if (childrenItem.get(j).replace("不限", "").equals(showBlock.trim())) {
                plateListViewAdapter.setSelectedPosition(j);
                tBlockPosition = j;
                break;
            }
        }
        setDefaultSelect();
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_region, this, true);
        regionListView = (ListView) findViewById(R.id.listView);
        plateListView = (ListView) findViewById(R.id.listView2);
//		setBackgroundDrawable(getResources().getDrawable(
//				R.drawable.choosearea_bg_left));

        DataProvider.SeuIndustry seuIndustry = DataProvider.getSeuIndustry();
        industryName = seuIndustry.getName();
        industryCareer = seuIndustry.getCareers();

        for (int i = 0; i < industryName.size(); i++) {
            groups.add(industryName.get(i));
            LinkedList<String> tItem = new LinkedList<String>();
            for (int j = 0; j < industryCareer.get(i).size(); j++) {
                tItem.add(industryCareer.get(i).get(j));
            }
            children.put(i, tItem);
        }

        earaListViewAdapter = new TextAdapter(context, groups,
                R.drawable.choose_item_selected,
                R.drawable.choose_eara_item_selector);
        earaListViewAdapter.setTextSize(17);
        earaListViewAdapter.setSelectedPositionNoNotify(tEaraPosition);
        regionListView.setAdapter(earaListViewAdapter);
        earaListViewAdapter
                .setOnItemClickListener(new TextAdapter.OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {
                        if (position < children.size()) {
                            childrenItem.clear();
                            childrenItem.addAll(children.get(position));
                            plateListViewAdapter.notifyDataSetChanged();
                            showStringLR = groups.get(position);
                        }
                    }
                });
        if (tEaraPosition < children.size())
            childrenItem.addAll(children.get(tEaraPosition));
        plateListViewAdapter = new TextAdapter(context, childrenItem,
                R.drawable.choose_item_right,
                R.drawable.choose_plate_item_selector);
        plateListViewAdapter.setTextSize(15);
        plateListViewAdapter.setSelectedPositionNoNotify(tBlockPosition);
        plateListView.setAdapter(plateListViewAdapter);
        plateListViewAdapter
                .setOnItemClickListener(new TextAdapter.OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, final int position) {
                        showString = childrenItem.get(position);
                        String str = showStringLR;
                        str += " " + showString;
                        showString = str;
                        if (mOnSelectListener != null) {

                            mOnSelectListener.getValue(showString);
                        }

                    }
                });
        if (tBlockPosition < childrenItem.size())
            showString = childrenItem.get(tBlockPosition);
        if (showString.contains("不限")) {
            showString = showString.replace("不限", "");
        }
        setDefaultSelect();
    }

    public void setDefaultSelect() {
        regionListView.setSelection(tEaraPosition);
        plateListView.setSelection(tBlockPosition);
        showStringLR = groups.get(tEaraPosition);
    }

    public String getShowText() {
        return showString;
    }

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        mOnSelectListener = onSelectListener;
    }

    public interface OnSelectListener {
        public void getValue(String showText);
    }

    @Override
    public void hide() {

    }

    @Override
    public void show() {

    }
}
