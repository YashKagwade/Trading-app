// AssetsPage.jsx
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getUserAssets } from '../api/assetApi';
import { getUserProfile } from '../api/userApi';
import { getWallet } from '../api/walletApi';
import { getTop50Coins } from '../api/coinApi'; // ✅ ADDED
import 'bootstrap/dist/css/bootstrap.min.css';
import '../styles/AssetsPage.css';

function AssetsPage() {
  const navigate = useNavigate();

  const [assets, setAssets] = useState([]);
  const [coins, setCoins] = useState([]); // ✅ ADDED
  const [user, setUser] = useState(null);
  const [wallet, setWallet] = useState(null);
  const [loading, setLoading] = useState(true);

  const token = localStorage.getItem('token');

  useEffect(() => {
    if (!token) navigate('/login');
  }, [token, navigate]);

  // 🔥 FETCH ALL DATA (FIXED)
  useEffect(() => {
    const fetchData = async () => {
      try {
        const [assetsRes, userRes, walletRes, coinsRes] = await Promise.all([
          getUserAssets(),
          getUserProfile(),
          getWallet(),
          getTop50Coins() // ✅ FETCH COINS
        ]);

        setAssets(assetsRes.data);
        setUser(userRes.data);
        setWallet(walletRes.data);
        setCoins(coinsRes);

      } catch (err) {
        console.error('Error fetching assets:', err);
        if (err.response?.status === 401) {
          localStorage.removeItem('token');
          navigate('/login');
        }
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [token, navigate]);

  const handleLogout = () => {
    localStorage.removeItem('token');
    navigate('/login');
  };

  // ✅ FIXED: Portfolio calculation using LIVE PRICE
  const calculatePortfolioValue = () => {
    return assets.reduce((total, asset) => {
      const coin = coins.find(c => c.id === asset.coin?.id);
      const price = coin?.current_price || 0;
      return total + (asset.quantity * price);
    }, 0);
  };

  // ✅ FIXED: Profit/Loss calculation
  const calculateTotalProfitLoss = () => {
    return assets.reduce((total, asset) => {
      const coin = coins.find(c => c.id === asset.coin?.id);
      const currentPrice = coin?.current_price || 0;

      const currentValue = asset.quantity * currentPrice;
      const buyValue = asset.quantity * asset.buyPrice;

      return total + (currentValue - buyValue);
    }, 0);
  };

  if (loading) {
    return (
      <div className="assets-loading">
        <div className="spinner-border text-primary"></div>
        <p>Loading your assets...</p>
      </div>
    );
  }

  return (
    <div className="assets-container">

      {/* Navbar */}
      <nav className="navbar navbar-dark bg-dark px-4">
        <div className="container-fluid">
          <span className="navbar-brand">💼 My Assets</span>
          <div className="d-flex gap-3">
            <button className="btn btn-outline-light" onClick={() => navigate('/dashboard')}>
              📈 Dashboard
            </button>
            <button className="btn btn-outline-light" onClick={() => navigate('/wallet')}>
              💰 Wallet: ₹{wallet?.balance?.toFixed(2) || '0.00'}
            </button>
            <button className="btn btn-outline-light" onClick={() => navigate('/watchlist')}>
              ⭐ Watchlist
            </button>
            <button className="btn btn-outline-danger" onClick={handleLogout}>
              🚪 Logout
            </button>
          </div>
        </div>
      </nav>

      {/* Content */}
      <div className="container py-4">

        {/* Stats */}
        <div className="row g-4 mb-4">
          <div className="col-md-6">
            <div className="stats-card">
              <h6>Total Portfolio Value</h6>
              <h3>₹{calculatePortfolioValue().toFixed(2)}</h3>
            </div>
          </div>

          <div className="col-md-6">
            <div className="stats-card">
              <h6>Total Profit/Loss</h6>
              <h3 className={calculateTotalProfitLoss() >= 0 ? 'positive' : 'negative'}>
                {calculateTotalProfitLoss() >= 0 ? '+' : ''}
                ₹{calculateTotalProfitLoss().toFixed(2)}
              </h3>
            </div>
          </div>
        </div>

        {/* Assets Table */}
        {assets.length === 0 ? (
          <div className="text-center">
            <h5>No Assets Yet</h5>
            <button className="btn btn-primary" onClick={() => navigate('/dashboard')}>
              Start Trading
            </button>
          </div>
        ) : (
          <table className="table table-dark">
            <thead>
              <tr>
                <th>Coin</th>
                <th>Qty</th>
                <th>Buy Price</th>
                <th>Current Price</th>
                <th>Value</th>
                <th>P/L</th>
              </tr>
            </thead>
            <tbody>
              {assets.map(asset => {

                // ✅ FIXED PRICE LOGIC
                const coin = coins.find(c => c.id === asset.coin?.id);
                const currentPrice = coin?.current_price || 0;

                const currentValue = asset.quantity * currentPrice;
                const buyValue = asset.quantity * asset.buyPrice;
                const profitLoss = currentValue - buyValue;

                return (
                  <tr key={asset.id}>
                    <td>{asset.coin?.name}</td>
                    <td>{asset.quantity}</td>
                    <td>₹{asset.buyPrice}</td>
                    <td>₹{currentPrice.toFixed(2)}</td>
                    <td>₹{currentValue.toFixed(2)}</td>
                    <td className={profitLoss >= 0 ? 'text-success' : 'text-danger'}>
                      ₹{profitLoss.toFixed(2)}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        )}

      </div>
    </div>
  );
}

export default AssetsPage;