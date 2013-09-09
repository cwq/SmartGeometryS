/*
 * 图形绘制控制类
 * 控制图像对象的绘制流程等
 * */

package com.sg.control;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import android.graphics.Canvas;
import android.graphics.Color;
import com.sg.object.Point;
import com.sg.object.constraint.ConstraintStruct;
import com.sg.object.graph.CurveGraph;
import com.sg.object.graph.Graph;
import com.sg.object.unit.CurveUnit;
import com.sg.object.unit.GUnit;
import com.sg.object.unit.PointUnit;
import com.sg.property.common.ThresholdProperty;
import com.sg.property.tools.Painter;
import com.sg.transformation.recognizer.Recognizer;

public class GraphControl {
	
	private ConcurrentHashMap<Long,Graph> graphList;
	private Collection<Graph> graphs;
	private List<Graph> constraintGraphs;
	
	private Painter painter;
	private Painter checkedPainter;
	
	private Recognizer recognizer;
	
	
	public GraphControl() {
		graphList = new ConcurrentHashMap<Long,Graph>();
		constraintGraphs = new ArrayList<Graph>();
		recognizer = new Recognizer();
		painter = new Painter(Color.BLACK, ThresholdProperty.DRAW_WIDTH);
		checkedPainter = new Painter(Color.RED, ThresholdProperty.DRAW_WIDTH);
	}
	
	/*
	 * 在画板canvas上绘制对象列表
	 * */
	public void drawGraphList(Canvas canvas) {

		graphs = graphList.values();
		for(Graph graph : graphs) {
			drawGraph(graph, canvas);
		}

	}
	
	/*
	 * 在画板canvas上绘制graph对象
	 * */
	public void drawGraph(Graph graph, Canvas canvas) {
		if(graph != null) {
			if(graph.isChecked()) {
				graph.draw(canvas, checkedPainter);
			} else {
				graph.draw(canvas, painter);
			}
		}
	}
	
	/**
	 * 获取选中图形，并把相关约束图形选中
	 * 无选中 返回null
	 * @param curPoint
	 * @return
	 */
	public Graph getCheckedGraph(Point curPoint) {
		graphs = graphList.values();
		for(Graph graph : graphs) {
			if(graph.isInGraph(curPoint)) {
				Graph tempGraph = graph;
				checkedGraph(graph, 0, true);
				return tempGraph;
			}
		}
		return null;
	}
	
	public void checkedGraph(Graph graph, long lastGraphKey, boolean state) {
		graph.setChecked(state);
		if(graph.isGraphConstrainted()) {
			List<ConstraintStruct> constraintStructs = graph.getConstraintStruct();
			for(ConstraintStruct cons : constraintStructs) {
				if(cons.getConstraintGraphKey() != lastGraphKey) {
					Graph g = graphList.get(cons.getConstraintGraphKey());
					checkedGraph(g, graph.getID(), state);
				}
				
			}
		}
	}
	
	public Graph createGraph(List<Point> pList) {
		return recognizer.recognize(pList);
	}
	
	/*
	 * 添加对象到绘制列表
	 * */
	public void addGraph(Graph graph) {
		if(graph != null && !graphList.containsKey(graph.getID())) {
			graphList.put(graph.getID(), graph);
		}
	}
	
	/**
	 * 删除单个图形
	 * @param graph
	 */
	public void deleteGraph(Graph graph) {
		if(graph != null)
			graphList.remove(graph.getID());
	}
	
	/**
	 * 删除约束图形
	 * @param graph
	 * @param lastGraphKey
	 */
	public void deleteConstraintedGraph(Graph graph, long lastGraphKey) {
		deleteGraph(graph);
		if(graph.isGraphConstrainted()) {
			List<ConstraintStruct> constraintStructs = graph.getConstraintStruct();
			for(ConstraintStruct cons : constraintStructs) {
				if(cons.getConstraintGraphKey() != lastGraphKey) {
					Graph g = graphList.get(cons.getConstraintGraphKey());
					deleteConstraintedGraph(g, graph.getID());
				}
				
			}
		}
	}
	
	public void replaceGraph(Graph graph) {
		if(graph != null)
			graphList.replace(graph.getID(), graph);
	}
	
	public void clearGraph() {
		graphList.clear();
	}
	
	public Graph getGraph(long key) {
		return graphList.get(key);
	}
	
