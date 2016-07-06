package com.moandroid.cocos2d.opengles;

import junit.framework.Assert;

import com.moandroid.cocos2d.events.CCTouchDispatcher;
import com.moandroid.cocos2d.events.CCTouchInputProtocol;
import com.moandroid.cocos2d.renderers.CCDirector;
import com.moandroid.cocos2d.util.CCConfig;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class CCGLSurfaceView extends GLSurfaceView 
							implements CCTouchInputProtocol {

	protected static final String LOG_TAG = CCGLSurfaceView.class.getSimpleName();

	public CCGLSurfaceView(Context context){
		super(context);
		if(CCConfig.CC_SURFACE_USE_GL_DEBUG_WRAPPER){
			setGLWrapper(new CCGLDebugWrapper());
			//setDebugFlags(DEBUG_LOG_GL_CALLS |  DEBUG_CHECK_GL_ERROR);
		}
		if(CCConfig.CC_DIRECTOR_BACKGROUND_TRANSLUCENT){//背景透明
			this.setZOrderOnTop(true);
			this.setEGLConfigChooser(8,8,8,8,16,0);
			this.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		}
		//setFocusable(true);
		//setFocusableInTouchMode(true);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Assert.assertTrue("You must set director first!", mDirector!=null);
		mDirector.onSurfaceDestroyed();
		super.surfaceDestroyed(holder);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(mDispatcher == null)
			return false;
		switch(event.getAction()){
		case MotionEvent.ACTION_CANCEL:
			mDispatcher.touchesCancelled(event);
			break;
        case MotionEvent.ACTION_DOWN:
            mDispatcher.touchesBegan(event);
            break;
        case MotionEvent.ACTION_MOVE:
            mDispatcher.touchesMoved(event);
            break;
        case MotionEvent.ACTION_UP:
            mDispatcher.touchesEnded(event);
            break;
		}
		return true;
	}

	private CCDirector mDirector;
	public void setDirector(CCDirector director) {
		mDirector = director;
		director.setTouchInput(this);
		director.setGLView(this);
		super.setRenderer(director);
	}
	
	private CCTouchDispatcher mDispatcher;
	@Override
	public void setTouchDispatcher(CCTouchDispatcher dispatcher){
		mDispatcher = dispatcher;
	}



}
