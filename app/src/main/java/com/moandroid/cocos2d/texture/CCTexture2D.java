package com.moandroid.cocos2d.texture;

import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE_2D;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE_MAG_FILTER;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE_MIN_FILTER;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE_WRAP_S;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE_WRAP_T;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;
import android.opengl.GLUtils;
import android.util.Log;

import com.moandroid.cocos2d.nodes.ui.CCLabel;
import com.moandroid.cocos2d.types.CCPoint;
import com.moandroid.cocos2d.types.CCRect;
import com.moandroid.cocos2d.types.CCSize;

public class CCTexture2D {
	public static final String LOG_TAG = CCTexture2D.class.getSimpleName();
	public static final int kMaxTextureSize = 1024;
	private static CCTexParams antiAliasTexParams = new CCTexParams(
			 GL10.GL_LINEAR, 
			 GL10.GL_LINEAR, 
			 GL10.GL_CLAMP_TO_EDGE, 
			 GL10.GL_CLAMP_TO_EDGE);
	
	private static CCTexParams aliasTexParams = new CCTexParams(
			 GL10.GL_NEAREST, 
			 GL10.GL_NEAREST, 
			 GL10.GL_CLAMP_TO_EDGE, 
			 GL10.GL_CLAMP_TO_EDGE);
	
	int _width;
	public int pixelsWide(){
		return _width;
	}
	
	int _height;
	public int pixelsHigh(){
		return _height;
	}
	
	public float getWidth(){
		return _size.width;
	}
	
	public float getHeight(){
		return _size.height;
	}
	
	int _name;
	public int name(){
		return _name;
	}
	
	CCSize _size;
	public CCSize contentSize() {
		return _size;
	}
	
	Config _format;
	public Config format(){
		return _format;
	}
	public void setFormat(Config newFormat){
		_format = newFormat;
	}
	
	float _maxS;
	float maxS(){
		return _maxS;
	}
	
	float _maxT;
	public float maxT(){
		return _maxT;
	}
	
	boolean _retainBitmap = false;
	
	public void init(Bitmap image, CCSize contentSize) {
		mBitmap = image;
        _width = image.getWidth();
        _height = image.getHeight();
        _size = contentSize;
        _format = image.getConfig();
        _maxS = _size.width /  _width;
        _maxT = _size.height / _height;
        ByteBuffer vfb;
        ByteBuffer tfb;
 
        vfb = ByteBuffer.allocateDirect(4 * 3 * 4);
        tfb = ByteBuffer.allocateDirect(4 * 2 * 4);
        
        vfb.order(ByteOrder.nativeOrder());
        mVertices = vfb.asFloatBuffer();

        tfb.order(ByteOrder.nativeOrder());
        mCoordinates = tfb.asFloatBuffer();
		float[] coordinates = {
				0.0f,	0.0f,
				0.0f,	_maxT,
				_maxS,	0.0f,
				_maxS,	_maxT
		};
		mCoordinates.put(coordinates);
		mCoordinates.position(0);
   }
	
