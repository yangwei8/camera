package com.leautolink.leautocamera.ui.activity;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.leautolink.leautocamera.R;
import com.leautolink.leautocamera.ui.base.BaseActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * Created by chenchunyu on 16/4/13.
 */
@EActivity(R.layout.activity_live_remind)
public class LiveRemindActivity extends BaseActivity implements View.OnClickListener {
    @ViewById(R.id.navigation_bar_left_ib)
    ImageButton navigation_bar_left_ib;
    @ViewById(R.id.navigation_bar_title)
    TextView navigation_bar_title;
    @AfterViews
    void init(){
        initView();
        initData();
        initListener();
    }

    private void initListener() {
        navigation_bar_left_ib.setOnClickListener(this);
    }


    private void initView() {
        navigation_bar_title.setText(getResources().getString(R.string.online_show));
    }

    private void initData() {
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.navigation_bar_left_ib:
                this.finish();
                break;
        }
    }
}
