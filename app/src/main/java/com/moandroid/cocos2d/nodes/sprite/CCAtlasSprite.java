package com.moandroid.cocos2d.nodes.sprite;

import java.util.HashMap;

import android.util.Log;

import com.moandroid.cocos2d.animation.CCAnimationProtocol;
import com.moandroid.cocos2d.animation.CCAtlasAnimation;
import com.moandroid.cocos2d.animation.CCNodeFrames;
import com.moandroid.cocos2d.nodes.CCAtlasNode;
import com.moandroid.cocos2d.nodes.CCNode;
import com.moandroid.cocos2d.nodes.protocol.CCNodeSizeProtocol;
import com.moandroid.cocos2d.nodes.protocol.CCRGBAProtocol;
import com.moandroid.cocos2d.types.CCColor3B;
import com.moandroid.cocos2d.types.CCColor4B;
import com.moandroid.cocos2d.types.CCPoint;
import com.moandroid.cocos2d.types.CCQuad2F;
import com.moandroid.cocos2d.types.CCQuad3F;
import com.moandroid.cocos2d.types.CCQuad4B;
import com.moandroid.cocos2d.types.CCRect;
import com.moandroid.cocos2d.types.CCSize;
import com.moandroid.cocos2d.types.CCVertex2F;
import com.moandroid.cocos2d.types.CCVertex3F;
import com.moandroid.cocos2d.util.CCFormatter;
import com.moandroid.cocos2d.util.CCUtils;

public class CCAtlasSprite extends CCNode
						   implements CCNodeSizeProtocol,
						   			  CCNodeFrames,
						   			  CCRGBAProtocol
						   			  
