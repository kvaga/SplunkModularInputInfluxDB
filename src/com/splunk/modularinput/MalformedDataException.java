package com.splunk.modularinput;

/**
 * Exception thrown when parsing XML that is syntactically valid, but does not
 * match the schema expected by the Splunk SDK for Java.
 */

public class MalformedDataException extends Exception {
    
	
	public MalformedDataException(String s) {
        super(s);
    }
}
