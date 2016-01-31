package org.maxgamer.rs.model.javascript;

import org.mozilla.javascript.ContinuationPending;

public class JavaScriptCall{
	private JavaScriptFiber fiber;
	private ContinuationPending state;
	private Object result;
	
	public JavaScriptCall(JavaScriptFiber fiber){
		this.fiber = fiber;
	}
	
	public JavaScriptFiber getFiber(){
		return fiber;
	}
	
	public void setState(ContinuationPending state){
		this.state = state;
	}
	
	public boolean isFinished(){
		return state == null;
	}
	
	public ContinuationPending getState(){
		return state;
	}
	
	public Object getResult(){
		if(isFinished() == false){
			throw new IllegalStateException("JavaScriptCall not complete!");
		}
		return result;
	}
	
	public void setResult(Object o){
		setState(null);
		this.result = o;
	}
	
	public void terminate(){
		setState(null);
	}
}