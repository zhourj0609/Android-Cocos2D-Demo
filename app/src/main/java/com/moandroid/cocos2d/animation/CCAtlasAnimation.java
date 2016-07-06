package com.moandroid.cocos2d.animation;

import java.util.ArrayList;
import java.util.Arrays;

import com.moandroid.cocos2d.nodes.sprite.CCAtlasSpriteFrame;
import com.moandroid.cocos2d.types.CCRect;

public class CCAtlasAnimation implements CCAnimationProtocol {
	   String _name;
	    float _delay;
	    ArrayList<Object> _frames;


	    public ArrayList<Object> frames() {
	        return _frames;
	    }

	    public String name() {
	        return _name;
	    }

	    public float delay() {
	        return _delay;
	    }

	    public CCAtlasAnimation(String n, float d) {
	        this(n, d, new CCAtlasSpriteFrame[]{});
	    }


	    /* initializes an CCAtlasAnimation with an AtlasSpriteManager, a _name, and the _frames from AtlasSpriteFrames */
	    public CCAtlasAnimation(String t, float d, CCAtlasSpriteFrame... f) {
	        _name = t;
	        _frames = new ArrayList<Object>();
	        _delay = d;

	        _frames.addAll(Arrays.asList(f));
	    }

	    public void addFrame(CCRect rect) {
	        CCAtlasSpriteFrame frame = new CCAtlasSpriteFrame(rect);
	        _frames.add(frame);
	    }

		@Override
		public void cleanup() {
			// TODO Auto-generated method stub
			
		}

}
