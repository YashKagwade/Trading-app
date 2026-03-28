import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { verifyOtp } from '../api/autApi';
import 'bootstrap/dist/css/bootstrap.min.css';
import '../styles/OtpVerificationPage.css';
import API from '../api/axios';
function OtpVerificationPage() {
  const [otp, setOtp] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleOtpSubmit = async (e) => {
    e.preventDefault();
    setError('');

    // Validate OTP
    if (!otp.trim()) {
      setError('OTP is required');
      return;
    }

    if (!/^\d{6}$/.test(otp)) {
      setError('OTP must be 6 digits');
      return;
    }

    setLoading(true);

    try {
      // Get session ID from sessionStorage
      const sessionId = sessionStorage.getItem('session');

      if (!sessionId) {
        setError('Session expired. Please login again.');
        navigate('/login');
        return;
      }

      // Verify OTP
      const response = await verifyOtp(otp, sessionId);

      // Store JWT token
      localStorage.setItem('token', response.data.jwt);
// 🔥 ADD THIS
const profile = await API.get("/api/users/profile");
localStorage.setItem("user", JSON.stringify(profile.data));
      // Clear session storage
      sessionStorage.removeItem('session');

      // Redirect to dashboard
      navigate('/dashboard');
    } catch (err) {
      setError(err.response?.data?.message || 'Invalid OTP. Try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="otp-container">
      <div className="otp-wrapper">
        <div className="otp-card shadow">
          <div className="otp-header">
            <h1 className="mb-2">FinGrow Trading</h1>
            <p className="text-secondary">Two-Factor Authentication</p>
          </div>

          <div className="otp-info">
            <p className="text-muted">
              We've sent a 6-digit OTP to your registered email.
            </p>
          </div>

          {error && (
            <div className="alert alert-danger" role="alert">
              {error}
            </div>
          )}

          <form onSubmit={handleOtpSubmit}>
            <div className="mb-3">
              <label htmlFor="otp" className="form-label">
                Enter OTP
              </label>
              <input
                type="text"
                className="form-control text-center otp-input"
                id="otp"
                placeholder="000000"
                value={otp}
                onChange={(e) => setOtp(e.target.value.replace(/\D/g, '').slice(0, 6))}
                maxLength="6"
                required
              />
              <small className="text-muted d-block mt-2">
                OTP is valid for 10 minutes
              </small>
            </div>

            <button
              type="submit"
              className="btn btn-primary w-100 mb-3"
              disabled={loading}
            >
              {loading ? 'Verifying...' : 'Verify OTP'}
            </button>
          </form>

          <div className="text-center">
            <p className="text-secondary mb-0">
              Didn't receive OTP? 
              <a href="/login" className="ms-2">Try again</a>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}

export default OtpVerificationPage;