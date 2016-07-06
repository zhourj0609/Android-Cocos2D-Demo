package com.moandroid.cocos2d.util;


import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import com.moandroid.cocos2d.types.*;


public final class CCUtils {
	public static final CCColor3B ccc3(final byte r, final byte g, final byte b){
		return new CCColor3B(r, g, b);
	}
	public static final CCColor3B ccWHITE = new CCColor3B((byte)255, (byte)255, (byte)255);
	public static final CCColor3B ccYELLOW = new CCColor3B((byte)255, (byte)255, (byte)0);
	public static final CCColor3B ccBLUE = new CCColor3B((byte)0, (byte)0, (byte)255);
	public static final CCColor3B ccGREEN = new CCColor3B((byte)0, (byte)255, (byte)0);
	public static final CCColor3B ccRED = new CCColor3B((byte)255, (byte)0, (byte)0);
	public static final CCColor3B ccMAGENTA = new CCColor3B((byte)255, (byte)0, (byte)255);
	public static final CCColor3B ccBLACK = new CCColor3B((byte)0, (byte)0, (byte)0);
	public static final CCColor3B ccORANGE = new CCColor3B((byte)255, (byte)127, (byte)0);
	public static final CCColor3B ccGRAY = new CCColor3B((byte)166, (byte)166, (byte)166);
	public static final int INT_MIN = 0;
	public static final int CC_BLEND_SRC = GL10.GL_ONE;
	public static final int CC_BLEND_DST = GL10.GL_ONE_MINUS_SRC_ALPHA;
	
	public static final CCColor4B ccc4(final byte r, final byte g, final byte b, final byte a){
		return new CCColor4B(r, g, b, a);
	}
	
	public static final CCColor4F ccc4FFromccc3B(CCColor3B c){
		return new CCColor4F((float)c.r/255, (float)c.g/255, (float)c.b/255, 1.f);
	}
	
	public static final CCColor4F ccc4FFromccc4B(CCColor4B c){
		return new CCColor4F((float)c.r/255, (float)c.g/255, (float)c.b/255, (float)c.a/255);
	}
	
	public static final boolean ccc4FEqual(CCColor4F a, CCColor4F b){
		return a.r == b.r && a.g == b.g && a.b == b.b && a.a == b.a;
	}
	
	public static final CCGridSize ccg(final int x, final int y){
		return new CCGridSize(x, y);
	}
	
    public static float degreesToRadians(float angle) {
        return (angle / 180.0f * (float) Math.PI);
    }
    
    public static void memmove(FloatBuffer src, int from, FloatBuffer dst, int to, int size) {
        if (to < from) {
            memcopy(src, from, dst, to, size);
        } else {
            for (int i = size - 1; i >= 0; i--) {
                dst.put(i + to, src.get(i + from));
            }
        }
    }
 
    public static void memmove(ByteBuffer src, int from, ByteBuffer dst, int to, int size) {
        if (to < from) {
            memcopy(src, from, dst, to, size);
        } else {
            for (int i = size - 1; i >= 0; i--) {
                dst.put(i + to, src.get(i + from));
            }
        }
    }
    
    public static void memcopy(FloatBuffer src, int from, FloatBuffer dst, int to, int size) {
        for (int i = 0; i < size; i++) {
            dst.put(i + to, src.get(i + from));
        }
    }
    
    public static void memcopy(ByteBuffer src, int from, ByteBuffer dst, int to, int size) {
        for (int i = 0; i < size; i++) {
            dst.put(i + to, src.get(i + from));
        }
    }

    public static float CC_DEGREES_TO_RADIANS(float angle) {
        return (angle / 180.0f * (float) Math.PI);
    }

    public static float CC_RADIANS_TO_DEGREES(float angle) {
        return (angle / (float) Math.PI * 180.0f);
    }

    public static void CC_SWAP(int x, int y ) {
    	int temp  = x;
		x = y; y = temp;
    }

    public static void CC_SWAP(float x, float y ) {
    	float temp  = x;
		x = y; y = temp;
    } 

	public static float CCRANDOM_0_1() {
		return (float) Math.random();
	}
}
