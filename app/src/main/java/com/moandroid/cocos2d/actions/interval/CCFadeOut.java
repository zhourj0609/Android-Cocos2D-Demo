package com.moandroid.cocos2d.actions.interval;

import com.moandroid.cocos2d.nodes.protocol.CCRGBAProtocol;


public class CCFadeOut extends CCIntervalAction {

    public static CCFadeOut action(float t) {
        return new CCFadeOut(t);
    }

    protected CCFadeOut(float t) {
        super(t);
    }

    @Override
    public void update(float t) {
        ((CCRGBAProtocol)_target).setOpacity((byte) (255.0f * (1 - t)));
    }

    @Override
    public CCFadeOut reverse() {
        return new CCFadeOut(_duration);
    }
}
