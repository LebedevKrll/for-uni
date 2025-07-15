import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import Books from './Books';
import axios from 'axios';

jest.mock('axios');

const mockBooksResponse = {
  data: {
    content: [
      { id: 1, title: 'Книга 1', author: 'Автор 1', genre: 'Фантастика' },
      { id: 2, title: 'Книга 2', author: 'Автор 2', genre: 'Драма' },
    ],
    totalPages: 1,
  },
};

const mockGenresResponse = {
  data: ['Фантастика', 'Драма', 'Роман'],
};

describe('Books', () => {
  beforeEach(() => {
    axios.get.mockImplementation((url) => {
      if (url === '/api/books') return Promise.resolve(mockBooksResponse);
      if (url === '/api/books/genres') return Promise.resolve(mockGenresResponse);
      return Promise.reject(new Error('not found'));
    });
  });

  test('рендерит список книг и жанры', async () => {
    render(<Books />);

    expect(screen.getByText(/загрузка книг/i)).toBeInTheDocument();

    await waitFor(() => {
      expect(screen.getByText('Книга 1')).toBeInTheDocument();
      expect(screen.getByText('Книга 2')).toBeInTheDocument();
      expect(screen.getByText('Фантастика')).toBeInTheDocument();
    });
  });

  test('фильтрует книги по названию', async () => {
    render(<Books />);

    await waitFor(() => screen.getByText('Книга 1'));

    fireEvent.change(screen.getByPlaceholderText(/поиск по названию/i), { target: { value: 'Книга 1' } });

    await waitFor(() => {
      expect(screen.getByText('Книга 1')).toBeInTheDocument();
      expect(screen.queryByText('Книга 2')).toBeNull();
    });
  });
});
