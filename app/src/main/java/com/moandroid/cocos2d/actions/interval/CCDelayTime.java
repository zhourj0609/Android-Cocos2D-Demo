package com.moandroid.cocos2d.actions.interval;

public class CCDelayTime extends CCIntervalAction {
	
    public static CCDelayTime action(float t) {
        return new CCDelayTime(t);
    }

    protected CCDelayTime(float t) {
        super(t);
    }

    @Override
    public void update(float t) {
    }

    @Override
    public CCDelayTime reverse() {
        return new CCDelayTime(_duration);
    }
}
