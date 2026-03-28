// DashboardPage.jsx
import { useState, useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import API from '../api/axios';
import { getWatchlist, toggleWatchlistCoin } from '../api/watchlistApi';
import { getUserAssets } from '../api/assetApi';
import { getWallet } from '../api/walletApi';
import { getUserProfile } from '../api/userApi';
import { getAllCoins, getCoinChart, getCoinDetails, getTop50Coins } from '../api/coinApi';
import { createOrder } from '../api/orderApi';
import {
  AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer
} from 'recharts';
import 'bootstrap/dist/css/bootstrap.min.css';
import '../styles/DashboardPage.css';

function DashboardPage() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const coinIdFromUrl = searchParams.get('coin');
  
  const [user, setUser] = useState(null);
  const [coins, setCoins] = useState([]);
  const [selectedCoin, setSelectedCoin] = useState(null);
  const [coinDetails, setCoinDetails] = useState(null);
  const [marketChart, setMarketChart] = useState([]);
  const [loading, setLoading] = useState(true);
  const [timeframe, setTimeframe] = useState(7);
  const [searchTerm, setSearchTerm] = useState('');
  const [showBuyModal, setShowBuyModal] = useState(false);
  const [showSellModal, setShowSellModal] = useState(false);
  const [orderQuantity, setOrderQuantity] = useState('');
  const [orderError, setOrderError] = useState('');
  const [orderSuccess, setOrderSuccess] = useState('');
  const [userAssets, setUserAssets] = useState([]);
  const [userWallet, setUserWallet] = useState(null);
  const [processingOrder, setProcessingOrder] = useState(false);
  const [connectionError, setConnectionError] = useState(false);
  const [watchlist, setWatchlist] = useState(null);
  const [isInWatchlist, setIsInWatchlist] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const [loadingMore, setLoadingMore] = useState(false);
  const [isLoadingCoins, setIsLoadingCoins] = useState(false);
  const [isAdmin, setIsAdmin] = useState(false);

  const token = localStorage.getItem('token');

  // Redirect to login if no token
  useEffect(() => {
    if (!token) {
      navigate('/login');
    }
  }, [token, navigate]);

  // Fetch current user profile
  useEffect(() => {
    const fetchUser = async () => {
      try {
        const response = await getUserProfile();
        setUser(response.data);
        if (response.data.role === 'ROLE_ADMIN'|| response.data.role === 'ADMIN') {
          setIsAdmin(true);
        }
        setConnectionError(false);
      } catch (err) {
        console.error('Error fetching user:', err);
        if (err.code === 'ERR_NETWORK') {
          setConnectionError(true);
        }
        if (err.response?.status === 401) {
          localStorage.removeItem('token');
          navigate('/login');
        }
      }
    };
    if (token) fetchUser();
  }, [token, navigate]);

  // Force refresh coins from API
  const forceRefreshCoins = async () => {
    setIsLoadingCoins(true);
    setOrderError('');
    
    try {
      console.log('🔄 Force refreshing coins from API...');
      const response = await API.get('/coins/refresh-all');
      console.log('Refresh response:', response.data);
      
      if (response.data.count > 0) {
        alert(`✅ ${response.data.message}`);
        
        const coinsResponse = await getTop50Coins();
        
        let coinsArray = [];
        if (Array.isArray(coinsResponse)) {
          coinsArray = coinsResponse;
        } else if (coinsResponse && coinsResponse.data && Array.isArray(coinsResponse.data)) {
          coinsArray = coinsResponse.data;
        } else if (coinsResponse && typeof coinsResponse === 'object') {
          coinsArray = Object.values(coinsResponse);
        }
        
        const uniqueCoins = [];
        const seenIds = new Set();
        for (const coin of coinsArray) {
          if (coin && coin.id && !seenIds.has(coin.id)) {
            seenIds.add(coin.id);
            uniqueCoins.push(coin);
          }
        }
        
        if (uniqueCoins.length > 0) {
          setCoins(uniqueCoins);
          localStorage.setItem('cachedCoins', JSON.stringify(uniqueCoins));
          if (coinIdFromUrl) {
            const coinFromUrl = uniqueCoins.find(coin => coin.id === coinIdFromUrl);
            setSelectedCoin(coinFromUrl || uniqueCoins[0]);
          } else {
            setSelectedCoin(uniqueCoins[0]);
          }
          console.log(`✅ Loaded ${uniqueCoins.length} unique coins`);
        }
      } else {
        alert('⚠️ Failed to load coins. Please try again.');
      }
    } catch (err) {
      console.error('Error refreshing coins:', err);
      if (err.response?.status === 429) {
        alert('⚠️ Rate limit exceeded. Please wait a minute and try again.');
      } else {
        alert('❌ Failed to refresh coins. Please try again later.');
      }
    } finally {
      setIsLoadingCoins(false);
    }
  };

  // Fetch coins list
  useEffect(() => {
    const fetchCoins = async () => {
      try {
        console.log('🔄 Fetching coins from backend...');
        
        const cachedCoins = localStorage.getItem('cachedCoins');
        if (cachedCoins && coins.length === 0) {
          try {
            const parsedCoins = JSON.parse(cachedCoins);
            if (parsedCoins.length > 0) {
              console.log(`📦 Using ${parsedCoins.length} cached coins`);
              setCoins(parsedCoins);
              if (coinIdFromUrl) {
                const coinFromUrl = parsedCoins.find(coin => coin.id === coinIdFromUrl);
                if (coinFromUrl) setSelectedCoin(coinFromUrl);
                else if (!selectedCoin && parsedCoins.length > 0) setSelectedCoin(parsedCoins[0]);
              } else if (!selectedCoin && parsedCoins.length > 0) {
                setSelectedCoin(parsedCoins[0]);
              }
            }
          } catch (e) {
            console.error('Error parsing cached coins:', e);
          }
        }
        
        const coinsData = await getTop50Coins();
        
        let coinsArray = [];
        if (Array.isArray(coinsData)) {
          coinsArray = coinsData;
        } else if (coinsData && coinsData.data && Array.isArray(coinsData.data)) {
          coinsArray = coinsData.data;
        } else if (coinsData && typeof coinsData === 'object') {
          coinsArray = Object.values(coinsData);
        }
        
        const uniqueCoins = [];
        const seenIds = new Set();
        for (const coin of coinsArray) {
          if (coin && coin.id && !seenIds.has(coin.id)) {
            seenIds.add(coin.id);
            uniqueCoins.push(coin);
          }
        }
        
        if (uniqueCoins.length > 0) {
          console.log(`✅ Loaded ${uniqueCoins.length} unique coins`);
          setCoins(uniqueCoins);
          localStorage.setItem('cachedCoins', JSON.stringify(uniqueCoins));
          
          if (coinIdFromUrl) {
            const coinFromUrl = uniqueCoins.find(coin => coin.id === coinIdFromUrl);
            if (coinFromUrl) {
              console.log(`🎯 Selecting coin from URL: ${coinFromUrl.name}`);
              setSelectedCoin(coinFromUrl);
            } else if (!selectedCoin && uniqueCoins.length > 0) {
              setSelectedCoin(uniqueCoins[0]);
            }
          } else if (!selectedCoin && uniqueCoins.length > 0) {
            setSelectedCoin(uniqueCoins[0]);
          }
        }
        setConnectionError(false);
      } catch (err) {
        console.error('Error fetching coins:', err);
        if (err.code === 'ERR_NETWORK') {
          setConnectionError(true);
        }
        if (coins.length === 0) {
          setOrderError('Failed to load coins. Click "Refresh Coins" to try again.');
        }
      } finally {
        setLoading(false);
      }
    };
    
    fetchCoins();
  }, [coinIdFromUrl]);

  // Load more coins (pagination)
  const loadMoreCoins = async () => {
    if (loadingMore) return;
    setLoadingMore(true);
    try {
      const nextPage = currentPage + 1;
      const response = await getAllCoins(nextPage);
      if (response.data && response.data.length > 0) {
        setCoins(prevCoins => [...prevCoins, ...response.data]);
        setCurrentPage(nextPage);
      }
    } catch (err) {
      console.error('Error loading more coins:', err);
    } finally {
      setLoadingMore(false);
    }
  };

  // Fetch user's watchlist
  useEffect(() => {
    const fetchWatchlist = async () => {
      try {
        const response = await getWatchlist();
        setWatchlist(response.data);
      } catch (err) {
        console.error('Error fetching watchlist:', err);
      }
    };
    if (user) fetchWatchlist();
  }, [user]);

  // Check if selected coin is in watchlist
  useEffect(() => {
    if (watchlist && selectedCoin) {
      const isInList = watchlist.coins?.some(coin => coin.id === selectedCoin.id);
      setIsInWatchlist(isInList);
    }
  }, [watchlist, selectedCoin]);

  // Fetch coin details when selected coin changes
  useEffect(() => {
    const fetchCoinDetailsAndChart = async () => {
      if (!selectedCoin) return;
      try {
        const detailsResponse = await getCoinDetails(selectedCoin.id);
        setCoinDetails(detailsResponse.data);
        
        const chartResponse = await getCoinChart(selectedCoin.id, timeframe);
        
        let chartData = chartResponse.data;
        if (typeof chartData === 'string') {
          chartData = JSON.parse(chartData);
        }
        
        if (chartData && chartData.prices) {
          const formattedData = chartData.prices.map(pricePoint => ({
            date: new Date(pricePoint[0]).toLocaleDateString(),
            price: pricePoint[1],
            timestamp: pricePoint[0]
          }));
          setMarketChart(formattedData);
        } else if (Array.isArray(chartData)) {
          const formattedData = chartData.map(point => ({
            date: new Date(point[0]).toLocaleDateString(),
            price: point[1],
            timestamp: point[0]
          }));
          setMarketChart(formattedData);
        }
      } catch (err) {
        console.error('Error fetching coin details:', err);
      }
    };
    fetchCoinDetailsAndChart();
  }, [selectedCoin, timeframe]);

  // Fetch user assets
  useEffect(() => {
    const fetchAssets = async () => {
      try {
        const response = await getUserAssets();
        setUserAssets(response.data);
      } catch (err) {
        console.error('Error fetching assets:', err);
      }
    };
    if (user) fetchAssets();
  }, [user]);

  // Fetch user wallet
  useEffect(() => {
    const fetchWallet = async () => {
      try {
        const response = await getWallet();
        setUserWallet(response.data);
      } catch (err) {
        console.error('Error fetching wallet:', err);
      }
    };
    if (user) fetchWallet();
  }, [user]);

  // Handle toggle watchlist
  const handleToggleWatchlist = async () => {
    if (!selectedCoin) return;
    try {
      await toggleWatchlistCoin(selectedCoin.id);
      setIsInWatchlist(!isInWatchlist);
      const watchlistResponse = await getWatchlist();
      setWatchlist(watchlistResponse.data);
    } catch (err) {
      console.error('Error toggling watchlist:', err);
    }
  };

  // Logout function
  const handleLogout = () => {
    localStorage.removeItem('token');
    navigate('/login');
  };

  // Handle buy order
  const handleBuy = async () => {
    setOrderError('');
    setOrderSuccess('');
    setProcessingOrder(true);

    if (!orderQuantity || parseFloat(orderQuantity) <= 0) {
      setOrderError('Please enter a valid quantity');
      setProcessingOrder(false);
      return;
    }

    const totalCost = parseFloat(orderQuantity) * (selectedCoin?.current_price || coinDetails?.currentPrice);

    if (userWallet && userWallet.balance < totalCost) {
      setOrderError(`Insufficient balance. Required: ₹${totalCost.toFixed(2)}`);
      setProcessingOrder(false);
      return;
    }

    try {
      const response = await createOrder({
        coinId: selectedCoin.id,
        quantity: parseFloat(orderQuantity),
        orderType: 'BUY'
      });

      setOrderSuccess(`Successfully bought ${orderQuantity} ${selectedCoin.symbol.toUpperCase()}`);
      setOrderQuantity('');
      setShowBuyModal(false);
      
      const walletRes = await getWallet();
      setUserWallet(walletRes.data);
      const assetsRes = await getUserAssets();
      setUserAssets(assetsRes.data);
      
      setTimeout(() => setOrderSuccess(''), 3000);
    } catch (err) {
      if (err.response?.data?.message?.includes('Rate limit')) {
        setOrderError('⚠️ Rate limit exceeded. Please wait a minute and try again.');
      } else {
        setOrderError(err.response?.data?.message || err.response?.data?.error || 'Buy order failed');
      }
    } finally {
      setProcessingOrder(false);
    }
  };

  // Handle sell order
  const handleSell = async () => {
    setOrderError('');
    setOrderSuccess('');
    setProcessingOrder(true);

    if (!orderQuantity || parseFloat(orderQuantity) <= 0) {
      setOrderError('Please enter a valid quantity');
      setProcessingOrder(false);
      return;
    }

    const assetToSell = userAssets.find(
      asset => asset.coin.id === selectedCoin.id
    );

    if (!assetToSell || assetToSell.quantity < parseFloat(orderQuantity)) {
      setOrderError('Insufficient asset quantity');
      setProcessingOrder(false);
      return;
    }

    try {
      const response = await createOrder({
        coinId: selectedCoin.id,
        quantity: parseFloat(orderQuantity),
        orderType: 'SELL'
      });

      setOrderSuccess(`Successfully sold ${orderQuantity} ${selectedCoin.symbol.toUpperCase()}`);
      setOrderQuantity('');
      setShowSellModal(false);
      
      const assetsRes = await getUserAssets();
      setUserAssets(assetsRes.data);
      
      setTimeout(() => setOrderSuccess(''), 3000);
    } catch (err) {
      if (err.response?.data?.message?.includes('Rate limit')) {
        setOrderError('⚠️ Rate limit exceeded. Please wait a minute and try again.');
      } else {
        setOrderError(err.response?.data?.message || err.response?.data?.error || 'Sell order failed');
      }
    } finally {
      setProcessingOrder(false);
    }
  };

  // Filter coins based on search
  const filteredCoins = coins.filter(coin =>
    coin.name?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    coin.symbol?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  // Get user's holding for selected coin
  const getUserHolding = () => {
    if (!selectedCoin) return null;
    const asset = userAssets.find(a => a.coin.id === selectedCoin.id);
    if (asset) {
      const currentPrice = selectedCoin.current_price || coinDetails?.currentPrice;
      const currentValue = asset.quantity * currentPrice;
      const buyValue = asset.quantity * asset.buyPrice;
      const profitLoss = currentValue - buyValue;
      const profitLossPercent = (profitLoss / buyValue) * 100;
      return { asset, currentValue, buyValue, profitLoss, profitLossPercent };
    }
    return null;
  };

  const holding = getUserHolding();
  const currentPrice = selectedCoin?.current_price || coinDetails?.currentPrice;

  // Show connection error if backend is not reachable
  if (connectionError) {
    return (
      <div className="dashboard-loading">
        <div className="alert alert-danger" style={{ maxWidth: '500px', margin: 'auto' }}>
          <h4>⚠️ Cannot Connect to Server</h4>
          <p>Please make sure:</p>
          <ul>
            <li>Spring Boot backend is running on port 5455</li>
            <li>Run: <code>mvn spring-boot:run</code> in your backend directory</li>
            <li>Check if backend is accessible at: <a href="http://localhost:5455">http://localhost:5455</a></li>
          </ul>
          <button className="btn btn-primary mt-2" onClick={() => window.location.reload()}>
            Retry Connection
          </button>
        </div>
      </div>
    );
  }

  if (loading && coins.length === 0) {
    return (
      <div className="dashboard-loading">
        <div className="spinner-border text-primary" role="status">
          <span className="visually-hidden">Loading...</span>
        </div>
        <p>Loading market data...</p>
        <button 
          className="btn btn-outline-primary mt-3"
          onClick={forceRefreshCoins}
          disabled={isLoadingCoins}
        >
          {isLoadingCoins ? 'Loading...' : '📥 Refresh Coins'}
        </button>
      </div>
    );
  }

  return (
    <div className="dashboard-container">
      {/* Navbar */}
      <nav className="navbar navbar-dark bg-dark px-4">
        <div className="container-fluid">
          <span className="navbar-brand mb-0 h1">
            📈 FinGrow Trading
          </span>
          <div className="d-flex gap-3">
            <button className="btn btn-outline-light" onClick={() => navigate('/dashboard')}>📈 Dashboard</button>
            <button className="btn btn-outline-light" onClick={() => navigate('/assets')}>💼 Assets</button>
            <button className="btn btn-outline-light" onClick={() => navigate('/orders')}>📋 Orders</button>
            <button className="btn btn-outline-light" onClick={() => navigate('/wallet')}>💰 Wallet: ₹{userWallet?.balance?.toFixed(2) || '0.00'}</button>
            <button className="btn btn-outline-light" onClick={() => navigate('/watchlist')}>⭐ Watchlist {watchlist?.coins?.length > 0 ? `(${watchlist.coins.length})` : ''}</button>
            <button className="btn btn-outline-warning" onClick={forceRefreshCoins} disabled={isLoadingCoins}>
              {isLoadingCoins ? '⏳ Refreshing...' : '🔄 Refresh Coins'}
            </button>
            {isAdmin && <button className="btn btn-outline-danger" onClick={() => navigate('/admin')}>👑 Admin</button>}
            <div className="dropdown">
              <button className="btn btn-light dropdown-toggle" type="button" data-bs-toggle="dropdown">
                👤 {user?.fullname || 'User'}
              </button>
              <ul className="dropdown-menu dropdown-menu-end">
                <li><button className="dropdown-item" onClick={() => navigate('/profile')}>👤 Profile</button></li>
                <li><button className="dropdown-item" onClick={() => navigate('/assets')}>💼 My Assets</button></li>
                <li><button className="dropdown-item" onClick={() => navigate('/orders')}>📋 Order History</button></li>
                <li><button className="dropdown-item" onClick={() => navigate('/watchlist')}>⭐ Watchlist</button></li>
                <li><button className="dropdown-item" onClick={() => navigate('/wallet')}>💰 Wallet</button></li>
                {isAdmin && <><li><hr className="dropdown-divider" /></li><li><button className="dropdown-item text-warning" onClick={() => navigate('/admin')}>👑 Admin Dashboard</button></li></>}
                <li><hr className="dropdown-divider" /></li>
                <li><button className="dropdown-item text-danger" onClick={handleLogout}>🚪 Logout</button></li>
              </ul>
            </div>
          </div>
        </div>
      </nav>

      {/* Success/Error Messages */}
      {orderSuccess && <div className="alert alert-success alert-dismissible fade show m-3">{orderSuccess}<button type="button" className="btn-close" onClick={() => setOrderSuccess('')}></button></div>}
      {orderError && <div className="alert alert-danger alert-dismissible fade show m-3">{orderError}<button type="button" className="btn-close" onClick={() => setOrderError('')}></button></div>}

      <div className="dashboard-content">
        {/* Left Sidebar - Coin List */}
        <div className="coin-sidebar">
          <div className="search-box p-3">
            <input type="text" className="form-control" placeholder="Search coins..." value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} />
          </div>
          <div className="coin-list">
            {filteredCoins.map(coin => (
              <div key={coin.id} className={`coin-item ${selectedCoin?.id === coin.id ? 'active' : ''}`} onClick={() => setSelectedCoin(coin)}>
                <img src={coin.image} alt={coin.name} className="coin-icon" />
                <div className="coin-info">
                  <div className="coin-name">{coin.name}</div>
                  <div className="coin-symbol">{coin.symbol?.toUpperCase()}</div>
                </div>
                <div className="coin-price">
                  <div className="price">₹{coin.current_price?.toFixed(2)}</div>
                  <div className={`price-change ${coin.price_change_percentage_24h >= 0 ? 'positive' : 'negative'}`}>
                    {coin.price_change_percentage_24h?.toFixed(2)}%
                  </div>
                </div>
              </div>
            ))}
            {coins.length >= 50 && (
              <div className="load-more-container p-3">
                <button className="btn btn-outline-primary w-100" onClick={loadMoreCoins} disabled={loadingMore}>
                  {loadingMore ? 'Loading...' : 'Load More Coins'}
                </button>
              </div>
            )}
          </div>
        </div>

        {/* Main Content - Chart and Trading */}
        <div className="main-content">
          {selectedCoin && (
            <>
              <div className="coin-header">
                <div className="coin-title">
                  <img src={selectedCoin.image} alt={selectedCoin.name} className="large-coin-icon" />
                  <div><h1>{selectedCoin.name}</h1><p className="text-muted">{selectedCoin.symbol?.toUpperCase()}</p></div>
                  <button onClick={handleToggleWatchlist} className={`watchlist-btn ${isInWatchlist ? 'active' : ''}`}>
                    {isInWatchlist ? '⭐' : '☆'}
                  </button>
                </div>
                <div className="coin-stats">
                  <div className="stat"><div className="stat-label">Price</div><div className="stat-value">₹{currentPrice?.toLocaleString()}</div></div>
                  <div className="stat"><div className="stat-label">24h Change</div><div className={`stat-value ${selectedCoin.price_change_percentage_24h >= 0 ? 'positive' : 'negative'}`}>{selectedCoin.price_change_percentage_24h?.toFixed(2)}%</div></div>
                  <div className="stat"><div className="stat-label">24h High</div><div className="stat-value">₹{selectedCoin.high_24h?.toLocaleString()}</div></div>
                  <div className="stat"><div className="stat-label">24h Low</div><div className="stat-value">₹{selectedCoin.low_24h?.toLocaleString()}</div></div>
                  <div className="stat"><div className="stat-label">Market Cap</div><div className="stat-value">₹{(selectedCoin.market_cap / 1e9)?.toFixed(2)}B</div></div>
                  <div className="stat"><div className="stat-label">Volume (24h)</div><div className="stat-value">₹{(selectedCoin.total_volume / 1e9)?.toFixed(2)}B</div></div>
                </div>
              </div>

              <div className="timeframe-selector">
                <button className={`timeframe-btn ${timeframe === 1 ? 'active' : ''}`} onClick={() => setTimeframe(1)}>1D</button>
                <button className={`timeframe-btn ${timeframe === 7 ? 'active' : ''}`} onClick={() => setTimeframe(7)}>7D</button>
                <button className={`timeframe-btn ${timeframe === 30 ? 'active' : ''}`} onClick={() => setTimeframe(30)}>1M</button>
                <button className={`timeframe-btn ${timeframe === 90 ? 'active' : ''}`} onClick={() => setTimeframe(90)}>3M</button>
                <button className={`timeframe-btn ${timeframe === 365 ? 'active' : ''}`} onClick={() => setTimeframe(365)}>1Y</button>
              </div>

              <div className="chart-container">
                {marketChart.length > 0 ? (
                  <ResponsiveContainer width="100%" height={400}>
                    <AreaChart data={marketChart}>
                      <defs><linearGradient id="colorPrice" x1="0" y1="0" x2="0" y2="1"><stop offset="5%" stopColor="#8884d8" stopOpacity={0.3}/><stop offset="95%" stopColor="#8884d8" stopOpacity={0}/></linearGradient></defs>
                      <CartesianGrid strokeDasharray="3 3" stroke="#333" />
                      <XAxis dataKey="date" tick={{ fill: '#888' }} interval="preserveStartEnd" />
                      <YAxis domain={['auto', 'auto']} tick={{ fill: '#888' }} tickFormatter={(value) => `₹${value.toLocaleString()}`} />
                      <Tooltip contentStyle={{ backgroundColor: '#1a1a2e', border: 'none', borderRadius: '8px' }} formatter={(value) => [`₹${value.toLocaleString()}`, 'Price']} />
                      <Area type="monotone" dataKey="price" stroke="#8884d8" strokeWidth={2} fill="url(#colorPrice)" />
                    </AreaChart>
                  </ResponsiveContainer>
                ) : <div className="chart-loading"><p>Loading chart data...</p></div>}
              </div>

              <div className="additional-stats">
                <div className="stat-card"><div className="stat-card-label">Market Cap Rank</div><div className="stat-card-value">#{selectedCoin.market_cap_rank}</div></div>
                <div className="stat-card"><div className="stat-card-label">Circulating Supply</div><div className="stat-card-value">{selectedCoin.circulating_supply?.toLocaleString()} {selectedCoin.symbol?.toUpperCase()}</div></div>
                <div className="stat-card"><div className="stat-card-label">Total Supply</div><div className="stat-card-value">{selectedCoin.total_supply?.toLocaleString()} {selectedCoin.symbol?.toUpperCase()}</div></div>
                <div className="stat-card"><div className="stat-card-label">ATH</div><div className="stat-card-value">₹{selectedCoin.ath?.toLocaleString()}</div></div>
              </div>

              <div className="trading-panel">
                <div className="trading-buttons">
                  <button className="btn-buy" onClick={() => setShowBuyModal(true)} disabled={processingOrder}>BUY</button>
                  <button className="btn-sell" onClick={() => setShowSellModal(true)} disabled={!holding || holding.asset.quantity === 0 || processingOrder}>SELL</button>
                </div>
                {holding && (
                  <div className="holding-info">
                    <h4>Your Holdings</h4>
                    <div className="holding-details">
                      <div className="holding-item"><span>Quantity:</span><strong>{holding.asset.quantity}</strong></div>
                      <div className="holding-item"><span>Avg Buy Price:</span><strong>₹{holding.asset.buyPrice.toFixed(2)}</strong></div>
                      <div className="holding-item"><span>Current Value:</span><strong>₹{holding.currentValue.toFixed(2)}</strong></div>
                      <div className="holding-item"><span>P/L:</span><strong className={holding.profitLoss >= 0 ? 'positive' : 'negative'}>₹{holding.profitLoss.toFixed(2)} ({holding.profitLossPercent.toFixed(2)}%)</strong></div>
                    </div>
                  </div>
                )}
              </div>
            </>
          )}
        </div>

        {/* Right Sidebar - Market Overview */}
        <div className="market-sidebar">
          <h4 className="sidebar-title">Market Overview</h4>
          <div className="market-stats">
            <div className="market-stat"><div className="market-stat-label">Total Market Cap</div><div className="market-stat-value">₹{(coins.reduce((sum, coin) => sum + (coin.market_cap || 0), 0) / 1e12).toFixed(2)}T</div></div>
            <div className="market-stat"><div className="market-stat-label">24h Volume</div><div className="market-stat-value">₹{(coins.reduce((sum, coin) => sum + (coin.total_volume || 0), 0) / 1e9).toFixed(2)}B</div></div>
          </div>
          <h4 className="sidebar-title mt-4">Top Gainers</h4>
          <div className="top-movers">
            {[...coins].sort((a, b) => (b.price_change_percentage_24h || 0) - (a.price_change_percentage_24h || 0)).slice(0, 5).map(coin => (
              <div key={coin.id} className="mover-item" onClick={() => setSelectedCoin(coin)}>
                <img src={coin.image} alt={coin.name} className="mover-icon" />
                <div className="mover-info"><div className="mover-name">{coin.symbol?.toUpperCase()}</div><div className="mover-change positive">+{coin.price_change_percentage_24h?.toFixed(2)}%</div></div>
                <div className="mover-price">₹{coin.current_price?.toFixed(2)}</div>
              </div>
            ))}
          </div>
          <h4 className="sidebar-title mt-4">Top Losers</h4>
          <div className="top-movers">
            {[...coins].sort((a, b) => (a.price_change_percentage_24h || 0) - (b.price_change_percentage_24h || 0)).slice(0, 5).map(coin => (
              <div key={coin.id} className="mover-item" onClick={() => setSelectedCoin(coin)}>
                <img src={coin.image} alt={coin.name} className="mover-icon" />
                <div className="mover-info"><div className="mover-name">{coin.symbol?.toUpperCase()}</div><div className="mover-change negative">{coin.price_change_percentage_24h?.toFixed(2)}%</div></div>
                <div className="mover-price">₹{coin.current_price?.toFixed(2)}</div>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Buy Modal */}
      {showBuyModal && (
        <div className="modal-overlay" onClick={() => setShowBuyModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header"><h3>Buy {selectedCoin?.name}</h3><button className="modal-close" onClick={() => setShowBuyModal(false)}>×</button></div>
            <div className="modal-body">
              <div className="price-info"><span>Current Price:</span><strong>₹{currentPrice?.toFixed(2)}</strong></div>
              <div className="form-group"><label>Quantity</label><input type="number" className="form-control" placeholder="Enter quantity" value={orderQuantity} onChange={(e) => setOrderQuantity(e.target.value)} step="0.0001" min="0" /></div>
              <div className="total-info"><span>Total:</span><strong>₹{(orderQuantity * currentPrice).toFixed(2)}</strong></div>
              {userWallet && <div className="wallet-info">Available Balance: ₹{userWallet.balance?.toFixed(2)}</div>}
            </div>
            <div className="modal-footer"><button className="btn-secondary" onClick={() => setShowBuyModal(false)}>Cancel</button><button className="btn-buy-modal" onClick={handleBuy} disabled={processingOrder}>{processingOrder ? 'Processing...' : 'Confirm Buy'}</button></div>
          </div>
        </div>
      )}

      {/* Sell Modal */}
      {showSellModal && (
        <div className="modal-overlay" onClick={() => setShowSellModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header"><h3>Sell {selectedCoin?.name}</h3><button className="modal-close" onClick={() => setShowSellModal(false)}>×</button></div>
            <div className="modal-body">
              <div className="price-info"><span>Current Price:</span><strong>₹{currentPrice?.toFixed(2)}</strong></div>
              {holding && <div className="holding-info-modal"><div>Available to sell: {holding.asset.quantity}</div><div>Avg Buy Price: ₹{holding.asset.buyPrice.toFixed(2)}</div></div>}
              <div className="form-group"><label>Quantity to Sell</label><input type="number" className="form-control" placeholder="Enter quantity" value={orderQuantity} onChange={(e) => setOrderQuantity(e.target.value)} step="0.0001" min="0" max={holding?.asset.quantity} /></div>
              <div className="total-info"><span>You will receive:</span><strong>₹{(orderQuantity * currentPrice).toFixed(2)}</strong></div>
            </div>
            <div className="modal-footer"><button className="btn-secondary" onClick={() => setShowSellModal(false)}>Cancel</button><button className="btn-sell-modal" onClick={handleSell} disabled={processingOrder}>{processingOrder ? 'Processing...' : 'Confirm Sell'}</button></div>
          </div>
        </div>
      )}
    </div>
  );
}

export default DashboardPage;