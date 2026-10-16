# Laboratorio: Acceso a datos y visualización en TableView — Catálogo de productos de la papelería Campus

- **Asignatura:** Programación de Aplicaciones de Escritorio
- **Autor:** José Alessandro Torres Parodi
- **Grupo:** 3
- **Docente:** Ing. Freddy López

---

## 1. Descripción del proyecto
Aplicación de escritorio desarrollada en Java 21 y JavaFX que gestiona el catálogo de productos de la **Papelería Campus** sobre una base de datos PostgreSQL (`papeleria_campus`). Implementa el patrón MVC apoyado en una clase `ProductoDAO` con JDBC puro, consultas parametrizadas mediante `PreparedStatement`, manejo monetario exacto con `BigDecimal`, validaciones obligatorias (`V1`–`V10`), manejo seguro de excepciones (`E1`–`E4`) y visualización en un `TableView` de 6 columnas con contador dinámico.

## 2. Tecnologías y versiones utilizadas
- **Java:** JDK 21
- **JavaFX:** `org.openjfx:javafx-controls:21.0.6` y `org.openjfx:javafx-fxml:21.0.6`
- **Construcción y dependencias:** Apache Maven
- **Base de datos:** PostgreSQL 18 y pgAdmin 4
- **Driver JDBC:** `org.postgresql:postgresql:42.7.4`
- **IDE:** IntelliJ IDEA
- **Control de versiones:** Git y GitHub

## 3. Requisitos previos
1. Tener instalado **Java 21** y configurado como SDK del proyecto en IntelliJ IDEA.
2. Tener en ejecución el servidor **PostgreSQL 18** en `localhost:5432`.
3. Disponer de **pgAdmin 4** para crear la base de datos y ejecutar los scripts SQL.

## 4. Dependencias Maven
Definidas en el archivo `pom.xml`:
- `javafx-controls` (21.0.6)
- `javafx-fxml` (21.0.6)
- `postgresql` (42.7.4)
- `junit-jupiter-api` (5.12.1, ámbito `test`)

## 5. Creación y configuración de PostgreSQL y orden de ejecución de scripts
1. En **pgAdmin 4**, hacer clic derecho sobre **Databases > Create > Database...** y crear la base de datos llamada:
   ```text
   papeleria_campus