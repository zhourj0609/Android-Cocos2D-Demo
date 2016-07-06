package com.moandroid.cocos2d.events;

public interface CCTouchDelegate {

	boolean touchesBegan(CCEvent event);

	boolean touchesMoved(CCEvent event);

	boolean touchesEnded(CCEvent event);

	boolean touchesCancelled(CCEvent event);

}
