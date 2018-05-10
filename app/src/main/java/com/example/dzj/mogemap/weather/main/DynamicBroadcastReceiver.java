package com.example.dzj.mogemap.weather.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class DynamicBroadcastReceiver extends BroadcastReceiver {

	GetLocation getLocation;
	WindowChange windowChange;
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if(intent.getAction().equals(WeatherActivity.DYNAMICACTION)){	//动作检测
			String result=intent.getStringExtra(WeatherActivity.RESULT);
			LocationInfo locationInfo=intent.getParcelableExtra(WeatherActivity.LOCATIONINFO);
			if(result.equals("fail")){
				Toast.makeText(context, "定位失败，将默认显示北京天气！", Toast.LENGTH_LONG).show();
			}
			getLocation.getLocation(locationInfo);
    	}
    	if(intent.getAction().equals(WeatherActivity.WINDOWCHANGE)){

				windowChange.onChange();

		}
	}
	public interface GetLocation{
		void getLocation(LocationInfo info);
	}
	public interface WindowChange{
		void onChange();
	}
	public void setGetLocation(GetLocation getLocation){
		this.getLocation=getLocation;
	}
	public void setWindowChange(WindowChange windowChange){
		this.windowChange=windowChange;
	}
}
