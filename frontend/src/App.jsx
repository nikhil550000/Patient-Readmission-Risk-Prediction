import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { useState } from 'react';
import { authService } from './services/auth';
import Login from './components/Auth/Login';
import PatientList from './components/Patients/PatientList';
import PredictionForm from './components/Predictions/PredictionForm';
import PredictionHistory from './components/Predictions/PredictionHistory';
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

// Users Page Placeholder
const UsersContent = () => (
  <div>
    <h1 style={{ color: '#333' }}>User Management</h1>
    <div style={{ background: 'white', padding: '40px', borderRadius: '10px', marginTop: '20px', textAlign: 'center' }}>
      <p style={{ fontSize: '18px', color: '#666' }}>User management features</p>
      <p style={{ fontSize: '14px', color: '#999', marginTop: '10px' }}>Admin only - manage doctors, staff, and patient users</p>
    </div>
  </div>
);

// Complete Dashboard Layout with Navigation
const DashboardLayout = () => {
  const [currentPage, setCurrentPage] = useState('dashboard');
  const user = authService.getCurrentUser();

  const handleLogout = () => {
    localStorage.clear();
    authService.logout();
    window.location.href = '/login';
  };

  // Define navigation items based on role
  // Define navigation items based on role
const getNavItems = () => {
  const baseItems = [
    { id: 'dashboard', label: 'Dashboard', icon: '📊', roles: ['ADMIN', 'DOCTOR', 'PATIENT'] }
  ];

  // Add role-specific items
  if (user?.role === 'ADMIN' || user?.role === 'DOCTOR') {
    baseItems.push(
      { id: 'patients', label: 'Patients', icon: '👥', roles: ['ADMIN', 'DOCTOR'] },
      { id: 'predict', label: 'Make Prediction', icon: '🔮', roles: ['ADMIN', 'DOCTOR'] },
      { id: 'history', label: 'Prediction History', icon: '📋', roles: ['ADMIN', 'DOCTOR'] }
    );
  }

  if (user?.role === 'PATIENT') {
    baseItems.push(
      { id: 'history', label: 'My Predictions', icon: '📋', roles: ['PATIENT'] }
    );
  }

  // REMOVED: User Management menu item completely

  return baseItems.filter(item => item.roles.includes(user?.role));
};


  const navItems = getNavItems();

  return (
    <div style={{ 
      display: 'flex', 
      minHeight: '100vh',
      width: '100vw',
      background: '#f8f9fa',
      position: 'relative',
      overflow: 'hidden'
    }}>
      {/* Sidebar */}
      <aside style={{
        width: '260px',
        minWidth: '260px',
        background: 'linear-gradient(180deg, #667eea 0%, #764ba2 100%)',
        color: 'white',
        padding: '20px',
        position: 'fixed',
        height: '100vh',
        left: 0,
        top: 0,
        display: 'flex',
        flexDirection: 'column',
        zIndex: 1000,
        overflowY: 'auto'
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
                  transition: 'background 0.2s',
                  cursor: 'pointer'
                }}
                onMouseEnter={(e) => {
                  if (currentPage !== item.id) {
                    e.currentTarget.style.background = 'rgba(255,255,255,0.1)';
                  }
                }}
                onMouseLeave={(e) => {
                  if (currentPage !== item.id) {
                    e.currentTarget.style.background = 'transparent';
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
      <main style={{ 
        marginLeft: '260px',
        width: 'calc(100vw - 260px)',
        minHeight: '100vh',
        padding: '30px',
        background: '#f8f9fa',
        overflowY: 'auto',
        overflowX: 'hidden',
        boxSizing: 'border-box'
      }}>
        {currentPage === 'dashboard' && <DashboardContent />}
        {currentPage === 'patients' && <PatientList />}
        {currentPage === 'predict' && <PredictionForm />}
        {currentPage === 'history' && <PredictionHistory />}
        {currentPage === 'users' && <UsersContent />}
      </main>
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
