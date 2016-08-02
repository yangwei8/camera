package com.leautolink.leautocamera.utils;

import org.androidannotations.annotations.EBean;

import java.io.IOException;

/**
 * Created by liushengli on 2016/5/17.
 */
@EBean(scope = EBean.Scope.Singleton)
public class ConnectionDetector {
    public boolean isInternetOn(){
        System.out.println("executeCommand");
        Runtime runtime = Runtime.getRuntime();
        try
        {
            Process  mIpAddrProcess = runtime.exec("/system/bin/ping -c 1 www.baidu.com");
            int mExitValue = mIpAddrProcess.waitFor();
            if(mExitValue==0){
                return true;
            }else{
                return false;
            }
        }
        catch (InterruptedException ignore)
        {
            ignore.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }
}
