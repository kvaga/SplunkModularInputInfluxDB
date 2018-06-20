package ru.kvaga.influxdb;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Result;
import org.influxdb.dto.QueryResult.Series;

public class InfluxDBClient {

	private String influxDBURL;
	private String influxDBLogin=null;
	private String influxDBPassword=null;
	private String influxDBDatabaseName;
	
	public InfluxDBClient(String influxDBURL, String influxDBDatabaseName, String influxDBLogin, String influxDBPassword){
		this.influxDBURL=influxDBURL;
		this.influxDBDatabaseName=influxDBDatabaseName;
		this.influxDBLogin=influxDBLogin;
		this.influxDBPassword=influxDBPassword;
	}
	public InfluxDBClient(String influxDBURL, String influxDBDatabaseName){
		this.influxDBURL=influxDBURL;
		this.influxDBDatabaseName=influxDBDatabaseName;
	}
	
	public QueryResult query(Query query){
		InfluxDB influxdb = InfluxDBFactory.connect(influxDBURL, influxDBLogin, influxDBPassword);
		influxdb.setDatabase(influxDBDatabaseName);
		QueryResult queryResult = influxdb.query(query);
		return queryResult;
	}
	public static void main(String[] args) {
		InfluxDBClient influxDBClient = new InfluxDBClient("http://sbt-oabs-406:8086", "CRMR", "test", "test");
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
        ArrayList<DataPointCRMRTxns> data2InfluxDB = new ArrayList<>();
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
        		data2InfluxDB.add(dpt);
        	}
        }
	}
}
