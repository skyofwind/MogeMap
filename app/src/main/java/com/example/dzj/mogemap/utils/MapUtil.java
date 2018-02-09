package com.example.dzj.mogemap.utils;

import android.graphics.Color;
import android.os.Bundle;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;

import static com.example.dzj.mogemap.utils.BitmapUtil.bmEnd;
import static com.example.dzj.mogemap.utils.BitmapUtil.bmStart;


/**
 * Created by dzj on 2017/11/6.
 */

public class MapUtil {

    private static MapUtil INSTANCE = new MapUtil();

    private MapStatus mapStatus = null;

    private Marker mMoveMarker = null;

    public MapView mapView = null;

    public BaiduMap baiduMap = null;

    public LatLng lastPoint = null;
    /**
     * 路线覆盖物
     */
    public Overlay polylineOverlay = null;

    public MapUtil() {
    }

    public static MapUtil getInstance() {
        return INSTANCE;
    }

    public void init(MapView view) {
        mapView = view;
        baiduMap = mapView.getMap();
        mapView.showScaleControl(false);
        mapView.showZoomControls(false);
    }
    public void onPause() {
        if (null != mapView) {
            mapView.onPause();
        }
    }

    public void onResume() {
        if (null != mapView) {
            mapView.onResume();
        }
    }

    public void clear() {
        lastPoint = null;
        if (null != mMoveMarker) {
            mMoveMarker.remove();
            mMoveMarker = null;
        }
        if (null != polylineOverlay) {
            polylineOverlay.remove();
            polylineOverlay = null;
        }
        if (null != baiduMap) {
            baiduMap.clear();
            baiduMap = null;
        }
        mapStatus = null;
        if (null != mapView) {
            mapView.onDestroy();
            mapView = null;
        }
    }
    public void drawTwoPointLine(LatLng start, LatLng end){
        List<LatLng> polylines = new ArrayList<>();
        polylines.add(start);
        polylines.add(end);
        PolylineOptions polylineOptions = new PolylineOptions().points(polylines).width(12).color(Color.RED);
        baiduMap.addOverlay(polylineOptions);
    }
    public void drawRuningLine(List<LatLng> points){
        if(points == null || points.size() == 0){
            if (null != polylineOverlay) {
                polylineOverlay.remove();
                polylineOverlay = null;
            }
            return ;
        }

        if(points.size() == 1){
            OverlayOptions startOptions = new MarkerOptions().position(points.get(0)).icon(bmStart)
                    .zIndex(9).draggable(true);
            baiduMap.addOverlay(startOptions);
            animateMapStatus(points.get(0), 18.0f);
            return;
        }
        LatLng startPoint;
        //LatLng endPoint;

        startPoint = points.get(0);
        //endPoint = points.get(points.size() - 1);
        // 添加起点图标
        OverlayOptions startOptions = new MarkerOptions()
                .position(startPoint).icon(bmStart)
                .zIndex(9).draggable(true);
        // 添加终点图标
        //OverlayOptions endOptions = new MarkerOptions().position(endPoint)
        //        .icon(bmEnd).zIndex(9).draggable(true);

        // 添加路线（轨迹）
        OverlayOptions polylineOptions = new PolylineOptions().width(13)
                .color(Color.RED).points(points);

        baiduMap.addOverlay(startOptions);
        //baiduMap.addOverlay(endOptions);
        polylineOverlay = baiduMap.addOverlay(polylineOptions);
    }
    public void drawStartPoint(LatLng latLng){
        // 添加起点图标
        OverlayOptions startOptions = new MarkerOptions()
                .position(latLng).icon(bmStart)
                .zIndex(9).draggable(true);
        baiduMap.addOverlay(startOptions);
    }
    public void drawEndPoint(LatLng latLng){
        // 添加终点图标
        OverlayOptions endOptions = new MarkerOptions().position(latLng)
                .icon(bmEnd).zIndex(9).draggable(true);
        baiduMap.addOverlay(endOptions);
    }
    public void drawHistoryTrack(List<LatLng> points){


        if(points == null || points.size() == 0){
            if (null != polylineOverlay) {
                polylineOverlay.remove();
                polylineOverlay = null;
            }
            return ;
        }

        if(points.size() == 1){
            OverlayOptions startOptions = new MarkerOptions().position(points.get(0)).icon(bmStart)
                    .zIndex(9).draggable(true);
            baiduMap.addOverlay(startOptions);
            animateMapStatus(points.get(0), 18.0f);
            return;
        }
        LatLng startPoint;
        LatLng endPoint;

        startPoint = points.get(0);
        endPoint = points.get(points.size() - 1);
        // 添加起点图标
        OverlayOptions startOptions = new MarkerOptions()
                .position(startPoint).icon(bmStart)
                .zIndex(9).draggable(true);
        // 添加终点图标
        OverlayOptions endOptions = new MarkerOptions().position(endPoint)
                .icon(bmEnd).zIndex(9).draggable(true);

        // 添加路线（轨迹）
        OverlayOptions polylineOptions = new PolylineOptions().width(10)
                .color(Color.BLUE).points(points);

        baiduMap.addOverlay(startOptions);
        baiduMap.addOverlay(endOptions);
        polylineOverlay = baiduMap.addOverlay(polylineOptions);
    }

    public Marker addOverlay(LatLng currentPoint, BitmapDescriptor icon, Bundle bundle) {
        OverlayOptions overlayOptions = new MarkerOptions().position(currentPoint)
            .icon(icon).zIndex(9).draggable(true);
        Marker marker = (Marker) baiduMap.addOverlay(overlayOptions);
        if (null != bundle) {
            marker.setExtraInfo(bundle);
        }
        return marker;
    }
    public void animateMapStatus(List<LatLng> points) {
        if (null == points || points.isEmpty()) {
            return;
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng point : points) {
            builder.include(point);
        }
        MapStatusUpdate msUpdate = MapStatusUpdateFactory.newLatLngBounds(builder.build());
        baiduMap.animateMapStatus(msUpdate);
    }

    public void animateMapStatus(LatLng point, float zoom) {
        MapStatus.Builder builder = new MapStatus.Builder();
        mapStatus = builder.target(point).zoom(zoom).build();
        baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(mapStatus));
    }

    public void setMapStatus(LatLng point, float zoom) {
        MapStatus.Builder builder = new MapStatus.Builder();
        mapStatus = builder.target(point).zoom(zoom).build();
        baiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(mapStatus));
    }

    public void refresh() {
        LatLng mapCenter = baiduMap.getMapStatus().target;
        float mapZoom = baiduMap.getMapStatus().zoom - 1.0f;
        setMapStatus(mapCenter, mapZoom);
    }

}
