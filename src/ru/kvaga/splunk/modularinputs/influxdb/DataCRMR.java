package ru.kvaga.splunk.modularinputs.influxdb;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Result;
import org.influxdb.dto.QueryResult.Series;

import ru.kvaga.influxdb.DataPointCRMRTxns;
import ru.kvaga.influxdb.InfluxDBClient;

public class DataCRMR {

	private String  influxDBURL, influxDBLogin, influxDBPassword;
	private String influxDBDatabaseName;
	private ArrayList<String> data;
      
	public DataCRMR(String influxDBURL, String influxDBDatabaseName, String influxDBLogin, String influxDBPassword){
		 this.influxDBURL = influxDBURL;
         this.influxDBDatabaseName=influxDBDatabaseName;
         this.influxDBLogin = influxDBLogin;
         this.influxDBPassword = influxDBPassword;
	}

	ArrayList<String> getDataCRMR(){
    	InfluxDBClient influxDBClient = new InfluxDBClient(influxDBURL, influxDBDatabaseName,influxDBLogin, influxDBPassword);
        Date currentTime = new Date(System.currentTimeMillis() - 3600 * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        System.out.println(sdf.format(currentTime));
        
        /*
        org.influxdb.dto.Query query = new Query(
        		"select count(value) from LRTransaction where Module='Integration' "
        		+ "AND time > '2018-06-01 14:40:00' - 3h and time < '2018-06-01 15:40:00' - 3h "
        		+ "group by TransactionName"
        		,"CRMR");
         */
 
        org.influxdb.dto.Query query = new Query(
        		"select count(value) from LRTransaction where Module='Integration' "
        		+ "AND time > '"+sdf.format(currentTime)+"'"
        		+ "group by TransactionName"
        		,"CRMR");
        ArrayList<String> data2InfluxDB = new ArrayList<>();
        QueryResult queryResult = influxDBClient.query(query);
        
        for(Result result : queryResult.getResults()){
        	for(Series series : result.getSeries()){
        		DataPointCRMRTxns dpt = new DataPointCRMRTxns();
        		/*
        		for(String columns: series.getColumns()){
        			System.out.print(" " + columns);
        		}
        		*/
        		        		
        		if(series.getTags()!=null){
	        		for(String tag : series.getTags().keySet()){
	        			dpt.transactionName=series.getTags().get(tag);
//	        			System.out.println("tag ["+tag+"]: " + series.getTags().get(tag));
	        		}
        		}
        		
        		for(Object value : series.getValues()){
        			String time = ((ArrayList<String>)value).get(0);
        			Double count = ((ArrayList<Double>)value).get(1);
        			dpt.time=time;
        			dpt.count=count;
        		}
        		
        		System.out.println("["+dpt.transactionName+"] " + dpt.time + " " + dpt.count);
        		data2InfluxDB.add("TransactionName="+dpt.transactionName + " count=" + dpt.count);
        	}
        }
        return data2InfluxDB;
        }
}
