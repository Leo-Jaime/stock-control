import type { ReactNode } from 'react';

export interface Column<T> {
  key: string;
  header: string;
  render?: (item: T) => ReactNode;
}

interface DataTableProps<T> {
  columns: Column<T>[];
  data: T[];
  actions?: (item: T) => ReactNode;
  emptyMessage?: string;
  emptyIcon?: string;
}

export default function DataTable<T>({
  columns,
  data,
  actions,
  emptyMessage = 'Nenhum registro encontrado',
  emptyIcon = '📭',
}: DataTableProps<T>) {
  if (data.length === 0) {
    return (
      <div className="table-container">
        <div className="empty-state">
          <div className="empty-icon">{emptyIcon}</div>
          <p>{emptyMessage}</p>
        </div>
      </div>
    );
  }

  return (
    <div className="table-container">
      <table className="data-table">
        <thead>
          <tr>
            {columns.map((col) => (
              <th key={col.key}>{col.header}</th>
            ))}
            {actions && <th>Ações</th>}
          </tr>
        </thead>
        <tbody>
          {data.map((item, index) => (
            <tr key={(item as Record<string, unknown>).id as string ?? index}>
              {columns.map((col) => (
                <td key={col.key} data-label={col.header}>
                  {col.render
                    ? col.render(item)
                    : String((item as Record<string, unknown>)[col.key] ?? '')}
                </td>
              ))}
              {actions && (
                <td data-label="Ações">
                  <div className="table-actions">{actions(item)}</div>
                </td>
              )}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
