# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

PDI Fundraising is a Spring Boot 3.5.9 web application for managing fundraising campaigns for Poivre des Îles. It's a multi-tenant group sales system that tracks campaigns, groups, sellers, and customer orders with integrated payment processing via Clover API.

**Tech Stack:** Java 21, Spring Boot 3, MySQL, Thymeleaf, Bootstrap 4.6, JPA/Hibernate, MapStruct, Lombok

## Common Commands

### Build & Run
```bash
# Build the application
./mvnw clean package

# Run locally
./mvnw spring-boot:run

# Run tests
./mvnw test

# Run specific test
./mvnw test -Dtest=ClassName#methodName

# Run integration tests
./mvnw verify
```

### Docker
```bash
# Build Docker image (uses fabric8 docker-maven-plugin)
./mvnw docker:build

# Build image (batch script)
docker_build.bat

# Build and push to Docker Hub
docker_build_push.bat

# Run locally with Docker
docker_run.bat

# Run MySQL container locally
docker_run_mysql.bat
```

### Maven Utilities
```bash
# Generate MapStruct implementations
./mvnw clean compile

# Skip tests during build
./mvnw package -DskipTests

# Check dependencies
./mvnw dependency:tree
```

## High-Level Architecture

### Layered Architecture

This application follows a clean layered architecture:

1. **Controller Layer** (`controller/`, `controller/rest/`)
   - MVC controllers serve Thymeleaf templates (MainController, OrderController, SalesController, AdminController)
   - REST controllers provide JSON APIs (CloverPaymentsController, PdiCampaignController, AdminRestController, FileController)

2. **Service Layer** (`service/`, `service/impl/`)
   - Business logic orchestration
   - Key services: CloverPaymentsService, PdiCampaignService, PdiGroupService, MailService, EncryptionService
   - Async operations enabled via @Async for emails and long-running tasks

3. **Repository Layer** (`repository/`)
   - JPA repositories extending JpaRepository/JpaSpecificationExecutor
   - Custom queries using @Query with JPQL
   - Criteria API for dynamic filtering

4. **Domain Model** (`model/`)
   - Core entities: PdiCampaign, PdiGroup, PdiSeller, OrderHeader, OrderItem, PdiProduct, User
   - All inherit from AbstractAuditingEntity for automatic audit tracking (created/modified dates and users)
   - JPA relationships: @ManyToOne, @OneToMany with lazy loading

### Domain Model Hierarchy

The fundraising system has a hierarchical structure:

```
PdiCampaign (top-level fundraising campaign)
  └─ PdiGroup (organizational sub-units within a campaign)
      └─ PdiSeller (individual sales representatives)
          └─ OrderHeader (customer orders)
              └─ OrderItem (individual products in an order)
```

- **Campaigns** aggregate sales across all groups and track profit percentages
- **Groups** contain multiple sellers and have group leaders
- **Sellers** take orders from buyers and can belong to multiple groups
- **Orders** are placed by buyers (customers) and link to sellers
- **Products** (PdiProduct) are bilingual and organized into sections/categories

### Data Import System

The application has two parallel import mechanisms for syncing data from external sources:

1. **FileMaker JDBC Import** (`imports/JdbcImportService`)
   - Direct JDBC connection to FileMaker database via custom JDBC driver (lib/fmjdbc.jar)
   - Imports 7 entity types: Products, Sections, OrderTypes, Campaigns, Groups, Sellers, GroupLinks
   - Uses custom RowMappers to transform JDBC ResultSet to domain entities

2. **CSV Import** (`imports/CsvImportService`)
   - File upload via admin interface for same 7 entity types
   - OpenCSV library for parsing with 10% error tolerance
   - Tracks last import timestamps via ProductLastImport and GroupLastImport entities

Both import systems use the same service layer (PdiCampaignService, PdiGroupService, etc.) to persist data, ensuring consistency.

### Payment Processing Flow

Payment integration with Clover Ecommerce API:

1. Buyer submits order form with encrypted payment token (tokenized card)
2. CloverPaymentsController receives POST to `/api/pay/charge`
3. CloverPaymentsService creates OrderHeader with status PENDING
4. Service calls Clover API (`/v1/charges`) with amount and token
5. On success:
   - Updates OrderHeader status to CONFIRMED
   - Generates unique confirmation number (format: `SW<tab>####`)
   - Sends receipt email to buyer (PROD) or admin (DEV)
   - Stores payment transaction ID and timestamp
6. On failure: marks OrderHeader as ERROR with error message

**Environment Handling:**
- DEV profile uses Clover sandbox API and sends receipts to webmestre@poivredesiles.com
- PROD profile uses production Clover API and sends receipts to actual buyer email
- Credentials injected via Docker secrets: `DEV_PAY_*` or `PROD_PAY_*` environment variables

