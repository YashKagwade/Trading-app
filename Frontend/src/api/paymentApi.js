import API from "./axios";

export const createPayment = (amount) => 
    API.post(`/api/payment/create?amount=${amount}`);

export const checkPaymentStatus = (orderId) => 
    API.get(`/api/payment/status/${orderId}`);