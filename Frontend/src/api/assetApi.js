/**
 * ASSET API FUNCTIONS
 * Purpose: Fetch user's cryptocurrency holdings/portfolio
 * Assets = coins owned by user
 */

import API from "./axios";

/**
 * GET ALL USER ASSETS
 * Endpoint: GET /api/asset
 * Requires: JWT authentication
 * Purpose: Fetch all coins owned by logged-in user (complete portfolio)
 * 
 * Response: Array of assets
 * [
 *   {
 *     id: number,
 *     quantity: number (how many coins),
 *     buyPrice: number (price per coin),
 *     coin: { id, symbol, name, currentPrice, ...}
 *   },
 *   ...
 * ]
 */
export const getUserAssets = () =>
  API.get("/api/asset");

/**
 * GET ASSET BY COIN ID
 * Endpoint: GET /api/asset/coin/{coinId}/user
 * Requires: JWT authentication
 * Purpose: Get user's holdings of a specific coin
 * 
 * Parameters:
 * - coinId: string (Bitcoin = "bitcoin", Ethereum = "ethereum", etc.)
 * 
 * Response: Single asset object
 * {
 *   id: number,
 *   quantity: number,
 *   buyPrice: number,
 *   coin: { ...coin details }
 * }
 */
export const getAssetByCoin = (coinId) =>
  API.get(`/api/asset/coin/${coinId}/user`);

/**
 * GET ASSET BY ID
 * Endpoint: GET /api/asset/{assetId}
 * Requires: JWT authentication
 * Purpose: Fetch specific asset by asset ID
 * 
 * Parameters:
 * - assetId: number (unique asset identifier)
 * 
 * Response: Asset object
 */
export const getAssetById = (assetId) =>
  API.get(`/api/asset/${assetId}`);