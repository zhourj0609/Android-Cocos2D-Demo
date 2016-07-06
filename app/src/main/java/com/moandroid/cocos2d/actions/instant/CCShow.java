package com.moandroid.cocos2d.actions.instant;

import com.moandroid.cocos2d.nodes.CCNode;

public class CCShow extends CCInstantAction {
    public static CCShow action() {
        return new CCShow();
    }

    @Override
    public void start(CCNode aTarget) {
        super.start(aTarget);
        _target.setVisible(true);
    }

    @Override
    public CCInstantAction reverse() {
        return new CCHide();
    }
}
