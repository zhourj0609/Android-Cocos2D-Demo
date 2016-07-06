package com.moandroid.cocos2d.texture;

import javax.microedition.khronos.opengles.GL10;

import com.moandroid.cocos2d.nodes.protocol.CCTextureProtocol;


public class CCTextureAtlas implements CCTextureProtocol{

	public static final String LOG_TAG = CCTextureAtlas.class.getSimpleName();
	
	public static CCTextureAtlas textureAtlas(String fileName){
		return new CCTextureAtlas(fileName);
	}
	
	public CCTextureAtlas(String fileName) {
		this(CCTextureCache.sharedTextureCache().getTexture(fileName));	
	}
	
	public static CCTextureAtlas textureAtlas(CCTexture2D tex){
		return new CCTextureAtlas(tex);
	}

	public CCTextureAtlas(CCTexture2D tex) {	
		_texture = tex;
	}

	private CCTexture2D _texture; 
    @Override
    public CCTexture2D texture() {
        return _texture;
    }
    @Override
    public void setTexture(CCTexture2D tx){
    	if(_texture != null)
    		_texture.release();
    	_texture = tx;
    }

	public void release() {
		if(_texture != null){
			_texture.release();
			_texture = null;
		}
	}
    
	public void bindTexture(GL10 gl){
		_texture.loadTexture(gl);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, _texture.name());
	}
}
