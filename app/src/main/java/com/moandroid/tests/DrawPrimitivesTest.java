package com.moandroid.tests;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import javax.microedition.khronos.opengles.GL10;

import com.moandroid.cocos2d.actions.interval.CCRepeat;
import com.moandroid.cocos2d.actions.interval.CCRotateBy;
import com.moandroid.cocos2d.nodes.CCLayer;
import com.moandroid.cocos2d.nodes.scenes.CCScene;
import com.moandroid.cocos2d.nodes.ui.CCMenu;
import com.moandroid.cocos2d.nodes.ui.CCMenuItemImage;
import com.moandroid.cocos2d.opengles.CCGLSurfaceView;
import com.moandroid.cocos2d.opengles.CCPrimitives;
import com.moandroid.cocos2d.renderers.CCDirector;
import com.moandroid.cocos2d.renderers.CCDirector2D;
import com.moandroid.cocos2d.types.CCPoint;
import com.moandroid.cocos2d.types.CCSize;
import com.moandroid.cocos2d.util.CCUtils;

public class DrawPrimitivesTest extends Activity {
    public static final String LOG_TAG = DrawPrimitivesTest.class.getSimpleName();
    private CCGLSurfaceView mGLSurfaceView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(	WindowManager.LayoutParams.FLAG_FULLSCREEN, 
								WindowManager.LayoutParams.FLAG_FULLSCREEN);
		mGLSurfaceView = new CCGLSurfaceView(this);
		mGLSurfaceView.setDirector(CCDirector2D.sharedDirector(this));
		setContentView(mGLSurfaceView);	
    }

    @Override
    public void onStart() {
        super.onStart();
        CCScene scene = CCScene.node();
        CCLayer layer = restartAction();
        scene.addChild(layer);
        CCDirector.sharedDirector().runWithScene(scene);
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
        CCDirector2D.sharedDirector().end();
	}

	@Override
	protected void onPause() {
		super.onPause();
		CCDirector.sharedDirector().pause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		CCDirector.sharedDirector().resume();
	}

    static int sceneIdx = 0;
    static Class<?> transitions[] = {
    	Test1.class,
    };

    public static CCLayer nextAction() {

        sceneIdx++;
        sceneIdx = sceneIdx % transitions.length;

        return restartAction();
    }

    public static CCLayer backAction() {
        sceneIdx--;
        int total = transitions.length;
        if (sceneIdx < 0)
            sceneIdx += total;

        return restartAction();
    }

    public static CCLayer restartAction() {
        try {
            Class<?> c = transitions[sceneIdx];
            return (CCLayer) c.newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    public static class TestDemo extends CCLayer {
        public TestDemo() {
            CCSize s = CCDirector.sharedDirector().winSize();

			CCMenuItemImage item1 = CCMenuItemImage.item("b1.png", "b2.png", this, "backCallback");
			CCMenuItemImage item2 = CCMenuItemImage.item("r1.png", "r2.png", this, "restartCallback");
			CCMenuItemImage item3 = CCMenuItemImage.item("f1.png", "f2.png", this, "nextCallback");
			
			CCMenu menu = CCMenu.menu(item1, item2, item3);
			menu.setPosition(CCPoint.zero());
			item1.setPosition(CCPoint.ccp(s.width/2 - 100, 30));
			item2.setPosition(CCPoint.ccp(s.width/2, 30));
			item3.setPosition(CCPoint.ccp(s.width/2 + 100, 30));
			addChild(menu,1);
	        this.runAction(CCRepeat.action(CCRotateBy.action(4, -360),3));
        }

        public void restartCallback() {
            CCScene s = CCScene.node();
            s.addChild(restartAction());
            CCDirector.sharedDirector().replaceScene(s);
        }

        public void nextCallback() {
            CCScene s = CCScene.node();
            s.addChild(nextAction());
            CCDirector.sharedDirector().replaceScene(s);
        }

        public void backCallback() {
            CCScene s = CCScene.node();
            s.addChild(backAction());
            CCDirector.sharedDirector().replaceScene(s);
        }

        String title() {
            return "No title";
        }
    }

    public static class Test1 extends TestDemo {

        public Test1(){
        	
        }
        //
        // TIP:
        // Every CocosNode has a "draw" method.
        // In the "draw" method you put all the code that actually draws your node.
        // And Test1 is a subclass of TestDemo, which is a subclass of Layer, which is a subclass of CocosNode.
        //
        // As you can see the drawing primitives aren't CocosNode objects. They are just helper
        // functions that let's you draw basic things like: points, line, polygons and circles.
        //
        //
        // TIP:
        // Don't draw your stuff outside the "draw" method. Otherwise it won't get transformed.
        //
        //
        // TIP:
        // If you want to rotate/translate/scale a circle or any other "primtive", you can do it by rotating
        // the node. eg:
        //    this.rotation = 90;
        //
        public void draw(GL10 gl) {
        	gl.glDisable(GL10.GL_TEXTURE_2D);
        	gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        	gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
            CCSize s = CCDirector.sharedDirector().winSize();

            
            // draw a simple line
            // The default state is:
            // Line Width: 1
            // color: 255,255,255,255 (white, non-transparent)
            // Anti-Aliased
            gl.glEnable(GL10.GL_LINE_SMOOTH);
            
            CCPrimitives.drawLine(gl, CCPoint.ccp(0, 0), CCPoint.ccp(s.width, s.height));

            // line: color, width, aliased
            // glLineWidth > 1 and GL_LINE_SMOOTH are not compatible
            // GL_SMOOTH_LINE_WIDTH_RANGE = (1,1) on iPhone
            gl.glDisable(GL10.GL_LINE_SMOOTH);
            gl.glLineWidth(5.0f);
            gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
            CCPrimitives.drawLine(gl, CCPoint.ccp(0, s.height), CCPoint.ccp(s.width, 0));

            // TIP:
            // If you are going to use always the same color or width, you don't
            // need to call it before every draw
            //
            // Remember: OpenGL is a state-machine.

            // draw big point in the center
            gl.glPointSize(64);
            gl.glColor4f(0.0f, 0.0f, 1.0f, 0.5f);
            CCPrimitives.drawPoint(gl, s.width / 2, s.height / 2);

            // draw 4 small points
            CCPoint points[] = {CCPoint.ccp(60, 60), CCPoint.ccp(70, 70), CCPoint.ccp(60, 70), CCPoint.ccp(70, 60)};
            gl.glPointSize(4);
            gl.glColor4f(0.0f, 1.0f, 1.0f, 1.0f);
            CCPrimitives.drawPoints(gl, points, 4);

            // draw a green circle with 10 segments
            gl.glLineWidth(16);
            gl.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
            CCPrimitives.drawCircle(gl, s.width / 2, s.height / 2, 100, 0, 10, false);

            // draw a green circle with 50 segments with line to center
            gl.glLineWidth(2);
            gl.glColor4f(0.0f, 1.0f, 1.0f, 1.0f);
            CCPrimitives.drawCircle(gl, s.width / 2, s.height / 2, 50, CCUtils.CC_DEGREES_TO_RADIANS(90), 50, true);

            // open yellow poly
            gl.glColor4f(1.0f, 1.0f, 0.0f, 1.0f);
            gl.glLineWidth(10);
            CCPoint vertices[] = {CCPoint.ccp(0, 0), CCPoint.ccp(50, 50), CCPoint.ccp(100, 50), CCPoint.ccp(100, 100), CCPoint.ccp(50, 100)};
            CCPrimitives.drawPoly(gl, vertices, 5, false);

            // closed purple poly
            gl.glColor4f(1.0f, 0.0f, 1.0f, 1.0f);
            gl.glLineWidth(2);
            CCPoint vertices2[] = {CCPoint.ccp(30, 130), CCPoint.ccp(30, 230), CCPoint.ccp(50, 200)};
            CCPrimitives.drawPoly(gl, vertices2, 3, true);

            // draw quad bezier path
            CCPrimitives.drawQuadBezier(gl, 0,s.height, s.width/2,s.height/2, s.width, s.height, 50);

            // draw cubic bezier path
            CCPrimitives.drawCubicBezier(gl, s.width/2, s.height/2, s.width/2+30, s.height/2+50,
                    s.width/2+60, s.height/2-50, s.width, s.height/2,100);


            // restore original values
            gl.glLineWidth(1);
            gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            gl.glPointSize(1);
            gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
            gl.glEnable(GL10.GL_TEXTURE_2D);
        }

        public String title() {
            return "draw primitives";
        }
    }

}
