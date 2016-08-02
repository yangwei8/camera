package com.leautolink.leautocamera.utils;

import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.leautolink.leautocamera.R;

/**
 * Created by lixinlei on 16/7/5.
 */
public class LocationUtils implements AMapLocationListener ,GeocodeSearch.OnGeocodeSearchListener {

    Context context ;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    AMapLocationClient mlocationClient = null;

    private GeocodeSearch geocoderSearch;
    private String addressName;
    private OnEndLocation endLocation = null;


    public LocationUtils(Context context) {
        this.context = context.getApplicationContext();
        initAMap();
        geocoderSearch = new GeocodeSearch( this.context );
        geocoderSearch.setOnGeocodeSearchListener(this);
    }

    public void setEndLocation(OnEndLocation endLocation) {
        this.endLocation = endLocation;
    }

    private void initAMap(){
        mlocationClient = new AMapLocationClient(context);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位监听
        mlocationClient.setLocationListener(this);
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //设置退出时是否杀死service
        //默认值:false, 不杀死
        mLocationOption.setKillProcess(true);
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        //启动定位
        mlocationClient.startLocation();
    }

    /**
     * 响应逆地理编码
     */
    public void getAddress(final LatLonPoint latLonPoint , String model) {
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
                model);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系GeocodeSearch.AMAP
        geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
    }
    public void startLocation(){
        mlocationClient.startLocation();
    }

    public void stopLocation(){
        mlocationClient.stopLocation();
    }

    public void destroy(){
        stopLocation();
        mlocationClient.onDestroy();
    }


    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (endLocation!=null){
            endLocation.onLocationChanged(amapLocation);
        }

    }


    /**
     * 根据定位结果返回定位信息的字符串
     * @param location
     * @return
     */
    public synchronized  String getLocationStr(AMapLocation location){
        if(null == location){
            return null;
        }
        StringBuffer sb = new StringBuffer();
        //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
        if(location.getErrorCode() == 0){
            LatLonPoint point = new LatLonPoint(location.getLatitude(),location.getLongitude());
            getAddress(point,GeocodeSearch.AMAP);

            sb.append(context.getResources().getString(R.string.location1) + "\n");
            sb.append(context.getResources().getString(R.string.location2) + location.getLocationType() + "\n");
            sb.append(context.getResources().getString(R.string.location3) + location.getLongitude() + "\n");
            sb.append(context.getResources().getString(R.string.location4)+ location.getLatitude() + "\n");
            sb.append(context.getResources().getString(R.string.location5)+ location.getAccuracy() + context.getResources().getString(R.string.location6) + "\n");
            sb.append(context.getResources().getString(R.string.location7) + location.getProvider() + "\n");

            if (location.getProvider().equalsIgnoreCase(
                    android.location.LocationManager.GPS_PROVIDER)) {
                // 以下信息只有提供者是GPS时才会有
                sb.append(context.getResources().getString(R.string.location8)+ location.getSpeed() + context.getResources().getString(R.string.location9)+ "\n");
                sb.append(context.getResources().getString(R.string.location10)+ location.getBearing() + "\n");
                // 获取当前提供定位服务的卫星个数
                sb.append(context.getResources().getString(R.string.location11)
                        + location.getSatellites() + "\n");
            } else {
                // 提供者是GPS时是没有以下信息的
                sb.append(context.getResources().getString(R.string.location12) + location.getCountry() + "\n");
                sb.append(context.getResources().getString(R.string.location13)+ location.getProvince() + "\n");
                sb.append(context.getResources().getString(R.string.location14)+ location.getCity() + "\n");
                sb.append(context.getResources().getString(R.string.location15)+ location.getCityCode() + "\n");
                sb.append(context.getResources().getString(R.string.location16)+ location.getDistrict() + "\n");
                sb.append(context.getResources().getString(R.string.location17)+ location.getAdCode() + "\n");
                sb.append(context.getResources().getString(R.string.location18)+ location.getAddress() + "\n");
                sb.append(context.getResources().getString(R.string.location19)+ location.getPoiName() + "\n");
            }
        } else {
            //定位失败
            sb.append(context.getResources().getString(R.string.location20)+ "\n");
            sb.append(context.getResources().getString(R.string.location21)+ location.getErrorCode() + "\n");
            sb.append(context.getResources().getString(R.string.location22)+ location.getErrorInfo() + "\n");
            sb.append(context.getResources().getString(R.string.location23)+ location.getLocationDetail() + "\n");
        }
        return sb.toString();
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        if (endLocation!=null){
            endLocation.onRegeocodeSearched(result,rCode);
        }

    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }


    public interface OnEndLocation{
        void onLocationChanged(AMapLocation location);
        void onRegeocodeSearched(RegeocodeResult result, int rCode);
    }

}
