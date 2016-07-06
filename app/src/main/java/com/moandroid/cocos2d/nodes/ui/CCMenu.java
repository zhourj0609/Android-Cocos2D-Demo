package com.moandroid.cocos2d.nodes.ui;

import java.util.ArrayList;

import com.moandroid.cocos2d.events.CCEvent;
import com.moandroid.cocos2d.events.CCTouchDispatcher;
import com.moandroid.cocos2d.events.CCTouchEvent;
import com.moandroid.cocos2d.nodes.CCLayer;
import com.moandroid.cocos2d.renderers.CCDirector;
import com.moandroid.cocos2d.types.CCPoint;
import com.moandroid.cocos2d.types.CCSize;
import com.moandroid.cocos2d.util.CCUtils;


public class CCMenu extends CCLayer {
	public static final String LOG_TAG = CCMenu.class.getSimpleName();
	public static final int kDefaultPadding = 5;
	public static final int kMenuStateWaiting = 1;
	public static final int kMenuStateTrackingTouch = 2;
	private CCMenuItem _selectedItem;
	private int state;


    public CCMenuItem selectedItem() {
        return _selectedItem;
    }

    public void setSelectedItem(CCMenuItem selectedItem) {
        _selectedItem = selectedItem;
    }

    @Override
    protected void registerWithTouchDispatcher() {
        CCTouchDispatcher.sharedDispatcher().addDelegate(this, CCUtils.INT_MIN+1);
    }

	    
    /**
     * creates a menu with its items
     */
    public static CCMenu menu(CCMenuItem... items) {
        return new CCMenu(items);
    }

    protected CCMenu(CCMenuItem... items) {
        // menu in the center of the screen
        CCSize s = CCDirector.sharedDirector().winSize();

        setPosition(s.width / 2, s.height / 2);

        setIsRelativeAnchorPoint(false);
        setAnchorPoint(CCPoint.ccp(0.5f, 0.5f));
        setContentSize(CCSize.make(s.width, s.height));

        _isTouchEnabled = true;

        int z = 0;
        for (int i = 0; i < items.length; i++) {
        	CCMenuItem item = items[i];
            addChild(item, z);
            ++z;
        }

        _selectedItem = null;
        state = kMenuStateWaiting;

    }

    // Menu - Events

    @Override
    public boolean touchesBegan(CCEvent event) {

        if (state != kMenuStateWaiting)
            return false;

        _selectedItem = itemForTouch((CCTouchEvent)event);

        if (_selectedItem != null) {
            _selectedItem.selected();
            state = kMenuStateTrackingTouch;
            return true;
        }

        return false;
    }

    @Override
    public boolean touchesEnded(CCEvent event) {
        if (state == kMenuStateTrackingTouch) {
            if (_selectedItem != null) {
                _selectedItem.unselected();
                _selectedItem.activate();
            }

            state = kMenuStateWaiting;
            return true;
        }

        return false;
    }

    @Override
    public boolean touchesCancelled(CCEvent event) {
        if (state == kMenuStateTrackingTouch) {
            if (_selectedItem != null) {
                _selectedItem.unselected();
            }

            state = kMenuStateWaiting;
            return true;
        }

        return false;
    }

    @Override
    public boolean touchesMoved(CCEvent event) {
        if (state == kMenuStateTrackingTouch) {
            CCMenuItem currentItem = itemForTouch((CCTouchEvent) event);

            if (currentItem != _selectedItem) {
                if (_selectedItem != null) {
                    _selectedItem.unselected();
                }
                _selectedItem = currentItem;
                if (_selectedItem != null) {
                    _selectedItem.selected();
                }
            }
            return true;
        }

        return false;
    }


    // Menu - Alignment

    /**
     * align items vertically
     */
    public void alignItemsVertically() {
        alignItemsVertically(kDefaultPadding);
    }

    /**
     * align items vertically with padding
     */
    public void alignItemsVertically(float padding) {
        float height = -padding;
        for (int i = 0; i < _children.size(); i++) {
            CCMenuItem item = (CCMenuItem) _children.get(i);
            height += item.height() * item.scaleY() + padding;
        }

        float y = height / 2.0f;
        for (int i = 0; i < _children.size(); i++) {
            CCMenuItem item = (CCMenuItem) _children.get(i);
            item.setPosition(0, y - item.height() * item.scaleY() / 2.0f);
            y -= item.height() * item.scaleY() + padding;
        }
    }

    /**
     * align items horizontally
     */
    public void alignItemsHorizontally() {
        alignItemsHorizontally(kDefaultPadding);
    }

