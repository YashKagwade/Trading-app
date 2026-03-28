/**
 * WATCHLIST API FUNCTIONS
 * Purpose: Manage user's favorite coins (watchlist)
 * Watchlist = list of coins user wants to track/monitor
 */

import API from "./axios";

/**
 * GET USER WATCHLIST
 * Endpoint: GET /api/watchlist/user
 * Requires: JWT authentication
 * Purpose: Fetch all coins in user's watchlist
 * 
 * Response: Watchlist object
 * {
 *   id: number,
 *   user: { ...user details },
 *   coins: [
 *     {
 *       id: "bitcoin",
 *       symbol: "btc",
 *       name: "Bitcoin",
 *       currentPrice: 45000,
 *       ...otherData
 *     },
 *     ...
 *   ]
 * }
 */
export const getWatchlist = () =>
  API.get("/api/watchlist/user");

/**
 * TOGGLE COIN IN WATCHLIST
 * Endpoint: PATCH /api/watchlist/add/coin/{coinId}
 * Requires: JWT authentication
 * Purpose: Add or remove coin from watchlist (toggle)
 * Acts like a toggle: if coin exists → remove it, if not → add it
 * 
 * Parameters:
 * - coinId: string (e.g., "bitcoin", "ethereum")
 * 
 * Response: Updated coin object
 */
export const toggleWatchlistCoin = (coinId) =>
  API.patch(`/api/watchlist/add/coin/${coinId}`);