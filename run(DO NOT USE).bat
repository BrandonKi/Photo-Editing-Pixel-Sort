@echo off
cd %cd%
javac -cp ".;.jfxrt.jar" Main.java
java -cp ".;.jfxrt.jar" Main
pause