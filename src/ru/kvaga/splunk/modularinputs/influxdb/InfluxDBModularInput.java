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

    
    public Scheme getScheme() {
    	 Scheme scheme = new Scheme("InfluxDBDataSourceInput");
    	 scheme.setUseSingleInstance(true);
    	 scheme.setUseExternalValidation(false);
    	 scheme.setDescription("Uploads data from InfluxDB instance");
    	 Argument param1 = new Argument("param1");
    	 param1.setDataType(DataType.STRING);
    	 param1.setDescription("Description of param1");
//    	 param1.setName("param1_1");
    	 param1.setRequiredOnCreate(true);
    	 param1.setRequiredOnEdit(true);
    	 
    	 
    	 Argument param2 = new Argument("param2");
    	 param2.setDataType(DataType.STRING);
    	 param2.setDescription("Description of param2");
//    	 param2.setName("param1_1");
    	 param2.setRequiredOnCreate(true);
    	 param2.setRequiredOnEdit(true);
    	 
    	 Argument param3 = new Argument("param3");
    	 param3.setDataType(DataType.STRING);
    	 param3.setDescription("Description of param3");
//    	 param3.setName("param1_1");
    	 param3.setRequiredOnCreate(true);
    	 param3.setRequiredOnEdit(true);
    	 ArrayList<Argument> args = new ArrayList<Argument>();
    	 args.add(param1);
    	 args.add(param2);
    	 args.add(param3);
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
    		 
    		 
             String index = ((SingleValueParameter)inputs.getInputs().get(inputName).get("param1")).getValue();
              String sourcetype = ((SingleValueParameter)inputs.getInputs().get(inputName).get("param2")).getValue();
//String index="index_1";
//String sourcetype="sourcetype1";
             Thread t = new Thread(new InfluxDBEventsUpload(ew, inputName, index, sourcetype));
             t.run();
         }
    	}catch(Exception ex) {
    		ew.synchronizedLog(EventWriter.ERROR, ex.getMessage());
    	}
    }
}