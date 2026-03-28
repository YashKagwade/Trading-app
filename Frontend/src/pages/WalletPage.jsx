// WalletPage.jsx
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getWallet } from '../api/walletApi';
import { requestWithdrawal } from '../api/withdrawalApi';
import { createPayment } from '../api/paymentApi';
import { getUserProfile } from '../api/userApi';
import API from '../api/axios';
import 'bootstrap/dist/css/bootstrap.min.css';
import '../styles/WalletPage.css';
import { transferMoneyByEmail } from '../api/walletApi';
import { useLocation } from "react-router-dom";
function WalletPage() {
  const navigate = useNavigate();
  const [wallet, setWallet] = useState(null);
  const [user, setUser] = useState(null);
  const [showDepositModal, setShowDepositModal] = useState(false);
  const [showWithdrawModal, setShowWithdrawModal] = useState(false);
  const [showTransferModal, setShowTransferModal] = useState(false);
  const [amount, setAmount] = useState('');
  const [receiverEmail, setReceiverEmail] = useState('');
  const [loading, setLoading] = useState(false);
  const [transactions, setTransactions] = useState([]);
  const [withdrawalHistory, setWithdrawalHistory] = useState([]);
  const [activeTab, setActiveTab] = useState('wallet');

  useEffect(() => {
    fetchUserData();
    fetchWallet();
    fetchTransactions();
    fetchWithdrawalHistory();
  }, []);


const location = useLocation();

useEffect(() => {
  const params = new URLSearchParams(location.search);
  const status = params.get("status");

  if (status === "success") {
    alert("Payment successful ✅");
    window.history.replaceState({}, document.title, "/wallet"); // clean URL
  }

  if (status === "failed") {
    alert("Payment failed ❌");
  }
}, []);
useEffect(() => {
  const token = localStorage.getItem("token");

  if (token && !localStorage.getItem("user")) {
    fetchUser();
  }
}, []);

const fetchUser = async () => {
  try {
    const res = await API.get("/api/users/profile");
    localStorage.setItem("user", JSON.stringify(res.data));
  } catch (err) {
    console.error("User fetch failed", err);
  }
};

  const fetchUserData = async () => {
    try {
      const response = await getUserProfile();
      setUser(response.data);
    } catch (err) {
      console.error('Error fetching user:', err);
    }
  };

  const fetchWallet = async () => {
    try {
      const response = await getWallet();
      setWallet(response.data);
    } catch (err) {
      console.error('Error fetching wallet:', err);
    }
  };

  const fetchTransactions = async () => {
    try {
      const response = await API.get('/api/wallet/transactions');
      setTransactions(response.data);
    } catch (err) {
      console.error('Error fetching transactions:', err);
    }
  };

  const fetchWithdrawalHistory = async () => {
    try {
      const response = await API.get('/api/withdrawal/user');
      setWithdrawalHistory(response.data);
    } catch (err) {
      console.error('Error fetching withdrawal history:', err);
    }
  };

const handleDeposit = async () => {
  if (!amount || amount <= 0) {
    alert('Please enter a valid amount');
    return;
  }

  setLoading(true);

  try {
    const res = await createPayment(amount);

    const { txnToken, orderId, mid } = res.data;

    // 🔥 PAYTM FORM REDIRECT (IMPORTANT FIX)
    const form = document.createElement("form");
    form.method = "POST";
    form.action = "https://securestage.paytmpayments.com/theia/api/v1/showPaymentPage";

    form.innerHTML = `
      <input type="hidden" name="mid" value="${mid}" />
      <input type="hidden" name="orderId" value="${orderId}" />
      <input type="hidden" name="txnToken" value="${txnToken}" />
    `;

    document.body.appendChild(form);
    form.submit();

  } catch (err) {
    console.error(err);
    alert('Deposit failed: ' + (err.response?.data?.message || err.message));
  } finally {
    setLoading(false);
  }
};

  const handleWithdraw = async () => {
    if (!amount || amount <= 0) {
      alert('Please enter a valid amount');
      return;
    }
    if (amount > wallet?.balance) {
      alert('Insufficient balance');
      return;
    }
    setLoading(true);
    try {
      await requestWithdrawal(amount);
      alert('Withdrawal request submitted successfully! Admin will review it.');
      setShowWithdrawModal(false);
      setAmount('');
      fetchWallet();
      fetchWithdrawalHistory();
    } catch (err) {
      alert('Withdrawal failed: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };
const handleTransfer = async () => {
  if (!amount || amount <= 0) {
    alert('Please enter a valid amount');
    return;
  }
  if (!receiverEmail) {
    alert('Please enter receiver email');
    return;
  }
  if (amount > wallet?.balance) {
    alert('Insufficient balance');
    return;
  }

  setLoading(true);
  try {
    await transferMoneyByEmail({
      email: receiverEmail,
      amount: amount
    });

    alert(`Successfully transferred ₹${amount} to ${receiverEmail}`);
    setShowTransferModal(false);
    setAmount('');
    setReceiverEmail('');
    fetchWallet();

  } catch (err) {
    alert('Transfer failed: ' + (err.response?.data?.message || err.message));
  } finally {
    setLoading(false);
  }
};

  const handleLogout = () => {
    localStorage.removeItem('token');
    navigate('/login');
  };

  return (
    <div className="wallet-container">
      {/* Navbar */}
      <nav className="navbar navbar-dark bg-dark px-4 shadow-sm">
        <div className="container-fluid">
          <span className="navbar-brand mb-0 h1">
            💰 FinGrow Wallet
          </span>
          <div className="d-flex gap-3">
            <button
              className="btn btn-outline-light"
              onClick={() => navigate('/dashboard')}
            >
              📈 Dashboard
            </button>
            <button
              className="btn btn-outline-light"
              onClick={() => navigate('/watchlist')}
            >
              ⭐ Watchlist
            </button>
      <div className="dropdown">
  <button
    className="btn btn-light dropdown-toggle"
    type="button"
    data-bs-toggle="dropdown"
    aria-expanded="false"
  >
    👤 {user?.fullname || 'User'}
  </button>
  <ul className="dropdown-menu dropdown-menu-end">
    <li>
      <button className="dropdown-item" onClick={() => navigate('/profile')}>
        👤 Profile
      </button>
    </li>
    <li>
      <button className="dropdown-item" onClick={() => navigate('/assets')}>
        💼 My Assets
      </button>
    </li>
    <li>
      <button className="dropdown-item" onClick={() => navigate('/orders')}>
        📋 Order History
      </button>
    </li>
    <li>
      <button className="dropdown-item" onClick={() => navigate('/watchlist')}>
        ⭐ Watchlist
      </button>
    </li>
    <li>
      <button className="dropdown-item" onClick={() => navigate('/wallet')}>
        💰 Wallet
      </button>
    </li>
    <li><hr className="dropdown-divider" /></li>
    <li>
      <button className="dropdown-item text-danger" onClick={handleLogout}>
        🚪 Logout
      </button>
    </li>
  </ul>
</div>
          </div>
        </div>
      </nav>

      <div className="container py-4">
        <div className="wallet-content">
          {/* Wallet Balance Card */}
          <div className="wallet-balance-card mb-4">
            <div className="balance-header">
              <h3>Total Balance</h3>
              <div className="balance-amount">₹{wallet?.balance?.toFixed(2) || '0.00'}</div>
            </div>
            
            <div className="action-buttons">
              <button className="btn-deposit" onClick={() => setShowDepositModal(true)}>
                💰 Deposit Money
              </button>
              <button className="btn-transfer" onClick={() => setShowTransferModal(true)}>
                🔄 Transfer to User
              </button>
              <button className="btn-withdraw" onClick={() => setShowWithdrawModal(true)}>
                💸 Withdraw to Bank
              </button>
            </div>
          </div>

          {/* Tabs */}
          <div className="wallet-tabs">
            <button 
              className={`tab ${activeTab === 'wallet' ? 'active' : ''}`}
              onClick={() => setActiveTab('wallet')}
            >
              Wallet Info
            </button>
            <button 
              className={`tab ${activeTab === 'transactions' ? 'active' : ''}`}
              onClick={() => setActiveTab('transactions')}
            >
              Transaction History
            </button>
            <button 
              className={`tab ${activeTab === 'withdrawals' ? 'active' : ''}`}
              onClick={() => setActiveTab('withdrawals')}
            >
              Withdrawal History
            </button>
          </div>

          {/* Wallet Info Tab */}
          {activeTab === 'wallet' && (
            <div className="wallet-info">
              <div className="info-card">
                <h4 className="mb-4">Wallet Details</h4>
                <div className="info-row">
                  <span>Wallet ID:</span>
                  <strong>#{wallet?.id}</strong>
                </div>
                <div className="info-row">
                  <span>User Name:</span>
                  <strong>{user?.fullname}</strong>
                </div>
                <div className="info-row">
                  <span>Email:</span>
                  <strong>{user?.email}</strong>
                </div>
                <div className="info-row">
                  <span>Mobile:</span>
                  <strong>{user?.mobile}</strong>
                </div>
                <div className="info-row">
                  <span>Current Balance:</span>
                  <strong className="balance-highlight">₹{wallet?.balance?.toFixed(2)}</strong>
                </div>
              </div>
            </div>
          )}

          {/* Transaction History Tab */}
          {activeTab === 'transactions' && (
            <div className="transactions-list">
              {transactions.length === 0 ? (
                <div className="empty-state">
                  <p className="mb-0">No transactions yet</p>
                </div>
              ) : (
                transactions.map((txn, index) => (
                  <div key={index} className="transaction-card">
                    <div className="txn-icon">
                      {txn.type === 'ADD_MONEY' && '💰'}
                      {txn.type === 'WITHDRAWAL' && '💸'}
                      {txn.type === 'WALLET_TRANSFER' && '🔄'}
                      {txn.type === 'BUY_ASSET' && '📈'}
                      {txn.type === 'SELL_ASSET' && '📉'}
                    </div>
                    <div className="txn-details">
                      <div className="txn-type">{txn.type?.replace('_', ' ')}</div>
                      <div className="txn-date">{new Date(txn.date).toLocaleDateString()}</div>
                      <div className="txn-purpose">{txn.purpose || 'Transaction'}</div>
                    </div>
                    <div className={`txn-amount ${txn.amount > 0 ? 'positive' : 'negative'}`}>
                      {txn.amount > 0 ? '+' : '-'} ₹{Math.abs(txn.amount).toFixed(2)}
                    </div>
                  </div>
                ))
              )}
            </div>
          )}

          {/* Withdrawal History Tab */}
          {activeTab === 'withdrawals' && (
            <div className="withdrawals-list">
              {withdrawalHistory.length === 0 ? (
                <div className="empty-state">
                  <p className="mb-0">No withdrawal requests yet</p>
                </div>
              ) : (
                withdrawalHistory.map((withdrawal, index) => (
                  <div key={index} className="withdrawal-card">
                    <div className="withdrawal-header">
                      <div className="withdrawal-amount">₹{withdrawal.amount}</div>
                      <div className={`withdrawal-status ${withdrawal.status?.toLowerCase()}`}>
                        {withdrawal.status}
                      </div>
                    </div>
                    <div className="withdrawal-date">
                      Requested on: {new Date(withdrawal.date).toLocaleDateString()}
                    </div>
                    <div className="withdrawal-id">Request ID: #{withdrawal.id}</div>
                  </div>
                ))
              )}
            </div>
          )}
        </div>
      </div>

      {/* Deposit Modal */}
      {showDepositModal && (
        <div className="modal-overlay" onClick={() => setShowDepositModal(false)}>
          <div className="modal-content" onClick={e => e.stopPropagation()}>
            <h3 className="mb-3">Deposit Money</h3>
            <div className="modal-body">
              <label>Amount (₹)</label>
              <input
                type="number"
                placeholder="Enter amount"
                value={amount}
                onChange={e => setAmount(e.target.value)}
                step="100"
                min="100"
                className="form-control"
              />
              <div className="balance-info mt-3">
                Minimum deposit: ₹100
              </div>
              <p className="info-text">You will be redirected to Paytm for payment</p>
            </div>
            <div className="modal-buttons">
              <button className="btn-cancel" onClick={() => setShowDepositModal(false)}>
                Cancel
              </button>
              <button className="btn-confirm" onClick={handleDeposit} disabled={loading}>
                {loading ? 'Processing...' : 'Proceed to Pay'}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Withdraw Modal */}
      {showWithdrawModal && (
        <div className="modal-overlay" onClick={() => setShowWithdrawModal(false)}>
          <div className="modal-content" onClick={e => e.stopPropagation()}>
            <h3 className="mb-3">Withdraw to Bank Account</h3>
            <div className="modal-body">
              <label>Amount (₹)</label>
              <input
                type="number"
                placeholder="Enter amount"
                value={amount}
                onChange={e => setAmount(e.target.value)}
                step="100"
                min="100"
                max={wallet?.balance}
                className="form-control"
              />
              <div className="balance-info mt-3">
                Available Balance: ₹{wallet?.balance?.toFixed(2)}
              </div>
              <p className="info-text">Withdrawal requests will be processed within 24-48 hours after admin approval</p>
            </div>
            <div className="modal-buttons">
              <button className="btn-cancel" onClick={() => setShowWithdrawModal(false)}>
                Cancel
              </button>
              <button className="btn-confirm" onClick={handleWithdraw} disabled={loading}>
                {loading ? 'Processing...' : 'Request Withdrawal'}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Transfer Modal */}
      {showTransferModal && (
        <div className="modal-overlay" onClick={() => setShowTransferModal(false)}>
          <div className="modal-content" onClick={e => e.stopPropagation()}>
            <h3 className="mb-3">Transfer to Another User</h3>
            <div className="modal-body">
              <label>Receiver Email</label>
              <input
                type="email"
                placeholder="Enter receiver's email"
                value={receiverEmail}
                onChange={e => setReceiverEmail(e.target.value)}
                className="form-control"
              />
              
              <label className="mt-3">Amount (₹)</label>
              <input
                type="number"
                placeholder="Enter amount"
                value={amount}
                onChange={e => setAmount(e.target.value)}
                step="1"
                min="1"
                max={wallet?.balance}
                className="form-control"
              />
              
              <div className="balance-info mt-3">
                Your Balance: ₹{wallet?.balance?.toFixed(2)}
              </div>
              <p className="info-text">Amount will be transferred directly to receiver's wallet</p>
            </div>
            <div className="modal-buttons">
              <button className="btn-cancel" onClick={() => setShowTransferModal(false)}>
                Cancel
              </button>
              <button className="btn-confirm" onClick={handleTransfer} disabled={loading}>
                {loading ? 'Processing...' : 'Confirm Transfer'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default WalletPage;