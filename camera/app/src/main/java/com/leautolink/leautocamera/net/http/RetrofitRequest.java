//package com.leautolink.leautocamera.net.http;
//
//import com.leautolink.leautocamera.config.Config;
//import com.leautolink.leautocamera.domain.ListingInfo;
//
//import retrofit2.Call;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//import retrofit2.http.GET;
//import retrofit2.http.Path;
//
///**
// * Retrofit请求类
// * Created by tianwei1 on 2016/2/29.
// */
//public class RetrofitRequest {
//    public static final String TAG = "RetrofitRequest";
//    /**
//     * 单例
//     */
//    public static Retrofit mRetrofit = null;
//
//    /**
//     * 创建Retrofit单例
//     *
//     * @return Retrofit单例
//     */
//    public static Retrofit newInstance() {
//        if (mRetrofit == null) {
//            synchronized (RetrofitRequest.class) {
//                if (mRetrofit == null) {
//                    mRetrofit = new Retrofit.Builder()
//                            .baseUrl(Config.HTTP_BASE_URL)
//                            .addConverterFactory(GsonConverterFactory.create())
//                            .build();
//                }
//            }
//        }
//        return mRetrofit;
//    }
//
//    /**
//     * RetrofitService
//     */
//    public interface RetrofitService{
////        @GET({})
////        Call<>
//
//    }
//
//    /**
//     * get请求
//     */
//    public void get() {
//
//    }
//}
