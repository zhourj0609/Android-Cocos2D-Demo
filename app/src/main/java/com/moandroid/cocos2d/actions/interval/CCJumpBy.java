package com.moandroid.cocos2d.actions.interval;

import com.moandroid.cocos2d.nodes.CCNode;

import android.graphics.PointF;

public class CCJumpBy extends CCIntervalAction {

    protected PointF _startPosition;
    protected PointF _delta;
    protected float _height;
    protected int _jumps;

    public static CCJumpBy action(float time, float x, float y, float height, int jumps) {
        return new CCJumpBy(time, x, y, height, jumps);
    }

    protected CCJumpBy(float time, float x, float y, float h, int j) {
        super(time);
        _startPosition = new PointF();
        _delta = new PointF(x, y);
        _height = h;
        _jumps = j;
    }

    @Override
    public CCJumpBy copy() {
        return new CCJumpBy(_duration, _delta.x, _delta.y, _height, _jumps);
    }

    @Override
    public void start(CCNode aTarget) {
        super.start(aTarget);
        _startPosition.x = _target.position().x;
        _startPosition.y = _target.position().y;
    }

    @Override
    public void update(float t) {
        float y = _height * (float) Math.abs(Math.sin(t * (float) Math.PI * _jumps));
        y += _delta.y * t;
        float x = _delta.x * t;
        _target.setPosition(_startPosition.x + x, _startPosition.y + y);
    }

    @Override
    public CCJumpBy reverse() {
        return new CCJumpBy(_duration, -_delta.x, -_delta.y, _height, _jumps);
    }

}
