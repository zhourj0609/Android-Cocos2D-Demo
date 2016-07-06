package com.moandroid.cocos2d.nodes;

import javax.microedition.khronos.opengles.GL10;

import com.moandroid.cocos2d.nodes.protocol.CCLabelProtocol;
import com.moandroid.cocos2d.types.CCQuad2F;
import com.moandroid.cocos2d.types.CCQuad3F;

public class CCLabelAtlas extends CCAtlasNode implements CCLabelProtocol {
    public static CCLabelAtlas label(String theString, String charmapfile, int w, int h, char c) {
        return new CCLabelAtlas(theString, charmapfile, w, h, c);
    }
	
	public CCLabelAtlas(String value, String charMapFile, int width, int height,
			char startCharMap) {
        super(charMapFile, width, height, value.length());

        _string = value;
        _mapStartChar = startCharMap;

        updateAtlasValues();
	}

   @Override
    public void updateAtlasValues() {
        int n = _string.length();

        CCQuad2F texCoord = new CCQuad2F();
        CCQuad3F vertex = new CCQuad3F();

        String s = _string;

        for (int i = 0; i < n; i++) {

            int a = s.charAt(i) - _mapStartChar;
            float row = (a % itemsPerRow()) * texStepX();
            float col = (a / itemsPerRow()) * texStepY();

//            texCoord.tl.x = row;                        // C - x
//            texCoord.tl.y = col + texStepY();                // C - y
// 
//            texCoord.tr.x = row + texStepX();                // D - x
//            texCoord.tr.y = col + texStepY();                // D - y
//
//            texCoord.bl.x = row;                        // A - x
//            texCoord.bl.y = col;                        // A - y
//            
//            texCoord.br.x = row + texStepX();                // B - x
//            texCoord.br.y = col;                        // B - y

//bitmap data is upside-down
			texCoord.bl.x = row;                        // C - x
			texCoord.bl.y = col + texStepY();                // C - y
			
			texCoord.br.x = row + texStepX();                // D - x
			texCoord.br.y = col + texStepY();                // D - y
			
			texCoord.tl.x = row;                        // A - x
			texCoord.tl.y = col;                        // A - y
			
			texCoord.tr.x = row + texStepX();                // B - x
			texCoord.tr.y = col;                        // B - y
			

			

            
            vertex.tl.x = i * itemWidth();                // C - x
            vertex.tl.y = itemHeight();                    // C - y
            vertex.tl.z = 0;                            // C - z
 
            vertex.tr.x = i * itemWidth() + itemWidth();    // D - x
            vertex.tr.y = itemHeight();                    // D - y
            vertex.tr.z = 0;                            // D - z

            vertex.bl.x = i * itemWidth();                // A - x
            vertex.bl.y = 0;                            // A - y
            vertex.bl.z = 0;                            // A - z
            
            vertex.br.x = i * itemWidth() + itemWidth();    // B - x
            vertex.br.y = 0;                            // B - y
            vertex.br.z = 0;                            // B - z

 
            updateQuad(texCoord, vertex, i);
        }
    }
	private String _string;
	char _mapStartChar;
	@Override
	public void setString(String newString) {
        if (newString.length() > totalQuads())
            resizeCapacity(newString.length());

        _string = newString;
        updateAtlasValues();
	}

    @Override
    public void draw(GL10 gl) {
    	draw(gl, _string.length());
    }
}
