package com.moandroid.cocos2d.nodes.scenes;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import junit.framework.Assert;

import com.moandroid.cocos2d.renderers.CCDirector;

public class CCSceneManager {
	public static final String LOG_TAG = CCSceneManager.class.getSimpleName();

	private static CCSceneManager _shareManager;
	public static CCSceneManager shareManager(){
		if(_shareManager == null){
			_shareManager = new CCSceneManager();
		}
		return _shareManager;
	}
	
//	private static boolean _purged;
	public static void purgeSharedSceneManager() {
		if(_shareManager != null){
			_shareManager.endAllScene();
			_shareManager._scenesStack = null;
			_shareManager._runningScene = null;
			_shareManager._nextScene = null;
			_shareManager = null;
//			_purged = true;
		}
	}
	
	private ArrayList<CCScene> _scenesStack;
	protected CCSceneManager(){
//		synchronized (CCSceneManager.class){
			_sendCleanupToScene = false;
//			_purged = false;
			_scenesStack = new ArrayList<CCScene>(10);
//		}	
	}
	
	private CCScene _runningScene;
	public CCScene runningScene(){
		return _runningScene;
	}
	
	private CCScene _nextScene;

	private boolean _sendCleanupToScene;

	public CCScene nextScene(){
		return _nextScene;
	}
		
	public void runWithScene(CCScene scene){
		_scenesStack.clear();
		pushScene(scene);
		_nextScene = scene;
	}
	
	public void pushScene(CCScene scene){
		Assert.assertTrue("Scene must be non-nil", scene != null);
		_scenesStack.add(scene);
		_nextScene = scene;
		_sendCleanupToScene = false;
	}
	
	public void popScene(CCScene scene){
		Assert.assertTrue("A running scene is needed",  _runningScene != null);
		int index = _scenesStack.size();
		if(index > 1){
			_scenesStack.remove(--index);
			_sendCleanupToScene = true;
			_nextScene = _scenesStack.get(index-1);
		}else{
			CCDirector.sharedDirector().onSceneIsEmpty();
		}
	}

	public void replaceScene(CCScene scene, boolean cleanup){
		Assert.assertTrue("Argument must be non-nil",  scene != null);
		int index = _scenesStack.size();
		_scenesStack.set(index - 1, scene);
		_nextScene = scene;
		_sendCleanupToScene = cleanup;
	}
	
	public void replaceScene(CCScene scene){
		replaceScene(scene, true);
	}
	
	public void runNextScene(){
//		if(_purged) return;
		if(_nextScene != null){
			if(_runningScene != null){
				_runningScene.onExit();	
			}
			if(_sendCleanupToScene){
				_runningScene.cleanup();
			}
			_runningScene = _nextScene;
			_nextScene = null;
			_runningScene.onEnter();		
		}
	}

	public void endAllScene() {
		if(_runningScene != null){
			_runningScene.cleanup();
			_runningScene = null;			
		}
		if(_nextScene != null){
			_nextScene.cleanup();
			_nextScene = null;				
		}
		for(int i=_scenesStack.size()-2; i >= 0; --i){
			_scenesStack.get(i).cleanup();
		}
		_scenesStack.clear();
	}

	public void visitScene(GL10 gl) {
//		if(_purged) return;
		if(_runningScene!=null)
			_runningScene.visit(gl);
	}
}
