package com.moandroid.tests;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.transition.Scene;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.moandroid.R;
import com.moandroid.cocos2d.actions.CCAction;
import com.moandroid.cocos2d.actions.CCFiniteTimeAction;
import com.moandroid.cocos2d.actions.CCRepeatForever;
import com.moandroid.cocos2d.actions.instant.CCCallFunc;
import com.moandroid.cocos2d.actions.instant.CCHide;
import com.moandroid.cocos2d.actions.instant.CCInstantAction;
import com.moandroid.cocos2d.actions.instant.CCToggleVisibility;
import com.moandroid.cocos2d.actions.interval.CCBezierBy;
import com.moandroid.cocos2d.actions.interval.CCBezierConfig;
import com.moandroid.cocos2d.actions.interval.CCIntervalAction;
import com.moandroid.cocos2d.actions.interval.CCMoveBy;
import com.moandroid.cocos2d.actions.interval.CCMoveTo;
import com.moandroid.cocos2d.actions.interval.CCRepeat;
import com.moandroid.cocos2d.actions.interval.CCScaleBy;
import com.moandroid.cocos2d.actions.interval.CCScaleTo;
import com.moandroid.cocos2d.actions.interval.CCSequence;
import com.moandroid.cocos2d.actions.interval.CCSpawn;
import com.moandroid.cocos2d.nodes.CCLayer;
import com.moandroid.cocos2d.nodes.scenes.CCScene;
import com.moandroid.cocos2d.nodes.sprite.CCSprite;
import com.moandroid.cocos2d.opengles.CCGLSurfaceView;
import com.moandroid.cocos2d.renderers.CCDirector;
import com.moandroid.cocos2d.renderers.CCDirector2D;
import com.moandroid.cocos2d.types.CCPoint;
import com.moandroid.cocos2d.types.CCSize;

import common.AnimationActivity;

/**
 * Created by renjiaozhou on 2016/7/7.
 */
public class AnimationTest extends AnimationActivity implements View.OnClickListener{

    private CCGLSurfaceView mGLSurfaceView;
    private ViewGroup viewGroup;
    private Button btnSendStart = null;

    //场景
    private CCScene animationScene = null;
    private SendStartAnimationLayer sendStartLayer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.animation_test);
        initAnimationSence();
        btnSendStart = (Button) findViewById(R.id.send_start_id);
        btnSendStart.setOnClickListener(this);
    }

    private void initAnimationSence() {

        mGLSurfaceView = new CCGLSurfaceView(this);
        mGLSurfaceView.setDirector(CCDirector2D.sharedDirector(this));
        //变为竖屏
        CCDirector.sharedDirector().setDeviceOrientation(CCDirector.kCCDeviceOrientationPortrait);
        viewGroup = (RelativeLayout)findViewById(R.id.android_cocos_sprite_scene_id);
        viewGroup.addView(mGLSurfaceView);
        initAniamtionLayer();
    }

    private void initAniamtionLayer(){
        animationScene  = CCScene.node();
        sendStartLayer = new SendStartAnimationLayer();
        animationScene.addChild(sendStartLayer);//添加星星动画场景
        CCDirector2D.sharedDirector().runWithScene(animationScene);
    }

    private void handSendStartAnimation() {
        sendStartLayer.handSendStartAnimation();// 开始业务动画

//        new Thread(){
//            @Override
//            public void run() {
//            }
//        }.start();
    }

    /**
     * 开启一个动画场景
     * @return
     */
    private CCScene restartAction(){
        return null;
    }

    class SendStartAnimationLayer extends CCLayer {

        class CallFunObje{
            CCSprite star;
            public CallFunObje(CCSprite ccSprite){
                star = ccSprite;
            }
            public void remove(){
                SendStartAnimationLayer.this.removeChild(star,true);
            }
        }

        public void handSendStartAnimation(){
            CCSprite start = CCSprite.sprite("stars/fx_star1.png");
            this.addChild(start);
            int [] postion = new int[2];

            btnSendStart.getLocationOnScreen(postion);
            CCSize size = CCDirector.sharedDirector().winSize();
            start.setAnchorPoint(CCPoint.ccp(0.5f,0.5f));

            CCPoint point = CCPoint.ccp(postion[0],size.height - postion[1]);

            //start.setPosition(CCPoint.ccp(postion[0],size.height - postion[1]));

            // 贝塞尔曲线
            CCBezierConfig bezierConfig = CCBezierConfig.config();
            CCBezierConfig bezier = CCBezierConfig.config();
            bezier.startPosition = CCPoint.ccp(point.x, point.y);
            bezier.controlPoint_1 = CCPoint.ccp(point.x-20, point.y+200);
            bezier.controlPoint_2 = CCPoint.ccp(point.x+20, point.y + 400);
            bezier.endPosition = CCPoint.ccp(point.x,point.y+600);

            CCIntervalAction bezierForward = CCBezierBy.action(3, bezier);

            CCCallFunc remove = CCCallFunc.action(new CallFunObje(start),"remove");

            //ScaleTo
            //start.setPosition(CCPoint.ccp(size.width/2,size.height/2));
            CCIntervalAction actionTo = CCScaleTo.action(1.5f, 3f);
            CCIntervalAction action1 = CCScaleTo.action(1.5f,1f);
            CCIntervalAction seq1 = (CCIntervalAction) CCSequence.actions(actionTo,action1);

            //CCAction rep1 = CCRepeatForever.action(seq1);
            // Scale
            CCSpawn spawn = (CCSpawn) CCSpawn.actions(bezierForward,seq1);

            CCIntervalAction seqall = (CCIntervalAction)CCSequence.actions(spawn,remove);
            //CCAction rep = CCRepeatForever.action(spawn);
            start.runAction(seqall);

            // ScaleBy
            //CCIntervalAction actionBy = CCScaleBy.action(2, 2.0f);

            // Test:
            //   Also test that the reverse of Hide is Show, and vice-versa

        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.send_start_id:
                this.handSendStartAnimation();
                break;
            default:
                break;
        }
    }

}
