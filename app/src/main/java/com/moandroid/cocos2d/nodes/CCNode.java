package com.moandroid.cocos2d.nodes;

import java.util.ArrayList;
import java.util.HashMap;

import javax.microedition.khronos.opengles.GL10;

import junit.framework.Assert;

import android.util.Log;
import android.view.MotionEvent;

import com.moandroid.cocos2d.actions.CCAction;
import com.moandroid.cocos2d.actions.CCActionManager;
import com.moandroid.cocos2d.actions.CCTimer;
import com.moandroid.cocos2d.camera.CCCamera;

import com.moandroid.cocos2d.nodes.protocol.CCNodeSizeProtocol;
import com.moandroid.cocos2d.renderers.CCDirector;
import com.moandroid.cocos2d.renderers.CCScheduler;
import com.moandroid.cocos2d.types.CCAffineTransform;
import com.moandroid.cocos2d.types.CCPoint;
import com.moandroid.cocos2d.types.CCRect;
import com.moandroid.cocos2d.types.CCSize;
import com.moandroid.cocos2d.util.CCFormatter;
import com.moandroid.cocos2d.util.CCUtils;

public class CCNode implements CCNodeSizeProtocol {
    public static final String LOG_TAG = CCNode.class.getSimpleName();

    public static final int INVALID_TAG = -1;

    public static CCNode node(){
        return new CCNode();
    }

    protected int _tag = INVALID_TAG;
    public int tag(){
        return _tag;
    }
    public void setTag(int t){
        _tag = t;
    }

    protected boolean _visible = true;
    public boolean visible(){
        return _visible;
    }
    public void setVisible(boolean v){
        _visible = v;
    }

    protected boolean _isRunning = false;
    public boolean isRunning(){
        return _isRunning;
    }
    public void setIsRunning(boolean r){
        _isRunning = r;
    }

    protected CCNode _parent;
    public CCNode parent(){
        return _parent;
    }
    public void setParent(CCNode p){
        _parent = p;
    }

    protected float _vertexZ = 0;
    public float vertexZ(){
        return _vertexZ;
    }
    public void setVertexZ(float v){
        _vertexZ = v;
    }

    //CCNode - Transform related properties
    protected float _rotation = 0.0f;
    public float rotation(){
        return _rotation;
    }
    public void setRotation(float r){
        _rotation = r;
        _isTransformDirty = _isInverseDirty = true;
    }

    protected float _scaleX = 1.0f;
    protected float _scaleY = 1.0f;
    public float scale(){
        Assert.assertTrue("CCNode#scale. ScaleX != ScaleY. Don't know which one to return", _scaleX == _scaleY);
        return _scaleX;
    }
    public void setScale(float s){
        _scaleX = _scaleY = s;
        _isTransformDirty = _isInverseDirty = true;
    }
    public float scaleX(){
        return _scaleX;
    }
    public void setScaleX(float s){
        _scaleX = s;
        _isTransformDirty = _isInverseDirty = true;
    }
    public float scaleY(){
        return _scaleY;
    }
    public void setScaleY(float s){
        _scaleY = s;
        _isTransformDirty = _isInverseDirty = true;
    }

    protected CCPoint _position = CCPoint.zero();
    public void setPosition(float x, float y) {
        _position.x = x;
        _position.y = y;
        _isTransformDirty = _isInverseDirty = true;
    }
    public void setPosition(CCPoint pt){
        _position.x = pt.x;
        _position.y = pt.y;
        _isTransformDirty = _isInverseDirty = true;
    }
    public CCPoint position(){
        return _position;
    }

    protected int _zOrder = 0;
    public int zOrder(){
        return _zOrder;
    }
    public void setZOrder(int z){
        _zOrder = z;
    }

    protected CCPoint _anchorPointInPixels = CCPoint.zero();
    public CCPoint anchorPointInPixels(){
        return _anchorPointInPixels;
    }

    protected boolean _isRelativeAnchorPoint = true;
    public boolean isRelativeAnchorPoint(){
        return _isRelativeAnchorPoint;
    }
    public void setIsRelativeAnchorPoint(boolean b){
        _isRelativeAnchorPoint = b;
        _isTransformDirty = _isInverseDirty = true;
    }

