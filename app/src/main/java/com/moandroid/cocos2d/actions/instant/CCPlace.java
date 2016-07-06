package com.moandroid.cocos2d.actions.instant;

import com.moandroid.cocos2d.nodes.CCNode;

import android.graphics.PointF;


public class CCPlace extends CCInstantAction{

    private PointF position;

    public static CCPlace action(float x, float y) {
        return new CCPlace(x, y);
    }

    /**
     * creates a Place action with a position
     */
    protected CCPlace(float x, float y) {
        position = new PointF(x, y);
    }

    @Override
    public CCPlace copy() {
        return new CCPlace(position.x, position.y);
    }

    @Override
    public void start(CCNode aTarget) {
        super.start(aTarget);
        _target.setPosition(position.x, position.y);
    }

}
