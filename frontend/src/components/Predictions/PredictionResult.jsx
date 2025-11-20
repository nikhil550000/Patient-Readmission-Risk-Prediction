import { CheckCircle, XCircle, ArrowLeft, TrendingUp, AlertTriangle } from 'lucide-react';
import './PredictionResult.css';

const PredictionResult = ({ result, onReset }) => {
  const getRiskColor = (probability) => {
    if (probability < 0.3) return '#10b981'; // Green
    if (probability < 0.7) return '#f59e0b'; // Orange
    return '#ef4444'; // Red
  };

  const getRiskLevel = (probability) => {
    if (probability < 0.3) return 'LOW';
    if (probability < 0.7) return 'MEDIUM';
    return 'HIGH';
  };

  return (
    <div className="prediction-result-container">
      <button onClick={onReset} className="back-button">
        <ArrowLeft size={18} />
        Make Another Prediction
      </button>

      <div className="result-card">
        <div className="result-header">
          <h2>Prediction Result</h2>
          <p>Patient ID: {result.subjectId}</p>
        </div>

        <div className="result-main">
          <div className={`prediction-badge ${result.willReadmit ? 'high-risk' : 'low-risk'}`}>
            {result.willReadmit ? (
              <>
                <AlertTriangle size={48} />
                <h3>High Risk of Readmission</h3>
              </>
            ) : (
              <>
                <CheckCircle size={48} />
                <h3>Low Risk of Readmission</h3>
              </>
            )}
          </div>

          <div className="probability-section">
            <h4>Readmission Probability</h4>
            <div className="probability-bar-container">
              <div 
                className="probability-bar"
                style={{ 
                  width: `${result.readmissionProbability * 100}%`,
                  backgroundColor: getRiskColor(result.readmissionProbability)
                }}
              >
                <span className="probability-text">
                  {(result.readmissionProbability * 100).toFixed(1)}%
                </span>
              </div>
            </div>
            <div className="risk-level-badge" style={{ backgroundColor: getRiskColor(result.readmissionProbability) }}>
              {getRiskLevel(result.readmissionProbability)} RISK
            </div>
          </div>

          <div className="probability-breakdown">
            <div className="breakdown-item">
              <div className="breakdown-label">
                <XCircle size={20} color="#10b981" />
                <span>No Readmission</span>
              </div>
              <div className="breakdown-value">
                {(result.noReadmissionProbability * 100).toFixed(1)}%
              </div>
            </div>
            <div className="breakdown-item">
              <div className="breakdown-label">
                <AlertTriangle size={20} color="#ef4444" />
                <span>Will Readmit</span>
              </div>
              <div className="breakdown-value">
                {(result.readmissionProbability * 100).toFixed(1)}%
              </div>
            </div>
          </div>
        </div>

        <div className="result-recommendations">
          <h4>
            <TrendingUp size={20} />
            Clinical Recommendations
          </h4>
          {result.willReadmit ? (
            <ul>
              <li>Consider extended monitoring period</li>
              <li>Schedule follow-up appointment within 7 days</li>
              <li>Review medication adherence plan</li>
              <li>Assess home care support needs</li>
              <li>Arrange telehealth check-ins</li>
            </ul>
          ) : (
            <ul>
              <li>Standard discharge protocol recommended</li>
              <li>Schedule routine follow-up in 2-4 weeks</li>
              <li>Provide standard post-discharge instructions</li>
              <li>Ensure patient has emergency contact information</li>
            </ul>
          )}
        </div>
      </div>
    </div>
  );
};

export default PredictionResult;
