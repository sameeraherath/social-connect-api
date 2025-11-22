# SocialConnect

**SocialConnect** is a robust backend REST API for a social media platform. Built with **Spring Boot 3** and **Java 17**, it provides essential social networking features including user authentication, dynamic news feeds, post management, and social interactions (likes, comments, and follows).

## 🚀 Features

- **User Management**: Secure registration, login (session-based), and profile management.
- **Dynamic Feed**: A personalized news feed algorithm that aggregates posts from followed users, ordered by recency.
- **Post System**: Create, read, update, and delete text-based posts.
- **Social Graph**: Follow and unfollow mechanism to build user connections.
- **Interactions**:
  - **Likes**: Like and unlike posts with duplicate prevention.
  - **Comments**: Threaded discussions on posts.
- **Media Support**: Profile picture uploads and storage.

## 🛠️ Tech Stack

- **Language**: Java 17
- **Framework**: Spring Boot 3.5.7
- **Database**: H2 In-Memory Database (for development/testing)
- **Security**: Spring Security (BCrypt hashing, Session management)
- **Persistence**: Spring Data JPA (Hibernate)
- **Tools**: Maven, Lombok

## ⚙️ Getting Started

### Prerequisites

- Java Development Kit (JDK) 17 or higher
- Maven (optional, wrapper included)

### Installation & Running

1.  **Clone the repository**

    ```bash
    git clone https://github.com/yourusername/socialconnect.git
    cd socialconnect
    ```

2.  **Run the application**
    Use the included Maven Wrapper to run the app without installing Maven globally:

    - **Windows**:
      ```cmd
      mvnw.cmd spring-boot:run
      ```
    - **Linux/macOS**:
      ```bash
      ./mvnw spring-boot:run
      ```

The API will be available at `http://localhost:8080`.

## 💾 Database Configuration

The project is currently configured to use an **H2 In-Memory Database**. Data is reset every time the application restarts.

- **Console URL**: `http://localhost:8080/h2-console`
- **JDBC URL**: `jdbc:h2:mem:socialconnect`
- **Driver Class**: `org.h2.Driver`
- **Username**: `sa`
- **Password**: _(leave empty)_

## 🔌 API Endpoints

### Authentication (`/api/auth`)

| Method | Endpoint           | Description                                    |
| :----- | :----------------- | :--------------------------------------------- |
| `POST` | `/register`        | Register a new user                            |
| `POST` | `/login`           | Login (Creates Session)                        |
| `POST` | `/logout`          | Invalidate session                             |
| `GET`  | `/me`              | Get current logged-in user details             |
| `PUT`  | `/profile`         | Update profile bio/names                       |
| `POST` | `/profile/picture` | Upload profile picture (`multipart/form-data`) |

### Posts (`/api/posts`)

| Method   | Endpoint         | Description                                 |
| :------- | :--------------- | :------------------------------------------ |
| `POST`   | `/`              | Create a new post                           |
| `GET`    | `/feed`          | **Get personalized feed of followed users** |
| `GET`    | `/`              | Get all posts (Global timeline)             |
| `GET`    | `/{id}`          | Get specific post details                   |
| `GET`    | `/user/{userId}` | Get all posts by a specific user            |
| `PUT`    | `/{id}`          | Update a post                               |
| `DELETE` | `/{id}`          | Delete a post                               |

### Social Graph (`/api/follows`)

| Method   | Endpoint              | Description     |
| :------- | :-------------------- | :-------------- |
| `POST`   | `/{userId}`           | Follow a user   |
| `DELETE` | `/{userId}`           | Unfollow a user |
| `GET`    | `/{userId}/followers` | List followers  |
| `GET`    | `/{userId}/following` | List following  |

### Interactions

| Method   | Endpoint                  | Description              |
| :------- | :------------------------ | :----------------------- |
| `POST`   | `/api/likes/post/{id}`    | Like a post              |
| `DELETE` | `/api/likes/post/{id}`    | Unlike a post            |
| `POST`   | `/api/comments/post/{id}` | Add a comment            |
| `GET`    | `/api/comments/post/{id}` | View comments for a post |

## 📂 Project Structure

```
src/main/java/com/socialconnect
├── config/          # Security and App configurations
├── controller/      # REST Controllers (API Layer)
├── dto/             # Data Transfer Objects (Request/Response)
├── entity/          # JPA Entities (Database Model)
├── exception/       # Global Exception Handling
├── repository/      # Data Access Layer
└── service/         # Business Logic
```

## 🔒 Security Note

This application uses **Session-based Authentication**.

- Public endpoints: `/api/auth/**`, `/h2-console/**`
- Protected endpoints: All other `/api/**` routes.
- When testing with Postman/Insomnia, ensure your client manages cookies automatically to maintain the `JSESSIONID` after logging in.

## 📁 File Storage

Uploaded profile pictures are stored locally in the `uploads/profile-pictures` directory relative to the application root.
