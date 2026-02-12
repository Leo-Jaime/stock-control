import { useEffect, useState, type FormEvent } from 'react';
import PageHeader from '../components/PageHeader';
import DataTable, { type Column } from '../components/DataTable';
import Modal from '../components/Modal';
import ConfirmDialog from '../components/ConfirmDialog';
import { useToast } from '../contexts/ToastContext';
import { getRawMaterials, createRawMaterial, updateRawMaterial, deleteRawMaterial, type RawMaterial, type ApiError } from '../api/api';

export default function RawMaterials() {
  const { showSuccess, showError } = useToast();
  const [materials, setMaterials] = useState<RawMaterial[]>([]);
  const [loading, setLoading] = useState(true);

  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState<RawMaterial | null>(null);
  const [form, setForm] = useState({ code: '', name: '', stockQuantity: '' });
  const [saving, setSaving] = useState(false);

  const [deleteTarget, setDeleteTarget] = useState<RawMaterial | null>(null);

  const loadMaterials = () => {
    getRawMaterials()
      .then(setMaterials)
      .catch(() => showError('Erro ao carregar matérias-primas'))
      .finally(() => setLoading(false));
  };

  useEffect(() => { loadMaterials(); }, []);

  const openCreate = () => {
    setEditing(null);
    setForm({ code: '', name: '', stockQuantity: '' });
    setModalOpen(true);
  };

  const openEdit = (material: RawMaterial) => {
    setEditing(material);
    setForm({ code: material.code, name: material.name, stockQuantity: String(material.stockQuantity) });
    setModalOpen(true);
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setSaving(true);

    const data = { code: form.code, name: form.name, stockQuantity: parseInt(form.stockQuantity) };

    try {
      if (editing) {
        await updateRawMaterial(editing.id, data);
        showSuccess('Matéria-prima atualizada com sucesso!');
      } else {
        await createRawMaterial(data);
        showSuccess('Matéria-prima criada com sucesso!');
      }
      setModalOpen(false);
      loadMaterials();
    } catch (err) {
      showError((err as ApiError).message || 'Erro ao salvar matéria-prima');
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (material: RawMaterial) => {
    try {
      await deleteRawMaterial(material.id);
      showSuccess('Matéria-prima excluída com sucesso!');
      loadMaterials();
    } catch (err) {
      showError((err as ApiError).message || 'Erro ao excluir matéria-prima');
    }
  };

  const getStockBadge = (qty: number) => {
    if (qty === 0) return <span className="badge badge-danger">Sem estoque</span>;
    if (qty <= 10) return <span className="badge badge-warning">{qty} unid.</span>;
    return <span className="badge badge-success">{qty} unid.</span>;
  };

  const columns: Column<RawMaterial>[] = [
    { key: 'code', header: 'Código' },
    { key: 'name', header: 'Nome' },
    {
      key: 'stockQuantity',
      header: 'Estoque',
      render: (m) => getStockBadge(m.stockQuantity),
    },
  ];

  if (loading) {
    return (
      <>
        <PageHeader title="Matérias-Primas" />
        <div className="loading"><div className="spinner" /> Carregando...</div>
      </>
    );
  }

  return (
    <>
      <PageHeader
        title="Matérias-Primas"
        action={
          <button className="btn btn-primary" onClick={openCreate}>
            + Nova Matéria-Prima
          </button>
        }
      />

      <DataTable
        columns={columns}
        data={materials}
        emptyMessage="Nenhuma matéria-prima cadastrada"
        emptyIcon="🧱"
        actions={(material) => (
          <>
            <button className="btn btn-sm btn-secondary" onClick={() => openEdit(material)}>
              ✏️ Editar
            </button>
            <button className="btn btn-sm btn-danger" onClick={() => setDeleteTarget(material)}>
              🗑
            </button>
          </>
        )}
      />

      <Modal
        isOpen={modalOpen}
        onClose={() => setModalOpen(false)}
        title={editing ? 'Editar Matéria-Prima' : 'Nova Matéria-Prima'}
      >
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Código</label>
            <input
              type="text"
              value={form.code}
              onChange={(e) => setForm({ ...form, code: e.target.value })}
              placeholder="Ex: MP-001"
              required
            />
          </div>
          <div className="form-group">
            <label>Nome</label>
            <input
              type="text"
              value={form.name}
              onChange={(e) => setForm({ ...form, name: e.target.value })}
              placeholder="Nome da matéria-prima"
              required
            />
          </div>
          <div className="form-group">
            <label>Quantidade em Estoque</label>
            <input
              type="number"
              min="0"
              value={form.stockQuantity}
              onChange={(e) => setForm({ ...form, stockQuantity: e.target.value })}
              placeholder="0"
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

      <ConfirmDialog
        isOpen={!!deleteTarget}
        onClose={() => setDeleteTarget(null)}
        onConfirm={() => deleteTarget && handleDelete(deleteTarget)}
        message={`Deseja excluir a matéria-prima "${deleteTarget?.name}"? Esta ação não pode ser desfeita.`}
      />
    </>
  );
}
