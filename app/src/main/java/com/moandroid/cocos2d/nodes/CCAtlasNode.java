package com.moandroid.cocos2d.nodes;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import junit.framework.Assert;


import com.moandroid.cocos2d.nodes.protocol.CCRGBAProtocol;
import com.moandroid.cocos2d.nodes.protocol.CCTextureProtocol;
import com.moandroid.cocos2d.texture.CCTexture2D;
import com.moandroid.cocos2d.texture.CCTextureAtlas;

import com.moandroid.cocos2d.types.CCBlendFunc;
import com.moandroid.cocos2d.types.CCColor3B;
import com.moandroid.cocos2d.types.CCQuad2F;
import com.moandroid.cocos2d.types.CCQuad3F;
import com.moandroid.cocos2d.types.CCQuad4B;
import com.moandroid.cocos2d.util.CCFormatter;
import com.moandroid.cocos2d.util.CCUtils;

public class CCAtlasNode extends CCNode 
						 implements CCRGBAProtocol, 
						 			CCTextureProtocol{

	public static final String LOG_TAG = CCAtlasNode.class.getSimpleName();
	
	public static CCAtlasNode atlas(String atlasFile, int itemWidth, int itemHeight, int itemsCount){
		return new CCAtlasNode(atlasFile, itemWidth, itemHeight, itemsCount);
	}
	
    private FloatBuffer _textureCoordinates;
    private FloatBuffer _vertexCoordinates;
	private ByteBuffer _colors;
	private ShortBuffer _indices;
	
	protected CCAtlasNode(String atlasFile, int itemWidth, int itemHeight,
			int itemsCount) {
		
		_itemWidth = itemWidth;
		_itemHeight = itemHeight;

		_textureAtlas = new CCTextureAtlas(atlasFile);
		_capacity = itemsCount;
		_withColorArray = false;
        ByteBuffer tbb;
        ByteBuffer vbb;
        ByteBuffer isb;
    
	    tbb = ByteBuffer.allocateDirect(4 * CCQuad2F.size * _capacity);
	    vbb = ByteBuffer.allocateDirect(4 * CCQuad3F.size * _capacity);
	    isb = ByteBuffer.allocateDirect(2 * 6 * _capacity);
      
        tbb.order(ByteOrder.nativeOrder());
        _textureCoordinates = tbb.asFloatBuffer();
        vbb.order(ByteOrder.nativeOrder());
        _vertexCoordinates = vbb.asFloatBuffer();
        isb.order(ByteOrder.nativeOrder());
        _indices = isb.asShortBuffer();
        initIndices();
    
        _blendFunc = new CCBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		_opacity = (byte)255;
		_color = CCUtils.ccWHITE;
		
		//_colorUnmodified = CCUtils.ccWHITE;
		//_opacityModifyRGB = true;
		//updateBlendFunc();
		//updateOpacityModifyRGB();
		calculateMaxItems();
		calculateTexCoordsSteps();
	}

	private CCTextureAtlas _textureAtlas;
	public CCTextureAtlas textureAtlas() {
		return _textureAtlas;
	}
	public void setTextureAtlas(CCTextureAtlas atlas) {
		_textureAtlas = atlas;
	}

	public void initIndices() {
        for (int i = 0; i < _capacity; i++) {
        	_indices.put((short) (i * 6 + 0), (short) (i * 4 + 0));
        	_indices.put((short) (i * 6 + 1), (short) (i * 4 + 2));
        	_indices.put((short) (i * 6 + 2), (short) (i * 4 + 1));
        	_indices.put((short) (i * 6 + 3), (short) (i * 4 + 1));
        	_indices.put((short) (i * 6 + 4), (short) (i * 4 + 2));
        	_indices.put((short) (i * 6 + 5), (short) (i * 4 + 3));
        }
		_indices.position(0);
	}

    public void initColorArray(){
    	if(!_withColorArray){
    		ByteBuffer cbb;

    		cbb = ByteBuffer.allocateDirect(1 * CCQuad4B.size * _capacity);	

    		cbb.order(ByteOrder.nativeOrder());
    		for(int i = 0; i< CCQuad4B.size * _capacity; ++i){
    			cbb.put((byte)0xFF);
    		}
    		cbb.position(0);
    		_colors = cbb;
    		_withColorArray = true;
    	}
    }
    public void updateQuad(CCQuad2F quadT, CCQuad3F quadV, int index) {
    	Assert.assertTrue("update quad with texture_: Invalid index", (index >= 0 && index < _capacity));
    	
        _totalQuads = Math.max(index + 1, _totalQuads);

        _textureCoordinates.position(index * CCQuad2F.size);;
        _textureCoordinates.put(quadT.getFloats(), 0, CCQuad2F.size);
        _textureCoordinates.position(0);
       //putBuffer(_textureCoordinates, quadT.getFloats(), index);
        _vertexCoordinates.position(index * CCQuad3F.size);
        _vertexCoordinates.put(quadV.getFloats(), 0, CCQuad3F.size);
        _vertexCoordinates.position(0);
       //putBuffer(_vertexCoordinates, quadV.getFloats(), index);
    }
    
    public void updateColor(CCQuad4B quadC, int index) {
    	Assert.assertTrue("update color with quad color: Invalid index", (index >= 0 && index < _capacity));

        _totalQuads = Math.max(index + 1, _totalQuads);

        if (!_withColorArray)
            initColorArray();

        if (_withColorArray){
        	_colors.position(index * CCQuad4B.size);
        	_colors.put(quadC.getBytes(), 0, CCQuad4B.size);
        	_colors.position(0);
            //putBuffer(_colors, quadC.getBytes(), index);
        }
    }
    
    public void insertQuad(CCQuad2F quadT, CCQuad3F quadV, int index){
    	Assert.assertTrue("insert quad with texture_: Invalid index", (index >= 0 && index < _capacity));
        ++_totalQuads;
        int remaining = (_totalQuads - 1) - index;

        if (remaining > 0) {
        	CCUtils.memmove(_textureCoordinates, index * CCQuad2F.size, _textureCoordinates, (index + 1) * CCQuad2F.size, remaining * CCQuad2F.size);
        	CCUtils.memmove(_vertexCoordinates, index * CCQuad3F.size, _vertexCoordinates, (index + 1) * CCQuad3F.size, remaining * CCQuad3F.size);
            if (_withColorArray) {
            	CCUtils.memmove(_colors, index * CCQuad4B.size, _colors, (index + 1) * CCQuad4B.size, remaining * CCQuad4B.size);
            }
        }
        _textureCoordinates.position(index * CCQuad2F.size);
        _textureCoordinates.put(quadT.getFloats(), 0, CCQuad2F.size);
        _textureCoordinates.position(0);
        //putBuffer(_textureCoordinates, quadT.getFloats(), index);
        _vertexCoordinates.position(index * CCQuad3F.size);
        _vertexCoordinates.put(quadV.getFloats(), 0, CCQuad3F.size);
        _vertexCoordinates.position(0);
        //putBuffer(_vertexCoordinates, quadV.getFloats(), index);
    }
    
    public void insertQuad(int from, int to) {
    	Assert.assertTrue("insertQuadFromIndex:atIndex: Invalid index", (to >= 0 && to < _totalQuads));
    	Assert.assertTrue("insertQuadFromIndex:atIndex: Invalid index", (from >= 0 && from < _totalQuads));

        if (from == to)
            return;

        int size = Math.abs(from - to);
        int dst = from + 1;
        int src = from;

        if (from > to) {
            dst = to + 1;
            src = to;
        }

        // tex coordinates
        float[] texCoordsBackup;
        //texCoordsBackup = getBuffer(_textureCoordinates, from, CCQuad2F.size);
        texCoordsBackup = new float[CCQuad2F.size];
        _textureCoordinates.get(texCoordsBackup, from * CCQuad2F.size, CCQuad2F.size);
        CCUtils.memmove(_textureCoordinates, src * CCQuad2F.size, _textureCoordinates, dst * CCQuad2F.size, size * CCQuad2F.size);
        //putBuffer(_textureCoordinates, texCoordsBackup, to);
        _textureCoordinates.position(to * CCQuad2F.size);
        _textureCoordinates.put(texCoordsBackup, 0, CCQuad2F.size);
        _textureCoordinates.position(0);
        // vertexCoordinates_ coordinates
        float[] vertexQuadBackup;
        //vertexQuadBackup = getBuffer(_vertexCoordinates, from, CCQuad3F.size);
        vertexQuadBackup = new float[CCQuad3F.size];
        _vertexCoordinates.get(vertexQuadBackup, from * CCQuad3F.size,  CCQuad3F.size);
        CCUtils.memmove(_vertexCoordinates, src  * CCQuad3F.size, _vertexCoordinates, dst  * CCQuad3F.size, size  * CCQuad3F.size);
        //putBuffer(_vertexCoordinates, vertexQuadBackup, to);
        _vertexCoordinates.position( to * CCQuad3F.size);
        _vertexCoordinates.put(vertexQuadBackup, 0, CCQuad3F.size);
        _vertexCoordinates.position(0);
        // colors_
        if (_withColorArray) {
            byte[] colorsBackup;
           // colorsBackup = getBuffer(_colors, from, CCQuad4B.size);
            colorsBackup = new byte[CCQuad4B.size];
            _colors.get(colorsBackup, from*CCQuad4B.size, CCQuad4B.size);
            CCUtils.memmove(_colors, src * CCQuad4B.size, _colors, dst * CCQuad4B.size, size * CCQuad4B.size);
            //putBuffer(_colors, colorsBackup, to);
            _colors.put(colorsBackup, to * CCQuad4B.size, CCQuad4B.size);
            _colors.position(0);
        }
    }
    
    public void removeAllQuads() {
        _totalQuads = 0;
    }
 
    public void resizeCapacity(int newCapacity) {
        if (newCapacity == _capacity)
            return;
        
        // update capacity and getTotalQuads
        _totalQuads = Math.min(_totalQuads, newCapacity);
        
        if(newCapacity < _capacity){
        	_capacity = newCapacity;
        	return;
        }
       
        _capacity = newCapacity;

        ByteBuffer tbb = null;
        ByteBuffer vbb = null;
        ByteBuffer isb = null;
        ByteBuffer cbb = null;
        

    	tbb = ByteBuffer.allocateDirect(4 * CCQuad2F.size * newCapacity);
    	vbb = ByteBuffer.allocateDirect(4 * CCQuad3F.size * newCapacity);
		isb = ByteBuffer.allocateDirect(2 * 6 * newCapacity);
        if (_withColorArray) 
            cbb = ByteBuffer.allocateDirect(1 * CCQuad4B.size * newCapacity);

        
        tbb.order(ByteOrder.nativeOrder());
        FloatBuffer tmpTexCoords = tbb.asFloatBuffer();
        tmpTexCoords.put(_textureCoordinates);
        tmpTexCoords.position(0);
        _textureCoordinates = tmpTexCoords;

       
        vbb.order(ByteOrder.nativeOrder());  
        FloatBuffer tmpVertexCoords = vbb.asFloatBuffer();
        tmpVertexCoords.put(_vertexCoordinates);
        tmpVertexCoords.position(0);
        _vertexCoordinates = tmpVertexCoords;

        
        isb.order(ByteOrder.nativeOrder());
        ShortBuffer tmpIndices = isb.asShortBuffer();
        tmpIndices.put(_indices);
        tmpIndices.position(0);
        _indices = tmpIndices;

        initIndices();

        if (_withColorArray) {
            ByteBuffer tmpColors = cbb;
            tmpColors.put(_colors);
            tmpColors.position(0);
            _colors = tmpColors;
        }
    }
    
	public void draw(GL10 gl){
		draw(gl, _totalQuads);
	}
	
	public void draw(GL10 gl, int capacity){
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glEnable(GL10.GL_TEXTURE_2D);

		gl.glColor4f((_color.r&0xFF)/255f, (_color.r&0xFF)/255f, (_color.r&0xFF)/255f, (_color.r&0xFF)/255f);
		
		boolean newBlend = false;
		if( _blendFunc.src != GL10.GL_ONE ||
			_blendFunc.dst != GL10.GL_ONE_MINUS_DST_ALPHA){
			newBlend = true;
			gl.glBlendFunc(_blendFunc.src, _blendFunc.dst);
		}
		
		_textureAtlas.bindTexture(gl);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);
        
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, _vertexCoordinates);
	
        
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, _textureCoordinates);
			
        if (_withColorArray)
            gl.glColorPointer(4, GL10.GL_UNSIGNED_BYTE, 0, _colors);
        
        	gl.glDrawElements(GL10.GL_TRIANGLES, capacity * 6, GL10.GL_UNSIGNED_SHORT, _indices);
		
		if(newBlend){
			gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_DST_ALPHA);
		}
					 
        gl.glDisable(GL10.GL_TEXTURE_2D);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
	}
	
	private int _itemsPerRow;
	public int itemsPerRow() {
		return _itemsPerRow;
	}
	public void setItemsPerRow(int perRow) {
		_itemsPerRow = perRow;
	}
	
	private int _itemsPerColumn;	
	public int itemsPerColumn() {
		return _itemsPerColumn;
	}
	public void setItemsPerColumn(int perColumn) {
		_itemsPerColumn = perColumn;
	}

	private float _texStepX;
	public float texStepX() {
		return _texStepX;
	}
	public void setTexStepX(float stepX) {
		_texStepX = stepX;
	}
	
	private float _texStepY;
	public float texStepY() {
		return _texStepY;
	}
	public void setTexStepY(float stepY) {
		_texStepY = stepY;
	}

	private int _itemWidth;
	public int itemWidth() {
		return _itemWidth;
	}
	public void setItemWidth(int width) {
		_itemWidth = width;
	}
	
	private int _itemHeight;	
	public int itemHeight() {
		return _itemHeight;
	}
	public void setItemHeight(int height) {
		_itemHeight = height;
	}
	
	private CCBlendFunc _blendFunc;
	public CCBlendFunc blendFunc() {
		return _blendFunc;
	}
	public void setBlendFunc(CCBlendFunc func) {
		_blendFunc = func;
	}

	private byte _opacity;
	@Override
	public byte opacity() {
		return _opacity;
	}
	@Override
	public void setOpacity(byte opactity) {
		_opacity = opactity;
	}	
	
	private CCColor3B _color = new CCColor3B((byte)255,(byte)255,(byte)255);	
