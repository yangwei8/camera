package com.leautolink.leautocamera.module;

import android.os.Handler;

import java.lang.ref.WeakReference;

public abstract class WeakHandler<T> extends Handler
{
    private WeakReference<T> mOwner;
    
    public WeakHandler(final T t) {
        this.mOwner = new WeakReference<T>(t);
    }
    
    public T getOwner() {
        return this.mOwner.get();
    }
}