package com.moandroid.cocos2d.types;

public class CCV3F_C4B_T2F_Quad {
	public CCV3F_C4B_T2F tl;
	public CCV3F_C4B_T2F tr;
	public CCV3F_C4B_T2F bl;
	public CCV3F_C4B_T2F br;
	public CCV3F_C4B_T2F_Quad(	CCV3F_C4B_T2F _tl, CCV3F_C4B_T2F _tr,
								CCV3F_C4B_T2F _bl, CCV3F_C4B_T2F _br){
		tl = new CCV3F_C4B_T2F(_tl.vertices, _tl.colors, _tl.texCoords);
		tr = new CCV3F_C4B_T2F(_tr.vertices, _tr.colors, _tr.texCoords);
		bl = new CCV3F_C4B_T2F(_bl.vertices, _bl.colors, _bl.texCoords);
		br = new CCV3F_C4B_T2F(_br.vertices, _br.colors, _br.texCoords);
		
	}
	
	public String toString(){
		return "< tl = " + tl + ", tr = " + tr + ", bl = " + bl + ", br = " + br + " >";
	}
}
