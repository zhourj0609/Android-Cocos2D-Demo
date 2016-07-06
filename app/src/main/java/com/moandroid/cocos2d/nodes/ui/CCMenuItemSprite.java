package com.moandroid.cocos2d.nodes.ui;

import javax.microedition.khronos.opengles.GL10;

import com.moandroid.cocos2d.nodes.CCNode;
import com.moandroid.cocos2d.nodes.protocol.CCRGBAProtocol;
import com.moandroid.cocos2d.types.CCColor3B;
import com.moandroid.cocos2d.types.CCSize;

public class CCMenuItemSprite extends CCMenuItem 
							  implements CCRGBAProtocol {


    protected CCNode _normalImage;
    protected CCNode _selectedImage;
    protected CCNode _disabledImage;

    public static CCMenuItemSprite item(CCNode normalSprite, CCNode selectedSprite, CCNode disabledSprite) {
        return new CCMenuItemSprite(normalSprite, selectedSprite, disabledSprite, null, null);
    }

    public static CCMenuItemSprite item(CCNode normalSprite, CCNode selectedSprite, CCNode disabledSprite, CCNode target, String selector) {
        return new CCMenuItemSprite(normalSprite, selectedSprite, disabledSprite, target, selector);
    }

    protected CCMenuItemSprite(CCNode normalSprite, CCNode selectedSprite, CCNode disabledSprite, CCNode target, String selector) {
        super(target, selector);
        _normalImage = normalSprite;
        _selectedImage = selectedSprite;
        _disabledImage = disabledSprite;
        setContentSize(CCSize.make(_normalImage.width(), _normalImage.height()));
    }

    @Override
    public void draw(GL10 gl) {
        if (_isEnabled) {
            if (_isSelected)
                _selectedImage.draw(gl);
            else
                _normalImage.draw(gl);

        } else {
            if (_disabledImage != null)
                _disabledImage.draw(gl);

                // disabled image was not provided
            else
                _normalImage.draw(gl);
        }
    }

    // CCRGBAProtocol protocol

    public void setOpacity(byte opacity) {
        ((CCRGBAProtocol) _normalImage).setOpacity(opacity);
        ((CCRGBAProtocol) _selectedImage).setOpacity(opacity);
        ((CCRGBAProtocol) _disabledImage).setOpacity(opacity);
    }

    public void setColor(CCColor3B color) {
        ((CCRGBAProtocol) _normalImage).setColor(color);
        ((CCRGBAProtocol) _selectedImage).setColor(color);
        ((CCRGBAProtocol) _disabledImage).setColor(color);
    }

    public CCColor3B color() {
        return ((CCRGBAProtocol) _normalImage).color();
    }

    public byte opacity() {
        return ((CCRGBAProtocol) _normalImage).opacity();
    }
}
