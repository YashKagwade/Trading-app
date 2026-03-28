import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import API from '../api/axios';
import '../styles/AdminDashboard.css';

function AdminDashboard() {
  const navigate = useNavigate();
  const [withdrawals, setWithdrawals] = useState([]);
  const [loading, setLoading] = useState(true);
  const [user, setUser] = useState(null);
  const [users, setUsers] = useState([]);
  const [activeTab, setActiveTab] = useState('withdrawals');
  const [selectedUser, setSelectedUser] = useState(null);
  const [showUserModal, setShowUserModal] = useState(false);
  const [stats, setStats] = useState({
    total: 0,
    pending: 0,
    approved: 0,
    rejected: 0
  });
  const [searchTerm, setSearchTerm] = useState('');

  const token = localStorage.getItem('token');

  useEffect(() => {
    if (!token) navigate('/login');
  }, [token, navigate]);

  // Fetch users
  const fetchAllUsers = async () => {
    try {
      const res = await API.get('/api/admin/users');
      setUsers(res.data);
    } catch (err) {
      console.error('Error fetching users:', err);
    }
  };

  // Fetch withdrawals
  const fetchWithdrawals = async () => {
    try {
      const res = await API.get('/api/withdrawal/admin');
      const data = res.data;

      setWithdrawals(data);

      setStats({
        total: data.length,
        pending: data.filter(w => w.status === 'PENDING').length,
        approved: data.filter(w => w.status === 'SUCCESS').length,
        rejected: data.filter(w => w.status === 'DECLINE').length
      });
    } catch (err) {
      console.error('Error fetching withdrawals:', err);
    }
  };

  // Initial load
  useEffect(() => {
    const load = async () => {
      try {
        const userRes = await API.get('/api/users/profile');
        setUser(userRes.data);

        if (userRes.data.role !== 'ROLE_ADMIN') {
          alert('Access denied. Admin privileges required.');
          navigate('/dashboard');
          return;
        }

        await fetchWithdrawals();
        await fetchAllUsers();

      } catch (err) {
        console.error(err);
        navigate('/dashboard');
      } finally {
        setLoading(false);
      }
    };

    load();
  }, []);

  // Approve withdrawal
  const handleApprove = async (id) => {
    try {
      await API.patch(`/api/withdrawal/admin/${id}/proceed/true`);
      await fetchWithdrawals();
      alert('✅ Withdrawal approved successfully!');
    } catch (err) {
      console.error('Error approving:', err);
      alert('Failed to approve withdrawal');
    }
  };

  // Reject withdrawal
  const handleReject = async (id) => {
    try {
      await API.patch(`/api/withdrawal/admin/${id}/proceed/false`);
      await fetchWithdrawals();
      alert('❌ Withdrawal rejected and amount refunded!');
    } catch (err) {
      console.error('Error rejecting:', err);
      alert('Failed to reject withdrawal');
    }
  };

  // Delete user
  const handleDeleteUser = async (id, name) => {
    if (!window.confirm(`Are you sure you want to delete user "${name}"? This action cannot be undone.`)) {
      return;
    }
    
    try {
      await API.delete(`/api/admin/users/${id}`);
      await fetchAllUsers();
      alert('User deleted successfully!');
    } catch (err) {
      console.error('Error deleting user:', err);
      alert('Failed to delete user');
    }
  };

  // View user details
  const viewUserDetails = async (id) => {
    try {
      const res = await API.get(`/api/admin/users/${id}`);
      setSelectedUser(res.data);
      setShowUserModal(true);
    } catch (err) {
      console.error('Error fetching user details:', err);
      alert('Failed to fetch user details');
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    navigate('/login');
  };

  // Filter withdrawals by search
  const filteredWithdrawals = withdrawals.filter(w => 
    w.user?.fullname?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    w.user?.email?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    w.id?.toString().includes(searchTerm)
  );

  // Filter users by search
  const filteredUsers = users.filter(u => 
    u.fullname?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    u.email?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    u.mobile?.includes(searchTerm)
  );

  const getStatusBadge = (status) => {
    switch(status) {
      case 'PENDING':
        return 'status-pending';
      case 'SUCCESS':
        return 'status-success';
      case 'DECLINE':
        return 'status-decline';
      default:
        return '';
    }
  };

  const getStatusIcon = (status) => {
    switch(status) {
      case 'PENDING':
        return '⏳';
      case 'SUCCESS':
        return '✅';
      case 'DECLINE':
        return '❌';
      default:
        return '📋';
    }
  };

  if (loading) {
    return (
      <div className="admin-loading">
        <div className="spinner-border text-primary" role="status">
          <span className="visually-hidden">Loading...</span>
        </div>
        <p>Loading admin dashboard...</p>
      </div>
    );
  }

  return (
    <div className="admin-container">
      {/* Navbar */}
      <nav className="navbar navbar-dark bg-dark px-4 shadow-sm">
        <div className="container-fluid">
          <span className="navbar-brand mb-0 h1">
            👑 Admin Dashboard
          </span>
          <div className="d-flex gap-3">
            <button
              className="btn btn-outline-light"
              onClick={() => navigate('/dashboard')}
            >
              📈 User Dashboard
            </button>
            <div className="dropdown">
              <button
                className="btn btn-light dropdown-toggle"
                type="button"
                data-bs-toggle="dropdown"
                aria-expanded="false"
              >
                👤 {user?.fullname} (Admin)
              </button>
              <ul className="dropdown-menu dropdown-menu-end">
                <li>
                  <button className="dropdown-item" onClick={() => navigate('/profile')}>
                    👤 Profile
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
        {/* Header */}
        <div className="admin-header mb-4">
          <h2>👑 Admin Control Panel</h2>
          <p>Manage withdrawals and user accounts</p>
        </div>

        {/* Stats Cards */}
        <div className="row g-4 mb-4">
          <div className="col-md-3">
            <div className="stat-card">
              <div className="stat-icon">💰</div>
              <div className="stat-info">
                <h6>Total Requests</h6>
                <h3>{stats.total}</h3>
              </div>
            </div>
          </div>
          <div className="col-md-3">
            <div className="stat-card pending">
              <div className="stat-icon">⏳</div>
              <div className="stat-info">
                <h6>Pending</h6>
                <h3 className="pending">{stats.pending}</h3>
              </div>
            </div>
          </div>
          <div className="col-md-3">
            <div className="stat-card success">
              <div className="stat-icon">✅</div>
              <div className="stat-info">
                <h6>Approved</h6>
                <h3 className="success">{stats.approved}</h3>
              </div>
            </div>
          </div>
          <div className="col-md-3">
            <div className="stat-card decline">
              <div className="stat-icon">❌</div>
              <div className="stat-info">
                <h6>Rejected</h6>
                <h3 className="decline">{stats.rejected}</h3>
              </div>
            </div>
          </div>
        </div>

        {/* Search Bar */}
        <div className="search-bar mb-4">
          <input
            type="text"
            className="search-input"
            placeholder="🔍 Search by name, email, or ID..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>

        {/* Tabs */}
        <div className="admin-tabs">
          <button 
            className={`tab-btn ${activeTab === 'withdrawals' ? 'active' : ''}`}
            onClick={() => setActiveTab('withdrawals')}
          >
            💰 Withdrawal Requests
            {stats.pending > 0 && (
              <span className="badge-pending">{stats.pending}</span>
            )}
          </button>
          <button 
            className={`tab-btn ${activeTab === 'users' ? 'active' : ''}`}
            onClick={() => setActiveTab('users')}
          >
            👥 User Management
            <span className="badge-users">{users.length}</span>
          </button>
        </div>

        {/* Withdrawals Tab */}
        {activeTab === 'withdrawals' && (
          <div className="withdrawals-section">
            {filteredWithdrawals.length === 0 ? (
              <div className="empty-state">
                <div className="empty-icon">📭</div>
                <h5>No Withdrawal Requests</h5>
                <p>All withdrawal requests will appear here</p>
              </div>
            ) : (
              <div className="withdrawals-grid">
                {filteredWithdrawals.map((withdrawal) => (
                  <div key={withdrawal.id} className="withdrawal-card">
                    <div className="card-header">
                      <div className="withdrawal-id">#{withdrawal.id}</div>
                      <div className={`status-badge ${getStatusBadge(withdrawal.status)}`}>
                        {getStatusIcon(withdrawal.status)} {withdrawal.status}
                      </div>
                    </div>
                    
                    <div className="user-info-section">
                      <div className="user-avatar">
                        {withdrawal.user?.fullname?.charAt(0) || 'U'}
                      </div>
                      <div className="user-details">
                        <h4>{withdrawal.user?.fullname}</h4>
                        <p>{withdrawal.user?.email}</p>
                        <p className="mobile">{withdrawal.user?.mobile}</p>
                      </div>
                    </div>
                    
                    <div className="withdrawal-amount-section">
                      <div className="amount-label">Requested Amount</div>
                      <div className="amount-value">₹{withdrawal.amount?.toLocaleString()}</div>
                    </div>
                    
                    <div className="date-section">
                      <span className="date-icon">📅</span>
                      {new Date(withdrawal.date).toLocaleString()}
                    </div>
                    
                    {withdrawal.status === 'PENDING' && (
                      <div className="action-buttons">
                        <button 
                          className="btn-approve"
                          onClick={() => handleApprove(withdrawal.id)}
                        >
                          ✓ Approve Request
                        </button>
                        <button 
                          className="btn-reject"
                          onClick={() => handleReject(withdrawal.id)}
                        >
                          ✗ Reject Request
                        </button>
                      </div>
                    )}
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {/* Users Tab */}
        {activeTab === 'users' && (
          <div className="users-section">
            {filteredUsers.length === 0 ? (
              <div className="empty-state">
                <div className="empty-icon">👥</div>
                <h5>No Users Found</h5>
                <p>Try a different search term</p>
              </div>
            ) : (
              <div className="users-grid">
                {filteredUsers.map((user) => (
                  <div key={user.id} className="user-card">
                    <div className="user-card-header">
                      <div className="user-avatar-large">
                        {user.fullname?.charAt(0) || 'U'}
                      </div>
                      <div className="user-role-badge">
                        {user.role === 'ROLE_ADMIN' ? '👑 Admin' : '👤 Customer'}
                      </div>
                    </div>
                    
                    <div className="user-card-body">
                      <h3>{user.fullname}</h3>
                      <p className="user-email">{user.email}</p>
                      <p className="user-mobile">{user.mobile || 'No mobile number'}</p>
                      
                      <div className="user-stats">
                        <div className="stat-item">
                          <span className="stat-label">ID:</span>
                          <span className="stat-value">#{user.id}</span>
                        </div>
                        <div className="stat-item">
                          <span className="stat-label">2FA:</span>
                          <span className={`stat-value ${user.twoFactorAuth?.enabled ? 'enabled' : 'disabled'}`}>
                            {user.twoFactorAuth?.enabled ? '✅ Enabled' : '❌ Disabled'}
                          </span>
                        </div>
                      </div>
                    </div>
                    
                    <div className="user-card-actions">
                      <button 
                        className="btn-view-details"
                        onClick={() => viewUserDetails(user.id)}
                      >
                        👁️ View Details
                      </button>
                      {user.role !== 'ROLE_ADMIN' && (
                        <button 
                          className="btn-delete-user"
                          onClick={() => handleDeleteUser(user.id, user.fullname)}
                        >
                          🗑️ Delete User
                        </button>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}
      </div>

      {/* User Details Modal */}
      {showUserModal && selectedUser && (
        <div className="modal-overlay" onClick={() => setShowUserModal(false)}>
          <div className="modal-content user-details-modal" onClick={e => e.stopPropagation()}>
            <div className="modal-header">
              <h3>User Details</h3>
              <button className="modal-close" onClick={() => setShowUserModal(false)}>✕</button>
            </div>
            <div className="modal-body">
              <div className="user-profile-header">
                <div className="user-avatar-modal">
                  {selectedUser.fullname?.charAt(0) || 'U'}
                </div>
                <div className="user-basic-info">
                  <h4>{selectedUser.fullname}</h4>
                  <span className={`role-tag ${selectedUser.role === 'ROLE_ADMIN' ? 'admin' : 'user'}`}>
                    {selectedUser.role === 'ROLE_ADMIN' ? 'Administrator' : 'Customer'}
                  </span>
                </div>
              </div>
              
              <div className="details-grid">
                <div className="detail-item">
                  <strong>User ID</strong>
                  <span>#{selectedUser.id}</span>
                </div>
                <div className="detail-item">
                  <strong>Email</strong>
                  <span>{selectedUser.email}</span>
                </div>
                <div className="detail-item">
                  <strong>Mobile</strong>
                  <span>{selectedUser.mobile || 'Not provided'}</span>
                </div>
                <div className="detail-item">
                  <strong>2FA Status</strong>
                  <span className={selectedUser.twoFactorAuth?.enabled ? 'text-success' : 'text-secondary'}>
                    {selectedUser.twoFactorAuth?.enabled ? '✅ Enabled' : '❌ Disabled'}
                  </span>
                </div>
                {selectedUser.twoFactorAuth?.enabled && (
                  <div className="detail-item">
                    <strong>2FA Send To</strong>
                    <span>{selectedUser.twoFactorAuth?.sendTo}</span>
                  </div>
                )}
                <div className="detail-item">
                  <strong>Member Since</strong>
                  <span>{selectedUser.createdAt ? new Date(selectedUser.createdAt).toLocaleDateString() : 'N/A'}</span>
                </div>
              </div>
            </div>
            <div className="modal-buttons">
              <button className="btn-cancel" onClick={() => setShowUserModal(false)}>
                Close
              </button>
              {selectedUser.role !== 'ROLE_ADMIN' && (
                <button 
                  className="btn-delete"
                  onClick={() => {
                    setShowUserModal(false);
                    handleDeleteUser(selectedUser.id, selectedUser.fullname);
                  }}
                >
                  Delete User
                </button>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default AdminDashboard;