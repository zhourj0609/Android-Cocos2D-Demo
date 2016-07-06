package com.moandroid.cocos2d.renderers;

import java.util.ArrayList;

import com.moandroid.cocos2d.actions.CCTimer;


public class CCScheduler {
    private static CCScheduler _sharedScheduler = null;

    public static CCScheduler sharedScheduler() {
            if (_sharedScheduler == null) {
            	_sharedScheduler = new CCScheduler();
            }
            return _sharedScheduler;
    }

//    private static boolean _purged;
	public static void purgeSharedScheduler() {
		if(_sharedScheduler != null){
			_sharedScheduler.removeAllSchedul();
			_sharedScheduler = null;
//			_purged = true;
		}
	}
	
    protected CCScheduler() {
//    	synchronized(CCScheduler.class){
//    		_purged = false;
    		_scheduledMethods = new ArrayList<CCTimer>(50);
    	    _methodsToRemove = new ArrayList<CCTimer>(20);
    	    _methodsToAdd = new ArrayList<CCTimer>(20);
    	    _timeScale = 1.0f;
//    	}
    }
    
    protected void removeAllSchedul(){
    	_scheduledMethods.clear();
    	_methodsToRemove.clear();
    	_methodsToAdd.clear();
    }
    
   	ArrayList<CCTimer> _scheduledMethods;
    ArrayList<CCTimer> _methodsToRemove;
    ArrayList<CCTimer> _methodsToAdd;

    private float _timeScale;

    public float timeScale() {
        return _timeScale;
    }

    public void setTimeScale(float ts) {
        _timeScale = ts;
    }

    public void schedule(CCTimer t) {
        if (_methodsToRemove.contains(t)) {
            _methodsToRemove.remove(t);
            return;
        }

        if (_scheduledMethods.contains(t) || _methodsToAdd.contains(t)) {
            return;
        }

        _methodsToAdd.add(t);
    }

    public void unschedule(CCTimer t) {
    	if (_methodsToAdd.contains(t)) {
            _methodsToAdd.remove(t);
            return;
        }

        if (!_scheduledMethods.contains(t) || _methodsToRemove.contains(t)) {
        	return;
        }

	    _methodsToRemove.add(t);
	 }

    public void tick(float dt) {
//    	if(_purged) return;
        if (_timeScale != 1.0f)
	            dt *= _timeScale;

        for (CCTimer k : _methodsToRemove)
            _scheduledMethods.remove(k);
        _methodsToRemove.clear();

        for (CCTimer k : _methodsToAdd)
            _scheduledMethods.add(k);
        _methodsToAdd.clear();

        for (CCTimer t : _scheduledMethods) {
            t.fire(dt);
        }
    }
}
