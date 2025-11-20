@echo off
echo Generando Parser y Scanner con COCO/R...
java -jar lib\coco.jar -package domotica.compiler src\domotica\compiler\Domotica.ATG
echo Parser y Scanner generados exitosamente!
pause