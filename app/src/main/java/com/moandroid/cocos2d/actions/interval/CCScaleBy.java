package com.moandroid.cocos2d.actions.interval;

import com.moandroid.cocos2d.nodes.CCNode;

public class CCScaleBy extends CCScaleTo {

	   public static CCScaleBy action(float t, float s) {
	        return new CCScaleBy(t, s, s);
	    }

	    public static CCScaleBy action(float t, float sx, float sy) {
	        return new CCScaleBy(t, sx, sy);
	    }

	    protected CCScaleBy(float t, float s) {
	        super(t, s, s);
	    }

	    protected CCScaleBy(float t, float sx, float sy) {
	        super(t, sx, sy);
	    }


	    @Override
	    public CCScaleBy copy() {
	        return new CCScaleBy(_duration, 
	        					 _endScaleX, 
	        					 _endScaleY);
	    }

	    @Override
	    public void start(CCNode aTarget) {
	        super.start(aTarget);
	        _deltaX = _startScaleX * _endScaleX - _startScaleX;
	        _deltaY = _startScaleY * _endScaleY - _startScaleY;
	    }

	    @Override
	    public CCScaleBy reverse() {
	        return new CCScaleBy(_duration, 1 / _endScaleX, 1 / _endScaleY);
	    }
}
