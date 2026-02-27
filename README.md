#🃏 Networked 3 Card Poker

CS 342 – Object-Oriented Programming | UIC

A full-stack desktop implementation of the casino game 3 Card Poker, built using Java, JavaFX, and Java Sockets. This project separates the system into a multi-threaded server and a JavaFX client application that communicate over TCP connections.

The application demonstrates object-oriented design, client-server architecture, and real-time game state synchronization.

###🏗️ Architecture

This project is structured as a two-module system inside one repository:

cs342-project3-3cardpoker-networked/
├── server/
│     ├── pom.xml
│     └── src/
├── client/
│     ├── pom.xml
│     └── src/
└── README.md

###Server

Handles socket connections

Manages game sessions

Processes bets and game logic

Sends results to clients

Supports multiple clients using threads

###Client

JavaFX graphical interface

Sends player actions (bet, deal, play, fold)

Displays cards and results

Updates bankroll dynamically

###🎮 Game Features

Ante and Pair Plus betting

Dealer qualification rules

Hand evaluation (Straight Flush, Three of a Kind, Straight, Flush, Pair, High Card)

Real-time communication between client and server

Multi-threaded server design

Multiple rounds support

Clean JavaFX UI with FXML and CSS

###🛠️ Technologies Used

Java 17

JavaFX (FXML + CSS)

Java Sockets (TCP)

Maven

JUnit 5

###▶️ How to Run
1️⃣ Start the Server
cd server
mvn clean compile exec:java
2️⃣ Start the Client

Open a new terminal:

cd client
mvn clean compile exec:java

The client will connect to the running server.

###📡 Networking Overview

The client and server communicate using structured messages over TCP sockets.
Each client session is handled in a separate thread to support concurrent gameplay.

###🎯 Learning Objectives

This project demonstrates:

Client-server system design

Multi-threaded programming

Socket communication

GUI development with JavaFX

Separation of concerns (UI vs Business Logic)

Maven project configuration

Unit testing of core game logic

###📚 Author

Roger Chiu
Computer Science – University of Illinois Chicago