package com.moandroid.cocos2d.nodes.protocol;

import com.moandroid.cocos2d.types.CCColor3B;

public interface CCRGBAProtocol {
	public CCColor3B color();
	public void setColor(CCColor3B color);

	public byte opacity();
	public void setOpacity(byte opactity);

//	public boolean opacityModifyRGB();
//	public void setOpacityModifyRGB(boolean modify);
}
