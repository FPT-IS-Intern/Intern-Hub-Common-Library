# Intern Hub Common Library

A shared Java library providing common utilities, exception handling, and context management for Spring Boot microservices.

[![Java Version](https://img.shields.io/badge/Java-25-blue.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.2-green.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-See%20LICENSE-blue.svg)](LICENSE)

> [!CAUTION]
>
> ## Breaking Changes in v2.0.0
>
> The following classes have been **moved to the [Security Library](../security)**:
>
> - `HasPermission` → `com.intern.hub.starter.security.annotation`
> - `HasPermissionAspect` → `com.intern.hub.starter.security.annotation.aspect`
> - `AuthContext` → `com.intern.hub.starter.security.context`
> - `AuthContextHolder` → `com.intern.hub.starter.security.context`
> - `Scope` → `com.intern.hub.starter.security.dto`
>
> **Migration**: If you use permission-based access control or authentication context, add the security library dependency and update your imports.

> [!CAUTION]
>
> ## Breaking Changes in v2.0.4
>
> ### 1. `RequestContext` — Simplified Structure
>
> `RequestContext` now holds only a `requestId` and a generic attributes map. The `traceId`, `startTime`, and `source` fields have been **removed**.
>
> **Before:**
> ```java
> new RequestContext(traceId, requestId, startTime, source);
> context.traceId();
> context.startTime();
> context.source();
> ```
>
> **After:**
> ```java
> new RequestContext(requestId);
> context.requestId();
> context.putAttribute("key", value);
> context.getAttribute("key");
> ```
>
> ### 2. `ContextFilter` — Headers Changed
>
> The filter no longer reads `X-Trace-ID` or `X-Source` headers. Only `X-Request-ID` is consumed.
>
> **Removed headers**: `X-Trace-ID`, `X-Source`
>
> **Trace IDs** are now sourced from **OpenTelemetry** (see `ResponseMetadata` below).
>
> ### 3. `ResponseMetadata` — OpenTelemetry Integration
>
> `traceId` is no longer populated from `RequestContext`. It is now read from the active OpenTelemetry span via `Span.current().getSpanContext().getTraceId()`. If no valid span is present, `traceId` will be `null`.
>
> A new overload `fromRequestId(String signature)` is available to attach a custom signature to the metadata.
>
> ### 4. `ResponseApi.ok(T data)` — Response Shape Changed
>
> `ResponseApi.ok(data)` now **always** includes a `ResponseStatus` with code `"success"` and auto-populated `ResponseMetadata`. Previously `status` and `metaData` were `null`.
>
> **Before:**
> ```json
> { "status": null, "data": { ... }, "metaData": null }
> ```
>
> **After:**
> ```json
> { "status": { "code": "success", "message": null, "errors": null }, "data": { ... }, "metaData": { "requestId": "...", "traceId": "...", "signature": null, "timestamp": 1704067200000 } }
> ```
>
> If you need to pass custom metadata, use `ResponseApi.ok(data, metaData)` — this variant sets `status` to `null`.
>
> **Migration**: If your clients check `"status": null` for success detection, update them to check `"status.code": "success"` or use `ResponseApi.ok(data, metaData)`.

## Table of Contents

- [Overview](#overview)
- [Requirements](#requirements)
- [Installation](#installation)
- [Features](#features)
  - [Global Exception Handling](#global-exception-handling)
  - [Request Context Management](#request-context-management)
  - [Snowflake ID Generator](#snowflake-id-generator)
  - [Utility Classes](#utility-classes)
  - [Standard API Response](#standard-api-response)
  - [Pagination Model](#pagination-model)
- [Configuration](#configuration)
  - [Disabling Auto-Configurations](#disabling-auto-configurations)
  - [Using Request Context](#using-request-context)
- [Usage Examples](#usage-examples)
- [API Documentation](#api-documentation)
- [Contributing](#contributing)
- [License](#license)

## Overview

This library provides essential building blocks for building robust Spring Boot applications, including:

- **Global Exception Handling**: Centralized exception handling with standardized API responses
- **Request Context Management**: Thread-safe request context using Java's `ScopedValue`
- **Snowflake ID Generator**: Distributed unique ID generation
- **Utility Classes**: Date/time helpers and random generators

## Requirements

- **Java 25** or higher (uses `ScopedValue` and other modern features)
- **Spring Boot 4.0.2** or compatible version
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
    implementation("com.github.FPT-IS-Intern:Intern-Hub-Common-Library:2.0.4")
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

| Exception                                | HTTP Status | Default Code            |
| ---------------------------------------- | ----------- | ----------------------- |
| `BadRequestException`                    | 400         | `bad.request`           |
| `UnauthorizeException`                   | 401         | `unauthorized`          |
| `ForbiddenException`                     | 403         | `forbidden`             |
| `NotFoundException`                      | 404         | `resource.not.found`    |
| `NoHandlerFoundException`                | 404         | `handler.not.found`     |
| `ConflictDataException`                  | 409         | `conflict.data`         |
| `TooManyRequestException`                | 429         | `too.many.requests`     |
| `InternalErrorException`                 | 500         | `internal.server.error` |
| `MethodArgumentTypeMismatchException`    | 400         | `invalid.parameter`     |
| `MethodArgumentNotValidException`        | 400         | `invalid.parameter`     |
| `Exception` (catch-all)                  | 500         | `internal.server.error` |

#### Throwing Exceptions

```java
// Throw with code only
throw new NotFoundException("user.not.found");

// Throw with code and message
throw new BadRequestException("invalid.email", "Email format is invalid");
```

#### Validation Error Response

When `MethodArgumentNotValidException` is thrown (e.g., via `@Valid` on a request body), the handler returns a structured list of field-level errors in the `data` field:

```json
{
  "status": {
    "code": "invalid.parameter",
    "message": "Invalid request parameters",
    "errors": null
  },
  "data": [
    { "field": "email", "message": "must not be blank" },
    { "field": "age",   "message": "must be greater than 0" }
  ],
  "metaData": {
    "requestId": "abc-123",
    "traceId": "xyz-789",
    "signature": null,
    "timestamp": 1704067200000
  }
}
```

### Request Context Management

The library provides thread-safe context holders using Java's `ScopedValue` for virtual thread compatibility.

`RequestContext` now stores a `requestId` and a flexible attributes map for any additional per-request data.

```java
// Setting the context (typically in a filter)
RequestContext requestContext = new RequestContext("request-456");

ScopedValue.where(RequestContextHolder.REQUEST_CONTEXT, requestContext).run(() -> {
    // Context is available throughout this scope
    RequestContext ctx = RequestContextHolder.get();
    System.out.println("Request ID: " + ctx.requestId());

    // Store and retrieve custom attributes
    ctx.putAttribute("userId", 42L);
    Long userId = (Long) ctx.getAttribute("userId");
});
```

> **Note**: `traceId`, `startTime`, and `source` are no longer stored in `RequestContext`. Use OpenTelemetry instrumentation for distributed tracing.

### Snowflake ID Generator

Generate unique 64-bit IDs suitable for distributed systems.

#### Auto-Configuration

The Snowflake generator is auto-configured. Configure it in `application.yml`:

```yaml
snowflake:
  machine-id: 1 # Unique ID for each instance (0-1023)
```

The bean is only created if no other `Snowflake` bean exists in the application context (`@ConditionalOnMissingBean`).

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
Instant instant           = DateTimeHelper.toInstant(epochMillis);
LocalDateTime localDT     = DateTimeHelper.toLocalDateTime(epochMillis);
ZonedDateTime zonedDT     = DateTimeHelper.toZonedDateTime(epochMillis);
OffsetDateTime offsetDT   = DateTimeHelper.toOffsetDateTime(epochMillis); // NEW
String isoString          = DateTimeHelper.toISOString(epochMillis);

// To epoch milliseconds
long millis = DateTimeHelper.from(instant);
long millis = DateTimeHelper.from(localDateTime);
long millis = DateTimeHelper.from(zonedDateTime);
long millis = DateTimeHelper.from(offsetDateTime);  // NEW
long millis = DateTimeHelper.from(date);            // NEW (java.util.Date)
long millis = DateTimeHelper.fromISOString("2025-01-22T10:30:00Z");

// Current time
long now                  = DateTimeHelper.currentTimeMillis();
LocalDateTime nowLocal    = DateTimeHelper.now();

// Zone info
ZoneId zone               = DateTimeHelper.getDefaultZone(); // NEW
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

All API responses use a consistent structure through `ResponseApi`.

#### `ResponseApi.ok(data)` — Success with status and metadata

Returns a response with `status.code = "success"` and auto-populated `metaData` (sourced from the active request context and OpenTelemetry span):

```json
{
  "status": {
    "code": "success",
    "message": null,
    "errors": null
  },
  "data": {
    "id": 123,
    "name": "John Doe"
  },
  "metaData": {
    "requestId": "abc-123",
    "traceId": "xyz-789",
    "signature": null,
    "timestamp": 1704067200000
  }
}
```

#### `ResponseApi.ok(data, metaData)` — Success with custom metadata

Returns a response where `status` is `null` and `metaData` is the value you provide:

```json
{
  "status": null,
  "data": { ... },
  "metaData": { "requestId": "abc-123", ... }
}
```

#### `ResponseApi.noContent()` — Success with no data

Returns a response with `status.code = "success"`, `data = null`, and auto-populated `metaData`:

```json
{
  "status": { "code": "success", "message": null, "errors": null },
  "data": null,
  "metaData": { ... }
}
```

#### Error Response

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

#### Available Factory Methods

```java
// Success response with auto-populated status ("success") and metadata
ResponseApi.ok(data);

// Success response with custom metadata; status is null
ResponseApi.ok(data, metaData);

// Success response with no data; status is "success" and metadata is auto-populated
ResponseApi.noContent();

// Full control over all fields
ResponseApi.of(status, data, metaData);
```

#### Creating Responses

```java
@RestController
public class UserController {

    @GetMapping("/users/{id}")
    public ResponseApi<User> getUser(@PathVariable Long id) {
        User user = userService.findById(id);
        return ResponseApi.ok(user);
    }

    @PostMapping("/users")
    public ResponseApi<User> createUser(@RequestBody @Valid CreateUserRequest request) {
        if (userService.existsByEmail(request.email())) {
            throw new ConflictDataException("user.email.exists", "Email already registered");
        }
        User user = userService.create(request);
        return ResponseApi.ok(user);
    }

    @DeleteMapping("/users/{id}")
    public ResponseApi<?> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseApi.noContent();
    }
}
```

#### ResponseMetadata

`ResponseMetadata` is populated automatically by `fromRequestId()`:

- `requestId` — from `RequestContextHolder.get().requestId()` (if context is bound)
- `traceId` — from the active **OpenTelemetry** span (`Span.current().getSpanContext().getTraceId()`); `null` if no valid span
- `signature` — optional, pass via `fromRequestId(String signature)`
- `timestamp` — current epoch milliseconds

```java
// Without signature
ResponseMetadata meta = ResponseMetadata.fromRequestId();

// With custom signature
ResponseMetadata meta = ResponseMetadata.fromRequestId("my-signature");
```

### Pagination Model

The library provides a generic `PaginatedData<T>` class for handling paginated data responses.

#### Structure

```java
public class PaginatedData<T> {
    Collection<T> items;      // The collection of items in the current page
    long totalItems;          // Total number of items across all pages
    int totalPages;           // Total number of pages
}
```

#### Creating Paginated Responses

```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping
    public ResponseApi<PaginatedData<User>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<User> users = userService.findAll(page, size);
        long totalItems = userService.countAll();
        int totalPages = (int) Math.ceil((double) totalItems / size);

        PaginatedData<User> pageable = PaginatedData.<User>builder()
            .items(users)
            .totalItems(totalItems)
            .totalPages(totalPages)
            .build();

        return ResponseApi.ok(pageable);
    }
}
```

#### Empty PaginatedData

```java
@GetMapping("/users/search")
public ResponseApi<PaginatedData<User>> searchUsers(@RequestParam String query) {
    List<User> users = userService.search(query);

    if (users.isEmpty()) {
        return ResponseApi.ok(PaginatedData.empty());
    }

    // Build pageable with results...
}
```

#### Response Format

```json
{
  "status": { "code": "success", "message": null, "errors": null },
  "data": {
    "items": [
      { "id": 1, "name": "John Doe",   "email": "john@example.com" },
      { "id": 2, "name": "Jane Smith", "email": "jane@example.com" }
    ],
    "totalItems": 42,
    "totalPages": 5
  },
  "metaData": {
    "requestId": "abc-123",
    "traceId": "xyz-789",
    "signature": null,
    "timestamp": 1704067200000
  }
}
```

For empty results:

```json
{
  "status": null,
  "data": {
    "items": [],
    "totalItems": 0,
    "totalPages": 0
  },
  "metaData": null
}
```

#### Service Layer Example

```java
@Service
public class UserService {

    private final UserRepository userRepository;

    public PaginatedData<User> findAllPaginated(int page, int size) {
        // Validate parameters
        if (page < 0 || size <= 0) {
            throw new BadRequestException("invalid.pagination",
                "Page must be >= 0 and size must be > 0");
        }

        // Fetch data using Spring Data JPA or other ORM
        Page<User> userPage = userRepository.findAll(
            PageRequest.of(page, size, Sort.by("createdAt").descending())
        );

        // Convert to library's PaginatedData
        return PaginatedData.<User>builder()
            .items(userPage.getContent())
            .totalItems(userPage.getTotalElements())
            .totalPages(userPage.getTotalPages())
            .build();
    }
}
```

#### Controller with Pagination and Permissions

```java
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseApi<PaginatedData<Order>> listOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status) {

        PaginatedData<Order> orders = orderService.findAllPaginated(page, size, status);
        return ResponseApi.ok(orders);
    }
}
```

#### Using with Different Collection Types

The `PaginatedData<T>` class accepts any `Collection<T>`, so you can use `List`, `Set`, or other collection types:

```java
// With List
PaginatedData<User> pageableList = PaginatedData.<User>builder()
    .items(List.of(user1, user2))
    .totalItems(2)
    .totalPages(1)
    .build();

// With Set
PaginatedData<String> pageableSet = PaginatedData.<String>builder()
    .items(Set.of("tag1", "tag2", "tag3"))
    .totalItems(3)
    .totalPages(1)
    .build();
```

## Configuration

### Application Properties

```yaml
# Snowflake configuration
snowflake:
  machine-id: 1 # Required for distributed deployments (0-1023)

# Request Context auto-configuration toggle (default: true)
common:
  context:
    enabled: true

# JVM timezone (for DateTimeHelper)
# Set via: -Duser.timezone=America/New_York
```

### Spring Boot Auto-Configuration

The library registers the following auto-configurations:

- `SnowflakeAutoConfiguration` — Automatically creates a `Snowflake` bean (skipped if a `Snowflake` bean already exists)
- `ContextAutoConfiguration` — Automatically registers a `ContextFilter` for Request Context management (servlet applications only)

To see the auto-configuration file location:

```
src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
```

### Disabling Auto-Configurations

#### Using `application.yml` (property toggle — preferred for `ContextAutoConfiguration`)

```yaml
common:
  context:
    enabled: false  # Disable ContextAutoConfiguration
```

#### Using `application.yml` (Spring exclusion)

```yaml
spring:
  autoconfigure:
    exclude:
      - com.intern.hub.library.common.autoconfig.snowflake.SnowflakeAutoConfiguration
      - com.intern.hub.library.common.autoconfig.context.ContextAutoConfiguration
```

#### Using `@SpringBootApplication` Annotation

```java
@SpringBootApplication(exclude = {
    SnowflakeAutoConfiguration.class,
    ContextAutoConfiguration.class
})
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

#### Available Auto-Configurations

| Auto-Configuration           | Description                                                   | When to Disable                                                                              |
| ---------------------------- | ------------------------------------------------------------- | -------------------------------------------------------------------------------------------- |
| `SnowflakeAutoConfiguration` | Creates a `Snowflake` bean for ID generation                  | When you want to provide a custom `Snowflake` bean or use a different ID generation strategy |
| `ContextAutoConfiguration`   | Registers `ContextFilter` for automatic Request Context setup | When you have a custom context filter or don't need Request Context tracking                 |

> **Note**: The `ContextAutoConfiguration` only activates for servlet-based web applications (`@ConditionalOnWebApplication(type = SERVLET)`) and when `common.context.enabled` is not set to `false`.

### Using Request Context

The library automatically sets up Request Context through the `ContextAutoConfiguration`. The context is populated from the `X-Request-ID` HTTP header and made available throughout the request lifecycle via `ScopedValue`.

#### HTTP Headers

The `ContextFilter` reads the following header from incoming requests:

| Header         | Description                                                 | Default Value       |
| -------------- | ----------------------------------------------------------- | ------------------- |
| `X-Request-ID` | Unique identifier for this specific request                 | Auto-generated UUID |

> **Note**: `X-Trace-ID` and `X-Source` headers are **no longer read** by the filter. Distributed tracing is now handled by OpenTelemetry instrumentation.

#### Getting Request Context

```java
import com.intern.hub.library.common.context.RequestContext;
import com.intern.hub.library.common.context.RequestContextHolder;

@Service
public class MyService {

    public void doSomething() {
        RequestContext context = RequestContextHolder.get();

        String requestId = context.requestId();  // Unique request ID

        // Custom per-request attributes
        context.putAttribute("userId", 42L);
        Long userId = (Long) context.getAttribute("userId");
    }
}
```

#### RequestContext Fields

| Field               | Type                    | Description                                      |
| ------------------- | ----------------------- | ------------------------------------------------ |
| `requestId`         | `String`                | Unique identifier for this specific request      |
| `requestAttributes` | `Map<String, Object>`   | Generic per-request attribute store              |

#### Usage in Controllers

```java
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @GetMapping("/{id}")
    public ResponseApi<Order> getOrder(@PathVariable Long id) {
        RequestContext ctx = RequestContextHolder.get();
        log.info("Processing request {}", ctx.requestId());

        Order order = orderService.findById(id);
        return ResponseApi.ok(order);
    }
}
```

#### Usage for Logging

```java
@Service
@Slf4j
public class PaymentService {

    public void processPayment(PaymentRequest request) {
        RequestContext ctx = RequestContextHolder.get();

        log.info("[RequestId: {}] Processing payment for amount: {}",
            ctx.requestId(),
            request.amount());

        // Your payment logic here...
    }
}
```

#### Important Notes

1. **Scope Limitation**: Request Context is only available within the scope of an HTTP request. Accessing it outside of a request (e.g., in scheduled tasks, async threads without proper context propagation) will throw `IllegalStateException`.

2. **Virtual Thread Compatible**: The library uses Java's `ScopedValue` which is optimized for virtual threads, making it more efficient than `ThreadLocal` in high-concurrency scenarios.

3. **Check Binding**: If you're unsure whether a context is bound, you can check:

```java
if (RequestContextHolder.REQUEST_CONTEXT.isBound()) {
    RequestContext ctx = RequestContextHolder.get();
    // Use context...
}
```

## Dependency Management Best Practices

### Avoiding Version Mismatches

When building applications that use this library alongside Spring Boot, it's important to avoid hardcoding dependency versions.

### Using Spring Boot BOM (Bill of Materials)

```kotlin
plugins {
    id("io.spring.dependency-management") version "1.1.7"
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:4.0.2")
    }
}

dependencies {
    // No version needed - managed by BOM
    implementation("org.springframework:spring-web")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("jakarta.servlet:jakarta.servlet-api")
}
```

### Benefits of BOM Usage

| Benefit                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| **Version Consistency**    | All Spring dependencies use compatible versions |
| **Simplified Maintenance** | Update one version to upgrade all dependencies  |
| **Reduced Conflicts**      | Prevents mixing incompatible library versions   |
| **IDE Support**            | Better auto-completion and type checking        |

> [!TIP]
> Run `./gradlew dependencies` to view the dependency tree and verify that all versions are consistent.

## Usage Examples

### Complete Filter Setup (Manual)

If you disable `ContextAutoConfiguration` and need a custom filter:

```java
@Component
public class CustomContextFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {
        String requestId = request.getHeader("X-Request-ID");
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }

        RequestContext requestContext = new RequestContext(requestId);

        ScopedValue.where(RequestContextHolder.REQUEST_CONTEXT, requestContext).run(() -> {
            try {
                filterChain.doFilter(request, response);
            } catch (IOException | ServletException e) {
                throw new RuntimeException(e);
            }
        });
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

    public Order createOrder(Long userId, CreateOrderRequest request) {
        Order order = new Order();
        order.setId(snowflake.next());
        order.setUserId(userId);
        order.setCreatedAt(DateTimeHelper.currentTimeMillis());

        return orderRepository.save(order);
    }

    public Order getOrder(Long orderId) {
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
    public ResponseApi<Order> createOrder(@RequestBody @Valid CreateOrderRequest request) {
        Long userId = 1L; // Get from authentication
        Order order = orderService.createOrder(userId, request);
        return ResponseApi.ok(order);
    }

    @GetMapping("/{id}")
    public ResponseApi<Order> getOrder(@PathVariable Long id) {
        Order order = orderService.getOrder(id);
        return ResponseApi.ok(order);
    }

    @DeleteMapping("/{id}")
    public ResponseApi<?> deleteOrder(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseApi.noContent();
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
