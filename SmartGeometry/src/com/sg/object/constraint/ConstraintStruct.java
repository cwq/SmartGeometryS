package com.sg.object.constraint;

import java.io.Serializable;

import com.sg.object.unit.GUnit;

public class ConstraintStruct implements Cloneable, Serializable {

	private ConstraintType constraintType;
	//private GUnit constraintUnit;
	private int constraintUnitIndex; //约束图元的下标
	
//	public ConstraintStruct() {
//		constraintType = ConstraintType.NONE;
//		constraintUnit = null;
//	}
	
	public ConstraintStruct(ConstraintType constraintType, int constraintUnitIndex) {
		this.constraintType = constraintType;
		//this.constraintUnit = constraintUnit;
		this.constraintUnitIndex = constraintUnitIndex;
	}
	
	public ConstraintType getConstraintType() {
		return constraintType;
	}
	
	public void setConstraintType(ConstraintType constraintType) {
		this.constraintType = constraintType;
	}
	
//	public GUnit getConstraintUnit() {
//		return constraintUnit;
//	}
//	
//	public void setConstraintUnit(GUnit constraintUnit) {
//		this.constraintUnit = constraintUnit;
//	}

	public int getConstraintUnitIndex() {
		return constraintUnitIndex;
	}

	public void setConstraintUnitIndex(int constraintUnitIndex) {
		this.constraintUnitIndex = constraintUnitIndex;
	}
	
	public ConstraintStruct clone() {
		ConstraintStruct constraintStruct = null;
		try {
			constraintStruct = (ConstraintStruct) super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        return constraintStruct; 
	}
	
}
