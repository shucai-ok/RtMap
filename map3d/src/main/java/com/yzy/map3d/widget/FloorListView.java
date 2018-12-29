package com.yzy.map3d.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yzy.map3d.R;
import com.yzy.map3d.util.StringUtils;

import java.util.List;

/**
 * @author 志尧
 * @date on 2018-01-22 15:31
 * @email 1417337180@qq.com
 * @describe 楼层切换控件
 * @ideas
 */

public class FloorListView extends LinearLayout {

    private IMapFloorItemClickListener iMapFloorItemClickListener;
    private RecyclerView rvMapList;

    public void setiMapFloorItemClickListener(IMapFloorItemClickListener iMapFloorItemClickListener) {
        this.iMapFloorItemClickListener = iMapFloorItemClickListener;
    }

    private LinearLayoutManager linearLayoutManager;

    private MapFloorListAdapter mapFloorListAdapter;

    //当前楼层
    private String currentFloor;
    //我的所在楼层
    private String myFloor;
    //楼层列表
    private List<String> floorList;

    public FloorListView setFloorList(List<String> floorList) {
        this.floorList = floorList;
        return this;
    }

    public FloorListView setCurrentFloor(String currentFloor) {
        this.currentFloor = currentFloor;
        return this;
    }

    public FloorListView setMyFloor(String myFloor) {
        this.myFloor = myFloor;
        return this;
    }

    public String getMyFloor() {
        return myFloor;
    }

    public FloorListView addClickListener() {
        if (iMapFloorItemClickListener != null) {
            iMapFloorItemClickListener.onItemClick(currentFloor);
        }

        return this;
    }

    public FloorListView(Context context) {
        this(context, null);
    }

    public FloorListView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloorListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_floor_list, this);

        rvMapList = findViewById(R.id.rv_map_list);
        LinearLayout llMapFloorUp = findViewById(R.id.ll_map_floor_up);
        LinearLayout llMapFloorDown = findViewById(R.id.ll_map_floor_down);

        linearLayoutManager = new LinearLayoutManager(getContext());
        rvMapList.setLayoutManager(linearLayoutManager);
        mapFloorListAdapter = new MapFloorListAdapter(floorList);
        rvMapList.setAdapter(mapFloorListAdapter);

        mapFloorListAdapter.setiRecyclerViewItemClickListener((view, floor, position) -> {
            if (iMapFloorItemClickListener != null) {
                if (StringUtils.isEquals(currentFloor, floor)) {
                    return;
                }

                setCurrentFloor(floor).refresh();
                iMapFloorItemClickListener.onItemClick(floor);
            }
        });

        llMapFloorUp.setOnClickListener(view -> {
            if (iMapFloorItemClickListener == null) {
                return;
            }

            int floorIndex = getFloorIndex();
            if (floorIndex == 0) {
                return;
            }

            floorIndex--;

            String upFloor = floorList.get(floorIndex);
            if (StringUtils.isEquals(currentFloor, upFloor)) {
                return;
            }

            setCurrentFloor(upFloor).refresh();
            iMapFloorItemClickListener.onItemClick(upFloor);
        });

        llMapFloorDown.setOnClickListener(view -> {
            if (iMapFloorItemClickListener == null) {
                return;
            }

            int floorIndex = getFloorIndex();
            if (floorIndex == floorList.size() - 1) {
                return;
            }

            floorIndex++;

            String downFloor = floorList.get(floorIndex);
            if (StringUtils.isEquals(currentFloor, downFloor)) {
                return;
            }

            setCurrentFloor(downFloor).refresh();
            iMapFloorItemClickListener.onItemClick(downFloor);
        });
    }

    class MapFloorListAdapter extends RecyclerView.Adapter<MapFloorListAdapter.MapFloorListHolder> implements OnClickListener {

        private IRecyclerViewItemClickListener iRecyclerViewItemClickListener;

        public void setiRecyclerViewItemClickListener(IRecyclerViewItemClickListener iRecyclerViewItemClickListener) {
            this.iRecyclerViewItemClickListener = iRecyclerViewItemClickListener;
        }

        private List<String> datas = null;

        public MapFloorListAdapter(List<String> datas) {
            this.datas = datas;
        }

        public void setDatas(List<String> datas) {
            this.datas = datas;
        }

        @Override
        public FloorListView.MapFloorListAdapter.MapFloorListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_floor, parent, false);
            MapFloorListHolder mapFloorListHolder = new MapFloorListHolder(view);
            view.setOnClickListener(this);
            return mapFloorListHolder;
        }

        @Override
        public int getItemCount() {
            return datas == null ? 0 : datas.size();
        }

        @Override
        public void onBindViewHolder(FloorListView.MapFloorListAdapter.MapFloorListHolder holder, int position) {
            if (StringUtils.isEquals(myFloor, datas.get(position))) {
                holder.ivFloorSelect.setVisibility(VISIBLE);
            } else {
                holder.ivFloorSelect.setVisibility(GONE);
            }

            if (StringUtils.isEquals(currentFloor, datas.get(position))) {
                holder.tvFloor.setSelected(true);
            } else {
                holder.tvFloor.setSelected(false);
            }

            if (datas.size() - 1 == position) {
                holder.viewLineBottom.setVisibility(VISIBLE);
            } else {
                holder.viewLineBottom.setVisibility(GONE);
            }

            holder.tvFloor.setText(datas.get(position));
            holder.itemView.setTag(R.id.tag_position, position);
            holder.itemView.setTag(R.id.tag_floor, datas.get(position));
        }

        @Override
        public void onClick(View view) {
            String tag = (String) view.getTag(R.id.tag_floor);

            if (iRecyclerViewItemClickListener != null) {
                iRecyclerViewItemClickListener.onItemClick(view,
                        tag, (int) view.getTag(R.id.tag_position));
            }
        }

        public class MapFloorListHolder extends RecyclerView.ViewHolder {

            public TextView tvFloor;
            public ImageView ivFloorSelect;
            public View viewLineTop;
            public View viewLineBottom;

            public MapFloorListHolder(View view) {
                super(view);
                tvFloor = view.findViewById(R.id.tv_floor);
                ivFloorSelect = view.findViewById(R.id.iv_floor_select);
                viewLineTop = view.findViewById(R.id.view_line_top);
                viewLineBottom = view.findViewById(R.id.view_line_bottom);
            }
        }
    }

    public interface IRecyclerViewItemClickListener {
        void onItemClick(View view, String floor, int position);
    }

    public interface IMapFloorItemClickListener {
        void onItemClick(String floor);
    }

    public void refresh() {
        if (null == floorList || floorList.isEmpty()) {
            return;
        }

        if (mapFloorListAdapter != null) {
            mapFloorListAdapter.setDatas(floorList);
            mapFloorListAdapter.notifyDataSetChanged();
        }

        rvMapList.post(() -> {
            int floorIndex = getFloorIndex() - 1;
            if (floorIndex < 0) {
                floorIndex = 0;
            }
            linearLayoutManager.scrollToPositionWithOffset(floorIndex, 0);
        });

        return;
    }

    int getFloorIndex() {
        for (int i = 0; i < floorList.size(); i++) {
            if (StringUtils.isEquals(currentFloor, floorList.get(i))) {
                return i;
            }
        }
        return 0;
    }
}