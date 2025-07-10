@echo off
echo Setting up Config Repository for Spring Cloud Config Server...

REM Create config repository directory if it doesn't exist
if not exist "%~dp0config-repo" mkdir "%~dp0config-repo"

REM Copy sample configuration files to the config repository
echo Copying sample configuration files...
copy "%~dp0src\main\resources\sample-config\application.yml" "%~dp0config-repo\"
copy "%~dp0src\main\resources\sample-config\application-dev.yml" "%~dp0config-repo\"
copy "%~dp0src\main\resources\sample-config\myapp.yml" "%~dp0config-repo\"
copy "%~dp0src\main\resources\sample-config\myapp-prod.yml" "%~dp0config-repo\"

REM Initialize Git repository if not already initialized
cd "%~dp0config-repo"
if not exist .git (
    echo Initializing Git repository...
    git init
    git config user.email "cooingpop@gmail.com"
    git config user.name "cooingpop"
)

REM Add and commit files
echo Adding files to Git repository...
git add .
git commit -m "Initial configuration setup"

echo.
echo Config Repository setup complete!
echo Location: %~dp0config-repo
echo.
echo You can now start the Spring Cloud Config Server.
