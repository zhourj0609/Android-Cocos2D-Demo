package com.moandroid.cocos2d.actions.interval;

import com.moandroid.cocos2d.actions.CCFiniteTimeAction;
import com.moandroid.cocos2d.nodes.CCNode;

public class CCReverseTime extends CCIntervalAction {
	   private CCFiniteTimeAction other;

	    public static CCReverseTime action(CCFiniteTimeAction action) {
	        return new CCReverseTime(action);
	    }

	    protected CCReverseTime(CCFiniteTimeAction action) {
	        super(action.duration());

	        other = action;
	    }

	    @Override
	    public CCReverseTime copy() {
	        return new CCReverseTime(other.copy());
	    }


	    @Override
	    public void start(CCNode aTarget) {
	        super.start(aTarget);
	        other.start(_target);
	    }

	    @Override
	    public void stop() {
	        other.stop();
	        super.stop();
	    }

	    @Override
	    public void update(float t) {
	        other.update(1 - t);
	    }

	    public CCReverseTime reverse() {
	        return new CCReverseTime(other.copy());
	    }
}
