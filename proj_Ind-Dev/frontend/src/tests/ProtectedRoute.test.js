import React from 'react';
import { render, screen } from '@testing-library/react';
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import ProtectedRoute from './ProtectedRoute';
import { useAuth } from '../contexts/AuthContext';

// Мокаем useAuth
jest.mock('../contexts/AuthContext');

const TestComponent = () => <div>Protected content</div>;

describe('ProtectedRoute', () => {
  afterEach(() => {
    jest.clearAllMocks();
  });

  test('показывает Loading при загрузке', () => {
    useAuth.mockReturnValue({ user: null, loading: true });
    render(
      <ProtectedRoute>
        <TestComponent />
      </ProtectedRoute>
    );
    expect(screen.getByText(/loading/i)).toBeInTheDocument();
  });

  test('редиректит на /login если пользователь не авторизован', () => {
    useAuth.mockReturnValue({ user: null, loading: false });
    render(
      <MemoryRouter initialEntries={['/protected']}>
        <Routes>
          <Route path="/login" element={<div>Login Page</div>} />
          <Route path="/protected" element={<ProtectedRoute><TestComponent /></ProtectedRoute>} />
        </Routes>
      </MemoryRouter>
    );
    expect(screen.getByText(/login page/i)).toBeInTheDocument();
  });

  test('редиректит на / если роль не совпадает', () => {
    useAuth.mockReturnValue({ user: { role: 'USER' }, loading: false });
    render(
      <MemoryRouter initialEntries={['/admin']}>
        <Routes>
          <Route path="/" element={<div>Home Page</div>} />
          <Route path="/admin" element={<ProtectedRoute requiredRole="ADMIN"><TestComponent /></ProtectedRoute>} />
        </Routes>
      </MemoryRouter>
    );
    expect(screen.getByText(/home page/i)).toBeInTheDocument();
  });

  test('рендерит дочерний компонент если пользователь авторизован и роль совпадает', () => {
    useAuth.mockReturnValue({ user: { role: 'ADMIN' }, loading: false });
    render(
      <MemoryRouter initialEntries={['/admin']}>
        <Routes>
          <Route path="/admin" element={<ProtectedRoute requiredRole="ADMIN"><TestComponent /></ProtectedRoute>} />
        </Routes>
      </MemoryRouter>
    );
    expect(screen.getByText(/protected content/i)).toBeInTheDocument();
  });
});
