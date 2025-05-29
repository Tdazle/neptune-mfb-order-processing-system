# Neptune MFB Order Processing System – Frontend

This is the frontend application for the Neptune MFB Order Processing System, built with [Angular CLI](https://github.com/angular/angular-cli) version 20.0.0.  
It provides a user interface for managing orders, products, and related operations.

---

## Table of Contents

- [Getting Started](#getting-started)
- [Development Server](#development-server)
- [Code Scaffolding](#code-scaffolding)
- [Building](#building)
- [Running Unit Tests](#running-unit-tests)
- [Project Structure](#project-structure)
- [Contributing](#contributing)
- [License](#license)

---

## Getting Started

### Prerequisites

- [Node.js](https://nodejs.org/) (v18 or above recommended)
- [npm](https://www.npmjs.com/) (comes with Node.js)
- [Angular CLI](https://angular.io/cli) (v20.0.0)

### Installation

Clone the repository and install dependencies:

```bash
  git clone https://github.com/Tdazle/neptune-mfb-order-processing-system.git
```

```bash
  cd neptune-mfb-order-processing-system/order-frontend
```

```bash
  npm install
```

### Running the Application
To start a local development server, run:

```bash
  ng serve
```

Navigate to http://localhost:4200/ in your browser.
The application will automatically reload if you modify any of the source files.

The build artifacts will be stored in the dist/ directory.
By default, the production build optimizes your application for performance and speed.

Running Unit Tests
To execute unit tests with the Karma test runner, use:

```bash
  ng test
```
### Project Structure

```
order-frontend/
├── src/
│   ├── app/                            # Angular application source code
│   │   ├── app.config.ts               # Application configuration
│   │   ├── app.html                    # HTML template for the application
│   │   ├── app.routes.ts               # Application routes
│   │   ├── app.ts                      # Application entry point
│   │   ├── models/                     # Data models
│   │   │   ├── order.model.ts          # Order model
│   │   │   ├── product.model.ts        # Product model
│   │   ├── order-form/                 # Order form components
│   │   │   ├── order-form.css          # CSS styles
│   │   │   ├── order-form.html         # HTML template
│   │   │   ├── order-form.spec.ts      # Unit tests
│   │   │   ├── order-form.ts           # TypeScript code
│   │   ├── order-list/                 # Order list components
│   │   │   ├── order-list.css          # CSS styles
│   │   │   ├── order-list.html         # HTML template
│   │   │   ├── order-list.spec.ts      # Unit tests
│   │   │   ├── order-list.ts           # TypeScript code
│   │   ├── services/                   # Services
│   │   │   ├── order.service.spec.ts   # Unit tests
│   │   │   ├── order.service.ts        # TypeScript code
│   │   │   ├── product.service.spec.ts # Unit tests
│   │   │   └── product.service.ts
│   ├── index.html                      # Main HTML file
│   ├── main.ts                         # Main application entry point
│   └── styles.css                      # Global CSS styles
├── tsconfig.app.json                   # TypeScript configuration for the application
├── tsconfig.json                       # TypeScript configuration for the project
├── tsconfig.spec.json                  # TypeScript configuration for unit tests
├── angular.json                        # Angular CLI configuration
├── package.json                        # NPM dependencies and scripts
└── README.md                           # Project documentation
```
### Contributing
Contributions are welcome! Please fork the repository and submit a pull request.
For major changes, please open an issue first to discuss what you would like to change.

### License
This project is for demo purposes.
