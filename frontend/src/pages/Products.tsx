import { useEffect, useState, type FormEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import PageHeader from '../components/PageHeader';
import DataTable, { type Column } from '../components/DataTable';
import Modal from '../components/Modal';
import ConfirmDialog from '../components/ConfirmDialog';
import { useToast } from '../contexts/ToastContext';
import { getProducts, createProduct, updateProduct, deleteProduct, type Product, type ApiError } from '../api/api';

export default function Products() {
  const navigate = useNavigate();
  const { showSuccess, showError } = useToast();
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);

  // Modal state
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState<Product | null>(null);
  const [form, setForm] = useState({ code: '', name: '', value: '' });
  const [saving, setSaving] = useState(false);

  // Confirm delete state
  const [deleteTarget, setDeleteTarget] = useState<Product | null>(null);

  const loadProducts = () => {
    getProducts()
      .then(setProducts)
      .catch(() => showError('Erro ao carregar produtos'))
      .finally(() => setLoading(false));
  };

  useEffect(() => { loadProducts(); }, []);

  const openCreate = () => {
    setEditing(null);
    setForm({ code: '', name: '', value: '' });
    setModalOpen(true);
  };

  const openEdit = (product: Product) => {
    setEditing(product);
    setForm({ code: product.code, name: product.name, value: String(product.value) });
    setModalOpen(true);
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setSaving(true);

    const data = { code: form.code, name: form.name, value: parseFloat(form.value) };

    try {
      if (editing) {
        await updateProduct(editing.id, data);
        showSuccess('Produto atualizado com sucesso!');
      } else {
        await createProduct(data);
        showSuccess('Produto criado com sucesso!');
      }
      setModalOpen(false);
      loadProducts();
    } catch (err) {
      showError((err as ApiError).message || 'Erro ao salvar produto');
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (product: Product) => {
    try {
      await deleteProduct(product.id);
      showSuccess('Produto excluído com sucesso!');
      loadProducts();
    } catch (err) {
      showError((err as ApiError).message || 'Erro ao excluir produto');
    }
  };

  const columns: Column<Product>[] = [
    { key: 'code', header: 'Código' },
    { key: 'name', header: 'Nome' },
    {
      key: 'value',
      header: 'Valor',
      render: (p) => (
        <span className="currency">
          {p.value.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}
        </span>
      ),
    },
  ];

  if (loading) {
    return (
      <>
        <PageHeader title="Produtos" />
        <div className="loading"><div className="spinner" /> Carregando...</div>
      </>
    );
  }

  return (
    <>
      <PageHeader
        title="Produtos"
        action={
          <button className="btn btn-primary" onClick={openCreate}>
            + Novo Produto
          </button>
        }
      />

      <DataTable
        columns={columns}
        data={products}
        emptyMessage="Nenhum produto cadastrado"
        emptyIcon="📦"
        actions={(product) => (
          <>
            <button className="btn btn-sm btn-secondary" onClick={() => navigate(`/products/${product.id}`)}>
              👁 Detalhes
            </button>
            <button className="btn btn-sm btn-secondary" onClick={() => openEdit(product)}>
              ✏️ Editar
            </button>
            <button className="btn btn-sm btn-danger" onClick={() => setDeleteTarget(product)}>
              🗑
            </button>
          </>
        )}
      />

      {/* Create / Edit Modal */}
      <Modal
        isOpen={modalOpen}
        onClose={() => setModalOpen(false)}
        title={editing ? 'Editar Produto' : 'Novo Produto'}
      >
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Código</label>
            <input
              type="text"
              value={form.code}
              onChange={(e) => setForm({ ...form, code: e.target.value })}
              placeholder="Ex: PROD-001"
              required
            />
          </div>
          <div className="form-group">
            <label>Nome</label>
            <input
              type="text"
              value={form.name}
              onChange={(e) => setForm({ ...form, name: e.target.value })}
              placeholder="Nome do produto"
              required
            />
          </div>
          <div className="form-group">
            <label>Valor (R$)</label>
            <input
              type="number"
              step="0.01"
              min="0.01"
              value={form.value}
              onChange={(e) => setForm({ ...form, value: e.target.value })}
              placeholder="0.00"
              required
            />
          </div>
          <div className="modal-footer">
            <button type="button" className="btn btn-secondary" onClick={() => setModalOpen(false)}>
              Cancelar
            </button>
            <button type="submit" className="btn btn-primary" disabled={saving}>
              {saving ? 'Salvando...' : editing ? 'Atualizar' : 'Criar'}
            </button>
          </div>
        </form>
      </Modal>

      {/* Delete Confirmation */}
      <ConfirmDialog
        isOpen={!!deleteTarget}
        onClose={() => setDeleteTarget(null)}
        onConfirm={() => deleteTarget && handleDelete(deleteTarget)}
        message={`Deseja excluir o produto "${deleteTarget?.name}"? Esta ação não pode ser desfeita.`}
      />
    </>
  );
}
