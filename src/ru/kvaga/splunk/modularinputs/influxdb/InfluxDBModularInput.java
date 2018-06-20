package ru.kvaga.splunk.modularinputs.influxdb;
import javax.xml.stream.XMLStreamException;

import com.splunk.modularinput.Argument;
import com.splunk.modularinput.Argument.DataType;

import ru.kvaga.influxdb.InfluxDBClient;
import ru.kvaga.splunk.modularinputs.utils.Utils;

import com.splunk.modularinput.EventWriter;
import com.splunk.modularinput.InputDefinition;
import com.splunk.modularinput.MalformedDataException;
import com.splunk.modularinput.Scheme;
import com.splunk.modularinput.Script;
import com.splunk.modularinput.SingleValueParameter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InfluxDBModularInput extends Script {
	
    public static void main(String[] args) {
    	
    	
    	System.out.println("[" + InfluxDBModularInput.class + "]");
        new InfluxDBModularInput().run(args);
    	
    }
    
    private final String PARAM_NAME_INFLUXDB_URL="influxdb_url";
    private final String PARAM_NAME_INFLUXDB_DATABASE_NAME="influxdb_databasename";
    private final String PARAM_NAME_INFLUXDB_LOGIN="influxdb_login";
    private final String PARAM_NAME_INFLUXDB_PASSWORD="influxdb_password";
    
    
    
    public Scheme getScheme() {
    	 Scheme scheme = new Scheme("InfluxDBDataSourceInput");
    	 scheme.setDescription("Description");
    	 scheme.setTitle("InfluxDB Modular Input");
    	 
    	 scheme.setUseExternalValidation(false);
    	 scheme.setDescription("Uploads data from InfluxDB instance");
    	 
    	 Argument influxdbURL = new Argument(PARAM_NAME_INFLUXDB_URL);
    	 influxdbURL.setDataType(DataType.STRING);
    	 influxdbURL.setDescription("URL of the InfluxDB database");
    	 influxdbURL.setRequiredOnCreate(true);
    	 influxdbURL.setRequiredOnEdit(true);

    	 Argument influxdbLogin = new Argument(PARAM_NAME_INFLUXDB_LOGIN);
    	 influxdbLogin.setDataType(DataType.STRING);
    	 influxdbLogin.setDescription("Login of the InfluxDB database (if required)");
    	 influxdbLogin.setRequiredOnCreate(false);
    	 influxdbLogin.setRequiredOnEdit(false);
    	 
    	 Argument influxdbPassword = new Argument(PARAM_NAME_INFLUXDB_PASSWORD);
    	 influxdbPassword.setDataType(DataType.STRING);
    	 influxdbPassword.setDescription("Password of the InfluxDB database (if required)");
    	 influxdbPassword.setRequiredOnCreate(false);
    	 influxdbPassword.setRequiredOnEdit(false);

    	 Argument influxdbDatabaseName = new Argument(PARAM_NAME_INFLUXDB_DATABASE_NAME);
    	 influxdbDatabaseName.setDataType(DataType.STRING);
    	 influxdbDatabaseName.setDescription("The name of local database");
    	 influxdbDatabaseName.setRequiredOnCreate(true);
    	 influxdbDatabaseName.setRequiredOnEdit(true);
    	 
    	 ArrayList<Argument> args = new ArrayList<Argument>();
    	 args.add(influxdbURL);
    	 args.add(influxdbLogin);
    	 args.add(influxdbPassword);
    	 args.add(influxdbDatabaseName);
    	 scheme.setArguments(args);
			return scheme;
    }

//    @Override
//    public void validateInput(ValidationDefinition definition) throws Exception {
//        // Validates input.
//    }

    @Override
    public void streamEvents(InputDefinition inputs, EventWriter ew) throws
            MalformedDataException, XMLStreamException, IOException {
    	try {
    	 for (String inputName : inputs.getInputs().keySet()) {
             // We get the parameters for each input and start a new thread for each one. 
             // All the real work happens in the class for event generation.
    		 
             String influxDBURL = ((SingleValueParameter)inputs.getInputs().get(inputName).get(PARAM_NAME_INFLUXDB_URL)).getValue();
             String influxDBLogin = ((SingleValueParameter)inputs.getInputs().get(inputName).get(PARAM_NAME_INFLUXDB_LOGIN)).getValue();
             String influxDBPassword = ((SingleValueParameter)inputs.getInputs().get(inputName).get(PARAM_NAME_INFLUXDB_PASSWORD)).getValue();
             String influxDBDatabaseName = ((SingleValueParameter)inputs.getInputs().get(inputName).get(PARAM_NAME_INFLUXDB_DATABASE_NAME)).getValue();
             
             //String index="index_1";
             //String sourcetype="sourcetype1";
             
             DataCRMR influxDBClient = new DataCRMR(influxDBURL, influxDBDatabaseName, influxDBLogin, influxDBPassword);
             ArrayList<String> data = influxDBClient.getDataCRMR();
             Thread t = new Thread(new InfluxDBEventsUpload(ew, inputName, data));
             t.run();
         }
    	 
    	}catch(Exception ex) {
//    		ew.synchronizedWriteEvent(Utils.convertException2LogString(ex));
    		ew.synchronizedLog(EventWriter.ERROR, Utils.convertException2LogString(ex));
//    		System.err.println(Utils.convertException2LogString(ex));
    	}
    }
//    private static String getErrorMessage(Exception ex) {
//    	StringBuilder sb = new StringBuilder();
//    	for(StackTraceElement ste :ex.getStackTrace()) {
//    		ste.getClassName() (ste.getFileName():ste.getLineNumber()) ste.get
//    	}
//    	return sb.toString();
//    }
}