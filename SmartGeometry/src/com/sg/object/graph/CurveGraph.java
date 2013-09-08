package com.sg.object.graph;

import java.io.Serializable;
import java.util.List;

import com.sg.object.Point;
import com.sg.object.unit.CurveUnit;
import com.sg.object.unit.GUnit;

public class CurveGraph extends Graph implements Serializable {

	// 一般方程：aX^2 + bY^2 + cXY + dX + eY + f = 0
	
	public CurveGraph() {
//		translationStratery = new CurveStrategy();  //选择线变换策略
	}
	
	public CurveGraph(List<Point> pList) {
		buildGraph(new CurveUnit(pList));
//		translationStratery = new CurveStrategy();  //选择线变换策略
	}

	public void bulidCurveGraph(List<GUnit> units) {
		for(GUnit unit : units) {
			buildGraph(unit);
		} 
	}
	@Override
	public void move(float mx, float my) {
		// TODO Auto-generated method stub
	}

}
