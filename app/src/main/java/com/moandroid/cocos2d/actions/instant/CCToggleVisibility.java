package com.moandroid.cocos2d.actions.instant;

import com.moandroid.cocos2d.nodes.CCNode;

public class CCToggleVisibility extends CCInstantAction {

    public static CCToggleVisibility action() {
        return new CCToggleVisibility();
    }

    @Override
    public void start(CCNode aTarget) {
        super.start(aTarget);
        _target.setVisible(!_target.visible());
    }

}
