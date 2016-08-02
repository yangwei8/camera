package com.leautolink.leautocamera.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.letv.loginsdk.R;
import com.leautolink.leautocamera.utils.Logger;
import com.letv.loginsdk.LetvLoginSdkManager;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * Created by tianwei on 16/7/19.
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;
    public String code;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxentry);
        api = WXAPIFactory.createWXAPI(this, LetvLoginSdkManager.WX_APP_ID, false);
        api.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        finish();
    }

    @Override
    public void onResp(BaseResp resp) {
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                Logger.e("ZSM", "WXEntryActivity  resp : " + resp);
                if (resp.toString().contains("com.tencent.mm.sdk.modelmsg.SendAuth")) {
                    code = ((SendAuth.Resp) resp).code;
                    Logger.e("ZSM", "WXEntryActivity  code : " + code);
                    new LetvLoginSdkManager().getAccessTokenByCode(code, LetvLoginSdkManager.WX_APP_ID, LetvLoginSdkManager.WX_APP_SECRET, WXEntryActivity.this);
                }
                finish();
                break;
            default:
                finish();
                break;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
        finish();
    }
}
