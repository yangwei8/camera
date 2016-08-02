package com.letv.leauto.cameracmdlibrary.connect.socket;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.letv.leauto.cameracmdlibrary.common.Config;
import com.letv.leauto.cameracmdlibrary.connect.event.ChannelErrorEventType;
import com.letv.leauto.cameracmdlibrary.connect.event.EventBusHelper;
import com.letv.leauto.cameracmdlibrary.utils.HashUtils;
import com.letv.leauto.cameracmdlibrary.utils.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by lixinlei on 15/9/29.
 */
public class SocketDataConnect {
    private static final String TAG = SocketDataConnect.class.getSimpleName();

    private final static int PROGRESS_MIN_STEP = 1;

    protected InputStream mInputStream;
    protected OutputStream mOutputStream;
    protected boolean mContinueRx;

    protected boolean mContinueTx;
    protected int mTxBytes;
    protected final Object mTxLock = new Object();

    private static final ExecutorService worker =
            Executors.newSingleThreadExecutor();

    private static final int CONN_TIME_OUT = 3000;
    private static final int READ_TIME_OUT = 30000;

    private Socket mSocket;
    private String mHostName;
    private int mPortNum;

    /**是否连接*/
    private boolean isConnect = false;

    public boolean isConnect() {
        return isConnect;
    }

    public SocketDataConnect setIP(String host, int port) {
        mHostName = host;
        mPortNum = port;
        return this;
    }

    /**
     * close socket
     */
    public void close() {
        try {
            if (mOutputStream != null) {
                mOutputStream.close();
                mOutputStream = null;
            }
            if (mInputStream != null) {
                mInputStream.close();
                mInputStream = null;
            }
            if (mSocket != null) {
                mSocket.close();
                mSocket = null;
            }
            isConnect = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean connect() {
        if (mSocket != null) {
            Logger.d(TAG, " connect() -->| "+"close old socket");
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mSocket = null;
        }
        Logger.d(TAG,"connect() -->| Connecting to Data socket...");
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(Config.CAMERA_IP, mPortNum), CONN_TIME_OUT);
            socket.setSoTimeout(READ_TIME_OUT);
            setStream(socket.getInputStream(), socket.getOutputStream());
            mSocket = socket;
            isConnect = true;
            return true;
        } catch (Exception e) {
            Logger.d(TAG,  "connect()  -->| "+e.getMessage());
            String message = "Can't connect to " + mHostName + "/" + mPortNum;
            isConnect = false;
            EventBusHelper.postChannelError(ChannelErrorEventType.CMD_CHANNEL_ERROR_CAN_NOT_CONNENT_SOCKET);
        }
        return false;
    }

    public SocketDataConnect setStream(InputStream input, OutputStream output) {
        mInputStream = input;
        mOutputStream = output;
        return this;
    }

    public void getFile(final String dstPath, final int size, final DownLoadCallBack callBack) {
        mContinueRx = true;
        worker.execute(new Runnable() {
            public void run() {
                rxDown(dstPath, size, callBack);
            }
        });
    }

    public void cancelGetFile() {
        mContinueRx = false;
    }

    public void putFile(final String srcPath , final UpLoadCallBack callBack) {
        mContinueTx = true;
        worker.execute(new Runnable() {
            public void run() {
                txStream(srcPath,callBack);
            }
        });
    }

    public int cancelPutFile() {
        mContinueTx = false;
        synchronized (mTxLock) {
            try {
                mTxLock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return mTxBytes;
    }

    public void setIsConnect(boolean isConnect) {
        this.isConnect = isConnect;
        close();
    }

    private void txStream(String srcPath,UpLoadCallBack callBack) {
        if(isConnect) {
            int total = 0;
            int prev = 0;

            try {
                byte[] buffer = new byte[1024];
                File file = new File(srcPath);
                FileInputStream in = new FileInputStream(file);
                final int size = (int) file.length();

                mTxBytes = 0;
//            EventBus.getDefault().post(new DataChannelUpLoadEvent(DataChannelUpLoadEvent.START_UPLOAD,0));
                callBack.onStart();
                while (mContinueTx) {
                    int read = in.read(buffer);
                    if (read <= 0)
                        break;
                    mOutputStream.write(buffer, 0, read);
                    mTxBytes += read;

                    total += read;
                    int curr = (int) (((long) total * 100) / size);
                    if (curr - prev >= PROGRESS_MIN_STEP) {
//                    mListener.onChannelEvent(
//                            IChannelListener.DATA_CHANNEL_EVENT_PUT_PROGRESS, curr);
//                    EventBus.getDefault().post(new DataChannelUpLoadEvent(DataChannelUpLoadEvent.START_UPLOAD,curr));
                        callBack.onProgress(curr);
                        prev = curr;
                    }
                }
                in.close();

                if (mContinueTx) {
                    callBack.onEnd();
                } else {

                }
                synchronized (mTxLock) {
                    mTxLock.notify();
                }
            } catch (Exception e) {
                isConnect = false;
                Logger.d(TAG,  " txStream -->| error : "+e.getMessage());
                callBack.onFailure();
            }
        }else {
            this.connect();
        }
    }

    /**
     * @param dstPath 需要保存的路径
     * @param size    文件大小
     */
    private void rxDown(String dstPath, int size,DownLoadCallBack callBack) {
        if (isConnect) {
            int total = 0;
            int prev = 0;

            try {
                byte[] buffer = new byte[1024];
                FileOutputStream out = null;
                int bytes;
                callBack.onStart(dstPath);
                out = new FileOutputStream(dstPath);
                while (total < size) {
                    try {
                        bytes = mInputStream.read(buffer);
                        out.write(buffer, 0, bytes);
                    } catch (SocketTimeoutException e) {
                        File file = new File(dstPath);
                        if (file.exists()){
                            file.delete();
                        }
                        if (!mContinueRx) {
                            Logger.d(TAG,  "rxDown() -->| "+"RX canceled");
                            out.close();
                            return;
                        }
                        continue;
                    }catch (Exception e){
                        File file = new File(dstPath);
                        if (file.exists()){
                            file.delete();
                        }
                        callBack.onFailure();
                        return;
                    }
                    total += bytes;
                    int curr = (int) (((long) total * 100) / size);
                    if (curr - prev >= PROGRESS_MIN_STEP) {
                        callBack.onProgress(curr);
                        prev = curr;
                    }
                }
                out.close();
                String md5 = HashUtils.getMd5ByFile(new File(dstPath));
                callBack.onEnd(md5);
                return;
            } catch (IOException e) {
                isConnect = false;
                e.printStackTrace();
            }
        }else {
            this.connect();
        }
    }

    private void rxStream(String dstPath, int size) {
        if (isConnect) {
            int total = 0;
            int prev = 0;

            try {
                byte[] buffer = new byte[1024];
                int bytes;
                Bitmap bitmap = BitmapFactory.decodeStream(mInputStream);
                while (total < size) {
                    try {
                        bytes = mInputStream.read(buffer);
                    } catch (SocketTimeoutException e) {
                        if (!mContinueRx) {
                            Logger.d(TAG,  "rxStream() -->| "+"RX canceled");
                            return;
                        }
                        continue;
                    }
                    total += bytes;
                    int curr = (int) (((long) total * 100) / size);
                    if (curr - prev >= PROGRESS_MIN_STEP) {
                        prev = curr;
                    }
                }
                return;
            } catch (IOException e) {
                isConnect = false;
                e.printStackTrace();
            }
        }else {
            this.connect();
        }
    }



}
