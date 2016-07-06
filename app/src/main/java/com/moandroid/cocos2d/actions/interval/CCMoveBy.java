package com.moandroid.cocos2d.actions.interval;

import com.moandroid.cocos2d.nodes.CCNode;

public class CCMoveBy extends CCMoveTo {

	   public static CCMoveBy action(float t, float x, float y) {
	        return new CCMoveBy(t, x, y);
	    }

	    protected CCMoveBy(float t, float x, float y) {
	        super(t, x, y);
	        _deltaX = x;
	        _deltaY = y;
	    }

	    @Override
	    public CCMoveBy copy() {
	        return new CCMoveBy(_duration,_deltaX,_deltaY);
	    }

	    @Override
	    public void start(CCNode aTarget) {
	        float savedX = _deltaX;
	        float savedY = _deltaY;
	        super.start(aTarget);
	        _deltaX = savedX;
	        _deltaY = savedY;
	    }

	    @Override
	    public CCMoveBy reverse() {
	        return new CCMoveBy(_duration, -_deltaX, -_deltaY);
	    }
}
