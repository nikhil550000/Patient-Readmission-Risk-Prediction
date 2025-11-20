import { Menu } from 'lucide-react';
import './Header.css';

const Header = ({ toggleSidebar }) => {
  return (
    <header className="dashboard-header">
      <button onClick={toggleSidebar} className="menu-toggle">
        <Menu size={24} />
      </button>
      <h1>Patient Readmission Risk Prediction</h1>
    </header>
  );
};

export default Header;
