package com.yzy.map3d.ui.main;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rtm.common.model.POI;
import com.rtm.common.model.POIExt;
import com.rtm.common.model.RMLocation;
import com.rtm.common.model.RMLocationMode;
import com.rtm.core.MessageContants;
import com.rtm.core.XunluMap;
import com.rtm.core.model.Location;
import com.rtm.core.model.NavigatePoint;
import com.rtm.core.utils.Handlerlist;
import com.rtm.frm.nmap.MapView;
import com.rtm.frm.nmap.MarkerLayer;
import com.rtm.frm.nmap.RouteLayer;
import com.rtm.frm.nmap.entry.Marker;
import com.rtm.frm.nmap.entry.RouteNode;
import com.rtm.frm.nmap.ifs.OnMapLoadCallBack;
import com.rtm.frm.nmap.ifs.OnNavigateChangeCallBack;
import com.rtm.frm.nmap.ifs.OnPoiSelectedCallBack;
import com.rtm.frm.nmap.view.LayoutPosition;
import com.rtm.location.LocationApp;
import com.rtm.location.utils.RMLocationListener;
import com.yzy.map3d.R;
import com.yzy.map3d.base.BaseAppCompatActivity;
import com.yzy.map3d.base.BasePresenter;
import com.yzy.map3d.bean.MapBuilder;
import com.yzy.map3d.model.MapModel;
import com.yzy.map3d.ui.search.SearchActivity;
import com.yzy.map3d.util.BitmapUtils;
import com.yzy.map3d.util.NetworkUtils;
import com.yzy.map3d.util.StringUtils;
import com.yzy.map3d.util.ToastUtil;
import com.yzy.map3d.util.UiUtils;
import com.yzy.map3d.widget.BluetoothDialogFragment;
import com.yzy.map3d.widget.FloorListView;
import com.yzy.map3d.widget.NetworkDialogFragment;

import java.util.List;

/**
 * @author 志尧
 * @date on 2018-12-19 13:52
 * @email yuzhiyao0912@gmail.com
 * @describe
 * @ideas
 */
public class MapActivity extends BaseAppCompatActivity implements IView {

    private static final int LOCATION_SUCCESS_NUM = 1;
    private static final int NAVIGATION_SUCCESS_NUM = 2;
    private static final int HISTORY_RECORD_NUM = 5;
    private static final int LOCATION_ERROR_NUM = 10;

    BluetoothAdapter mBlueadapter;

    public static final String MAP_BUILDER = "mapBuilder";
    public static final String SEARCH_POI = "searchPoi";

    Presenter presenter;

    MapBuilder mMapBuilder;

    MapView mMapView;
    FloorListView mFloorListView;
    LinearLayout llSearchView;
    LinearLayout llBack;
    LinearLayout llRemidView;
    TextView tvRemid;
    LinearLayout llRecordView;
    TextView tvRecordView;
    ImageView ivLocation;
    LinearLayout llPoiWaitView;
    LinearLayout llPoiView;
    LinearLayout llPoiViewClick;
    TextView tvPoiName;
    TextView tvPoiFloor;
    ImageView ivNavigation;
    LinearLayout llNavigationBg;
    TextView tvNavigationCurrent;
    TextView tvNavigationRoute;
    TextView tvNavigationMyFloor;
    ImageView ivNavigationBack;
    TextView tvNavigationNext;

    AlertDialog mAlertDialog;
    NetworkDialogFragment networkDialogFragment;
    BluetoothDialogFragment bluetoothDialogFragment;

    int locationSuccess;
    int locationError;
    int navigationSuccess;

    MarkerLayer mMarkerLayer;
    RouteLayer mRouteLayer;

    POI mFromPoi;
    POI mToPoi;
    POI mNewHistoryPoi;

    RMLocation mRmLocation;

    Marker mFromMarker;
    Marker mToMarker;
    Marker mSelectMarker;

    MyRMLocationListener myRMLocationListener;

    Bitmap mSelectBitmap;
    Bitmap mFromBitmap;
    Bitmap mToBitmap;

