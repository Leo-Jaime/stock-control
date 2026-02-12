import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import PageHeader from '../components/PageHeader';
import { getProducts, getRawMaterials, calculateProduction, type Product, type RawMaterial, type ProductionReport } from '../api/api';

export default function Dashboard() {
  const navigate = useNavigate();
  const [products, setProducts] = useState<Product[]>([]);
  const [rawMaterials, setRawMaterials] = useState<RawMaterial[]>([]);
  const [report, setReport] = useState<ProductionReport | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([getProducts(), getRawMaterials(), calculateProduction()])
      .then(([prods, mats, rep]) => {
        setProducts(prods);
        setRawMaterials(mats);
        setReport(rep);
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return (
      <>
        <PageHeader title="Dashboard" />
        <div className="loading"><div className="spinner" /> Carregando...</div>
      </>
    );
  }

  const totalStockValue = products.reduce((sum, p) => sum + p.value, 0);
  const lowStockMaterials = rawMaterials.filter((m) => m.stockQuantity <= 10);

  return (
    <>
      <PageHeader title="Dashboard" />

      <div className="stats-grid">
        <div className="stat-card" style={{ cursor: 'pointer' }} onClick={() => navigate('/products')}>
          <div className="stat-icon">📦</div>
          <div className="stat-value">{products.length}</div>
          <div className="stat-label">Produtos Cadastrados</div>
        </div>

        <div className="stat-card" style={{ cursor: 'pointer' }} onClick={() => navigate('/raw-materials')}>
          <div className="stat-icon">🧱</div>
          <div className="stat-value">{rawMaterials.length}</div>
          <div className="stat-label">Matérias-Primas</div>
        </div>

        <div className="stat-card">
          <div className="stat-icon">💰</div>
          <div className="stat-value currency">
            {totalStockValue.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}
          </div>
          <div className="stat-label">Valor Total em Produtos</div>
        </div>

        <div className="stat-card" style={{ cursor: 'pointer' }} onClick={() => navigate('/production')}>
          <div className="stat-icon">🏭</div>
          <div className="stat-value">{report?.totalProducts ?? 0}</div>
          <div className="stat-label">Produtos Produzíveis</div>
        </div>
      </div>

      {lowStockMaterials.length > 0 && (
        <div className="card">
          <h3 style={{ marginBottom: '16px', fontSize: '1rem' }}>⚠️ Estoque Baixo (≤ 10 unidades)</h3>
          <div style={{ display: 'flex', flexWrap: 'wrap', gap: '8px' }}>
            {lowStockMaterials.map((m) => (
              <span key={m.id} className={`badge ${m.stockQuantity === 0 ? 'badge-danger' : 'badge-warning'}`}>
                {m.name}: {m.stockQuantity}
              </span>
            ))}
          </div>
        </div>
      )}

      {report && report.totalProductionValue > 0 && (
        <div className="card" style={{ marginTop: '20px' }}>
          <h3 style={{ marginBottom: '8px', fontSize: '1rem' }}>🏭 Potencial de Produção</h3>
          <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>
            Valor total estimado:{' '}
            <strong style={{ color: 'var(--success)' }}>
              {report.totalProductionValue.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}
            </strong>
          </p>
        </div>
      )}
    </>
  );
}
