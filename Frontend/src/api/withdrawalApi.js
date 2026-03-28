/**
 * WITHDRAWAL API FUNCTIONS
 * Purpose: Handle user withdrawal requests and admin approval
 * Withdrawal = transferring money from wallet to bank account
 */

import API from "./axios";

/**
 * REQUEST WITHDRAWAL
 * Endpoint: POST /api/withdrawal/{amount}
 * Requires: JWT authentication
 * Purpose: User requests to withdraw money from wallet
 * Creates a withdrawal request with PENDING status
 * Immediately deducts amount from wallet (funds frozen)
 * 
 * Parameters:
 * - amount: number (amount in INR to withdraw)
 * 
 * Response: Withdrawal object
 * {
 *   id: number,
 *   amount: number,
 *   status: "PENDING" (waiting for admin approval),
 *   user: { ...user details },
 *   date: timestamp
 * }
 */
export const requestWithdrawal = (amount) =>
  API.post(`/api/withdrawal/${amount}`);

/**
 * ADMIN APPROVE/REJECT WITHDRAWAL
 * Endpoint: PATCH /api/withdrawal/admin/{id}/proceed/{accept}
 * Requires: JWT authentication (admin only)
 * Purpose: Admin approves or rejects user's withdrawal request
 * 
 * Parameters:
 * - id: number (withdrawal request ID)
 * - accept: boolean (true = approve, false = reject)
 * 
 * Response: Updated withdrawal object with new status
 * - If approved: status = "SUCCESS" (money sent to bank)
 * - If rejected: status = "DECLINE" (amount refunded to wallet)
 */
export const proceedWithdrawal = (id, accept) =>
  API.patch(`/api/withdrawal/admin/${id}/proceed/${accept}`);

/**
 * GET ALL WITHDRAWALS (ADMIN)
 * Endpoint: GET /api/withdrawal/admin
 * Requires: JWT authentication (admin only)
 * Purpose: Admin views all user withdrawal requests
 * Used in admin dashboard to manage withdrawals
 * 
 * Response: Array of withdrawal objects (all users' withdrawals)
 * [
 *   {
 *     id: number,
 *     amount: number,
 *     status: "PENDING" | "SUCCESS" | "DECLINE",
 *     user: { fullname, email, mobile, ... },
 *     date: timestamp
 *   },
 *   ...
 * ]
 */
export const getAllWithdrawals = () =>
  API.get("/api/withdrawal/admin");