import { useState, useEffect } from 'react';
import api from '../../services/api';
import { Search, UserPlus, Mail, Phone, X } from 'lucide-react';
import './PatientList.css';

const PatientList = () => {
  const [patients, setPatients] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [error, setError] = useState('');
  const [showAddModal, setShowAddModal] = useState(false);
  const [showPredictionModal, setShowPredictionModal] = useState(false);
  const [selectedPatient, setSelectedPatient] = useState(null);

  // ... keep existing fetchPatients, filteredPatients, calculateAge functions ...

  useEffect(() => {
    fetchPatients();
  }, []);

  const fetchPatients = async () => {
    try {
      setLoading(true);
      const response = await api.get('/patients');
      setPatients(response.data);
      setError('');
    } catch (err) {
      setError('Failed to fetch patients');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const filteredPatients = patients.filter(patient =>
    patient.fullName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    patient.email?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    patient.patientId?.toString().includes(searchTerm)
  );

  const calculateAge = (dateOfBirth) => {
    if (!dateOfBirth) return 'N/A';
    const today = new Date();
    const birthDate = new Date(dateOfBirth);
    let age = today.getFullYear() - birthDate.getFullYear();
    const monthDiff = today.getMonth() - birthDate.getMonth();
    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
      age--;
    }
    return age;
  };

  const handleViewDetails = (patient) => {
    const age = calculateAge(patient.dateOfBirth);
    alert(
      `Patient Details\n\n` +
      `Name: ${patient.fullName}\n` +
      `Patient ID: ${patient.patientId}\n` +
      `Age: ${age} years\n` +
      `Gender: ${patient.gender === 'M' ? 'Male' : 'Female'}\n` +
      `Email: ${patient.email || 'Not provided'}\n` +
      `Phone: ${patient.phone || 'Not provided'}\n` +
      `Address: ${patient.address || 'Not provided'}`
    );
  };

  const handleMakePrediction = (patient) => {
    setSelectedPatient(patient);
    setShowPredictionModal(true);
  };

  const handleAddPatient = () => {
    setShowAddModal(true);
  };

  const handleAddPatientSubmit = async (e) => {
    e.preventDefault();
    const formData = new FormData(e.target);
    
    try {
      const patientData = {
        patientId: parseInt(formData.get('patientId')),
        fullName: formData.get('fullName'),
        dateOfBirth: formData.get('dateOfBirth'),
        gender: formData.get('gender'),
        email: formData.get('email'),
        phone: formData.get('phone'),
        address: formData.get('address')
      };

      await api.post('/patients', patientData);
      setShowAddModal(false);
      fetchPatients(); // Reload patient list
      alert('Patient added successfully!');
    } catch (err) {
      alert('Failed to add patient: ' + (err.response?.data?.message || err.message));
    }
  };

  if (loading) {
    return <div className="loading">Loading patients...</div>;
  }

  return (
    <div className="patient-list-container">
      <div className="page-header">
        <h2>Patient Management</h2>
        <button className="btn-primary" onClick={handleAddPatient}>
          <UserPlus size={18} />
          Add New Patient
        </button>
      </div>

      {error && <div className="error-banner">{error}</div>}

      <div className="search-bar">
        <Search size={20} className="search-icon" />
        <input
          type="text"
          placeholder="Search by name, email, or patient ID..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
      </div>

      <div className="patients-grid">
        {filteredPatients.map((patient) => (
          <div key={patient.id} className="patient-card">
            <div className="patient-header">
              <div className="patient-avatar">
                {patient.fullName?.charAt(0) || 'P'}
              </div>
              <div className="patient-info">
                <h3>{patient.fullName}</h3>
                <p className="patient-id">ID: {patient.patientId}</p>
              </div>
            </div>

            <div className="patient-details">
              <div className="detail-row">
                <span className="label">Age:</span>
                <span className="value">{calculateAge(patient.dateOfBirth)} years</span>
              </div>
              <div className="detail-row">
                <span className="label">Gender:</span>
                <span className="value">{patient.gender === 'M' ? 'Male' : 'Female'}</span>
              </div>
              <div className="detail-row">
                <Mail size={16} />
                <span className="value">{patient.email || 'No email'}</span>
              </div>
              <div className="detail-row">
                <Phone size={16} />
                <span className="value">{patient.phone || 'No phone'}</span>
              </div>
            </div>

            <div className="patient-footer">
              <button 
                className="btn-secondary"
                onClick={() => handleViewDetails(patient)}
              >
                View Details
              </button>
              <button 
                className="btn-predict"
                onClick={() => handleMakePrediction(patient)}
              >
                Make Prediction
              </button>
            </div>
          </div>
        ))}
      </div>

      {filteredPatients.length === 0 && (
        <div className="no-results">
          <p>No patients found</p>
        </div>
      )}

      {/* Add Patient Modal */}
      {showAddModal && (
        <div className="modal-overlay" onClick={() => setShowAddModal(false)}>
          <div className="modal-content add-patient-modal" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3>Add New Patient</h3>
              <button className="close-btn" onClick={() => setShowAddModal(false)}>
                <X size={24} />
              </button>
            </div>
            <form onSubmit={handleAddPatientSubmit}>
              <div className="form-row">
                <div className="form-field">
                  <label>Patient ID *</label>
                  <input type="number" name="patientId" required />
                </div>
                <div className="form-field">
                  <label>Full Name *</label>
                  <input type="text" name="fullName" required />
                </div>
              </div>
              <div className="form-row">
                <div className="form-field">
                  <label>Date of Birth *</label>
                  <input type="date" name="dateOfBirth" required />
                </div>
                <div className="form-field">
                  <label>Gender *</label>
                  <select name="gender" required>
                    <option value="M">Male</option>
                    <option value="F">Female</option>
                  </select>
                </div>
              </div>
              <div className="form-row">
                <div className="form-field">
                  <label>Email</label>
                  <input type="email" name="email" />
                </div>
                <div className="form-field">
                  <label>Phone</label>
                  <input type="tel" name="phone" />
                </div>
              </div>
              <div className="form-field full-width">
                <label>Address</label>
                <textarea name="address" rows="2"></textarea>
              </div>
              <div className="modal-actions">
                <button type="button" onClick={() => setShowAddModal(false)}>Cancel</button>
                <button type="submit" className="submit-btn">Add Patient</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Prediction Info Modal */}
      {showPredictionModal && selectedPatient && (
        <div className="modal-overlay" onClick={() => setShowPredictionModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <h3>Make Prediction for {selectedPatient.fullName}</h3>
            <p style={{ marginBottom: '15px', color: '#666' }}>
              To make a prediction, you'll need to enter lab values in the Prediction Form.
            </p>
            <p style={{ marginBottom: '20px', fontSize: '14px', color: '#999' }}>
              Patient ID: {selectedPatient.patientId}<br/>
              Age: {calculateAge(selectedPatient.dateOfBirth)} years<br/>
              Gender: {selectedPatient.gender === 'M' ? 'Male' : 'Female'}
            </p>
            <div style={{ display: 'flex', gap: '10px', justifyContent: 'center' }}>
              <button onClick={() => setShowPredictionModal(false)}>Close</button>
              <button 
                onClick={() => {
                  setShowPredictionModal(false);
                  alert('Navigate to "Make Prediction" page and use:\n\n' +
                        `Patient ID: ${selectedPatient.patientId}\n` +
                        `Age: ${calculateAge(selectedPatient.dateOfBirth)}\n` +
                        `Gender: ${selectedPatient.gender}`);
                }}
                style={{ background: '#667eea', color: 'white' }}
              >
                Go to Prediction Form
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default PatientList;