	public CCTexture2D(Bitmap image) {
		CCSize imageSize = CCSize.make(image.getWidth(), image.getHeight());
//		CCAffineTransform transform = CCAffineTransform.identity();
	    int width = (int) imageSize.width;
	    if ((width != 1) && (width & (width - 1)) != 0) {
	        int i = 1;
	        while (i < width)
	            i *= 2;
	        width = i;
	    }
	
	    int height = (int) imageSize.height;
	    if ((height != 1) && (height & (height - 1)) != 0) {
	        int i = 1;
	        while ( i < height)
	            i *= 2;
	        height = i;
	    }
	
	    while (width > kMaxTextureSize || height > kMaxTextureSize) {
//	        width /= 2;
//	        height /= 2;
//	        transform.scale(0.5f, 0.5f);
//	        imageSize.width *= 0.5f;
//	        imageSize.height *= 0.5f;
	    	Log.e(LOG_TAG, "Too big picture!");
	    }
	    
	    if (imageSize.width != width && imageSize.height != height) {
	        Bitmap bitmap = Bitmap.createBitmap(width, height,
	                image.hasAlpha() ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
	        Canvas canvas = new Canvas(bitmap);
	        canvas.drawBitmap(image, 0, 0, null);
	        image = bitmap;
	    }
	    init(image, imageSize);	
	}

	void resetBitmap(Bitmap image){
		CCSize imageSize = CCSize.make(image.getWidth(), image.getHeight());
		int width = _width;
		int height = _height;
		    
	    if (imageSize.width != width && imageSize.height != height) {
	        Bitmap bitmap = Bitmap.createBitmap(width, height,
	                image.hasAlpha() ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
	        Canvas canvas = new Canvas(bitmap);
	        canvas.drawBitmap(image, 0, 0, null);
	        image = bitmap;
	    }
	    mBitmap = image;
	}
	
	private FloatBuffer mVertices;
	private FloatBuffer mCoordinates;
	Bitmap mBitmap;
	
	private CCTexParams _texParams = antiAliasTexParams;
	public void setTexParameters(GL10 gl, CCTexParams texParams) {
		_texParams = texParams;
	}
	
	public void setAntiAliasTexParameters(GL10 gl){
		setTexParameters(gl, antiAliasTexParams);
	}
	
	public void setAliasTexParameters(GL10 gl){
		setTexParameters(gl, aliasTexParams);
	}
	
    public CCTexture2D(Bitmap image, CCSize imageSize) {
        Bitmap.Config config = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = Bitmap.createBitmap((int) imageSize.width, (int) imageSize.height, config);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(image, 0, 0, new Paint());
        init(bitmap, imageSize);
   }
    
	public CCTexture2D(String text, String fontName, int fontSize) {
		this(text, null,
		CCLabel.TEXT_ALIGNMENT_CENTER, fontName, fontSize);
	}

	public CCTexture2D(String text, CCSize dimensions,
			int alignment, String fontName, int fontSize) {
		 	Typeface typeface = Typeface.create(fontName, Typeface.NORMAL);

	        Paint textPaint = new Paint();
	        textPaint.setTypeface(typeface);
	        textPaint.setTextSize(fontSize);

	        int ascent = 0;
	        int descent = 0;
	        int measuredTextWidth = 0;

	        ascent = (int) Math.ceil(-textPaint.ascent());  // Paint.ascent is negative, so negate it
	        descent = (int) Math.ceil(textPaint.descent());
	        measuredTextWidth = (int) Math.ceil(textPaint.measureText(text));

	        int textWidth = measuredTextWidth;
	        int textHeight = ascent + descent;
	        
	        if(dimensions == null || dimensions.width == 0 || dimensions.height == 0){
	        	dimensions = CCSize.make(measuredTextWidth, ascent + descent);
	        }
	        
	        int width = (int) dimensions.width;
	        if ((width != 1) && (width & (width - 1)) != 0) {
	            int i = 1;
	            while (i < width)
	                i *= 2;
	            width = i;
	        }
	        int height = (int) dimensions.height;
	        if ((height != 1) && (height & (height - 1)) != 0) {
	            int i = 1;
	            while (i < height)
	                i *= 2;
	            height = i;
	        }

	        Bitmap.Config config = Bitmap.Config.ALPHA_8;
	        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
	        Canvas canvas = new Canvas(bitmap);
	        bitmap.eraseColor(0);

	        int centerOffsetHeight = ((int) dimensions.height - textHeight) / 2;
	        int centerOffsetWidth = ((int) dimensions.width - textWidth) / 2;

	        switch (alignment) {
	            case CCLabel.TEXT_ALIGNMENT_LEFT:
	                centerOffsetWidth = 0;
	                break;
	            case CCLabel.TEXT_ALIGNMENT_CENTER:
	                //centerOffsetWidth = (effectiveTextWidth - textWidth) / 2;
	                break;
	            case CCLabel.TEXT_ALIGNMENT_RIGHT:
	                centerOffsetWidth = (int) dimensions.width - textWidth;
	                break;
	        }

	        canvas.drawText(text,
	                centerOffsetWidth,
	                ascent + centerOffsetHeight,
	                textPaint);

	        
	        init(bitmap, dimensions);		
	}
	
	public void drawInRect(GL10 gl, CCRect rect){
		gl.glEnable(GL10.GL_TEXTURE_2D);
		
		loadTexture(gl);
		
		float[] coordinates = {
				0.0f,	_maxT,
				_maxS,	_maxT,
				0.0f,	0.0f,
				_maxS,	0.0f
		};
		mCoordinates.put(coordinates);
		mCoordinates.position(0);

		float[] vertices = {
				rect.origin.x,						rect.origin.y,						0.0f,
				rect.origin.x + rect.size.width,	rect.origin.y,						0.0f,
				rect.origin.x,						rect.origin.y + rect.size.height,	0.0f,
				rect.origin.x + rect.size.width, 	rect.origin.y + rect.size.height,	0.0f
			};
		mVertices.put(vertices);
		mVertices.position(0);

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		gl.glBindTexture(GL10.GL_TEXTURE_2D, _name);
		
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertices);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mCoordinates);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisable(GL10.GL_TEXTURE_2D);			
	}
	
	private CCPoint _point;
	public void drawAtPoint(GL10 gl, CCPoint point) {		
		gl.glEnable(GL10.GL_TEXTURE_2D);
		loadTexture(gl);
		if(_point==null || !CCPoint.equalToPoint(point, _point))
		{
			float width = _size.width;
			float height = _size.height;
			float[] vertices = {
					point.x,			height + point.y,	0.0f,
					point.x,			point.y,			0.0f,
					width + point.x, 	height + point.y,	0.0f,
					width + point.x,	point.y,			0.0f
				};
			mVertices.put(vertices);
			mVertices.position(0);
			_point = point;
		}
		
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		gl.glBindTexture(GL10.GL_TEXTURE_2D, _name);
		
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertices);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mCoordinates);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisable(GL10.GL_TEXTURE_2D);	
	}
	  
	public void loadTexture(GL10 gl) {
	    if (_name == 0) {
	        int[] textures = new int[1];
	        gl.glGenTextures(1, textures, 0);
	        _name = textures[0];
	        gl.glBindTexture(GL10.GL_TEXTURE_2D, _name);
	        applyTexParameters(gl);
	        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mBitmap, 0);
	        if(!_retainBitmap){
		        mBitmap.recycle();
		        mBitmap = null;	        	
	        }
	    }
	}
	
    public void applyTexParameters(GL10 gl) {
        gl.glTexParameterx(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, _texParams.minFilter );
        gl.glTexParameterx(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, _texParams.magFilter);
        gl.glTexParameterx(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, _texParams.wrapS);
        gl.glTexParameterx(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, _texParams.wrapT);
    }
    
	public void release() {
		if(CCTextureCache.sharedTextureCache().contains(this)){
			CCTextureCache.sharedTextureCache().release(this);
		}else{
			if(_retainBitmap)
				mBitmap.recycle();
			if(_name > 0){
				int[] textures = {_name};
				GL10 gl = CCTextureCache.sharedTextureCache().currentGL();
				if(gl != null)
					gl.glDeleteTextures(1, textures, 0);
				_name = -1;
		   }
	   }	
	}
	
	int _count;

	String _key;
}
