import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { useEffect } from "react"; // ✅ FIX
import API from "./api/axios";     // ✅ FIX

import LoginPage from './pages/LoginPage';
import SignupPage from './pages/SignupPage';
import OtpVerificationPage from './pages/OtpVerificationPage';
import DashboardPage from './pages/DashboardPage';
import WalletPage from './pages/WalletPage';
import WatchlistPage from "./pages/WatchList";
import ProfilePage from "./pages/ProfilePage";
import AssetsPage from "./pages/AssetsPage";
import OrderHistoryPage from "./pages/OrderHistoryPage";
import ForgotPasswordPage from "./pages/ForgotPasswordPage";
import ProtectedRoute from './components/ProtectedRoute';
import AdminDashboard from "./pages/AdminDashboard";

import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.bundle.min.js';

function App() {

  // 🔥 Restore user after reload (PAYMENT FIX)
  useEffect(() => {
    const token = localStorage.getItem("token");
    const user = localStorage.getItem("user");

    if (token && !user) {
      API.get("/api/users/profile")
        .then(res => {
          localStorage.setItem("user", JSON.stringify(res.data));
          console.log("✅ User restored");
        })
        .catch(err => {
          console.error("❌ Failed to restore user", err);
        });
    }
  }, []);

  return (
    <Router>
      <Routes>

        {/* Public Routes */}
        <Route path="/login" element={<LoginPage />} />
        <Route path="/signup" element={<SignupPage />} />
        <Route path="/verify-otp" element={<OtpVerificationPage />} />
        <Route path="/forgot-password" element={<ForgotPasswordPage />} />

        {/* Protected Routes */}
        <Route path="/dashboard" element={
          <ProtectedRoute>
            <DashboardPage />
          </ProtectedRoute>
        } />

        <Route path="/wallet" element={
          <ProtectedRoute>
            <WalletPage />
          </ProtectedRoute>
        } />

        <Route path="/watchlist" element={
          <ProtectedRoute>
            <WatchlistPage />
          </ProtectedRoute>
        } />

        <Route path="/profile" element={
          <ProtectedRoute>
            <ProfilePage />
          </ProtectedRoute>
        } />

        <Route path="/assets" element={
          <ProtectedRoute>
            <AssetsPage />
          </ProtectedRoute>
        } />

        <Route path="/orders" element={
          <ProtectedRoute>
            <OrderHistoryPage />
          </ProtectedRoute>
        } />

        {/* 🔥 ADMIN ROUTE */}
        <Route path="/admin" element={
          <ProtectedRoute adminOnly={true}>
            <AdminDashboard />
          </ProtectedRoute>
        } />

        {/* Default */}
        <Route path="/" element={<LoginPage />} />

      </Routes>
    </Router>
  );
}

export default App;