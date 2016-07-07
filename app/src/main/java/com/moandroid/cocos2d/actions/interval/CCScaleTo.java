package com.moandroid.cocos2d.actions.interval;

import com.moandroid.cocos2d.nodes.CCNode;

public class CCScaleTo extends CCIntervalAction {

	  	protected float _scaleX;
	    protected float _scaleY;
	    protected float _startScaleX;
	    protected float _startScaleY;
	    protected float _endScaleX;
	    protected float _endScaleY;
	    protected float _deltaX;
	    protected float _deltaY;

	    public static CCScaleTo action(float t, float s) {
	        return new CCScaleTo(t, s);
	    }

	    public static CCScaleTo action(float t, float sx, float sy) {
	        return new CCScaleTo(t, sx, sy);
	    }

	    protected CCScaleTo(float t, float s) {
	        this(t, s, s);
	    }

	    protected CCScaleTo(float t, float sx, float sy) {
	        super(t);
	        _endScaleX = sx;
	        _endScaleY = sy;
	    }

	    @Override
	    public CCScaleTo copy() {
	        return new CCScaleTo(_duration, _endScaleX, _endScaleY);
	    }

	    @Override
	    public void start(CCNode aTarget) {
	        super.start(aTarget);
	        _startScaleX = _target.scaleX();
	        _startScaleY = _target.scaleY();
	        _deltaX = _endScaleX - _startScaleX;
	        _deltaY = _endScaleY - _startScaleY;
	    }

	    @Override
	    public void update(float t) {
	        _target.setScaleX(_startScaleX + _deltaX * t);
	        _target.setScaleY(_startScaleY + _deltaY * t);
	    }

	@Override
	public CCIntervalAction reverse() {
		return CCScaleTo.action(_duration,1f);
	}
}
