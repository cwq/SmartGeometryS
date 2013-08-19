package com.sg.object.constraint;

/**
 * 与曲线的约束类型
 * 外切圆：ExternallyTangentCircle；
 * 内切圆：InternallyTangentCircle；
 * 圆的弦，半径：HypotenuseOfCircle
 * 切线：TangentOfCircle
 * @author cai
 *
 */
public enum ConstraintType {

	NONE,
	ExternallyTangentCircle,
	InternallyTangentCircle,
	HypotenuseOfCircle,
	TangentOfCircle;
}