    boolean hasNavigation;
    boolean hasClickFloor = true;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MessageContants.RTMAP_MAP:
                    int progress = msg.arg1;
                    Log.e("prtmap", "SDK进度码" + progress);
                    if (progress == MessageContants.Loader.MAP_LOAD_START) {
                        Log.e("prtmap", "开始加载");
                    } else if (progress == MessageContants.Loader.MAP_FailNetResult) {
                        Log.e("prtmap", "校验结果：" + msg.obj);
                    } else if (progress == MessageContants.Loader.MAP_Down_Success) {
                        Log.e("prtmap", "地图下载成功");
                    } else if (progress == MessageContants.Loader.MAP_Down_Fail) {
                        Log.e("prtmap", "地图下载失败");
                    } else if (progress == MessageContants.Loader.MAP_Update_Success) {
                        Log.e("prtmap", "地图更新成功");
                    } else if (progress == MessageContants.Loader.MAP_Update_Fail) {
                        Log.e("prtmap", "地图更新失败");
                    } else if (progress == MessageContants.Loader.MAP_LOAD_END) {
                        Log.e("prtmap", "地图加载完成");
                    } else if (progress == MessageContants.Loader.MAP_FailCheckNet) {
                        Log.e("prtmap", "网络问题");
                    }
                    break;
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_map;
    }

    @Override
    protected BasePresenter createPresenter() {
        return presenter = new Presenter(this);
    }

    @Override
    protected void initialize() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (hasPermission(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE)) {
                mapInitialize();
            } else {
                requestPermission(
                        RC_MAP,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE);
            }
            return;
        }

        mapInitialize();
    }

    @Override
    public void mapInitialize() {
        mBlueadapter = BluetoothAdapter.getDefaultAdapter();

        findViewById();
        initClick();
        initData();
        initDialog();
        initMap();
        initView();
        initLocation();
    }

    void findViewById() {
        mMapView = findViewById(R.id.mapView);
        mFloorListView = findViewById(R.id.floorListView);
        llSearchView = findViewById(R.id.llSearchView);
        llBack = findViewById(R.id.llBack);
        llRemidView = findViewById(R.id.llRemidView);
        tvRemid = findViewById(R.id.tvRemid);
        llRecordView = findViewById(R.id.llRecordView);
        tvRecordView = findViewById(R.id.tvRecordView);
        ivLocation = findViewById(R.id.ivLocation);
        llPoiWaitView = findViewById(R.id.llPoiWaitView);
        llPoiView = findViewById(R.id.llPoiView);
        llPoiViewClick = findViewById(R.id.llPoiViewClick);
        tvPoiName = findViewById(R.id.tvPoiName);
        tvPoiFloor = findViewById(R.id.tvPoiFloor);
        ivNavigation = findViewById(R.id.ivNavigation);
        llNavigationBg = findViewById(R.id.llNavigationBg);
        tvNavigationCurrent = findViewById(R.id.tvNavigationCurrent);
        tvNavigationRoute = findViewById(R.id.tvNavigationRoute);
        tvNavigationMyFloor = findViewById(R.id.tvNavigationMyFloor);
        ivNavigationBack = findViewById(R.id.ivNavigationBack);
        tvNavigationNext = findViewById(R.id.tvNavigationNext);

        mMapView.setVisibility(View.VISIBLE);
    }

    void initClick() {
        MyViewOnClickListener myViewOnClickListener = new MyViewOnClickListener();
        llSearchView.setOnClickListener(myViewOnClickListener);
        llRecordView.setOnClickListener(myViewOnClickListener);
        llBack.setOnClickListener(myViewOnClickListener);
        ivLocation.setOnClickListener(myViewOnClickListener);
        llPoiView.setOnClickListener(myViewOnClickListener);
        llPoiViewClick.setOnClickListener(myViewOnClickListener);
        ivNavigation.setOnClickListener(myViewOnClickListener);
        ivNavigationBack.setOnClickListener(myViewOnClickListener);
        llPoiWaitView.setOnClickListener(myViewOnClickListener);
        llNavigationBg.setOnClickListener(myViewOnClickListener);
        tvNavigationCurrent.setOnClickListener(myViewOnClickListener);
        tvNavigationNext.setOnClickListener(myViewOnClickListener);

        mFloorListView.setiMapFloorItemClickListener(new MyFloorItemClickListener());
    }

    void initDialog() {
        networkDialogFragment = NetworkDialogFragment.newInstance();
        networkDialogFragment.setiDialogSettingClick(() -> {
            NetworkUtils.openWirelessSettings(MapActivity.this);
        });

        bluetoothDialogFragment = BluetoothDialogFragment.newInstance();
        bluetoothDialogFragment.setiDialogSettingClick(() -> {
            startActivity(new Intent(android.provider
                    .Settings.ACTION_BLUETOOTH_SETTINGS)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        });

        if (mMapBuilder.getHasCheck() != null && mMapBuilder.getHasCheck()) {
            mAlertDialog = new AlertDialog.Builder(MapActivity.this)
                    .setTitle("提示")
                    .setMessage(mMapBuilder.getCheckMsg())
                    .setPositiveButton("我知道了", (dialog, which) -> {

                    })
                    .create();
        }
    }

    void initData() {
        mMapBuilder = getIntent().getParcelableExtra(MAP_BUILDER);
        if (mMapBuilder == null) {
            mMapBuilder = new MapBuilder();
            mMapBuilder.resetData();
        }

        mFromPoi = new POI();
        myRMLocationListener = new MyRMLocationListener();

        mSelectBitmap = BitmapUtils.getBitmap(MapActivity.this, R.drawable.icon_select_poi);
        mFromBitmap = BitmapUtils.getBitmap(MapActivity.this, R.drawable.icon_start_poi);
        mToBitmap = BitmapUtils.getBitmap(MapActivity.this, R.drawable.icon_end_poi);

        mNewHistoryPoi = presenter.getNewHistory(MapActivity.this);

        if (mMapBuilder.getDebug() != null && mMapBuilder.getDebug()) {
            presenter.setRmLocations(mMapBuilder.getBuildId());
        }
    }

    void initMap() {
        Handlerlist.getInstance().register(mHandler);
        XunluMap.getInstance().init(MapActivity.this);
        mMarkerLayer = new MarkerLayer(mMapView);

        RouteLayer.DISTANCE_END = 20;
        RouteLayer.DISTANCE_LIMIT = 8;

        mRouteLayer = mMapView.getRouterLayer();
        mRouteLayer.setReplanWhenDeviated(true);
        mRouteLayer.changeToNavigation();

        mMapView.setOnPoiSelectedCallBack(new MyOnPoiSelectedCallBack());
        mMapView.setOnMapLoadCallBack(new MyOnMapLoadCallBack());
        mRouteLayer.setOnNavigateChangeCallBack(new MyOnNavigateChangeCallBack());

        mMapView.setShowScale(false);
        mMapView.setShowLogo(mMapBuilder.getShowLogo());
        mMapView.setShowCompass(true);
        mMapView.setCompassIcon(BitmapUtils.getBitmap(MapActivity.this, R.drawable.icon_compass));
        LayoutPosition layoutPosition = new LayoutPosition(
                LayoutPosition.Align.LEFT_TOP,
                UiUtils.dip2px(MapActivity.this, 4),
                UiUtils.dip2px(MapActivity.this, 22));
        mMapView.setCompassPosition(layoutPosition);

        mMapView.setLocationIcon(BitmapUtils.getBitmap(MapActivity.this, R.drawable.icon_guide));
        mMapView.setLocationMode(RMLocationMode.NORMAL);
    }

    void initView() {
        llPoiWaitView.setVisibility(View.VISIBLE);
        ivNavigation.setEnabled(false);
        presenter.getFloorList(MapActivity.this, mMapBuilder.getBuildId());
        presenter.searchPoi(
                MapActivity.this, mMapBuilder.getBuildId(),
                mMapBuilder.getSearchName(), mMapBuilder.getFloor());

        if (mMapBuilder.getDebug() != null && mMapBuilder.getDebug()) {
            tvNavigationNext.setVisibility(View.VISIBLE);
        }
    }

    void initLocation() {
        LocationApp.getInstance().init(MapActivity.this);
        if (!LocationApp.getInstance().isStartLocate()) {
            LocationApp.getInstance().start();
        }
    }

    @Override
    public void setFloorList(List<String> floorList) {
        mFloorListView.setFloorList(floorList)
                .setCurrentFloor(mMapBuilder.getFloor())
                .addClickListener()
                .refresh();
        mFloorListView.setVisibility(View.VISIBLE);
    }

    @Override
    public void updateKeyRouteNodeView(String desc, String route) {
        int length = route.length();
        if (length <= 12) {
            tvNavigationRoute.setTextSize(28);
        } else if (length > 12 && length <= 14) {
            tvNavigationRoute.setTextSize(24);
        } else if (length > 14 && length <= 16) {
            tvNavigationRoute.setTextSize(22);
        } else if (length > 16 && length <= 18) {
            tvNavigationRoute.setTextSize(19);
        } else {
            tvNavigationRoute.setTextSize(16);
        }

        tvNavigationCurrent.setText(desc);
        tvNavigationRoute.setText(route);
    }

    @Override
    public void selectedPoi(POI poi) {
        if (poi == null || StringUtils.isEmpty(poi.getName())) {
            return;
        }

        if (mRouteLayer != null && (mRouteLayer.hasData() || mRouteLayer.isNavigating())) {
            return;
        }

        if (mToPoi != null) {
            if (mToPoi.getPoiNO() == poi.getPoiNO()) {
                return;
            }
        }

        mToPoi = poi;
        llRecordView.setVisibility(View.GONE);
        llPoiWaitView.setVisibility(View.GONE);
        llPoiView.setVisibility(View.VISIBLE);
        tvPoiName.setText(poi.getName());
        tvPoiFloor.setText(poi.getFloor());
        showNavigationView();
        addSelectMarker();
        mMapView.moveToCenter(poi.getX(), poi.getY());
    }

    void searchPoiCallback(POI poi) {
        poi.setStyle(5);
        if (!StringUtils.isEquals(poi.getFloor(), mMapBuilder.getFloor())) {
            mFloorListView.setCurrentFloor(poi.getFloor())
                    .addClickListener()
                    .refresh();
        }
        selectedPoi(poi);
    }

    /*****************************************生命周期**************************************/

    @Override
    protected void onStart() {
        super.onStart();
        LocationApp.getInstance().registerLocationListener(myRMLocationListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (99 != requestCode) {
            return;
        }

        if (resultCode == 100) {
            searchPoiCallback((POIExt) data.getSerializableExtra(SEARCH_POI));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocationApp.getInstance().unRegisterLocationListener(myRMLocationListener);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (hasNavigation) {
                onClickNavigationStop();
            } else {
                exitActivity();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.cancelVibrator();

        myRMLocationListener = null;
        networkDialogFragment = null;
        bluetoothDialogFragment = null;

        if (mBlueadapter != null) {
            mBlueadapter.isDiscovering();
            mBlueadapter = null;
        }

        if (mMarkerLayer != null) {
            mMarkerLayer.clearLayer();
            mMarkerLayer = null;
        }

        if (mRouteLayer != null) {
            if (mRouteLayer.isNavigating()) {
                mRouteLayer.stopNavigation();
            }

            mRouteLayer.clearLayer();
            mRouteLayer = null;
        }

        if (mMapView != null) {
            mMapView.removeAllViews();
            mMapView = null;
        }

        if (mSelectBitmap != null) {
            mSelectBitmap.recycle();
            mSelectBitmap = null;
        }

        if (mFromBitmap != null) {
            mFromBitmap.recycle();
            mFromBitmap = null;
        }

        if (mToBitmap != null) {
            mToBitmap.recycle();
            mToBitmap = null;
        }

        System.gc();
    }

    /***************************************View点击事件****************************************/

    class MyViewOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (R.id.llBack == v.getId()) {
                exitActivity();
                return;
            }

            if (R.id.llSearchView == v.getId()) {
                onClickSearch();
                return;
            }

            if (R.id.llRecordView == v.getId()) {
                searchPoiCallback(mNewHistoryPoi);
                return;
            }

            if (R.id.ivLocation == v.getId()) {
                onClickLocation();
                mMapView.setMyCurrentLocation(mRmLocation);
                return;
            }

            if (R.id.llPoiViewClick == v.getId()) {
                onClickPoiView();
                return;
            }

            if (R.id.ivNavigation == v.getId()) {
                onClickPlanNavigation();
                return;
            }

            if (R.id.ivNavigationBack == v.getId()) {
                onClickNavigationStop();
                return;
            }

            if (R.id.tvNavigationCurrent == v.getId()) {
                onClickPlayVoice();
                return;
            }

            if (R.id.tvNavigationNext == v.getId()) {
                presenter.index++;
                return;
            }
        }
    }

    void onClickSearch() {
        Intent intent = new Intent();
        intent.putExtra(SearchActivity.SEARCH_NAME, mMapBuilder.getSearchName());
        intent.putExtra(SearchActivity.SEARCH_BUILDID, mMapBuilder.getBuildId());
        intent.setClass(MapActivity.this, SearchActivity.class);
        startActivityForResult(intent, 99);
    }

    void onClickLocation() {
        if (mRmLocation == null) {
            locationError = 0;
            tvRemid.setText("正在定位...");
            llRemidView.setVisibility(View.VISIBLE);
            return;
        }

        mFloorListView.setMyFloor(mRmLocation.getFloor())
                .setCurrentFloor(mRmLocation.getFloor())
                .addClickListener()
                .refresh();

        mMapView.moveToCenter(mRmLocation.getX(), mRmLocation.getY());
    }

    void onClickPoiView() {
        if (mToPoi == null) {
            return;
        }

        mFloorListView.setCurrentFloor(mToPoi.getFloor())
                .addClickListener()
                .refresh();
        mMapView.moveToCenter(mToPoi.getX(), mToPoi.getY());
    }

    void onClickPlanNavigation() {
        mFromPoi.setName("我的位置");
        mFromPoi.setBuildId(mRmLocation.getBuildID());
        mFromPoi.setFloor(mRmLocation.getFloor());
        mFromPoi.setX(mRmLocation.getX());
        mFromPoi.setY(mRmLocation.getY());
        mRouteLayer.planNavigation(mToPoi, true);
    }

    void onClickNavigationStop() {
        hasNavigation = false;
        hasClickFloor = true;

        if (mRouteLayer.isNavigating()) {
            mRouteLayer.stopNavigation();
        }

        mRouteLayer.clearLayer();
        addSelectMarker();
        llSearchView.setVisibility(View.VISIBLE);
        ivNavigation.setVisibility(View.VISIBLE);
        llPoiView.setVisibility(View.VISIBLE);
        mFloorListView.setVisibility(View.VISIBLE);

        llNavigationBg.setVisibility(View.GONE);
        tvNavigationMyFloor.setVisibility(View.GONE);
        ivNavigationBack.setVisibility(View.GONE);
    }

    void onClickPlayVoice() {

    }

    /***************************************楼层选中****************************************/

    class MyFloorItemClickListener implements FloorListView.IMapFloorItemClickListener {

        @Override
        public void onItemClick(String floor) {
            mMapBuilder.setFloor(floor);
            mMapView.loadMap(mMapBuilder.getBuildId(), mMapBuilder.getFloor());
        }
    }

    /***************************************地图加载****************************************/

    class MyOnMapLoadCallBack implements OnMapLoadCallBack {

        @Override
        public void onMapLoadBegin() {
            if (hasClickFloor) {
                showLoading();
            }
        }

        @Override
        public void onMapLoaded(float v) {
            if (hasClickFloor) {
                loadProgress("正在加载...  " + Math.round(v) + "%");
            }
        }

        @Override
        public void onMapLoadOver() {
            hideLoading();
            if (hasClickFloor) {
                ToastUtil.showCenter(MapActivity.this, " " + mMapBuilder.getFloor() + " ");
            }
        }

        @Override
        public void onMapLoadError(String s) {

        }
    }

    /***************************************选点****************************************/

    class MyOnPoiSelectedCallBack implements OnPoiSelectedCallBack {

        @Override
        public void onPoiSelected(POI poi, Location location) {
            selectedPoi(poi);
        }
    }

    void addSelectMarker() {
        mMarkerLayer.clearLayer();
        if (mToPoi.getStyle() != 5) {
            return;
        }

        mSelectMarker = new Marker(mToPoi, mSelectBitmap);
        mMarkerLayer.addMarker(mSelectMarker);
    }

    void showNavigationView() {
        boolean enabled;
        if (mRmLocation == null || mRmLocation.getError() != 0 || mToPoi == null) {
            enabled = false;
        } else {
            enabled = true;
        }
        ivNavigation.setEnabled(enabled);
    }

    /*****************************************导航**************************************/

    class MyOnNavigateChangeCallBack implements OnNavigateChangeCallBack {

        @Override
        public void onNavigationBegin() {
            if (mMapBuilder.getDebug() != null && mMapBuilder.getDebug()) {
                presenter.clearDebugNavigate(mRmLocation);
            }
            showLoading();
        }

        @Override
        public void onNavigationFailed(String s) {
            ToastUtil.showCenter(MapActivity.this, "导航路线失败");
            hideLoading();
        }

        @Override
        public void onNavigationStarted(float distance, List<NavigatePoint> path, boolean isFromGuide) {
            if (mMapBuilder.getDebug() != null && mMapBuilder.getDebug()) {
                presenter.startDebugNavigate(path);
            }

            MapModel.savePoi2HistoryList(MapActivity.this, mToPoi);

            hasClickFloor = false;
            hasNavigation = true;
            navigationSuccess = 0;

            mMarkerLayer.clearLayer();
            mFromMarker = new Marker(mFromPoi, mFromBitmap);
            mMarkerLayer.addMarker(mFromMarker);

            mToMarker = new Marker(mToPoi, mToBitmap);
            mMarkerLayer.addMarker(mToMarker);

            updateKeyRouteNodeView("导航开始", "全程总长约" + (int) distance + "米");
            tvNavigationMyFloor.setText(mRmLocation.getFloor());

            llSearchView.setVisibility(View.GONE);
            ivNavigation.setVisibility(View.GONE);
            llPoiView.setVisibility(View.GONE);
            mFloorListView.setVisibility(View.GONE);

            llNavigationBg.setVisibility(View.VISIBLE);
            tvNavigationMyFloor.setVisibility(View.VISIBLE);
            ivNavigationBack.setVisibility(View.VISIBLE);


            if (!StringUtils.isEquals(mRmLocation.getFloor(), mMapBuilder.getFloor())) {
                onClickLocation();
            } else {
                hideLoading();
            }

            if (mMapBuilder.getHasCheck() != null && mMapBuilder.getHasCheck()) {
                if (presenter.checkInshow(path, mMapBuilder.getCheckFloor(), mMapBuilder.getCheckPosition())) {
                    if (mAlertDialog != null && !mAlertDialog.isShowing()) {
                        mAlertDialog.show();
                    }
                }
            }
        }

        @Override
        public void onRouteBookChanged(List<RouteNode> list, RouteNode routeNode) {
            navigationSuccess++;
            if (navigationSuccess <= NAVIGATION_SUCCESS_NUM) {
                return;
            }

            presenter.routeBookChanged(routeNode);
        }

        @Override
        public void onMyLocationObliqued() {

        }

        @Override
        public void onPathReplaned(float distance, List<NavigatePoint> list) {

        }

        @Override
        public void onArrived() {
            presenter.startVibrator(MapActivity.this);
            updateKeyRouteNodeView("导航结束", "已到达终点附近");
            mRouteLayer.stopNavigation();
            mRouteLayer.clearLayer();
            hasClickFloor = true;
        }

        @Override
        public void onNavigationFinished() {

        }
    }

    /*****************************************定位**************************************/

    class MyRMLocationListener implements RMLocationListener {

        @Override
        public void onReceiveLocation(RMLocation rmLocation) {
            if (!checkMapLocation()) {
                locationSuccess = 0;
                llRemidView.setVisibility(View.GONE);
                llRecordView.setVisibility(View.GONE);
                return;
            }

            if (mMapBuilder.getDebug() != null && mMapBuilder.getDebug()) {
                rmLocation = presenter.nextDebug();
                if (rmLocation == null) {
                    return;
                }
            }

            if (rmLocation.getError() != 0) {
                locationError++;
                onLocationError(rmLocation.getError());
                return;
            }

            locationSuccess++;
            if (locationSuccess <= LOCATION_SUCCESS_NUM) {
                tvRemid.setText("正在定位...");
                return;
            }

            onLocationSuccess(rmLocation);
        }
    }

    boolean checkMapLocation() {
        if (!NetworkUtils.isConnected(MapActivity.this)) {
            if (bluetoothDialogFragment != null && bluetoothDialogFragment.getDialog() != null
                    && bluetoothDialogFragment.getDialog().isShowing()) {
                bluetoothDialogFragment.dismiss();
            }

            if (networkDialogFragment != null && networkDialogFragment.getDialog() != null
                    && networkDialogFragment.getDialog().isShowing()) {
                return false;
            }

            networkDialogFragment.show(getFragmentManager(), NetworkDialogFragment.NETWORK_DIALOG_TAG);
            return false;
        }

        if (mBlueadapter != null && !mBlueadapter.isEnabled()) {
            if (networkDialogFragment != null && networkDialogFragment.getDialog() != null
                    && networkDialogFragment.getDialog().isShowing()) {
                networkDialogFragment.dismiss();
            }

            if (bluetoothDialogFragment != null && bluetoothDialogFragment.getDialog() != null
                    && bluetoothDialogFragment.getDialog().isShowing()) {
                return false;
            }

            bluetoothDialogFragment.show(getFragmentManager(), BluetoothDialogFragment.BLUETOOTH_DIALOG_TAG);
            return false;
        }

        return true;
    }

    void onLocationError(int error) {
        locationSuccess = 0;
        llRemidView.setVisibility(View.VISIBLE);
        llRecordView.setVisibility(View.GONE);
        ivLocation.setImageResource(R.drawable.icon_location_error);
        dialogDismiss();

        if (locationError == LOCATION_ERROR_NUM + 1) {
            if (error == 11) {
                tvRemid.setText("不在建筑物内");
            }
        }
    }

    void onLocationSuccess(RMLocation rmLocation) {
        locationError = 0;
        llRemidView.setVisibility(View.GONE);
        ivLocation.setImageResource(R.drawable.icon_location_success);
        dialogDismiss();

        mRmLocation = rmLocation;

        if (locationSuccess == LOCATION_SUCCESS_NUM + 1) {
            if (StringUtils.isEmpty(mMapBuilder.getSearchName())) {
                onClickLocation();
                if (mNewHistoryPoi != null) {
                    tvRecordView.setText("您上回找" + mNewHistoryPoi.getName());
                    llRecordView.setVisibility(View.VISIBLE);
                }
            } else {
                mFloorListView.setMyFloor(mRmLocation.getFloor()).refresh();
                showNavigationView();
            }
        }

        if (locationSuccess == HISTORY_RECORD_NUM + 1) {
            llRecordView.setVisibility(View.GONE);
        }

        if (!StringUtils.isEquals(mFloorListView.getMyFloor(), mRmLocation.getFloor())) {
            onClickLocation();
            if (hasNavigation) {
                tvNavigationMyFloor.setText(mRmLocation.getFloor());
            }
        }

        mMapView.setMyCurrentLocation(mRmLocation);

        if (mMapBuilder.getNavigationFollow() != null && mMapBuilder.getNavigationFollow()) {
            if (hasNavigation) {
                mMapView.moveToCenter(mRmLocation.getX(), mRmLocation.getY());
            }
        }
    }

    void dialogDismiss() {
        if (networkDialogFragment != null && networkDialogFragment.getDialog() != null
                && networkDialogFragment.getDialog().isShowing()) {
            networkDialogFragment.dismiss();
        }

        if (bluetoothDialogFragment != null && bluetoothDialogFragment.getDialog() != null
                && bluetoothDialogFragment.getDialog().isShowing()) {
            bluetoothDialogFragment.dismiss();
        }
    }
}