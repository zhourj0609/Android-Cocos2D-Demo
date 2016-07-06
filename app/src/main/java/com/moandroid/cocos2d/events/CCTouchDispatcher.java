package com.moandroid.cocos2d.events;

import java.util.ArrayList;

import android.view.MotionEvent;

public class CCTouchDispatcher {

	public static final String LOG_TAG = CCTouchDispatcher.class.getSimpleName();
	private static CCTouchDispatcher _sharedDispatcher;
	public static CCTouchDispatcher sharedDispatcher() {
		if(_sharedDispatcher == null)
			_sharedDispatcher = new CCTouchDispatcher();
		return _sharedDispatcher;
	}
	
	public CCTouchDispatcher(){
		_purged = false;
		set_dispatchEvents(true);
		_toAdd = false;
		_toRemove = false;
		_toQuit = false;
		_locked = false;
//		_targetedHandlers = new ArrayList<CCTouchHandler>(8);
		_standardHandlers = new ArrayList<CCTouchHandler>(4);
		_handlersToAdd = new ArrayList<CCTouchHandler>(8);
		_handlersToRemove = new ArrayList<CCTouchDelegate>(8);
	}
	
	private static boolean _purged;
	public static void purgeSharedDispatcher() {
		if(_sharedDispatcher != null){
			_sharedDispatcher.set_dispatchEvents(false);
			_sharedDispatcher._standardHandlers.clear();
			_sharedDispatcher._standardHandlers = null;
//			_sharedDispatcher._targetedHandlers.clear();
//			_sharedDispatcher._targetedHandlers = null;		
			_sharedDispatcher._handlersToAdd.clear();
			_sharedDispatcher._handlersToAdd = null;
			_sharedDispatcher._handlersToRemove.clear();
			_sharedDispatcher._handlersToRemove = null;			
			_sharedDispatcher = null;
			_purged = true;
		}
	}

	
	private boolean _dispatchEvents;
	public boolean _dispatchEvents(){
		return _dispatchEvents;
	}
	public void set_dispatchEvents(boolean b){
		_dispatchEvents = b;
	}

//	private ArrayList<CCTouchHandler> _targetedHandlers;
	private ArrayList<CCTouchHandler> _standardHandlers;
	private ArrayList<CCTouchHandler> _handlersToAdd;
	private ArrayList<CCTouchDelegate> _handlersToRemove;
	
	private boolean _toAdd;
	private boolean _toRemove;
	private boolean _toQuit;
	private boolean _locked;
		
	private void forceAddHandler(CCTouchHandler handler, ArrayList<CCTouchHandler> array){
		int i = 0;
		for(CCTouchHandler h : array){
			if(h.priority() < handler.priority())
				++i;
			if( h.delegate() == handler.delegate() )
                return;
		}
		array.add(i, handler);
	}

	public /*synchronized*/ void addDelegate(CCStandardTouchDelegate delegate, int priority){
		CCTouchHandler handler = CCStandardTouchHandler.handler(delegate, priority);
		if(!_locked){
			forceAddHandler(handler, _standardHandlers);
		}else{
			_handlersToAdd.add(handler);
			_toAdd = true;
		}
	}
	
//	public void addDeleget(CCTargetedTouchDelegate delegate, int priority){
//		CCTouchHandler handler = CCTargetedTouchHandler.handler(delegate, priority);
//		if(!_locked){
//			forceAddHandler(handler, _targetedHandlers);
//		}else{
//			_handlersToAdd.add(handler);
//			_toAdd = true;
//		}
//	}	
	
	private void forceRemoveDelegate(CCTouchDelegate delegate){
//		for(CCTouchHandler handler : _targetedHandlers){
//			if(handler.delegate() == delegate){
//				_targetedHandlers.remove(handler);
//				break;
//			}
//		}
		for(CCTouchHandler handler : _standardHandlers){
			if(handler.delegate() == delegate){
				_standardHandlers.remove(handler);
				break;
			}
		}
	}

	public /*synchronized*/ void removeDelegate(CCTouchDelegate delegate){
		if(delegate == null)
			return;
		if(!_locked){
			forceRemoveDelegate(delegate);
		}else{
			_handlersToRemove.add(delegate);
			_toRemove = true;
		}
	}
	
	private void forceRemoveAllDelegates(){
		_standardHandlers.clear();
//		_targetedHandlers.clear();
	}
	
