@echo off
echo ==========================================
echo    COMPILADOR DOMOTICO CON COCO/R
echo ==========================================
echo.

echo Paso 1: Limpiando directorio bin...
if exist bin rmdir /s /q bin
mkdir bin

REM ===================================================================
REM Paso 2 DESACTIVADO: No regenerar el Parser.
REM Estamos usando un Parser.java modificado manualmente.
REM Si necesitas regenerar el parser, borra los 'REM' de este bloque.
REM 
REM echo Paso 2: Generando Parser y Scanner con COCO/R...
REM pushd src\domotica\compiler
REM java -jar ..\..\..\lib\coco.jar -frames ..\..\..\lib -package domotica.compiler Domotica.ATG
REM set ERR=%ERRORLEVEL%
REM popd
REM if %ERR% neq 0 (
REM     echo ERROR: No se pudo generar el parser con COCO/R
REM     pause
REM     exit /b 1
REM )
REM ===================================================================

echo Paso 2: Generando Parser y Scanner con COCO/R... 

REM probar agregarle a javac -encoding UTF-8

echo Paso 3: Compilando proyecto completo...
javac -d bin -cp "lib\coco.jar" ^
    src\domotica\compiler\*.java ^
    src\domotica\ast\*.java ^
    src\domotica\devices\*.java ^
    src\domotica\gui\*.java ^
    src\domotica\runtime\*.java ^
    src\domotica\*.java

if %errorlevel% equ 0 (
    echo.
    echo  COMPILACION EXITOSA
    echo.
    echo Para ejecutar el proyecto:
    echo   java -cp "bin;lib\coco.jar" domotica.Main
) else (
    echo.
    echo  ERROR EN LA COMPILACION
)

pause