package com.moandroid.cocos2d.actions.interval;

import com.moandroid.cocos2d.nodes.CCNode;
import com.moandroid.cocos2d.nodes.protocol.CCRGBAProtocol;
import com.moandroid.cocos2d.types.CCColor3B;

public class CCTintTo extends CCIntervalAction {

	  protected int toR, toG, toB;
	    protected int fromR, fromG, fromB;

	    public static CCTintTo action(float t, int r, int g, int b) {
	        return new CCTintTo(t, r, g, b);
	    }

	    protected CCTintTo(float t, int r, int g, int b) {
	        super(t);
	        toR = r;
	        toG = g;
	        toB = b;
	    }

	    @Override
	    public CCTintTo copy() {
	        return new CCTintTo(_duration, toR, toG, toB);
	    }

	    @Override
	    public void start(CCNode aTarget) {
	        super.start(aTarget);

	        CCRGBAProtocol tn = (CCRGBAProtocol) _target;

	        CCColor3B c = tn.color();
	        fromR = c.r&0xFF;
	        fromG = c.g&0xFF;
	        fromB = c.b&0xFF;
	    }

	    @Override
	    public void update(float t) {
	        ((CCRGBAProtocol) _target).setColor(
	                new CCColor3B((byte) (fromR + (toR - fromR) * t),
	                        (byte) (fromG + (toG - fromG) * t),
	                        (byte) (fromB + (toB - fromB) * t)));
	    }
}