{
    public static final int kIndexNotInitialized = -1;

    CCAtlasNode _textureAtlas;
    int _atlasIndex;

    /**
     * returns the altas index of the AtlasSprite
     */
    public int atlasIndex() {
        return _atlasIndex;
    }

    public void setIndex(int index) {
        _atlasIndex = index;
    }

    // texture pixels
    private CCRect _rect;

    // texture coords
    // stored as floats in the range [0..1]
    private CCQuad2F _texCoords = new CCQuad2F();

    // vertex coordinates
    // stored as pixel locations
    private CCQuad3F vertexCoords_ = new CCQuad3F();

    // opacity and RGB protocol
    private byte _opacity;
    private CCColor3B _color;
    
    private boolean _dirty;

    // Animations that belong to the sprite
    HashMap<String, CCAtlasAnimation> _animations;

    // image is flipped
    private boolean _flipX;
    private	boolean _flipY;

    /**
     * whether or not the Sprite's color needs to be updated in the Atlas
     */
    public boolean isDirty() {
        return _dirty;
    }

    /**
     * returns the rect of the AtlasSprite
     */
    public CCRect getTextureRect() {
        return _rect;
    }


    public static CCAtlasSprite sprite(CCRect rect, CCAtlasSpriteManager manager) {
        return new CCAtlasSprite(rect, manager);
    }

    protected CCAtlasSprite(CCRect rect, CCAtlasSpriteManager manager) {
        _textureAtlas = manager.atlas();

        _atlasIndex = kIndexNotInitialized;

        // default transform anchor: center
        setAnchorPoint(CCPoint.ccp(0.5f, 0.5f));

        // RGB and opacity
        _opacity = (byte) 255;
        _color = new CCColor3B((byte)255, (byte)255, (byte)255);

        _animations = null;        // lazy alloc

        setTextureRect(rect);
    }

    @Override
    public String toString() {
        return CCFormatter.format("<%s = %08X | Rect = (%.2f,%.2f,%.2f,%.2f) | tag = %i>", CCAtlasSprite.class.getSimpleName(),
                this, _rect.origin.x, _rect.origin.y, _rect.size.width, _rect.size.height, tag());
    }

    private void allocAnimations() {
        _animations = new HashMap<String, CCAtlasAnimation>(2);
    }

    public void setTextureRect(CCRect rect) {
        _rect = rect;

        updateTextureCoords();

        // Don't update Atlas if index == -1. issue #283
        if (_atlasIndex == kIndexNotInitialized)
            _dirty = true;
        else
            updateAtlas();

        // add these lines
        if (!((rect.size.width == width()) && ((rect.size.getHeight()) == height()))) {
            setContentSize(CCSize.make(rect.size.width, rect.size.height));
            _dirty = true;
        }
    }

    private void updateTextureCoords() {
        float atlasWidth = _textureAtlas.texture().pixelsWide();
        float atlasHeight = _textureAtlas.texture().pixelsHigh();

        float left = _rect.origin.x / atlasWidth;
        float right = (_rect.origin.x + _rect.size.width) / atlasWidth;
        float top = _rect.origin.y / atlasHeight;
        float bottom = (_rect.origin.y + _rect.size.height) / atlasHeight;

        if( _flipX)
            CCUtils.CC_SWAP(left,right);
        if( _flipY)
            CCUtils.CC_SWAP(top,bottom);

        CCQuad2F newCoords = new CCQuad2F(
                new CCVertex2F(left, bottom),
                new CCVertex2F(right, bottom),
                new CCVertex2F(left, top),
                new CCVertex2F(right, top));

        _texCoords = newCoords;
    }

    public void updateColor() {
    	CCColor4B color = new CCColor4B(_color.r, _color.g, _color.b, _opacity);
    	CCQuad4B colorQuad = new CCQuad4B(color,color,color,color);
        _textureAtlas.updateColor(colorQuad, _atlasIndex);
        _dirty = false;
    }

    // algorithm from pyglet ( http://www.pyglet.org )
    public void updatePosition() {

        // if not _visible then everything is 0
        if (!_visible) {
            CCQuad3F newVertices = new CCQuad3F(
                    new CCVertex3F(0, 0, 0),
                    new CCVertex3F(0, 0, 0),
                    new CCVertex3F(0, 0, 0),
                    new CCVertex3F(0, 0, 0));

            vertexCoords_ = newVertices;
        }

        // rotation ? -> update: rotation, scale, position
        else if (rotation() != 0) {
            float x1 = -anchorPoint().x * scaleX();
            float y1 = -anchorPoint().y * scaleY();

            float x2 = x1 + _rect.size.width * scaleX();
            float y2 = y1 + _rect.size.height * scaleY();
            float x = position().x;
            float y = position().y;

            float r = -CCUtils.CC_DEGREES_TO_RADIANS(rotation());
            float cr = (float)Math.cos(r);
            float sr = (float)Math.sin(r);
            float ax = x1 * cr - y1 * sr + x;
            float ay = x1 * sr + y1 * cr + y;
            float bx = x2 * cr - y1 * sr + x;
            float by = x2 * sr + y1 * cr + y;
            float cx = x2 * cr - y2 * sr + x;
            float cy = x2 * sr + y2 * cr + y;
            float dx = x1 * cr - y2 * sr + x;
            float dy = x1 * sr + y2 * cr + y;

            CCQuad3F newVertices = new CCQuad3F(
            		new CCVertex3F(ax, ay, 0),
            		new CCVertex3F(bx, by, 0),
            		new CCVertex3F(dx, dy, 0),
            		new CCVertex3F(cx, cy, 0));

            vertexCoords_ = newVertices;
        }

        // scale ? -> update: scale, position
        else if (scaleX() != 1 || scaleY() != 1) {
            float x = position().x;
            float y = position().y;

            float x1 = (x - anchorPoint().x * scaleX());
            float y1 = (y - anchorPoint().y * scaleY());
            float x2 = (x1 + _rect.size.width * scaleX());
            float y2 = (y1 + _rect.size.height * scaleY());

            CCQuad3F newVertices = new CCQuad3F(
            		new CCVertex3F(x1, y1, 0),
            		new CCVertex3F(x2, y1, 0),
            		new CCVertex3F(x1, y2, 0),
            		new CCVertex3F(x2, y2, 0));

            vertexCoords_ = newVertices;
        }

        // update position
        else {
            float x = position().x;
            float y = position().y;

            float x1 = (x - anchorPoint().x);
            float y1 = (y - anchorPoint().y);
            float x2 = (x1 + _rect.size.width);
            float y2 = (y1 + _rect.size.height);

            CCQuad3F newVertices = new CCQuad3F(
            		new CCVertex3F(x1, y1, 0),
            		new CCVertex3F(x2, y1, 0),
            		new CCVertex3F(x1, y2, 0),
            		new CCVertex3F(x2, y2, 0));

            vertexCoords_ = newVertices;
        }

        _textureAtlas.updateQuad(_texCoords, vertexCoords_, _atlasIndex);
        _dirty = false;
    }

    public void updateAtlas() {
        _textureAtlas.updateQuad(_texCoords, vertexCoords_, _atlasIndex);
    }

    public void insertInAtlas(int index) {
        _atlasIndex = index;
        _textureAtlas.insertQuad(_texCoords, vertexCoords_, _atlasIndex);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        _dirty = true;
    }

    @Override
    public void setRotation(float rot) {
        super.setRotation(rot);
        _dirty = true;
    }

    @Override
    public void setScaleX(float sx) {
        super.setScaleX(sx);
        _dirty = true;
    }

    @Override
    public void setScaleY(float sy) {
        super.setScaleY(sy);
        _dirty = true;
    }

    @Override
    public void setScale(float s) {
        super.setScale(s);
        _dirty = true;
    }

    @Override
    public void setVertexZ(float z) {
        super.setVertexZ(z);
        _dirty = true;
    }

    @Override
    public void setAnchorPoint(CCPoint pt) {
        super.setAnchorPoint(pt);
        _dirty = true;
    }

    @Override
    public void setIsRelativeAnchorPoint(boolean relative) {
        Log.w("AtlasSprite", "relativeTransformAnchor_ is ignored in AtlasSprite");
    }

    @Override
    public void setVisible(boolean v) {
        super.setVisible(v);
        _dirty = true;
    }

    public void setFlipX(boolean b)
    {
        if( _flipX != b ) {
            _flipX = b;
            setTextureRect(_rect);
        }
    }

    public boolean getFlipX()
    {
        return _flipX;
    }

    public void setFlipY(boolean b)
    {
        if( _flipY != b ) {
            _flipY = b;
            setTextureRect(_rect);
        }
    }

    public boolean getFlipY()
    {
        return _flipY;
    }
    
    @Override
    public CCNode addChild(CCNode child, int z, int aTag) {
        assert false : "AtlasSprite can't have children";
        return null;
    }

    public void setOpacity(byte opacity) {
        _opacity = opacity;
        _dirty = true;
    }

    public int getOpacity() {
        return _opacity;
    }

    public void setColor(CCColor3B color) {
        _color.r = color.r;
        _color.g = color.g;
        _color.b = color.b;
        _dirty = true;
    }

    public CCColor3B getColor() {
        return new CCColor3B(_color.r, _color.g, _color.b);
    }

    // TODO Remove cast
    public void setDisplayFrame(Object newFrame) {
        if (newFrame instanceof CCAtlasSpriteFrame) {
            CCAtlasSpriteFrame frame = (CCAtlasSpriteFrame) newFrame;
            CCRect rect = frame.rect;

            setTextureRect(rect);
        }
    }

    // TODO Remove cast
    public void setDisplayFrame(String animationName, int frameIndex) {
        if (_animations != null){
            CCAtlasAnimation a = _animations.get(animationName);
            CCAtlasSpriteFrame frame = (CCAtlasSpriteFrame) a.frames().get(frameIndex);
            CCRect rect = frame.rect;
            setTextureRect(rect);     	
        }
    }

    // TODO Remove cast
    public boolean isFrameDisplayed(Object frame) {
        if (frame instanceof CCAtlasSpriteFrame) {
            CCAtlasSpriteFrame spr = (CCAtlasSpriteFrame) frame;
            CCRect r = spr.rect;
            return CCRect.equalToRect(r, _rect);
        }
        return false;
    }

    public CCAtlasSpriteFrame displayFrame() {
        return new CCAtlasSpriteFrame(_rect);
    }

    public void addAnimation(CCAnimationProtocol anim) {
        // lazy alloc
        if (_animations == null)
            allocAnimations();

        _animations.put(anim.name(), (CCAtlasAnimation) anim);
    }

    public CCAnimationProtocol animationByName(String animationName) {
        return _animations.get(animationName);
    }

	@Override
	public CCColor3B color() {
		return _color;
	}

	@Override
	public byte opacity() {
		return _opacity;
	}
}
