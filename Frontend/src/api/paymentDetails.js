/**
 * PAYMENT DETAILS API FUNCTIONS
 * Purpose: Manage user's bank account details for withdrawal
 * Payment Details = bank account info used for withdrawals
 */

import API from "./axios";

/**
 * ADD BANK DETAILS
 * Endpoint: POST /api/payment-details
 * Requires: JWT authentication
 * Purpose: Save user's bank account details for future withdrawals
 * 
 * Expected data object:
 * {
 *   accountNumber: string (10-16 digits),
 *   accountHolderName: string,
 *   ifsc: string (IFSC code of bank),
 *   bankName: string
 * }
 * 
 * Response: Saved payment details object
 */
export const addPaymentDetails = (data) =>
  API.post("/api/payment-details", data);

/**
 * GET BANK DETAILS
 * Endpoint: GET /api/payment-details
 * Requires: JWT authentication
 * Purpose: Fetch user's saved bank account details
 * Used to pre-fill withdrawal form
 * 
 * Response: Payment details object
 * {
 *   id: number,
 *   accountNumber: string,
 *   accountHolderName: string,
 *   ifsc: string,
 *   bankName: string,
 *   user: { ...user details }
 * }
 */
export const getPaymentDetails = () =>
  API.get("/api/payment-details");