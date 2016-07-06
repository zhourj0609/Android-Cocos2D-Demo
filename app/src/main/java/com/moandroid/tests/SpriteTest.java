package com.moandroid.tests;

import com.moandroid.R;
import com.moandroid.cocos2d.actions.CCAction;
import com.moandroid.cocos2d.actions.CCRepeatForever;
import com.moandroid.cocos2d.actions.camera.CCOrbitCamera;
import com.moandroid.cocos2d.actions.instant.CCCallFunc;
import com.moandroid.cocos2d.actions.instant.CCHide;
import com.moandroid.cocos2d.actions.instant.CCInstantAction;
import com.moandroid.cocos2d.actions.instant.CCPlace;
import com.moandroid.cocos2d.actions.instant.CCToggleVisibility;
import com.moandroid.cocos2d.actions.interval.CCAnimate;
import com.moandroid.cocos2d.actions.interval.CCBezierBy;
import com.moandroid.cocos2d.actions.interval.CCBezierConfig;
import com.moandroid.cocos2d.actions.interval.CCBlink;
import com.moandroid.cocos2d.actions.interval.CCDelayTime;
import com.moandroid.cocos2d.actions.interval.CCFadeIn;
import com.moandroid.cocos2d.actions.interval.CCFadeOut;
import com.moandroid.cocos2d.actions.interval.CCIntervalAction;
import com.moandroid.cocos2d.actions.interval.CCJumpBy;
import com.moandroid.cocos2d.actions.interval.CCJumpTo;
import com.moandroid.cocos2d.actions.interval.CCMoveBy;
import com.moandroid.cocos2d.actions.interval.CCMoveTo;
import com.moandroid.cocos2d.actions.interval.CCRepeat;
import com.moandroid.cocos2d.actions.interval.CCRotateBy;
import com.moandroid.cocos2d.actions.interval.CCRotateTo;
import com.moandroid.cocos2d.actions.interval.CCScaleBy;
import com.moandroid.cocos2d.actions.interval.CCScaleTo;
import com.moandroid.cocos2d.actions.interval.CCSequence;
import com.moandroid.cocos2d.actions.interval.CCSpawn;
import com.moandroid.cocos2d.actions.interval.CCTintBy;
import com.moandroid.cocos2d.actions.interval.CCTintTo;
import com.moandroid.cocos2d.animation.CCAnimation;
import com.moandroid.cocos2d.nodes.CCLayer;
import com.moandroid.cocos2d.nodes.scenes.CCScene;
import com.moandroid.cocos2d.nodes.sprite.CCSprite;
import com.moandroid.cocos2d.nodes.ui.CCLabel;
import com.moandroid.cocos2d.nodes.ui.CCMenu;
import com.moandroid.cocos2d.nodes.ui.CCMenuItemImage;
import com.moandroid.cocos2d.opengles.CCGLSurfaceView;
import com.moandroid.cocos2d.renderers.CCDirector;
import com.moandroid.cocos2d.renderers.CCDirector2D;
import com.moandroid.cocos2d.types.CCPoint;
import com.moandroid.cocos2d.types.CCSize;
import com.moandroid.cocos2d.util.CCFormatter;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class SpriteTest extends Activity {

	public static final String LOG_TAG = SpriteTest.class.getSimpleName();

    private CCGLSurfaceView mGLSurfaceView;
    private FrameLayout layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(	WindowManager.LayoutParams.FLAG_FULLSCREEN,
								WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.sprite_sceen);

		mGLSurfaceView = new CCGLSurfaceView(this);

        mGLSurfaceView.setDirector(CCDirector2D.sharedDirector(this));
        //变为竖屏
        CCDirector.sharedDirector().setDeviceOrientation(CCDirector.kCCDeviceOrientationPortrait);

        layout = (FrameLayout)findViewById(R.id.android_cocos_sprite_scene_id);
        layout.addView(mGLSurfaceView);

        CCScene scene = CCScene.node();
        scene.addChild(restartAction());
		CCDirector2D.sharedDirector().runWithScene(scene);
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
        SpriteManual.class,
        SpriteMove.class,
        SpriteRotate.class,
        SpriteScale.class,
        SpriteJump.class,
        SpriteBezier.class,
        SpriteBlink.class,
        SpriteFade.class,
        SpriteTint.class,
        SpriteAnimate.class,
        SpriteSequence.class,
        SpriteSpawn.class,
        SpriteReverse.class,
        SpriteDelayTime.class,
        SpriteRepeat.class,
        SpriteCallFunc.class,
        SpriteReverseSequence.class,
        SpriteReverseSequence2.class,
        SpriteOrbit.class,
	};

	static CCLayer nextAction(){
		++sceneIdx;
		sceneIdx = sceneIdx % transitions.length;
		return restartAction();
	}

	static CCLayer backAction(){
		--sceneIdx;
		int total = transitions.length;
		if(sceneIdx < 0)
			sceneIdx += total;
		return restartAction();
	}

	static CCLayer restartAction(){
		try{
			Class<?> c = transitions[sceneIdx];
			return (CCLayer)c.newInstance();
		} catch (IllegalAccessException e){
			Log.d(LOG_TAG, "Create new instance for transitions[" + sceneIdx + "] IllegalAccessException error!");
			return null;
		} catch (InstantiationException e){
			Log.d(LOG_TAG, "Create new instance for transitions[" + sceneIdx + "] InstantiationException error!");
			return null;
		} catch (Exception e){
			Log.d(LOG_TAG, "Create new instance for transitions[" + sceneIdx + "] error!");
			return null;
		}
	}

	static class SpriteDemo extends CCLayer{
		CCSprite grossini;
		CCSprite tamara;
		public SpriteDemo(){

			CCSize s = CCDirector2D.sharedDirector().winSize();
			CCLabel label = CCLabel.label(title(), "Arial", 32);
			label.setPosition(CCPoint.ccp(s.width/2, s.height-16));
			addChild(label);
			String st = subtitle();
			CCLabel slabel = CCLabel.label(st, "Thonburi", 16);
			addChild(slabel,1);
			slabel.setPosition(CCPoint.ccp(s.width/2, s.height- 32 - 8));
			grossini = CCSprite.sprite("grossini.png");
			grossini.setPosition(s.width/3, 150);

			tamara  = CCSprite.sprite("grossinis_sister1.png");
			tamara.setPosition(s.width/3 * 2, 150);

			addChild(grossini,1);
			addChild(tamara,1);

			CCMenuItemImage item1 = CCMenuItemImage.item("b1.png", "b2.png", this, "backCallback");
			CCMenuItemImage item2 = CCMenuItemImage.item("r1.png", "r2.png", this, "restartCallback");
			CCMenuItemImage item3 = CCMenuItemImage.item("f1.png", "f2.png", this, "nextCallback");

			CCMenu menu = CCMenu.menu(item1, item2, item3);
			menu.setPosition(CCPoint.zero());
			item1.setPosition(CCPoint.ccp(s.width/2 - 100, 30));
			item2.setPosition(CCPoint.ccp(s.width/2, 30));
			item3.setPosition(CCPoint.ccp(s.width/2 + 100, 30));
			addChild(menu,1);
		}

		public void restartCallback(){
			CCScene s = CCScene.node();
			s.addChild(restartAction());
			CCDirector2D.sharedDirector().replaceScene(s);
		}

		public void nextCallback(){
			CCScene s = CCScene.node();
			s.addChild(nextAction());
			CCDirector2D.sharedDirector().replaceScene(s);
		}

		public void backCallback(){
			CCScene s = CCScene.node();
			s.addChild(backAction());
			CCDirector2D.sharedDirector().replaceScene(s);
		}

		public String title(){
			return "No title";
		}

		public String subtitle(){
			return "No sub title";
		}

        protected void centerSprites() {
            CCSize s = CCDirector.sharedDirector().winSize();

            grossini.setPosition(s.width / 3, s.height / 2);
            tamara.setPosition(2 * s.width / 3, s.height / 2);
        }
	}

    static class SpriteManual extends SpriteDemo {

        @Override
        public void onEnter() {
            super.onEnter();
            tamara.setScaleX(2.5f);
            tamara.setScaleY(-1.0f);
            tamara.setPosition(100, 100);

            grossini.setRotation(120.0f);
            grossini.setOpacity((byte) 128);
            grossini.setPosition(240, 160);
        }

        @Override
        public String title() {
            return "Manual Transformation";
        }
    }


    static class SpriteMove extends SpriteDemo {

        @Override
        public void onEnter() {
            super.onEnter();

            CCSize s = CCDirector.sharedDirector().winSize();


            CCIntervalAction actionTo = CCMoveTo.action(2, s.width - 40, s.height - 40);
            CCIntervalAction actionBy = CCMoveBy.action(2, 80, 80);

            tamara.runAction(actionTo);
            grossini.runAction(actionBy);
        }

        @Override
        public String title() {
            return "MoveTo / MoveBy";
        }
    }

    static class SpriteRotate extends SpriteDemo {

        public void onEnter() {
            super.onEnter();

            centerSprites();

            CCIntervalAction actionTo = CCRotateTo.action(2, 45);
            CCIntervalAction actionBy = CCRotateBy.action(2, 360);

            tamara.runAction(actionTo);
            grossini.runAction(actionBy);
        }

        public String title() {
            return "RotateTo / RotateBy";
        }
    }

    static class SpriteScale extends SpriteDemo {

        public void onEnter() {
            super.onEnter();

            centerSprites();

            CCIntervalAction actionTo = CCScaleTo.action(2, 0.5f);
            CCIntervalAction actionBy = CCScaleBy.action(2, 2.0f);

            //	grossini.transformAnchor_ = CCPoint.ccp( [grossini transformAnchor_].x, 0 );

            tamara.runAction(actionTo);
            grossini.runAction(actionBy);
        }

        public String title() {
            return "ScaleTo / ScaleBy";
        }

    }

    static class SpriteJump extends SpriteDemo {

        public void onEnter() {
            super.onEnter();
            CCSize s = CCDirector.sharedDirector().winSize();
            CCIntervalAction actionTo = CCJumpTo.action(2, s.width - 40, s.height - 40, 40, 4);
            CCIntervalAction actionBy = CCJumpBy.action(2, 300, 0, 40, 4);

            tamara.runAction(actionTo);
            grossini.runAction(actionBy);
        }

        public String title() {
            return "JumpTo / JumpBy";
        }
    }

    static class SpriteBezier extends SpriteDemo {

        public void onEnter() {
            super.onEnter();

            CCSize s = CCDirector.sharedDirector().winSize();

            //
            // startPosition can be any coordinate, but since the movement
            // is relative to the Bezier curve, item it (0,0)
            //

            // sprite 1
            CCBezierConfig bezier = CCBezierConfig.config();
            bezier.startPosition = CCPoint.ccp(0, 0);
            bezier.controlPoint_1 = CCPoint.ccp(0, s.height / 2);
            bezier.controlPoint_2 = CCPoint.ccp(300, -s.height / 2);
            bezier.endPosition = CCPoint.ccp(300, 100);

            CCIntervalAction bezierForward = CCBezierBy.action(3, bezier);
            CCIntervalAction bezierBack = bezierForward.reverse();
            CCIntervalAction seq = (CCIntervalAction) CCSequence.actions(bezierForward, bezierBack);
            CCAction rep = CCRepeatForever.action(seq);


            // sprite 2
            CCBezierConfig bezier2 = CCBezierConfig.config();
            bezier2.startPosition = CCPoint.ccp(0, 0);
            bezier2.controlPoint_1 = CCPoint.ccp(100, s.height / 2);
            bezier2.controlPoint_2 = CCPoint.ccp(200, -s.height / 2);
            bezier2.endPosition = CCPoint.ccp(300, 0);

            CCIntervalAction bezierForward2 = CCBezierBy.action(3, bezier2);
            CCIntervalAction bezierBack2 = bezierForward2.reverse();
            CCIntervalAction seq2 = (CCIntervalAction) CCSequence.actions(bezierForward2, bezierBack2);
            CCAction rep2 = CCRepeatForever.action(seq2);


            grossini.runAction(rep);
            tamara.runAction(rep2);
        }

        public String title() {
            return "BezierBy";
        }
    }


    static class SpriteBlink extends SpriteDemo {

        public void onEnter() {
            super.onEnter();

            centerSprites();

            CCIntervalAction action1 = CCBlink.action(2, 10);
            CCIntervalAction action2 = CCBlink.action(2, 5);

            tamara.runAction(action1);
            grossini.runAction(action2);
        }

        public String title() {
            return "Blink";
        }
    }

    static class SpriteFade extends SpriteDemo {
        public void onEnter() {
            super.onEnter();

            centerSprites();

            tamara.setOpacity((byte) 0);
            CCIntervalAction action1 = CCFadeIn.action(1.0f);
            CCIntervalAction action2 = CCFadeOut.action(1.0f);

            tamara.runAction(action1);
            grossini.runAction(action2);
        }

        public String title() {
            return "FadeIn / FadeOut";
        }
    }

    static class SpriteTint extends SpriteDemo {

        public void onEnter() {
            super.onEnter();

            centerSprites();

            CCIntervalAction action1 = CCTintTo.action(2,  255, 0, 255);
            CCIntervalAction action2 = CCTintBy.action(2,  0, 128, 128);

            tamara.runAction(action1);
            grossini.runAction(action2);
        }

        public String title() {
            return "TintTo / TintBy";
        }
    }

    static class SpriteAnimate extends SpriteDemo {

        public void onEnter() {
            super.onEnter();

            centerSprites();

            tamara.setVisible(false);

            CCAnimation animation = CCAnimation.animation("dance", 0.2f);
            for (int i = 1; i < 15; i++) {
                animation.addFrame(CCFormatter.format("grossini_dance_%02d.png", i));
            }

            CCIntervalAction action = CCAnimate.action(animation);

            grossini.runAction(action);
        }

        public String title() {
            return "Animation";
        }
    }


    static class SpriteSequence extends SpriteDemo {
        public static CCLayer layer() {
            return new SpriteSequence();
        }

        public void onEnter() {
            super.onEnter();

            tamara.setVisible(false);

            CCIntervalAction action = (CCIntervalAction) CCSequence.actions(CCMoveBy.action(2, 240, 0),
                    CCRotateBy.action(2, 540));

            grossini.runAction(action);
        }

        public String title() {
            return "Sequence: Move + Rotate";
        }
    }

    static class SpriteSpawn extends SpriteDemo {

        public void onEnter() {
            super.onEnter();

            tamara.setVisible(false);

            CCIntervalAction action = (CCIntervalAction) CCSpawn.actions(CCJumpBy.action(2, 300, 0, 50, 4),
                    CCRotateBy.action(2, 720));

            grossini.runAction(action);
        }

        public String title() {
            return "Spawn: Jump + Rotate";
        }
    }

    static class SpriteReverse extends SpriteDemo {
        public static CCLayer layer() {
            return new SpriteReverse();
        }

        public void onEnter() {
            super.onEnter();

            tamara.setVisible(false);

            CCIntervalAction jump = CCJumpBy.action(2, 300, 0, 50, 4);
            CCIntervalAction action = (CCIntervalAction) CCSequence.actions(jump, jump.reverse());

            grossini.runAction(action);
        }

        public String title() {
            return "Reverse an Action";
        }
    }

    static class SpriteDelayTime extends SpriteDemo {

        public void onEnter() {
            super.onEnter();

            tamara.setVisible(false);

            CCIntervalAction move = CCMoveBy.action(1, 150, 0);
            CCIntervalAction action = (CCIntervalAction) CCSequence.actions(move, CCDelayTime.action(2), move);

            grossini.runAction(action);
        }

        public String title() {
            return "DelayTime: m + Delay + m";
        }
    }

    static class SpriteReverseSequence extends SpriteDemo {

        public void onEnter() {
            super.onEnter();

            tamara.setVisible(false);

            CCIntervalAction move1 = CCMoveBy.action(1, 250, 0);
            CCIntervalAction move2 = CCMoveBy.action(1, 0, 50);
            CCIntervalAction seq = (CCIntervalAction) CCSequence.actions(move1, move2, move1.reverse());
            CCAction action = CCSequence.actions(seq, seq.reverse());

            grossini.runAction(action);
        }

        public String title() {
            return "Reverse a Sequence";
        }
    }

    static class SpriteReverseSequence2 extends SpriteDemo {

        public void onEnter() {
            super.onEnter();

            // Test:
            //   Sequence should work both with IntervalAction and InstantActions

            CCIntervalAction move1 = CCMoveBy.action(1, 50, 0);
            CCIntervalAction move2 = CCMoveBy.action(1, 0, 50);
            CCInstantAction tog1 = CCToggleVisibility.action();
            CCInstantAction tog2 = CCToggleVisibility.action();
            CCIntervalAction seq = (CCIntervalAction) CCSequence.actions(move1, tog1, move2, tog2, move1.reverse());
            CCAction action = CCRepeat.action((CCIntervalAction) CCSequence.actions(seq, seq.reverse()), 3);


            // Test:
            //   Also test that the reverse of Hide is Show, and vice-versa
            grossini.runAction(action);

            CCIntervalAction move_tamara = CCMoveBy.action(1, 50, 0);
            CCIntervalAction move_tamara2 = CCMoveBy.action(1, 50, 0);
            CCInstantAction hide = CCHide.action();
            CCIntervalAction seq_tamara = (CCIntervalAction) CCSequence.actions(move_tamara, hide, move_tamara2);
            CCIntervalAction seq_back = seq_tamara.reverse();
            tamara.runAction(CCSequence.actions(seq_tamara, seq_back));
        }

        public String title() {
            return "Reverse Sequence 2";
        }
    }


    static class SpriteRepeat extends SpriteDemo {

        public void onEnter() {
            super.onEnter();

            CCIntervalAction a1 = CCMoveBy.action(1, 150, 0);
            CCAction action1 = CCRepeat.action((CCIntervalAction) CCSequence.actions(CCPlace.action(60, 60), a1), 3);
            CCAction action2 = CCRepeatForever.action((CCIntervalAction) CCSequence.actions(a1.copy(), a1.reverse()));

            grossini.runAction(action1);
            tamara.runAction(action2);
        }

        public String title() {
            return "Repeat / RepeatForever actions";
        }
    }

    static class SpriteCallFunc extends SpriteDemo {

        public void onEnter() {
            super.onEnter();

            tamara.setVisible(false);

            CCIntervalAction action = (CCIntervalAction) CCSequence.actions(
                    CCMoveBy.action(2, 200, 0),
                    CCCallFunc.action(this, "callback"));
            grossini.runAction(action);
        }

        public void callback() {
            tamara.setVisible(true);
        }

        public String title() {
            return "Callback Action: CallFunc";
        }
    }

    static class SpriteOrbit extends SpriteDemo {

        public void onEnter() {
            super.onEnter();

            centerSprites();

            CCIntervalAction orbit1 = CCOrbitCamera.action(2, 1, 0, 0, 180, 0, 0);
            CCIntervalAction action1 = (CCIntervalAction) CCSequence.actions(
                    orbit1,
                    orbit1.reverse());

            CCIntervalAction orbit2 = CCOrbitCamera.action(2, 1, 0, 0, 180, -45, 0);
            CCAction action2 = CCSequence.actions(
                    orbit2,
                    orbit2.reverse());


            grossini.runAction(action1);
            tamara.runAction(action2);
        }


        public String title() {
            return "OrbitCamera Action";
        }
    }
}


