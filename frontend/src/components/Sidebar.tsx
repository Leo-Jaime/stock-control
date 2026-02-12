import { NavLink, useLocation } from 'react-router-dom';
import { useTheme } from '../contexts/ThemeContext';
import { useEffect } from 'react';

interface SidebarProps {
  isOpen: boolean;
  onClose: () => void;
}

const navItems = [
  { to: '/', icon: '📊', label: 'Dashboard' },
  { to: '/products', icon: '📦', label: 'Produtos' },
  { to: '/raw-materials', icon: '🧱', label: 'Matérias-Primas' },
  { to: '/production', icon: '🏭', label: 'Produção' },
];

export default function Sidebar({ isOpen, onClose }: SidebarProps) {
  const { theme, toggleTheme } = useTheme();
  const location = useLocation();

  // Close sidebar on route change (mobile)
  useEffect(() => {
    onClose();
  }, [location.pathname]);

  return (
    <aside className={`sidebar ${isOpen ? 'open' : ''}`}>
      <div className="sidebar-logo">
        <h2>📋 Stock Control</h2>
        <span>Sistema de Controle de Estoque</span>
      </div>

      <nav className="sidebar-nav">
        {navItems.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            end={item.to === '/'}
            className={({ isActive }) => isActive ? 'active' : ''}
          >
            <span className="nav-icon">{item.icon}</span>
            {item.label}
          </NavLink>
        ))}
      </nav>

      <div className="sidebar-footer">
        <button className="theme-toggle" onClick={toggleTheme}>
          <span>{theme === 'light' ? 'Tema Escuro' : 'Tema Claro'}</span>
          <span className="toggle-icon">{theme === 'light' ? '🌙' : '☀️'}</span>
        </button>
      </div>
    </aside>
  );
}
