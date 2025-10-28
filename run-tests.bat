@echo off
REM Script to run tests on Windows
REM Usage: run-tests.bat [test-class]

echo ======================================
echo PlayerController Test Automation
echo ======================================
echo.

if "%1"=="" (
    echo Running all tests...
    call mvn clean test
) else (
    echo Running test class: %1
    call mvn clean test -Dtest=%1
)

echo.
echo Execution completed!
echo.
echo Reports available at:
echo   - TestNG: target\surefire-reports\index.html
echo   - Logs: target\logs\test-execution.log
echo.
echo To generate Allure report, run:
echo   mvn allure:serve
echo.

pause
