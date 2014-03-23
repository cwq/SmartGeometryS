/*
 * 笔画类
 * 消除噪声后的笔信息
 * 根据速率，方向，曲率寻找特征点
 * */
package com.sg.transformation.collection;

import java.util.ArrayList;
import java.util.List;

import com.sg.logic.common.CommonFunc;
import com.sg.object.Point;
import com.sg.property.common.ThresholdProperty;

public class Stroke {
	
	public Stroke() {
		
	}
	
	/**
	 * 速度过滤方法：低于平均值的一定百分比算是特征点
	 */
	public void speed(List<Point> pList) {
		int n = pList.size();
		pList.get(0).setSpeed(0.0); // 第一点和最后一点速度置为0.0
		pList.get(n - 1).setSpeed(0.0);

		double average = 0.0;
		for (int i = 1; i < n - 1; i++) {
			double tempSpeed = CommonFunc.distance(pList.get(i), pList.get(i - 1))
					+ CommonFunc.distance(pList.get(i + 1), pList.get(i));
			pList.get(i).setSpeed(tempSpeed);
			average += tempSpeed;
		}
		average /= n;
		for (int i = 0; i < n; i++) {
			if (pList.get(i).getSpeed() < average * 0.42) {
				pList.get(i).increaseTotal();
			}
		}
	}

	/**
	 * 方向过滤方法 对于点i；边<i-1,i>和边<i,i+1>之间的夹角来判断是否平滑过渡 夹角大于170度时，平滑过渡，否则，点i为一个转折点
	 * 运用余弦定理求解角度
	 */
	public void direction(List<Point> pList) {
		int n = pList.size();
		for (int i = 1; i < n - 1; i++) { // 余弦定理
			double A = CommonFunc.distance(pList.get(i - 1), pList.get(i + 1));
			double B = CommonFunc.distance(pList.get(i), pList.get(i + 1));
			double C = CommonFunc.distance(pList.get(i - 1), pList.get(i));

			double tmp = B * B + C * C - A * A;
			tmp = tmp / (2 * B * C + 0.00001);
			double ridian = Math.acos(tmp);
			pList.get(i).setDirection(ridian * 180.0 / Math.PI);
		}


		for (int i = 1; i < n - 1; i++) {
			if (pList.get(i).getDirection() <= 170.0) {
                pList.get(i).increaseTotal();
			}
		}
		pList.get(0).increaseTotal();
		pList.get(n-1).increaseTotal();
		
	}

	/**
	 * 一：将五个点平移，使得i点在原点
     * 二：将五个点绕原点旋转[0,180)，记录五个点|y|的绝对值的和
     * 三：使得绝对值和最小的角度为i点切线与x轴的夹角
     * 旋转矩阵：cosA   -sinA
     *                              sinA    cosA
     * 原来坐标(x,y),旋转后(xcosA+ysinA,-xsinA+ycosA);
     * 所以只需枚举[0,180)，计算所有-xsinA+ycosA的和，就是切线与x轴夹角
     * 求出夹角后，求曲率：
     * double rate = System.Math.Sin(30*pi/180.0);
	 * */
	public void curvity(List<Point> pList) {
		int n = pList.size();
		double[] sita = new double[n];

		for (int i = 2; i < n - 2; i++) {
			double mint = 0.0;

			for (int k = 0; k < 4; k++) {     //有改动 lmc,for (int k = 0; k <= 4; k++)
				mint += Math.abs(pList.get(i - 2 + k).getY()
						- pList.get(i).getY());
			}
			sita[i] = 0.0;

			for (int j = 1; j < 180; j++) {
				double tmp = 0.0;

				for (int k = 0; k < 4; k++) {   //有改动 lmc,for (int k = 0; k <= 4; k++)
					tmp += Math.abs((pList.get(i - 2 + k).getY() - pList.get(i).getY()) * Math.cos(j * Math.PI / 180.0)
							- (pList.get(i - 2 + k).getX() - pList.get(i).getX()) * Math.sin(j * Math.PI / 180.0));
				}
				
				if(tmp < mint) {
					mint = tmp;
					sita[i] = j;
				}
			}
		}
		
		for(int i = 2; i < n-2; i++) {
			double tmp = 0.0;
			for(int k = 1; k < 4; k++) {
				tmp += Math.abs(sita[i - 2 + k] - sita[i - 3 + k]);
			}
			
			pList.get(i).setCurvity(tmp / CommonFunc.distance(pList.get(i-2), pList.get(i+2)));
			
			if(Math.abs(pList.get(i).getCurvity()) > 100) {    //处理可能为异常抖动的点
				pList.get(i).setCurvity(0.0);
				pList.get(i).increaseTotal(-1);
				if(pList.get(i).getTotal() >= 2) {
					pList.get(i).increaseTotal();
				}
			}
		}
		
		double average = 0.0;
		for(int i = 0; i < n; i++) {
			average += pList.get(i).getCurvity();
		}
		average /= n;
		
		for(int i = 1; i < n-1; i++) {
			if(pList.get(i).getCurvity() > average * 1.0) {
				pList.get(i).increaseTotal();
			} else if(pList.get(i).getTotal() >= 2) {
				pList.get(i).increaseTotal();
			}
		}
		
//		averageCurvity = average;
		pList.get(0).increaseTotal();
		pList.get(n-1).increaseTotal();
	}
	
	/**
	 * 对系列电进一步处理
	 * */
	public void space(List<Point> pList) {
		int n = pList.size();
		
			for(int i = 1; i < n-1; i++) {
				if( pList.get(i).getTotal() >= 2 ) {
					//去除起始点附近的噪点
					for(int j = i-1; j >= 0; j--) {
						if(pList.get(j).getTotal() >= 2 && 
								CommonFunc.distance(pList.get(i),pList.get(j)) <= 
								ThresholdProperty.TWO_POINT_IS_CLOSED) {
							pList.get(i).increaseTotal(-1);
						}
					}
				}
				if((pList.get(i).getTotal() >= 2) && 
						(CommonFunc.distance(pList.get(i),pList.get(n -1)) <=
						ThresholdProperty.TWO_POINT_IS_CLOSED)){
					//处理末尾附近点的冗余
					pList.get(i).increaseTotal(-1);
				}
			}	
		}

	public List<Integer> getSpecialPointIndex(List<Point> pList) {
		speed(pList);
		direction(pList);
//		curvity(pList);
		space(pList);
		
		List<Integer> specialPointIndexs = new ArrayList<Integer>();
		int n = pList.size();
		for(int i=0; i < n; i++) {
			if(pList.get(i).getTotal() >= 2) {    //特征值大于等于4的点为特征点
				specialPointIndexs.add(i);
			}
		}
		
		return specialPointIndexs;
	}
	
	public List<Integer> getSpecialPointIndexForDelete(List<Point> pList) {
//		speed(pList);
//		direction(pList);
//		curvity(pList);
//		space(pList);
		
		List<Integer> specialPointIndexsForDelete = new ArrayList<Integer>();
		int n = pList.size();
		for(int i=0; i < n; i++) {
			if(pList.get(i).getTotal() >= 2) {    
				specialPointIndexsForDelete.add(i);
			}
		}
		return specialPointIndexsForDelete;
	}
	
}
