@echo off

goto checkEnv

:envOk
java -Xms64m -Xmx96m -cp "%BVALID_HOME%\bvalid.jar" -Djava.endorsed.dirs="%BVALID_HOME%\lib" -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl net.sf.bvalid.BValid %1 %2 %3 %4 %5 %6 %7 %8 %9
if errorlevel 1 goto endWithError
goto end

:checkEnv
if "%BVALID_HOME%" == "" goto setHome

:checkJarExists
if not exist "%BVALID_HOME%\bvalid.jar" goto jarNotFound
goto envOk

:setHome
set BVALID_HOME=.
goto checkJarExists

:jarNotFound
echo ERROR: %BVALID_HOME%\bvalid.jar was not found.
echo NOTE:  To run bvalid from any directory, BVALID_HOME must be defined.

:endWithError
exit /B 1

:end
