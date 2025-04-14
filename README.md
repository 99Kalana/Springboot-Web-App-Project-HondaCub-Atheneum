*** Change the branch from "main" to ---> "master" for View the Frontend and Backend codes. ***
The Folder "src" contains the Backend, and the Module "Frontend" contains the frontend of HTML and JavaScript.

# Springboot-Web-App-Project-HondaCub-Atheneum
Honda Cub Atheneum Springboot Web Application Project by Kalana Sachinthana


## Project Description

Honda Cub Atheneum is a comprehensive web platform designed for Honda Super Cub enthusiasts worldwide. It serves as both an e-commerce marketplace for a wide range of spare parts, catering to models from 1958 to 2025, and a vibrant knowledge hub. The platform aims to streamline the purchasing process for parts while fostering a strong community through features like historical archives, restoration guides, and user forums. Key functionalities include advanced part searching, a multi-vendor marketplace, order tracking, interactive model history, restoration resources, discussion forums, user blogs, a loyalty program, and secure authentication with QR code login.

## Setup Instructions

This guide will walk you through the steps to install, configure, and run both the frontend and backend applications of the Honda Cub Atheneum project.

### Prerequisites

Before you begin, ensure you have the following installed on your system:

**Backend:**

* **Java Development Kit (JDK):** Make sure you have a compatible JDK installed (ideally Java 11 or later, as typically used with Spring Boot). You can download it from [Oracle](https://www.oracle.com/java/technologies/javase-downloads.html) or [OpenJDK](https://openjdk.java.net/).
* **Maven:** Spring Boot projects often use Maven for dependency management and building. Install Maven from [Apache Maven](https://maven.apache.org/download.cgi).
* **MySQL:** You will need a running MySQL database server. You can download and install it from [MySQL Community Downloads](https://dev.mysql.com/downloads/community/).

**Frontend:**

* **Web Browser:** A modern web browser (Chrome, Firefox, Safari, Edge) to view the frontend application.

### Backend Setup

1.  **Navigate to the Backend Directory:**
    Open your terminal or command prompt and navigate to the `backend/` folder within your project repository.

    ```bash
    cd backend
    ```

2.  **Configure MySQL Database:**
    * Create a new database for the project (e.g., `hondacubatheneum`).
    * Update the database connection properties in the `src/main/resources/application.properties` or `application.yml` file. You will need to provide the following details:
        ```properties
        spring.datasource.url=jdbc:mysql://localhost:3306/hondacubatheneum?useSSL=false&serverTimezone=UTC
        spring.datasource.username=your_mysql_username
        spring.datasource.password=your_mysql_password
        spring.jpa.hibernate.ddl-auto=update
        ```
        Replace `your_mysql_username` and `your_mysql_password` with your MySQL credentials. `spring.jpa.hibernate.ddl-auto=update` will automatically create or update the database schema based on your JPA entities. For production, consider using a more controlled approach for schema management.

3.  **Build the Backend Application:**
    Use Maven to build the project. Run the following command in the `backend/` directory:

    ```bash
    mvn clean install
    ```

4.  **Run the Backend Application:**
    After the build is successful, you can run the Spring Boot application using the following Maven command:

    ```bash
    mvn spring-boot:run
    ```
    Alternatively, you can find the generated `.jar` file in the `backend/target/` directory (e.g., `hondacubatheneum-0.0.1-SNAPSHOT.jar`) and run it using:
    ```bash
    java -jar target/hondacubatheneum-0.0.1-SNAPSHOT.jar
    ```

5.  **Backend API URL:**
    The backend API should be accessible at a base URL, typically `http://localhost:8080/api/v1`.

### Frontend Setup

1.  **Navigate to the Frontend Directory:**
    Open a new terminal or command prompt and navigate to the `frontend/` folder within your project repository.

    ```bash
    cd frontend
    ```

2.  **Open `index.html` in your Web Browser:**
    Since your frontend is built with basic HTML, CSS, and JavaScript, you likely don't have a build process or a local development server. You can simply open the `index.html` file located in the `frontend/` directory directly in your web browser.

3.  **Backend API Configuration (if needed):**
    Examine your JavaScript files in the `frontend/` directory (especially any files related to API calls) to see how the backend API URL is configured. Ensure that the URL points to the correct address where your backend application is running (likely `http://localhost:8080/api/v1`). You might need to adjust this URL in your JavaScript code if your backend is running on a different host or port.

    **Note:** If you used any JavaScript frameworks or libraries that require a build process (like React, Angular, Vue.js), the setup steps would be different (involving `npm install` and `npm start` or similar commands). Based on your technology stack description, it seems like a standard HTML/CSS/JS setup. If this is incorrect, please let me know, and I can adjust the instructions.

After following these steps, your backend application should be running, and you should be able to open the `index.html` file in your browser to interact with the frontend. Ensure that your browser's developer console is open to check for any errors related to API calls or JavaScript.
