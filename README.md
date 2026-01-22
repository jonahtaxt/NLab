# NLab

An **Appointment Management System** for nutritionists and patients. A full-stack application designed to manage patient-nutritionist appointments, including package management, payment processing, body metrics tracking, and payment tracking.

## Table of Contents

- [Project Context](#project-context)
- [Project Structure](#project-structure)
- [Technology Stack](#technology-stack)
- [Features](#features)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [Database Schema](#database-schema)
- [Security](#security)
- [License](#license)

## Project Context

NLab is a real-world, full-stack application built to support appointment
scheduling, patient progression, and historical health data management
for nutritionist workflows.

From an engineering perspective, this project prioritizes:
- clear workflow modeling
- durable, long-lived data
- correctness of state transitions
- maintainability over time

The repository is structured to be approachable for engineers who want to
understand, run, or extend the system.


## Project Structure

The project is organized as a monorepo with three main components:

```
NLab/
├── api/                          # Spring Boot REST API (Java)
├── web/                          # Next.js Frontend (React/TypeScript)
└── DatabaseProjectNlab/          # SQL Server Database Project
```

## Technology Stack

### Frontend
| Technology | Version | Purpose |
|------------|---------|---------|
| Next.js | Latest | React framework |
| React | Latest | UI library |
| TypeScript | 5.7.3 | Type safety |
| Tailwind CSS | 3.4.17 | Styling |
| Shadcn/ui | - | UI components |
| NextAuth | 5.0.0-beta.25 | Authentication |
| React Hook Form | 7.54.2 | Form handling |
| Zod | 3.24.1 | Schema validation |
| Sonner | 2.0.1 | Toast notifications |
| React Day Picker | 8.10.1 | Calendar component |
| date-fns | 4.1.0 | Date utilities |

### Backend
| Technology | Version |
|------------|---------|
| Spring Boot | 3.4.2 |
| Java | 23 |
| Spring Data JPA | - |
| Spring Security | 6.x |
| Keycloak (OAuth2/JWT) | - |

### Database
| Technology | Details |
|------------|---------|
| Microsoft SQL Server | localhost:1433 |
| Database Name | nutrition_db |

## Features

### Patient Management
- Create, read, and update patient profiles
- Active/inactive status tracking
- Email uniqueness validation
- Contact information management
- **Patient detail view** with comprehensive information

### Nutritionist Management
- Professional profile management
- Appointment scheduling capabilities
- Active/inactive status tracking

### Appointment Scheduling
- Schedule appointments with nutritionists
- Status tracking: `SCHEDULED`, `COMPLETED`, `CANCELLED`, `RESCHEDULED`, `NO_SHOW`
- Date/time validation (future dates only)
- Appointment notes
- Date range filtering
- **Patient appointment history view**

### Appointment Notes (Body Metrics)
Detailed health metrics tracking per appointment:
- **Weight & Body Composition**: Weight, total fat, upper/lower fat, visceral fat
- **Body Mass**: Muscle mass, bone mass, metabolic age
- **Skinfold Measurements**: Subscapular, triceps, biceps, iliac crest, suprailiac, abdominal
- **Circumference Measurements**: Mid-arm (relaxed/flexed), umbilical, waist, hip
- Additional notes field

### Package Management
- Package type creation and management
- Appointment count per package
- Pricing and nutritionist rate configuration
- Active/inactive status

### Purchased Packages
- Track patient package purchases
- Remaining appointment count
- Expiration date tracking
- Payment method associations
- **Total paid tracking**

### Patient Payments
- Record payments against purchased packages
- Payment method and card type association
- Payment date tracking
- **Automatic package update after payment**

### Settings (Admin)
- Payment method configuration
- Card payment type management (bank fees, installments)

## Getting Started

### Prerequisites
- Java 23
- Node.js (LTS)
- pnpm
- Microsoft SQL Server
- Keycloak (for authentication)

### Backend Setup

```bash
cd api

# Install dependencies and build
mvn clean install

# Run the application
mvn spring-boot:run
```

The API will start on `http://localhost:8080`

### Frontend Setup

```bash
cd web

# Install dependencies
pnpm install

# Run development server with Turbopack
pnpm dev

# Build for production
pnpm build

# Start production server
pnpm start
```

The frontend will start on `http://localhost:3000`

### Environment Variables

**Frontend (`web/.env`):**
```env
NEXT_PUBLIC_KEYCLOAK_URL=<keycloak-url>
NEXT_PUBLIC_CLIENT_ID=<client-id>
NEXT_PUBLIC_CLIENT_SECRET=<client-secret>
NEXT_PUBLIC_REALM=<realm>
```

**Backend (`api/src/main/resources/application.properties`):**
- SQL Server connection is pre-configured for `localhost:1433`
- OAuth2 configuration for Keycloak integration

## API Documentation

### Base URL
`http://localhost:8080/api`

### Endpoints

#### Patients
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/patients` | List all patients (paginated) |
| GET | `/patients/active` | List active patients |
| GET | `/patients/{id}` | Get patient by ID |
| GET | `/patients/{id}/purchased-packages` | Get patient with packages and payments |
| POST | `/patients` | Create a patient |
| PUT | `/patients/{id}` | Update a patient |

#### Nutritionists
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/nutritionists` | List all nutritionists (paginated) |
| GET | `/nutritionists/active` | List active nutritionists |
| GET | `/nutritionists/{id}` | Get nutritionist by ID |
| POST | `/nutritionists` | Create a nutritionist |
| PUT | `/nutritionists/{id}` | Update a nutritionist |

#### Appointments
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/appointments` | Create an appointment |
| GET | `/appointments/nutritionist/{id}` | Get appointments by nutritionist |
| GET | `/appointments/patient/{id}` | Get appointments by patient |
| PUT | `/appointments/{id}/status` | Update appointment status |
| DELETE | `/appointments/{id}` | Delete an appointment |

#### Appointment Notes
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/appointment-notes` | Create appointment notes (body metrics) |

#### Packages
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/packages` | List all packages (paginated) |
| GET | `/packages/select` | List packages for dropdown selection |
| GET | `/packages/{id}` | Get package by ID |
| POST | `/packages` | Create a package |
| PUT | `/packages/{id}` | Update a package |

#### Purchased Packages
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/purchased-packages` | List all purchased packages |
| GET | `/purchased-packages/{id}` | Get purchased package by ID |
| GET | `/purchased-packages/patient/{id}` | Get packages by patient |
| POST | `/purchased-packages` | Create a purchased package |

#### Patient Payments
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/patient-payment` | Create a patient payment |

#### Payment Configuration
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/payment-methods` | List payment methods |
| GET | `/card-payment-types` | List card payment types |

### Pagination

Paginated endpoints support the following query parameters:
- `page` - Page number (0-indexed)
- `size` - Page size
- `sort` - Sort field and direction (e.g., `lastName,asc`)

Response format:
```json
{
  "content": [...],
  "pageNumber": 0,
  "pageSize": 10,
  "totalElements": 100,
  "totalPages": 10,
  "first": true,
  "last": false
}
```

## Database Schema

### Tables

| Table | Description |
|-------|-------------|
| `Patient` | User profiles (name, email, phone, status) |
| `Nutritionist` | Professional profiles |
| `Appointment` | Links purchases to nutritionists with scheduling |
| `AppointmentNotes` | Body metrics and health measurements per appointment |
| `PackageType` | Service offerings (appointments, pricing) |
| `PurchasedPackage` | Patient purchases with remaining appointments and total paid |
| `PaymentMethod` | Payment options configuration |
| `CardPaymentType` | Card-specific details (fees, installments) |
| `PatientPayment` | Individual payment records for purchased packages |
| `NutritionistPaymentPeriod` | Payment cycles for nutritionists |

### Views

| View | Description |
|------|-------------|
| `vPatientAppointments` | Combined view of appointments with patient, nutritionist, and package details |

## Security

### Authentication
- **JWT-based authentication** via Keycloak
- **OAuth2 Resource Server** configuration
- **Stateless session management**
- **Protected routes** on frontend

### Authorization
Role-based access control with three roles:

| Role | Access Level |
|------|--------------|
| `ADMIN` | Full system access (all features + settings) |
| `NUTRITIONIST` | Manage patients, appointments, and packages |
| `PATIENT` | View own information |

**Navigation Access by Role:**
| Page | ADMIN | NUTRITIONIST | PATIENT |
|------|-------|--------------|---------|
| Dashboard | Yes | Yes | Yes |
| Nutritionists | Yes | No | No |
| Patients | Yes | Yes | No |
| Packages | Yes | Yes | No |
| Settings | Yes | No | No |

### Data Protection
- Input validation using Jakarta Bean Validation
- HTML escaping and sanitization
- Email uniqueness constraints
- CORS configuration for allowed origins

## Frontend Pages

| Route | Description |
|-------|-------------|
| `/` | Landing page |
| `/login` | Authentication page |
| `/dashboard` | Main dashboard |
| `/dashboard/patients` | Patient list with search and pagination |
| `/dashboard/patients/[id]` | Patient detail view with appointments and payments |
| `/dashboard/nutritionists` | Nutritionist management |
| `/dashboard/packages` | Package type management |
| `/dashboard/settings` | Payment methods and card types configuration |

## License

This project is licensed under the **GNU General Public License v3 (GPL-3.0)**.

---

This project is not intended to demonstrate horizontal scaling or
high-availability patterns; those concerns were intentionally out of scope
for the problems being explored here.

