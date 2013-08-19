/*
 * 读取properties文件初始化常用阈值
 * 如果有常变量要添加：1，在threshold.properties文件中添加相应格式的信息，2，在该类中定义变量然后读取文件初始化
 * */

package com.sg.property.common;

public class ThresholdProperty {
	
	//点阈值
	public static int POINT_COUNT = 20;
	public static int POINT_DISTANCE = 8;
	public static double POINT_SELECTED_DISTANCE = 20.0;
	
	//线阈值
	public static double JUDGE_LINE_VALUE = 0.9;
	
	//时间
	public static int PRESS_TIME_SHORT = 300;
	public static int PRESS_TIME_LONG = 1000;
	
	//图形选中
	public static double GRAPH_CHECKED_DISTANCE = 40;
	
	//三角形四边形
	public static double TWO_POINT_IS_CLOSED = 50.0; //用于判断是否能成为三角形或四边形的阀值
	public static double TWO_POINT_IS_CONSTRAINTED = 30.0;
	
	//画比大小
	public static int DRAW_WITH = 3;
	
	public static void set(float density) {
		//POINT_COUNT *= density;
		POINT_DISTANCE *= density;
		POINT_SELECTED_DISTANCE *= density;
		//JUDGE_LINE_VALUE *= density;
		//PRESS_TIME_SHORT *= density;
		//PRESS_TIME_LONG *= density;
		GRAPH_CHECKED_DISTANCE *= density;
		TWO_POINT_IS_CLOSED *= density;
		TWO_POINT_IS_CONSTRAINTED *= density;
		DRAW_WITH *= density;
	}
}