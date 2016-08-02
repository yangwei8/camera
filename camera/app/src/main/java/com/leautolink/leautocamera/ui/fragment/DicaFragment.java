package com.leautolink.leautocamera.ui.fragment;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.google.gson.reflect.TypeToken;
import com.leautolink.leautocamera.R;
import com.leautolink.leautocamera.domain.respone.BaseInfo;
import com.leautolink.leautocamera.domain.respone.TabInfos;
import com.leautolink.leautocamera.net.http.GsonUtils;
import com.leautolink.leautocamera.net.http.OkHttpRequest;
import com.leautolink.leautocamera.net.http.RequestTag.RequestTag;
import com.leautolink.leautocamera.net.http.httpcallback.GetCallBack;
import com.leautolink.leautocamera.ui.adapter.DiscaViewPagerAdapter;
import com.leautolink.leautocamera.utils.Logger;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

@EFragment(R.layout.fragment_dica)
public class DicaFragment extends Fragment {

    @ViewById(R.id.tab_discover_tab)
    TabLayout tab_discover_tab;

    @ViewById(R.id.vp_main)
    ViewPager vp_main;

    private List<Fragment> fragments;

    private DiscaViewPagerAdapter pagerAdapter;
    private ArrayList<String> tabList;

    @Override
    public void onResume() {
        super.onResume();
        if (tabList==null){
            getTabInfos();
        }
    }

    @AfterViews
    void init(){
//        getTabInfos();
    }


    private void getTabInfos(){
        OkHttpRequest.get(RequestTag.TAB_TAG, RequestTag.TAB_URL, new GetCallBack() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Object response) {
                try {
                    String respone = ((Response)response).body().string();
                    Logger.e(respone);

                    BaseInfo<TabInfos> tabInfosBaseInfo = GsonUtils.getDefault().fromJson(respone, new TypeToken<BaseInfo<TabInfos>>() {
                    }.getType());
                    Logger.e( "   :  " + tabInfosBaseInfo.getRows().size() + "  :   ~!~!~!~!~!~!~!~!~!~!~!~~!~~!~!~!~!~!!~!~!~  ");
                    initAdpater(tabInfosBaseInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                Logger.e(error   + "   : getTabInfos" );
            }
        });
    }

    @UiThread
    void initAdpater(BaseInfo<TabInfos> tabInfosBaseInfo){
        if (tabInfosBaseInfo.getCode()==200 && tabInfosBaseInfo.getRows().size()>0){
            fragments = new ArrayList<>();
            tabList = new ArrayList<>();

            for (int i = 0 ; i< tabInfosBaseInfo.getRows().size() ;i++){
                DiscoverFragment discoverFragment = DiscoverFragment_.builder().build();
                fragments.add(discoverFragment);
                TabInfos tabInfos = tabInfosBaseInfo.getRows().get(i);
                discoverFragment.setClassifyId(tabInfos.getId());
                tabList.add(tabInfos.getName());
            }


            pagerAdapter = new DiscaViewPagerAdapter(getFragmentManager(),fragments , tabList);
            vp_main.setAdapter(pagerAdapter);
            tab_discover_tab.setTabMode(TabLayout.MODE_SCROLLABLE);//设置tab模式，当前为系统默认模式
            tab_discover_tab.addTab(tab_discover_tab.newTab().setText(tabList.get(0)));//添加tab选项卡
            tab_discover_tab.addTab(tab_discover_tab.newTab().setText(tabList.get(1)));
            tab_discover_tab.addTab(tab_discover_tab.newTab().setText(tabList.get(2)));
            tab_discover_tab.setupWithViewPager(vp_main);//将TabLayout和ViewPager关联起来。
            tab_discover_tab.setTabsFromPagerAdapter(pagerAdapter);//给Tabs设置适配器


        }
        


    }


}
