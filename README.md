# 💈 TuTurno – Backend del Sistema de Gestión de Turnos para Peluquerías y Barberías

Este repositorio contiene el **backend** de **TuTurno**, 
El proyecto ofrece una API REST desarrollada con **Spring Boot**, conectada a una base de datos **PostgreSQL**, y con **autenticación mediante JWT**.

---

## 🧾 Descripción general

**TuTurno** Sistema de reservas online donde dueños de locales gestionan horarios, servicios y empleados, mientras que los clientes pueden explorar comercios disponibles, filtrarlos por ubicación o rubro y reservar turnos según servicio, empleado y horario. Incluye autenticación, administración de turnos y visualización de historial.  

Está construido siguiendo buenas prácticas de desarrollo , con arquitectura en capas (controladores, servicios, repositorios y entidades) y manejo de seguridad mediante **Spring Security**.

---

## ⚙️ Tecnologías utilizadas

- ☕ **Java 21**
- 🌱 **Spring Boot 3.5.5**
- 🔐 **Spring Security**
- 💾 **Spring Data JPA / Hibernate**
- 🧩 **PostgreSQL**
- 🧰 **Maven**
- 🔑 **JWT (io.jsonwebtoken)**
- 🧮 **Spring Validation**
- ⚙️ **Spring DevTools** (para recarga automática en desarrollo)

---
## 🧩 Principales funcionalidades

- 👤 **Gestión de usuarios y roles**  
  Administración de clientes, empleados y administradores con diferentes permisos de acceso.

- ✂️ **Registro de servicios**  
  Creación, modificación y eliminación de servicios ofrecidos (cortes, coloraciones, etc.).

- 🕐 **Gestión de turnos**  
  Sistema de reservas que permite asignar turnos, verificar disponibilidad y cancelar citas.

- 🔐 **Autenticación y autorización con JWT**  
  Implementación de seguridad con **Spring Security** y **JSON Web Tokens**, asegurando que solo los usuarios autenticados accedan a los endpoints protegidos.

- 👑 **Dueño como Prestador de Servicios**
  Los dueños pueden gestionar sus propios turnos y atender clientes directamente, utilizando la infraestructura de horarios del local.

- 🚀 **Onboarding Inteligente de Empleados**
  Al registrar un nuevo empleado, este hereda automáticamente los horarios generales de atención del local, facilitando la configuración inicial.

- 🧭 **Arquitectura REST**  
  Endpoints organizados de forma clara y estándar para facilitar la comunicación con el frontend.

- 🧱 **Integración con frontend en JavaScript vanilla**  
  API conectada a una interfaz desarrollada en JavaScript puro que consume los servicios del backend.

- 🧠 **Validación de datos**  
  Uso de anotaciones como `@Valid`, `@NotNull`, `@Email`, etc., para garantizar la integridad de los datos antes de persistirlos.

  ---
## 📁 Estructura del proyecto

```text
sistema-turnos-back/
│
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── gaston/
│   │               └── sistema/
│   │                   └── turno/
│   │                       └── sistematurnos_back/
│   │                           ├── controllers/      # Controladores REST
│   │                           ├── dto/              # Objetos de transferencia de datos
│   │                           ├── entities/         # Entidades del modelo de datos
│   │                           ├── repositories/     # Acceso a la base de datos
│   │                           ├── security/         # Configuración de seguridad
│   │                           ├── services/         # Lógica de negocio
│   │                           └── validation/       # Validaciones personalizadas
│   │
│   └── resources/
│       └── application.properties  # Configuración general
│    
├── pom.xml             # Dependencias Maven
└── README.md           # Documentación