### Security & Encryption

**Authentication:**
- Spring Security 6 with form-based login
- Role hierarchy: ADMIN > CAMPAIGN_LEADER > GROUP_LEADER > SELLER > BUYER
- BCrypt password encoding
- Admin credentials initialized from application.properties (adminUsernames, adminPasswords)

**Authorization:**
- Method-level security with @Secured annotations
- URL-based rules in SecurityConfig
- CSRF protection (disabled only for payment callbacks)
- Maintenance mode filter toggles application access via `application.mode.maintenance` property

**At-Rest Encryption:**
- Sensitive PII fields encrypted using JPA AttributeConverters (StringCryptoConverter)
- Encrypted fields: User names, Seller names/phones, OrderHeader buyer info
- Cipher: AES/CBC/PKCS5Padding with configurable key
- CipherInitializer manages cipher setup with zero IV
- EncryptionService provides batch migration from plaintext to encrypted (set `application.action.encrypt=true`)
- BusinessNumber entity tracks encryption state

**Keystore Management:**
- Three PKCS12 keystores: dev (keystore-staging.p12), staging, prod (keystore-prod.p12)
- Passwords injected via environment variables (KEYSTORE_PASSWORD, PROD_KEYSTORE_PASSWORD)
- Used for SSL/TLS in production deployments

### Configuration & Profiles

The application uses Spring profiles for environment-specific configuration:

**Profiles:**
- **default** (application.properties): Local development with MySQL on port 3309
- **dev** (application-dev.properties): Staging environment on port 8081
- **prod** (application-prod.properties): Production environment on port 8083
- **test** (application-test.properties): Test profile with Testcontainers

**Key Configuration Points:**
- Database: Environment-specific credentials via Docker secrets (DB_URL, DB_USER, DB_PWD for dev; PROD_* for prod)
- Mail: Gmail SMTP for dev, production SMTP for prod
- Payment: Sandbox vs production Clover API URLs and credentials
- Base URL: Used for email links (e.g., financement.poivredesiles.com)
- FileMaker: JDBC connection string for imports
- Encryption: Toggle encryption state and batch actions

**Profile Activation:**
- Set via `SPRING_PROFILES_ACTIVE` environment variable in Docker Compose (dcs-prod.yml)
- Dev: `JAVA_OPTS="-Xms256m -Xmx768m -Xss512k -XX:MaxMetaspaceSize=128m"` (memory-optimized)
- Prod: `JAVA_OPTS="-Xms1024m -Xmx1024m"` (higher baseline memory)

### Testing Strategy

**Test Infrastructure:**
- Base class: `AbstractContainerBaseTest` starts MySQL 8.0 Testcontainer before tests
- JUnit 5 with Spring Boot Test framework
- Profile: "test" activates test-specific properties

**Test Types:**
- **Unit Tests**: Service layer logic with mocked dependencies (Mockito)
- **Integration Tests**: Full Spring context with Testcontainers (IT suffix)
  - CsvImportServiceIT, JdbcImportServiceIT: Import validation
  - PaymentsServiceTest: Payment flow
  - PdiCampaignServiceTest, PdiGroupServiceTest: Domain operations
- **Controller Tests**: Spring MVC test framework for HTTP endpoints

**Running Tests:**
- All tests: `./mvnw test`
- Integration tests: `./mvnw verify`
- Single test: `./mvnw test -Dtest=ClassName`
- Note: Integration tests require Docker for Testcontainers

### Async & Scheduled Tasks

**Task Execution:**
- Configured in AsyncConfiguration with custom thread pools
- Core pool: 2 threads, Max: 50, Queue: 10,000
- Thread naming: `pdi-task-*` and `pdi-task-scheduling-*`

**Async Operations:**
- Email sending (MailService methods annotated with @Async)
- Long-running imports
- Campaign closure and recap generation

**Scheduled Tasks:**
- Campaign auto-block after 1-year timeout (immediate data deletion)
- Cleanup jobs (configurable via @Scheduled annotations)

### MapStruct DTO Mapping

The application uses MapStruct for automatic entity-to-DTO conversions:

- Mappers: UserMapper, OrderHeaderMapper, PdiCampaignMapper, etc.
- Configuration: `@Mapper(componentModel = "spring")` for Spring bean injection
- Generated implementations in `target/generated-sources/annotations`
- Compilation: MapStruct processors configured in maven-compiler-plugin with Lombok binding

**Important:** Always run `./mvnw clean compile` after modifying mapper interfaces to regenerate implementations.

## Development Notes

### Adding New Entities

When creating new domain entities:

