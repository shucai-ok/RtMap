package com.yzy.map3d.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yzy.map3d.R;
import com.yzy.map3d.util.StringUtils;

/**
 * @author 志尧
 * @date on 2018-12-26 14:51
 * @email 1417337180@qq.com
 * @describe 搜索栏
 * @ideas
 */
public class SearchTitleView extends LinearLayout {

    private ISearchTitleClick iSearchTitleClick;

    public void setiSearchTitleClick(ISearchTitleClick iSearchTitleClick) {
        this.iSearchTitleClick = iSearchTitleClick;
    }

    private LinearLayout llSearchBack;
    private EditText editSearch;
    private LinearLayout llSearchClear;
    private TextView tvSearchRefresh;
    private boolean mFocusable;

    public SearchTitleView(Context context) {
        this(context, null);
    }

    public SearchTitleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchTitleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_map_search, this);

        findViewById();
        initClick();
    }

    void findViewById() {
        llSearchBack = findViewById(R.id.llSearchBack);
        editSearch = findViewById(R.id.editSearch);
        llSearchClear = findViewById(R.id.llSearchClear);
        tvSearchRefresh = findViewById(R.id.tvSearchRefresh);
    }

    void initClick() {
        SearchOnClickListener clickListener = new SearchOnClickListener();
        llSearchBack.setOnClickListener(clickListener);
        editSearch.setOnClickListener(clickListener);
        llSearchClear.setOnClickListener(clickListener);
        tvSearchRefresh.setOnClickListener(clickListener);


        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (mFocusable) {
                    if (StringUtils.isEmpty(editable.toString())) {
                        llSearchClear.setVisibility(GONE);
                    } else {
                        llSearchClear.setVisibility(VISIBLE);
                    }

                    if (iSearchTitleClick != null) {
                        iSearchTitleClick.headSearch(editable.toString());
                    }

                    return;
                }

                if (!mFocusable) {
                    if (StringUtils.isEmpty(editable.toString())) {
                        return;
                    }

                    if (iSearchTitleClick != null) {
                        iSearchTitleClick.headSearch(editable.toString());
                    }

                    editSearch.setText("");
                }
            }
        });
    }

    public void setEditFocusable(boolean focusable) {
        this.mFocusable = focusable;

        editSearch.setFocusableInTouchMode(focusable);
        editSearch.setFocusable(focusable);

        if (focusable) {
            editSearch.setClickable(false);
            editSearch.setOnClickListener(null);
            editSearch.requestFocus();

            tvSearchRefresh.setVisibility(VISIBLE);
        } else {
            editSearch.setClickable(true);
            llSearchClear.setVisibility(GONE);
            tvSearchRefresh.setVisibility(GONE);
        }
    }

    public void setEditKey(String key) {
        if (StringUtils.isEmpty(key)) {
            return;
        }

        editSearch.setText(key);
        editSearch.setSelection(key.length());
    }

    class SearchOnClickListener implements OnClickListener {

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.llSearchClear) {
                editSearch.setText("");
                return;
            }

            if (iSearchTitleClick == null) {
                return;
            }

            if (view.getId() == R.id.llSearchBack) {
                iSearchTitleClick.headBack();
                return;
            }

            if (view.getId() == R.id.editSearch) {
                iSearchTitleClick.headSearch("");
                return;
            }

            if (view.getId() == R.id.tvSearchRefresh) {
                iSearchTitleClick.headSearch(editSearch.getText().toString());
                return;
            }
        }
    }

    public interface ISearchTitleClick {
        /**
         * 返回
         */
        void headBack();

        /**
         * 搜索
         *
         * @param key
         */
        void headSearch(String key);
    }
}
