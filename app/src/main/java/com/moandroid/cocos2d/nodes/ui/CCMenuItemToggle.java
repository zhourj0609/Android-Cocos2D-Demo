package com.moandroid.cocos2d.nodes.ui;

import java.util.ArrayList;
import java.util.Arrays;


import com.moandroid.cocos2d.nodes.CCNode;
import com.moandroid.cocos2d.nodes.protocol.CCRGBAProtocol;
import com.moandroid.cocos2d.types.CCColor3B;
import com.moandroid.cocos2d.types.CCSize;

public class CCMenuItemToggle extends CCMenuItem 
							  implements CCRGBAProtocol{

	   private int _selectedIndex;
	    private ArrayList<CCMenuItem> _subItems;
	    private byte _opacity;
	    CCColor3B _color;

	    public static CCMenuItemToggle item(CCNode target, String selector, CCMenuItem... items) {
	        return new CCMenuItemToggle(target, selector, items);
	    }

	    protected CCMenuItemToggle(CCNode t, String sel, CCMenuItem... items) {
	        super(t, sel);

	        _subItems = new ArrayList<CCMenuItem>(items.length);

	        _subItems.addAll(Arrays.asList(items));


	        _selectedIndex = Integer.MAX_VALUE;
	        setSelectedIndex(0);

	    }

	    public void setSelectedIndex(int index) {
	        if (index != _selectedIndex) {
	            _selectedIndex = index;
	            removeChild(kCurrentItem, false);

	            CCMenuItem item = _subItems.get(_selectedIndex);
	            addChild(item, 0, kCurrentItem);

	            float width = item.width();
	            float height = item.height();

	            setContentSize(CCSize.make(width, height));
	            item.setPosition(width / 2, height / 2);
	        }
	    }

	    public int selectedIndex() {
	        return _selectedIndex;
	    }

	    @Override
	    public void selected() {
	        _subItems.get(_selectedIndex).selected();
	    }

	    @Override
	    public void unselected() {
	        _subItems.get(_selectedIndex).unselected();
	    }

	    @Override
	    public void activate() {
	        // update index

	        if (_isEnabled) {
	            int newIndex = (_selectedIndex + 1) % _subItems.size();
	            setSelectedIndex(newIndex);

	        }
	        super.activate();
	    }

	    @Override
	    public void setIsEnabled(boolean enabled) {
	        super.setIsEnabled(enabled);
	        for (CCMenuItem item : _subItems)
	            item.setIsEnabled(enabled);
	    }

	    public CCMenuItem selectedItem() {
	        return _subItems.get(_selectedIndex);
	    }

	    public byte opacity(){
	    	return _opacity;
	    }
	    
	    public void setOpacity(byte opacity) {
	        _opacity = opacity;
	        for (CCMenuItem item : _subItems)
	            ((CCRGBAProtocol) item).setOpacity(opacity);
	    }

		public CCColor3B color(){
			return _color;
		}
		
	    public void setColor(CCColor3B color) {
	        _color = color;
	        for (CCMenuItem item : _subItems)
	            ((CCRGBAProtocol) item).setColor(color);
	    }
}
