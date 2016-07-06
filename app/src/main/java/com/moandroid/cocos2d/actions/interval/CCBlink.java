package com.moandroid.cocos2d.actions.interval;

public class CCBlink extends CCIntervalAction {

	   private int _times;

	    public static CCBlink action(float t, int times) {
	        return new CCBlink(t, times);
	    }

	    protected CCBlink(float t, int times) {
	        super(t);
	        _times = times;
	    }

	    @Override
	    public CCBlink copy() {
	        return new CCBlink(_duration, _times);
	    }

	    @Override
	    public void update(float t) {
	        float slice = 1.0f / _times;
	        float m = t % slice;
	        _target.setVisible(m > slice / 2 ? true : false);
	    }

	    @Override
	    public CCBlink reverse() {
	        return new CCBlink(_duration, _times);
	    }

}
