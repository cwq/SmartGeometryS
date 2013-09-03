/*
 * 图形绘制控制类
 * 控制图像对象的绘制流程等
 * */

package com.sg.control;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import android.R.integer;
import android.graphics.Canvas;
import android.graphics.Color;
import com.sg.object.Point;
import com.sg.object.constraint.ConstraintStruct;
import com.sg.object.graph.Graph;
import com.sg.object.graph.LineGraph;
import com.sg.property.common.ThresholdProperty;
import com.sg.property.tools.Painter;
import com.sg.transformation.recognizer.Recognizer;

public class GraphControl {
	
	private ConcurrentHashMap<Long,Graph> graphList;
	private Collection<Graph> graphs;
//	private List<LineGraph> lines;
	
	private Painter painter;
	private Painter checkedPainter;
	
	private Recognizer recognizer;
	
	
	public GraphControl() {
		graphList = new ConcurrentHashMap<Long,Graph>();
//		lines = new ArrayList<LineGraph>();
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
				checkedGraph(graph, 0, true);
				return graph;
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
//			if(graph instanceof LineGraph) {
//				lines.add((LineGraph) graph);
//			}
		}
	}
	
	public void deleteGraph(Graph graph) {
		if(graph != null)
			graphList.remove(graph.getID());
//		if(graph instanceof LineGraph) {
//			lines.remove((LineGraph) graph);
//		}
	}
	
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
//		if(graph instanceof LineGraph) {
//			for(int index = 0; index < lines.size(); index++) {
//				if(lines.get(index).getID() == graph.getID()) {
//					lines.set(index, (LineGraph) graph);
//					break;
//				}
//			}
//		}
	}
	
	public void clearGraph() {
		graphList.clear();
//		lines.clear();
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
	
//	public List<LineGraph> getLines() {
//		return lines;
//	}
	
	public void setConcurrentHashMap(ConcurrentHashMap<Long,Graph> graphList) {
		this.graphList = graphList;
//		lines.clear();
//		graphs = graphList.values();
//		for(Graph graph : graphs) {
//			if(graph instanceof LineGraph) {
//				lines.add((LineGraph) graph);
//			}
//		}
	}

}
