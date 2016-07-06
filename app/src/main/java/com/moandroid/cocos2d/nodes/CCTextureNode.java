package com.moandroid.cocos2d.nodes;

import javax.microedition.khronos.opengles.GL10;

import com.moandroid.cocos2d.nodes.protocol.CCBlendProtocol;
import com.moandroid.cocos2d.nodes.protocol.CCNodeSizeProtocol;
import com.moandroid.cocos2d.nodes.protocol.CCRGBAProtocol;
import com.moandroid.cocos2d.nodes.protocol.CCTextureProtocol;
import com.moandroid.cocos2d.texture.CCTexture2D;
import com.moandroid.cocos2d.texture.CCTextureCache;
import com.moandroid.cocos2d.types.CCBlendFunc;
import com.moandroid.cocos2d.types.CCColor3B;
import com.moandroid.cocos2d.types.CCPoint;
import com.moandroid.cocos2d.types.CCSize;
import com.moandroid.cocos2d.util.CCUtils;

public class CCTextureNode extends CCNode
						   implements CCRGBAProtocol, 
									  CCNodeSizeProtocol,
									  CCTextureProtocol,
									  CCBlendProtocol{

	public static final String LOG_TAG = CCTextureNode.class.getSimpleName();


	public CCTextureNode(){
		setAnchorPoint(CCPoint.ccp(0.5f,0.5f));
	}
	
    private byte _opacity = (byte)255;
	@Override
	public byte opacity() {
		return _opacity;
	}
	@Override
	public void setOpacity(byte opactity) {
		_opacity = opactity;
	}
	
	private CCColor3B _color =  new CCColor3B((byte)255,(byte)255,(byte)255);
	@Override
	public CCColor3B color() {
		return _color;
	}
	@Override
	public void setColor(CCColor3B color) {
		_color.r = color.r;
		_color.g = color.g;
		_color.b = color.b;
       _color = color;
	}

	@Override
	public float height() {
		return _texture.getHeight();
	}

	@Override
	public float width() {
		return _texture.getWidth();
	}

	protected CCTexture2D _texture;
	@Override
	public void setTexture(CCTexture2D texture) {
		if(_texture != null){
			CCTextureCache.sharedTextureCache().release(_texture);
		}
		_texture = texture;
		CCTextureCache.sharedTextureCache().retain(_texture);
		setContentSize(CCSize.make(_texture.getWidth(), _texture.getHeight()));
		
	}
	@Override
	public CCTexture2D texture() {
		return _texture;
	}

	private CCBlendFunc _blendFunc = new CCBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);//CCUtils.CC_BLEND_SRC,  CCUtils.CC_BLEND_DST);
	@Override
	public CCBlendFunc blendFunc() {
		return _blendFunc;
	}

	@Override
	public void setBlendFunc(CCBlendFunc blendFunc) {
		_blendFunc.src = blendFunc.src;
		_blendFunc.dst = blendFunc.dst;
	}
	@Override
	public void cleanup() {
		if(_texture!=null)
			CCTextureCache.sharedTextureCache().release(_texture);
		super.cleanup();
	}
	private CCPoint _point = CCPoint.zero();
	@Override
	public void draw(GL10 gl) {
		if (_texture != null){
			gl.glDisableClientState(GL10.GL_COLOR_ARRAY);		
	        gl.glColor4f((_color.r&0xFF) / 255f, (_color.g & 0xFF) / 255f, (_color.b & 0xFF) / 255f, (_opacity & 0xFF) / 255f);

	        boolean newBlend = false;
	        if (_blendFunc.src != CCUtils.CC_BLEND_SRC ||
	        	_blendFunc.dst != CCUtils.CC_BLEND_DST) {
	            newBlend = true;
	            gl.glBlendFunc(_blendFunc.src, _blendFunc.dst);
	        }

	        _texture.drawAtPoint(gl, _point);

	        if (newBlend)
	           gl.glBlendFunc(CCUtils.CC_BLEND_SRC, CCUtils.CC_BLEND_DST);
	        // is this chepear than saving/restoring color state ?
	        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);				
		}
	}

	
}
