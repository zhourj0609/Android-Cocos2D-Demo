package com.moandroid.cocos2d.util;

public class CCConfig {
	public static final float 	CC_DIRECTOR_FPS_INTERVAL = 1f;
	public static final boolean CC_DIRECTOR_SHOW_FPS = true;
	public static final boolean CC_DIRECTOR_FAST_FPS = true;
	public static final boolean CC_DIRECTOR_ENABLE_PROFILERS = false;

	public static final boolean CC_SURFACE_USE_GL_DEBUG_WRAPPER = false;
	
	public static final float CC_DIRECTOR_SLOWEST_UPDATE_DELAY = 1f / 24f;
	public static final float CC_DIRECTOR_FASTEST_UPDATE_DELAY = 1f / 60f;
	public static final float CC_DIRECTOR_NORMAL_UPDATE_DELAY  = 1f / 30f;
	public static float CC_DIRECTOR_UPDATE_DELAY  = CC_DIRECTOR_NORMAL_UPDATE_DELAY;

	public static  boolean CC_DIRECTOR_BACKGROUND_TRANSLUCENT = true;
}
