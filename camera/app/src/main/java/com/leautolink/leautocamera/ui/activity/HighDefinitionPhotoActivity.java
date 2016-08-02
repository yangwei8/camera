package com.leautolink.leautocamera.ui.activity;

import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.leautolink.leautocamera.R;
import com.leautolink.leautocamera.ui.base.BaseActivity;
import com.polites.android.GestureImageView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;

@EActivity(R.layout.activity_high_definition_photo)
@WindowFeature(Window.FEATURE_NO_TITLE)
public class HighDefinitionPhotoActivity extends BaseActivity {

    @ViewById(R.id.image)
    GestureImageView image;

    @ViewById(R.id.close)
    ImageView close;

    @ViewById(R.id.main_content)
    RelativeLayout main_content;
    @Extra
    String url;
    @Extra
    String hightUrl;
    @Extra
    int videoID;


    @AfterViews
    void init() {
           Glide.with(this)
                   .load(hightUrl)
                   .thumbnail(0.1f)
                   .into(image);
    }
    @Click({R.id.main_content,R.id.close})
    void click(View view){
        switch (view.getId()){
            case R.id.close:
                this.finish();
                break;
        }
    }

}
