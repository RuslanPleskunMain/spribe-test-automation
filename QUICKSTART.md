# Quick Start Guide

Get up and running with the PlayerController test automation framework in 5 minutes!

## 🚀 Prerequisites

Ensure you have installed:
- ✅ Java 11 or higher
- ✅ Maven 3.6 or higher
- ✅ Git
- ✅ Allure

## 📦 Installation

### 1. Clone the Repository
```bash
git clone <your-repository-url>
cd spribe_test_task
```

### 2. Verify Java & Maven
```bash
java -version   # Should show Java 11+
mvn -version    # Should show Maven 3.6+
```

### 3. Install Dependencies
```bash
mvn clean install -DskipTests
```

This will download all required dependencies

## ▶️ Run Your First Test

### Option 1: Run All Tests
```bash
mvn clean test
```

### Option 2: Use the Helper Script

**On macOS/Linux:**
```bash
./run-tests.sh
```

**On Windows:**
```bash
run-tests.bat
```

### Option 3: Run Specific Test Class
```bash
mvn clean test -Dtest=CreatePlayerTest
```

## 📊 View Results

### Quick View - TestNG Report
After test execution, open:
```
target/surefire-reports/index.html
```

### Beautiful Reports - Allure
```bash
# Install Allure (one-time setup)
brew install allure  # macOS
# or download from: https://github.com/allure-framework/allure2/releases

# Generate and view report
mvn allure:serve
```

This will open an interactive report in your browser!

## 📝 Check Logs

View detailed execution logs:
```
target/logs/test-execution.log
```

## ⚙️ Configuration

Want to test against a different environment? Edit:
```
src/test/resources/config.properties
```

Or override at runtime:
```bash
mvn clean test -Dbase.url=http://your-server.com
```

## 🧪 What Gets Tested?

The framework tests all 4 PlayerController endpoints:

| Endpoint | Method | Tests |
|----------|--------|-------|
| `/player/create/{editor}` | POST | 18 tests |
| `/player/get` | GET | 5 tests |
| `/player/update/{editor}/{id}` | PATCH | 16 tests |
| `/player/delete/{editor}` | DELETE | 12 tests |
| Integration Scenarios | Various | 8 tests |

**Total: 57 automated tests** covering positive, negative, and edge cases!

## 🎯 Next Steps

1. **Review the tests**: Check out `src/test/java/com/spribe/tests/`
2. **Read the docs**: See [README.md](README.md) for comprehensive documentation
3. **Check for bugs**: Review [BUGS.md](BUGS.md) for identified issues
4. **View test coverage**: See [TEST_SUMMARY.md](TEST_SUMMARY.md)

## 🐛 Troubleshooting

### Tests are failing
1. Check if the API is accessible: `http://3.68.165.45/swagger-ui.html`
2. Review logs in `target/logs/test-execution.log`
3. Ensure you're using Java 11+

### Maven build fails
```bash
# Clean and rebuild
mvn clean
mvn clean install -DskipTests
```

### Can't generate Allure reports
```bash
# Make sure Allure is installed
allure --version

# If not installed:
# macOS: brew install allure
# Linux: See https://docs.qameta.io/allure/#_installing_a_commandline
# Windows: Use Scoop - scoop install allure
```

## 📖 Key Files

| File | Purpose |
|------|---------|
| `pom.xml` | Maven dependencies & build configuration |
| `src/test/resources/config.properties` | Application configuration |
| `src/test/resources/testng.xml` | Test suite configuration |
| `src/test/resources/log4j2.xml` | Logging configuration |
| `src/test/java/com/spribe/tests/` | All test classes |

## 💡 Pro Tips

1. **Parallel Execution**: Tests run in 3 threads by default (configured in `testng.xml`)
2. **Automatic Cleanup**: Created test data is automatically deleted
3. **Comprehensive Logging**: Every request/response is logged
4. **Allure Integration**: Beautiful reports with request/response details

## 🤝 Need Help?

- Check [README.md](README.md) for detailed documentation
- Review [TEST_SUMMARY.md](TEST_SUMMARY.md) for test coverage
- See [BUGS.md](BUGS.md) for known issues
- Check logs in `target/logs/`
