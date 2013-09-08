//package com.sg.logic.strategy;
//
//import java.io.Serializable;
//import java.util.List;
//
//import android.util.Log;
//
//import com.sg.control.GraphControl;
//import com.sg.logic.common.CommonFunc;
//import com.sg.object.Point;
//import com.sg.object.graph.CurveGraph;
//import com.sg.object.graph.Graph;
//import com.sg.object.graph.TriangleGraph;
//import com.sg.object.unit.CurveUnit;
//import com.sg.object.unit.GUnit;
//import com.sg.object.unit.LineUnit;
//import com.sg.object.graph.PointGraph;
//import com.sg.object.unit.PointUnit;
//
//public class LineStrategy implements TranslationStratery, Serializable{
//	/*
//	 * 平移操作可以不要用相对中心的坐标
//	 */
//	
////	//平移点元
////	public static void translatePoint(GUnit unit, float[][] transMatrix){
////		((PointUnit)unit).setX(((PointUnit)unit).getX() + transMatrix[0][2]);
////		((PointUnit)unit).setY(((PointUnit)unit).getY() + transMatrix[1][2]);
////	}
//	
//	//点在直线上移动
//	public static void translatePointInLine(GraphControl graphControl, GUnit unit, Point transPoint){
//		Graph graph = graphControl.getGraph(((PointUnit)unit).getKeyOfLineOrCurve());
//		if(graph instanceof TriangleGraph){
//			int[] ver = new int[2];
//			List<GUnit> units = graph.getGraph();
//			GUnit line = null;
//			for(int i = 0;i < units.size();i++){
//				if(units.get(i) == unit){
//					line = units.get(--i);
//					break;
//				}
//			}
//			switch(((PointUnit)unit).getMark()){
//				case 0:{
//					ver[0] = 2;
//					ver[1] = 4;
//					break;
//				}
//				case 2:{
//					ver[0] = 0;
//					ver[1] = 4;
//					break;
//				}
//				case 4:{
//					ver[0] = 0;
//					ver[1] = 2;
//					break;
//				}
//			}
//			//获取移动点坐标
//			double x1 = transPoint.getX();
//			double y1 = transPoint.getY();
//			Log.v("x1,y1",x1+","+y1);
//			//获取三角形边的第一个顶点坐标
//			double x2 = ((PointUnit)units.get(ver[0])).getX();
//			double y2 = ((PointUnit)units.get(ver[0])).getY();
//			Log.v("x2,y2",x2+","+y2);
//			//获取三角形的第二个顶点坐标
//			double x3 = ((PointUnit)units.get(ver[1])).getX();
//			double y3 = ((PointUnit)units.get(ver[1])).getY();
//			Log.v("x3,y3",x3+","+y3);
//			//记录转换后的约束点坐标
//			double xo,yo;
//			
//			//计算三角形边的斜率
//			double k1 = (y2-y3)/(x2-x3);
//			
//			xo = (x1*(x2-x3)+(y2-k1*x2-y1)*(y3-y2))/(k1*(y2-y3)+x2-x3);
//			yo = k1*(xo-x2)+y2;
//			
//			double distance1 = CommonFunc.distance(new Point((int)x2,(int)y2), new Point((int)xo,(int)yo));
//			double distance2 = CommonFunc.distance(new Point((int)x3,(int)y3), new Point((int)xo,(int)yo));
//			double a = distance1/distance2;
//			if(CommonFunc.distance(new Point((int)x2,(int)y2), new Point((int)xo,(int)yo)) > 
//			   CommonFunc.distance(new Point((int)x2,(int)y2), new Point((int)x3,(int)y3)))
//				a = CommonFunc.distance(new Point((int)x2,(int)y2), new Point((int)x3,(int)y3));
//			if(CommonFunc.distance(new Point((int)x3,(int)y3), new Point((int)xo,(int)yo)) > 
//			   CommonFunc.distance(new Point((int)x2,(int)y2), new Point((int)x3,(int)y3)))
//				a = 0;
//			Log.v("xo,yo",xo+","+yo);
//			//设置新的点坐标
//		/*	((PointUnit)unit).setX((int)xo);
//			((PointUnit)unit).setY((int)yo);*/
//			((LineUnit)line).setDrag(true);
//			((LineUnit)line).setProportion(a);
//			((LineUnit)line).getEndPointUnit().setX((float)xo);
//			((LineUnit)line).getEndPointUnit().setY((float)yo);
//		} else {
//			List<GUnit> units = graph.getGraph();
//			LineUnit line = null;
//			for(GUnit u : units) {
//				if(u instanceof LineUnit && u.getID() == ((PointUnit)unit).getIdOfLineOrCurve()) {
//					line = (LineUnit) u;
//					break;
//				}
//			}
//			if(line != null) {
//				float x1 = transPoint.getX();
//				float y1 = transPoint.getY();
//				float x2 = line.getStartPointUnit().getX();
//				float y2 = line.getStartPointUnit().getY();
//				float x3 = line.getEndPointUnit().getX();
//				float y3 = line.getEndPointUnit().getY();
//				float k1 = (y2-y3)/(x2-x3);
//				float x = (x1*(x2-x3)+(y2-k1*x2-y1)*(y3-y2))/(k1*(y2-y3)+x2-x3);
//				float y = k1*(x-x2)+y2;
//				((PointUnit)unit).setX(x);
//				((PointUnit)unit).setY(y);
//			}
//			
//		}
////		
////		//cai
////		if(graph instanceof CurveGraph) {
////			List<GUnit> units = graph.getGraph();
////			LineUnit line = null;
////			for(GUnit u : units) {
////				if(u instanceof LineUnit) {
////					if(((LineUnit)u).getStartPointUnit() == unit || ((LineUnit)u).getEndPointUnit() == unit) {
////						line = (LineUnit) u;
////						break;
////					}
////					
////				}
////			}
////			if(line != null) {
////				float x1 = transPoint.getX();
////				float y1 = transPoint.getY();
////				float x2 = line.getStartPointUnit().getX();
////				float y2 = line.getStartPointUnit().getY();
////				float x3 = line.getEndPointUnit().getX();
////				float y3 = line.getEndPointUnit().getY();
////				float k1 = (y2-y3)/(x2-x3);
////				float x = (x1*(x2-x3)+(y2-k1*x2-y1)*(y3-y2))/(k1*(y2-y3)+x2-x3);
////				float y = k1*(x-x2)+y2;
////				((PointUnit)unit).setX(x);
////				((PointUnit)unit).setY(y);
////			}
////			
////		}
//	}
//	
//	@Override
//	public void translate(Graph graph, float[][] transMatrix) {
//		/*平移矩阵
//		 * 1, 0, Tx 
//		 * 0, 1, Ty
//		 * 0, 0, 1
//		 * 点坐标为（x,y,1）
//		 */
//		List<GUnit> gUnit = graph.getGraph();
//		PointUnit tangPoint = null;
//		for(GUnit unit : gUnit){
//			if(unit instanceof LineUnit){
//				tangPoint = ((LineUnit) unit).getTangentPoint();
//				if(tangPoint != null) {
//					tangPoint.translate(transMatrix);
//				}
//			} else {
//				unit.translate(transMatrix);
//			}
//		}
//	}
//
//	/*因为伸缩，旋转操作是相对于图形中心的--为方便计算，定义图形中心为点的平均坐标（Sx/n,Sy/n）
//	 * 将图形点坐标化为相对于中心的坐标，矩阵变换之后再还原为系统坐标
//	 */
//	@Override
//	public void scale(Graph graph, float[][] scaleMatrix, CurveUnit centerCurve) {
//		/*伸缩矩阵
//		 * Tx, 0, 0 
//		 * 0 ,Ty, 0
//		 * 0 , 0, 1
//		 * 点坐标为（x,y,1）
//		 */
//
//		//如果图形是一个点,没有伸缩变换
//		if(graph instanceof PointGraph){
//			return;
//		}
//		else{
//			Point translationCenter;
//			//求中心坐标
//			if(centerCurve != null) {
//				translationCenter = centerCurve.getCenter().getPoint();
//			} else {
//				translationCenter = findTranslationCenter(graph);
//			}
//			
//			PointUnit tangPoint = null;
//			//变换
//			for(GUnit unit : graph.getGraph()){
//				if(unit instanceof LineUnit){
//					tangPoint = ((LineUnit) unit).getTangentPoint();
//					if(tangPoint != null) {
//						tangPoint.scale(scaleMatrix, translationCenter);
//					}
//				} else {
//					//将图形点坐标化为相对于中心的坐标，矩阵变换之后再还原为系统坐标
//					unit.scale(scaleMatrix, translationCenter);
//				}
//			}
//		}
//	}
//
//	@Override
//	public void rotate(Graph graph, float[][] rotateMatrix, CurveUnit centerCurve) {
//		/*顺时针旋转矩阵
//		 *cosQ, -sinQ, 0 
//		 * sinQ, cosQ, 0
//		 * 0   , 0   , 1
//		 * 点坐标为（x,y,1）
//		 */
//		
//		//如果图形是一个点,可以当做没有旋转变换
//		if(graph instanceof PointGraph){
//			return;
//		}
//		else{
//			Point translationCenter;
//			//求中心坐标
//			if(centerCurve != null) {
//				translationCenter = centerCurve.getCenter().getPoint();
//			} else {
//				translationCenter = findTranslationCenter(graph);
//			}
//			
//			//变换
//			PointUnit tangPoint = null;
//			for(GUnit unit : graph.getGraph()){
//				if(unit instanceof LineUnit){
//					tangPoint = ((LineUnit) unit).getTangentPoint();
//					if(tangPoint != null) {
//						tangPoint.rotate(rotateMatrix, translationCenter);
//					}
//				} else {
//					//将图形点坐标化为相对于中心的坐标，矩阵变换之后再还原为系统坐标
//					unit.rotate(rotateMatrix, translationCenter);
//				}
//			}
//		}
//	}
//	
//	private Point findTranslationCenter(Graph graph) {
//		float x = 0, y = 0;
//		int n = 0;  //n记录点元个数
//		for(GUnit unit : graph.getGraph()){
//			if(unit instanceof PointUnit){
//				if(!((PointUnit) unit).isInLine()) {
//					n ++;
//					x += ((PointUnit)unit).getX();
//					y += ((PointUnit)unit).getY();
//				}
//			}
//		}
//		x /= n;          
//		y /= n;
//		return new Point(x, y);
//	}
//}
