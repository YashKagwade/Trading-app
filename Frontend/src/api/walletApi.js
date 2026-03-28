import API from "./axios";

// GET WALLET
export const getWallet = () =>
  API.get("/api/wallet/getwallet");

// DEPOSIT
export const depositMoney = (orderId, paymentId) =>
  API.put(`/api/wallet/deposit?order_id=${orderId}&payment_id=${paymentId}`);

// ❌ OLD TRANSFER (keep if needed)
export const transferMoney = (walletId, data) =>
  API.put(`/api/wallet/${walletId}/transfer`, data);

// ✅ NEW TRANSFER USING EMAIL (ADD THIS)
export const transferMoneyByEmail = (data) =>
  API.post("/api/wallet/transfer/email", data);

// PAY ORDER
export const payOrder = (orderId) =>
  API.put(`/api/wallet/${orderId}/pay`);