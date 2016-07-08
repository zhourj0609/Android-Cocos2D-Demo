package com.moandroid.cocos2d.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import junit.framework.Assert;

import com.moandroid.cocos2d.nodes.CCNode;
import com.moandroid.cocos2d.renderers.CCScheduler;

public class CCActionManager {

    public static final String LOG_TAG = CCActionManager.class.getSimpleName();

    private static CCActionManager _shareManager;
    private HashMap<CCNode, ArrayList<CCAction>> actionsMap;
    private HashMap<CCNode,Boolean> reqRemoveNodeList;
    private HashMap<CCNode,ArrayList<CCAction>>  reqRemoveNodePartAction;

    private HashMap<CCNode,ArrayList<CCAction>> reqAddAction;

    private final Object LOCK = new Object();// 对象锁

    public static CCActionManager sharedManager() {
        if(_shareManager == null)
            _shareManager = new CCActionManager();
        return _shareManager;
    }

    public CCActionManager(){
        actionsMap = new HashMap<CCNode, ArrayList<CCAction>>(131);
        reqRemoveNodePartAction = new HashMap<CCNode,ArrayList<CCAction>>(10);
        reqRemoveNodeList = new HashMap<CCNode,Boolean>(10);
        reqAddAction = new HashMap<CCNode,ArrayList<CCAction>>(10);
        CCScheduler.sharedScheduler().schedule(CCTimer.timer(this, "tick"));
    }

    //	private static boolean _purged;
    public static void purgeSharedManager() {
        if(_shareManager != null){
            _shareManager.removeAllActions();
            _shareManager.actionsMap.clear();
            _shareManager.actionsMap = null;
            _shareManager.reqRemoveNodePartAction = null;
            _shareManager.reqAddAction = null;
            _shareManager.reqRemoveNodePartAction = null;
            _shareManager = null;
        }
    }

    public /*synchronized*/ void addAction(CCAction action, CCNode target) {
        Assert.assertTrue("Argument action must be non-null", action != null);
        Assert.assertTrue("Argument target must be non-null", target != null);
        synchronized (LOCK) {
            ArrayList<CCAction> actionList;
            actionList = reqAddAction.get(target);
            if (actionList == null) {
                actionList = new ArrayList<CCAction>(4);
                actionsMap.put(target, actionList);
            }
            Assert.assertTrue("runAction: Action already running", !actionList.contains(action));
            actionList.add(action);
            action.start(target);
        }
    }

    public void resumeAllActions(CCNode node) {
        node.setIsRunning(true);
    }

    public void pauseAllActions(CCNode node) {
        node.setIsRunning(false);
    }

    public void removeAllActions() {
        synchronized (LOCK) {
            Collection<ArrayList<CCAction>> values = actionsMap.values();
            for (ArrayList<CCAction> list : values) {
                list.clear();
            }
            actionsMap.clear();
            clearAllRequestAction();
        }
    }

    public void clearAllRequestAction(){
        Collection<ArrayList<CCAction>> reqVluseMap = reqRemoveNodePartAction.values();
        for(ArrayList<CCAction> list : reqVluseMap){
            list.clear();
        }
        reqVluseMap.clear();
        reqAddAction.clear();
        reqRemoveNodeList.clear();
    }

    public void removeAllActions(CCNode target) {
        reqRemoveNodeList.put(target,true);
    }

    public void removeAction(CCAction action) {
        CCNode target = action.originalTarget();
        ArrayList<CCAction> actionList = reqRemoveNodePartAction.get(target);
        if(actionList == null){
            actionList = new ArrayList<CCAction>(4);
            actionsMap.put(target, actionList);
        }
        actionList.add(action);
    }

    public void removeAction(int tag, CCNode target) {
        Assert.assertTrue("Invalid tag", tag != CCAction.INVALID_TAG);
        synchronized (LOCK) {
            if (!actionsMap.containsKey(target)) return;
            ArrayList<CCAction> actionList = actionsMap.get(target);
            for (CCAction action : actionList) {
                if (action.tag() == tag) {
                    removeAction(action);
                    break;
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


    public void tick(float dt) {
        synchronized(LOCK) {
            synchronizedRemoveAction();
            synchronizedAddAction();
            clearAllRequestAction();
            Iterator<Entry<CCNode, ArrayList<CCAction>>> it = actionsMap.entrySet().iterator();
            Map.Entry<CCNode, ArrayList<CCAction>> entry = null;
            while (it.hasNext()){
                entry = it.next();
                CCNode key = entry.getKey();
                if(!key.isRunning()) continue;
                ArrayList<CCAction> list = entry.getValue();
                for(int i=0; i<list.size();i++){
                    CCAction action = list.get(i);action.step(dt);
                    if(action.isDone()){
                        action.stop();
                        list.remove(i);
                        i--;
                    }
                }
                if(list.isEmpty()){
                    it.remove();
                }
            }
        }
    }

    private void synchronizedRemoveAction(){
        Iterator<Entry<CCNode,Boolean>> it = reqRemoveNodeList.entrySet().iterator();
        HashMap.Entry<CCNode,Boolean> entry;
        while (it.hasNext()){
            entry = it.next();
            CCNode key = entry.getKey();
            if(actionsMap.containsKey(key)){
                actionsMap.remove(key);
            }
        }
        Iterator<Entry<CCNode,ArrayList<CCAction>>> it2 = reqRemoveNodePartAction.entrySet().iterator();
        HashMap.Entry<CCNode,ArrayList<CCAction>> entry2;
        while (it2.hasNext()){
            entry2 = it2.next();
            CCNode key = entry2.getKey();
            ArrayList<CCAction> removeValues = entry2.getValue();
            if(actionsMap.containsKey(key)){
                ArrayList<CCAction> orginValue = actionsMap.get(key);
                for(int i=0; i<removeValues.size();i++){
                    for(int j= orginValue.size()-1;j>=0; j--){
                        if(removeValues.get(i).equals(orginValue.get(j))){
                            orginValue.remove(j);
                        }
                    }
                }
            }
        }
    }

    private void synchronizedAddAction(){
        Iterator<Entry<CCNode,ArrayList<CCAction>>> it = reqAddAction.entrySet().iterator();
        Entry<CCNode,ArrayList<CCAction>> entry;
        while (it.hasNext()){
            entry = it.next();
            CCNode key = entry.getKey();
            ArrayList<CCAction> orginValue;
            if(actionsMap.containsKey(key)){
                orginValue = actionsMap.get(key);
                orginValue.addAll(entry.getValue());
            }else {
                orginValue = new ArrayList<CCAction>();
                orginValue.addAll(entry.getValue());
            }
        }
    }
}
