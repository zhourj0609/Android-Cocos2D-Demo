package com.moandroid.cocos2d.actions.interval;

import com.moandroid.cocos2d.types.CCPoint;

public class CCBezierConfig {

	public static CCBezierConfig config() {
		return new CCBezierConfig();
	}

	public CCPoint startPosition;
	public CCPoint controlPoint_1;
	public CCPoint controlPoint_2;
	public CCPoint endPosition;

}