1. Extend `AbstractAuditingEntity` for automatic audit fields
2. Use Lombok annotations (@Data, @EqualsAndHashCode) to reduce boilerplate
3. Add encryption converters for sensitive fields: `@Convert(converter = StringCryptoConverter.class)`
4. Create corresponding JPA repository extending JpaRepository
5. Implement service layer with interface and impl classes
6. Create MapStruct mapper for DTOs if exposing via REST API
7. Add integration test extending AbstractContainerBaseTest

### Working with Encryption

- Encryption is toggled via `application.encrypted` property (true in dev/prod)
- To batch-encrypt existing plaintext data: set `application.action.encrypt=true` and restart
- EncryptionService reads BusinessNumber to track encryption state
- Test encryption with CipherInitializerTest and StringCryptoConverterTest

### Payment Integration

- Clover API credentials are environment-specific (DEV_PAY_* vs PROD_PAY_*)
- Test with sandbox credentials in dev profile
- Payment callback endpoint (`/api/pay/callback`) has CSRF disabled
- Always test payment flow end-to-end in integration tests before deploying

### Database Migrations

- JPA DDL: `spring.jpa.hibernate.ddl-auto=update` (auto-generates schema changes)
- Manual SQL scripts in `sql/` directory for complex migrations
- **Warning:** Production uses ddl-auto=update, so test schema changes carefully in dev first

### FileMaker Integration

- FileMaker JDBC driver in `lib/fmjdbc.jar` (not in Maven repos)
- Connection string format: `jdbc:filemaker://<host>/<database>`
- Import service reads from tables: Produit, Section, TypeBC, Campagne, Groupe, Vendeur, LienGroupe
- Ensure FileMaker database is running and accessible before import operations

### Docker Deployment

- Image built with fabric8 docker-maven-plugin: `./mvnw docker:build`
- Multi-assembly: JAR + keystores copied to image
- Base image: eclipse-temurin:21.0.7_6-jre-jammy (JRE 21)
- Docker Compose: `dcs-prod.yml` defines two services (DEV-pdi-fundraising, PROD-pdi-fundraising)
- Secrets managed externally via Docker secrets (not in Git)

### Environment Variables

Key environment variables required for deployment:

**Dev Profile:**
- DB_URL, DB_USER, DB_PWD (database)
- EMAIL_PASSWORD (SMTP auth)
- DEV_PAY_MERCHANT_ID, DEV_PAY_TOKEN, DEV_PAY_PRIVATE_TOKEN (Clover sandbox)
- KEYSTORE_PASSWORD, KEYSTORE_FILENAME (SSL/TLS)

**Prod Profile:**
- PROD_DB_URL, PROD_DB_USER, PROD_DB_PWD (database)
- EMAIL_PASSWORD (SMTP auth)
- PROD_PAY_MERCHANT_ID, PROD_PAY_TOKEN, PROD_PAY_PRIVATE_TOKEN (Clover production)
- PROD_KEYSTORE_PASSWORD, PROD_KEYSTORE_FILENAME (SSL/TLS)

**Common:**
- SPRING_PROFILES_ACTIVE (dev or prod)
- JAVA_OPTS (JVM heap and memory settings)
- APPLICATION_ENCRYPTED, APPLICATION_MODE_MAINTENANCE, APPLICATION_ACTION_ENCRYPT (feature toggles)
- LOGGING_FILE_PATH (log output directory)

## Important Patterns

### Campaign Lifecycle

1. **Creation**: Import via CSV or FileMaker JDBC
2. **Active**: Groups and sellers added, orders taken
3. **Closed**: Campaign leader requests close
   - All users except campaign leader disabled
   - Recap email generated with sales aggregation
   - Campaign marked as closed with timestamp
4. **Blocked**: Auto-triggered after 1-year timeout
   - Immediate data deletion
   - Permanent state (no reversal)

### Order Status Flow

- **PENDING**: Order created, payment in progress
- **CONFIRMED**: Payment successful, confirmation number generated, receipt sent
- **CANCELLED**: Buyer or admin cancelled (not implemented in current version)
- **ERROR**: Payment failed or processing error, error message stored

### Profit Calculation

- Profit per campaign = Total Sales × Profit Percentage
- Sales aggregated bottom-up: OrderItem → OrderHeader → PdiSeller → PdiGroup → PdiCampaign
- BigDecimal used throughout for precise monetary calculations

### Bilingual Content

- Products have French and English names/descriptions
- Message bundles: messages_fr.properties for French localization
- Thymeleaf templates use `#{key}` syntax for i18n

## Version Information

- Current version: **6.4.2**
- Recent changes:
  - 6.4.2: Increased memory allocation
  - 6.4.1: Adjusted payment variables for dev vs prod
  - 6.4.0: Environment variable naming standardization (PROD_ vs DEV_ prefixes)
