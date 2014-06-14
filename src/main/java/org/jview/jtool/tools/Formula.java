/**
 * TODO
 */
package org.jview.jtool.tools;

import org.nfunk.jep.JEP;



/**
 * 数据库公式处理
 * @author jview
 * @created  2007-3-22
 *
 */
public class Formula {
	/**
	 * 初始化解析器
	 * @return
	 */
	private static JEP getJep(){
		JEP myParser = new JEP();
		
		// Allow implicit multiplication
		myParser.setImplicitMul(true);

		// Load the standard functions
		myParser.addStandardFunctions();

		// Load the standard constants, and complex variables/functions
		myParser.addStandardConstants();
		myParser.addComplex();
		
		// Add and initialize x to 0
		myParser.addVariable("x",0);
		return myParser;
	}
	/**
	 * 计算数学表达式
	 * @param value
	 * @return
	 */
	public static double formulaParser (String value){
		double sum = 0;
		JEP myParser = null;
		if(myParser==null){
			myParser = getJep();
		}
		try{
			myParser.parseExpression(value);
			boolean hasError = myParser.hasError();
			sum = myParser.getValue();
			
		}catch(Exception e){
			e.printStackTrace();
			
		}
		return sum;
	}
	
	/**
	 * 递归法得到Fibonacci
	 * @param n
	 * @return
	 */
	public static int getFibonacci(int n){
		int result[] = {0,1};
		if(n<2){
			return result[n];
		}
		else{
			return getFibonacci(n-1)+getFibonacci(n-2);
		}
			
	}
	
	/**
	 * 连加优化法Fibonacci2
	 * @param n
	 * @return
	 */
	public static int getFibonacci2(int n){
		int result[] = {0, 1};   
	      if(n < 2)   
	            return result[n];   
	      int   fibNMinusOne = 1;   
	      int   fibNMinusTwo = 0;   
	      int   fibN = 0;   
	      for(int i = 2; i <= n; ++ i)   
	       {   
	             fibN = fibNMinusOne + fibNMinusTwo;   
	             fibNMinusTwo = fibNMinusOne;   
	             fibNMinusOne = fibN;   
	       }   
	      return fibN;

	}
	
	public static void main(String[] args) {
		double d = Formula.formulaParser("2+(3+10)+4/3");
		System.out.println("value="+d);
		System.out.println(Formula.getFibonacci2(49));
	}
	
	
}
