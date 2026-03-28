import { useState } from "react";
import API from "../api/axios";
import { useNavigate } from "react-router-dom";
import 'bootstrap/dist/css/bootstrap.min.css';
import '../styles/ForgotPasswordPage.css';

function ForgotPasswordPage() {
  const [email, setEmail] = useState("");
  const [session, setSession] = useState("");
  const [otp, setOtp] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [step, setStep] = useState(1);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const sendOtp = async () => {
    if (!email) {
      setError("Email is required");
      return;
    }
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
      setError("Invalid email format");
      return;
    }

    setLoading(true);
    setError("");
    try {
      const res = await API.post("/auth/users/reset-password/send-otp", {
        sendTo: email,
        verificationType: "EMAIL"
      });
      setSession(res.data.session);
      setStep(2);
    } catch (err) {
      console.error("Error sending OTP:", err);
      setError(err.response?.data?.message || "Error sending OTP. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  const resetPassword = async () => {
    if (!otp) {
      setError("OTP is required");
      return;
    }
    if (!password || password.length < 6) {
      setError("Password must be at least 6 characters");
      return;
    }
    if (password !== confirmPassword) {
      setError("Passwords do not match");
      return;
    }

    setLoading(true);
    setError("");
    try {
      await API.patch(`/auth/users/reset-password/verify-otp?id=${session}`, {
        otp,
        password
      });
      alert("Password updated successfully! You can now login.");
      navigate("/login");
    } catch (err) {
      console.error("Error resetting password:", err);
      setError(err.response?.data?.message || "Invalid OTP or server error.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="forgot-password-container">
      <div className="forgot-password-wrapper">
        <div className="forgot-password-card shadow">
          <div className="forgot-password-header">
            <h2>Reset Password</h2>
            <p>Enter your email to receive a verification code</p>
          </div>

          {error && <div className="alert alert-danger">{error}</div>}

          {step === 1 && (
            <div className="step-1">
              <div className="mb-3">
                <label className="form-label">Email Address</label>
                <input
                  type="email"
                  className="form-control"
                  placeholder="Enter your email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                />
              </div>
              <button
                className="btn btn-primary w-100"
                onClick={sendOtp}
                disabled={loading}
              >
                {loading ? "Sending..." : "Send OTP"}
              </button>
            </div>
          )}

          {step === 2 && (
            <div className="step-2">
              <div className="mb-3">
                <label className="form-label">Verification Code (OTP)</label>
                <input
                  type="text"
                  className="form-control"
                  placeholder="Enter 6-digit code"
                  value={otp}
                  onChange={(e) => setOtp(e.target.value)}
                />
              </div>
              <div className="mb-3">
                <label className="form-label">New Password</label>
                <input
                  type="password"
                  className="form-control"
                  placeholder="Minimum 6 characters"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                />
              </div>
              <div className="mb-3">
                <label className="form-label">Confirm Password</label>
                <input
                  type="password"
                  className="form-control"
                  placeholder="Re-enter new password"
                  value={confirmPassword}
                  onChange={(e) => setConfirmPassword(e.target.value)}
                />
              </div>
              <button
                className="btn btn-primary w-100"
                onClick={resetPassword}
                disabled={loading}
              >
                {loading ? "Resetting..." : "Reset Password"}
              </button>
            </div>
          )}

          <div className="text-center mt-3">
            <a href="/login" className="small">Back to Login</a>
          </div>
        </div>
      </div>
    </div>
  );
}

export default ForgotPasswordPage;