package com.moandroid.cocos2d.animation;

import java.util.ArrayList;


public interface CCAnimationProtocol {
	public ArrayList<Object> frames();
	public float delay();
	public String name();
	public void cleanup();
}
