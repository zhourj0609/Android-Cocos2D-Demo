package com.moandroid.cocos2d.nodes.ui;

import java.lang.reflect.Method;

import com.moandroid.cocos2d.nodes.CCNode;
import com.moandroid.cocos2d.types.CCPoint;
import com.moandroid.cocos2d.types.CCRect;

public class CCMenuItem extends CCNode {

	   public static final int kItemSize = 32;

	    static int _fontSize = kItemSize;
	    static String _fontName = "DroidSans";

	    public static final int kCurrentItem = 0xc0c05001;

	    public static final int kZoomActionTag = 0xc0c05002;

	    protected boolean _isEnabled;
	    protected boolean _isSelected;

	    protected Object _target;
	    protected String _selector;

	    private Method invocation;

	    /**
	     * Initializes a menu item with a target/_selector
	     */
	    protected CCMenuItem(Object rec, String cb) {
	        _target = rec;
	        _selector = cb;

	        setAnchorPoint(CCPoint.ccp(0.5f, 0.5f));

	        invocation = null;
	        if (rec != null && cb != null)
	            try {
	                Class<?> cls = rec.getClass();
	                invocation = cls.getMethod(cb);
	            } catch (Exception e) {
	                e.printStackTrace();
	            }

	        _isEnabled = true;
	        _isSelected = false;
	    }

	    /**
	     * Activate the item
	     */
	    public void activate() {
	        if (_isEnabled) {
	            if (_target != null && invocation != null) {
	                try {
	                    invocation.invoke(_target);
	                } catch (Exception e) {
	                	e.printStackTrace();
	                }
	            }
	        }
	    }

	    /**
	     * The item was selected (not activated), similar to "mouse-over"
	     */
	    public void selected() {
	        _isSelected = true;
	    }

	    /**
	     * The item was unselected
	     */
	    public void unselected() {
	        _isSelected = false;
	    }

	    /**
	     * Enable or disabled the MenuItem
	     */
	    public void setIsEnabled(boolean enabled) {
	        _isEnabled = enabled;
	    }


	    /**
	     * Returns whether or not the MenuItem is enabled
	     */
	    public boolean _isEnabled() {
	        return _isEnabled;
	    }

	    /**
	     * Returns the outside box
	     */
	    public CCRect rect() {
	    	if(_isRelativeAnchorPoint){
		        return CCRect.make(position().x - width() * anchorPoint().x,
		        				   position().y - height() * anchorPoint().y,
		        				   width(),
		        				   height());
	    	}else{
		        return CCRect.make(position().x,
     				   position().y,
     				   width(),
     				   height());	    		
	    	}
	    }

		public boolean containsPoint(CCPoint location) {
	           CCPoint local = convertToNodeSpace(location);
	            CCRect r = CCRect.make(0, 0, width(), height());//item.rect();
	            return CCRect.containsPoint(r, local);
		}


}
