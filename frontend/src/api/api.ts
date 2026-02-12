// === Types ===

export interface Product {
  id: number;
  code: string;
  name: string;
  value: number;
}

export interface RawMaterial {
  id: number;
  code: string;
  name: string;
  stockQuantity: number;
}

export interface ProductRawMaterial {
  id: number;
  productId: number;
  rawMaterialId: number;
  rawMaterialName: string;
  rawMaterialCode: string;
  requiredQuantity: number;
}

export interface ProductionSuggestion {
  productId: number;
  productCode: string;
  productName: string;
  productValue: number;
  quantityCanProduce: number;
  totalValue: number;
}

export interface ProductionReport {
  suggestions: ProductionSuggestion[];
  totalProductionValue: number;
  totalProducts: number;
}

export interface ApiError {
  message: string;
  status: number;
}


const BASE = import.meta.env.VITE_API_URL || '/api';

function getAuthHeaders(): Record<string, string> {
  const token = localStorage.getItem('token');
  const headers: Record<string, string> = { 'Content-Type': 'application/json' };
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }
  return headers;
}

async function request<T>(url: string, options?: RequestInit): Promise<T> {
  let res: Response;
  try {
    res = await fetch(`${BASE}${url}`, {
      headers: getAuthHeaders(),
      ...options,
    });
  } catch {
    throw {
      message: 'Não foi possível conectar ao servidor. Verifique se o backend está rodando.',
      status: 0,
    } as ApiError;
  }

  if (res.status === 401) {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    window.location.href = '/login';
    throw { message: 'Sessão expirada. Faça login novamente.', status: 401 } as ApiError;
  }

  if (!res.ok) {
    const error: ApiError = await res.json().catch(() => ({
      message: `Erro ${res.status}: ${res.statusText}`,
      status: res.status,
    }));
    throw error;
  }

  if (res.status === 204) return undefined as T;
  return res.json();
}

// === Produtos ===

export const getProducts = () =>
  request<Product[]>('/products');

export const getProduct = (id: number) =>
  request<Product>(`/products/${id}`);

export const createProduct = (data: Omit<Product, 'id'>) =>
  request<Product>('/products', { method: 'POST', body: JSON.stringify(data) });

export const updateProduct = (id: number, data: Omit<Product, 'id'>) =>
  request<Product>(`/products/${id}`, { method: 'PUT', body: JSON.stringify(data) });

export const deleteProduct = (id: number) =>
  request<void>(`/products/${id}`, { method: 'DELETE' });

// === Matérias-Primas ===

export const getRawMaterials = () =>
  request<RawMaterial[]>('/raw-materials');

export const getRawMaterial = (id: number) =>
  request<RawMaterial>(`/raw-materials/${id}`);

export const createRawMaterial = (data: Omit<RawMaterial, 'id'>) =>
  request<RawMaterial>('/raw-materials', { method: 'POST', body: JSON.stringify(data) });

export const updateRawMaterial = (id: number, data: Omit<RawMaterial, 'id'>) =>
  request<RawMaterial>(`/raw-materials/${id}`, { method: 'PUT', body: JSON.stringify(data) });

export const deleteRawMaterial = (id: number) =>
  request<void>(`/raw-materials/${id}`, { method: 'DELETE' });

// === Matérias-Primas dos Produtos ===

export const getProductRawMaterials = (productId: number) =>
  request<ProductRawMaterial[]>(`/products/${productId}/raw-materials`);

export const addProductRawMaterial = (productId: number, data: { rawMaterialId: number; requiredQuantity: number }) =>
  request<ProductRawMaterial>(`/products/${productId}/raw-materials`, { method: 'POST', body: JSON.stringify(data) });

export const updateProductRawMaterial = (productId: number, assocId: number, data: { requiredQuantity: number }) =>
  request<ProductRawMaterial>(`/products/${productId}/raw-materials/${assocId}`, { method: 'PUT', body: JSON.stringify(data) });

export const removeProductRawMaterial = (productId: number, assocId: number) =>
  request<void>(`/products/${productId}/raw-materials/${assocId}`, { method: 'DELETE' });

// === Produção ===

export const calculateProduction = () =>
  request<ProductionReport>('/production/calculate');
