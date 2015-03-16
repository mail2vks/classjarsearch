Working as a Java Developer and working across multiple projects there is one frequent error which I have encountered is : `Exception in thread "main" java.lang.NoClassDefFound Error`.The reason for this error is missing class file from the CLASSPATH.

This sometimes becomes very frustrating when you have to search for missing class in a set of JAR files. For resolving this error quickly I have written a JAVA utility which searches for class file in all jar files present in a particular directory.

This tool is very simple and it only has 2 inputs. First text input should be class name that we want to search. Class Name can be either full or partial. It can also be '.' or '/' seperated. Second text input is directory location in which search should be conducted. This can be provided using Browse button which displys a basic file browser. Hit Search button and any jar file which has class that matches input would be displayed.

**Download zip file and extract to a local directory. Switch to extracted directory and execute:
java -jar `JarSearch.jar`**

**I have added `JarSearch.exe` which is single download for windows**