    protected CCPoint _anchorPoint = CCPoint.zero();
    public CCPoint anchorPoint(){
        return _anchorPoint;
    }
    public void setAnchorPoint(CCPoint pt){
        if(!CCPoint.equalToPoint(pt, _anchorPoint)){
            _anchorPoint = pt;
            _anchorPointInPixels = CCPoint.ccp(
                    _contentSize.width * _anchorPoint.x,
                    _contentSize.height * _anchorPoint.y);
            _isTransformDirty = _isInverseDirty = true;
        }
    }

    protected CCSize _contentSize = CCSize.zero();
    public CCSize contentSize(){
        return _contentSize;
    }
    public void setContentSize(CCSize s){
        if(!CCSize.equalToSize(s, _contentSize)){
            _contentSize = s;
            _anchorPointInPixels = CCPoint.ccp(
                    _contentSize.width * _anchorPoint.x,
                    _contentSize.height * _anchorPoint.y);
            _isTransformDirty = _isInverseDirty = true;
        }
    }

    public CCRect boundingBox(){
        CCRect rect = CCRect.make(0, 0, _contentSize.width, _contentSize.height);
        return CCRect.rectApplyAffineTransform(rect, nodeToParentTransform());
    }

    //private boolean _cleanup = false;
    public void cleanup(){
        //_cleanup = true;
        stopAllActions();
        _scheduledTimers = null;
        if(_children != null){
            for(CCNode c : _children){
                c.cleanup();
            }
        }
    }

    public String toString(){
        return CCFormatter.format("<%s = %08X | Tag = %i>", CCNode.class, _tag);
    }

//CCNode Composition

    protected ArrayList<CCNode> _children;
    public ArrayList<CCNode> getChildren(){
        return _children;
    }

    private void childrenAlloc(){
        _children = new ArrayList<CCNode>(4);
    }

    public CCNode getChild(int tag) {
        Assert.assertTrue("Invalid tag", tag != INVALID_TAG);
        if(_children != null){
            for(CCNode node : _children){
                if(node.tag() == tag)
                    return node;
            }
        }
        return null;
    }

    public CCNode addChild(CCNode child, int z, int tag) {
        Assert.assertTrue("Argument must be non-nil", child != null);
        Assert.assertTrue("child already added. It can't be added again", child.parent() == null);

        if(_children == null){
            childrenAlloc();
        }
        insertChild(child, z);
        child.setTag(tag);
        child.setParent(this);
        if(_isRunning)
            child.onEnter();
        return child;
    }
    public CCNode addChild(CCNode child, int z) {
        Assert.assertTrue("Argument must be non-nil", child != null);
        return addChild(child, z, child.tag());
    }
    public CCNode addChild(CCNode child) {
        Assert.assertTrue("Argument must be non-nil", child != null);
        return addChild(child, child.zOrder(), child.tag());
    }

    public /*synchronized*/ void insertChild(CCNode child, int z){
        Assert.assertTrue("Child must be non-nil", child != null);
        boolean added = false;
        for (int i = 0; i < _children.size(); i++) {
            CCNode c = _children.get(i);
            if(c == null) continue;
            if(c.zOrder() > z){
                added = true;
                _children.add(i, child);
                break;
            }
        }
        if(added == false){
            _children.add(child);
        }
        child.setZOrder(z);
    }

    public /*synchronized*/ void reorderChild(CCNode child, int zOrder){
        Assert.assertTrue("Child must be non-nil", child != null);
        _children.remove(child);
        insertChild(child,zOrder);
    }

    public void removeChild(CCNode child, boolean cleanup) {
        if(child == null) return;
        if(_children != null && _children.contains(child)){
            detachChild(child, cleanup);
        }
    }
    public void removeChild(int tag, boolean cleanup) {
        Assert.assertTrue("Invalid tag", tag != INVALID_TAG);
        CCNode child = getChild(tag);
        if(child == null)
            Log.v(LOG_TAG, "cocos2d: removeChild: child not found!");
        else
            removeChild(child, cleanup);
    }
    public void removeAllChildren(boolean cleanup) {
        if(_children == null)
            return;
        for(CCNode c : _children){
            if(_isRunning)
                c.onExit();
            if(cleanup)
                c.cleanup();
            c.setParent(null);
        }
    }

