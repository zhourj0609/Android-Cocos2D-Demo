package com.moandroid.cocos2d.renderers;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import junit.framework.Assert;

import com.moandroid.cocos2d.actions.CCActionManager;

import com.moandroid.cocos2d.events.CCAccelerometerDispatcher;
import com.moandroid.cocos2d.events.CCEventManager;
import com.moandroid.cocos2d.events.CCTouchDispatcher;
import com.moandroid.cocos2d.events.CCTouchInputProtocol;

import com.moandroid.cocos2d.nodes.CCLabelAtlas;
import com.moandroid.cocos2d.nodes.scenes.CCScene;
import com.moandroid.cocos2d.nodes.scenes.CCSceneManager;

import com.moandroid.cocos2d.texture.CCTexture2D;
import com.moandroid.cocos2d.texture.CCTextureCache;

import com.moandroid.cocos2d.types.CCPoint;
import com.moandroid.cocos2d.types.CCSize;

import com.moandroid.cocos2d.util.CCConfig;
import com.moandroid.cocos2d.util.CCFormatter;
import com.moandroid.cocos2d.util.CCProfiler;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.Log;
import android.view.WindowManager;

public abstract class CCDirector implements GLSurfaceView.Renderer{
    public static final String LOG_TAG = CCDirector.class.getSimpleName();

    public static final int kCCDirectorProjection2D = 1;
    public static final int kCCDirectorProjection3D = 2;
    public static final int kCCDirectorProjectionCustom = 3;

    public static final int kCCDeviceOrientationPortrait = 1;
    public static final int kCCDeviceOrientationPortraitUpsideDown = 2;
    public static final int kCCDeviceOrientationLandscapeLeft = 3;
    public static final int kCCDeviceOrientationLandscapeRight = 4;

    protected static CCDirector _sharedDirector;

    public static CCDirector sharedDirector() {
        Assert.assertTrue("You have not create an instance of CCDirector!", _sharedDirector != null);
        return _sharedDirector;
    }

    private static boolean _purged;
    protected CCDirector(Context context){
//    	synchronized(CCDirector.class){
        _purged = false;
        _context = context;
        _deviceOrientation = kCCDeviceOrientationLandscapeRight;
        _displayFPS = CCConfig.CC_DIRECTOR_SHOW_FPS;
        _labelFPS = null;
        _texFPS = null;
        _nextDeltaTimeZero = true;
        WindowManager wm = (WindowManager) _context.getSystemService(Context.WINDOW_SERVICE);
        _width = wm.getDefaultDisplay().getWidth();
        _height = wm.getDefaultDisplay().getHeight();
        CCEventManager.sharedManager();
//    	}
    }

    public synchronized void end(){
        if(CCConfig.CC_DIRECTOR_FAST_FPS && _labelFPS != null){
            _labelFPS.cleanup();
            _labelFPS = null;
        }else if(_texFPS != null){
            _texFPS.release();
            _texFPS = null;
        }

        CCTouchDispatcher.purgeSharedDispatcher();
        CCAccelerometerDispatcher.purgeSharedDispatcher();
        CCEventManager.purgeSharedManager();
        CCSceneManager.purgeSharedSceneManager();
        CCActionManager.purgeSharedManager();
        CCScheduler.purgeSharedScheduler();
        CCTextureCache.purgeSharedTextureCache();

        _input.setTouchDispatcher(null);
        _glView = null;
        _context = null;
        _sharedDirector = null;
        _purged = true;
    }

    private boolean _nextDeltaTimeZero;
    public void setNextDeltaTimeZero(boolean b){
        _nextDeltaTimeZero = b;
    }

    public boolean nextDeltaTimeZero(){
        return _nextDeltaTimeZero;
    }

    private float _dt;
    private long _lastUpdata;
    protected void calculateDeltaTime(){
        long now = System.currentTimeMillis();
        long dt;
        dt = now - _lastUpdata;
        if(_nextDeltaTimeZero){
            _dt = 0;
            _nextDeltaTimeZero = false;
        }else{
            _dt = dt / 1000.0f;

            _dt = Math.max(0, _dt);
        }
        _lastUpdata = now;
    }

