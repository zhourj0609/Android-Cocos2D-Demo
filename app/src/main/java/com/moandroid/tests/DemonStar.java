package com.moandroid.tests;

import java.util.ArrayList;
import java.util.Iterator;

import javax.microedition.khronos.opengles.GL10;

import org.jbox2d.collision.AABB;
import org.jbox2d.collision.CircleDef;
import org.jbox2d.collision.PolygonDef;
import org.jbox2d.collision.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.ContactFilter;
import org.jbox2d.dynamics.ContactListener;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.ContactPoint;
import org.jbox2d.dynamics.contacts.ContactResult;

import com.moandroid.cocos2d.actions.CCFiniteTimeAction;
import com.moandroid.cocos2d.actions.CCRepeatForever;
import com.moandroid.cocos2d.actions.instant.CCCallFunc;
import com.moandroid.cocos2d.actions.interval.CCAnimate;
import com.moandroid.cocos2d.actions.interval.CCDelayTime;
import com.moandroid.cocos2d.actions.interval.CCSequence;
import com.moandroid.cocos2d.animation.CCAnimation;
import com.moandroid.cocos2d.events.CCEvent;
import com.moandroid.cocos2d.nodes.CCLayer;
import com.moandroid.cocos2d.nodes.CCNode;
import com.moandroid.cocos2d.nodes.scenes.CCScene;
import com.moandroid.cocos2d.nodes.sprite.CCSprite;
import com.moandroid.cocos2d.opengles.CCGLSurfaceView;
import com.moandroid.cocos2d.opengles.CCPrimitives;
import com.moandroid.cocos2d.renderers.CCDirector;
import com.moandroid.cocos2d.renderers.CCDirector2D;
import com.moandroid.cocos2d.texture.CCTexture2D;
import com.moandroid.cocos2d.texture.CCTextureCache;
import com.moandroid.cocos2d.types.CCPoint;
import com.moandroid.cocos2d.types.CCSize;
import com.moandroid.cocos2d.util.CCUtils;
import com.moandroid.sound.SoundManager;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class DemonStar extends Activity {
	public static final String LOG_TAG = DemonStar.class.getSimpleName();
	
	private CCGLSurfaceView mGLSurfaceView;
	private GameLayer _gameLayer;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(	WindowManager.LayoutParams.FLAG_FULLSCREEN, 
								WindowManager.LayoutParams.FLAG_FULLSCREEN);
		mGLSurfaceView = new CCGLSurfaceView(this);
		mGLSurfaceView.setDirector(CCDirector2D.sharedDirector(this));
		CCDirector2D.sharedDirector().setDeviceOrientation(CCDirector.kCCDeviceOrientationPortrait);
		setContentView(mGLSurfaceView);
		CCScene scene = CCScene.node();
		_gameLayer = new GameLayer();
		scene.addChild(_gameLayer);
		CCDirector.sharedDirector().runWithScene(scene);
		SoundManager.sharedSoundManager().loadSound(this, "demonstar/sound/lv8bg.mp3", true, false);
		SoundManager.sharedSoundManager().loadSound(this, "demonstar/sound/bullet1.mp3", true, false);
		SoundManager.sharedSoundManager().loadSound(this, "demonstar/sound/crash.mp3", false, false);
		SoundManager.sharedSoundManager().loadSound(this, "demonstar/sound/bomb1.mp3", false, false);
		SoundManager.sharedSoundManager().loadSound(this, "demonstar/sound/bomb2.mp3", false, false);
	}

	@Override
	protected void onDestroy() {
        CCDirector2D.sharedDirector().end();
        SoundManager.purgeSharedManager();
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		CCDirector.sharedDirector().pause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		CCDirector.sharedDirector().resume();
		super.onResume();
	}
	
	private class GameLayer extends CCLayer{
		protected static final float PTM_RATIO = 24.0f;
		protected static final float BUFFER = 1.0f;
//		protected static final int BOX_GROUP_INDEX = -1;
		protected static final int SELF_GROUP_INDEX = -2;
		protected static final int ENEMY_GROUP_INDEX = -3;
		protected final World bxWorld;
		private Fight mFighterA;
		Body bxSpriteBody;
		public GameLayer(){
        	setTouchEnabled(true);
        	setAccelerometerEnabled(true);
            CCSize s = CCDirector.sharedDirector().winSize();
            float scaledWidth = s.width/PTM_RATIO;
            float scaledHeight = s.height/PTM_RATIO;
        	Vec2 lower = new Vec2(-BUFFER, -BUFFER);
        	Vec2 upper = new Vec2(	scaledWidth + BUFFER, 
        							scaledHeight + BUFFER);
        	Vec2 gravity = new Vec2(0f, 0f);         
        	bxWorld = new World(new AABB(lower, upper), gravity, false);
            
            mFighterA = new Fight(Fight.FIGHT_A);
           
            mFighterA.setPosition(s.width/2,s.height/2);
            
            addChild(mFighterA, 1);
            
        	BodyDef bxSpriteBodyDef = new BodyDef();
        	bxSpriteBodyDef.userData = mFighterA;
        	bxSpriteBodyDef.position.set(s.width/(2*PTM_RATIO), s.height/(2*PTM_RATIO));
        	bxSpriteBodyDef.angularDamping = 100f;

        	PolygonDef bxSpritePolygonDef = new PolygonDef();
        	bxSpritePolygonDef.setAsBox(mFighterA.width()/(2*PTM_RATIO), mFighterA.height()/(2*PTM_RATIO));
        	bxSpritePolygonDef.density = 2.0f;
        	bxSpritePolygonDef.restitution = 0;
        	bxSpritePolygonDef.filter.groupIndex = SELF_GROUP_INDEX;
        	bxSpriteBody = bxWorld.createBody(bxSpriteBodyDef);
	    	bxSpriteBody.createShape(bxSpritePolygonDef);
	    	bxSpriteBody.setMassFromShapes();
	    	
	    	bxWorld.setContactListener(new GameContactListener());
	    	bxWorld.setContactFilter(new GameContactFilter());
	    	//schedule("createEnemy",3);
 		}

		private ArrayList<Body> _discardeds = new ArrayList<Body>(10);
        public void tick(float delta) {
        	Iterator<Body> iter = _discardeds.iterator();
        	while(iter.hasNext()){
        		Body b = iter.next();
        		bxWorld.destroyBody(b);
        		if(!(b.getUserData() instanceof Fight) || 
        			((Fight)b.getUserData()).isDeath==false)
        			removeChild((CCNode)b.getUserData(),true);
        	}
        	_discardeds.clear();
			Vec2 velocity = new Vec2(_velx, _vely);
			bxSpriteBody.setLinearVelocity(velocity);
//        	synchronized (bxWorld) {
        		bxWorld.step(delta, 6);
//        	}
        	CCSize s = CCDirector.sharedDirector().winSize();
        	float x = bxSpriteBody.getPosition().x * PTM_RATIO;
        	float y = bxSpriteBody.getPosition().y * PTM_RATIO;
        	boolean change = false;
        	if(x > s.width - mFighterA.width()/2 && bxSpriteBody.m_linearVelocity.x>0){
        		x = s.width - mFighterA.width()/2;
        		change = true;
        	}
        	if(x < mFighterA.width()/2 && bxSpriteBody.m_linearVelocity.x<0){
        		x = mFighterA.width()/2;
        		change = true;
        	}
        	if(y < mFighterA.height()/2 && bxSpriteBody.m_linearVelocity.y<0){
        		y = mFighterA.height()/2;
        		change = true;
        	}
        	if(y>s.height - mFighterA.height()/2 && bxSpriteBody.m_linearVelocity.y>0){
        		y = s.height - mFighterA.height()/2;
        		change = true;
        	}
        	if(change){
        		bxSpriteBody.setXForm(new Vec2(x/PTM_RATIO, y/PTM_RATIO), 0);
        		bxSpriteBody.setLinearVelocity(new Vec2(0,0));
        	}
        	// Iterate over the bodies in the physics world
        	for (Body b = bxWorld.getBodyList(); b != null; b = b.getNext()) {
        		Object userData = b.getUserData();
        		if (userData != null) {
        			if(userData instanceof Fight){
        				if(b.isFrozen()){
        					_discardeds.add(b);
        				}else{
        					Fight sprite = (Fight)userData;
        					sprite.setPosition(b.getPosition().x * PTM_RATIO, b.getPosition().y * PTM_RATIO);
        					sprite.setRotation(-1.0f * CCUtils.CC_RADIANS_TO_DEGREES(b.getAngle()));
        				}
        			}else if(userData instanceof Bullet){
        				if(b.isFrozen()){
        					_discardeds.add(b);
        				}else{
	        				Bullet bullet = (Bullet)userData;
	        				bullet.setPosition(b.getPosition().x * PTM_RATIO, b.getPosition().y * PTM_RATIO);
	        				bullet.setRotation(-1.0f * CCUtils.CC_RADIANS_TO_DEGREES(b.getAngle()));
        				}
        			}
        		}
        	}
        }
        
        public void onEnter(){
        	SoundManager.sharedSoundManager().playSound("demonstar/sound/lv8bg.mp3");
        	schedule("tick");
        	super.onEnter();	
        }
        
		@Override       
		public void onExit() {
			endGame();
			super.onExit();	
		}
		
		private float _velx;
		private float _vely;
		
		@Override
		public boolean accelerometerChanged(float accelX, float accelY,
				float accelZ) {
			CCPoint vec = CCDirector.sharedDirector().convertAccelerometer(accelX, accelY);

			float x,y;
			if(vec.x < -1)
				x = 4;
			else if(vec.x > 1)
				x = -4;
			else
				x = 0;
			if(vec.y < -1)
				y = 4;
			else if(vec.y > 1)
				y = -4;
			else
				y = 0;
			_velx = x;
			_vely = y;
			return true;
		}

		
		@Override
		public void draw(GL10 gl) {
			super.draw(gl);
//			gl.glDisable(GL10.GL_TEXTURE_2D);
//			gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
//			gl.glLineWidth(2);
//			gl.glColor4f(1f, 1f, 1f, 1f);
//            CCSize s = CCDirector.sharedDirector().winSize();
//   	     
//            CCRect rc = CCRect.make(5, 0, s.width-10, 10);
//            CCPrimitives.drawRect(gl, rc);
//            
//            rc.setAll(0, 5, 10, s.height-10);
//            CCPrimitives.drawRect(gl, rc);
//            
//            rc.setAll(5, s.height-10, s.width-10, 10);
//            CCPrimitives.drawRect(gl, rc);
//            
//            rc.setAll(s.width-10, 5, 10, s.height-10);
//            CCPrimitives.drawRect(gl, rc);
// 
//			gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
//            gl.glEnable(GL10.GL_TEXTURE_2D);
		}

		private boolean _isShooting = false;
		@Override
		public boolean touchesBegan(CCEvent event) {
			if(!_isShooting){
				_isShooting = true;
				SoundManager.sharedSoundManager().playSound("demonstar/sound/bullet1.mp3");
				CCFiniteTimeAction ac = CCSequence.actions(CCCallFunc.action(this,"shotBullet"), CCDelayTime.action(0.2f));
				_gameLayer.runAction(CCRepeatForever.action(ac));
			}else{
				_isShooting = false;
				SoundManager.sharedSoundManager().stopSound("demonstar/sound/bullet1.mp3");	
				_gameLayer.stopAllActions();
			}
			return true;
		}

		public void shotBullet(){
			if(mFighterA == null)
				return;
			for(int i=0; i<3; ++i){
				Bullet bullet = new Bullet();
				addChild(bullet,3);
				bullet.setPosition(mFighterA.position());
				
	        	BodyDef bxBulletBodyDef = new BodyDef();
	        	bxBulletBodyDef.userData = bullet;
	        	bxBulletBodyDef.position.set(mFighterA.position().x/PTM_RATIO, mFighterA.position().y/PTM_RATIO);
	        	
	        	CircleDef bxBulletCircleDef = new CircleDef();
	        	bxBulletCircleDef.radius = 2/PTM_RATIO;        	
	        	bxBulletCircleDef.density = 1f;
	        	bxBulletCircleDef.restitution = 0;
	        	bxBulletCircleDef.filter.groupIndex = SELF_GROUP_INDEX;
	        	
	        	Body bxBulletBody = bxWorld.createBody(bxBulletBodyDef);
	        	bxBulletBody.createShape(bxBulletCircleDef);
	        	bxBulletBody.setMassFromShapes();
	        	if(i == 0){
	        		bxBulletBody.setLinearVelocity(new Vec2(-10f,10f));
	        	}else if(i == 1){
	        		bxBulletBody.setLinearVelocity(new Vec2(0f,1.414f*10));
	        	}else if(i == 2){
	        		bxBulletBody.setLinearVelocity(new Vec2(10f,10f));
	        	}
			}
		}
		
		private void endGame(){
			unschedule("tick");
			SoundManager.sharedSoundManager().stopSound("demonstar/sound/lv8bg.mp3");
			if(_isShooting)
				SoundManager.sharedSoundManager().playSound("demonstar/sound/bullet1.mp3");	
		}
		
		public void createEnemy(float dt){
			CCSize s = CCDirector.sharedDirector().winSize();
			for(int i=0; i<18; ++i){
				Fight enemy = new Fight(Fight.FIGHT_B);        
	            enemy.setPosition(i%6*53+10, s.height-i/6*50);
	            enemy.setRotation(180);
				addChild(enemy,-1);
				
	        	BodyDef bxEnemyBodyDef = new BodyDef();
	        	bxEnemyBodyDef.userData = enemy;
	        	bxEnemyBodyDef.position.set((i%6*53+10)/PTM_RATIO, (s.height-i/6*50)/PTM_RATIO);
	        	bxEnemyBodyDef.angle = CCUtils.CC_DEGREES_TO_RADIANS(180);
	        	PolygonDef bxEnemyPolygonDef = new PolygonDef();
	        	bxEnemyPolygonDef.setAsBox(enemy.width()/(2*PTM_RATIO), enemy.height()/(2*PTM_RATIO));
	        	bxEnemyPolygonDef.density = 2.0f;
	        	bxEnemyPolygonDef.restitution = 0;
	        	bxEnemyPolygonDef.filter.groupIndex = ENEMY_GROUP_INDEX;
	        	
	        	Body bxEnemyBody = bxWorld.createBody(bxEnemyBodyDef);
	        	bxEnemyBody.createShape(bxEnemyPolygonDef);
	        	bxEnemyBody.setMassFromShapes();
	        	
	        	bxEnemyBody.setLinearVelocity(new Vec2(0f,-3f));
			}
		}
		
		public class Bullet extends CCNode{
	
			@Override
			public void draw(GL10 gl) {
				super.draw(gl);
				gl.glDisable(GL10.GL_TEXTURE_2D);
				gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
				gl.glColor4f(1f, 0f, 0f, 1f);
				gl.glPointSize(3f);
	            CCPrimitives.drawPoint(gl, 0, 0);
				gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
	            gl.glEnable(GL10.GL_TEXTURE_2D);			
			}	
		}
	
		class GameContactListener implements ContactListener{
	
			@Override
			public void add(ContactPoint point) {
				Object s1 = (Object)point.shape1.m_body.getUserData();
				Object s2 = (Object)point.shape2.m_body.getUserData();
				if(	s1 instanceof Bullet &&
					s2 instanceof Fight){
					_discardeds.add(point.shape1.m_body);
					_discardeds.add(point.shape2.m_body);
					//((CCSprite)s2).runAction(CCAnimate.action(_animC));
					((Fight)s2).isDeath = true;
					((Fight)s2).runAction(CCSequence.actions(CCCallFunc.action(s2,"die"),CCCallFunc.action(s2,"deleteSelf")));
					SoundManager.sharedSoundManager().playSound("demonstar/sound/crash.mp3");
					if((Fight)s2 == mFighterA){
						_gameLayer.stopAllActions();
						mFighterA = null;
						SoundManager.sharedSoundManager().stopSound("demonstar/sound/bullet1.mp3");	
					}
				}else if(	s2 instanceof Bullet &&
						 	s1 instanceof Fight){
					_discardeds.add(point.shape1.m_body);
					_discardeds.add(point.shape2.m_body);
					((Fight)s1).isDeath = true;
					((Fight)s1).runAction(CCSequence.actions(CCCallFunc.action(s1,"die"),CCCallFunc.action(s1,"deleteSelf")));
					SoundManager.sharedSoundManager().playSound("demonstar/sound/crash.mp3");	
					if((Fight)s1 == mFighterA){
						_gameLayer.stopAllActions();
						mFighterA = null;
						SoundManager.sharedSoundManager().stopSound("demonstar/sound/bullet1.mp3");	
					}
				}else if(	s1 instanceof Fight &&
							s2 instanceof Fight){
					_discardeds.add(point.shape1.m_body);
					_discardeds.add(point.shape2.m_body);
					((Fight)s1).isDeath = true;
					((Fight)s2).isDeath = true;
					((Fight)s1).runAction(CCSequence.actions(CCCallFunc.action(s1,"die"),CCCallFunc.action(s1,"deleteSelf")));
					((Fight)s2).runAction(CCSequence.actions(CCCallFunc.action(s2,"die"),CCCallFunc.action(s2,"deleteSelf")));
					SoundManager.sharedSoundManager().stopSound("demonstar/sound/bullet1.mp3");	
					SoundManager.sharedSoundManager().playSound("demonstar/sound/crash.mp3");	
					_gameLayer.stopAllActions();
					mFighterA = null;
				}
				
			}
	
			@Override
			public void persist(ContactPoint point) {
				// TODO Auto-generated method stub
				
			}
	
			@Override
			public void remove(ContactPoint point) {
				// TODO Auto-generated method stub
				
			}
	
			@Override
			public void result(ContactResult point) {
				// TODO Auto-generated method stub
				
			}
		}
		
		class GameContactFilter implements ContactFilter{
	
			@Override
			public boolean shouldCollide(Shape shape1, Shape shape2) {
				if(	shape1.m_filter.groupIndex == shape2.m_filter.groupIndex ||
					shape1.m_body.getUserData() instanceof Bullet && 
					shape2.m_body.getUserData() instanceof Bullet)
					return false;
				return true;
			}
		}
	}
	
	public static class Fight extends CCNode{
		public static final int FIGHT_A = 1;
		public static final int FIGHT_B = 2;
		public int _type;
		public static final String FIGHT_A_PATH = "demonstar/image/fighterA_0.png";
		public static final String FIGHT_B_PATH = "demonstar/image/fighterB_0.png";
		private CCSprite sprite;
		public static final CCAnimation _animA = CCAnimation.animation("fighterA",0.1f,"demonstar/image/fighterA_0.png","demonstar/image/fighterA_1.png");;
		public static final CCAnimation _animB = CCAnimation.animation("fighterB",0.1f,"demonstar/image/fighterB_0.png","demonstar/image/fighterB_1.png");;
		public static final CCAnimation _animC = CCAnimation.animation("fighterC",0.1f,
        		"demonstar/image/explosion_02.png",
        		"demonstar/image/explosion_03.png",
        		"demonstar/image/explosion_04.png",
        		"demonstar/image/explosion_05.png",
        		"demonstar/image/explosion_06.png",
        		"demonstar/image/explosion_07.png",
        		"demonstar/image/explosion_08.png",
        		"demonstar/image/explosion_09.png",
        		"demonstar/image/explosion_10.png",
        		"demonstar/image/explosion_11.png",
        		"demonstar/image/explosion_12.png",
        		"demonstar/image/explosion_13.png",
        		"demonstar/image/explosion_14.png");
		
		public Fight(int type){
			if(type == FIGHT_A){
				type = FIGHT_A;
				sprite = CCSprite.sprite(FIGHT_A_PATH);
				sprite.runAction(CCRepeatForever.action(CCAnimate.action(_animA)));
			}else{
				sprite = CCSprite.sprite(FIGHT_B_PATH);
				sprite.runAction(CCRepeatForever.action(CCAnimate.action(_animB)));
			}
			addChild(sprite);
			isDeath = false;
		}
		
		public boolean isDeath;
		
		public void die(){
			sprite.runAction(CCAnimate.action(_animC));
		}
		
		public void deleteSelf(){
			if(_parent!=null){
				_parent.removeChild(this, true);
			}
		}
	}
}
