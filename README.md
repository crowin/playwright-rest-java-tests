# Java Automation Project

This repository contains a **Java automation project** with a modular architecture, designed for both **API** and **UI testing**. The project uses **JUnit 5** for test execution, **AssertJ** for fluent assertions, and **Java Playwright** for UI automation.

---

## Project Structure

The project consists of three modules:

### 1. `common`
- Core utilities and shared components for all modules.
- Implements a **REST client** and API endpoints for interacting with Products.
- Provides **configuration**, base classes, and reusable utilities.
- Used by both `tests-api` and `tests-ui` for API interactions and shared logic.

### 2. `tests-ui`
- UI automation framework based on **Java Playwright**.
- Thread-safe static approach (similar to Selenide) for browser management.
- Implements **Page Object Model (POM)** for pages and UI components.
- Provides reusable **fixtures** and test setup utilities.
- Functional tests leverage **API authorization** and inject tokens into Playwright for faster execution.
- UI assertions use **Playwright assertions** and **AssertJ**.
- Optimized for performance by combining **UI interactions** with **API checks**.

### 3. `tests-api`
- API tests using the shared **REST client** from `common`.
- Validates API endpoints, request/response structures, and business logic.
- Can be executed independently from UI tests.

---

## Technologies and Libraries

- **Java 17+**
- **JUnit 5**
- **AssertJ**
- **Java Playwright**
- **OkHttp3**
- **Jackson** for JSON serialization/deserialization

---

## Key Features

- Modular architecture for **separation of concerns**.
- Reusable **REST client** and API abstractions in `common`.
- **Thread-safe UI automation** leveraging Playwright.
- Combined **API + UI testing approach**:
    - API calls for authentication and token injection.
    - API assertions for faster and more reliable tests.
- **Page Object Model (POM)** for maintainable UI tests.
- Fluent, expressive assertions using AssertJ and Playwright.

---

## How to Run Tests

### UI Tests
1. Configure environment (base URL, credentials) in `common`.
2. Run tests using Gradle:
```bash
./gradlew :tests-ui:test
