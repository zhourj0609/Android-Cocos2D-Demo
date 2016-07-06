package com.moandroid.cocos2d.nodes;

import com.moandroid.cocos2d.texture.CCTexture2D;
import com.moandroid.cocos2d.types.CCGridSize;
import com.moandroid.cocos2d.types.CCPoint;

public class CCGridBase extends CCNode{

	public CCGridBase(CCGridSize gridSize, CCTexture2D texture, boolean isFlipped) {
		// TODO Auto-generated constructor stub
	}
	public CCGridBase(CCGridSize gridSize) {
		// TODO Auto-generated constructor stub
	}
	public static CCGridBase grid(CCGridSize gridSize, CCTexture2D texture, boolean isFlipped){
		return new CCGridBase(gridSize, texture, isFlipped);
	}
	
	public static CCGridBase grid(CCGridSize gridSize){
		return new CCGridBase(gridSize);
	}	
	
	public void blit(){
		
	}
	
	public void reuse(){
	}
	
	public void calculateVertexPoints(){
		
	}
	


	public void beforeDraw() {
		// TODO Auto-generated method stub
		
	}

	public void afterDraw() {
		// TODO Auto-generated method stub
		
	}
	
	private boolean _active; 
	public boolean active() {
		return _active;
	}
	public void setActive(boolean isActive){
		_active = isActive;
	}
	
	private int _reuseGrid;
	public int reuseGrid(){
		return _reuseGrid;
	}
	public void setReuseGrid(int grid){
		_reuseGrid = grid;
	}
	
	private CCGridSize _gridSize;
	public CCGridSize gridSize(){
		return _gridSize;
	}
	
	private CCPoint _step;
	public CCPoint step(){
		return _step;
	}
	public void setStep(CCPoint pt){
		_step = pt;
	}

	private CCTexture2D _texture;
	public void setTexture(CCTexture2D tx){
		_texture = tx;
	}
	
	private boolean _isTextureFlipped;
	public boolean isTextureFlipped(){
		return _isTextureFlipped;
	}
	public void setTextureFlipped(boolean isFlipped){
		_isTextureFlipped = isFlipped;
	}
}
