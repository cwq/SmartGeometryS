package com.sg.transformation.computeagent;

import java.util.List;

import com.sg.control.GraphControl;
import com.sg.logic.common.CommonFunc;
import com.sg.object.graph.Graph;
import com.sg.object.graph.LineGraph;
import com.sg.object.graph.RectangleGraph;
import com.sg.object.graph.TriangleGraph;
import com.sg.object.unit.GUnit;
import com.sg.object.unit.LineUnit;
import com.sg.object.unit.PointUnit;
import com.sg.property.common.ThresholdProperty;

public class LineToLineConstraint {

	public LineToLineConstraint() {
		
	}
	
	//直线图形curGraph与直线图形graph的约束
	public Graph lineToLineConstrain(Graph constraintGraph, Graph curGraph){
		double minDist = ThresholdProperty.TWO_POINT_IS_CONSTRAINTED;
		PointUnit constraintPointUnit = null;
		PointUnit otherConstraintPointUnit = null;
		//GUnit curUnit;
		
		int num = constraintGraph.getGraph().size();
		PointUnit firstPointUnit = (PointUnit) constraintGraph.getGraph().get(0);    //约束图形的第一个点元
		PointUnit lastPointUnit = (PointUnit) constraintGraph.getGraph().get(num - 1);  //约束图形的最后一个点元

		if(this.lineToPoint((LineGraph) curGraph, firstPointUnit) < minDist){      //发生约束的是第一个点
			constraintPointUnit = firstPointUnit;
		}else{
			if(this.lineToPoint((LineGraph) curGraph, lastPointUnit) < minDist){     //发生约束的是最后一个点
				constraintPointUnit = lastPointUnit;
			}else{
				return null;           //没有约束
			}
		}
		boolean isALine = (num == 3);    //如果约束图形是一条直线直线，只需进行一个点的约束识别，如果识别两点会造成约束图形与被约束图形直线重合
		PointUnit otherPointUnit = getOtherPointUnit(curGraph, constraintPointUnit);  //被约束图形（直线）的另一个点元
		otherPointUnit.clearDegree();    //成为一个独立的点
		
		if(!isALine) {
			if(constraintPointUnit == firstPointUnit){          //如果约束点是第一个点
				if(CommonFunc.distance(lastPointUnit, otherPointUnit) < minDist){         //有另一个约束点，则是闭合图形
					otherConstraintPointUnit = lastPointUnit;
					constraintGraph.buildGraph(new LineUnit(constraintPointUnit, otherConstraintPointUnit));
					constraintGraph.setIsClosed(true);
				}else{                                                                    //没有另一个约束点，则图元加在第一个点之前，保持点线点的顺序
					constraintGraph.buildGraph(0, new LineUnit(constraintPointUnit, otherPointUnit));
					constraintGraph.buildGraph(0, otherPointUnit);
				}
			}else{                                             //如果约束点是最后一个点
				if(CommonFunc.distance(firstPointUnit, otherPointUnit) < minDist){
					otherConstraintPointUnit = firstPointUnit;
					constraintGraph.buildGraph(new LineUnit(constraintPointUnit, otherConstraintPointUnit));
					constraintGraph.setIsClosed(true);
				}else{                                                                    //没有另一个约束点，则图元加在最后一个点之后，保持点线点的顺序
					constraintGraph.buildGraph(num, new LineUnit(constraintPointUnit, otherPointUnit));
					constraintGraph.buildGraph(num + 1, otherPointUnit);
				}
			}
		}else{
			if(constraintPointUnit == firstPointUnit){
				constraintGraph.buildGraph(0, new LineUnit(constraintPointUnit, otherPointUnit));
				constraintGraph.buildGraph(0, otherPointUnit);
			}else{
				constraintGraph.buildGraph(num, new LineUnit(constraintPointUnit, otherPointUnit));
				constraintGraph.buildGraph(num + 1, otherPointUnit);
			}
		}
			return constraintGraph;
	}
	
	//重新构建闭合图形
	public Graph rebuildLinearClose(GraphControl graphControl, Graph graph) {
		Graph temp = null;
		if(graph instanceof LineGraph && graph.isClosed()){
			List<GUnit> units = graph.getGraph();
			if(units.size() == 6){
				//重新构造成三角形
				graphControl.deleteGraph(graph);
				temp = new TriangleGraph(units);
				temp.setID(graph.getID());
				graphControl.addGraph(temp);
			}
			if(units.size() == 8){
				//重新构造四边形
				graphControl.deleteGraph(graph);
				temp = new RectangleGraph(units);
				temp.setID(graph.getID());
				graphControl.addGraph(temp);
			}
		}
		return temp;
	}
	
	
	/*
	 * 线段的两个端点到点的最小距离
	 * */
	private double lineToPoint(LineGraph lineGraph, PointUnit pUnit) {
		double[] minDist = new double[2];
		/*
		int i = 0;
		for(GUnit unit : lineGraph.getGraph()) {
			if(unit instanceof PointUnit) {
				minDist[i] = CommonFunc.distance((PointUnit)unit, pUnit);
				i++;
			}
		}
		*/
		//this.constraintPointUnit = pUnit;
		int num = lineGraph.getGraph().size();
		minDist[0] = CommonFunc.distance((PointUnit)lineGraph.getGraph().get(0), pUnit);
		minDist[1] = CommonFunc.distance((PointUnit)lineGraph.getGraph().get(num - 1), pUnit);
		return CommonFunc.min(minDist);
	}
	
	private PointUnit getOtherPointUnit(Graph graph, PointUnit constraintPUnit) {
		//找到直线除了约束的另一个点
		//PointUnit theOther = null;
		/*
		double maxDist = Double.MIN_VALUE;
		double curDist = maxDist;
		
		if(graph instanceof LineGraph) {
			for(GUnit unit : graph.getGraph()) {
				if(unit instanceof PointUnit) {
					curDist = CommonFunc.distance((PointUnit)unit, constraintPUnit);
					if(maxDist < curDist) {
						maxDist = curDist;
						theOther = (PointUnit)unit;
					}
				}
			}
			return theOther;
		}
		
		if(graph instanceof LineGraph){
			for(GUnit unit : graph.getGraph()) {
				if(unit instanceof LineUnit){
					PointUnit starPointUnit = ((LineUnit)unit).getStartPointUnit();
					PointUnit endPointUnit = ((LineUnit)unit).getEndPointUnit();
					//距离远的为另一个点
					if(CommonFunc.distance(starPointUnit, constraintPUnit) < CommonFunc.distance(endPointUnit, constraintPUnit)){
						theOther = endPointUnit;
					}
					else{
						theOther = starPointUnit;
					}
					return theOther;
				}
			}
		}
		return null;
		*/
		//理约束点远的为另一点
		int num = graph.getGraph().size();
		PointUnit starPointUnit = (PointUnit) graph.getGraph().get(0);
		PointUnit endPointUnit = (PointUnit) graph.getGraph().get(num - 1);
		if(CommonFunc.distance(starPointUnit, constraintPUnit) < CommonFunc.distance(endPointUnit, constraintPUnit)){
			return endPointUnit;
		}
		else{
			return starPointUnit;
		}
	}
}
