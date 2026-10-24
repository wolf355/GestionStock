@echo off
setlocal

REM ============================================================
REM  Build GestionStock-MODEP
REM  Prérequis :
REM    - JDK 21
REM    - Maven
REM    - JavaFX JMODS
REM ============================================================

REM ======= A MODIFIER SI NECESSAIRE =======
set JAVAFX_JMODS=C:\javafx-jmods-21.0.12

REM ========================================

echo.
echo ====================================================
echo  ETAPE 1 : Compilation Maven
echo ====================================================
call mvn clean package

IF ERRORLEVEL 1 (
    echo.
    echo ECHEC DE LA COMPILATION
    pause
    exit /b 1
)

echo.
echo ====================================================
echo  ETAPE 2 : Suppression anciens fichiers
echo ====================================================

if exist runtime rmdir /S /Q runtime
if exist GestionStock-MODEP rmdir /S /Q GestionStock-MODEP
if exist GestionStock-MODEP-1.0.exe del /Q GestionStock-MODEP-1.0.exe

echo.
echo ====================================================
echo  ETAPE 3 : Creation du runtime Java
echo ====================================================

jlink ^
--module-path "%JAVA_HOME%\jmods;%JAVAFX_JMODS%" ^
--add-modules java.base,java.desktop,java.sql,java.naming,java.logging,java.xml,javafx.controls,javafx.fxml,javafx.graphics ^
--strip-debug ^
--compress=2 ^
--no-header-files ^
--no-man-pages ^
--output runtime

IF ERRORLEVEL 1 (
    echo.
    echo ERREUR DURANT JLINK
    pause
    exit /b 1
)

echo.
echo ====================================================
echo  ETAPE 4 : Generation de l'installeur
echo ====================================================

jpackage ^
--type exe ^
--name GestionStock-MODEP ^
--app-version 1.0 ^
--vendor MODEP ^
--input target ^
--main-jar GestionStock-app.jar ^
--main-class Launcher ^
--runtime-image runtime ^
--win-menu ^
--win-shortcut ^
--dest .

IF ERRORLEVEL 1 (
    echo.
    echo ERREUR JPACKAGE
    pause
    exit /b 1
)

echo.
echo ====================================================
echo             BUILD TERMINE AVEC SUCCES
echo ====================================================
echo.
echo Installeur genere :
echo.
echo    GestionStock-MODEP-1.0.exe
echo.
pause