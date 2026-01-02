@echo off
setlocal EnableExtensions EnableDelayedExpansion

set "ROOT=%~dp0"
if "%ROOT:~-1%"=="\" set "ROOT=%ROOT:~0,-1%"
set "BACKEND_DIR=%ROOT%\backend"
set "FRONTEND_DIR=%ROOT%\frontend"

if not exist "%BACKEND_DIR%\" (
  echo backend directory not found: %BACKEND_DIR%\
  exit /b 1
)
if not exist "%FRONTEND_DIR%\" (
  echo frontend directory not found: %FRONTEND_DIR%\
  exit /b 1
)

set "BACKEND_PORT=8080"
call :pick_free_port BACKEND_PORT 8080 8099
if not "!BACKEND_PORT!"=="8080" (
  echo Port 8080 is in use. Using backend port !BACKEND_PORT! instead.
)

if not exist "%FRONTEND_DIR%\node_modules\" (
  echo Installing frontend dependencies (npm install)
  pushd "%FRONTEND_DIR%"
  call npm install
  popd
)

set "FRONTEND_PORT=5173"
call :pick_free_port FRONTEND_PORT 5173 5199
if not "!FRONTEND_PORT!"=="5173" (
  echo Port 5173 is in use. Using frontend port !FRONTEND_PORT! instead.
)

echo Starting backend: http://localhost:!BACKEND_PORT!
start "backend" cmd /k "cd /d ""%BACKEND_DIR%"" && set SERVER_PORT=!BACKEND_PORT!&& set SPRING_DATASOURCE_URL=jdbc:h2:file:./data/perm_platform_!BACKEND_PORT!;MODE=MySQL;DATABASE_TO_LOWER=TRUE;AUTO_SERVER=TRUE&& mvn spring-boot:run -Dspring-boot.run.profiles=h2"

echo Waiting for frontend then opening browser: http://localhost:!FRONTEND_PORT!/
start "" /B cmd /Q /C "for /L %%i in (1,1,60) do (netstat -ano ^| findstr /C::!FRONTEND_PORT! ^| findstr LISTENING >nul && (start http://localhost:!FRONTEND_PORT!/ & exit /b 0) || timeout /t 1 >nul)"

echo Starting frontend in this window: http://localhost:!FRONTEND_PORT!
pushd "%FRONTEND_DIR%"
set "VITE_API_BASE_URL=http://localhost:!BACKEND_PORT!"
npm run dev -- --port !FRONTEND_PORT!
popd
exit /b 0

:pick_free_port
set "VAR_NAME=%~1"
set /a p=%~2
set /a max=%~3
:pick_free_port_loop
for /f "tokens=5" %%a in ('netstat -ano ^| findstr /R /C:":!p! .*LISTENING"') do (
  set "IN_USE=1"
)
if defined IN_USE (
  set "IN_USE="
  set /a p+=1
  if !p! GTR !max! (
    echo No free port available in range %~2-%~3
    exit /b 2
  )
  goto pick_free_port_loop
)
set "%VAR_NAME%=!p!"
exit /b 0
