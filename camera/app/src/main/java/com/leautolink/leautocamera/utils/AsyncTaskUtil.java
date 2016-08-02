package com.leautolink.leautocamera.utils;

import android.os.AsyncTask;
import android.support.annotation.MainThread;

/**
 * Created by tianwei on 16/6/7.
 */
public class AsyncTaskUtil {
    private AsyncTaskUtil() {
    }

    private AsynchTask mAsynchTask;
    private AsyncTaskListener mAsyncTaskListener;


    public static AsyncTaskUtil newInstance() {
        return new AsyncTaskUtil();
    }

    public AsyncTaskListener getAsyncTaskListener() {
        return mAsyncTaskListener;
    }

    /**
     * 异步执行任务
     *
     * @param params
     * @param asyncTaskListener
     */
    @MainThread
    public synchronized void execute(AsyncTaskListener asyncTaskListener, Object... params) {
        mAsyncTaskListener = asyncTaskListener;
        mAsynchTask = new AsynchTask();
        mAsynchTask.execute(params);
    }

    public void cancel() {
        if (mAsynchTask != null) {
            mAsynchTask.cancel(true);
            mAsynchTask = null;
        }
    }

    public void publishProgress(int progress) {
        if (mAsyncTaskListener != null) {
            mAsyncTaskListener.onProgressUpdate(progress);
        }
    }

    public class AsynchTask extends AsyncTask<Object, Integer, Object> {

        @Override
        protected void onPreExecute() {
            mAsyncTaskListener.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object... params) {
            return mAsyncTaskListener.doInBackground(params);
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
//            mAsyncTaskListener.onProgressUpdate(progress);
        }

        @Override
        protected void onPostExecute(Object result) {
            mAsyncTaskListener.onPostExecute(result);
        }

        @Override
        protected void onCancelled() {
            mAsyncTaskListener.onCancelled();
        }

        @Override
        protected void onCancelled(Object result) {
            mAsyncTaskListener.onCancelled(result);
        }
    }


    public interface AsyncTaskListener {
        void onPreExecute();

        Object doInBackground(Object... params);

        void onProgressUpdate(int progress);

        void onPostExecute(Object result);

        void onCancelled();

        void onCancelled(Object result);
    }

}
