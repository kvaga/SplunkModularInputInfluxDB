package ru.kvaga.splunk.modularinputs.influxdb;
import java.util.Random;

import com.splunk.modularinput.Event;
import com.splunk.modularinput.EventWriter;
import com.splunk.modularinput.MalformedDataException;

class InfluxDBEventsUpload implements Runnable {
        private String  influxDBURL, influxDBLogin, influxDBPassword;
        int influxDBDelayInSeconds;
        private EventWriter ew;
        private String inputName;

        public InfluxDBEventsUpload(EventWriter ew, String inputName, String influxDBURL, String influxDBLogin, String influxDBPassword, int influxDBDelayInSeconds) {
            super();
            this.influxDBURL = influxDBURL;
            this.influxDBLogin = influxDBLogin;
            this.influxDBPassword = influxDBPassword;
            this.influxDBDelayInSeconds = influxDBDelayInSeconds;
            this.ew = ew;
            this.inputName = inputName;
        }

        public void run() {
        	
            ew.synchronizedLog(EventWriter.INFO, "Uploading InfluxDB data " + inputName +
                    " started for INFLUXDB_URL= " + influxDBURL + " influxDBLogin=" + influxDBLogin + " influxDBPassword=[********] + delayInSeconds="+influxDBDelayInSeconds );

            final Random randomGenerator = new Random();
            while (true) {
                Event event = new Event();
                event.setStanza(inputName);
                event.setData("INFLUXDB_URL= " + influxDBURL + " influxDBLogin=" + influxDBLogin + " influxDBPassword=[********] + delayInSeconds="+influxDBDelayInSeconds + " data=" + randomGenerator.nextInt(100));

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