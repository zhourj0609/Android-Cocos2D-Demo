package com.moandroid.cocos2d.events;

import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import com.moandroid.cocos2d.actions.CCTimer;
import com.moandroid.cocos2d.renderers.CCScheduler;


public class CCEventManager {
	public static final String LOG_TAG = CCEventManager.class.getSimpleName();
	public static final int BLOCK_CAPABILITY = 50;
	public int _blockNum;
	private static CCEventManager _shareManager;
	public static CCEventManager sharedManager() {
		if(_shareManager == null)
			_shareManager = new CCEventManager();
		return _shareManager;
	}

	public CCEventManager(){
		_eventsQueue = new ArrayBlockingQueue<CCEvent>(20);
		_toAddEvent = new ArrayBlockingQueue<CCEvent>(20);
		_pause = false;
		_toSwap = false;
		_blockNum = 1;
		CCScheduler.sharedScheduler().schedule(CCTimer.timer(this, "tick"));
	}

	public static void purgeSharedManager() {
		if(_shareManager != null){
			_shareManager.removeAllEvents();
			_shareManager._eventsQueue = null;
			_shareManager._toAddEvent = null;
			_shareManager = null;
			_pause = true;
		}
	}
	
	private static boolean _pause;
	public static void pause(){
		_pause = true;
	}
	public static void resume(){
		_pause = false;
	}	
	private ArrayBlockingQueue<CCEvent> _eventsQueue;
	private ArrayBlockingQueue<CCEvent> _toAddEvent;
	private boolean _toAdd;
	private boolean _isLocked;
	private boolean _toSwap;

	public synchronized void addEvent(CCEvent event) {
		if(_pause) return;
		if(!_isLocked){
			if(!_eventsQueue.offer(event)){
				++_blockNum;
				ArrayBlockingQueue<CCEvent> newQueue = new ArrayBlockingQueue<CCEvent>(BLOCK_CAPABILITY*_blockNum,false);
				_eventsQueue.drainTo(newQueue);
				_eventsQueue = newQueue;
				newQueue = new ArrayBlockingQueue<CCEvent>(BLOCK_CAPABILITY*_blockNum,false);
				_toAddEvent.drainTo(newQueue);
				_toAddEvent = newQueue;
			}
		}
		_toAddEvent.offer(event);
		if(!_toAddEvent.offer(event)){
			++_blockNum;
			ArrayBlockingQueue<CCEvent> newQueue = new ArrayBlockingQueue<CCEvent>(BLOCK_CAPABILITY*_blockNum,false);
			_toAddEvent.drainTo(newQueue);
			_toAddEvent = newQueue;
			_toSwap = true;
		}
		_toAdd = true;
	}

	public synchronized void removeAllEvents() {
		_eventsQueue.clear();
		_toAddEvent.clear();
	}
	
	protected synchronized void upDateEvents(){
		if(_toAdd){
			if(_toSwap){
				_eventsQueue = _toAddEvent;
				ArrayBlockingQueue<CCEvent> newQueue = new ArrayBlockingQueue<CCEvent>(BLOCK_CAPABILITY*_blockNum,false);
				_toAddEvent = newQueue;
				_toSwap = false;
				return;
			}
			Iterator<CCEvent> iter = _toAddEvent.iterator();
			while(iter.hasNext()){
				CCEvent event = iter.next();
				_eventsQueue.offer(event);
			}
			_toAddEvent.clear();
			_toAdd = false;
		}
	}
	
    public void tick(float dt) {
    	_isLocked = true;
    	upDateEvents();
    	CCEvent event;
    	event = _eventsQueue.poll();
    	while(event!=null){
    		dispatchEvent(event);
    		event = _eventsQueue.poll();
    	}
    	_isLocked = false;
    }

	private void dispatchEvent(CCEvent event) {
		switch(event.type){
		case CCEvent.CC_EVENT_TOUCHES_BEGAN:{
			CCTouchEvent tevent = (CCTouchEvent)event;
			CCTouchDispatcher.sharedDispatcher().touchesBegan(tevent);
			break;
		}
		case CCEvent.CC_EVENT_TOUCHES_MOVED:{
			CCTouchEvent tevent = (CCTouchEvent)event;
			CCTouchDispatcher.sharedDispatcher().touchesMoved(tevent);
			break;
		}
		case CCEvent.CC_EVENT_TOUCHES_ENDED:{
			CCTouchEvent tevent = (CCTouchEvent)event;
			CCTouchDispatcher.sharedDispatcher().touchesEnded(tevent);
			break;
		}
		case CCEvent.CC_EVENT_TOUCHES_CANCELLED:{
			CCTouchEvent tevent = (CCTouchEvent)event;
			CCTouchDispatcher.sharedDispatcher().touchesCancelled(tevent);
			break;
		}
		case CCEvent.CC_EVENT_ACCELEROMETER_ACCURACY_CHANGED:{
			CCAccelerometerEvent aevent = (CCAccelerometerEvent)event;
			CCAccelerometerDispatcher.sharedDispatcher().accelerometerAccuracyChanged(aevent.accuracy);
			break;
		}
		case CCEvent.CC_EVENT_ACCELEROMETER_CHANGED:
			CCAccelerometerEvent aevent = (CCAccelerometerEvent)event;
			CCAccelerometerDispatcher.sharedDispatcher().accelerometerChanged(aevent.accelx, aevent.accely, aevent.accelz);
			break;
		}
	}
}
