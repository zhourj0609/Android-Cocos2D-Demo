package com.moandroid.cocos2d.nodes.ui;

import com.moandroid.cocos2d.nodes.CCTextureNode;
import com.moandroid.cocos2d.nodes.protocol.CCLabelProtocol;
import com.moandroid.cocos2d.nodes.protocol.CCNodeSizeProtocol;
import com.moandroid.cocos2d.texture.CCTextureCache;
import com.moandroid.cocos2d.types.CCSize;


public class CCLabel extends CCTextureNode 
					 implements CCLabelProtocol, CCNodeSizeProtocol{
	public static final int TEXT_ALIGNMENT_LEFT = 1;
	public static final int TEXT_ALIGNMENT_CENTER = 2;
	public static final int TEXT_ALIGNMENT_RIGHT = 3;
    private CCSize _dimensions;
    private int _alignment = TEXT_ALIGNMENT_CENTER;
    private String _fontName;
    private int _fontSize;

    public static CCLabel label(String string, String fontname, int fontsize) {
        return new CCLabel(string, 0, 0, TEXT_ALIGNMENT_CENTER, fontname, fontsize);
    }

    protected CCLabel(String string, String fontname, int fontsize) {
        this(string, 0, 0, TEXT_ALIGNMENT_CENTER, fontname, fontsize);
    }

    public static CCLabel node(String string, float w, float h, int alignment, String name, int size) {
        return new CCLabel(string, w, h, alignment, name, size);
    }

    protected CCLabel(String string, float w, float h, int alignment, String name, int size) {
        _dimensions = CCSize.make(w, h);
        _alignment = alignment;
        _fontName = name;
        _fontSize = size;

        setString(string);
    }

    public void setString(String string) {
        setTexture(CCTextureCache.sharedTextureCache().getTexture(string, _dimensions, _alignment, _fontName, _fontSize));
    }
}
