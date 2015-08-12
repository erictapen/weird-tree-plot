package math;

import java.util.HashMap;
import java.util.Vector;


/** Faster acces to Math functions through Hashmaps
 * @author justin
 *
 */
public class FastMath {
	private HashMap<Integer, Integer> sqrtInteger;
	private HashMap<Vector<Integer>, Integer> distInteger;
	private HashMap<Double, Double> sqrtDouble;
	private HashMap<Vector<Double>, Double> distDouble;
	
	public FastMath() {
		this.sqrtInteger = new HashMap<Integer, Integer>(); //TODO find good initialcapacities
		this.distInteger = new HashMap<Vector<Integer>, Integer>();
		this.sqrtDouble = new HashMap<Double, Double>();
		this.distDouble = new HashMap<Vector<Double>, Double>();
		
	}
	
	public Integer sqrtInt(Integer x) {
		Integer res = this.sqrtInteger.get(x);
		if(res!=null) return res;
		this.sqrtInteger.put(x, new Integer((int) Math.sqrt(x)));
		return this.sqrtInt(x);
	}
	
	public Integer distInt(Vector<Integer> v) {
		Integer res = this.distInteger.get(v);
		if(res!=null) return res;
		this.distInteger.put(v, new Integer((int) Math.sqrt(v.firstElement()*v.firstElement() 
				+ v.lastElement()*v.lastElement())));
		return this.distInt(v);
	}
	
	public Double sqrtD(Double x) {
		Double res = this.sqrtDouble.get(x);
		if(res!=null) return res;
		this.sqrtDouble.put(x, new Double(Math.sqrt(x)));
		return this.sqrtD(x);
	}
	
	public Double distD(Vector<Double> v) {
		Double res = this.distDouble.get(v);
		if(res!=null) return res;
		this.distDouble.put(v, new Double(Math.sqrt(v.firstElement()*v.firstElement() 
				+ v.lastElement()*v.lastElement())));
		return this.distD(v);
	}
	
	
	
}
