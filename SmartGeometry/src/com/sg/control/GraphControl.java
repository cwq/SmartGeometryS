/*
 * 图形绘制控制类
 * 控制图像对象的绘制流程等
 * */

package com.sg.control;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import android.graphics.Canvas;
import android.graphics.Color;
import com.sg.object.Point;
import com.sg.object.graph.Graph;
import com.sg.property.common.ThresholdProperty;
import com.sg.property.tools.Painter;
import com.sg.transformation.recognizer.Recognizer;

public class GraphControl {
	
	private ConcurrentHashMap<Long,Graph> graphList;
	private Collection<Graph> graphs;
	
	private Painter painter;
	private Painter checkedPainter;
	
	private Recognizer recognizer;
	
	
	public GraphControl() {
		this.graphList = new ConcurrentHashMap<Long,Graph>();
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
	
	public Graph getCheckedGraph(Point curPoint) {
		graphs = graphList.values();
		for(Graph graph : graphs) {
			if(graph.isInGraph(curPoint))
				return graph;
		}
		return null;
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
	
	public void deleteGraph(Graph graph) {
		graphList.remove(graph.getID());
	}
	
	public void replaceGraph(Graph graph) {
		graphList.replace(graph.getID(), graph);
	}
	
	public void clearGraph() {
		graphList.clear();
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

}