    private int _width;
    private int _height;
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        _width  = width;
        _height = height;
        gl.glViewport(0, 0, width, height);
    }
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        CCTextureCache.sharedTextureCache().setCurrentGL(gl);
        CCTextureCache.sharedTextureCache().restoreTextures();

        initGLDefaultValues(gl);
    }

    private float _step;
    @Override
    public final synchronized void onDrawFrame(GL10 gl) {

        //TODO 这里添加暂停sleep减少帧数
        if(_purged)	return;
        calculateDeltaTime();
        _step += _dt;
        if (!_isPaused && _step > CCConfig.CC_DIRECTOR_UPDATE_DELAY){
            CCScheduler.sharedScheduler().tick(_step);
            _step = 0;
        }
        CCSceneManager.shareManager().runNextScene();
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glPushMatrix();
        applyLandscape(gl);

        beginRender(gl);
        CCSceneManager.shareManager().visitScene(gl);
        endRender(gl);

        if(displayFPS()){
            showFPS(gl);
        }

        if(CCConfig.CC_DIRECTOR_ENABLE_PROFILERS)
            showProfilers(gl);
        gl.glPopMatrix();
        gl.glDisable(GL10.GL_TEXTURE_2D);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }

    public void onSurfaceDestroyed() {
        CCTextureCache.sharedTextureCache().setCurrentGL(null);
    }

    protected abstract void  beginRender(GL10 gl);
    protected abstract void  endRender(GL10 gl);

    protected abstract void initGLDefaultValues(GL10 gl);

    private int _projection;

    public int projection(){
        return _projection;
    }
    public void setProjection(GL10 gl, int pro){
        switch(pro){
            case kCCDirectorProjection2D:{
                gl.glMatrixMode(GL10.GL_PROJECTION);
                gl.glLoadIdentity();
                gl.glOrthof(0, _width, 0, _height, -1000, 1000);//设置正交投影
                gl.glMatrixMode(GL10.GL_MODELVIEW);
                gl.glLoadIdentity();
            }
            break;
            case kCCDirectorProjection3D:{
                gl.glMatrixMode(GL10.GL_PROJECTION);
                gl.glLoadIdentity();
                GLU.gluPerspective(gl, 60, (float)_width/(float)_height, 0.5f, 1500.0f);
                gl.glMatrixMode(GL10.GL_MODELVIEW);
                gl.glLoadIdentity();
                GLU.gluLookAt(gl, _width/2,	_height/2,	getZEye(),
                        _width/2,	_height/2,	0.0f,
                        0.0f,		1.0f,		0.0f);
            }
            break;
            case kCCDirectorProjectionCustom:{
            }
            break;
            default:{
                Log.v(LOG_TAG, "cocos2d: Director: unrecognized projection");
            }
            break;
        }
        _projection = pro;
    }

    public void setAlphaBlending(GL10 gl, boolean on){
        if(on){
            gl.glEnable(GL10.GL_BLEND);
            gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
        }else{
            gl.glDisable(GL10.GL_BLEND);
        }
    }

    public void setDepthTest(GL10 gl, boolean on){
        if(on){
            gl.glClearDepthf(1.0f);
            gl.glEnable(GL10.GL_DEPTH_TEST);
            gl.glDepthFunc(GL10.GL_LEQUAL);
            gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
        }else{
            gl.glDisable(GL10.GL_DEPTH_TEST);
        }
    }

    private float getZEye() {
        return _height/1.1566f;
    }

    public CCPoint convertToGL(CCPoint pt){
        float newY = _height - pt.y;
        float newX = _width - pt.x;
        CCPoint ret;
        switch(_deviceOrientation){
            case kCCDeviceOrientationPortrait:{
                ret = CCPoint.ccp(pt.x, newY);
            }
            break;
            case kCCDeviceOrientationPortraitUpsideDown:{
                ret = CCPoint.ccp(newX, pt.y);
            }
            break;
            case kCCDeviceOrientationLandscapeLeft:{
                ret = CCPoint.ccp(pt.x, pt.y);
            }
            break;
            case kCCDeviceOrientationLandscapeRight:{
                ret = CCPoint.ccp(newX, newY);
            }
            break;
            default:{
                ret = null;
                Log.v(LOG_TAG,"Unknown device Orientation");
            }
        }
        return ret;
    }

    public CCPoint convertToUI(CCPoint pt){
        int oppositeX = (int) (_width - pt.x);
        int oppositeY = (int) (_height - pt.y);
        CCPoint uiPoint;
        switch(_deviceOrientation){
            case kCCDeviceOrientationPortrait:
                uiPoint = CCPoint.ccp(pt.x, pt.y);
                break;
            case kCCDeviceOrientationPortraitUpsideDown:
                uiPoint = CCPoint.ccp(oppositeX, oppositeY);
                break;
            case kCCDeviceOrientationLandscapeLeft:
                uiPoint = CCPoint.ccp(pt.y, oppositeX);
                break;
            case kCCDeviceOrientationLandscapeRight:
                uiPoint = CCPoint.ccp(oppositeY, pt.x);
                break;
            default:
                uiPoint = null;
                Log.v(LOG_TAG,"Unknown device Orientation");
        }
        return uiPoint;
    }

    public CCSize winSize(){
        switch(_deviceOrientation){
            case kCCDeviceOrientationLandscapeLeft:
            case kCCDeviceOrientationLandscapeRight:{
                return CCSize.make(_height, _width);
            }
            default:
                return CCSize.make(_width, _height);
        }
    }

    protected void applyLandscape(GL10 gl){
        float w = _width / 2;
        float h = _height / 2;
        switch(_deviceOrientation){
            case kCCDeviceOrientationPortrait:
                break;
            case kCCDeviceOrientationPortraitUpsideDown:// 竖屏
                gl.glTranslatef(w, h, 0);// 移动坐标系到屏幕中心
                gl.glRotatef(180, 0, 0, 1);// 绕旋转180度
                gl.glTranslatef(-w, -h, 0);//将世界坐标系设置在左下角（0，0）
                break;
            case kCCDeviceOrientationLandscapeLeft:// 横屏 left
                gl.glTranslatef(w, h, 0);
                gl.glRotatef(90, 0, 0, 1);
                gl.glTranslatef(-h, -w, 0);
                break;
            case kCCDeviceOrientationLandscapeRight:// 横屏 right
                gl.glTranslatef(w, h, 0);
                gl.glRotatef(-90, 0, 0, 1);
                gl.glTranslatef(-h, -w, 0);
                break;
            default:
                Log.v(LOG_TAG,"Unknown device Orientation");
        }
    }

    private int _frames;
    private float accumDt;
    private float frameRate;
    private CCLabelAtlas _labelFPS;
    private CCTexture2D _texFPS;

    protected void showFPS(GL10 gl){
        ++_frames;
        accumDt += _dt;
        if(CCConfig.CC_DIRECTOR_FAST_FPS){
            if(_labelFPS == null){
                _labelFPS = new CCLabelAtlas("00.0", "fps_images.png", 16, 24, '.');
            }
            if(accumDt > CCConfig.CC_DIRECTOR_FPS_INTERVAL){
                frameRate = _frames/accumDt;
                _frames = 0;
                accumDt = 0;
                _labelFPS.setString(CCFormatter.format("%.1f", frameRate));
            }
            _labelFPS.draw(gl);
        }
        else {
            if(accumDt > CCConfig.CC_DIRECTOR_FPS_INTERVAL || _texFPS == null){
                frameRate = _frames/accumDt;
                _frames = 0;
                accumDt = 0;
                if(_texFPS != null)
                    _texFPS.release();
                _texFPS = new CCTexture2D(CCFormatter.format("%.2f", frameRate), "Arial", 24);
            }

            gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
            gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
            gl.glColor4f(224.0f/255.0f, 224.0f/255.0f, 224.0f/255.0f, 200.0f/255.0f);
            _texFPS.drawAtPoint(gl, CCPoint.ccp(0,0));
            gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
            gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        }
    }


    private float accumDtForProfiler;
    protected void showProfilers(GL10 gl) {
        accumDtForProfiler += _dt;
        if(accumDtForProfiler > 1.0f){
            accumDtForProfiler = 0;
            CCProfiler.sharedProfiler().displayTimers();
        }
    }

    private boolean _displayFPS;

    public void setEnableDisplayFPS(boolean toShow){
        _displayFPS = toShow;
    }

    public boolean displayFPS(){
        return _displayFPS;
    }

    private int _deviceOrientation = CCDirector.kCCDeviceOrientationPortrait;//默认为竖屏

    public int deviceOrientation(){
        return _deviceOrientation;
    }

    public void setDeviceOrientation(int ori){
        _deviceOrientation = ori;
    }

    private boolean _isPaused;
    public boolean IsPaused(){
        return _isPaused;
    }

    public void setPaused(boolean paused){
        _isPaused = paused;
    }

    public void runWithScene(CCScene scene){
        CCSceneManager.shareManager().runWithScene(scene);
    }

    public void replaceScene(CCScene scene){
        CCSceneManager.shareManager().replaceScene(scene);
    }

    public void pushScene(CCScene scene){
        CCSceneManager.shareManager().pushScene(scene);
    }

    public void popScene(CCScene scene){
        CCSceneManager.shareManager().popScene(scene);
    }

    public void onSceneIsEmpty() {
        end();
    }

    public void pause(){
        if(_isPaused){
            return;
        }
        CCEventManager.pause();
        _isPaused = true;
        if(_glView!=null)
            _glView.onPause();
    }

    public void resume(){
        if(!_isPaused){
            return;
        }
        CCEventManager.resume();
        _isPaused = false;
        _nextDeltaTimeZero = true;
        if(_glView!=null)
            _glView.onResume();
    }

    private Context _context;
    public Context context() {
        return _context;
    }
    public void setContext(Context cx) {
        _context = cx;
    }

    private CCTouchInputProtocol _input;
    public void setTouchInput(CCTouchInputProtocol input) {
        _input = input;
        input.setTouchDispatcher(CCTouchDispatcher.sharedDispatcher());
    }

    public CCPoint convertCoordinate(float x, float y) {
        return convertToGL(x,y);
    }

    public CCPoint convertToGL(float uiPointX, float uiPointY) {

        switch ( _deviceOrientation) {
            case kCCDeviceOrientationPortrait:
                return CCPoint.ccp(uiPointX, _height - uiPointY);

            case kCCDeviceOrientationPortraitUpsideDown:
                return CCPoint.ccp(_width - uiPointX, uiPointY);

            case kCCDeviceOrientationLandscapeLeft:
                return CCPoint.ccp( _height - uiPointY, _width - uiPointX);

            case kCCDeviceOrientationLandscapeRight:
                return CCPoint.ccp(uiPointY, uiPointX);
        }
        return null;
    }

    private GLSurfaceView _glView;
    public void setGLView(GLSurfaceView surfaceView) {
        _glView = surfaceView;
    }

    public CCPoint convertAccelerometer(float accelX, float accelY) {
        switch ( _deviceOrientation) {
            case kCCDeviceOrientationPortrait:
                return CCPoint.ccp(accelX, accelY);

            case kCCDeviceOrientationPortraitUpsideDown:
                return CCPoint.ccp(-accelX, -accelY);

            case kCCDeviceOrientationLandscapeLeft:
                return CCPoint.ccp( accelY, -accelX);

            case kCCDeviceOrientationLandscapeRight:
                return CCPoint.ccp(-accelY, accelX);
        }
        return null;
    }
}
