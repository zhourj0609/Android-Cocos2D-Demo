package com.moandroid.cocos2d.animation;


public interface CCNodeFrames {

	public void setDisplayFrame(Object frame);
    public void setDisplayFrame(String animationName, int frameIndex);

    public boolean isFrameDisplayed(Object frame);

    public Object displayFrame();

    public CCAnimationProtocol animationByName(String animationName);

    public void addAnimation(CCAnimationProtocol animation);
}
