package com.moandroid.cocos2d.renderers;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.moandroid.cocos2d.util.CCConfig;

public class CCDirector2D extends CCDirector {
		
	public CCDirector2D(Context context) {
		super(context);
	}

	public static CCDirector2D sharedDirector(Context context) {
        if (CCDirector._sharedDirector == null) {
        	CCDirector._sharedDirector = new CCDirector2D(context);
        }
        return (CCDirector2D) CCDirector._sharedDirector;
	}
	
	@Override
	public void  beginRender(GL10 gl){
		//执行清屏
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
	}

	@Override
	public void  endRender(GL10 gl){
		
	}
	
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		super.onSurfaceChanged(gl, width, height);
		setProjection(gl, kCCDirectorProjection2D);
	}
	
	protected void initGLDefaultValues(GL10 gl){
		setAlphaBlending(gl, true);
		setDepthTest(gl, false);
		if(CCConfig.CC_DIRECTOR_BACKGROUND_TRANSLUCENT){
			gl.glClearColor(0.0f,0.0f,0.0f,0.0f);
		}else {
			gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		}
	}
}
