package com.moandroid.cocos2d.actions.interval;

import com.moandroid.cocos2d.nodes.CCNode;

public class CCRotateBy extends CCIntervalAction {

	  private float _angle;
	    private float _start_angle;

	    public static CCRotateBy action(float t, float a) {
	        return new CCRotateBy(t, a);
	    }

	    protected CCRotateBy(float t, float a) {
	        super(t);
	        _angle = a;
	    }

	    @Override
	    public CCRotateBy copy() {
	        return new CCRotateBy(_duration, _angle);
	    }


	    @Override
	    public void start(CCNode aTarget) {
	        super.start(aTarget);
	        _start_angle = _target.rotation();
	    }

	    @Override
	    public void update(float t) {
	        _target.setRotation(_start_angle + _angle * t);
	    }

	    @Override
	    public CCRotateBy reverse() {
	        return new CCRotateBy(_duration, -_angle);
	    }

}
