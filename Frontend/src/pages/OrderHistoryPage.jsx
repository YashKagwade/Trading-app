// src/pages/OrderHistoryPage.jsx
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import API from '../api/axios';
import '../styles/OrderHistoryPage.css';

function OrderHistoryPage() {
  const navigate = useNavigate();
  const [allOrders, setAllOrders] = useState([]);
  const [buyOrders, setBuyOrders] = useState([]);
  const [sellOrders, setSellOrders] = useState([]);
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState('all'); // 'all', 'buy', 'sell'
  const [selectedOrder, setSelectedOrder] = useState(null);
  const [showDetailsModal, setShowDetailsModal] = useState(false);

  const token = localStorage.getItem('token');

  useEffect(() => {
    if (!token) {
      navigate('/login');
    }
  }, [token, navigate]);

  // Fetch user profile
  useEffect(() => {
    const fetchUser = async () => {
      try {
        const response = await API.get('/api/users/profile');
        setUser(response.data);
      } catch (err) {
        console.error('Error fetching user:', err);
        if (err.response?.status === 401) {
          localStorage.removeItem('token');
          navigate('/login');
        }
      }
    };
    if (token) {
      fetchUser();
    }
  }, [token, navigate]);

  // Fetch all orders
  useEffect(() => {
    const fetchOrders = async () => {
      if (!token) return;
      
      setLoading(true);
      try {
        // Fetch all orders
        const response = await API.get('/api/orders');
        const orders = response.data;
        
        setAllOrders(orders);
        
        // Filter buy orders (where orderType is BUY)
        const buy = orders.filter(order => order.orderType === 'BUY');
        setBuyOrders(buy);
        
        // Filter sell orders (where orderType is SELL)
        const sell = orders.filter(order => order.orderType === 'SELL');
        setSellOrders(sell);
        
        console.log('All Orders:', orders);
        console.log('Buy Orders:', buy);
        console.log('Sell Orders:', sell);
      } catch (err) {
        console.error('Error fetching orders:', err);
      } finally {
        setLoading(false);
      }
    };
    fetchOrders();
  }, [token]);

  const handleLogout = () => {
    localStorage.removeItem('token');
    navigate('/login');
  };

  const getStatusBadgeClass = (status) => {
    switch(status) {
      case 'SUCCESS':
        return 'status-success';
      case 'PENDING':
        return 'status-pending';
      case 'FAILED':
        return 'status-failed';
      case 'CANCELLED':
        return 'status-cancelled';
      default:
        return 'status-default';
    }
  };

  const getOrderTypeClass = (type) => {
    return type === 'BUY' ? 'type-buy' : 'type-sell';
  };

  const viewOrderDetails = (order) => {
    setSelectedOrder(order);
    setShowDetailsModal(true);
  };

  const getCurrentOrders = () => {
    switch(activeTab) {
      case 'buy':
        return buyOrders;
      case 'sell':
        return sellOrders;
      default:
        return allOrders;
    }
  };

  const getTotalValue = (orders) => {
    return orders.reduce((total, order) => total + (order.price || 0), 0);
  };

  const getSuccessCount = (orders) => {
    return orders.filter(order => order.status === 'SUCCESS').length;
  };

  const currentOrders = getCurrentOrders();
  const totalValue = getTotalValue(currentOrders);
  const successCount = getSuccessCount(currentOrders);

  if (loading) {
    return (
      <div className="order-history-loading">
        <div className="spinner-border text-primary" role="status">
          <span className="visually-hidden">Loading...</span>
        </div>
        <p>Loading order history...</p>
      </div>
    );
  }

  return (
    <div className="order-history-container">
      {/* Navbar */}
      <nav className="navbar navbar-dark bg-dark px-4 shadow-sm">
        <div className="container-fluid">
          <span className="navbar-brand mb-0 h1">
            📋 Order History
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
              onClick={() => navigate('/assets')}
            >
              💼 Assets
            </button>
            <button
              className="btn btn-outline-light"
              onClick={() => navigate('/wallet')}
            >
              💰 Wallet
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

      {/* Main Content */}
      <div className="container py-4">
        <div className="order-header mb-4">
          <h2>📋 Order History</h2>
          <p className="text-muted">Track all your buy and sell transactions</p>
        </div>

        {/* Stats Cards */}
        <div className="row g-4 mb-4">
          <div className="col-md-4">
            <div className="stat-card">
              <div className="stat-icon">📊</div>
              <div className="stat-info">
                <h6>Total Orders</h6>
                <h3>{currentOrders.length}</h3>
              </div>
            </div>
          </div>
          <div className="col-md-4">
            <div className="stat-card">
              <div className="stat-icon">✅</div>
              <div className="stat-info">
                <h6>Successful Orders</h6>
                <h3 className="success">{successCount}</h3>
              </div>
            </div>
          </div>
          <div className="col-md-4">
            <div className="stat-card">
              <div className="stat-icon">💰</div>
              <div className="stat-info">
                <h6>Total Value</h6>
                <h3>₹{totalValue.toFixed(2)}</h3>
              </div>
            </div>
          </div>
        </div>

        {/* Tab Navigation */}
        <div className="order-tabs mb-4">
          <button
            className={`order-tab ${activeTab === 'all' ? 'active' : ''}`}
            onClick={() => setActiveTab('all')}
          >
            📋 All Orders ({allOrders.length})
          </button>
          <button
            className={`order-tab ${activeTab === 'buy' ? 'active' : ''}`}
            onClick={() => setActiveTab('buy')}
          >
            📈 Buy Orders ({buyOrders.length})
          </button>
          <button
            className={`order-tab ${activeTab === 'sell' ? 'active' : ''}`}
            onClick={() => setActiveTab('sell')}
          >
            📉 Sell Orders ({sellOrders.length})
          </button>
        </div>

        {/* Orders List */}
        {currentOrders.length === 0 ? (
          <div className="empty-orders">
            <div className="empty-icon">
              {activeTab === 'buy' ? '📈' : activeTab === 'sell' ? '📉' : '📭'}
            </div>
            <h5>
              {activeTab === 'buy' 
                ? 'No Buy Orders Yet' 
                : activeTab === 'sell' 
                ? 'No Sell Orders Yet' 
                : 'No Orders Yet'}
            </h5>
            <p>
              {activeTab === 'buy' 
                ? 'You haven\'t purchased any coins yet. Start trading to see your buy orders!'
                : activeTab === 'sell'
                ? 'You haven\'t sold any coins yet. Your sell orders will appear here!'
                : 'You haven\'t placed any orders yet. Start trading to see your order history!'}
            </p>
            <button className="btn-primary" onClick={() => navigate('/dashboard')}>
              Start Trading
            </button>
          </div>
        ) : (
          <div className="orders-list">
            {currentOrders.map((order) => (
              <div key={order.id} className="order-card" onClick={() => viewOrderDetails(order)}>
                <div className="order-card-header">
                  <div className="order-info">
                    <span className={`order-type ${getOrderTypeClass(order.orderType)}`}>
                      {order.orderType}
                    </span>
                    <span className="order-id">Order #{order.id}</span>
                  </div>
                  <div className={`order-status ${getStatusBadgeClass(order.status)}`}>
                    {order.status}
                  </div>
                </div>
                
                <div className="order-card-body">
                  <div className="order-details-row">
                    <div className="detail">
                      <span className="label">Coin:</span>
                      <strong className="value">
                        {order.orderItem?.coin?.name || order.orderItem?.coin?.id || 'N/A'}
                      </strong>
                      <span className="symbol">
                        {order.orderItem?.coin?.symbol?.toUpperCase()}
                      </span>
                    </div>
                    <div className="detail">
                      <span className="label">Quantity:</span>
                      <strong className="value">{order.orderItem?.quantity || 0}</strong>
                    </div>
                    <div className="detail">
                      <span className="label">Price per coin:</span>
                      <strong className="value">
                        ₹{order.orderType === 'BUY' 
                          ? order.orderItem?.buyPrice?.toFixed(2) 
                          : order.orderItem?.sellPrice?.toFixed(2)}
                      </strong>
                    </div>
                    <div className="detail">
                      <span className="label">Total:</span>
                      <strong className="value">₹{order.price?.toFixed(2)}</strong>
                    </div>
                  </div>
                  
                  <div className="order-meta">
                    <span className="date">
                      📅 {new Date(order.timestamp).toLocaleString()}
                    </span>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Order Details Modal */}
      {showDetailsModal && selectedOrder && (
        <div className="modal-overlay" onClick={() => setShowDetailsModal(false)}>
          <div className="modal-content order-details-modal" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3>Order Details</h3>
              <button className="modal-close" onClick={() => setShowDetailsModal(false)}>×</button>
            </div>
            <div className="modal-body">
              <div className="details-grid">
                <div className="detail-item">
                  <span className="detail-label">Order ID</span>
                  <span className="detail-value">#{selectedOrder.id}</span>
                </div>
                <div className="detail-item">
                  <span className="detail-label">Type</span>
                  <span className={`detail-value ${getOrderTypeClass(selectedOrder.orderType)}`}>
                    {selectedOrder.orderType}
                  </span>
                </div>
                <div className="detail-item">
                  <span className="detail-label">Status</span>
                  <span className={`detail-value ${getStatusBadgeClass(selectedOrder.status)}`}>
                    {selectedOrder.status}
                  </span>
                </div>
                <div className="detail-item">
                  <span className="detail-label">Coin</span>
                  <span className="detail-value">
                    {selectedOrder.orderItem?.coin?.name} ({selectedOrder.orderItem?.coin?.symbol?.toUpperCase()})
                  </span>
                </div>
                <div className="detail-item">
                  <span className="detail-label">Quantity</span>
                  <span className="detail-value">{selectedOrder.orderItem?.quantity}</span>
                </div>
                <div className="detail-item">
                  <span className="detail-label">Buy Price</span>
                  <span className="detail-value">₹{selectedOrder.orderItem?.buyPrice?.toFixed(2) || 'N/A'}</span>
                </div>
                <div className="detail-item">
                  <span className="detail-label">Sell Price</span>
                  <span className="detail-value">₹{selectedOrder.orderItem?.sellPrice?.toFixed(2) || 'N/A'}</span>
                </div>
                <div className="detail-item">
                  <span className="detail-label">Total Amount</span>
                  <span className="detail-value highlight">₹{selectedOrder.price?.toFixed(2)}</span>
                </div>
                <div className="detail-item">
                  <span className="detail-label">Date & Time</span>
                  <span className="detail-value">{new Date(selectedOrder.timestamp).toLocaleString()}</span>
                </div>
              </div>
            </div>
            <div className="modal-footer">
              <button className="btn-secondary" onClick={() => setShowDetailsModal(false)}>
                Close
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default OrderHistoryPage;