    public /*synchronized*/ void detachChild(CCNode child, boolean doCleanup){
        Assert.assertTrue("Child must be non-nil", child != null);

        if(_isRunning){
            child.onExit();
        }
        if(doCleanup){
            child.cleanup();
        }
        child.setParent(null);
        _children.remove(child);
    }

    public void draw(GL10 gl){

    }
    public /*synchronized*/ void visit(GL10 gl){
        if(_visible == false)// || _cleanup == true)
            return;
        gl.glPushMatrix();
        transform(gl);//将世界坐标系转变为当前的物体的坐标系并绘制
        int i = 0;

        // 如果有子孩子先进行绘制 zOrder < 0 的
        if(_children != null){
            for(i = 0; i<_children.size(); ++i){
                CCNode c = _children.get(i);
                if(c.zOrder()<0)
                    c.visit(gl);
                else
                    break;
            }
        }
        //绘制自己
        draw(gl);
        if(_children != null){
            for(;i<_children.size(); ++i){
                CCNode c = _children.get(i);
                if(c.zOrder()>=0)
                    c.visit(gl);
                else
                    break;
            }
        }
        gl.glPopMatrix();
    }
    //CCNode - Transformations
    public void transform(GL10 gl){
        //当 relativeAnchorPoint = true 设置锚点位置为场景绘制原点
        if (_isRelativeAnchorPoint && (_anchorPointInPixels.x != 0 || _anchorPointInPixels.y != 0)) {
            gl.glTranslatef(-_anchorPointInPixels.x, -_anchorPointInPixels.y, 0);
        }

        // 根据锚点设置场景位置
        if (_anchorPointInPixels.x != 0 || _anchorPointInPixels.y != 0) {
            gl.glTranslatef(_position.x + _anchorPointInPixels.x, _position.y + _anchorPointInPixels.y, _vertexZ);
        } else if (_position.x != 0 || _position.y != 0) {
            gl.glTranslatef(_position.x, _position.y, _vertexZ);
        }

        //设置旋转
        if (_rotation != 0.0f) {
            gl.glRotatef(-_rotation, 0.0f, 0.0f, 1.0f);
        }

        //设置缩放
        if (_scaleX != 1.0f || _scaleY != 1.0f) {
            gl.glScalef(_scaleX, _scaleY, 1.0f);
        }

        // restore and reposition_ point【可以理解为场景的左下角为原点（当前局部坐标的0,0点）】
        if (_anchorPointInPixels.x != 0 || _anchorPointInPixels.y != 0.0f) {
            gl.glTranslatef(-_anchorPointInPixels.x, -_anchorPointInPixels.y, 0);
        }
    }

    //CCNode Actions

    public void onEnter(){
        if(_children != null){
            for(CCNode c : _children){
                c.onEnter();
            }
            activateTimers();
        }
        _isRunning = true;
    }

    public void onExit(){
        deactivateTimers();
        _isRunning = false;
        if(_children != null)
            for(CCNode c : _children){
                c.onExit();
            }
    }

    public CCAction runAction(CCAction action){
        Assert.assertTrue("Argument must be non-nil", action!= null);
        CCActionManager.sharedManager().addAction(action, this);
        return action;
    }

    public void stopAllActions(){
        CCActionManager.sharedManager().removeAllActions(this);
    }

    public void stopAction(CCAction action){
        CCActionManager.sharedManager().removeAction(action);
    }

    public void stopAction(int tag){
        Assert.assertTrue("Invalid tag", tag != INVALID_TAG);
        CCActionManager.sharedManager().removeAction(tag, this);
    }

    public CCAction getAction(int tag){
        Assert.assertTrue("Invalid tag", tag != INVALID_TAG);
        return CCActionManager.sharedManager().getAction(tag, this);
    }

    public int numberOfRunningActions(){
        return CCActionManager.sharedManager().numberOfRunningActions(this);
    }

    private HashMap<String, CCTimer> _scheduledTimers;

    private void allocTimers(){
        _scheduledTimers = new HashMap<String, CCTimer>(2);
    }

    public void schedule(String sel){
        float t = 0;
        schedule(sel, t);
    }

