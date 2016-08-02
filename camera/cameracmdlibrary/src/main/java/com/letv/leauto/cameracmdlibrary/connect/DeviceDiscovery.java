package com.letv.leauto.cameracmdlibrary.connect;

import android.util.Log;

import com.letv.leauto.cameracmdlibrary.common.Config;
import com.letv.leauto.cameracmdlibrary.connect.event.DeviceLostEvent;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import de.greenrobot.event.EventBus;

/**
 * Created by liushengli on 2016/4/27.
 */
public class DeviceDiscovery {
    private static final String  TAG="DeviceDiscovery";
    private static final int  SERVER_PORT= 7877;
    private static final int  READBUFFER_SIZE= 1024;
    private static final int  REQUEST_INTERVAL= 5000;
    private static final int  MAX_ERROR = 3;
    private byte[] mReadBuffer = new byte[READBUFFER_SIZE];
    private AtomicBoolean isStop = new AtomicBoolean(false);
    private AtomicBoolean isRunning = new AtomicBoolean(false);
    private int mErrTimes  = 0;
    private Object mLock = new Object();
    private static DeviceDiscovery mSelf;
    private DeviceDiscovery(){}
    public static DeviceDiscovery getInstance(){
        if(mSelf==null){
            mSelf = new DeviceDiscovery();
        }
        return mSelf;
    }
    public void startDiscovery(){
        if(isRunning.get())
            return;
        isRunning.set(true);

        new Thread(mStartDiscoveryTask).start();

        isRunning.set(false);
    }
    private Runnable mStartDiscoveryTask = new Runnable() {
        @Override
        public void run() {
            isStop.set(false);
            try {
                DatagramSocket socket = new DatagramSocket();
                socket.setBroadcast(true);
                DatagramPacket receivePacket = new DatagramPacket(mReadBuffer, mReadBuffer.length);
                socket.setSoTimeout(REQUEST_INTERVAL);
                String    data    =   "amba discovery";
                DatagramPacket sendPacket =
                        new DatagramPacket(data.getBytes(), data.getBytes().length, InetAddress.getByName(Config.CAMERA_IP), SERVER_PORT);
                while(!isStop.get()){
                    try {
                        if(MAX_ERROR<=mErrTimes){
                            Log.e(TAG, "StartDiscoveryTask device lost");
                            EventBus.getDefault().post(new DeviceLostEvent());
                            mErrTimes = 0;
                        }
                        Log.e(TAG, "StartDiscoveryTask begin send");
                        socket.send(sendPacket);
                        socket.receive(receivePacket);
                        Log.e(TAG, "StartDiscoveryTask after receive");
                        sleep();
                        mErrTimes = 0;
                        //Log.e(TAG, "StartDiscoveryTask end "+isStop.get());
                    }
                    catch (SocketTimeoutException e){
                        Log.e(TAG, "SocketTimeoutException  ----------->");
                        Log.e(TAG,e.getMessage());
                        mErrTimes++;
                    }
                    catch(Exception e){
                        Log.e(TAG, "Exception  ----------->");
                        Log.e(TAG,e.getMessage());
                        mErrTimes++;
                        sleep();
                    }
                }
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private void sleep(){
        synchronized (mLock){
            try {
                mLock.wait(REQUEST_INTERVAL);
            }
            catch(Exception e){
            }
        }
    }
    public void stopDiscovery(){
        //Log.e(TAG, "stopDiscovery ");
        isStop.set(true);
        synchronized (mLock){
            mLock.notifyAll();
        }
    }
}
