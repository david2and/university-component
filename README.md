# Componente de integracion Moodle

Este proyecto es una aplicación que permite la integracion automatizada contra el API de Moodle. Esta aplicacion esta realizada con Java y Spring Boot. A continuación, se detallan los pasos para configurar el entorno, compilar y ejecutar la aplicación.

## Prerrequisitos

- Java Development Kit (JDK) 17 o superior
- Apache Maven 3.6.3 o superior
- Un IDE como IntelliJ IDEA (opcional)

## Configuración del entorno y ejecucion

1. **Clonar el Repositorio**

   Clona este repositorio en tu máquina local y selecciona el branch "feature/UNCP" utilizando los siguientes comandos:

   ```sh
   git clone https://github.com/david2and/university-component.git
   git checkout feature/UNCP

2. **Configuracion de Variables**

   En el archivo de configuracion application.yml configurar las siguientes variables segun corresponda:

    - rest-config.moodleUrl: URL del API de moodle
    - rest-config.universityUrl: URL del API de la universidad
    - university-endpoints.syncGrades: Url de la universidad para hacer push de la informacion
    - spring.datasource.url: Url de la Base de datos
    - spring.datasource.username: Username de la base de datos
    - spring.datasource.password: Password de la base de datos
3. Construir el proyecto con el siguiente comando:
   ```sh
   mvn clean package
4. Ejecutar el ejecutable .jar
   ```sh
   java -jar target/Component-0.0.1-SNAPSHOT.jar
