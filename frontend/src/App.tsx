import { useState } from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { ThemeProvider } from './contexts/ThemeContext';
import { ToastProvider } from './contexts/ToastContext';
import Sidebar from './components/Sidebar';
import Toast from './components/Toast';
import Dashboard from './pages/Dashboard';
import Products from './pages/Products';
import ProductDetails from './pages/ProductDetails';
import RawMaterials from './pages/RawMaterials';
import Production from './pages/Production';

export default function App() {
  const [sidebarOpen, setSidebarOpen] = useState(false);

  const closeSidebar = () => setSidebarOpen(false);

  return (
    <ThemeProvider>
      <ToastProvider>
        <BrowserRouter>
          <div className="app-layout">
            {sidebarOpen && (
              <div className="sidebar-overlay visible" onClick={closeSidebar} />
            )}
            <Sidebar isOpen={sidebarOpen} onClose={closeSidebar} />
            <div className="main-content">
              <header className="app-header">
                <button
                  className="mobile-menu-btn"
                  onClick={() => setSidebarOpen(true)}
                  aria-label="Abrir menu"
                >
                  ☰
                </button>
                <h1>Teste Full Stack Java/Quarkus + React</h1>
              </header>

              <div className="page-content">
                <Routes>
                  <Route path="/" element={<Dashboard />} />
                  <Route path="/products" element={<Products />} />
                  <Route path="/products/:id" element={<ProductDetails />} />
                  <Route path="/raw-materials" element={<RawMaterials />} />
                  <Route path="/production" element={<Production />} />
                </Routes>
              </div>

              <footer className="app-footer">
                Desenvolvido por{' '}
                <a href="https://leo-portifolio.vercel.app/" target="_blank" rel="noopener noreferrer">
                  Leo Jaime
                </a>{' '}
                — © {new Date().getFullYear()}
              </footer>
            </div>
          </div>
          <Toast />
        </BrowserRouter>
      </ToastProvider>
    </ThemeProvider>
  );
}
