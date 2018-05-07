/**
 * 
 */
package com.ailk.common.data;

/**
 * @author yifur
 *
 */
public final class GlobalContext {
	
	private static ThreadLocal<IVisit> context = new ThreadLocal<IVisit>() {
		protected IVisit initialValue() {
			return null;
		};
	};
	
	private GlobalContext() {
		
	}
	
	/**
	 * get
	 * @return
	 */
	public static IVisit get() {
		return context.get();
	}
	
	/**
	 * set
	 * @param visit
	 */
	public static void set(IVisit visit) {
		context.set(visit);
	}
	
	
	/**
	 * remove
	 */
	public static void remove() {
		context.remove();
	}

}
