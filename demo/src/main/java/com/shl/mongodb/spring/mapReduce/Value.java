package com.shl.mongodb.spring.mapReduce;

public class Value {
   private float goodsSum;
   private float priceSum;
public float getGoodsSum() {
	return goodsSum;
}
public void setGoodsSum(float goodsSum) {
	this.goodsSum = goodsSum;
}
public float getPriceSum() {
	return priceSum;
}
public void setPriceSum(float priceSum) {
	this.priceSum = priceSum;
}
   
@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "priceSum= "+priceSum+", goodsSum= "+goodsSum;
	}
}
