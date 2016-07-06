package com.moandroid.cocos2d.nodes.ui;

import com.moandroid.cocos2d.nodes.CCNode;
import com.moandroid.cocos2d.nodes.sprite.CCSprite;

public class CCMenuItemImage extends CCMenuItemSprite {

    public static CCMenuItemImage item(String value, String value2) {
        return item(value, value2, null, null, null);
    }

    public static CCMenuItemImage item(String value, String value2, CCNode t, String s) {
        return item(value, value2, null, t, s);
    }

    public static CCMenuItemImage item(String value, String value2, String value3) {
        return item(value, value2, value3, null, null);
    }

    public static CCMenuItemImage item(String normalI, String selectedI, String disabledI, CCNode t, String sel) {
        return new CCMenuItemImage(CCSprite.sprite(normalI), CCSprite.sprite(selectedI), (disabledI == null) ? null : CCSprite.sprite(disabledI), t, sel);
    }

    protected CCMenuItemImage(CCSprite normal, CCSprite selected, CCSprite disabled, CCNode t, String sel) {
        super(normal, selected, disabled, t, sel);
    }
}
