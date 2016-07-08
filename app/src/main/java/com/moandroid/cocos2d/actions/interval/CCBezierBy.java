package com.moandroid.cocos2d.actions.interval;

import com.moandroid.cocos2d.nodes.CCNode;
import com.moandroid.cocos2d.types.CCPoint;

public class CCBezierBy extends CCIntervalAction {

	public static CCBezierBy action(float t, CCBezierConfig c) {
        return new CCBezierBy(t, c);
    }
	
    public CCBezierBy(float t, CCBezierConfig c) {
        super(t);
        _config = c;
        _startPosition = CCPoint.make(0, 0);
	}
    
    private CCBezierConfig _config;
    private CCPoint _startPosition;
    
    public CCBezierBy copy(){
    	return new CCBezierBy(_duration, _config);
    }
    
    @Override
    public void start(CCNode target) {
        super.start(target);
        _startPosition.x = _target.position().x;
        _startPosition.y = _target.position().y;
    }

    @Override
    public void update(float t) {
        float xa = _config.startPosition.x;
        float xb = _config.controlPoint_1.x;
        float xc = _config.controlPoint_2.x;
        float xd = _config.endPosition.x;

        float ya = _config.startPosition.y;
        float yb = _config.controlPoint_1.y;
        float yc = _config.controlPoint_2.y;
        float yd = _config.endPosition.y;

        float x = bezierat(xa, xb, xc, xd, t);
        float y = bezierat(ya, yb, yc, yd, t);
        _target.setPosition(_startPosition.x + x, _startPosition.y + y);
    }

    // Bezier cubic formulae :
    //	((1 - t) + t)3 = 1 expands to (1 - t)3 + 3t(1-t)2 + 3t2(1 - t) + t3 = 1
    private static float bezierat(float a, float b, float c, float d, float t) {
        return (float) (Math.pow(1 - t, 3) * a +
                3 * t * (Math.pow(1 - t, 2)) * b +
                3 * Math.pow(t, 2) * (1 - t) * c +
                Math.pow(t, 3) * d);
    }

    @Override
    public CCIntervalAction reverse() {
        // TODO: reverse it's not working as expected
        CCBezierConfig r = new CCBezierConfig();
        r.startPosition = CCPoint.ccpNeg(_config.startPosition);
        r.endPosition = CCPoint.ccpNeg(_config.endPosition);
        r.controlPoint_1 = CCPoint.ccpNeg(_config.controlPoint_1);
        r.controlPoint_2 = CCPoint.ccpNeg(_config.controlPoint_2);

        return new CCBezierBy(_duration, r);
    }  
}
