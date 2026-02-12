import type { ReactNode } from 'react';

interface PageHeaderProps {
  title: string;
  action?: ReactNode;
}

export default function PageHeader({ title, action }: PageHeaderProps) {
  return (
    <div className="page-header">
      <h2>{title}</h2>
      {action}
    </div>
  );
}
