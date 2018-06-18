package ru.kvaga.splunk.modularinputs.influxdb;
import javax.xml.stream.XMLStreamException;

import com.splunk.modularinput.Argument;
import com.splunk.modularinput.Argument.DataType;
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
    private final String PARAM_NAME_INFLUXDB_LOGIN="influxdb_login";
    private final String PARAM_NAME_INFLUXDB_PASSWORD="influxdb_password";
    private final String PARAM_NAME_INFLUXDB_DELAY_IN_SECONDS="delay_in_seconds";
    
    
    public Scheme getScheme() {
    	 Scheme scheme = new Scheme("InfluxDBDataSourceInput");
    	 scheme.setUseSingleInstance(true);
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
    	 influxdbLogin.setRequiredOnCreate(true);
    	 influxdbLogin.setRequiredOnEdit(true);
    	 
    	 Argument influxdbPassword = new Argument(PARAM_NAME_INFLUXDB_PASSWORD);
    	 influxdbPassword.setDataType(DataType.STRING);
    	 influxdbPassword.setDescription("Password of the InfluxDB database (if required)");
    	 influxdbPassword.setRequiredOnCreate(true);
    	 influxdbPassword.setRequiredOnEdit(true);

    	 Argument delayInSeconds = new Argument(PARAM_NAME_INFLUXDB_DELAY_IN_SECONDS);
    	 delayInSeconds.setDataType(DataType.NUMBER);
    	 delayInSeconds.setDescription("How long (in seconds) a job has to wait before the next invocation");
    	 delayInSeconds.setRequiredOnCreate(true);
    	 delayInSeconds.setRequiredOnEdit(true);
    	 
    	 ArrayList<Argument> args = new ArrayList<Argument>();
    	 args.add(influxdbURL);
    	 args.add(influxdbLogin);
    	 args.add(influxdbPassword);
    	 args.add(delayInSeconds);
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
             int influxDBDelayInSeconds = ((SingleValueParameter)inputs.getInputs().get(inputName).get(PARAM_NAME_INFLUXDB_DELAY_IN_SECONDS)).getInt();
             
//String index="index_1";
//String sourcetype="sourcetype1";
             Thread t = new Thread(new InfluxDBEventsUpload(ew, inputName, influxDBURL, influxDBLogin, influxDBPassword, influxDBDelayInSeconds));
             t.run();
         }
    	 
    	}catch(Exception ex) {
    		ew.synchronizedLog(EventWriter.ERROR, ex.getMessage());
    		ex.printStackTrace();
    	}
    }
}