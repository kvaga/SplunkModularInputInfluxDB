package ru.kvaga.splunk.modularinputs.influxdb;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Result;
import org.influxdb.dto.QueryResult.Series;

import com.splunk.modularinput.Event;
import com.splunk.modularinput.EventWriter;
import com.splunk.modularinput.MalformedDataException;

import ru.kvaga.influxdb.DataPointCRMRTxns;
import ru.kvaga.influxdb.InfluxDBClient;
import ru.kvaga.splunk.modularinputs.utils.Utils;

class InfluxDBEventsUpload implements Runnable {
        private String  influxDBURL, influxDBLogin, influxDBPassword;
        private String influxDBDatabaseName;
        private ArrayList<String> data;
        int influxDBDelayInSeconds;
        private EventWriter ew;
        private String inputName;
        

        public InfluxDBEventsUpload(EventWriter ew, String inputName, ArrayList<String> data) {
            super();
            this.ew = ew;
            this.inputName = inputName;
            this.data=data;
        }

        public void run() {
        	try{
	            ew.synchronizedLog(EventWriter.INFO, "Uploading InfluxDB data " + inputName +
	                    " started for INFLUXDB_URL= " + influxDBURL + " influxDBLogin=" + influxDBLogin + " influxDBPassword=[********] + delayInSeconds="+influxDBDelayInSeconds );
	             for(String str : data) {
	                Event event = new Event();
	                event.setStanza(inputName);
	                event.setData(str);
	                ew.writeEvent(event);
	            }             
            }catch(Exception ex){
            	ew.synchronizedLog(EventWriter.ERROR, Utils.convertException2LogString(ex));
//            	try {
//					ew.synchronizedWriteEvent(Utils.convertException2LogString(ex));
//				} catch (MalformedDataException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
            	
//            	System.err.println(Utils.convertException2LogString(ex));
            }
        }
        
        /*
        private void test(){
        	final Random randomGenerator = new Random();
            for (String str : data) {
                Event event = new Event();
                event.setStanza(inputName);
                event.setData(str);
                try {
                    ew.writeEvent(event);
                } catch (MalformedDataException e) {
                    ew.synchronizedLog(EventWriter.ERROR, "MalformedDataException in writing event to input" +
                            inputName + ": " + e.toString());
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return;
                }
            }

        }
        */
        
        
    }