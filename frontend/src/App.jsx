import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { useState } from 'react';
import { authService } from './services/auth';
import Login from './components/Auth/Login';
import './App.css';

// Dashboard Overview Content
const DashboardContent = () => (
  <div>
    <h1 style={{ marginBottom: '30px', color: '#333' }}>Dashboard Overview</h1>
    <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', gap: '20px' }}>
      {[
        { label: 'Total Patients', value: '5' },
        { label: 'Predictions Today', value: '0' },
        { label: 'High Risk', value: '0' },
        { label: 'Active Doctors', value: '2' }
      ].map((stat, idx) => (
        <div key={idx} style={{ background: 'white', padding: '30px', borderRadius: '10px', boxShadow: '0 2px 8px rgba(0,0,0,0.1)', textAlign: 'center' }}>
          <h3 style={{ fontSize: '14px', color: '#666', marginBottom: '10px', textTransform: 'uppercase', letterSpacing: '0.5px' }}>{stat.label}</h3>
          <p style={{ fontSize: '36px', fontWeight: '700', color: '#667eea', margin: 0 }}>{stat.value}</p>
        </div>
      ))}
    </div>
  </div>
);

// Patients Page Placeholder
const PatientsContent = () => (
  <div>
    <h1 style={{ color: '#333' }}>Patient Management</h1>
    <div style={{ background: 'white', padding: '40px', borderRadius: '10px', marginTop: '20px', textAlign: 'center' }}>
      <p style={{ fontSize: '18px', color: '#666' }}>Patient list will appear here</p>
      <p style={{ fontSize: '14px', color: '#999', marginTop: '10px' }}>Connect to API endpoint: GET /api/v1/patients</p>
    </div>
  </div>
);

// Prediction Page Placeholder
const PredictContent = () => (
  <div>
    <h1 style={{ color: '#333' }}>Make Prediction</h1>
    <div style={{ background: 'white', padding: '40px', borderRadius: '10px', marginTop: '20px', textAlign: 'center' }}>
      <p style={{ fontSize: '18px', color: '#666' }}>Prediction form will appear here</p>
      <p style={{ fontSize: '14px', color: '#999', marginTop: '10px' }}>POST /api/v1/predictions/predict</p>
    </div>
  </div>
);

// History Page Placeholder
const HistoryContent = () => (
  <div>
    <h1 style={{ color: '#333' }}>Prediction History</h1>
    <div style={{ background: 'white', padding: '40px', borderRadius: '10px', marginTop: '20px', textAlign: 'center' }}>
      <p style={{ fontSize: '18px', color: '#666' }}>Prediction history will appear here</p>
      <p style={{ fontSize: '14px', color: '#999', marginTop: '10px' }}>GET /api/v1/predictions/recent</p>
    </div>
  </div>
);

// Users Page Placeholder
const UsersContent = () => (
  <div>
    <h1 style={{ color: '#333' }}>User Management</h1>
    <div style={{ background: 'white', padding: '40px', borderRadius: '10px', marginTop: '20px', textAlign: 'center' }}>
      <p style={{ fontSize: '18px', color: '#666' }}>User management will appear here</p>
      <p style={{ fontSize: '14px', color: '#999', marginTop: '10px' }}>Admin only feature</p>
    </div>
  </div>
);

