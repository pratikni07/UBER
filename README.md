# 🚗 RideShare - An Uber Clone Application

A full-stack ride-sharing application built with Spring Boot, MongoDB, and Flutter, implementing real-time location tracking, route optimization, and secure payment processing.

## ✨ Features

- 🗺️ Real-time location tracking and mapping
- 🚦 Intelligent route optimization using OSRM and Dijkstra's algorithm
- 📧 Email notifications using JavaMailer
- 📱 Cross-platform mobile application
- 🚗 Driver-rider matching system
- ⭐ Rating and review system


## 🛠️ Tech Stack

### Backend
- Spring Boot 3.x
- MongoDB
- Spring Security
- JavaMailer
- OSRM API
- WebSocket for real-time communication

### Frontend
- Flutter 3.x
- Provider for state management
- Socket.IO client
- SharedPreferences for local storage

## 🚀 Getting Started

### Prerequisites
- JDK 17 or higher
- MongoDB 5.0 or higher
- Flutter SDK
- OSRM server setup

## 📱 Application Flow

1. **User Registration/Login**
   - Email verification
   - Profile setup
   - Role selection (Driver/Rider)

2. **Booking Flow**
   - Source and destination selection
   - Route preview with estimated time and fare
   - Driver matching
   - Real-time tracking
   - Payment processing

3. **Driver Flow**
   - Status management (Online/Offline)
   - Ride requests acceptance
   - Navigation assistance
   - Earnings tracking

## 🔒 Security Features

- JWT based authentication
- Password encryption
- Role-based access control
- Secure payment gateway integration
- API rate limiting
- Request validation

---
⭐ Don't forget to star this repository if you found it helpful!
