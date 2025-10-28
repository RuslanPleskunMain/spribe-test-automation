#!/bin/bash

# Script to run tests and generate reports
# Usage: ./run-tests.sh [options]
#   -h, --help          Show this help message
#   -r, --report        Generate and open Allure report after tests
#   -t, --test CLASS    Run specific test class (e.g., CreatePlayerTest)
#   -s, --skip-tests    Skip test execution, just generate report from existing results

set -e

GENERATE_REPORT=false
SKIP_TESTS=false
TEST_CLASS=""

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        -h|--help)
            echo "Usage: $0 [options]"
            echo "Options:"
            echo "  -h, --help          Show this help message"
            echo "  -r, --report        Generate and open Allure report after tests"
            echo "  -t, --test CLASS    Run specific test class"
            echo "  -s, --skip-tests    Skip test execution, just generate report"
            exit 0
            ;;
        -r|--report)
            GENERATE_REPORT=true
            shift
            ;;
        -t|--test)
            TEST_CLASS="$2"
            shift 2
            ;;
        -s|--skip-tests)
            SKIP_TESTS=true
            shift
            ;;
        *)
            echo "Unknown option: $1"
            echo "Use -h or --help for usage information"
            exit 1
            ;;
    esac
done

echo "======================================"
echo "PlayerController Test Automation"
echo "======================================"
echo ""

# Run tests
if [ "$SKIP_TESTS" = false ]; then
    echo "📋 Running tests..."
    if [ -n "$TEST_CLASS" ]; then
        echo "   Test class: $TEST_CLASS"
        mvn clean test -Dtest="$TEST_CLASS"
    else
        echo "   Running all tests"
        mvn clean test
    fi
    echo ""
fi

# Generate report
if [ "$GENERATE_REPORT" = true ]; then
    echo "📊 Generating Allure report..."
    mvn allure:serve
fi

echo ""
echo "✅ Execution completed!"
echo ""
echo "Reports available at:"
echo "  - TestNG: target/surefire-reports/index.html"
echo "  - Logs: target/logs/test-execution.log"
echo ""
echo "To generate Allure report, run:"
echo "  mvn allure:serve"
echo ""
