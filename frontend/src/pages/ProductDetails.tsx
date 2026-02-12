import { useEffect, useState, type FormEvent } from 'react';
import { useParams, Link } from 'react-router-dom';
import PageHeader from '../components/PageHeader';
import DataTable, { type Column } from '../components/DataTable';
import Modal from '../components/Modal';
import ConfirmDialog from '../components/ConfirmDialog';
import { useToast } from '../contexts/ToastContext';
import {
  getProduct,
  getProductRawMaterials,
  getRawMaterials,
  addProductRawMaterial,
  updateProductRawMaterial,
  removeProductRawMaterial,
  type Product,
  type ProductRawMaterial,
  type RawMaterial,
  type ApiError,
} from '../api/api';

export default function ProductDetails() {
  const { id } = useParams<{ id: string }>();
  const productId = Number(id);
  const { showSuccess, showError } = useToast();

  const [product, setProduct] = useState<Product | null>(null);
  const [associations, setAssociations] = useState<ProductRawMaterial[]>([]);
  const [allMaterials, setAllMaterials] = useState<RawMaterial[]>([]);
  const [loading, setLoading] = useState(true);

  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState<ProductRawMaterial | null>(null);
  const [form, setForm] = useState({ rawMaterialId: '', requiredQuantity: '' });
  const [saving, setSaving] = useState(false);

  const [deleteTarget, setDeleteTarget] = useState<ProductRawMaterial | null>(null);

  const loadData = () => {
    Promise.all([getProduct(productId), getProductRawMaterials(productId), getRawMaterials()])
      .then(([prod, assocs, mats]) => {
        setProduct(prod);
        setAssociations(assocs);
        setAllMaterials(mats);
      })
      .catch(() => showError('Erro ao carregar dados'))
      .finally(() => setLoading(false));
  };

  useEffect(() => { loadData(); }, [productId]);

  const availableMaterials = allMaterials.filter(
    (m) => !associations.some((a) => a.rawMaterialId === m.id)
  );

  const openAdd = () => {
    setEditing(null);
    setForm({ rawMaterialId: '', requiredQuantity: '' });
    setModalOpen(true);
  };

  const openEdit = (assoc: ProductRawMaterial) => {
    setEditing(assoc);
    setForm({ rawMaterialId: String(assoc.rawMaterialId), requiredQuantity: String(assoc.requiredQuantity) });
    setModalOpen(true);
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setSaving(true);

    try {
      if (editing) {
        await updateProductRawMaterial(productId, editing.id, {
          requiredQuantity: parseInt(form.requiredQuantity),
        });
        showSuccess('Associação atualizada com sucesso!');
      } else {
        await addProductRawMaterial(productId, {
          rawMaterialId: parseInt(form.rawMaterialId),
          requiredQuantity: parseInt(form.requiredQuantity),
        });
        showSuccess('Matéria-prima associada com sucesso!');
      }
      setModalOpen(false);
      loadData();
    } catch (err) {
      showError((err as ApiError).message || 'Erro ao salvar associação');
    } finally {
      setSaving(false);
    }
  };

  const handleRemove = async (assoc: ProductRawMaterial) => {
    try {
      await removeProductRawMaterial(productId, assoc.id);
      showSuccess('Associação removida com sucesso!');
      loadData();
    } catch (err) {
      showError((err as ApiError).message || 'Erro ao remover associação');
    }
  };

  const columns: Column<ProductRawMaterial>[] = [
    { key: 'rawMaterialCode', header: 'Código' },
    { key: 'rawMaterialName', header: 'Matéria-Prima' },
    {
      key: 'requiredQuantity',
      header: 'Qtd. Necessária',
      render: (a) => <strong>{a.requiredQuantity} unid.</strong>,
    },
  ];

  if (loading) {
    return (
      <>
        <Link to="/products" className="back-link">← Voltar para Produtos</Link>
        <div className="loading"><div className="spinner" /> Carregando...</div>
      </>
    );
  }

  if (!product) {
    return (
      <>
        <Link to="/products" className="back-link">← Voltar para Produtos</Link>
        <div className="empty-state">
          <div className="empty-icon">❌</div>
          <p>Produto não encontrado</p>
        </div>
      </>
    );
  }

  return (
    <>
      <Link to="/products" className="back-link">← Voltar para Produtos</Link>

      <PageHeader title={product.name} />

      <div className="product-info">
        <div className="info-item">
          <div className="info-label">Código</div>
          <div className="info-value">{product.code}</div>
        </div>
        <div className="info-item">
          <div className="info-label">Nome</div>
          <div className="info-value">{product.name}</div>
        </div>
        <div className="info-item">
          <div className="info-label">Valor</div>
          <div className="info-value currency">
            {product.value.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}
          </div>
        </div>
        <div className="info-item">
          <div className="info-label">Matérias-Primas</div>
          <div className="info-value">{associations.length}</div>
        </div>
      </div>

      <PageHeader
        title="Matérias-Primas Associadas"
        action={
          <button className="btn btn-primary" onClick={openAdd} disabled={availableMaterials.length === 0}>
            + Associar Matéria-Prima
          </button>
        }
      />

      <DataTable
        columns={columns}
        data={associations}
        emptyMessage="Nenhuma matéria-prima associada a este produto"
        emptyIcon="🔗"
        actions={(assoc) => (
          <>
            <button className="btn btn-sm btn-secondary" onClick={() => openEdit(assoc)}>
              ✏️ Editar
            </button>
            <button className="btn btn-sm btn-danger" onClick={() => setDeleteTarget(assoc)}>
              🗑
            </button>
          </>
        )}
      />

      <Modal
        isOpen={modalOpen}
        onClose={() => setModalOpen(false)}
        title={editing ? 'Editar Quantidade' : 'Associar Matéria-Prima'}
      >
        <form onSubmit={handleSubmit}>
          {!editing && (
            <div className="form-group">
              <label>Matéria-Prima</label>
              <select
                value={form.rawMaterialId}
                onChange={(e) => setForm({ ...form, rawMaterialId: e.target.value })}
                required
              >
                <option value="">Selecione...</option>
                {availableMaterials.map((m) => (
                  <option key={m.id} value={m.id}>
                    {m.code} — {m.name} (estoque: {m.stockQuantity})
                  </option>
                ))}
              </select>
            </div>
          )}
          <div className="form-group">
            <label>Quantidade Necessária</label>
            <input
              type="number"
              min="1"
              value={form.requiredQuantity}
              onChange={(e) => setForm({ ...form, requiredQuantity: e.target.value })}
              placeholder="Quantidade por unidade do produto"
              required
            />
          </div>
          <div className="modal-footer">
            <button type="button" className="btn btn-secondary" onClick={() => setModalOpen(false)}>
              Cancelar
            </button>
            <button type="submit" className="btn btn-primary" disabled={saving}>
              {saving ? 'Salvando...' : editing ? 'Atualizar' : 'Associar'}
            </button>
          </div>
        </form>
      </Modal>

      <ConfirmDialog
        isOpen={!!deleteTarget}
        onClose={() => setDeleteTarget(null)}
        onConfirm={() => deleteTarget && handleRemove(deleteTarget)}
        message={`Deseja remover a matéria-prima "${deleteTarget?.rawMaterialName}" deste produto?`}
      />
    </>
  );
}
