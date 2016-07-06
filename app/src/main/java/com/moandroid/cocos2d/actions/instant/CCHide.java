package com.moandroid.cocos2d.actions.instant;

import com.moandroid.cocos2d.nodes.CCNode;

public class CCHide extends CCInstantAction {

    public static CCHide action() {
        return new CCHide();
    }

    @Override
    public void start(CCNode aTarget) {
        super.start(aTarget);
        _target.setVisible(false);
    }

    @Override
    public CCInstantAction reverse() {
        return new CCShow();
    }

}
