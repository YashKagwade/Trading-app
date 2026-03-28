/**
 * USER API FUNCTIONS
 * Purpose: Manage user profile and two-factor authentication (2FA)
 */

import API from "./axios";

/**
 * GET USER PROFILE
 * Endpoint: GET /api/users/profile
 * Requires: JWT authentication
 * Purpose: Fetch current logged-in user's profile information
 * 
 * Response: User object
 * {
 *   id: number,
 *   fullname: string,
 *   email: string,
 *   mobile: string,
 *   role: "ROLE_CUSTOMER",
 *   twoFactorAuth: { enabled: boolean, sendTo: string },
 *   ...
 * }
 */
export const getUserProfile = () =>
  API.get("/api/users/profile");

/**
 * SEND VERIFICATION OTP
 * Endpoint: POST /api/users/verification/{type}/send-otp
 * Requires: JWT authentication
 * Purpose: Send OTP to user's email/mobile to enable 2FA
 * This is the FIRST STEP to enable two-factor authentication
 * 
 * Parameters:
 * - type: "EMAIL" | "MOBILE" (where to send OTP)
 * 
 * Response: Success message
 * "OTP sent successfully"
 */
export const sendVerificationOtp = (type) =>
  API.post(`/api/users/verification/${type}/send-otp`);

/**
 * ENABLE TWO-FACTOR AUTHENTICATION
 * Endpoint: PATCH /api/users/enable-twofactor/verify-otp/{otp}
 * Requires: JWT authentication
 * Purpose: Verify OTP and ENABLE 2FA in user account
 * This is the SECOND STEP after user receives and enters OTP
 * 
 * Parameters:
 * - otp: string (6-digit code received via email/SMS)
 * 
 * Response: Updated user object with 2FA enabled
 * {
 *   ...user details,
 *   twoFactorAuth: { enabled: true, sendTo: "email@example.com" }
 * }
 */
export const enableTwoFactor = (otp) =>
  API.patch(`/api/users/enable-twofactor/verify-otp/${otp}`);