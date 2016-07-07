package com.moandroid.cocos2d.texture;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.microedition.khronos.opengles.GL10;

import com.moandroid.cocos2d.renderers.CCDirector;
import com.moandroid.cocos2d.types.CCSize;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class CCTextureCache {

	public static final String LOG_TAG = CCTextureCache.class.getSimpleName();
	public static int  kCleanTexturesLimit= 30;
	public static final int kToExpandCapacity = 10;
	
	private static CCTextureCache _sharedTextureCache;
	public static CCTextureCache sharedTextureCache() {
		if(_sharedTextureCache == null){
			_sharedTextureCache = new CCTextureCache();
		}else{
			if(_sharedTextureCache._textures.size() > _sharedTextureCache._cleanTexturesLimit)
				_sharedTextureCache.removeUnusedTextures();
			if(_sharedTextureCache._textures.size() > _sharedTextureCache._cleanTexturesLimit){
				_sharedTextureCache._cleanTexturesLimit += 
					((_sharedTextureCache._textures.size() - _sharedTextureCache._cleanTexturesLimit) / kToExpandCapacity + 1) *
						kToExpandCapacity;
			}else if(_sharedTextureCache._textures.size() > _sharedTextureCache._cleanTexturesLimit - kToExpandCapacity/2){
				_sharedTextureCache._cleanTexturesLimit += kToExpandCapacity;
			}
		}
		return _sharedTextureCache;
	}
	
	private int _cleanTexturesLimit = kCleanTexturesLimit;
	public static void purgeSharedTextureCache() {
		if(_sharedTextureCache != null){
			_sharedTextureCache.removeAllTextures();
			_sharedTextureCache._textures = null;
			_sharedTextureCache = null;
		}
	}
	
	protected CCTextureCache(){
//		synchronized (CCTextureCache.class) {
			_textures = new HashMap<String, CCTexture2D>(10);
//		}
	}
		
	private HashMap<String, CCTexture2D> _textures;
	
	public CCTexture2D getTexture(String fileName) {
		if(_textures.containsKey(fileName)){// 先缓存中取
			CCTexture2D tx = _textures.get(fileName);
			++tx._count;
			return tx;
		}else{
			try {
				InputStream is = CCDirector.sharedDirector().context().getAssets().open(fileName);		
				Bitmap bmp  = BitmapFactory.decodeStream(is);
		        //Bitmap bmp = Bitmap.createBitmap(128, 128,
		                //Bitmap.Config.ARGB_8888 );
		        //Canvas canvas = new Canvas(bmp);
		        //canvas.drawColor(0xFFFFFFFF);
				CCTexture2D tx = new CCTexture2D(bmp);
				++tx._count;
				_textures.put(fileName, tx);//缓存
				return tx;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
	}
	
	public CCTexture2D getTexture(Bitmap bmp){
		String tagID = String.format("%d", System.currentTimeMillis());
		CCTexture2D tx = new CCTexture2D(bmp);
		++tx._count;
		tx._retainBitmap = true;
		tx._key = tagID;
		_textures.put(tagID, tx);
		return tx;	
	}

	public CCTexture2D getTexture(String text, CCSize dimensions,
			int alignment, String fontName, int fontSize){
		String tagID = String.format("%d", System.currentTimeMillis());
		CCTexture2D tx = new CCTexture2D(text,dimensions,alignment,fontName,fontSize);
		++tx._count;
		tx._retainBitmap = true;
		tx._key = tagID;
		_textures.put(tagID, tx);
		return tx;
	}
	
	private void removeAllTextures() {
		Iterator<Entry<String, CCTexture2D>> iter = _textures.entrySet().iterator();
		int size = _textures.size();
		int[] names = new int[size];
		int i = 0;
		while (iter.hasNext()) {
			Entry<String, CCTexture2D> entry = iter.next();
		    CCTexture2D tx = entry.getValue();
		    if(tx._name > 0){
		    	names[i] = tx._name;
		    	tx._name = -1;
		    	++i;
		    }else if(tx._retainBitmap && tx.mBitmap != null){
		    	tx.mBitmap.recycle();
		    }
		} 
		if(_gl != null)
			_gl.glDeleteTextures(i, names, 0);
		_textures.clear();
	}

	public void removeUnusedTextures(){
		Iterator<Entry<String, CCTexture2D>> iter = _textures.entrySet().iterator();
		int size = _textures.size();
		int[] names = new int[size];
		String[] keys = new String[size];
		int i = 0;
		while (iter.hasNext()) {
			Entry<String, CCTexture2D> entry = iter.next();
		    CCTexture2D tx = entry.getValue();
		    String key = entry.getKey();
		    if(tx._count <= 1){
			    names[i] = tx._name;
			    keys[i] = key;
			    tx._name = -1;
			    ++i;
		    }
		} 
		if(i > 0)
			_gl.glDeleteTextures(i, names, 0);
		for(int j=0;j<i;++j)
			_textures.remove(keys[j]);
	}
		
	private GL10 _gl;
	public GL10 currentGL() {
		return _gl;
	}
	public void setCurrentGL(GL10 gl){
		_gl = gl;
	}

//	public void saveTextures() {
//		Iterator<Entry<String, CCTexture2D>> iter = _textures.entrySet().iterator();
//		int size = _textures.size();
//		int[] names = new int[size];
//		String[] keys = new String[size];
//		int i = 0;
//		int j = 0;
//		while (iter.hasNext()) {
//			Entry<String, CCTexture2D> entry = iter.next();
//		    CCTexture2D tx = entry.getValue();
//		    String key = entry.getKey();
//		    if(tx._name>0){
//		    	names[j] = tx._name; 
//		    	++j;
//		    }
//		    if(tx._count <= 1){
//			    keys[i] = key;
//			    tx._name = -1;
//			    ++i;
//		    }
//		}
//		if(j>0)
//			_gl.glDeleteTextures(j, names, 0);
//		for(j=0;j<i;++j)
//			_textures.remove(keys[j]);
//	}

	public void restoreTextures() {
		Iterator<Entry<String, CCTexture2D>> iter = _textures.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, CCTexture2D> entry = iter.next();
		    CCTexture2D tx = entry.getValue();
		    String key = entry.getKey();
		    tx._name = 0;
		    if(!tx._retainBitmap){
				try {
					if(tx.mBitmap!=null) continue;
					InputStream is = CCDirector.sharedDirector().context().getAssets().open(key);		
					Bitmap bmp = BitmapFactory.decodeStream(is);
					tx.resetBitmap(bmp);
				} catch (IOException e) {
					e.printStackTrace();					
				}
		    }
		} 
	}
	
	public boolean contains(CCTexture2D tx) {
		return _textures.containsValue(tx);
	}

	public void retain(CCTexture2D tx) {
		++tx._count;
	}
	
	public void release(CCTexture2D tx) {
		--tx._count;
		if(tx._count <= 0 && tx._retainBitmap){
			if(tx._name > 0){
				int[] textures = {tx._name};
				if(_gl != null)
					_gl.glDeleteTextures(1, textures, 0);
				tx._name = -1;	
			}
			tx.mBitmap.recycle();
			removeBitmapTexture(tx);
		}
	}

	void removeBitmapTexture(CCTexture2D tx) {
		if(tx._key!=null)
			_textures.remove(tx._key);
	}
}
