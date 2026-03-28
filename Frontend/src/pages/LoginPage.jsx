/**
 * LOGIN PAGE COMPONENT
 * URL: http://localhost:5175/login
 * Purpose: User login interface
 * 
 * Features:
 * 1. Email and password input fields
 * 2. Form validation (email format, password length)
 * 3. API call to backend (/auth/signin)
 * 4. Handle 2FA (if enabled) or direct dashboard access (if disabled)
 * 5. Navigate to signup page for new users
 */

import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { signin } from '../api/autApi';
import 'bootstrap/dist/css/bootstrap.min.css';
import '../styles/LoginPage.css';
import API from '../api/axios'; // ✅ ADD THIS
function LoginPage() {
  // State variables to manage form data and UI
  const [email, setEmail] = useState('');           // Stores user email
  const [password, setPassword] = useState('');     // Stores user password
  const [loading, setLoading] = useState(false);    // Shows loading state during API call
  const [error, setError] = useState('');           // Displays error messages to user
  const navigate = useNavigate();                   // Router hook to navigate to different pages

  /**
   * HANDLE LOGIN FUNCTION
   * Triggered when user clicks "Login" button
   * 
   * Steps:
   * 1. Clear previous errors
   * 2. Call signin API with email and password
   * 3. If 2FA enabled → store session ID → go to /verify-otp
   * 4. If 2FA disabled → store JWT → go to /dashboard
   * 5. If error → display error message
   */
  const handleLogin = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const response = await signin({ email, password });
      console.log('🔐 Login Response:', response.data);

      if (response.data.isTwoFactorAuthEnabled) {
        console.log('📱 2FA Enabled, storing session and going to OTP');
        sessionStorage.setItem("session", response.data.session);
        console.log('Session stored:', sessionStorage.getItem("session"));
        navigate('/verify-otp');
      } else {
        console.log('✅ 2FA Disabled, storing JWT and going to dashboard');
        localStorage.setItem('token', response.data.jwt);
        console.log('Token stored:', localStorage.getItem('token'));
          // 🔥 ADD THESE 2 LINES HERE
  const profile = await API.get("/api/users/profile");
  localStorage.setItem("user", JSON.stringify(profile.data));
        // Verify token was stored
        const storedToken = localStorage.getItem('token');
        if (storedToken) {
          console.log('Token successfully stored, length:', storedToken.length);
          navigate('/dashboard');
        } else {
          console.error('❌ Token not stored!');
          setError('Failed to store authentication token');
        }
      }
    } catch (err) {
      console.error('❌ Login error:', err);
      setError(err.response?.data?.message || 'Login failed. Try again.');
    } finally {
      setLoading(false);
    }
  };

  const validateLogin = () => {
    if (!email.trim()) {
      setError("Email is required");
      return false;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
      setError("Invalid email format");
      return false;
    }

    if (!password) {
      setError("Password is required");
      return false;
    }

    if (password.length < 6) {
      setError("Password must be at least 6 characters");
      return false;
    }

    return true;
  };

  /**
   * RETURN JSX (UI)
   * Renders login form with background image
   */
  return (
    <div className="login-container">  {/* Background image container */}
      <div className="login-wrapper">   {/* Centered wrapper */}
        <div className="login-card shadow">  {/* White card with shadow */}
          {/* HEADER */}
          <div className="login-header">
            <h1 className="mb-2">FinGrow Trading</h1>  {/* App title */}
          </div>

          {/* ERROR MESSAGE (only shown if error exists) */}
          {error && (
            <div className="alert alert-danger" role="alert">
              {error}  {/* Displays backend error or validation error */}
            </div>
          )}

          {/* LOGIN FORM */}
          <form onSubmit={handleLogin}>
            {/* EMAIL INPUT FIELD */}
            <div className="mb-3">
              <label htmlFor="email" className="form-label">
                Email Address
              </label>
              <input
                type="email"
                className="form-control"
                id="email"
                placeholder="Enter your email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </div>

            {/* PASSWORD INPUT FIELD */}
            <div className="mb-3">
              <label htmlFor="password" className="form-label">
                Password
              </label>
              <input
                type="password"
                className="form-control"
                id="password"
                placeholder="Enter your password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
              {/* FORGOT PASSWORD LINK - Styled and positioned */}
              <div className="forgot-password-link mt-2 text-end">
                <a href="/forgot-password" className="small">Forgot Password?</a>
              </div>
            </div>

            {/* LOGIN BUTTON */}
            <button
              type="submit"
              className="btn btn-primary w-100 mb-3"
              disabled={loading}
            >
              {loading ? 'Logging in...' : 'Login'}
            </button>
          </form>

          {/* SIGNUP LINK FOR NEW USERS */}
          <div className="text-center">
            <p className="text-secondary mb-0">
              Don't have an account?
              <a href="/signup" className="ms-2">Sign up</a>
              {/* Link to signup page */}
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}

export default LoginPage;