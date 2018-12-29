package com.yzy.map3d.ui.search;


import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rtm.common.model.POI;
import com.rtm.common.model.POIExt;
import com.yzy.map3d.R;

import java.util.List;

/**
 * @author 志尧
 * @date on 2018-02-28 16:03
 * @email 1417337180@qq.com
 * @describe
 * @ideas
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchHolder> {

    private ISearchItemClick iSearchItemClick;

    public void setiSearchItemClick(ISearchItemClick iSearchItemClick) {
        this.iSearchItemClick = iSearchItemClick;
    }

    private List<POI> datas;

    public SearchAdapter(List<POI> datas) {
        this.datas = datas;
    }

    public void setDatas(List<POI> datas) {
        this.datas = datas;
    }

    private int mode;

    public void setMode(int mode) {
        this.mode = mode;
    }

    @Override
    public SearchHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_map_search, parent, false);
        SearchHolder mapFloorListHolder = new SearchHolder(view);
        view.setOnClickListener(v -> {
            if (iSearchItemClick != null) {
                int tag = (int) view.getTag();
                iSearchItemClick.itemClick(datas.get(tag), tag);
            }
        });
        return mapFloorListHolder;
    }

    @Override
    public void onBindViewHolder(SearchHolder holder, int position) {
        holder.initData(datas.get(position));
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    public class SearchHolder extends RecyclerView.ViewHolder {

        public ImageView ivItemSearchIcon;
        public TextView tvItemSearchName;
        public TextView tvItemSearchFloor;
        public View viewLine;
        public ImageView ivItemSearchUp;


        public SearchHolder(View itemView) {
            super(itemView);
            ivItemSearchIcon = itemView.findViewById(R.id.iv_item_search_icon);
            tvItemSearchName = itemView.findViewById(R.id.tv_item_search_name);
            tvItemSearchFloor = itemView.findViewById(R.id.tv_item_search_floor);
            viewLine = itemView.findViewById(R.id.view_item_search_line);
            ivItemSearchUp = itemView.findViewById(R.id.iv_item_search_up);
        }

        public void initData(POI poi) {
            if (poi == null) {
                return;
            }

            int adapterPosition = getAdapterPosition();
            if (adapterPosition == datas.size() - 1) {
                viewLine.setVisibility(View.GONE);
            } else {
                viewLine.setVisibility(View.VISIBLE);
            }

            if (mode == 0 && datas.size() > 3) {
                if (adapterPosition == datas.size() - 1) {
                    if (poi.getPoiNO() < 0) {
                        ivItemSearchUp.setVisibility(View.GONE);
                        ivItemSearchIcon.setVisibility(View.GONE);
                        tvItemSearchFloor.setVisibility(View.GONE);

                        tvItemSearchName.setText(poi.getName());
                        tvItemSearchName.setGravity(Gravity.CENTER);
                        tvItemSearchName.setTextColor(Color.parseColor("#cccccc"));
                        return;
                    }
                }
            }

            if (mode == 1) {
                ivItemSearchIcon.setImageResource(R.drawable.icon_search_collect);
            } else {
                ivItemSearchIcon.setImageResource(R.drawable.icon_search_history);
            }

            tvItemSearchFloor.setText(poi.getFloor());
            tvItemSearchFloor.setVisibility(View.VISIBLE);
            ivItemSearchIcon.setVisibility(View.VISIBLE);
            ivItemSearchUp.setVisibility(View.VISIBLE);

            tvItemSearchName.setGravity(Gravity.CENTER_VERTICAL);
            tvItemSearchName.setTextColor(Color.parseColor("#333333"));
            tvItemSearchName.setText(poi.getName());
        }
    }

    public interface ISearchItemClick {

        /**
         * 搜索点击
         *
         * @param poi
         * @param position
         */
        void itemClick(POI poi, int position);

    }
}