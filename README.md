# Intern Hub Common Library

A shared Java library providing common utilities, exception handling, and context management for Spring Boot microservices.

[![Java Version](https://img.shields.io/badge/Java-25-blue.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.1-green.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-See%20LICENSE-blue.svg)](LICENSE)

## Table of Contents

- [Overview](#overview)
- [Requirements](#requirements)
- [Installation](#installation)
- [Features](#features)
  - [Global Exception Handling](#global-exception-handling)
  - [Permission-Based Access Control](#permission-based-access-control)
  - [Context Management](#context-management)
  - [Snowflake ID Generator](#snowflake-id-generator)
  - [Utility Classes](#utility-classes)
  - [Standard API Response](#standard-api-response)
- [Configuration](#configuration)
- [Usage Examples](#usage-examples)
- [API Documentation](#api-documentation)
- [Contributing](#contributing)
- [License](#license)

## Overview

This library provides essential building blocks for building robust Spring Boot applications, including:

- **Global Exception Handling**: Centralized exception handling with standardized API responses
- **Permission-Based Authorization**: Declarative permission checking using annotations
- **Context Management**: Thread-safe request and authentication context using Java's `ScopedValue`
- **Snowflake ID Generator**: Distributed unique ID generation
- **Utility Classes**: Date/time helpers and random generators

## Requirements

- **Java 25** or higher (uses `ScopedValue` and other modern features)
- **Spring Boot 4.0.1** or compatible version
- **Gradle 9.x** for building

## Installation

### Using JitPack

Add the JitPack repository to your `build.gradle.kts`:

```kotlin
repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}
```

Add the dependency:

```kotlin
dependencies {
    implementation("com.github.FPT-IS-Intern:Intern-Hub-Common-Library:1.0.0")
}
```

### Building from Source

```bash
git clone https://github.com/your-repo/common.git
cd common
./gradlew build
./gradlew publishToMavenLocal
```

## Features

### Global Exception Handling

Enable centralized exception handling across all REST controllers by adding the `@EnableGlobalExceptionHandler` annotation:

```java
@SpringBootApplication
@EnableGlobalExceptionHandler
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

#### Supported Exceptions

| Exception | HTTP Status | Default Code |
|-----------|-------------|--------------|
| `BadRequestException` | 400 | `bad.request` |
| `UnauthorizeException` | 401 | `unauthorized` |
| `ForbiddenException` | 403 | `forbidden` |
| `NotFoundException` | 404 | `resource.not.found` |
| `ConflictDataException` | 409 | `conflict.data` |
| `TooManyRequestException` | 429 | `too.many.requests` |
| `InternalErrorException` | 500 | `internal.server.error` |

#### Throwing Exceptions

```java
// Throw with code only
throw new NotFoundException("user.not.found");

// Throw with code and message
throw new BadRequestException("invalid.email", "Email format is invalid");
```

### Permission-Based Access Control

Use the `@HasPermission` annotation to declaratively check permissions on controller methods:

```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/{id}")
    @HasPermission(resource = "user", action = "read", scope = Scope.OWN)
    public User getUser(@PathVariable Long id) {
        // Only users with "user:read" permission and at least OWN scope can access
        return userService.findById(id);
    }

    @DeleteMapping("/{id}")
    @HasPermission(resource = "user", action = "delete", scope = Scope.ALL)
    public void deleteUser(@PathVariable Long id) {
        // Only admins with ALL scope can delete users
        userService.delete(id);
    }
}
```

#### Scope Levels

| Scope | Value | Description |
|-------|-------|-------------|
| `OWN` | 1 | User can only access their own resources |
| `TENANT` | 2 | User can access resources within their organization |
| `ALL` | 3 | User can access all resources (admin level) |

### Context Management

The library provides thread-safe context holders using Java's `ScopedValue` for virtual thread compatibility.

#### Request Context

```java
// Setting the context (typically in a filter)
RequestContext requestContext = new RequestContext(
    "trace-123",    // traceId
    "request-456",  // requestId
    System.currentTimeMillis(),  // startTime
    "web-client"    // source
);

ScopedValue.runWhere(RequestContextHolder.REQUEST_CONTEXT, requestContext, () -> {
    // Context is available throughout this scope
    RequestContext ctx = RequestContextHolder.get();
    System.out.println("Request ID: " + ctx.requestId());
});
```

#### Authentication Context

```java
// Setting the auth context (typically in an authentication filter)
Map<String, Scope> permissions = Map.of(
    "user:read", Scope.OWN,
    "user:write", Scope.OWN,
    "order:read", Scope.TENANT
);

AuthContext authContext = new AuthContext(userId, permissions);

ScopedValue.runWhere(AuthContextHolder.AUTH_CONTEXT, authContext, () -> {
    // Auth context is available throughout this scope
    AuthContext ctx = AuthContextHolder.get().orElseThrow();
    System.out.println("User ID: " + ctx.userId());
});
```

### Snowflake ID Generator

Generate unique 64-bit IDs suitable for distributed systems.

#### Auto-Configuration

The Snowflake generator is auto-configured. Configure it in `application.yml`:

```yaml
snowflake:
  machine-id: 1  # Unique ID for each instance (0-1023)
```

#### Usage

```java
@Service
public class MyService {
    
    private final Snowflake snowflake;
    
    public MyService(Snowflake snowflake) {
        this.snowflake = snowflake;
    }
    
    public Long generateId() {
        return snowflake.next();
    }
    
    public long getTimestamp(long id) {
        return snowflake.extractTimestamp(id);
    }
    
    public long getMachineId(long id) {
        return snowflake.extractMachineId(id);
    }
}
```

#### ID Structure

```
| 41 bits: timestamp | 10 bits: machine ID | 12 bits: sequence |
```

- **Timestamp**: Milliseconds since custom epoch (default: Jan 1, 2025)
- **Machine ID**: 0-1023 (must be unique per instance)
- **Sequence**: 0-4095 (allows 4096 IDs per millisecond per machine)

### Utility Classes

#### DateTimeHelper

Utility class for converting between different date-time representations:

```java
// From epoch milliseconds
Instant instant = DateTimeHelper.toInstant(epochMillis);
LocalDateTime localDateTime = DateTimeHelper.toLocalDateTime(epochMillis);
ZonedDateTime zonedDateTime = DateTimeHelper.toZonedDateTime(epochMillis);
String isoString = DateTimeHelper.toISOString(epochMillis);

// To epoch milliseconds
long millis = DateTimeHelper.from(instant);
long millis = DateTimeHelper.from(localDateTime);
long millis = DateTimeHelper.fromISOString("2025-01-22T10:30:00Z");

// Current time
long now = DateTimeHelper.currentTimeMillis();
LocalDateTime nowLocal = DateTimeHelper.now();
```

#### RandomGenerator

Secure random value generation:

```java
// Generate URL-safe secret key (64 bytes, Base64 encoded)
String secretKey = RandomGenerator.generateSecretKey();

// Generate random numeric string
String otp = RandomGenerator.randomNumberString(6, false);  // e.g., "847291"

// Generate random alphanumeric string
String token = RandomGenerator.randomAlphaNumericString(32);

// Generate secure random bytes
byte[] bytes = RandomGenerator.randomSecureBytes(16);

// Generate random boolean
boolean flag = RandomGenerator.randomBoolean();
```

### Standard API Response

All API responses use a consistent structure through `ResponseApi`:

```json
{
  "status": null,
  "data": {
    "id": 123,
    "name": "John Doe"
  },
  "metaData": null
}
```

For error responses, the global exception handler automatically returns:

```json
{
  "status": {
    "code": "resource.not.found",
    "message": "User not found",
    "errors": null
  },
  "data": null,
  "metaData": {
    "requestId": "abc-123",
    "traceId": "xyz-789",
    "signature": null,
    "timestamp": 1704067200000
  }
}
```

#### Creating Responses

```java
@RestController
public class UserController {

    @GetMapping("/users/{id}")
    public ResponseApi<User> getUser(@PathVariable Long id) {
        User user = userService.findById(id);
        // For success, just return the data
        return ResponseApi.ok(user);
    }
    
    @PostMapping("/users")
    public ResponseApi<User> createUser(@RequestBody CreateUserRequest request) {
        // For errors, throw an exception - the global handler will format the response
        if (userService.existsByEmail(request.email())) {
            throw new ConflictDataException("user.email.exists", "Email already registered");
        }
        User user = userService.create(request);
        return ResponseApi.ok(user);
    }
}
```

#### Available Factory Methods

```java
// Simple success response with data only
ResponseApi.ok(data);

// Success response with data and metadata
ResponseApi.ok(data, ResponseMetadata.fromRequestId());

// Full control over all fields
ResponseApi.of(status, data, metaData);
```

## Configuration

### Application Properties

```yaml
# Snowflake configuration
snowflake:
  machine-id: 1  # Required for distributed deployments (0-1023)

# JVM timezone (for DateTimeHelper)
# Set via: -Duser.timezone=America/New_York
```

### Spring Boot Auto-Configuration

The library registers the following auto-configurations:

- `SnowflakeAutoConfiguration` - Automatically creates a `Snowflake` bean

To see the auto-configuration file location:
```
src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
```

## Usage Examples

### Complete Filter Setup

```java
@Component
public class ContextFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, 
                         FilterChain chain) throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        
        // Create request context
        RequestContext requestContext = new RequestContext(
            UUID.randomUUID().toString(),  // traceId
            UUID.randomUUID().toString(),  // requestId
            System.currentTimeMillis(),
            httpRequest.getHeader("X-Source")
        );
        
        // Create auth context from JWT or session
        AuthContext authContext = extractAuthContext(httpRequest);
        
        // Run request within both contexts
        try {
            ScopedValue.where(RequestContextHolder.REQUEST_CONTEXT, requestContext)
                .where(AuthContextHolder.AUTH_CONTEXT, authContext)
                .run(() -> {
                    try {
                        chain.doFilter(request, response);
                    } catch (IOException | ServletException e) {
                        throw new RuntimeException(e);
                    }
                });
        } catch (RuntimeException e) {
            if (e.getCause() instanceof IOException) {
                throw (IOException) e.getCause();
            }
            if (e.getCause() instanceof ServletException) {
                throw (ServletException) e.getCause();
            }
            throw e;
        }
    }
}
```

### Service Layer Example

```java
@Service
public class OrderService {

    private final Snowflake snowflake;
    private final OrderRepository orderRepository;

    public OrderService(Snowflake snowflake, OrderRepository orderRepository) {
        this.snowflake = snowflake;
        this.orderRepository = orderRepository;
    }

    public Order createOrder(CreateOrderRequest request) {
        // Throw exception if not authenticated - global handler returns 401
        AuthContext auth = AuthContextHolder.get()
            .orElseThrow(() -> new UnauthorizeException("auth.required", "Authentication required"));
        
        Order order = new Order();
        order.setId(snowflake.next());
        order.setUserId(auth.userId());
        order.setCreatedAt(DateTimeHelper.currentTimeMillis());
        
        return orderRepository.save(order);
    }

    public Order getOrder(Long orderId) {
        // Throw exception if not found - global handler returns 404
        return orderRepository.findById(orderId)
            .orElseThrow(() -> new NotFoundException("order.not.found", 
                "Order with ID " + orderId + " not found"));
    }
}
```

### Controller Layer Example

```java
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @HasPermission(resource = "order", action = "create", scope = Scope.OWN)
    public ResponseApi<Order> createOrder(@RequestBody @Valid CreateOrderRequest request) {
        // Simply return data - errors are thrown as exceptions
        Order order = orderService.createOrder(request);
        return ResponseApi.ok(order);
    }

    @GetMapping("/{id}")
    @HasPermission(resource = "order", action = "read", scope = Scope.OWN)
    public ResponseApi<Order> getOrder(@PathVariable Long id) {
        // If order not found, service throws NotFoundException
        // Global handler converts it to proper error response
        Order order = orderService.getOrder(id);
        return ResponseApi.ok(order);
    }
}
```

## API Documentation

Full JavaDoc is available for all public APIs. Generate documentation using:

```bash
./gradlew javadoc
```

Documentation will be generated in `build/docs/javadoc/`.

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the terms specified in the [LICENSE](LICENSE) file.

---

**Note**: This library uses Java 25 features including `ScopedValue`. Make sure your runtime environment supports Java 25 or later.
