package com.moandroid.cocos2d.nodes.ui;

import javax.microedition.khronos.opengles.GL10;

import com.moandroid.cocos2d.nodes.CCNode;
import com.moandroid.cocos2d.nodes.sprite.CCAtlasSprite;


public class CCMenuItemAtlasSprite extends CCMenuItemSprite {

    public static CCMenuItemAtlasSprite item(CCAtlasSprite normalSprite, CCAtlasSprite selectedSprite, CCAtlasSprite disabledSprite, CCNode target, String selector) {
        return new CCMenuItemAtlasSprite(normalSprite, selectedSprite, disabledSprite, target, selector);
    }

    protected CCMenuItemAtlasSprite(CCAtlasSprite normalSprite, CCAtlasSprite selectedSprite, CCAtlasSprite disabledSprite,
                               CCNode target, String selector) {
        super(normalSprite, selectedSprite, disabledSprite, target, selector);

        _normalImage.setVisible(true);
        _selectedImage.setVisible(false);
        _disabledImage.setVisible(false);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        _normalImage.setPosition(x, y);
        _selectedImage.setPosition(x, y);
        _disabledImage.setPosition(x, y);
    }

    @Override
    public void setRotation(float angle) {
        super.setRotation(angle);
        _normalImage.setRotation(angle);
        _selectedImage.setRotation(angle);
        _disabledImage.setRotation(angle);
    }

    @Override
    public void setScale(float scale) {
        super.setScale(scale);
        _normalImage.setScale(scale);
        _selectedImage.setScale(scale);
        _disabledImage.setScale(scale);
    }

    @Override
    public void selected() {
        if (_isEnabled) {
            super.selected();
            _normalImage.setVisible(false);
            _selectedImage.setVisible(true);
            _disabledImage.setVisible(false);
        }
    }

    @Override
    public void unselected() {
        if (_isEnabled) {
            super.unselected();
            _normalImage.setVisible(true);
            _selectedImage.setVisible(false);
            _disabledImage.setVisible(false);
        }
    }

    @Override
    public void setIsEnabled(boolean enabled) {
        super.setIsEnabled(enabled);
        if (enabled) {
            _normalImage.setVisible(true);
            _selectedImage.setVisible(false);
            _disabledImage.setVisible(false);

        } else {
            _normalImage.setVisible(false);
            _selectedImage.setVisible(false);
            if (_disabledImage != null)
                _disabledImage.setVisible(true);
            else
                _normalImage.setVisible(true);
        }
    }

    @Override
    public void draw(GL10 gl) {
        // override parent draw
        // since AtlasSpriteManager is the one that draws all the CCAtlasSprite objects
    }

}
