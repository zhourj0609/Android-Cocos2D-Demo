package com.moandroid.cocos2d.actions;

import com.moandroid.cocos2d.nodes.CCNode;

public class CCRepeatForever extends CCAction {

	   protected CCFiniteTimeAction _other;

	    public static CCRepeatForever action(CCFiniteTimeAction action) {
	        return new CCRepeatForever(action);
	    }

	    protected CCRepeatForever(CCFiniteTimeAction action) {
	        _other = action;
	    }

	    @Override
	    public CCRepeatForever copy() {
	        return new CCRepeatForever(_other.copy());
	    }

	    @Override
	    public void start(CCNode aTarget) {
	        super.start(aTarget);
	        _other.start(_target);
	    }

	    @Override
	    public void step(float dt) {
	        _other.step(dt);
			// repeat forever do this action
	        if (_other.isDone()) _other.start(_target);
	    }


	    @Override
	    public boolean isDone() {
	        return false;
	    }

	    public CCRepeatForever reverse() {
	        return new CCRepeatForever(_other.reverse());
	    }

}