// Complete Dashboard Layout with Navigation
const DashboardLayout = () => {
  const [currentPage, setCurrentPage] = useState('dashboard');
  const user = authService.getCurrentUser();

  const handleLogout = () => {
    authService.logout();
    window.location.href = '/login';
  };

  const navItems = [
    { id: 'dashboard', label: 'Dashboard', icon: '📊' },
    { id: 'patients', label: 'Patients', icon: '👥' },
    { id: 'predict', label: 'Make Prediction', icon: '🔮' },
    { id: 'history', label: 'Prediction History', icon: '📋' },
  ];

  if (user?.role === 'ADMIN') {
    navItems.push({ id: 'users', label: 'User Management', icon: '⚙️' });
  }

  return (
    <div style={{ display: 'flex', minHeight: '100vh', background: '#f8f9fa' }}>
      {/* Sidebar */}
      <aside style={{
        width: '260px',
        background: 'linear-gradient(180deg, #667eea 0%, #764ba2 100%)',
        color: 'white',
        padding: '20px',
        position: 'fixed',
        height: '100vh',
        display: 'flex',
        flexDirection: 'column'
      }}>
        <div>
          <h2 style={{ marginBottom: '5px', fontSize: '20px' }}>Healthcare AI</h2>
          <p style={{ fontSize: '12px', opacity: 0.8, marginBottom: '30px' }}>Readmission Prediction</p>
          
          <nav style={{ display: 'flex', flexDirection: 'column', gap: '5px' }}>
            {navItems.map(item => (
              <a
                key={item.id}
                href="#"
                onClick={(e) => { e.preventDefault(); setCurrentPage(item.id); }}
                style={{
                  color: 'white',
                  padding: '12px 15px',
                  textDecoration: 'none',
                  borderRadius: '6px',
                  background: currentPage === item.id ? 'rgba(255,255,255,0.2)' : 'transparent',
                  display: 'flex',
                  alignItems: 'center',
                  gap: '10px',
                  transition: 'background 0.2s'
                }}
                onMouseEnter={(e) => {
                  if (currentPage !== item.id) {
                    e.target.style.background = 'rgba(255,255,255,0.1)';
                  }
                }}
                onMouseLeave={(e) => {
                  if (currentPage !== item.id) {
                    e.target.style.background = 'transparent';
                  }
                }}
              >
                <span>{item.icon}</span>
                <span>{item.label}</span>
              </a>
            ))}
          </nav>
        </div>

        <div style={{ marginTop: 'auto' }}>
          <div style={{ marginBottom: '15px', padding: '12px', background: 'rgba(255,255,255,0.1)', borderRadius: '8px' }}>
            <div style={{ width: '40px', height: '40px', borderRadius: '50%', background: 'rgba(255,255,255,0.2)', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '18px', fontWeight: '600', marginBottom: '10px' }}>
              {user?.fullName?.charAt(0) || 'U'}
            </div>
            <p style={{ fontSize: '14px', fontWeight: '600', margin: '0 0 2px 0' }}>{user?.fullName}</p>
            <p style={{ fontSize: '12px', margin: '0', opacity: 0.8, textTransform: 'capitalize' }}>{user?.role?.toLowerCase()}</p>
          </div>
          <button 
            onClick={handleLogout}
            style={{
              width: '100%',
              padding: '10px',
              background: 'rgba(255,255,255,0.1)',
              border: 'none',
              borderRadius: '6px',
              color: 'white',
              cursor: 'pointer',
              fontSize: '14px',
              fontWeight: '600',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              gap: '8px'
            }}
          >
            <span>🚪</span>
            <span>Logout</span>
          </button>
        </div>
      </aside>

      {/* Main Content */}
      <div style={{ marginLeft: '260px', flex: 1, padding: '30px' }}>
        {currentPage === 'dashboard' && <DashboardContent />}
        {currentPage === 'patients' && <PatientsContent />}
        {currentPage === 'predict' && <PredictContent />}
        {currentPage === 'history' && <HistoryContent />}
        {currentPage === 'users' && <UsersContent />}
      </div>
    </div>
  );
};

// Auth check component
const PrivateRoute = ({ children }) => {
  const token = localStorage.getItem('token');
  if (!token) {
    return <Navigate to="/login" replace />;
  }
  return children;
};

// Root redirect
const Home = () => {
  const token = localStorage.getItem('token');
  return <Navigate to={token ? "/dashboard" : "/login"} replace />;
};

// Main App Component
function App() {
  return (
    <Router>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/dashboard" element={
          <PrivateRoute>
            <DashboardLayout />
          </PrivateRoute>
        } />
        <Route path="/" element={<Home />} />
        <Route path="*" element={<Home />} />
      </Routes>
    </Router>
  );
}

export default App;
