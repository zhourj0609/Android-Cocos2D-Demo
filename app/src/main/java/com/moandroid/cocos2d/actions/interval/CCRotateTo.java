package com.moandroid.cocos2d.actions.interval;

import com.moandroid.cocos2d.nodes.CCNode;

public class CCRotateTo extends CCIntervalAction {

    private float _angle;
    private float _start_angle;


    public static CCRotateTo action(float t, float a) {
        return new CCRotateTo(t, a);
    }

    protected CCRotateTo(float t, float a) {
        super(t);
        _angle = a;
    }

    @Override
    public CCRotateTo copy() {
        return new CCRotateTo(_duration, _angle);
    }

    @Override
    public void start(CCNode aTarget) {
        super.start(aTarget);
        _start_angle = _target.rotation();
        if (_start_angle > 0)
            _start_angle = (float) (_start_angle % 360.0f);
        else
            _start_angle = (float) (_start_angle % -360.0f);

        _angle -= _start_angle;
        if (_angle > 180)
            _angle = -360 + _angle;
        if (_angle < -180)
            _angle = 360 + _angle;
    }

    @Override
    public void update(float t) {
        _target.setRotation(_start_angle + _angle * t);
    }

}