    /**
     * align items horizontally with padding
     */
    public void alignItemsHorizontally(float padding) {

        float width = -padding;
        for (int i = 0; i < _children.size(); i++) {
            CCMenuItem item = (CCMenuItem) _children.get(i);
            width += item.width() * item.scaleX() + padding;
        }

        float x = width / 2.0f;
        for (int i = 0; i < _children.size(); i++) {
            CCMenuItem item = (CCMenuItem) _children.get(i);
            item.setPosition(x - item.width() * item.scaleX() / 2.0f, 0);
            x -= item.width() * item.scaleX() + padding;
        }
    }

    /**
     * align items in rows of columns
     */
    public void alignItemsInColumns(int columns[]) {
        ArrayList<Integer> rows = new ArrayList<Integer>();
        for (int i = 0; i < columns.length; i++) {
            rows.add(columns[i]);
        }

        int height = -5;
        int row = 0, rowHeight = 0, columnsOccupied = 0, rowColumns;
        for (int i = 0; i < _children.size(); i++) {
            CCMenuItem item = (CCMenuItem) _children.get(i);
            assert row < rows.size() : "Too many menu items for the amount of rows/columns.";

            rowColumns = rows.get(row);
            assert rowColumns != 0 : "Can't have zero columns on a row";

            rowHeight = (int) Math.max(rowHeight, item.height());
            ++columnsOccupied;

            if (columnsOccupied >= rowColumns) {
                height += rowHeight + 5;

                columnsOccupied = 0;
                rowHeight = 0;
                ++row;
            }
        }


        CCSize winSize = CCDirector.sharedDirector().winSize();

        row = 0;
        rowHeight = 0;
        rowColumns = 0;
        float w = 0, x = 0, y = height / 2;
        for (int i = 0; i < _children.size(); i++) {
            CCMenuItem item = (CCMenuItem) _children.get(i);
            if (rowColumns == 0) {
                rowColumns = rows.get(row);
                w = winSize.width / (1 + rowColumns);
                x = w;
            }

            rowHeight = Math.max(rowHeight, (int) item.height());
            item.setPosition(x - winSize.width / 2, y - item.height() / 2);

            x += w + 10;
            ++columnsOccupied;

            if (columnsOccupied >= rowColumns) {
                y -= rowHeight + 5;

                columnsOccupied = 0;
                rowColumns = 0;
                rowHeight = 0;
                ++row;
            }
        }
    }

    /**
     * align items in columns of rows
     */

    public void alignItemsInRows(int rows[]) {
        ArrayList<Integer> columns = new ArrayList<Integer>();
        for (int i = 0; i < rows.length; i++) {
            columns.add(rows[i]);
        }

        ArrayList<Integer> columnWidths = new ArrayList<Integer>();
        ArrayList<Integer> columnHeights = new ArrayList<Integer>();

        int width = -10, columnHeight = -5;
        int column = 0, columnWidth = 0, rowsOccupied = 0, columnRows;
        for (int i = 0; i < _children.size(); i++) {
            CCMenuItem item = (CCMenuItem) _children.get(i);


            columnRows = columns.get(column);


            columnWidth = (int) Math.max(columnWidth, item.width());
            columnHeight += item.height() + 5;
            ++rowsOccupied;

            if (rowsOccupied >= columnRows) {
                columnWidths.add(columnWidth);
                columnHeights.add(columnHeight);
                width += columnWidth + 10;

                rowsOccupied = 0;
                columnWidth = 0;
                columnHeight = -5;
                ++column;
            }
        }



        CCSize winSize = CCDirector.sharedDirector().winSize();

        column = 0;
        columnWidth = 0;
        columnRows = 0;
        float x = -width / 2, y = 0;
        for (int i = 0; i < _children.size(); i++) {
            CCMenuItem item = (CCMenuItem) _children.get(i);
            if (columnRows == 0) {
                columnRows = columns.get(column);
                y = columnHeights.get(column) + winSize.height / 2;
            }

            columnWidth = (int) Math.max(columnWidth, item.width());
            item.setPosition(x + columnWidths.get(column) / 2, y - winSize.height / 2);

            y -= item.height() + 10;
            ++rowsOccupied;

            if (rowsOccupied >= columnRows) {
                x += columnWidth + 5;

                rowsOccupied = 0;
                columnRows = 0;
                columnWidth = 0;
                ++column;
            }
        }
    }

    private CCMenuItem itemForTouch(CCTouchEvent event) {
        CCPoint touchLocation = CCDirector.sharedDirector().convertToGL(event.x, event.y);
        for (int i = 0; i < _children.size(); i++) {
            CCMenuItem item = (CCMenuItem) _children.get(i);
            if(item.containsPoint(touchLocation)){
            	return item;
            }
        }
        return null;
    }

}
