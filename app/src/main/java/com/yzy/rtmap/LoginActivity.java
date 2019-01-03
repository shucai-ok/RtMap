package com.yzy.rtmap;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.yzy.map3d.MapIntentBuilder;
import com.yzy.map3d.util.SPUtils;

public class LoginActivity extends AppCompatActivity {

    private EditText etBuildId;
    private EditText etSearchFloor;
    private EditText etSearchName;
    private Switch switchDebug;
    private Switch switchFollow;
    private Switch switchCheck;
    private LinearLayout llCheckShow;
    private EditText etCheckFloor;
    private EditText etCheckMsg;
    private EditText etCheckX1;
    private EditText etCheckY1;
    private EditText etCheckX2;
    private EditText etCheckY2;
    private Button btToMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewById();
        initView();
    }

    void findViewById() {
        etBuildId = findViewById(R.id.etBuildId);
        etSearchFloor = findViewById(R.id.etSearchFloor);
        etSearchName = findViewById(R.id.etSearchName);
        switchDebug = findViewById(R.id.switchDebug);
        switchFollow = findViewById(R.id.switchFollow);
        switchCheck = findViewById(R.id.switchCheck);
        llCheckShow = findViewById(R.id.llCheckShow);
        etCheckFloor = findViewById(R.id.etCheckFloor);
        etCheckMsg = findViewById(R.id.etCheckMsg);
        etCheckX1 = findViewById(R.id.etCheckX1);
        etCheckY1 = findViewById(R.id.etCheckY1);
        etCheckX2 = findViewById(R.id.etCheckX2);
        etCheckY2 = findViewById(R.id.etCheckY2);
        btToMap = findViewById(R.id.btToMap);
    }

    void initView() {
        etBuildId.setText(SPUtils.getInstance(LoginActivity.this).getString("mapBuildId", ""));

        switchCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                llCheckShow.setVisibility(View.VISIBLE);
            } else {
                llCheckShow.setVisibility(View.GONE);
            }
        });

        btToMap.setOnClickListener(v -> {
            toMap();
        });
    }

    void toMap() {
        String buildId = etBuildId.getText().toString();
        String floor = etSearchFloor.getText().toString();
        String searchName = etSearchName.getText().toString();
        boolean debug = switchDebug.isChecked();
        boolean follow = switchFollow.isChecked();
        boolean check = switchCheck.isChecked();
        String checkFloor = etCheckFloor.getText().toString();
        String checkMsg = etCheckMsg.getText().toString();
        String checkX1 = etCheckX1.getText().toString();
        String checkY1 = etCheckY1.getText().toString();
        String checkX2 = etCheckX2.getText().toString();
        String checkY2 = etCheckY2.getText().toString();

        if (buildId == null || buildId.length() <= 0) {
            Toast.makeText(this, "请输入buildId", Toast.LENGTH_SHORT).show();
            return;
        }

        MapIntentBuilder.Builder builder = new MapIntentBuilder.Builder(LoginActivity.this)
                .setDebug(debug)
                .setBuildId(buildId)
                .setSearchName(searchName)
                .setFloor(floor)
                .setNavigationFollow(follow);

        if (check) {
            float[] floors = new float[]{0, 0, 0, 0};
            floors[0] = Float.parseFloat(checkX1);
            floors[1] = Float.parseFloat(checkY1);
            floors[2] = Float.parseFloat(checkX2);
            floors[3] = Float.parseFloat(checkY2);

            builder.setHasCheck(check)
                    .setCheckFloor(checkFloor)
                    .setCheckMsg(checkMsg)
                    .setCheckPosition(floors);
        }

        SPUtils.getInstance(LoginActivity.this).put("mapBuildId", buildId);
        startActivity(builder.build());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MapIntentBuilder.cleanMap();
    }
}