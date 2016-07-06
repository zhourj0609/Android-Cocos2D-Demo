package com.moandroid.cocos2d.nodes.ui;

import javax.microedition.khronos.opengles.GL10;

import com.moandroid.cocos2d.actions.CCAction;
import com.moandroid.cocos2d.actions.interval.CCScaleTo;
import com.moandroid.cocos2d.nodes.CCNode;
import com.moandroid.cocos2d.nodes.protocol.CCLabelProtocol;
import com.moandroid.cocos2d.nodes.protocol.CCNodeSizeProtocol;
import com.moandroid.cocos2d.nodes.protocol.CCRGBAProtocol;
import com.moandroid.cocos2d.types.CCColor3B;
import com.moandroid.cocos2d.types.CCSize;

public class CCMenuItemLabel extends CCMenuItem 
							 implements CCRGBAProtocol {

    private CCLabelProtocol _label;
    private CCColor3B _colorBackup;
    private CCColor3B _disabledColor;

    public static CCMenuItemLabel item(CCLabelProtocol label, CCNode target, String selector) {
        return new CCMenuItemLabel(label, target, selector);
    }

    protected CCMenuItemLabel(CCLabelProtocol label, CCNode target, String selector) {
        super(target, selector);
        setLabel(label);
        _colorBackup = new CCColor3B((byte)255, (byte)255, (byte)255);
        _disabledColor = new CCColor3B((byte)126, (byte)126, (byte)126);
    }

    public void setOpacity(byte opacity) {
        ((CCRGBAProtocol) _label).setOpacity(opacity);
    }

    public byte opacity() {
        return ((CCRGBAProtocol) _label).opacity();
    }

    public void setColor(CCColor3B color) {
        ((CCRGBAProtocol) _label).setColor(color);
    }

    public CCColor3B color() {
        return ((CCRGBAProtocol) _label).color();
    }

    public CCColor3B getDisabledColor() {
        return new CCColor3B(_disabledColor.r, _disabledColor.g, _disabledColor.b);
    }

    public void setDisabledColor(CCColor3B color) {
        _disabledColor.r = color.r;
        _disabledColor.g = color.g;
        _disabledColor.b = color.b;
    }

    public CCLabelProtocol getLabel() {
        return _label;
    }

    public void setLabel(CCLabelProtocol label) {
        _label = label;
        setContentSize(CCSize.make(((CCNodeSizeProtocol) _label).width(), ((CCNodeSizeProtocol) _label).height()));
    }

    public void setString(String string) {
        _label.setString(string);
        setContentSize(CCSize.make(((CCNodeSizeProtocol) _label).width(), ((CCNodeSizeProtocol) _label).height()));
    }

    public void activate() {
        if (_isEnabled) {
            stopAllActions();

            setScale(1.0f);

            super.activate();
        }
    }

    public void selected() {
        // subclass to change the default action
        if (_isEnabled) {
            super.selected();

            stopAction(kZoomActionTag);
            CCAction zoomAction = CCScaleTo.action(0.1f, 1.2f);
            zoomAction.setTag(kZoomActionTag);
            runAction(zoomAction);
        }
    }

    public void unselected() {
        // subclass to change the default action
        if (_isEnabled) {
            super.unselected();

            stopAction(kZoomActionTag);
            CCAction zoomAction = CCScaleTo.action(0.1f, 1.0f);
            zoomAction.setTag(kZoomActionTag);
            runAction(zoomAction);
        }
    }

    public void setIsEnabled(boolean enabled) {
        if (_isEnabled != enabled) {
            if (!enabled) {
                _colorBackup = ((CCRGBAProtocol) _label).color();
                ((CCRGBAProtocol) _label).setColor(_disabledColor);
            } else
                ((CCRGBAProtocol) _label).setColor(_colorBackup);
        }

        super.setIsEnabled(enabled);
    }

    public void draw(GL10 gl) {
        ((CCNode)_label).draw(gl);
    }
}
