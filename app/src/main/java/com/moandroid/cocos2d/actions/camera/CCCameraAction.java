package com.moandroid.cocos2d.actions.camera;

import com.moandroid.cocos2d.actions.interval.CCIntervalAction;
import com.moandroid.cocos2d.actions.interval.CCReverseTime;
import com.moandroid.cocos2d.nodes.CCNode;

public class CCCameraAction extends CCIntervalAction{
    protected float centerXOrig;
    protected float centerYOrig;
    protected float centerZOrig;

    protected float eyeXOrig;
    protected float eyeYOrig;
    protected float eyeZOrig;

    protected float upXOrig;
    protected float upYOrig;
    protected float upZOrig;

    protected CCCameraAction(float t) {
        super(t);
    }

    @Override
    public void start(CCNode aTarget) {
        super.start(aTarget);

        float x[] = new float[1];
        float y[] = new float[1];
        float z[] = new float[1];

        _target.camera().center(x, y, z);
        centerXOrig = x[0];
        centerYOrig = y[0];
        centerZOrig = z[0];

        _target.camera().eye(x, y, z);
        eyeXOrig = x[0];
        eyeYOrig = y[0];
        eyeZOrig = z[0];

        _target.camera().up(x, y, z);
        upXOrig = x[0];
        upYOrig = y[0];
        upZOrig = z[0];
    }

    @Override
    public CCIntervalAction reverse() {
        return CCReverseTime.action(this);
    }
}
