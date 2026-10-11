@echo off
title Create Routines AI Desktop Shortcut
echo Creating Routines AI desktop shortcut...

powershell -NoProfile -ExecutionPolicy Bypass -Command ^
  "$WshShell = New-Object -ComObject WScript.Shell; " ^
  "$Desktop = [System.Environment]::GetFolderPath('Desktop'); " ^
  "$Shortcut = $WshShell.CreateShortcut([System.IO.Path]::Combine($Desktop, 'Routines AI.lnk')); " ^
  "$AppDir = (Resolve-Path '%~dp0desktop').Path; " ^
  "$Electron = Join-Path $AppDir 'node_modules\electron\dist\electron.exe'; " ^
  "if (Test-Path $Electron) { " ^
  "  $Shortcut.TargetPath = $Electron; " ^
  "  $Shortcut.Arguments = '.'; " ^
  "} else { " ^
  "  $Shortcut.TargetPath = (Resolve-Path '%~dp0start-pc.bat').Path; " ^
  "} " ^
  "$Shortcut.WorkingDirectory = $AppDir; " ^
  "$Icon = Join-Path $AppDir 'icon.ico'; " ^
  "if (Test-Path $Icon) { $Shortcut.IconLocation = \"$Icon,0\"; } " ^
  "$Shortcut.Description = 'Routines AI - Family Wall Kiosk'; " ^
  "$Shortcut.Save(); " ^
  "Write-Host 'Shortcut created successfully on Desktop!'"

echo Done!
pause
