cls
ECHO OFF
set JDK_PATH="c:\Program Files\Java\jdk1.8.0_144\bin"
set SPLUNK_APP_PATH=C:\Program Files\Splunk\etc\apps\ModularInputInfluxDB
set SRC_APP_FILES_PATH=SplunkEnterprise

echo =======================================
echo Build and update application's jar file
%JDK_PATH%\jar.exe -cvfm splunk-modularinputs-influxdb.jar MANIFEST.MF -C bin/ . && %JDK_PATH%\java.exe -jar splunk-modularinputs-influxdb.jar --scheme && copy splunk-modularinputs-influxdb.jar "%SPLUNK_APP_PATH%\jars" /Y

echo =======================================
echo Update all other application's files
copy %SRC_APP_FILES_PATH%\app.conf "%SPLUNK_APP_PATH%\default" /Y
copy %SRC_APP_FILES_PATH%\README\* "%SPLUNK_APP_PATH%\README\" /Y

echo =======================================
echo Update shims
set CMD=xcopy %SRC_APP_FILES_PATH%\darwin_x86_64 "%SPLUNK_APP_PATH%\" /Y /E
echo %CMD%
%CMD%
set CMD=xcopy %SRC_APP_FILES_PATH%\linux_x86 "%SPLUNK_APP_PATH%\" /Y /E
echo %CMD%
%CMD%
set CMD=xcopy %SRC_APP_FILES_PATH%\linux_x86_64 "%SPLUNK_APP_PATH%\" /Y /E
echo %CMD%
%CMD%
set CMD=xcopy %SRC_APP_FILES_PATH%\windows_x86 "%SPLUNK_APP_PATH%\" /Y /E
echo %CMD%
%CMD%
set CMD=xcopy %SRC_APP_FILES_PATH%\windows_x86_64 "%SPLUNK_APP_PATH%\" /Y /E
echo %CMD%
%CMD%
