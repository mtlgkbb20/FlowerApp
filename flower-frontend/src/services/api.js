import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor - Token ekleme
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor - Hata yönetimi
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Auth API
export const authAPI = {
  register: (data) => api.post('/auth/register', data),
  login: (data) => api.post('/auth/login', data),
  test: () => api.get('/auth/test'),
};

// Preference API
export const preferenceAPI = {
  save: (data) => api.post('/preferences', data),
  getLatest: () => api.get('/preferences/latest'),
  getAll: () => api.get('/preferences/all'),
  generatePrompt: (data) => api.post('/preferences/generate-prompt', data),
};

// Design API
export const designAPI = {
  generate: (data) => api.post('/designs/generate', data),
  getMyDesigns: async () => {
    const response = await api.get('/designs/my-designs');
    return response.data;
  },
};

export default api;