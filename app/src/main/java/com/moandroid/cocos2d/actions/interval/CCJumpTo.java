package com.moandroid.cocos2d.actions.interval;

import com.moandroid.cocos2d.nodes.CCNode;

public class CCJumpTo extends CCJumpBy {

    public static CCJumpTo action(float time, float x, float y, float height, int jumps) {
        return new CCJumpTo(time, x, y, height, jumps);
    }

    protected CCJumpTo(float time, float x, float y, float height, int jumps) {
        super(time, x, y, height, jumps);
    }

    @Override
    public CCJumpTo copy() {
        return new CCJumpTo(_duration, _delta.x, _delta.y, _height, _jumps);
    }

    @Override
    public void start(CCNode aTarget) {
        super.start(aTarget);
        _delta.x -= _startPosition.x;
        _delta.y -= _startPosition.y;
    }

}
