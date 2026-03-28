/**
 * ORDER API FUNCTIONS
 * Purpose: Handle buy/sell orders for cryptocurrencies
 * Orders = transactions to buy or sell coins
 */

import API from "./axios";

/**
 * CREATE ORDER
 * Endpoint: POST /api/orders/pay
 * Requires: JWT authentication
 * Purpose: Create a new buy or sell order
 * 
 * Expected data object:
 * {
 *   coinId: string,
 *   quantity: number,
 *   orderType: "BUY" | "SELL"
 * }
 * 
 * Response: Order object with id, status, etc.
 */
export const createOrder = (data) =>
  API.post("/api/orders/pay", data);

/**
 * GET ALL ORDERS
 * Endpoint: GET /api/orders
 * Requires: JWT authentication
 * Purpose: Fetch all orders (buy/sell) with optional filters
 * 
 * Parameters (optional):
 * params = {
 *   orderType: "BUY" | "SELL",
 *   assetSymbol: string (filter by coin)
 * }
 * 
 * Response: Array of order objects
 */
export const getOrders = (params = {}) =>
  API.get("/api/orders", { params });

/**
 * GET ORDER BY ID
 * Endpoint: GET /api/orders/{orderId}
 * Requires: JWT authentication
 * Purpose: Fetch details of a specific order
 * 
 * Parameters:
 * - orderId: number (unique order identifier)
 * 
 * Response: Single order object with all details
 */
export const getOrderById = (orderId) =>
  API.get(`/api/orders/${orderId}`);