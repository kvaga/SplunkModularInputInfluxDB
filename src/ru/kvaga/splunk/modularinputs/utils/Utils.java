package ru.kvaga.splunk.modularinputs.utils;

import com.splunk.modularinput.Event;

public class Utils {
	public static String convertException2LogString(Exception ex) {
		StringBuilder sb = new StringBuilder();
		sb.append("ERROR ExecProcessor - Exception: " + ex.getMessage());
		
		sb.append('\n');
		for(StackTraceElement ste : ex.getStackTrace()) {
			sb.append(" at " +ste.getClassName() + "." + ste.getMethodName() + "(" + ste.getFileName() + ":" + ste.getLineNumber()+")");
			sb.append('\n');
			
		}
		Exception cause =  (Exception) ex.getCause();
		if(cause!=null) {
			sb.append("Cause: " + cause.getMessage());
			sb.append('\n');
		for(StackTraceElement ste : cause.getStackTrace()) {
			sb.append(" at " + ste.getClassName() + "." + ste.getMethodName() + "(" + ste.getFileName() + ":" + ste.getLineNumber()+")");
			sb.append('\n');
		}
		}
		Event event = new Event();
		event.setData(sb.toString());
		event.setIndex("_internal");
		event.setHost("localhost");
		event.setSourceType("splunkd");
		
		return sb.toString();
	}
}
