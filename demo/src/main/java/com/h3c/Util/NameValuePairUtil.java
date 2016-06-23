package com.h3c.Util;

import java.util.ArrayList;

import org.csource.common.NameValuePair;

public class NameValuePairUtil {

	private ArrayList<NameValuePair> pairs;
	
	public NameValuePairUtil(){
		pairs=new ArrayList<>();
	}
	
	public void add(String name,String value){
		NameValuePair pair=new NameValuePair(name, value);
		pairs.add(pair);
	}
	
	public NameValuePair[] getNameValuePairArray(){
		NameValuePair[] valuePairs=new NameValuePair[pairs.size()];
		return pairs.toArray(valuePairs);
	}
}
