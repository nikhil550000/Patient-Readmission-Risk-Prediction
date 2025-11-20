import { useState, useEffect } from 'react';
import api from '../../services/api';
import { Calendar, TrendingUp, TrendingDown } from 'lucide-react';
import './PredictionHistory.css';

const PredictionHistory = () => {
  const [predictions, setPredictions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchPredictions();
  }, []);

  const fetchPredictions = async () => {
    try {
      setLoading(true);
      const response = await api.get('/predictions/recent');
      setPredictions(response.data);
      setError('');
    } catch (err) {
      setError('Failed to fetch prediction history');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleString('en-US', {
      month: 'short',
      day: 'numeric',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  if (loading) {
    return <div className="loading">Loading prediction history...</div>;
  }

  return (
    <div className="history-container">
      <div className="page-header">
        <h2>Prediction History</h2>
        <p>View all past predictions and their outcomes</p>
      </div>

      {error && <div className="error-banner">{error}</div>}

      {predictions.length === 0 ? (
        <div className="no-data">
          <p>No predictions found</p>
        </div>
      ) : (
        <div className="history-table">
          <table>
            <thead>
              <tr>
                <th>Date</th>
                <th>Patient ID</th>
                <th>Age</th>
                <th>Gender</th>
                <th>Risk Level</th>
                <th>Probability</th>
                <th>Prediction</th>
              </tr>
            </thead>
            <tbody>
              {predictions.map((prediction) => (
                <tr key={prediction.id}>
                  <td>
                    <div className="date-cell">
                      <Calendar size={16} />
                      {formatDate(prediction.createdAt)}
                    </div>
                  </td>
                  <td>{prediction.subjectId}</td>
                  <td>{prediction.age?.toFixed(0)} yrs</td>
                  <td>{prediction.gender === 'M' ? 'Male' : 'Female'}</td>
                  <td>
                    <span className={`risk-badge risk-${prediction.riskLevel?.toLowerCase()}`}>
                      {prediction.riskLevel}
                    </span>
                  </td>
                  <td>
                    <div className="probability-cell">
                      {(prediction.readmissionProbability * 100).toFixed(1)}%
                    </div>
                  </td>
                  <td>
                    <div className={`prediction-cell ${prediction.willReadmit ? 'will-readmit' : 'no-readmit'}`}>
                      {prediction.willReadmit ? (
                        <>
                          <TrendingUp size={16} />
                          Will Readmit
                        </>
                      ) : (
                        <>
                          <TrendingDown size={16} />
                          No Readmission
                        </>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};

export default PredictionHistory;
