package com.moandroid.cocos2d.actions.interval;

import android.graphics.Path;
import android.graphics.PathMeasure;

import com.moandroid.cocos2d.nodes.CCNode;
import com.moandroid.cocos2d.types.CCPoint;

/**
 * Created by renjiaozhou on 2016/7/14.
 */
public class PathAnimation  extends CCIntervalAction{


    private PathMeasure mPm;
    private float mDistance;
    private Path mPath;

    public static PathAnimation action(float d, Path path){
        return new PathAnimation(d,path);
    }

    protected PathAnimation(float d) {
        super(d);
    }

    public PathAnimation(float d,Path path){
        super(d);
        mPath = path;
    }

    @Override
    public void start(CCNode target) {
        super.start(target);
        mPm = new PathMeasure(mPath,false);
        mDistance = mPm.getLength();
    }

    @Override
    public void update(float t) {
        //super.update(t);
        float [] pos = new float[2];
        float [] tan = new float[2];
        mPm.getPosTan(mDistance*t,pos,tan);
        _target.setPosition(CCPoint.ccp(pos[0],pos[1]));
    }

}
