package com.sg.logic.strategy;

import java.io.Serializable;
import java.util.List;

import android.util.Log;

import com.sg.logic.common.CommonFunc;
import com.sg.logic.common.CurveType;
import com.sg.object.Point;
import com.sg.object.constraint.ConstraintStruct;
import com.sg.object.constraint.ConstraintType;
import com.sg.object.graph.Graph;
import com.sg.object.unit.CurveUnit;
import com.sg.object.unit.GUnit;
import com.sg.object.unit.PointUnit;
import com.sg.object.unit.LineUnit;
import com.sg.property.common.ThresholdProperty;

public class CurveStrategy implements TranslationStratery, Serializable {
	
	//点在曲线线上移动
	public static void translatePointInCurve(GUnit unit, Graph graph, Point transPoint){
//		CurveUnit curve = null;
//		for(GUnit u : graph.getGraph()) {
//			if(u instanceof CurveUnit && !((CurveUnit) u).isInternallyTangentCircleOfTriangle())
//				curve = (CurveUnit) u;
//		}
		CurveUnit curve = (CurveUnit) graph.getGraph().get(((PointUnit)unit).getIndexOfCurve());
		Point point = curve.getEndPoint(transPoint);
		if(point != null) {
			((PointUnit)unit).setX(point.getX());
			((PointUnit)unit).setY(point.getY());
		}
		
	}

	@Override
	public void translate(Graph graph, float[][] transMatrix) {
		/*平移矩阵
		 * 1, 0, Tx 
		 * 0, 1, Ty
		 * 0, 0, 1
		 * 点坐标为（x,y,1）
		 */
		// TODO Auto-generated method stub
		List<GUnit> gUnit = graph.getGraph();
		for(GUnit unit : gUnit) {
			if(!(unit instanceof PointUnit))
				unit.translate(transMatrix);
		}
	}

	@Override
	public void scale(Graph graph, float[][] scaleMatrix, CurveUnit centerCurve) {
		// TODO Auto-generated method stub
		/*伸缩矩阵
		 * Tx, 0, 0 
		 * 0 ,Ty, 0
		 * 0 , 0, 1
		 * 点坐标为（x,y,1）
		 */

		//求中心坐标
		//如果曲线闭合则取其实点和中间点，如果不闭合则在加上终点
		Point translationCenter;
		if(centerCurve != null) {
			//有放大缩小中心，则要保持约束
			double oldR = centerCurve.getRadius();
			translationCenter = centerCurve.getCenter().getPoint();
			//对于内切，放大小圆半径必须小于大圆，缩小大圆必须大于小圆半径
			//if(oldR * scaleMatrix[0][0] < 内切圆半径)
			//放大中心曲线，平移相关图元
			centerCurve.scale(scaleMatrix, translationCenter);
			//double differentR = centerCurve.getRadius() - oldR;
			double scale = (centerCurve.getRadius() - oldR) / oldR;
			//变化该圆的弦，切线
			List<ConstraintStruct> constraintStructs = centerCurve.getConstraintStruct();
			for(ConstraintStruct conStrut : constraintStructs) {
				//弦
				if(conStrut.getConstraintType() == ConstraintType.HypotenuseOfCircle) {
					graph.getGraph().get(conStrut.getConstraintUnitIndex()).scale(scaleMatrix, translationCenter);
				}
				//切线
				if(conStrut.getConstraintType() == ConstraintType.TangentOfCircle) {
					LineUnit line = (LineUnit) graph.getGraph().get(conStrut.getConstraintUnitIndex());
//					PointUnit pointUnit1 = line.getStartPointUnit();
//					PointUnit pointUnit2 = line.getEndPointUnit();
//					PointUnit center = centerCurve.getCenter();
//					float k = ((float) (pointUnit2.getY() - pointUnit1
//							.getY()))
//							/ (pointUnit2.getX() - pointUnit1.getX());
//					float x = ((-k)
//							* pointUnit1.getY()
//							+ k
//							* k
//							* pointUnit1.getX()
//							+ center.getX() + k
//							* center.getY())
//							/ (1 + k * k);
//					float y = k * x + pointUnit1.getY() - k
//							* pointUnit1.getX();
					
					PointUnit center = centerCurve.getCenter();
					PointUnit tangentPoint = line.getTangentPoint();
					float x = tangentPoint.getX();
					float y = tangentPoint.getY();
					
					float[][] transMatrix = {{1,0,(float) ((x-center.getX())*scale)},
							{0,1,(float) ((y-center.getY())*scale)}, {0,0,1}};
					line.translate(transMatrix);
				}
			}
		} else {
			translationCenter = findTranslationCenter(graph);
			//变换
			for(GUnit unit : graph.getGraph()){
				if(!(unit instanceof PointUnit))
					unit.scale(scaleMatrix, translationCenter);
			}
		}

	}

	@Override
	public void rotate(Graph graph, float[][] rotateMatrix, CurveUnit centerCurve) {
		// TODO Auto-generated method stub
		/*正角表示逆时针旋转
		 *cosQ, -sinQ, 0 
		 * sinQ, cosQ, 0
		 * 0   , 0   , 1
		 * 点坐标为（x,y,1）
		 */
		
		//求中心坐标
		//如果曲线闭合则取其实点和中间点，如果不闭合则在加上终点
		Point translationCenter;
		if(centerCurve != null) {
			translationCenter = centerCurve.getCenter().getPoint();
		} else {
			translationCenter = findTranslationCenter(graph);
		}
		
		//变换
		for(GUnit unit : graph.getGraph()){
			if(!(unit instanceof PointUnit))
				unit.rotate(rotateMatrix, translationCenter);
		}
	}
	
	private Point findTranslationCenter(Graph graph) {
		float x = 0, y = 0;
		int n = 0;  //n记录点元个数
		for(GUnit unit : graph.getGraph()) {
			if(unit instanceof CurveUnit) {
				if(((CurveUnit)unit).getCurveType() == CurveType.Circle || (((CurveUnit)unit).getCurveType() == CurveType.Ellipse && ((CurveUnit)unit).isOverHalf())) {
					PointUnit center = ((CurveUnit)unit).getCenter();
					x += center.getX();
					y += center.getY();
					n++;
				} else {
					List<Point> pList = ((CurveUnit)unit).getPList();
					int size = pList.size();
					n += 2;
					x += pList.get(0).getX();
					x += pList.get(size - 1).getX();
					y += pList.get(0).getY();
					y += pList.get(size - 1).getY();
				}
			}
		}
		x /= n;
		y /= n;
		return new Point(x, y);
	}
	
}
