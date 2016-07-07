package com.moandroid.cocos2d.actions.instant;

import com.moandroid.cocos2d.actions.CCFiniteTimeAction;

public class CCInstantAction extends CCFiniteTimeAction{

    protected CCInstantAction() {
        super(0);
    }

    protected CCInstantAction(float d) {
        super(d);
    }
    
    @Override
    public CCInstantAction copy() {
        return new CCInstantAction(_duration);
    }

    @Override
    public boolean isDone() {
        return true;
    }

    @Override
    public void step(float dt) {
        update(1f);
    }

    public void update(float t) {
        // ignore
    }

    @Override
    public CCInstantAction reverse() {
        return copy();
    }

}
