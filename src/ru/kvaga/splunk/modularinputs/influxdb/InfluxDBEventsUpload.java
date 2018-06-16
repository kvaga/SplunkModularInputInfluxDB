package ru.kvaga.splunk.modularinputs.influxdb;
import java.util.Random;

import com.splunk.modularinput.Event;
import com.splunk.modularinput.EventWriter;
import com.splunk.modularinput.MalformedDataException;

class InfluxDBEventsUpload implements Runnable {
        private String index, sourcetype;
        EventWriter ew;
        String inputName;

        public InfluxDBEventsUpload(EventWriter ew, String inputName, String index, String sourcetype) {
            super();
            this.index = index;
            this.sourcetype = sourcetype;
            this.ew = ew;
            this.inputName = inputName;
        }

        public void run() {
        	System.out.println("");
            ew.synchronizedLog(EventWriter.INFO, "Uploading InfluxDB data " + inputName +
                    " started to index= " + index + " sourcetype=" + sourcetype   );

            final Random randomGenerator = new Random();
            while (true) {
                Event event = new Event();
                event.setStanza(inputName);
                event.setData("index=" + index + "sourcetype=" + sourcetype + "data: " + randomGenerator.nextInt(100));

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
    }