import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import AddBook from './AddBook';
import { useAuth } from '../contexts/AuthContext';
import axios from 'axios';

jest.mock('../contexts/AuthContext');
jest.mock('axios');

describe('AddBook', () => {
  beforeEach(() => {
    useAuth.mockReturnValue({
      user: { id: 1, name: 'Test User' },
      token: 'fake-token',
    });
  });

  test('рендерит форму и добавляет книгу успешно', async () => {
    axios.post.mockResolvedValueOnce({ data: { /* ответ сервера */ } });

    render(<AddBook />);

    fireEvent.change(screen.getByLabelText(/название книги/i), { target: { value: 'Новая книга' } });
    fireEvent.change(screen.getByLabelText(/автор/i), { target: { value: 'Автор' } });
    fireEvent.change(screen.getByLabelText(/жанр/i), { target: { value: 'Фантастика' } });

    fireEvent.click(screen.getByRole('button', { name: /добавить книгу/i }));

    await waitFor(() => {
      expect(screen.getByText(/книга успешно добавлена/i)).toBeInTheDocument();
    });
  });

  test('показывает ошибку при неудачном добавлении', async () => {
    axios.post.mockRejectedValueOnce({
      response: { data: { message: 'Ошибка сервера' } },
    });

    render(<AddBook />);

    fireEvent.change(screen.getByLabelText(/название книги/i), { target: { value: 'Новая книга' } });
    fireEvent.change(screen.getByLabelText(/автор/i), { target: { value: 'Автор' } });
    fireEvent.change(screen.getByLabelText(/жанр/i), { target: { value: 'Фантастика' } });

    fireEvent.click(screen.getByRole('button', { name: /добавить книгу/i }));

    await waitFor(() => {
      expect(screen.getByText(/ошибка при добавлении книги/i)).toBeInTheDocument();
    });
  });
});
