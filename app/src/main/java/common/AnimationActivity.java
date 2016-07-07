package common;

import android.app.Activity;

import com.moandroid.cocos2d.renderers.CCDirector;
import com.moandroid.cocos2d.renderers.CCDirector2D;

/**
 * Created by renjiaozhou on 2016/7/7.
 */
public class AnimationActivity extends Activity {

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CCDirector2D.sharedDirector().end();
    }

    @Override
    protected void onPause() {
        super.onPause();
        CCDirector.sharedDirector().pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        CCDirector.sharedDirector().resume();
    }
}
