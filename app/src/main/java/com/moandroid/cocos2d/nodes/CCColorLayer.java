package com.moandroid.cocos2d.nodes;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import com.moandroid.cocos2d.nodes.protocol.CCBlendProtocol;
import com.moandroid.cocos2d.nodes.protocol.CCRGBAProtocol;
import com.moandroid.cocos2d.renderers.CCDirector;
import com.moandroid.cocos2d.types.CCBlendFunc;
import com.moandroid.cocos2d.types.CCColor3B;
import com.moandroid.cocos2d.types.CCColor4B;
import com.moandroid.cocos2d.types.CCSize;

public class CCColorLayer extends CCLayer 
						  implements CCRGBAProtocol, CCBlendProtocol{


	public static CCColorLayer layer(CCColor4B color, int width, int height){
		return new CCColorLayer(color, width, height);
	}
	
	public static CCColorLayer layer(CCColor4B color){
		CCSize size = CCDirector.sharedDirector().winSize();
		return new CCColorLayer(color, (int)size.width, (int)size.height);
	}
	
	protected CCColorLayer(CCColor4B color, int width, int height) {
		_color.r = color.r;
		_color.g = color.g;
		_color.b = color.b;
		_opacity = color.a;
		setContentSize(CCSize.make(width, height));
		ByteBuffer buffer = ByteBuffer.allocateDirect(4 * 2 * 4);
		buffer.order(ByteOrder.nativeOrder());
		_squareVertices = buffer.asFloatBuffer();
		float[] vertices = {
				0,0,
				0,height,
				width,0,
				width,height,
		};
		_squareVertices.put(vertices);
		_squareVertices.position(0);
		buffer = ByteBuffer.allocateDirect(1 * 4 * 4);
		buffer.order(ByteOrder.nativeOrder());
		_squareColors = buffer;
		updateColor();
	}
	
	protected void updateColor(){
		byte[] colors={
			_color.r,_color.g,_color.b,_opacity,
			_color.r,_color.g,_color.b,_opacity,
			_color.r,_color.g,_color.b,_opacity,
			_color.r,_color.g,_color.b,_opacity,
		};
		_squareColors.put(colors);
		_squareColors.position(0);
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
		updateColor();
	}

	private byte _opacity;
	@Override
	public byte opacity() {
		return _opacity;
	}
	@Override
	public void setOpacity(byte opactity) {
		_opacity = opactity;
		updateColor();
	}

	private CCBlendFunc _blendFunc = new CCBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_DST_ALPHA);
	@Override
	public CCBlendFunc blendFunc() {
		return _blendFunc;
	}

	@Override
	public void setBlendFunc(CCBlendFunc blendFunc) {
		_blendFunc = blendFunc;
	}
	
	private FloatBuffer _squareVertices;
	private ByteBuffer _squareColors;
	
	public void draw(GL10 gl){
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisable(GL10.GL_TEXTURE_2D);

		gl.glVertexPointer(2, GL10.GL_FLOAT, 0, _squareVertices);
		gl.glColorPointer(4, GL10.GL_UNSIGNED_BYTE, 0, _squareColors);
		
		boolean newBlend = false;
		if( _blendFunc.src != GL10.GL_ONE ||
			_blendFunc.dst !=  GL10.GL_ONE_MINUS_DST_ALPHA) {
			newBlend = true;
			gl.glBlendFunc(_blendFunc.src, _blendFunc.dst);
		}
		else if( _opacity != 255 ) {
			newBlend = true;
			gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		}
		
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		
		if( newBlend )
			gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_DST_ALPHA);
		
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glEnable(GL10.GL_TEXTURE_2D);
	}
}
