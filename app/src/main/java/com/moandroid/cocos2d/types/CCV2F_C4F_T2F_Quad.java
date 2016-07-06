package com.moandroid.cocos2d.types;

public class CCV2F_C4F_T2F_Quad {
	public CCV2F_C4F_T2F tl;
	public CCV2F_C4F_T2F tr;
	public CCV2F_C4F_T2F bl;
	public CCV2F_C4F_T2F br;
	public CCV2F_C4F_T2F_Quad(	CCV2F_C4F_T2F _tl, CCV2F_C4F_T2F _tr,
								CCV2F_C4F_T2F _bl, CCV2F_C4F_T2F _br){
		tl = new CCV2F_C4F_T2F(_tl.vertices, _tl.colors, _tl.texCoords);
		tr = new CCV2F_C4F_T2F(_tr.vertices, _tr.colors, _tr.texCoords);
		bl = new CCV2F_C4F_T2F(_bl.vertices, _bl.colors, _bl.texCoords);
		br = new CCV2F_C4F_T2F(_br.vertices, _br.colors, _br.texCoords);
		
	}
	
	public String toString(){
		return "< tl = " + tl + ", tr = " + tr + ", bl = " + bl + ", br = " + br + " >";
	}
}
