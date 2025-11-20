import { useState, useEffect } from 'react';
import api from '../../services/api';
import { Search, UserPlus, Mail, Phone } from 'lucide-react';
import './PatientList.css';

const PatientList = () => {
  const [patients, setPatients] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [error, setError] = useState('');

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

  if (loading) {
    return <div className="loading">Loading patients...</div>;
  }

  return (
    <div className="patient-list-container">
      <div className="page-header">
        <h2>Patient Management</h2>
        <button className="btn-primary">
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
              <button className="btn-secondary">View Details</button>
              <button className="btn-predict">Make Prediction</button>
            </div>
          </div>
        ))}
      </div>

      {filteredPatients.length === 0 && (
        <div className="no-results">
          <p>No patients found</p>
        </div>
      )}
    </div>
  );
};

export default PatientList;
