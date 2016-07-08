package com.moandroid.cocos2d.actions;

import java.lang.reflect.Method;

public class CCTimer {
    private Object _target;//
    private String _selector;
    private Method _invocation;
    public float _interval;// interval call time
    float _elapsed;

    public CCTimer(Object target, String selector) {
        this(target, selector, 0);
    }

    public CCTimer(Object target, String selector, float seconds) {

        _target = target;
        _selector = selector;
        _interval = seconds;
        try {
            Class<?> cls = _target.getClass();
            _invocation = cls.getMethod(_selector, Float.TYPE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void set_interval(float i) {
        _interval = i;
    }

    public float get_interval() {
        return _interval;
    }

    public void fire(float dt) {
        _elapsed += dt;
        if (_elapsed >= _interval) {
            try {
                _invocation.invoke(_target, _elapsed);
            } catch (Exception e) {
                e.printStackTrace();
            }
            _elapsed = 0;
        }
    }

	public static CCTimer timer(Object target, String selector) {
		return new CCTimer(target, selector);
	}
}

