/**
 * AUTHENTICATION API FUNCTIONS
 * Purpose: Handle login, signup, and 2FA OTP verification
 * All requests go to backend (Spring Boot) running on port 5455
 */

import API from "./axios";

/**
 * SIGNUP API
 * Endpoint: POST /auth/signup
 * Purpose: Register new user account
 * 
 * Expected data object:
 * {
 *   fullname: string,
 *   email: string,
 *   password: string,
 *   mobile: string
 * }
 * 
 * Response:
 * {
 *   jwt: string (authentication token),
 *   status: boolean,
 *   message: string
 * }
 */
export const signup = (data) =>
  API.post("/auth/signup", data);

/**
 * SIGNIN API
 * Endpoint: POST /auth/signin
 * Purpose: Login user with email and password
 * 
 * Expected data object:
 * {
 *   email: string,
 *   password: string
 * }
 * 
 * Response:
 * {
 *   jwt: string (if 2FA disabled),
 *   isTwoFactorAuthEnabled: boolean,
 *   session: string (if 2FA enabled),
 *   status: boolean,
 *   message: string
 * }
 */
export const signin = (data) =>
  API.post("/auth/signin", data);

/**
 * VERIFY OTP API
 * Endpoint: POST /auth/two-factor/otp/{otp}?id={sessionId}
 * Purpose: Verify OTP for 2FA and get JWT token
 * 
 * Parameters:
 * - otp: string (6-digit One Time Password sent to email)
 * - id: string (session ID received from signin response)
 * 
 * Response:
 * {
 *   jwt: string (authentication token after OTP verification),
 *   status: boolean,
 *   message: string
 * }
 */
export const verifyOtp = (otp, id) =>
  API.post(`/auth/two-factor/otp/${otp}?id=${id}`);