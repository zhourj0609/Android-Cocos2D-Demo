package com.moandroid.cocos2d.nodes.ui;

import com.moandroid.cocos2d.nodes.CCLabelAtlas;
import com.moandroid.cocos2d.nodes.CCNode;

public class CCMenuItemAtlasFont extends CCMenuItemLabel {
	  public static CCMenuItemAtlasFont item(String value, String charMapFile, int itemWidth, int itemHeight, char startCharMap) {
	        assert value.length() != 0 :"value length must be greater than 0";

	        CCLabelAtlas label = CCLabelAtlas.label(value, charMapFile, itemWidth, itemHeight, startCharMap);
	    	return new CCMenuItemAtlasFont(label, null, null);
	    }

	    public static CCMenuItemAtlasFont item(String value, String charMapFile, int itemWidth, int itemHeight, char startCharMap, CCNode rec, String cb) {
	        assert value.length() != 0 :"value length must be greater than 0";

	        CCLabelAtlas label = CCLabelAtlas.label(value, charMapFile, itemWidth, itemHeight, startCharMap);
	    	return new CCMenuItemAtlasFont(label, rec, cb);
	    }

	    protected CCMenuItemAtlasFont(CCLabelAtlas label, CCNode rec, String cb) {
	        super(label, rec, cb);
	    }
}
