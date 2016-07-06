package com.moandroid.cocos2d.nodes;

import com.moandroid.cocos2d.events.CCAccelerometerDelegate;
import com.moandroid.cocos2d.events.CCAccelerometerDispatcher;
import com.moandroid.cocos2d.events.CCEvent;
import com.moandroid.cocos2d.events.CCStandardTouchDelegate;
import com.moandroid.cocos2d.events.CCTouchDispatcher;
import com.moandroid.cocos2d.renderers.CCDirector;
import com.moandroid.cocos2d.types.CCPoint;
import com.moandroid.cocos2d.types.CCSize;

public class CCLayer extends CCNode 
					 implements CCStandardTouchDelegate,
							    CCAccelerometerDelegate{
	
	static public CCLayer layer(){
		return new CCLayer();
	}
	
	public CCLayer(){
		CCSize s = CCDirector.sharedDirector().winSize();
		_anchorPoint = CCPoint.ccp(0.5f, 0.5f);
		setContentSize(s);
		_isRelativeAnchorPoint = false;
	}
	
	protected boolean _isTouchEnabled = false;
	public boolean isTouchEnabled(){
		return _isTouchEnabled;
	}
	public void setTouchEnabled(boolean enable){
		_isTouchEnabled = enable;
		if(_isRunning){
			if(enable){
				CCTouchDispatcher.sharedDispatcher().addDelegate(this, 0);
			}else{
				CCTouchDispatcher.sharedDispatcher().removeDelegate(this);
			}
		}
	}
	
	private boolean _isAccelerometerEnabled = false;
	public boolean _isAccelerometerEnabled(){
		return _isAccelerometerEnabled;
	}
	public void setAccelerometerEnabled(boolean enable){
		_isAccelerometerEnabled = enable;
		if(_isRunning){
			if(enable){
				CCAccelerometerDispatcher.sharedDispatcher().addDelegate(this);
			}else{
				CCAccelerometerDispatcher.sharedDispatcher().removeDelegate(this);
			}		
		}
	}
	
    protected void registerWithTouchDispatcher() {
        CCTouchDispatcher.sharedDispatcher().addDelegate(this, 0);
    }
    
	public void onEnter(){
		if(_isTouchEnabled){
			CCTouchDispatcher.sharedDispatcher().addDelegate(this, 0);
		}
		if(_isAccelerometerEnabled){
			CCAccelerometerDispatcher.sharedDispatcher().addDelegate(this);
		}
		super.onEnter();
	}
	
	public void onExit(){
		if(_isTouchEnabled){
			CCTouchDispatcher.sharedDispatcher().removeDelegate(this);
		}
		if(_isAccelerometerEnabled){
			CCAccelerometerDispatcher.sharedDispatcher().removeDelegate(this);
		}
		super.onExit();		
	}
	
	@Override
	public boolean touchesBegan(CCEvent event) {
		return false;
	}
	@Override
	public boolean touchesCancelled(CCEvent event) {
		return false;
	}
	@Override
	public boolean touchesEnded(CCEvent event) {
		return false;
	}
	@Override
	public boolean touchesMoved(CCEvent event) {
		return false;
	}

	@Override
	public boolean accelerometerAccuracyChanged(int accuracy) {
		return false;
	}

	@Override
	public boolean accelerometerChanged(float accelX, float accelY, float accelZ) {
		return true;
	}

}
