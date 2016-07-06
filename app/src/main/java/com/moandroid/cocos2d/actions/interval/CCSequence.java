package com.moandroid.cocos2d.actions.interval;

import java.util.ArrayList;

import com.moandroid.cocos2d.actions.CCFiniteTimeAction;
import com.moandroid.cocos2d.nodes.CCNode;

public class CCSequence extends CCIntervalAction {

	   private ArrayList<CCFiniteTimeAction> actions;
	    private float split;
	    private int last;


	    public static CCFiniteTimeAction actions(CCFiniteTimeAction action1, CCFiniteTimeAction... actions) {
	        CCFiniteTimeAction prev = action1;
	        for (CCFiniteTimeAction now : actions) {
	            prev = new CCSequence(prev, now);
	        }
	        return (CCFiniteTimeAction) prev;
	    }

	    protected CCSequence(CCFiniteTimeAction one, CCFiniteTimeAction two) {
	        //assert one != null : "Sequence: argument one must be non-null";
	        //assert two != null : "Sequence: argument two must be non-null";

	        super(one.duration() + two.duration());

	        actions = new ArrayList<CCFiniteTimeAction>(2);
	        actions.add(one);
	        actions.add(two);
	    }

	    @Override
	    public CCSequence copy() {
	        return new CCSequence(actions.get(0).copy(), actions.get(1).copy());
	    }

	    @Override
	    public void start(CCNode aTarget) {
	        super.start(aTarget);
	        split = actions.get(0).duration() / _duration;
	        last = -1;
	    }

	    public void stop() {
	        for (CCFiniteTimeAction action : actions)
	            action.stop();
	        super.stop();
	    }


	    @Override
	    public void update(float t) {
	        int found;
	        float new_t;

	        if (t >= split) {
	            found = 1;
	            if (split == 1)
	                new_t = 1;
	            else
	                new_t = (t - split) / (1 - split);
	        } else {
	            found = 0;
	            if (split != 0)
	                new_t = t / split;
	            else
	                new_t = 1;
	        }

	        if (last == -1 && found == 1) {
	            actions.get(0).start(_target);
	            actions.get(0).update(1.0f);
	            actions.get(0).stop();
	        }

	        if (last != found) {
	            if (last != -1) {
	                actions.get(last).update(1.0f);
	                actions.get(last).stop();
	            }
	            actions.get(found).start(_target);
	        }
	        actions.get(found).update(new_t);
	        last = found;
	    }

	    @Override
	    public CCSequence reverse() {
	        return new CCSequence(actions.get(1).reverse(), actions.get(0).reverse());
	    }

}
