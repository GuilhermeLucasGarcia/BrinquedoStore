# BrinquedoStore

A simple e-commerce application for toys, built entirely with **Java Spring Boot**.

## Features

- **Backend & Frontend**: Spring Boot MVC (Model-View-Controller) with Thymeleaf templates.
- **Database**: H2 (In-memory) for easy setup.
- **Styling**: Bootstrap 5 + Custom CSS.
- **Endpoints**:
  - `/` - Home page (Product Catalog)
  - `/detalhes/{id}` - Product Details
  - `/h2-console` - H2 Database Console (User: `sa`, Password: ``, JDBC URL: `jdbc:h2:mem:brinquedostore`)

## How to Run

1. Open a terminal in the project root.
2. Navigate to the `backend` directory:
   ```bash
   cd backend
   ```
3. Run the application using Maven Wrapper:
   ```bash
   ./mvnw spring-boot:run
   ```
4. Access the application at: `http://localhost:8080`

## Project Structure

- `src/main/java/com/brinquedostore/api`:
  - `controller`: Web controllers (`HomeController`) and REST controllers (`BrinquedoController`).
  - `model`: JPA Entities (`Brinquedo`).
  - `repository`: JPA Repositories (`BrinquedoRepository`).
  - `service`: Business logic (`BrinquedoService`).
- `src/main/resources`:
  - `templates`: Thymeleaf HTML templates (`index.html`, `detalhes.html`).
  - `static`: Static assets (`css/style.css`, images).
  - `application.properties`: Configuration.
