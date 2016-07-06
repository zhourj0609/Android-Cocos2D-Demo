package com.moandroid.cocos2d.actions.interval;

import com.moandroid.cocos2d.actions.CCFiniteTimeAction;
import com.moandroid.cocos2d.nodes.CCNode;

public class CCSpawn extends CCIntervalAction {

    private CCFiniteTimeAction one;
    private CCFiniteTimeAction two;

    public static CCFiniteTimeAction actions(CCFiniteTimeAction action1, CCFiniteTimeAction... params) {
        CCFiniteTimeAction prev = action1;

        if (action1 != null) {
            for (CCFiniteTimeAction now : params)
                prev = new CCSpawn(prev, now);
        }
        return  (CCFiniteTimeAction)prev;
    }

    protected CCSpawn(CCFiniteTimeAction one_, CCFiniteTimeAction two_) {
 
        super(Math.max(one_.duration(), two_.duration()));


        float d1 = one_.duration();
        float d2 = two_.duration();

        one = one_;
        two = two_;

        if (d1 > d2)
            two = new CCSequence(two_, new CCDelayTime(d1 - d2));
        else if (d1 < d2)
            one = new CCSequence(one_, new CCDelayTime(d2 - d1));
    }

    @Override
    public CCSpawn copy() {
        return new CCSpawn(one.copy(), two.copy());
    }


    @Override
    public void start(CCNode aTarget) {
        super.start(aTarget);
        one.start(_target);
        two.start(_target);
    }

    @Override
    public void stop() {
        one.stop();
        two.stop();
        super.stop();
    }

    @Override
    public void update(float t) {
        one.update(t);
        two.update(t);
    }

    @Override
    public CCSpawn reverse() {
        return new CCSpawn(one.reverse(), two.reverse());
    }

}