	public Collection<Graph> getGraphList() {
		return graphList.values();
	}
	
	public ConcurrentHashMap<Long,Graph> getConcurrentHashMap() {
		return graphList;
	}
	
	public void setConcurrentHashMap(ConcurrentHashMap<Long,Graph> graphList) {
		this.graphList = graphList;
	}
	
	/**
	 * 获取所有约束图形 使用前需checkedGraphs.clear()
	 * @param graph
	 * @param lastGraphKey
	 */
	private void getConstraintGraphs(Graph graph, long lastGraphKey) {
		constraintGraphs.add(graph);
		if(graph.isGraphConstrainted()) {
			List<ConstraintStruct> constraintStructs = graph.getConstraintStruct();
			for(ConstraintStruct cons : constraintStructs) {
				if(cons.getConstraintGraphKey() != lastGraphKey) {
					Graph g = graphList.get(cons.getConstraintGraphKey());
					getConstraintGraphs(g, graph.getID());
				}
				
			}
		}
	}
	
	public Object[] getCurGraphCurUnit(Graph graph, Point point) {
		constraintGraphs.clear();
		getConstraintGraphs(graph, 0);
		Object[] objects = new Object[2];
		objects[0] = null;
		objects[1] = null;
		for(Graph g : constraintGraphs) {
			if(g.isInGraph(point)) {
				if(objects[0] == null) {
					objects[0] = g;
				}
				if(objects[1] == null) {
					for(GUnit u : g.getGraph()) {
						if(u instanceof PointUnit){
							if(((PointUnit)u).isInLine() && !((PointUnit)u).isCommonConstrainted())
								continue;
							if(u.isInUnit(point)){
								objects[1] = u;
								break;
							}
						}
					}
				}
				
			}
			if(objects[0] != null && objects[1] != null) {
				return objects;
			}
		}
		return objects;
	}
	
	public Graph getCenterGraph(Point point) {
		for(Graph g : constraintGraphs) {
			if(g instanceof CurveGraph) {
				if(((CurveUnit) g.getGraph().get(0)).isInCircle(point)) {
					return g;
				}
			}
		}
		return null;
	}
	
	/**
	 * 平移约束图形
	 * @param graph
	 * @param transMatrix
	 * @param lastGraphKey
	 */
	public void translateGraph(Graph graph, float[][] transMatrix, long lastGraphKey) {
		graph.translate(transMatrix);
		if(graph.isGraphConstrainted()) {
			List<ConstraintStruct> constraintStructs = graph.getConstraintStruct();
			for(ConstraintStruct cons : constraintStructs) {
				if(cons.getConstraintGraphKey() != lastGraphKey) {
					Graph g = graphList.get(cons.getConstraintGraphKey());
					translateGraph(g, transMatrix, graph.getID());
				}
				
			}
		}
	}
	
	/**
	 * 缩放约束图形
	 * @param graph
	 * @param scaleMatrix
	 * @param centerGraph
	 * @param lastGraphKey
	 */
	public void scaleGraph(Graph graph, float[][] scaleMatrix, Point translationCenter, long lastGraphKey) {
		graph.scale(scaleMatrix, translationCenter);
		if(graph.isGraphConstrainted()) {
			List<ConstraintStruct> constraintStructs = graph.getConstraintStruct();
			for(ConstraintStruct cons : constraintStructs) {
				if(cons.getConstraintGraphKey() != lastGraphKey) {
					Graph g = graphList.get(cons.getConstraintGraphKey());
					scaleGraph(g, scaleMatrix ,translationCenter, graph.getID());
				}
				
			}
		}
	}
	
	/**
	 * 旋转约束图形
	 * @param graph
	 * @param rotateMatrix
	 * @param translationCenter
	 * @param lastGraphKey
	 */
	public void rotateGraph(Graph graph, float[][] rotateMatrix, Point translationCenter, long lastGraphKey) {
		graph.rotate(rotateMatrix, translationCenter);
		if(graph.isGraphConstrainted()) {
			List<ConstraintStruct> constraintStructs = graph.getConstraintStruct();
			for(ConstraintStruct cons : constraintStructs) {
				if(cons.getConstraintGraphKey() != lastGraphKey) {
					Graph g = graphList.get(cons.getConstraintGraphKey());
					rotateGraph(g, rotateMatrix ,translationCenter, graph.getID());
				}
				
			}
		}
	}

}
