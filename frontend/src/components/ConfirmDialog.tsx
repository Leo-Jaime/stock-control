import Modal from './Modal';

interface ConfirmDialogProps {
  isOpen: boolean;
  onClose: () => void;
  onConfirm: () => void;
  title?: string;
  message: string;
}

export default function ConfirmDialog({ isOpen, onClose, onConfirm, title = 'Confirmar Exclusão', message }: ConfirmDialogProps) {
  return (
    <Modal isOpen={isOpen} onClose={onClose} title={title}>
      <div className="confirm-content">
        <div className="confirm-icon">⚠️</div>
        <p>{message}</p>
      </div>
      <div className="confirm-actions">
        <button className="btn btn-secondary" onClick={onClose}>Cancelar</button>
        <button className="btn btn-danger" onClick={() => { onConfirm(); onClose(); }}>Excluir</button>
      </div>
    </Modal>
  );
}
