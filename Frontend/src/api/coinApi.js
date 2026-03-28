// src/api/coinApi.js
import API from "./axios";

export const getAllCoins = (page = 1) => 
  API.get(`/coins/list?page=${page}`);

export const getTrendingCoins = () =>
  API.get("/coins/trending");

export const getCoinDetails = (coinId) =>
  API.get(`/coins/details/${coinId}`);

export const getCoinChart = (coinId, days = 7) =>
  API.get(`/coins/${coinId}/chart?days=${days}`);

// ✅ FIXED: Handle the response properly
export const getTop50Coins = async () => {
  try {
    const response = await API.get('/coins/top50');
    
    console.log('📊 Top50 API Response:', response);
    
    // The data is already the array of coins
    if (response.data && Array.isArray(response.data)) {
      console.log(`✅ Got ${response.data.length} coins from API`);
      return response.data;
    }
    
    // If it's wrapped in a data property
    if (response.data && response.data.data && Array.isArray(response.data.data)) {
      console.log(`✅ Got ${response.data.data.length} coins from API (nested)`);
      return response.data.data;
    }
    
    // If it's a JsonNode that needs conversion
    if (response.data && typeof response.data === 'object') {
      const values = Object.values(response.data);
      if (values.length > 0 && Array.isArray(values[0])) {
        return values[0];
      }
      if (values.length > 0 && values[0] && typeof values[0] === 'object') {
        console.log(`✅ Converted object to array with ${values.length} items`);
        return values;
      }
    }
    
    console.warn('⚠️ Unexpected response format:', response.data);
    return [];
  } catch (err) {
    console.error('❌ Error fetching top50 coins:', err);
    return [];
  }
};