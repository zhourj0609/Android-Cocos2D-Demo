package com.moandroid.cocos2d.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import junit.framework.Assert;

import com.moandroid.cocos2d.nodes.CCNode;
import com.moandroid.cocos2d.renderers.CCScheduler;

public class CCActionManager {
	public static final String LOG_TAG = CCActionManager.class.getSimpleName();

	private static CCActionManager _shareManager;
	public static CCActionManager sharedManager() {
		if(_shareManager == null)
			_shareManager = new CCActionManager();
		return _shareManager;
	}

	public CCActionManager(){
//		_purged = false;
		actionsMap = new HashMap<CCNode, ArrayList<CCAction>>(131);
//		_discardedList = new ArrayList<CCNode>(8);
		CCScheduler.sharedScheduler().schedule(CCTimer.timer(this, "tick"));
	}
	
//	private static boolean _purged;
	public static void purgeSharedManager() {
		if(_shareManager != null){
			_shareManager.removeAllActions();
			_shareManager.actionsMap.clear();
			_shareManager.actionsMap = null;
//			_shareManager._discardedList.clear();
//			_shareManager._discardedList = null;
			_shareManager = null;
//			_purged = true;
		}
	}
	
	private HashMap<CCNode, ArrayList<CCAction>> actionsMap;

	public /*synchronized*/ void addAction(CCAction action, CCNode target) {
		Assert.assertTrue("Argument action must be non-null", action != null);
		Assert.assertTrue("Argument target must be non-null", target != null);
		ArrayList<CCAction> actionList;
		actionList = actionsMap.get(target);
		if(actionList == null){
			actionList = new ArrayList<CCAction>(4);
			actionsMap.put(target, actionList);
		}
		Assert.assertTrue("runAction: Action already running",!actionList.contains(action));
		actionList.add(action);
		action.start(target);
	}

	public void resumeAllActions(CCNode node) {
		node.setIsRunning(true);
	}

	public void pauseAllActions(CCNode node) {
		node.setIsRunning(false);
	}

	public /*synchronized*/ void removeAllActions(){
		Collection<ArrayList<CCAction>> values = actionsMap.values();
		for(ArrayList<CCAction> list : values){
			list.clear();
		}
		actionsMap.clear();
	}
	
	public /*synchronized*/ void removeAllActions(CCNode target) {
		if(actionsMap.containsKey(target)){
			actionsMap.remove(target);
		}
	}
	
	public /*synchronized*/ void removeAction(CCAction action) {
		CCNode target = action.originalTarget();
		if(actionsMap.containsKey(target)){
			ArrayList<CCAction> actionList = actionsMap.get(target);
			actionList.remove(action);
		}
	}
	
	public /*synchronized*/ void removeAction(int tag, CCNode target) {
		Assert.assertTrue("Invalid tag", tag != CCAction.INVALID_TAG);
		if(actionsMap.containsKey(target)){
			ArrayList<CCAction> actionList = actionsMap.get(target);
			for(CCAction action : actionList){
				if(action.tag() == tag){
					actionList.remove(action);
				}
			}
		}	
	}	
	
	public CCAction getAction(int tag, CCNode target) {
		Assert.assertTrue("Invalid tag", tag != CCAction.INVALID_TAG);
		if(actionsMap.containsKey(target)){
			ArrayList<CCAction> actionList = actionsMap.get(target);
			for(CCAction action : actionList){
				if(action.tag() == tag){
					return action;
				}
			}
		}
		return null;
	}

	public int numberOfRunningActions(CCNode target) {
		if(actionsMap.containsKey(target)){
			ArrayList<CCAction> actionList = actionsMap.get(target);
			return actionList.size();
		}
		return 0;
	}
	
//	private ArrayList<CCNode> _discardedList;
	
    public /*synchronized*/ void tick(float dt) {
//    	if(_purged) return;
    	Iterator<Entry<CCNode,ArrayList<CCAction>>> it = actionsMap.entrySet().iterator();
    	Map.Entry<CCNode,ArrayList<CCAction>> entry = null;

    	if(it.hasNext())
			entry = it.next();
		while(entry != null){
			CCNode key = entry.getKey();
			if(key.isRunning()){
				ArrayList<CCAction> list = entry.getValue();
//				for(int i = 0; i<list.size(); ++i) {
//					CCAction action = list.get(i);
//					action.step(dt);
//					if(action.isDone()){
//						action.stop();
//						list.remove(i);
//						--i;
//					}
//				}
				for (int i=list.size()-1; i >=0; i--){
					CCAction action = list.get(i);
					action.step(dt);
					if(action.isDone()){
						action.stop();
						list.remove(i);
					}
				}
				if(it.hasNext())
					entry = it.next();
				else
					entry = null;
				if(list.isEmpty()){
					actionsMap.remove(key);
					//_discardedList.add(key);
				}
			}
		}
//		for(CCNode node : _discardedList){
//			actionsMap.remove(node);
//		}
//		_discardedList.clear();
    }
}
