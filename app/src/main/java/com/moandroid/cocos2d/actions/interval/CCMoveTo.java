package com.moandroid.cocos2d.actions.interval;

import com.moandroid.cocos2d.nodes.CCNode;

public class CCMoveTo extends CCIntervalAction {

    private float _endPositionX;
    private float _endPositionY;
    private float _startPositionX;
    private float _startPositionY;
    protected float _deltaX;
    protected float _deltaY;

    public static CCMoveTo action(float t, float x, float y) {
        return new CCMoveTo(t, x, y);
    }

    protected CCMoveTo(float t, float x, float y) {
        super(t);
        _endPositionX = x;
        _endPositionY = y;
    }

    @Override
    public CCMoveTo copy() {
        return new CCMoveTo(_duration, _endPositionX, _endPositionY);
    }

    @Override
    public void start(CCNode aTarget) {
        super.start(aTarget);
        _startPositionX = _target.position().x;
        _startPositionY = _target.position().y;
        _deltaX = _endPositionX - _startPositionX;
        _deltaY = _endPositionY - _startPositionY;
    }

    @Override
    public void update(float t) {
        _target.setPosition(_startPositionX + _deltaX * t, _startPositionY + _deltaY * t);
    }

}
