package com.moandroid.cocos2d.nodes.scenes;

import javax.microedition.khronos.opengles.GL10;

import com.moandroid.cocos2d.nodes.CCGridBase;
import com.moandroid.cocos2d.nodes.CCNode;


public class CCScene extends CCNode{

	public static final String LOG_TAG = CCScene.class.getSimpleName();
	
	public static CCScene node() {
		return new CCScene();
	}
	
	private CCGridBase _grid;
	public CCGridBase getGrid(){
		return _grid;
	}
	public void setGrid(CCGridBase g){
		_grid = g;
	}
	
	 public void visit(GL10 gl){
    	if(_visible == false )//|| _cleanup == true)
    		return;
    	gl.glPushMatrix();
    	transform(gl);
    	if(_children != null){
        	for(CCNode c : _children){
        		if(c.zOrder()<0)
        			c.visit(gl);
        		else
        			break;
        	}	
    	}
    	draw(gl);
    	if(_children != null){
        	for(CCNode c : _children){
        		if(c.zOrder()>=0)
        			c.visit(gl);
        		else
        			break;
        	}	
    	} 		
    	gl.glPopMatrix();
    }   

}
