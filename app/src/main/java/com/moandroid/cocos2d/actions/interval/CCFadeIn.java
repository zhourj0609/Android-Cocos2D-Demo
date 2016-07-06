package com.moandroid.cocos2d.actions.interval;


import com.moandroid.cocos2d.nodes.protocol.CCRGBAProtocol;

public class CCFadeIn extends CCIntervalAction {

    public static CCFadeIn action(float t) {
        return new CCFadeIn(t);
    }

    protected CCFadeIn(float t) {
        super(t);
    }

    @Override
    public void update(float t) {
        ((CCRGBAProtocol) _target).setOpacity((byte) (255.0f * t));
    }

    @Override
    public CCFadeIn reverse() {
        return new CCFadeIn(_duration);
    }
}
