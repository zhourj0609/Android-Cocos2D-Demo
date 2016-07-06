package com.moandroid.cocos2d.actions.interval;

import com.moandroid.cocos2d.types.CCColor3B;
import com.moandroid.cocos2d.nodes.CCNode;
import com.moandroid.cocos2d.nodes.protocol.CCRGBAProtocol;

public class CCTintBy extends CCIntervalAction {

	   protected int deltaR, deltaG, deltaB;
	    protected int fromR, fromG, fromB;

	    public static CCTintBy action(float t, int r, int g, int b) {
	        return new CCTintBy(t, r, g, b);
	    }

	    protected CCTintBy(float t, int r, int g, int b) {
	        super(t);
	        deltaR = r;
	        deltaG = g;
	        deltaB = b;
	    }

	    @Override
	    public CCTintBy copy() {
	        return new CCTintBy(_duration, deltaR, deltaG, deltaB);
	    }

	    @Override
	    public void start(CCNode aTarget) {
	        super.start(aTarget);

	        CCColor3B c = ((CCRGBAProtocol) _target).color();
	        fromR = c.g&0xFF;
	        fromG = c.g&0xFF;
	        fromB = c.b&0xFF;
	    }

	    @Override
	    public void update(float t) {
	        CCRGBAProtocol tn = (CCRGBAProtocol) _target;
	        tn.setColor(new CCColor3B(
	        			(byte) (fromR + deltaR * t), 
	        			(byte) (fromG + deltaG * t),
	        			(byte) (fromB + deltaB * t)));
	    }

	    @Override
	    public CCTintBy reverse() {
	        return new CCTintBy(_duration, -deltaR, -deltaG, -deltaB);
	    }
}
