import { useState } from 'react';
import api from '../../services/api';
import PredictionResult from './PredictionResult';
import { Activity, AlertCircle } from 'lucide-react';
import './PredictionForm.css';

const PredictionForm = () => {
  const [formData, setFormData] = useState({
    subjectId: '',
    hadmId: '',
    icuStayId: '',
    age: '',
    gender: 'M',
    los: '',
    insurance: 'Medicare',
    maritalStatus: 'MARRIED',
    glucoseMin: '',
    glucoseMax: '',
    glucoseMean: '',
    calciumMin: '',
    calciumMax: '',
    calciumMean: '',
    albuminMin: '',
    albuminMax: '',
    albuminMean: '',
    plateletMin: '',
    plateletMax: '',
    plateletMean: '',
    magnesiumMin: '',
    magnesiumMax: '',
    magnesiumMean: '',
    ureaMin: '',
    ureaMax: '',
    ureaMean: '',
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [result, setResult] = useState(null);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setResult(null);

    try {
      // Convert string values to numbers
      const payload = {
        ...formData,
        subjectId: parseInt(formData.subjectId),
        hadmId: parseInt(formData.hadmId),
        icuStayId: parseInt(formData.icuStayId),
        age: parseFloat(formData.age),
        los: parseFloat(formData.los),
        glucoseMin: parseFloat(formData.glucoseMin),
        glucoseMax: parseFloat(formData.glucoseMax),
        glucoseMean: parseFloat(formData.glucoseMean),
        calciumMin: parseFloat(formData.calciumMin),
        calciumMax: parseFloat(formData.calciumMax),
        calciumMean: parseFloat(formData.calciumMean),
        albuminMin: parseFloat(formData.albuminMin),
        albuminMax: parseFloat(formData.albuminMax),
        albuminMean: parseFloat(formData.albuminMean),
        plateletMin: parseFloat(formData.plateletMin),
        plateletMax: parseFloat(formData.plateletMax),
        plateletMean: parseFloat(formData.plateletMean),
        magnesiumMin: parseFloat(formData.magnesiumMin),
        magnesiumMax: parseFloat(formData.magnesiumMax),
        magnesiumMean: parseFloat(formData.magnesiumMean),
        ureaMin: parseFloat(formData.ureaMin),
        ureaMax: parseFloat(formData.ureaMax),
        ureaMean: parseFloat(formData.ureaMean),
      };

      const response = await api.post('/predictions/predict', payload);
      setResult(response.data);
    } catch (err) {
      setError(err.response?.data?.error || 'Prediction failed. Please try again.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  if (result) {
    return <PredictionResult result={result} onReset={() => setResult(null)} />;
  }

  return (
    <div className="prediction-form-container">
      <div className="page-header">
        <div className="header-content">
          <Activity size={32} className="header-icon" />
          <div>
            <h2>Make Prediction</h2>
            <p>Enter patient data to predict readmission risk</p>
          </div>
        </div>
      </div>

      {error && (
        <div className="error-alert">
          <AlertCircle size={20} />
          <span>{error}</span>
        </div>
      )}

      <form onSubmit={handleSubmit} className="prediction-form">
        {/* Patient Information */}
        <div className="form-section">
          <h3>Patient Information</h3>
          <div className="form-grid">
            <div className="form-group">
              <label>Subject ID *</label>
              <input
                type="number"
                name="subjectId"
                value={formData.subjectId}
                onChange={handleChange}
                required
              />
            </div>
            <div className="form-group">
              <label>Hospital Admission ID *</label>
              <input
                type="number"
                name="hadmId"
                value={formData.hadmId}
                onChange={handleChange}
                required
              />
            </div>
            <div className="form-group">
              <label>ICU Stay ID *</label>
              <input
                type="number"
                name="icuStayId"
                value={formData.icuStayId}
                onChange={handleChange}
                required
              />
            </div>
            <div className="form-group">
              <label>Age *</label>
              <input
                type="number"
                name="age"
                value={formData.age}
                onChange={handleChange}
                step="0.1"
                required
              />
            </div>
            <div className="form-group">
              <label>Gender *</label>
              <select name="gender" value={formData.gender} onChange={handleChange} required>
                <option value="M">Male</option>
                <option value="F">Female</option>
              </select>
            </div>
            <div className="form-group">
              <label>Length of Stay (days) *</label>
              <input
                type="number"
                name="los"
                value={formData.los}
                onChange={handleChange}
                step="0.1"
                required
              />
            </div>
            <div className="form-group">
              <label>Insurance *</label>
              <select name="insurance" value={formData.insurance} onChange={handleChange} required>
                <option value="Medicare">Medicare</option>
                <option value="Medicaid">Medicaid</option>
                <option value="Private">Private</option>
                <option value="Government">Government</option>
                <option value="Self Pay">Self Pay</option>
              </select>
            </div>
            <div className="form-group">
              <label>Marital Status *</label>
              <select name="maritalStatus" value={formData.maritalStatus} onChange={handleChange} required>
                <option value="MARRIED">Married</option>
                <option value="SINGLE">Single</option>
                <option value="WIDOWED">Widowed</option>
                <option value="DIVORCED">Divorced</option>
              </select>
            </div>
          </div>
        </div>

        {/* Lab Values - Glucose */}
        <div className="form-section">
          <h3>Glucose Levels</h3>
          <div className="form-grid">
            <div className="form-group">
              <label>Min *</label>
              <input type="number" name="glucoseMin" value={formData.glucoseMin} onChange={handleChange} step="0.1" required />
            </div>
            <div className="form-group">
              <label>Max *</label>
              <input type="number" name="glucoseMax" value={formData.glucoseMax} onChange={handleChange} step="0.1" required />
            </div>
            <div className="form-group">
              <label>Mean *</label>
              <input type="number" name="glucoseMean" value={formData.glucoseMean} onChange={handleChange} step="0.1" required />
            </div>
          </div>
        </div>

        {/* Lab Values - Calcium */}
        <div className="form-section">
          <h3>Calcium Levels</h3>
          <div className="form-grid">
            <div className="form-group">
              <label>Min *</label>
              <input type="number" name="calciumMin" value={formData.calciumMin} onChange={handleChange} step="0.1" required />
            </div>
            <div className="form-group">
              <label>Max *</label>
              <input type="number" name="calciumMax" value={formData.calciumMax} onChange={handleChange} step="0.1" required />
            </div>
            <div className="form-group">
              <label>Mean *</label>
              <input type="number" name="calciumMean" value={formData.calciumMean} onChange={handleChange} step="0.1" required />
            </div>
          </div>
        </div>

        {/* Continue for other lab values... */}
        {/* Albumin */}
        <div className="form-section">
          <h3>Albumin Levels</h3>
          <div className="form-grid">
            <div className="form-group">
              <label>Min *</label>
              <input type="number" name="albuminMin" value={formData.albuminMin} onChange={handleChange} step="0.1" required />
            </div>
            <div className="form-group">
              <label>Max *</label>
              <input type="number" name="albuminMax" value={formData.albuminMax} onChange={handleChange} step="0.1" required />
            </div>
            <div className="form-group">
              <label>Mean *</label>
              <input type="number" name="albuminMean" value={formData.albuminMean} onChange={handleChange} step="0.1" required />
            </div>
          </div>
        </div>

        {/* Platelet */}
        <div className="form-section">
          <h3>Platelet Count</h3>
          <div className="form-grid">
            <div className="form-group">
              <label>Min *</label>
              <input type="number" name="plateletMin" value={formData.plateletMin} onChange={handleChange} step="0.1" required />
            </div>
            <div className="form-group">
              <label>Max *</label>
              <input type="number" name="plateletMax" value={formData.plateletMax} onChange={handleChange} step="0.1" required />
            </div>
            <div className="form-group">
              <label>Mean *</label>
              <input type="number" name="plateletMean" value={formData.plateletMean} onChange={handleChange} step="0.1" required />
            </div>
          </div>
        </div>

        {/* Magnesium */}
        <div className="form-section">
          <h3>Magnesium Levels</h3>
          <div className="form-grid">
            <div className="form-group">
              <label>Min *</label>
              <input type="number" name="magnesiumMin" value={formData.magnesiumMin} onChange={handleChange} step="0.1" required />
            </div>
            <div className="form-group">
              <label>Max *</label>
              <input type="number" name="magnesiumMax" value={formData.magnesiumMax} onChange={handleChange} step="0.1" required />
            </div>
            <div className="form-group">
              <label>Mean *</label>
              <input type="number" name="magnesiumMean" value={formData.magnesiumMean} onChange={handleChange} step="0.1" required />
            </div>
          </div>
        </div>

        {/* Urea */}
        <div className="form-section">
          <h3>Urea Levels</h3>
          <div className="form-grid">
            <div className="form-group">
              <label>Min *</label>
              <input type="number" name="ureaMin" value={formData.ureaMin} onChange={handleChange} step="0.1" required />
            </div>
            <div className="form-group">
              <label>Max *</label>
              <input type="number" name="ureaMax" value={formData.ureaMax} onChange={handleChange} step="0.1" required />
            </div>
            <div className="form-group">
              <label>Mean *</label>
              <input type="number" name="ureaMean" value={formData.ureaMean} onChange={handleChange} step="0.1" required />
            </div>
          </div>
        </div>

        <div className="form-actions">
          <button type="submit" className="btn-submit" disabled={loading}>
            {loading ? 'Processing...' : 'Generate Prediction'}
          </button>
        </div>
      </form>
    </div>
  );
};

export default PredictionForm;
