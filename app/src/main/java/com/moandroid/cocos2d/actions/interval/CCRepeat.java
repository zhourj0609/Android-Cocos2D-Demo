package com.moandroid.cocos2d.actions.interval;

import com.moandroid.cocos2d.actions.CCFiniteTimeAction;
import com.moandroid.cocos2d.nodes.CCNode;

public class CCRepeat extends CCIntervalAction {

	  	private int times;
	    private int total;
	    private CCFiniteTimeAction other;

	    public static CCRepeat action(CCFiniteTimeAction action, int t) {
	        return new CCRepeat(action, t);
	    }

	    protected CCRepeat(CCFiniteTimeAction action, int t) {
	        super(action.duration() * t);

	        times = t;
	        other = action;

	        total = 0;
	    }

	    @Override
	    public CCRepeat copy() {
	        return new CCRepeat(other.copy(), times);
	    }

	    @Override
	    public void start(CCNode aTarget) {
	        total = 0;
	        super.start(aTarget);
	        other.start(aTarget);
	    }

	    public void stop() {

	        other.stop();
	        super.stop();
	    }

//	    @Override
//	    public void step(float dt) {
//	        other.step(dt);
//	        if (other.isDone()) {
//	            total++;
//	            other.start();
//	        }
//	    }

	    // issue #80. Instead of hooking step:, hook update: since it can be called by any
	    // container action like Repeat, Sequence, AccelDeccel, etc..

	    @Override
	    public void update(float dt) {
	        float t = dt * times;
	        float r = t % 1.0f;
	        if (t > total + 1) {
	            other.update(1.0f);
	            total++;
	            other.stop();
	            other.start(_target);
	            other.update(0.0f);
	        } else {
	            // fix last repeat position
	            // else it could be 0.
	            if (dt == 1.0f)
	                r = 1.0f;
	            other.update(Math.min(r, 1));
	        }
	    }

	    @Override
	    public boolean isDone() {
	        return (total == times);
	    }

	    @Override
	    public CCRepeat reverse() {
	        return new CCRepeat(other.reverse(), times);
	    }

}
