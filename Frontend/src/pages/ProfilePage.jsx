// ProfilePage.jsx
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import API from '../api/axios';
import { getUserProfile } from '../api/userApi';
import { getUserAssets } from '../api/assetApi';
import { getWallet } from '../api/walletApi';
import 'bootstrap/dist/css/bootstrap.min.css';
import '../styles/ProfilePage.css';

function ProfilePage() {
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [assets, setAssets] = useState([]);
  const [wallet, setWallet] = useState(null);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState('profile');
  const [editing, setEditing] = useState(false);
  const [editForm, setEditForm] = useState({
    fullname: '',
    mobile: ''
  });

  const token = localStorage.getItem('token');

  // Redirect to login if no token
  useEffect(() => {
    if (!token) {
      navigate('/login');
    }
  }, [token, navigate]);

  // Fetch user profile
  useEffect(() => {
    const fetchUserData = async () => {
      try {
        const userResponse = await getUserProfile();
        setUser(userResponse.data);
        setEditForm({
          fullname: userResponse.data.fullname,
          mobile: userResponse.data.mobile || ''
        });
        
        const assetsResponse = await getUserAssets();
        setAssets(assetsResponse.data);
        
        const walletResponse = await getWallet();
        setWallet(walletResponse.data);
      } catch (err) {
        console.error('Error fetching user data:', err);
        if (err.response?.status === 401) {
          localStorage.removeItem('token');
          navigate('/login');
        }
      } finally {
        setLoading(false);
      }
    };
    fetchUserData();
  }, [token, navigate]);

  // Calculate total portfolio value
  const calculatePortfolioValue = () => {
    return assets.reduce((total, asset) => {
      return total + (asset.quantity * (asset.coin?.currentPrice || 0));
    }, 0);
  };

  // Calculate total profit/loss
  const calculateProfitLoss = () => {
    return assets.reduce((total, asset) => {
      const currentValue = asset.quantity * (asset.coin?.currentPrice || 0);
      const buyValue = asset.quantity * asset.buyPrice;
      return total + (currentValue - buyValue);
    }, 0);
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    navigate('/login');
  };

  const handleEditProfile = async () => {
    try {
      // Update user profile
      const response = await API.put('/api/users/profile', editForm);
      setUser(response.data);
      setEditing(false);
      alert('Profile updated successfully!');
    } catch (err) {
      console.error('Error updating profile:', err);
      alert('Failed to update profile');
    }
  };

  if (loading) {
    return (
      <div className="profile-loading">
        <div className="spinner-border text-primary" role="status">
          <span className="visually-hidden">Loading...</span>
        </div>
        <p>Loading profile...</p>
      </div>
    );
  }

  return (
    <div className="profile-container">
      {/* Navbar */}
      <nav className="navbar navbar-dark bg-dark px-4 shadow-sm">
        <div className="container-fluid">
          <span className="navbar-brand mb-0 h1">
            👤 My Profile
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
              💰 Wallet: ₹{wallet?.balance?.toFixed(2) || '0.00'}
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
        {/* Stats Cards */}
        <div className="row g-4 mb-4">
          <div className="col-md-4">
            <div className="stat-card">
              <div className="stat-icon">💰</div>
              <div className="stat-info">
                <h6>Wallet Balance</h6>
                <h3>₹{wallet?.balance?.toFixed(2) || '0.00'}</h3>
              </div>
            </div>
          </div>
          <div className="col-md-4">
            <div className="stat-card">
              <div className="stat-icon">📊</div>
              <div className="stat-info">
                <h6>Portfolio Value</h6>
                <h3>₹{calculatePortfolioValue().toFixed(2)}</h3>
              </div>
            </div>
          </div>
          <div className="col-md-4">
            <div className="stat-card">
              <div className="stat-icon">📈</div>
              <div className="stat-info">
                <h6>Total P/L</h6>
                <h3 className={calculateProfitLoss() >= 0 ? 'positive' : 'negative'}>
                  {calculateProfitLoss() >= 0 ? '+' : ''}₹{calculateProfitLoss().toFixed(2)}
                </h3>
              </div>
            </div>
          </div>
        </div>

        {/* Tabs */}
        <div className="profile-tabs">
          <button 
            className={`tab ${activeTab === 'profile' ? 'active' : ''}`}
            onClick={() => setActiveTab('profile')}
          >
            Profile Information
          </button>
          <button 
            className={`tab ${activeTab === 'assets' ? 'active' : ''}`}
            onClick={() => setActiveTab('assets')}
          >
            My Assets ({assets.length})
          </button>
        </div>

        {/* Profile Information Tab */}
        {activeTab === 'profile' && (
          <div className="profile-info-card">
            <div className="card-header">
              <h4>Personal Information</h4>
              {!editing ? (
                <button className="btn-edit" onClick={() => setEditing(true)}>
                  ✏️ Edit Profile
                </button>
              ) : (
                <div className="edit-actions">
                  <button className="btn-cancel" onClick={() => setEditing(false)}>Cancel</button>
                  <button className="btn-save" onClick={handleEditProfile}>Save Changes</button>
                </div>
              )}
            </div>
            
            <div className="profile-details">
              <div className="detail-row">
                <div className="detail-label">Full Name</div>
                {editing ? (
                  <input
                    type="text"
                    className="form-control"
                    value={editForm.fullname}
                    onChange={(e) => setEditForm({...editForm, fullname: e.target.value})}
                  />
                ) : (
                  <div className="detail-value">{user?.fullname}</div>
                )}
              </div>
              
              <div className="detail-row">
                <div className="detail-label">Email Address</div>
                <div className="detail-value">{user?.email}</div>
              </div>
              
              <div className="detail-row">
                <div className="detail-label">Mobile Number</div>
                {editing ? (
                  <input
                    type="tel"
                    className="form-control"
                    value={editForm.mobile}
                    onChange={(e) => setEditForm({...editForm, mobile: e.target.value})}
                  />
                ) : (
                  <div className="detail-value">{user?.mobile || 'Not provided'}</div>
                )}
              </div>
              
              <div className="detail-row">
                <div className="detail-label">Account Type</div>
                <div className="detail-value">
                  <span className="badge-role">{user?.role || 'Customer'}</span>
                </div>
              </div>
              
              <div className="detail-row">
                <div className="detail-label">2FA Status</div>
                <div className="detail-value">
                  <span className={`badge-2fa ${user?.twoFactorAuth?.enabled ? 'enabled' : 'disabled'}`}>
                    {user?.twoFactorAuth?.enabled ? '✅ Enabled' : '❌ Disabled'}
                  </span>
                </div>
              </div>
              
              <div className="detail-row">
                <div className="detail-label">Member Since</div>
                <div className="detail-value">
                  {user?.createdAt ? new Date(user.createdAt).toLocaleDateString() : 'N/A'}
                </div>
              </div>
            </div>
          </div>
        )}

        {/* Assets Tab */}
        {activeTab === 'assets' && (
          <div className="assets-list">
            {assets.length === 0 ? (
              <div className="empty-assets">
                <div className="empty-icon">📭</div>
                <h5>No Assets Yet</h5>
                <p>Start trading to build your portfolio</p>
                <button className="btn-primary" onClick={() => navigate('/dashboard')}>
                  Start Trading
                </button>
              </div>
            ) : (
              <div className="assets-grid">
                {assets.map((asset) => {
                  const currentPrice = asset.coin?.currentPrice || 0;
                  const currentValue = asset.quantity * currentPrice;
                  const buyValue = asset.quantity * asset.buyPrice;
                  const profitLoss = currentValue - buyValue;
                  const profitLossPercent = (profitLoss / buyValue) * 100;
                  
                  return (
                    <div key={asset.id} className="asset-card">
                      <div className="asset-header">
                        <img 
                          src={asset.coin?.image} 
                          alt={asset.coin?.name} 
                          className="asset-icon"
                          onError={(e) => {
                            e.target.src = 'https://via.placeholder.com/40?text=Coin';
                          }}
                        />
                        <div>
                          <h5 className="asset-name">{asset.coin?.name}</h5>
                          <span className="asset-symbol">{asset.coin?.symbol?.toUpperCase()}</span>
                        </div>
                        <div className="asset-quantity">{asset.quantity}</div>
                      </div>
                      
                      <div className="asset-details">
                        <div className="detail">
                          <span>Buy Price:</span>
                          <strong>₹{asset.buyPrice.toFixed(2)}</strong>
                        </div>
                        <div className="detail">
                          <span>Current Price:</span>
                          <strong>₹{currentPrice.toFixed(2)}</strong>
                        </div>
                        <div className="detail">
                          <span>Current Value:</span>
                          <strong>₹{currentValue.toFixed(2)}</strong>
                        </div>
                        <div className="detail">
                          <span>Profit/Loss:</span>
                          <strong className={profitLoss >= 0 ? 'positive' : 'negative'}>
                            {profitLoss >= 0 ? '+' : ''}₹{profitLoss.toFixed(2)} ({profitLossPercent.toFixed(2)}%)
                          </strong>
                        </div>
                      </div>
                    </div>
                  );
                })}
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}

// Make sure to export default at the end
export default ProfilePage;