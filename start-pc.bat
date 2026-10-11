@echo off
title Routines AI - PC Desktop Launcher
echo ========================================================
echo   Routines AI - Genesis OS Family Kiosk (PC Version)
echo ========================================================
echo.
echo Launching Routines AI on PC...
echo.

cd /d "%~dp0desktop"

REM If electron is installed locally in node_modules, launch with electron
if exist "node_modules\.bin\electron.cmd" (
    start "" "node_modules\.bin\electron.cmd" .
    exit /b 0
)

REM Otherwise, launch with zero-dependency Node server and open default browser in kiosk window
where node >nul 2>nul
if %ERRORLEVEL% equ 0 (
    echo Starting local wall kiosk runner...
    start "" node server.js --open
    exit /b 0
)

REM Fallback if neither is found
echo Error: Node.js was not detected in PATH.
echo Please install Node.js from https://nodejs.org to run on PC.
pause
