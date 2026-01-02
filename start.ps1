$ErrorActionPreference = 'Stop'

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$backendDir = Join-Path $root 'backend'
$frontendDir = Join-Path $root 'frontend'

if (-not (Test-Path $backendDir)) { throw "backend directory not found: $backendDir" }
if (-not (Test-Path $frontendDir)) { throw "frontend directory not found: $frontendDir" }

if (-not (Get-Command mvn -ErrorAction SilentlyContinue)) { throw "mvn not found in PATH" }
if (-not (Get-Command npm -ErrorAction SilentlyContinue)) { throw "npm not found in PATH" }

Write-Host "Starting backend: http://localhost:8080"
Start-Process -FilePath cmd.exe -WorkingDirectory $backendDir -ArgumentList @(
  '/c',
  'mvn',
  'spring-boot:run',
  '-Dspring-boot.run.profiles=h2'
)

if (-not (Test-Path (Join-Path $frontendDir 'node_modules'))) {
  Write-Host "Installing frontend dependencies (npm install)"
  Start-Process -FilePath cmd.exe -WorkingDirectory $frontendDir -ArgumentList @('/c', 'npm', 'install') -Wait
}

Write-Host "Starting frontend: http://localhost:5173"
Start-Process -FilePath cmd.exe -WorkingDirectory $frontendDir -ArgumentList @('/c', 'npm', 'run', 'dev', '--', '--port', '5173')

Write-Host "Started."

