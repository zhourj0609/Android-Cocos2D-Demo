package com.moandroid.tests;


import com.moandroid.cocos2d.nodes.CCLayer;
import com.moandroid.cocos2d.opengles.CCGLSurfaceView;
import com.moandroid.cocos2d.renderers.CCDirector;
import com.moandroid.cocos2d.renderers.CCDirector2D;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class AtlasTest extends Activity {
	public static final String LOG_TAG = AtlasTest.class.getSimpleName();
	
	private CCGLSurfaceView mGLSurfaceView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(	WindowManager.LayoutParams.FLAG_FULLSCREEN, 
								WindowManager.LayoutParams.FLAG_FULLSCREEN);
		mGLSurfaceView = new CCGLSurfaceView(this);
		mGLSurfaceView.setDirector(CCDirector2D.sharedDirector(this));
		setContentView(mGLSurfaceView);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
        CCDirector2D.sharedDirector().end();
	}

	@Override
	protected void onPause() {
		super.onPause();
		CCDirector.sharedDirector().pause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		CCDirector.sharedDirector().resume();
	}
	
	static int sceneIdx = -1;
	static Class<?> transitions[] = {
		Atlas1.class,
		LabelAtlasTest.class,
		LabelAtlasColorTest.class,
		Atlas3.class,
		Atlas4.class,
		Atlas5.class,
		Atlas6.class,
		AtlasBitmapColor.class,
		AtlasFastBitmap.class,
	};
	
	static CCLayer nextAction(){
		++sceneIdx;
		sceneIdx = sceneIdx % transitions.length;
		return restartAction();
	}
	
	static CCLayer backAction(){
		--sceneIdx;
		int total = transitions.length;
		if(sceneIdx < 0)
			sceneIdx += total;
		return restartAction();
	}
	
	static CCLayer restartAction(){
		try{
			Class<?> c = transitions[sceneIdx];
			return (CCLayer)c.newInstance();
		} catch (IllegalAccessException e){
			Log.d(LOG_TAG, "Create new instance for transitions[" + sceneIdx + "] IllegalAccessException error!");
			return null;
		} catch (InstantiationException e){
			Log.d(LOG_TAG, "Create new instance for transitions[" + sceneIdx + "] InstantiationException error!");
			return null;
		} catch (Exception e){
			Log.d(LOG_TAG, "Create new instance for transitions[" + sceneIdx + "] error!");
			return null;
		}
	}
	
	static class AtlasDemo extends CCLayer{
		public AtlasDemo(){
			/*
			CCSize s = CCDirector2D.sharedDirector().winSize();
			CCLabel label = CCLabel.label(title(), "Arial", 32);
			addChild(label, 1);
			label.setPosition(CCPoint.ccp(s.width/2, s.height -50));
			String subtitle = subtitle();
			if(subtitle != null){
				CCLabel l= CCLabel.label(subtitle, "Thonburi", 16);
				addChild(l, 1);
				l.setPosition(CCPoint.ccp(s.width/2, s.height - 80));
			}
			
			CCMenuItemImage item1 = CCMenuItemImage.item("b1.png", "b2.png", this, "backCallBack");
			CCMenuItemImage item2 = CCMenuItemImage.item("r1.png", "r2.png", this, "restartCallback");
			CCMenuItemImage item3 = CCMenuItemImage.item("f1.png", "f2.png", this, "nextCallback");
			
			CCMenu menu = CCMenu.menu(item1, item2, item3);
			menu.setPosition(CCPoint.zero());
			item1.setPosition(CCPoint.ccp(s.width/2 - 100, 30));
			item2.setPosition(CCPoint.ccp(s.width/2, 	   30));
			item3.setPosition(CCPoint.ccp(s.width/2 + 100, 30));
			
			addChild(menu, 1);*/
		}

		public String subtitle() {
			return null;
		}

		public String title() {
			return "No title";
		}
		
		protected void restartCallback(){
			// TODO Auto-generated method stub
		}
		
		protected void nextCallback(){
			// TODO Auto-generated method stub
		}
		
		protected void backCallback(){
			// TODO Auto-generated method stub
		}
	}
	
	static class Atlas1 extends AtlasDemo{
		
	}
	
	static class LabelAtlasTest extends AtlasDemo{
		
	}
	
	static class LabelAtlasColorTest extends AtlasDemo{
		
	}
	
	static class Atlas3 extends AtlasDemo{
		
	}
	
	static class Atlas4 extends AtlasDemo{
		
	}
	
	static class Atlas5 extends AtlasDemo{
		
	}
	
	static class Atlas6 extends AtlasDemo{
		
	}
	
	static class AtlasBitmapColor extends AtlasDemo{
		
	}
	
	static class AtlasFastBitmap extends AtlasDemo{
		
	}
}
