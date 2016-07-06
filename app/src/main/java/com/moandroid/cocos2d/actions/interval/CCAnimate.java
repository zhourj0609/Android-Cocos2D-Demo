package com.moandroid.cocos2d.actions.interval;

import com.moandroid.cocos2d.animation.CCAnimationProtocol;
import com.moandroid.cocos2d.animation.CCNodeFrames;
import com.moandroid.cocos2d.nodes.CCNode;
import com.moandroid.cocos2d.texture.CCTexture2D;
import com.moandroid.cocos2d.texture.CCTextureCache;


public class CCAnimate extends CCIntervalAction{

		private CCAnimationProtocol _animation;
	    private CCTexture2D _originalFrame;
	    private boolean _restoreOriginalFrame;

	    public static CCAnimate action(CCAnimationProtocol anim) {
	        assert anim != null : "Animate: argument Animation must be non-null";
	        return new CCAnimate(anim, true);
	    }

	    public static CCAnimate action(CCAnimationProtocol anim, boolean restore) {
	        assert anim != null : "Animate: argument Animation must be non-null";
	        return new CCAnimate(anim, restore);
	    }

	    protected CCAnimate(CCAnimationProtocol anim, boolean restore) {
	        super(anim.frames().size() * anim.delay());

	        _restoreOriginalFrame = restore;
	        _animation = anim;
	        _originalFrame = null;
	    }

	    @Override
	    public CCAnimate copy() {
	        return new CCAnimate(_animation, true);
	    }

	    @Override
	    public void start(CCNode aTarget) {
	        super.start(aTarget);
	        if(_restoreOriginalFrame){
		        CCNodeFrames sprite = (CCNodeFrames) _target;   
		        _originalFrame = (CCTexture2D) sprite.displayFrame();
	        }
	    }

	    @Override
	    public void stop() {
	        if (_restoreOriginalFrame) {
	        	CCNodeFrames sprite = (CCNodeFrames) _target;
	            sprite.setDisplayFrame(_originalFrame);
	            CCTextureCache.sharedTextureCache().release(_originalFrame);
	            _originalFrame = null;
	        }

	        super.stop();
	    }

	    @Override
	    public void update(float t) {
	        int idx = 0;

	        float slice = 1.0f / _animation.frames().size();

	        if (t != 0)
	            idx = (int) (t / slice);

	        if (idx >= _animation.frames().size()) {
	            idx = _animation.frames().size() - 1;
	        }

	        CCNodeFrames sprite = (CCNodeFrames) _target;
	        if (!sprite.isFrameDisplayed((CCTexture2D) _animation.frames().get(idx))) {
	            sprite.setDisplayFrame((CCTexture2D) _animation.frames().get(idx));
	        }
	    }
}