//	private CCColor3B _colorUnmodified;
	@Override
	public CCColor3B color() {
		//if(_opacityModifyRGB){
		//	return _colorUnmodified;
		//}
		return _color;
	}
	@Override
	public void setColor(CCColor3B color) {
//		_colorUnmodified = color;
//		if(_opacityModifyRGB){
//			_color = new CCColor3B( (byte)((color.r&0xFF) * (_opacity&0xFF)/255),
//									(byte)((color.g&0xFF) * (_opacity&0xFF)/255), 
//									(byte)((color.b&0xFF) * (_opacity&0xFF)/255));
//		}else{
			_color.r = color.r;
			_color.g = color.g;
			_color.b = color.b;
//		}
	}
	
//	boolean _opacityModifyRGB;
		
    private int _totalQuads;   
    public int totalQuads() {
        return _totalQuads;
    }
    
    private int _capacity;
    public int capacity() {
        return _capacity;
    }
    private boolean _withColorArray;
    public boolean withColorArray(){
    	return _withColorArray;
    }
    
//	@Override
//	public boolean opacityModifyRGB() {
//		return _opacityModifyRGB;
//	}
//	@Override
//	public void setOpacityModifyRGB(boolean modify) {
//		_opacityModifyRGB = modify;
//	}
	
	public void cleanup(){
		if(_textureAtlas != null){
			_textureAtlas.release();
			_textureAtlas = null;
		}
		super.cleanup();
	}

	private void calculateMaxItems() {
		_itemsPerColumn = 
			(int)(_textureAtlas.texture().getHeight() / _itemHeight);
		_itemsPerRow = 
			(int)(_textureAtlas.texture().getWidth() / _itemWidth);
	}

	private void calculateTexCoordsSteps() {
		_texStepX =
			_itemWidth / (float)_textureAtlas.texture().pixelsWide();
		_texStepY =
			_itemHeight / (float)_textureAtlas.texture().pixelsHigh();		
	}

	public String toString() {
        return CCFormatter.format("<%s = %08X | getTotalQuads = %i>", CCAtlasNode.class, this, _totalQuads);
    }
	
	public void updateAtlasValues(){
		
	}
	@Override
	public void setTexture(CCTexture2D texture) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public CCTexture2D texture() {
		return _textureAtlas.texture();
	}
	
//	public void updateOpacityModifyRGB(){
//	}

//	public void updateBlendFunc() {	
//		_blendFunc.src = GL10.GL_SRC_ALPHA;
//		_blendFunc.dst = GL10.GL_ONE_MINUS_SRC_ALPHA;
//	}
	
	
//  private void putBuffer(FloatBuffer dst, float[] floats, int index) {
//  for (int i = 0; i < floats.length; i++) {
//      dst.put(index * floats.length + i, floats[i]);
//  }
//}
//
//private void putBuffer(ByteBuffer dst, byte[] bytes, int index) {
//  for (int i = 0; i < bytes.length; i++) {
//      dst.put(index * bytes.length + i, bytes[i]);
//  }
//}
//
//private float[] getBuffer(FloatBuffer src, int index, int len) {
//  float[] array = new float[len];
//  for (int i = 0; i < len; i++) {
//  	array[i] = src.get(index * len + i);
//  }
//  return array;
//}
//
//private byte[] getBuffer(ByteBuffer src, int index, int len) {
//  byte[] array = new byte[len];
//  for (int i = 0; i < len; i++) {
//  	array[i] = src.get(index * len + i);
//  }
//  return array;
//}	
}
