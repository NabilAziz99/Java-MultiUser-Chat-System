# Java Multi-User Chat System

A multi-threaded chat server built in Java, providing real-time user-to-user and group messaging capabilities. The system employs Java Sockets for network communications and leverages multi-threading to handle concurrent users efficiently.

## 🌟 Features

- **Real-time Messaging**: Enable users to send and receive messages in real-time.
- **User-to-user Messaging**: Allow private messaging between individual users.
- **Group Messaging**: Users can join group chats and communicate with multiple users simultaneously.
- **Concurrency**: Efficiently handles multiple users at the same time using multi-threading.
- **Socket-based Communication**: Uses Java Sockets for the underlying network communications.

## 🔧 Prerequisites

- Java Development Kit (JDK) version 8 or newer.
- Basic knowledge of Java programming and networking.

## 🚀 Getting Started

1. **Clone the Repository**:
    ```bash
    git clone [repository-url]
    ```

2. **Navigate to the Directory**:
    ```bash
    cd Java-Multi-User-Chat-System
    ```

3. **Compile the Java Files**:
    ```bash
    javac *.java
    ```

4. **Run the Server**:
    ```bash
    java ChatServer
    ```

5. **Run the Client** (on a new terminal):
    ```bash
    java ChatClient
    ```

6. Follow on-screen prompts to connect and start chatting!

## 📜 Usage

- To send a private message: `/pm [username] [message]`
- To join a group chat: `/join [groupname]`
- For more commands: `/help`


