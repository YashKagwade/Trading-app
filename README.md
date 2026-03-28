A full-stack cryptocurrency trading platform with real-time market data, secure authentication, wallet system, payment integration, and admin dashboard.

---

## 🚀 Features

### 👤 User Features
- 🔐 JWT Authentication (Login / Signup)
- 🔑 Two-Factor Authentication (OTP via Email)
- 💰 Wallet System (Deposit, Transfer, Withdraw)
- 📈 Buy & Sell Crypto Assets
- 💼 Portfolio Tracking (Live Profit/Loss)
- ⭐ Watchlist
- 📊 Order History

---

### 👨‍💼 Admin Features
- 👑 Admin Dashboard
- 💸 Approve / Reject Withdrawals
- 👥 Manage Users (View/Delete)
- 📊 Monitor Transactions

---

## 🔐 Security Features

- JWT-based authentication
- Role-Based Access Control (ADMIN / USER)
- OTP-based 2FA verification
- BCrypt password encryption
- Secure payment verification
- CORS configuration

---

## 🛠️ Tech Stack

### Frontend
- React (Vite)
- Bootstrap / Tailwind
- Axios

### Backend
- Spring Boot
- Spring Security
- JWT

### Database
- MySQL

### APIs
- Coin Market API
- Paytm Payment Gateway

---

## ⚙️ Setup Instructions
Add in application.properties:
paytm.mid=YOUR_MID
paytm.mkey=YOUR_MERCHANT_KEY
paytm.website=WEBSTAGING
paytm.industry.type=Retail
paytm.channel.id=WEB

DATABASE CONFIG
spring.datasource.url=jdbc:mysql://localhost:3306/db_fingrow
spring.datasource.username=root
spring.datasource.password=your_password

### 🔹 1. Clone Repository

```bash
git clone https://github.com/YOUR_USERNAME/trading-app.git
cd trading-app
