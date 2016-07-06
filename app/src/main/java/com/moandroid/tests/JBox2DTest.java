package com.moandroid.tests;

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

import com.moandroid.cocos2d.events.CCEvent;
import com.moandroid.cocos2d.events.CCTouchEvent;
import com.moandroid.cocos2d.nodes.CCLayer;
import com.moandroid.cocos2d.nodes.scenes.CCScene;
import com.moandroid.cocos2d.nodes.sprite.CCSprite;
import com.moandroid.cocos2d.nodes.ui.CCLabel;
import com.moandroid.cocos2d.opengles.CCGLSurfaceView;
import com.moandroid.cocos2d.opengles.CCPrimitives;
import com.moandroid.cocos2d.renderers.CCDirector;
import com.moandroid.cocos2d.renderers.CCDirector2D;
import com.moandroid.cocos2d.types.CCColor3B;
import com.moandroid.cocos2d.types.CCPoint;
import com.moandroid.cocos2d.types.CCSize;
import com.moandroid.cocos2d.util.CCUtils;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class JBox2DTest extends Activity {
	public static final String LOG_TAG = JBox2DTest.class.getSimpleName();
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
		CCScene scene = CCScene.node();
		scene.addChild(new JBox2DTestDemo());
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
	
	public class JBox2DTestDemo extends CCLayer {
	    //public static final int kTagSpriteManager = 1;
	    	
		// Pixel to meters ratio. Box2D uses meters as the unit for measurement.
		// This ratio defines how many pixels correspond to 1 Box2D "meter"
		// Box2D is optimized for objects of 1x1 meter therefore it makes sense
		// to define the ratio so that your most common object type is 1x1 meter.
        protected static final float PTM_RATIO = 24.0f;
        
        // Simulation space should be larger than window per Box2D recommendation.
        protected static final float BUFFER = 1.0f;
	        
	    protected final World bxWorld;
	        
	        public JBox2DTestDemo() {
	        	setTouchEnabled(true);
	        	setAccelerometerEnabled(true);
	            CCSize s = CCDirector.sharedDirector().winSize();
	            float scaledWidth = s.width/PTM_RATIO;
	            float scaledHeight = s.height/PTM_RATIO;
	                
	        	Vec2 lower = new Vec2(-BUFFER, -BUFFER);
	        	Vec2 upper = new Vec2(scaledWidth+BUFFER, scaledHeight+BUFFER);
	        	Vec2 gravity = new Vec2(0f, -2f);
	        	
	        	bxWorld = new World(new AABB(lower, upper), gravity, true);
				//bxWorld.setGravity( gravity );
				bxWorld.setContactFilter(new MyContactFiter());
				//bxWorld.setContactListener(new MyContactListener());
				
	        	// TODO: JBox2D debug drawing is a bit different now.  We could add debug drawing the old way, but looks like that is going away soon.
	        	/*
	        	GLESDebugDraw debugDraw = new GLESDebugDraw( PTM_RATIO );        	
	            bxWorld.setDebugDraw(debugDraw);

	            int flags = 0;
	            flags |= DebugDraw.e_shapeBit;
	            flags |= DebugDraw.e_jointBit;
	            flags |= DebugDraw.e_aabbBit;
	            flags |= DebugDraw.e_pairBit;
	            flags |= DebugDraw.e_centerOfMassBit;
	            debugDraw.setFlags(flags);
	            */
	            
	            BodyDef bxGroundBodyDef = new BodyDef();
	            //bxGroundBodyDef.position.set(0.0f, 0.0f);
	            bxGroundBodyDef.position.set(s.width/(2*PTM_RATIO), -10f/PTM_RATIO);
	            
	            Body bxGroundBody = bxWorld.createBody(bxGroundBodyDef);

	            PolygonDef bxGroundOutsideEdgeDef = new PolygonDef();
//	            bxGroundOutsideEdgeDef.addVertex(new Vec2(0f,0f));
//	            bxGroundOutsideEdgeDef.addVertex(new Vec2(0f,scaledHeight));
//	            bxGroundOutsideEdgeDef.addVertex(new Vec2(scaledWidth,scaledHeight));
//	            bxGroundOutsideEdgeDef.addVertex(new Vec2(scaledWidth,0f));            
//	            bxGroundOutsideEdgeDef.addVertex(new Vec2(0f,0f));
	            bxGroundOutsideEdgeDef.setAsBox(s.width/(2*PTM_RATIO), 10f/PTM_RATIO);
	            bxGroundBody.createShape(bxGroundOutsideEdgeDef);
	            
	            //?AtlasSpriteManager mgr = new AtlasSpriteManager("blocks.png", 150);
	            //?addChild(mgr, 0, kTagSpriteManager);

	            //?addNewSpriteWithCoords(CCPoint.ccp(s.width / 2.0f, s.height / 2.0f));
	            
	            CCLabel label = CCLabel.label("Tap screen", "DroidSans", 32);
	            label.setPosition(s.width / 2f, s.height - 50f);
	            label.setColor(new CCColor3B((byte)0, (byte)0, (byte)255));
	            addChild(label,2);
	        }

			@Override
			public void onEnter() {
				super.onEnter();
//				for(int i =0;i<3;++i){
//					addNewSpriteWithCoords(CCPoint.ccp(50*i+50, 320));
//				}
				// start ticking (for physics simulation)
				//_timer.schedule(new mytask(), 0, 30);
				schedule("tick");
			}

			@Override
			public void onExit() {
				super.onExit();
				//_timer.cancel();
				// stop ticking (for physics simulation)			
				unschedule("tick");
			}

			//Timer _timer = new Timer("jbox",false);
//			class mytask extends TimerTask{
//
//				@Override
//				public void run() {
//					step(1);
//				}
//				
//			}
			
			private void addNewSpriteWithCoords(CCPoint pos) {
	            //?AtlasSpriteManager mgr = (AtlasSpriteManager) getChild(kTagSpriteManager);

	        	// We have a 64x64 sprite sheet with 4 different 32x32 images.  The following code is
	        	// just randomly picking one of the images
	        	//?int idx = (CCUtils.CCRANDOM_0_1() > 0.5f ? 0:1);
	        	//?int idy = (CCUtils.CCRANDOM_0_1() > 0.5f ? 0:1);
	            //?AtlasSprite sprite = AtlasSprite.sprite(CCRect.make(idx * 32f, idy * 32f, 32f, 32f), mgr);
	        	CCSprite sprite = CCSprite.sprite("r1.png");
	        	addChild(sprite,-1);
	            //?mgr.addChild(sprite);

	            //?sprite.setPosition(pos.x, pos.y);

	        	// Define the dynamic body.
	        	// Set up a 1m squared box in the physics world
	        	BodyDef bxSpriteBodyDef = new BodyDef();
	        	bxSpriteBodyDef.userData = sprite;
	        	bxSpriteBodyDef.position.set(pos.x/PTM_RATIO, pos.y/PTM_RATIO);
	        	bxSpriteBodyDef.linearDamping = 0.3f;
	        	
	        	// Define another box shape for our dynamic body.
	        	//PolygonDef bxSpritePolygonDef = new PolygonDef();
	        	//bxSpritePolygonDef.setAsBox(0.5f, 0.5f);
	        	//bxSpritePolygonDef.density = 1.0f;
	        	//bxSpritePolygonDef.friction = 0.3f;
	        	
	        	CircleDef bxSpritePolygonDef = new CircleDef();
	        	//bxSpritePolygonDef.localPosition = new Vec2(0.5f, 0.5f);
	        	bxSpritePolygonDef.radius = sprite.height()/(2*PTM_RATIO);
	        	bxSpritePolygonDef.density = 1.0f;
	        	bxSpritePolygonDef.friction = 0.3f;
	        	
//	        	synchronized (bxWorld) {
	        		// Define the dynamic body fixture and set mass so it's dynamic.
	        		Body bxSpriteBody = bxWorld.createBody(bxSpriteBodyDef);
		    		bxSpriteBody.createShape(bxSpritePolygonDef);
		    		bxSpriteBody.setMassFromShapes();
//	        	}
	        }

//			public void step(float delta){
//	        	// It is recommended that a fixed time step is used with Box2D for stability
//	        	// of the simulation, however, we are using a variable time step here.
//	        	// You need to make an informed choice, the following URL is useful
//	        	// http://gafferongames.com/game-physics/fix-your-timestep/
//	        	
//	        	// Instruct the world to perform a simulation step. It is
//	        	// generally best to keep the time step and iterations fixed.
//	        	//synchronized (bxWorld) {
//	        		bxWorld.step(delta, 10);
//	        	//}
//		        	
//	        	// Iterate over the bodies in the physics world
//	        	for (Body b = bxWorld.getBodyList(); b != null; b = b.getNext()) {
//	        		Object userData = b.getUserData();
//	        		
////	        		if (userData != null && userData instanceof AtlasSprite) {
////	        			// Synchronize the AtlasSprite position and rotation with the corresponding body
////	        			AtlasSprite sprite = (AtlasSprite)userData;
////	        			sprite.setPosition(b.getPosition().x * PTM_RATIO, b.getPosition().y * PTM_RATIO);
////	        			sprite.setRotation(-1.0f * CCMacros.CC_RADIANS_TO_DEGREES(b.getAngle()));
////	        		}
//	        		if (userData != null && userData instanceof CCSprite) {
//	        			// Synchronize the AtlasSprite position and rotation with the corresponding body
//	        			CCSprite sprite = (CCSprite)userData;
//	        			sprite.setPosition(b.getPosition().x * PTM_RATIO, b.getPosition().y * PTM_RATIO);
//	        			sprite.setRotation(-1.0f * CCUtils.CC_RADIANS_TO_DEGREES(b.getAngle()));
//	        		}
//	        	}				
//			}
			
	        public void tick(float delta) {
	        	// It is recommended that a fixed time step is used with Box2D for stability
	        	// of the simulation, however, we are using a variable time step here.
	        	// You need to make an informed choice, the following URL is useful
	        	// http://gafferongames.com/game-physics/fix-your-timestep/
	        	
	        	// Instruct the world to perform a simulation step. It is
	        	// generally best to keep the time step and iterations fixed.
//	        	synchronized (bxWorld) {
	        		bxWorld.step(delta, 8);
//	        	}
		        
	        	// Iterate over the bodies in the physics world
	        	for (Body b = bxWorld.getBodyList(); b != null; b = b.getNext()) {
	        		Object userData = b.getUserData();
	        		
//	        		if (userData != null && userData instanceof AtlasSprite) {
//	        			// Synchronize the AtlasSprite position and rotation with the corresponding body
//	        			AtlasSprite sprite = (AtlasSprite)userData;
//	        			sprite.setPosition(b.getPosition().x * PTM_RATIO, b.getPosition().y * PTM_RATIO);
//	        			sprite.setRotation(-1.0f * CCMacros.CC_RADIANS_TO_DEGREES(b.getAngle()));
//	        		}
	        		if (userData != null && userData instanceof CCSprite) {
	        			// Synchronize the AtlasSprite position and rotation with the corresponding body
	        			CCSprite sprite = (CCSprite)userData;
	        			sprite.setPosition(b.getPosition().x * PTM_RATIO, b.getPosition().y * PTM_RATIO);
	        			sprite.setRotation(-1.0f * CCUtils.CC_RADIANS_TO_DEGREES(b.getAngle()));
	        		}
	        	}
	        }
	        
	        @Override
			public void draw(GL10 gl) {
				super.draw(gl);
				gl.glLineWidth(2);
	            CCSize s = CCDirector.sharedDirector().winSize();
	     
	            CCPoint[] points = {
	            	CCPoint.ccp(0, -10),
	            	CCPoint.ccp(0, 10),
	            	CCPoint.ccp(s.width,10),
	            	CCPoint.ccp(s.width,-10),
	            };
	            CCPrimitives.drawPoly(gl, points, 4, true);
			}

			@Override
	        public boolean touchesBegan(CCEvent event) {
				CCTouchEvent tevent = (CCTouchEvent)event;
	            CCPoint location = CCDirector.sharedDirector().convertToGL(tevent.x, tevent.y);

	            addNewSpriteWithCoords(location);
	 
	            return true;
	        }

			@Override
		    public boolean accelerometerChanged(float accelX, float accelY, float accelZ) {
				// no filtering being done in this demo (just magnify the gravity a bit)
				//Log.v(LOG_TAG,CCFormatter.format("x:%f,y:%f,z:%f",accelX,accelY,accelZ));
				CCPoint pt = CCDirector.sharedDirector().convertAccelerometer(accelX, accelY);
				Vec2 gravity = new Vec2( pt.x * -2.00f, pt.y * -2.00f );
				bxWorld.setGravity( gravity );
				return true;
			}
	    }
	
	class MyContactListener implements ContactListener{

		@Override
		public void add(ContactPoint point) {
			// TODO Auto-generated method stub
			
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
	
	class MyContactFiter implements ContactFilter{

		@Override
		public boolean shouldCollide(Shape shape1, Shape shape2) {
			if(shape1.m_type == shape2.m_type)
				return false;
			else 
				return true;
		}
		
	}
}
