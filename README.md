# PlayerController Test Automation Framework

A comprehensive test automation framework for testing the PlayerController REST API using Java, TestNG, and RestAssured.

## 📋 Table of Contents

- [Technology Stack](#-technology-stack)
- [Project Structure](#-project-structure)
- [Prerequisites](#-prerequisites)
- [Installation & Setup](#installation--setup)
- [Running Tests](#-running-tests)
- [Test Reports](#-test-reports)
- [Configuration](#-configuration)
- [Test Coverage](#-test-coverage)
- [Test Types](#-test-types)
- [Key Features](#-key-features)
- [Best Practices Implemented](#-best-practices-implemented)
- [Production-Ready Considerations](#-production-ready-considerations)
- [License](#-license)

## 🛠 Technology Stack

- **Java**: 11+
- **Build Tool**: Maven 3.6+
- **Test Framework**: TestNG 7.8.0
- **API Testing**: RestAssured 5.3.2
- **Reporting**: Allure 2.24.0
- **Logging**: Log4j2 2.20.0
- **JSON Processing**: Jackson 2.15.3

## 📁 Project Structure

```
player-controller-tests/
├── src/
│   └── test/
│       ├── java/
│       │   └── com/
│       │       └── spribe/
│       │           ├── api/              # API client and service layer
│       │           │   ├── ApiClient.java
│       │           │   └── PlayerService.java
│       │           ├── base/             # Base test class
│       │           │   └── BaseTest.java
│       │           ├── config/           # Configuration management
│       │           │   └── ConfigManager.java
│       │           ├── models/           # Data models (POJOs)
│       │           │   ├── Player.java
│       │           │   └── PlayerBuilder.java
│       │           ├── tests/            # Test classes
│       │           │   ├── CreatePlayerTest.java
│       │           │   ├── GetPlayerTest.java
│       │           │   ├── UpdatePlayerTest.java
│       │           │   └── DeletePlayerTest.java
│       │           └── utils/            # Utility classes
│       │               └── TestDataGenerator.java
│       └── resources/
│           ├── config.properties         # Application configuration
│           ├── log4j2.xml               # Logging configuration
│           ├── testng.xml               # TestNG suite configuration
│           └── allure.properties        # Allure configuration
├── pom.xml                              # Maven dependencies
└── README.md                            # This file
```

## 🔧 Prerequisites

1. **Java Development Kit (JDK) 11 or higher**
   ```bash
   java -version
   ```

2. **Apache Maven 3.6 or higher**
   ```bash
   mvn -version
   ```

3. **Allure Command Line** (for generating reports)
   ```bash
   # macOS
   brew install allure
   
   # Linux
   sudo apt-add-repository ppa:qameta/allure
   sudo apt-get update
   sudo apt-get install allure
   
   # Windows
   scoop install allure
   ```

## 🚀 Installation & Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd spribe_test_task
   ```

2. **Install dependencies**
   ```bash
   mvn clean install -DskipTests
   ```

3. **Verify the setup**
   ```bash
   mvn clean compile
   ```

## ▶️ Running Tests

### Run All Tests
```bash
mvn clean test
```

### Run Specific Test Class
```bash
mvn clean test -Dtest=CreatePlayerTest
```

### Run Tests with Custom Configuration
```bash
mvn clean test -Dbase.url=http://3.68.165.45 -Dthread.count=3
```

### Run Tests in Parallel (3 threads)
The framework is configured to run tests in parallel by default with 3 threads as specified in `testng.xml`.

### Maven Commands
- **Clean and Run Tests**: `mvn clean test`
- **Skip Tests**: `mvn clean install -DskipTests`
- **Run with Verbose Output**: `mvn clean test -X`

## 📊 Test Reports

### Allure Reports

1. **Generate Allure Report**
   ```bash
   mvn allure:report
   ```

2. **View Allure Report**
   ```bash
   mvn allure:serve
   ```
   This will start a local web server and open the report in your default browser.

3. **Alternative: Generate and Open Report Manually**
   ```bash
   allure generate target/allure-results --clean -o target/allure-report
   allure open target/allure-report
   ```

### TestNG Reports

After running tests, you can find the TestNG HTML reports at:
```
target/surefire-reports/index.html
```

### Log Files

Test execution logs are available at:
```
target/logs/test-execution.log
```

## ⚙️ Configuration

### Application Configuration (`src/test/resources/config.properties`)

```properties
# Application Configuration
base.url=http://3.68.165.45
swagger.path=/swagger-ui.html

# Test Configuration
thread.count=3
timeout.seconds=10

# Pre-existing Users
supervisor.login=supervisor
admin.login=admin
```

### Logging Configuration (`src/test/resources/log4j2.xml`)

Logs are configured to output to:
- Console (for real-time monitoring)
- File: `target/logs/test-execution.log` (for persistence)

Log levels:
- `com.spribe` package: DEBUG
- `io.restassured` package: DEBUG
- Root level: INFO

### Test Suite Configuration (`src/test/resources/testng.xml`)

- **Parallel Execution**: Methods level
- **Thread Count**: 3
- **Verbose Level**: 1

## 🧪 Test Coverage

### Create Player Tests (CreatePlayerTest.java)
**Positive Tests:**
- ✅ Create player by supervisor
- ✅ Create player by admin
- ✅ Create player with admin role
- ✅ Create player with minimum age (16)
- ✅ Create player with maximum age (60)
- ✅ Create player with female gender

**Negative Tests:**
- ❌ Create player by user (forbidden)
- ❌ Create player with age below minimum (15)
- ❌ Create player with age above maximum (61)
- ❌ Create player with duplicate login
- ❌ Create player with duplicate screenName
- ❌ Create player with short password (< 7 chars)
- ❌ Create player with long password (> 15 chars)
- ❌ Create player with password without numbers
- ❌ Create player with password without letters
- ❌ Create player with password with special characters
- ❌ Create player with invalid gender
- ❌ Create player with invalid role

### Get Player Tests (GetPlayerTest.java)
**Positive Tests:**
- ✅ Get player by valid ID
- ✅ Get player multiple times (consistency check)

**Negative Tests:**
- ❌ Get player with non-existent ID
- ❌ Get player with negative ID
- ❌ Get player with zero ID

### Update Player Tests (UpdatePlayerTest.java)
**Positive Tests:**
- ✅ Update player age by supervisor
- ✅ Update player screenName by supervisor
- ✅ Update player gender by supervisor
- ✅ Update player password by supervisor
- ✅ Update player by admin
- ✅ Update own profile by user
- ✅ Update multiple fields at once

**Negative Tests:**
- ❌ Update player by user without permissions
- ❌ Update player with age below minimum
- ❌ Update player with age above maximum
- ❌ Update player with invalid gender
- ❌ Update player with duplicate screenName
- ❌ Update player with non-existent ID
- ❌ Update player with short password
- ❌ Update player with long password

### Delete Player Tests (DeletePlayerTest.java)
**Positive Tests:**
- ✅ Delete user by supervisor
- ✅ Delete admin by supervisor
- ✅ Delete user by admin

**Negative Tests:**
- ❌ Delete player by user (forbidden)
- ❌ Delete own profile by user
- ❌ Delete non-existent player
- ❌ Delete player with negative ID
- ❌ Delete player twice
- ❌ Delete self by admin
- ❌ Admin tries to delete another admin
- ❌ Delete supervisor (documented bug)

## 🎯 Key Features

1. **Modular Architecture**: Separation of concerns with dedicated packages for API, models, tests, and utilities
2. **Builder Pattern**: Flexible test data creation using PlayerBuilder
3. **Comprehensive Logging**: Detailed logs for debugging and audit trails
4. **Allure Integration**: Beautiful, interactive test reports with step-by-step execution details
5. **Parallel Execution**: Tests run in 3 threads for faster execution
6. **Configuration Management**: Externalized configuration for easy environment switching
7. **Test Data Management**: Automatic cleanup of created test data
8. **Negative Testing**: Extensive negative test cases covering edge cases and invalid inputs
9. **RestAssured Filters**: Request/Response logging with Allure integration

## 📝 Best Practices Implemented

- ✅ Page Object Model pattern adapted for API testing (Service Layer)
- ✅ Single Responsibility Principle in class design
- ✅ DRY (Don't Repeat Yourself) principle
- ✅ Meaningful test names and descriptions
- ✅ Proper use of TestNG assertions
- ✅ Test data cleanup in @AfterMethod
- ✅ Comprehensive error handling
- ✅ Proper use of HTTP status codes
- ✅ Boundary value testing
- ✅ Negative testing for all critical flows

## 🔒 Production-Ready Considerations

This framework is designed with production-ready principles:
- **Reliability**: Comprehensive test coverage with cleanup mechanisms
- **Maintainability**: Clear structure and documentation
- **Scalability**: Parallel execution support
- **Observability**: Detailed logging and reporting
- **Configuration**: Environment-specific settings externalized
- **Safety**: Tests validate before modifying system state

## 📄 License

This project is created for the Spribe test task.