    public void schedule(String selector, float interval){
        Assert.assertTrue("Argument must be non-nil", selector != null);
        Assert.assertTrue("Arguemnt must be positive", interval>=0);

        if(_scheduledTimers == null){
            allocTimers();
        }

        String key = this.getClass().getName() + "." + selector + "(float)";
        if (_scheduledTimers.containsKey(key)) {
            return;
        }

        CCTimer timer = new CCTimer(this, selector, interval);

        if (_isRunning)
            CCScheduler.sharedScheduler().schedule(timer);

        _scheduledTimers.put(key, timer);
    }

    public void unschedule(String selector){
        // explicit null handling
        if (selector == null)
            return;

        if (_scheduledTimers == null)
            return;

        CCTimer timer;

        String key = this.getClass().getName() + "." + selector + "(float)";
        if ((timer = _scheduledTimers.get(key)) == null) {
            return;
        }

        _scheduledTimers.remove(key);
        if (_isRunning)
            CCScheduler.sharedScheduler().unschedule(timer);
    }

    public void activateTimers(){
        if (_scheduledTimers != null)
            for (String key : _scheduledTimers.keySet())
                CCScheduler.sharedScheduler().schedule(_scheduledTimers.get(key));

        CCActionManager.sharedManager().resumeAllActions(this);
    }

    public void deactivateTimers(){
        if (_scheduledTimers != null)
            for (String key : _scheduledTimers.keySet())
                CCScheduler.sharedScheduler().unschedule(_scheduledTimers.get(key));

        CCActionManager.sharedManager().pauseAllActions(this);
    }

    //CCNode Transform
    private CCAffineTransform _transform;
    private CCAffineTransform _inverse;
    private boolean _isTransformDirty = true;
    private boolean _isInverseDirty = true;

    public CCAffineTransform nodeToParentTransform(){
        if (_isTransformDirty) {
            _transform = CCAffineTransform.identity();
            if (!_isRelativeAnchorPoint) {
                _transform.translate((int) _anchorPointInPixels.x, (int) _anchorPointInPixels.y);
            }
            _transform.translate((int) _position.x, (int) _position.y);
            _transform.rotate(-CCUtils.degreesToRadians(_rotation));
            _transform.scale(_scaleX, _scaleY);
            _transform.translate(-(int) _anchorPointInPixels.x, -(int) _anchorPointInPixels.y);
            _isTransformDirty = false;
        }
        return _transform;
    }

    public CCAffineTransform parentToNodeTransform(){
        if (_isInverseDirty) {
            _inverse = nodeToWorldTransform().createInverse();

            _isInverseDirty = false;
        }
        return _inverse;
    }

    public CCAffineTransform nodeToWorldTransform() {
        CCAffineTransform t = nodeToParentTransform();
        for (CCNode p = _parent; p != null; p = p._parent)
            t.concatenate(p.nodeToParentTransform());
        return t;
    }

    public CCAffineTransform worldToNodeTransform(){
        return nodeToWorldTransform().createInverse();
    }

    public CCPoint convertToNodeSpace(CCPoint worldPoint){
        return CCPoint.applyAffineTransform(worldPoint, worldToNodeTransform());
    }

    public CCPoint convertToWorldSpace(CCPoint nodePoint){
        return CCPoint.applyAffineTransform(nodePoint, nodeToWorldTransform());
    }

    public CCPoint convertToNodeSpaceAR(CCPoint worldPoint){
        CCPoint nodePoint = convertToNodeSpace(worldPoint);
        return CCPoint.ccpSub(nodePoint, _anchorPointInPixels);
    }

    public CCPoint convertToWorldSpaceAR(CCPoint nodePoint){
        nodePoint = CCPoint.ccpAdd(nodePoint, _anchorPointInPixels);
        return convertToWorldSpace(nodePoint);
    }

    public CCPoint convertTouchToNodeSpace(MotionEvent event){
        CCPoint point = CCDirector.sharedDirector().convertToGL(CCPoint.ccp(event.getX(), event.getY()));
        return convertToNodeSpace(point);
    }

    public CCPoint convertTouchToNodeSpaceAR(MotionEvent event){
        CCPoint point = CCDirector.sharedDirector().convertToGL(CCPoint.ccp(event.getX(), event.getY()));
        return convertToNodeSpaceAR(point);
    }

    protected CCCamera _camera = new CCCamera();
    public CCCamera camera() {
        return _camera;
    }
    @Override
    public float height() {

        return _contentSize.height;
    }
    @Override
    public float width() {

        return _contentSize.width;
    }
}
