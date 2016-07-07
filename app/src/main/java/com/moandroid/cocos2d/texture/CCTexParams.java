package com.moandroid.cocos2d.texture;

public class CCTexParams {

    public int minFilter;
    public int magFilter;
    public int wrapS;
    public int wrapT;

    public CCTexParams(int min, int mag, int s, int t) {
        minFilter = min;
        magFilter = mag;
        wrapS = s;
        wrapT = t;
    }
}
