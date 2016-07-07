package com.moandroid.cocos2d.nodes.sprite;

import java.util.HashMap;
import android.graphics.Bitmap;

import com.moandroid.cocos2d.animation.CCAnimationProtocol;
import com.moandroid.cocos2d.animation.CCNodeFrames;
import com.moandroid.cocos2d.nodes.CCTextureNode;
import com.moandroid.cocos2d.texture.CCTexture2D;
import com.moandroid.cocos2d.texture.CCTextureCache;

public class CCSprite extends CCTextureNode 
					  implements CCNodeFrames {

	public static final String LOG_TAG = CCSprite.class.getSimpleName();
	
	public static CCSprite sprite(String fileName) {
		return new CCSprite(CCTextureCache.sharedTextureCache().getTexture(fileName));
	}

    public static CCSprite sprite(Bitmap image) {
        return new CCSprite(CCTextureCache.sharedTextureCache().getTexture(image));
    }

	protected CCSprite(CCTexture2D texture) {
		setTexture(texture);
	}

	private HashMap<String, CCAnimationProtocol> _animations;
	private void allocAnimations() {
        _animations = new HashMap<String, CCAnimationProtocol>(2);
    }

	@Override
	public void setDisplayFrame(String animationName, int frameIndex) {
		if(_animations != null){
			CCAnimationProtocol anim = _animations.get(animationName);
			CCTexture2D frame = (CCTexture2D) anim.frames().get(frameIndex);
			setTexture(frame);
			CCTextureCache.sharedTextureCache().retain(frame);
		}
	}

	@Override
	public boolean isFrameDisplayed(Object frame) {
		return _texture == (CCTexture2D)frame;
	}
	
	@Override
	public Object displayFrame() {
		return _texture;
	}

	@Override
	public void addAnimation(CCAnimationProtocol animation) {
		if(_animations == null){
			allocAnimations();
		}
		_animations.put(animation.name(), animation);
	}

	@Override
	public CCAnimationProtocol animationByName(String animationName) {
		return _animations.get(animationName);
	}

	@Override
	public void setDisplayFrame(Object frame) {
		CCTexture2D tx= (CCTexture2D)frame;
		setTexture(tx);
		CCTextureCache.sharedTextureCache().retain(tx);
	}
}
