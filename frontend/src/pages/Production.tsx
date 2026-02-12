import { useState } from 'react';
import PageHeader from '../components/PageHeader';
import DataTable, { type Column } from '../components/DataTable';
import { useToast } from '../contexts/ToastContext';
import { calculateProduction, type ProductionReport, type ProductionSuggestion, type ApiError } from '../api/api';

export default function Production() {
  const { showSuccess, showError } = useToast();
  const [report, setReport] = useState<ProductionReport | null>(null);
  const [loading, setLoading] = useState(false);

  const handleCalculate = async () => {
    setLoading(true);
    try {
      const data = await calculateProduction();
      setReport(data);
      showSuccess('Cálculo de produção realizado!');
    } catch (err) {
      showError((err as ApiError).message || 'Erro ao calcular produção');
    } finally {
      setLoading(false);
    }
  };

  const columns: Column<ProductionSuggestion>[] = [
    { key: 'productCode', header: 'Código' },
    { key: 'productName', header: 'Produto' },
    {
      key: 'productValue',
      header: 'Valor Unit.',
      render: (s) => (
        <span className="currency">
          {s.productValue.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}
        </span>
      ),
    },
    {
      key: 'quantityCanProduce',
      header: 'Qtd. Produzível',
      render: (s) => <strong>{s.quantityCanProduce} unid.</strong>,
    },
    {
      key: 'totalValue',
      header: 'Valor Total',
      render: (s) => (
        <span className="currency" style={{ color: 'var(--success)', fontWeight: 600 }}>
          {s.totalValue.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}
        </span>
      ),
    },
  ];

  return (
    <>
      <PageHeader
        title="Plano de Produção"
        action={
          <button className="btn btn-primary" onClick={handleCalculate} disabled={loading}>
            {loading ? '⏳ Calculando...' : '🏭 Calcular Produção'}
          </button>
        }
      />

      {!report && !loading && (
        <div className="card" style={{ textAlign: 'center', padding: '60px' }}>
          <div style={{ fontSize: '3rem', marginBottom: '16px' }}>🏭</div>
          <p style={{ color: 'var(--text-secondary)', fontSize: '1rem', marginBottom: '20px' }}>
            Clique em "Calcular Produção" para gerar o plano baseado no estoque atual
          </p>
          <button className="btn btn-primary" onClick={handleCalculate} disabled={loading}>
            🏭 Calcular Produção
          </button>
        </div>
      )}

      {loading && (
        <div className="loading"><div className="spinner" /> Calculando plano de produção...</div>
      )}

      {report && !loading && (
        <>
          <div className="production-summary">
            <div className="summary-card">
              <div className="summary-value">{report.totalProducts}</div>
              <div className="summary-label">Produtos Produzíveis</div>
            </div>
            <div className="summary-card">
              <div className="summary-value currency">
                {report.totalProductionValue.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}
              </div>
              <div className="summary-label">Valor Total Estimado</div>
            </div>
          </div>

          <DataTable
            columns={columns}
            data={report.suggestions}
            emptyMessage="Nenhum produto pode ser produzido com o estoque atual"
            emptyIcon="📭"
          />
        </>
      )}
    </>
  );
}
