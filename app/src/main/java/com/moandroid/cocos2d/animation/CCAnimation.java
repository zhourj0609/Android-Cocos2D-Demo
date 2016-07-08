package com.moandroid.cocos2d.animation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import com.moandroid.cocos2d.texture.CCTexture2D;
import com.moandroid.cocos2d.texture.CCTextureCache;

import android.graphics.Bitmap;

public class CCAnimation implements CCAnimationProtocol {

	public static CCAnimation animation(String string, float delay) {
		return new CCAnimation(string,delay);
	}

	public static CCAnimation animation(String string, float delay, String...filenames) {
		return new CCAnimation(string,delay,filenames);
	}
	
	private String _name;
	private float _delay;
	ArrayList<Object> _frames;

	public String name() {
	    return _name;
	}
	
	public float delay() {
	    return _delay;
	}
	
	public ArrayList<Object> frames() {
	    return _frames;
	}
	
	protected CCAnimation(String n, float delay) {
	    this(n, delay, new CCTexture2D[]{});
	}

	protected CCAnimation(String n, float d, String... filenames) {
	    _name = n;
	    _frames = new ArrayList<Object>();
	    _delay = d;
	
	    if (filenames != null) {
	        for (String filename : filenames) {
	            CCTexture2D tex = CCTextureCache.sharedTextureCache().getTexture(filename);
	            _frames.add(tex);
	            CCTextureCache.sharedTextureCache().retain(tex);
	        }
	    }
	}
	
	public void addFrame(String filename) {
		CCTexture2D tex = CCTextureCache.sharedTextureCache().getTexture(filename);
	    _frames.add(tex);
	}

	public CCAnimation(String n, float d, Bitmap... images) {
	    _name = n;
	    _frames = new ArrayList<Object>();
	    _delay = d;
	    if (images != null) {
	        for (Bitmap bitmap : images) {
	        	CCTexture2D tex = CCTextureCache.sharedTextureCache().getTexture(bitmap);
	            _frames.add(tex);
	        }
	    }
	}
	
	public void addFrame(Bitmap bitmap) {
		CCTexture2D tex = CCTextureCache.sharedTextureCache().getTexture(bitmap);
	    _frames.add(tex);
	}
	
	public CCAnimation(String n, float d, CCTexture2D... textures) {
	    _name = n;
	    _frames = new ArrayList<Object>();
	    _delay = d;
	
	    if (textures != null) {
	        _frames.addAll(Arrays.asList(textures));
	    }
	}
	
	public void addFrame(CCTexture2D tex) {
	    _frames.add(tex);
	}
	
	public void cleanup(){
		Iterator<Object> iter = _frames.iterator();
		CCTexture2D next;
		while(iter.hasNext()){
			next = (CCTexture2D) iter.next();
			CCTextureCache.sharedTextureCache().release(next);
		}
		_frames.clear();
	}
}