	public /*synchronized*/ void removeAllDelegates(){
		if(!_locked)
			forceRemoveAllDelegates();
		else
			_toQuit = true;
	}
	
//	public void setPriority(int priority, CCTouchDelegate delegate){
//		
//	}
	
	private /*synchronized*/ void updateHandlers(){
		if(_toRemove){
			_toRemove = false;
			for(CCTouchDelegate delegate : _handlersToRemove){
				forceRemoveDelegate(delegate);
			}
			_handlersToRemove.clear();
		}
		if(_toAdd){
			_toAdd = false;
			for(CCTouchHandler handler : _handlersToAdd){
//				if(handler instanceof CCTargetedTouchHandler){
//					forceAddHandler(handler, _targetedHandlers);
//				}else if(handler instanceof CCStandardTouchHandler){
					forceAddHandler(handler, _standardHandlers);
//				}
			}
			_handlersToAdd.clear();
		}
		if(_toQuit){
			_toQuit = false;
			forceRemoveAllDelegates();
		}	
	}
	
	public void touchesBegan(MotionEvent event) {
		CCTouchEvent tevent = new CCTouchEvent();
		tevent.type = CCEvent.CC_EVENT_TOUCHES_BEGAN;
		tevent.x = event.getX();
		tevent.y = event.getY();
		CCEventManager.sharedManager().addEvent(tevent);
	}
	
	public void touchesMoved(MotionEvent event) {
		CCTouchEvent tevent = new CCTouchEvent();
		tevent.type = CCEvent.CC_EVENT_TOUCHES_MOVED;
		tevent.x = event.getX();
		tevent.y = event.getY();
		CCEventManager.sharedManager().addEvent(tevent);
	}

	public void touchesEnded(MotionEvent event) {
		CCTouchEvent tevent = new CCTouchEvent();
		tevent.type = CCEvent.CC_EVENT_TOUCHES_ENDED;
		tevent.x = event.getX();
		tevent.y = event.getY();
		CCEventManager.sharedManager().addEvent(tevent);
	}
	
	public void touchesCancelled(MotionEvent event) {
		CCTouchEvent tevent = new CCTouchEvent();
		tevent.type = CCEvent.CC_EVENT_TOUCHES_CANCELLED;
		tevent.x = event.getX();
		tevent.y = event.getY();
		CCEventManager.sharedManager().addEvent(tevent);
	}

	public void touchesBegan(CCEvent event) {
		if(_purged)return;
		_locked = true;
		updateHandlers();
		if(_dispatchEvents){
//            for( CCTouchHandler handler : _targetedHandlers ) {
//                if( handler.delegate().touchesBegan(event))
//                    break;
//            }
            
            for( CCTouchHandler handler : _standardHandlers){
                if( handler.delegate().touchesBegan(event))
                    break;
            }
		}
		_locked = false;
		
	}

	public void touchesMoved(CCEvent event) {
		if(_purged)return;
		_locked = true;
		updateHandlers();
		if(_dispatchEvents){
//            for( CCTouchHandler handler : _targetedHandlers ) {
//                if( handler.delegate().touchesMoved(event))
//                    break;
//            }
            
            for( CCTouchHandler handler : _standardHandlers){
                if( handler.delegate().touchesMoved(event))
                    break;
            }
		}
		_locked = false;
	}

	public void touchesEnded(CCEvent event) {
		if(_purged)return;
		_locked = true;
		updateHandlers();
		if(_dispatchEvents){
//            for( CCTouchHandler handler : _targetedHandlers ) {
//                if( handler.delegate().touchesEnded(event))
//                    break;
//            }
            
            for( CCTouchHandler handler : _standardHandlers){
                if( handler.delegate().touchesEnded(event))
                    break;
            }
		}	

		_locked = false;	
	}

	public void touchesCancelled(CCEvent event) {
		if(_purged)return;
		_locked = true;
		updateHandlers();
		if(_dispatchEvents){
//            for( CCTouchHandler handler : _targetedHandlers ) {
//                if( handler.delegate().touchesCancelled(event))
//                    break;
//            }
            
            for( CCTouchHandler handler : _standardHandlers){
                if( handler.delegate().touchesCancelled(event))
                    break;
            }
		}	

		_locked = false;
		
	}
}
