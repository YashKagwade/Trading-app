import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { signup } from '../api/autApi';
import 'bootstrap/dist/css/bootstrap.min.css';
import '../styles/SignupPage.css';

function SignupPage() {
  const [fullname, setFullname] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [mobile, setMobile] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  // Validation function
 const validateForm = () => {

  if (!fullname.trim()) {
    setError("Full name is required");
    return false;
  }

  if (fullname.length < 3) {
    setError("Full name must be at least 3 characters");
    return false;
  }

  if (!/^[a-zA-Z\s]+$/.test(fullname)) {
    setError("Full name should contain only letters");
    return false;
  }

  if (!email.trim()) {
    setError("Email is required");
    return false;
  }

  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(email)) {
    setError("Invalid email format");
    return false;
  }

  if (!mobile.trim()) {
    setError("Mobile number is required");
    return false;
  }

  if (!/^[6-9]\d{9}$/.test(mobile)) {
    setError("Enter valid Indian mobile number");
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

  // 🔥 STRONG PASSWORD (important for real apps)
  if (!/(?=.*[A-Z])(?=.*[0-9])/.test(password)) {
    setError("Password must contain at least 1 uppercase and 1 number");
    return false;
  }

  if (password !== confirmPassword) {
    setError("Passwords do not match");
    return false;
  }

  return true;
};
  const handleSignup = async (e) => {
    e.preventDefault();
    setError('');

    // Validate before sending request
    if (!validateForm()) {
      return;
    }

    setLoading(true);

    try {
      const response = await signup({
        fullname,
        email,
        password,
        mobile,
      });

      // Store JWT token
      localStorage.setItem('token', response.data.jwt);
      
      // Redirect to dashboard
      navigate('/dashboard');
    } catch (err) {
      setError(
        err.response?.data?.message || 
        err.message || 
        'Signup failed. Try again.'
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="signup-container">
      <div className="signup-wrapper">
        <div className="signup-card shadow">
          <div className="signup-header">
            <h1 className="mb-2">FinGrow Trading</h1>
            <p className="text-secondary">Create Your Account</p>
          </div>

          {error && (
            <div className="alert alert-danger" role="alert">
              {error}
            </div>
          )}

          <form onSubmit={handleSignup}>
            <div className="mb-3">
              <label htmlFor="fullname" className="form-label">
                Full Name
              </label>
              <input
                type="text"
                className="form-control"
                id="fullname"
                placeholder="Enter your full name"
                value={fullname}
                onChange={(e) => setFullname(e.target.value)}
                required
              />
            </div>

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

            <div className="mb-3">
              <label htmlFor="mobile" className="form-label">
                Mobile Number
              </label>
              <input
                type="tel"
                className="form-control"
                id="mobile"
                placeholder="Enter 10-digit mobile number"
                value={mobile}
                onChange={(e) => setMobile(e.target.value.replace(/\D/g, ''))}
                maxLength="10"
                required
              />
            </div>

            <div className="mb-3">
              <label htmlFor="password" className="form-label">
                Password
              </label>
              <input
                type="password"
                className="form-control"
                id="password"
                placeholder="Enter password (min 6 characters)"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </div>

            <div className="mb-3">
              <label htmlFor="confirmPassword" className="form-label">
                Confirm Password
              </label>
              <input
                type="password"
                className="form-control"
                id="confirmPassword"
                placeholder="Confirm your password"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                required
              />
            </div>

            <button 
              type="submit" 
              className="btn btn-primary w-100 mb-3"
              disabled={loading}
            >
              {loading ? 'Creating Account...' : 'Sign Up'}
            </button>
          </form>

          <div className="text-center">
            <p className="text-secondary mb-0">
              Already have an account? 
              <a href="/login" className="ms-2">Login</a>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}

export default SignupPage;