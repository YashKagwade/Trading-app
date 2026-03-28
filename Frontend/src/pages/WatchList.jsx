import React, { useEffect, useState } from "react";
import API from "../api/axios";
import { Link, useNavigate } from "react-router-dom";
import "../styles/DashboardPage.css";
import "../styles/WatchList.css";

const WatchlistPage = () => {
  const [watchlist, setWatchlist] = useState([]);
  const [loading, setLoading] = useState(true);
  const [user, setUser] = useState(null);
  const navigate = useNavigate();

  const fetchUserProfile = async () => {
    try {
      const response = await API.get("/api/users/profile");
      setUser(response.data);
    } catch (err) {
      console.error("Error fetching user:", err);
    }
  };

  const fetchWatchlist = async () => {
    try {
      setLoading(true);
      const res = await API.get("/api/watchlist/user");
      setWatchlist(res.data?.coins || []);
    } catch (err) {
      console.error(err);
      if (err.response?.status === 401) {
        localStorage.removeItem("token");
        navigate("/login");
      } else {
        alert("Failed to load watchlist");
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUserProfile();
    fetchWatchlist();
  }, []);

  const handleToggle = async (coinId) => {
    try {
      await API.patch(`/api/watchlist/add/coin/${coinId}`);
      fetchWatchlist();
    } catch {
      alert("Error updating watchlist");
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    navigate('/login');
  };

  if (loading) {
    return (
      <div className="loading-container">
        <div className="spinner-border text-primary" role="status">
          <span className="visually-hidden">Loading...</span>
        </div>
        <p className="mt-3">Loading watchlist...</p>
      </div>
    );
  }

  return (
    <div className="watchlist-container">
      {/* Navbar - Same as Dashboard */}
      <nav className="navbar navbar-dark bg-dark px-4 shadow-sm">
        <div className="container-fluid">
          <span className="navbar-brand mb-0 h1">
            📈 FinGrow Trading
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
              onClick={() => navigate('/wallet')}
            >
              💰 Wallet
            </button>
            <button
              className="btn btn-light active"
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
        <div className="watchlist-header mb-4">
          <h2 className="mb-2">⭐ Your Watchlist</h2>
          <p className="text-muted">Track your favorite cryptocurrencies</p>
        </div>

        {watchlist.length === 0 ? (
          <div className="empty-watchlist text-center py-5">
            <div className="empty-state-icon mb-3">📭</div>
            <h5 className="text-muted">No coins in watchlist</h5>
            <p className="text-secondary">Start adding your favorite coins to track them here</p>
            <Link to="/dashboard" className="btn btn-primary mt-3">
              Explore Coins
            </Link>
          </div>
        ) : (
          <>
            <div className="watchlist-stats mb-3">
              <span className="badge bg-primary">Total Coins: {watchlist.length}</span>
            </div>
            <div className="row g-4">
              {watchlist.map((coin) => (
                <div key={coin.id} className="col-md-6 col-lg-4">
                  <div className="watchlist-card">
                    <div className="watchlist-card-header">
                      <div className="coin-info">
                        <img 
                          src={coin.image} 
                          alt={coin.name} 
                          className="coin-avatar"
                          onError={(e) => {
                            e.target.src = 'https://via.placeholder.com/40?text=Coin';
                          }}
                        />
                        <div>
                          <h5 className="coin-name mb-0">{coin.name}</h5>
                          <span className="coin-symbol">{coin.symbol?.toUpperCase()}</span>
                        </div>
                      </div>
                      <button
                        className="remove-btn"
                        onClick={() => handleToggle(coin.id)}
                        title="Remove from watchlist"
                      >
                        ✖
                      </button>
                    </div>

                    <div className="watchlist-card-body">
                      <div className="price-row">
                        <span className="label">Current Price:</span>
                        <span className="value">₹ {coin.currentPrice?.toLocaleString(undefined, {
                          minimumFractionDigits: 2,
                          maximumFractionDigits: 2
                        })}</span>
                      </div>
                      
                      <div className="price-row">
                        <span className="label">24h Change:</span>
                        <span className={`value ${coin.price_change_percentage_24h >= 0 ? 'positive' : 'negative'}`}>
                          {coin.price_change_percentage_24h?.toFixed(2)}%
                        </span>
                      </div>
                      
                      <div className="price-row">
                        <span className="label">Market Cap:</span>
                        <span className="value">₹ {(coin.market_cap / 1e9)?.toFixed(2)}B</span>
                      </div>
                      
                      <div className="price-row">
                        <span className="label">24h Volume:</span>
                        <span className="value">₹ {(coin.total_volume / 1e9)?.toFixed(2)}B</span>
                      </div>
                    </div>

                    <div className="watchlist-card-footer">
                      <button
                        className="btn-view-details"
                        onClick={() => navigate(`/dashboard?coin=${coin.id}`)}
                      >
                        View Details →
                      </button>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </>
        )}
      </div>
    </div>
  );
};

export default WatchlistPage;