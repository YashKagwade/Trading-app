/**
 * AXIOS CONFIGURATION FILE
 * Purpose: Configure Axios HTTP client with JWT authentication
 */

import axios from "axios";

// Create Axios instance
const API = axios.create({
  baseURL: "http://localhost:5455",
});

/**
 * REQUEST INTERCEPTOR
 * Adds JWT token to all protected API requests
 * Skips token for public/auth endpoints like login, signup, forgot password
 */
API.interceptors.request.use((req) => {
  const token = localStorage.getItem("token");

  // 🚨 FORCE remove Authorization for forgot password
  if (req.url.includes("/auth/users/reset-password")) {
    delete req.headers.Authorization;
    return req;
  }

  if (token) {
    req.headers.Authorization = `Bearer ${token}`;
  }

  return req;
});
export default API;