/**
 * map累加器
 */
package org.jview.jtool.tools;

import java.util.*;

import org.jview.jtool.util.ErrorCode;




/**
 * 动态累加器
 * @author chenjh
 *
 */

public class AutoTool {

	private int rowCount=1;
	private int sunInt=0;
	private float sunFloat=0;





	public int getRowCount() {
		return rowCount++;
	}

	
	public void setRowCount(int rowCount) {	
		this.rowCount=rowCount;
	}
	
	


	public float getSunFloat() {
		return sunFloat;
	}

	
	public void setSunFloat(float sunFloat) {
		this.sunFloat = sunFloat;
	}

	
	public int getSunInt() {
		return sunInt;
	}

	
	public void setSunInt(int sunInt) {
		this.sunInt = sunInt;
	}
	
	
	/**
	 * 动态累加器
	 */
	private Map map = new HashMap();
	private int mapCount;
	
	private double sumValue;
	public double getSumValue(String keys){
		if(this.map.get(keys)!=null && !ErrorCode.isEmpty(""+this.map.get(keys))){			
			this.sumValue = Double.parseDouble(""+this.map.get(keys));
		}	
		else{
			this.sumValue = 0;
		}
		return sumValue;
	}
	
	
	/**
	 * 动态加法器
	 * @param keys
	 * @param value
	 */
	public void addSumValue(String keys, double value){
		try{
		if(this.map.get(keys)==null){
			this.map.put(keys, ""+value);			
		}	
		else{
			this.map.put(keys, (((double)this.getSumValue(keys))+value));
		}	
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private Object mapObj = null;
	public int getMapCount(String value) {
//		System.out.println("================getMapCount="+this.map.get(value));
		mapObj = this.map.get(value);
		if(mapObj!=null){					
			this.mapCount = (Integer)mapObj;
		}	
		else{
			this.mapCount = 0;
		}
		return mapCount;
	}
	
	
	/**
	 * 动态计数器
	 * @param value
	 */
	public void addMapCount(String value) {
//		System.out.println("===========addMapCount="+this.map.get(value)+(this.map.get(value)==null));
		if(this.map.get(value)==null){
			this.map.put(value, 1);
//			System.out.println("map put "+value+"1");
		}	
		else{
			this.map.put(value, (this.getMapCount(value)+1));
		}		
	}


	
	
	public void clear(){
		this.map.clear();
	}
	
	
}
