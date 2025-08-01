import React, { useEffect, useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import axios from 'axios';

function MyBooks() {
  const { user, token } = useAuth();
  const userId = user?.id
  const [books, setBooks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    async function fetchMyBooks() {
      try {
        setLoading(true);
        setError(null);
        const response = await axios.get(`http://localhost:8080/api/books/me/${userId}`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        console.log(response)
        setBooks(response.data);
      } catch (error) {
        console.error('Ошибка при загрузке моих книг', error);
        setError(error.message || 'Ошибка загрузки');
      } finally {
        setLoading(false);
      }
    }

    if (token && userId) {
      fetchMyBooks();
    }
  }, [token, userId]);

  if (loading) return <p>Загрузка моих книг...</p>;
  if (error) return <p style={{ color: 'red' }}>Ошибка: {error}</p>;
  if (books.length === 0) return <p>У вас пока нет добавленных книг.</p>;

  return (
    <div style={{ padding: '20px', fontFamily: 'Arial, sans-serif' }}>
      <h1>Мои книги</h1>
      <ul style={{ listStyle: 'none', padding: 0 }}>
        {books.map(book => (
          <li key={book.id} style={{ marginBottom: '20px', borderBottom: '1px solid #ccc', paddingBottom: '10px' }}>
            <h2>{book.title}</h2>
            <h4>Автор: {book.author}</h4>
            <p>{book.description}</p>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default MyBooks;