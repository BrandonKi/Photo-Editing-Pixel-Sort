@echo off
cd %cd%
javac -cp ".;.javafx-mx.jar" Main.java
java -cp ".;.javafx-mx.jar" Main
pause