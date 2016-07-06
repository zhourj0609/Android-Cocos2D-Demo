package com.moandroid.cocos2d.events;

import java.util.ArrayList;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.moandroid.cocos2d.renderers.CCDirector;

public class CCAccelerometerDispatcher {
	public static final String LOG_TAG = CCAccelerometerDispatcher.class.getSimpleName();
	
	private static CCAccelerometerDispatcher _sharedDispatcher;
	public static CCAccelerometerDispatcher sharedDispatcher() {
		if(_sharedDispatcher == null){
			_sharedDispatcher = new CCAccelerometerDispatcher();
		}
		return _sharedDispatcher;
	}
	
	protected CCAccelerometerDispatcher(){
		_purged = false;
		_toAdd = false;
		_toRemove = false;
		_toQuit = false;
		_locked = false;
		_isAccelerometerEnabled = false;
		_listener = new accelerometerListener();
		_delegatesToAdd = new ArrayList<CCAccelerometerDelegate>(8);
		_delegatesToRemove = new ArrayList<CCAccelerometerDelegate>(8);
		_delegates = new ArrayList<CCAccelerometerDelegate>(8);
	}
	
	private static boolean _purged;
	public static void purgeSharedDispatcher() {
		if(_sharedDispatcher != null){
			_sharedDispatcher.setIsAccelerometerEnabled(false);
			_sharedDispatcher._listener = null;
			_sharedDispatcher._delegates.clear();
			_sharedDispatcher._delegates = null;
			_sharedDispatcher._delegatesToAdd.clear();
			_sharedDispatcher._delegatesToAdd = null;
			_sharedDispatcher._delegatesToRemove.clear();
			_sharedDispatcher._delegatesToRemove = null;
			_sharedDispatcher = null;
			_purged = true;
		}
	}

	public void accelerometerAccuracyChanged( int accuracy){
		if(_purged){
			return;
		}
		_locked = true;
		updateDelegate();
        for( CCAccelerometerDelegate delegate : _delegates ) {
            if( delegate.accelerometerAccuracyChanged(accuracy))
                break;
        }
        _locked = false;
	}
	
	public void accelerometerChanged(float x, float y, float z){
		if(_purged){
			return;
		}
		_locked = true;
		updateDelegate();
        for( CCAccelerometerDelegate delegate : _delegates ) {
            if( delegate.accelerometerChanged(x,y,z))
                break;
        }
        _locked = false;	
	}
	
	class accelerometerListener implements SensorEventListener{

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			CCAccelerometerEvent cevent = new CCAccelerometerEvent();
			cevent.type = CCEvent.CC_EVENT_ACCELEROMETER_ACCURACY_CHANGED;
			cevent.accuracy = accuracy;
			CCEventManager.sharedManager().addEvent(cevent);
		}
			
		@Override
		public void onSensorChanged(SensorEvent event) {
			CCAccelerometerEvent cevent = new CCAccelerometerEvent();
			cevent.type = CCEvent.CC_EVENT_ACCELEROMETER_CHANGED;
			cevent.accelx = event.values[0];
			cevent.accely = event.values[1];
			cevent.accelz = event.values[2];
			CCEventManager.sharedManager().addEvent(cevent);
		}		
	}
	
	accelerometerListener _listener;
	boolean _isAccelerometerEnabled;
	public boolean isAccelerometerEnabled(){
		return _isAccelerometerEnabled;
	}
	
	protected void setIsAccelerometerEnabled(boolean enable){
		if(_isAccelerometerEnabled == enable)
			return;
		Context context = CCDirector.sharedDirector().context();
		if(context!=null){
			SensorManager sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
			Sensor sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			if(sensor!=null){
				if(enable){
					sm.registerListener(_listener, sensor, SensorManager.SENSOR_DELAY_GAME);
					_isAccelerometerEnabled = true;
				}else{
					sm.unregisterListener(_listener);
					_isAccelerometerEnabled = false;
				}
			}		
		}
	}
	
	private ArrayList<CCAccelerometerDelegate> _delegatesToAdd;
	private ArrayList<CCAccelerometerDelegate> _delegatesToRemove;
	
	private boolean _toAdd;
	private boolean _toRemove;
	private boolean _toQuit;
	private boolean _locked;
	private ArrayList<CCAccelerometerDelegate> _delegates;
	
	public /*synchronized*/ void addDelegate(CCAccelerometerDelegate delegate){
		if(!_locked){
			setIsAccelerometerEnabled(true);
			if(!_delegates.contains(delegate)){
				_delegates.add(delegate);
			}			
		}else{
			_delegatesToAdd.add(delegate);
			_toAdd = true;
		}
	}
	
	public /*synchronized*/ void removeDelegate(CCAccelerometerDelegate delegate){
		if(!_locked){
			_delegates.remove(delegate);
			if(_delegates.isEmpty()){
				_isAccelerometerEnabled = false;
			}
		}else{
			_delegatesToRemove.add(delegate);
			_toRemove = true;
		}
	}
	
	public /*synchronized*/ void removeAllDelegate(){
		if(!_locked){
			_delegates.clear();
			setIsAccelerometerEnabled(false);
		}else{
			_toQuit = true;
		}
	}
	
	protected /*synchronized*/ void updateDelegate(){
		if(_toQuit){
			_toQuit = false;
			_delegates.clear();
			setIsAccelerometerEnabled(false);
		}
		if(_toRemove){
			_toRemove = false;
			for(CCAccelerometerDelegate delegate:_delegatesToRemove){
				removeDelegate(delegate);
			}
			_delegatesToRemove.clear();
		}
		if(_toAdd){
			_toAdd = false;
			for(CCAccelerometerDelegate delegate:_delegatesToAdd){
				addDelegate(delegate);
			}
			_delegatesToAdd.clear();
		}
	}
}
