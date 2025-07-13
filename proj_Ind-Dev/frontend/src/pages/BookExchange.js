import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import { useAuth } from '../contexts/AuthContext';

function ExchangeBookPage() {
  const { id } = useParams();
  const { user, token } = useAuth();

  const [book, setBook] = useState(null);
  const [userBooks, setUserBooks] = useState([]);
  const [selectedBookId, setSelectedBookId] = useState('');
  const [loadingBook, setLoadingBook] = useState(true);
  const [loadingUserBooks, setLoadingUserBooks] = useState(true);
  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    async function fetchBook() {
      try {
        setLoadingBook(true);
        const response = await axios.get(`http://localhost:8080/api/books/${id}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        setBook(response.data);
      } catch (err) {
        setError('Не удалось загрузить информацию о книге.');
      } finally {
        setLoadingBook(false);
      }
    }
    fetchBook();
  }, [id, token]);

  useEffect(() => {
    async function fetchUserBooks() {
      if (!user?.id) return;
      try {
        setLoadingUserBooks(true);
        const response = await axios.get(`http://localhost:8080/api/books/me/${user.id}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        setUserBooks(response.data);
      } catch (err) {
        setError('Не удалось загрузить ваши книги.');
      } finally {
        setLoadingUserBooks(false);
      }
    }
    fetchUserBooks();
  }, [user, token]);

  const handleSelectChange = (e) => {
    setSelectedBookId(e.target.value);
  };

  const handleExchangeProposal = async () => {
    if (!selectedBookId) {
      setError('Пожалуйста, выберите книгу для обмена.');
      return;
    }
    setError('');
    setSuccessMessage('');
    setSubmitting(true);
try {
      await axios.post(
        'http://localhost:8080/api/exchanges',
        {
          reqBookId: Number(id),
          offeredBookId: Number(selectedBookId),
        },
        {
          headers: { 
            Authorization: `Bearer ${token}`,
            'X-User-Id': user?.id,
            'X-User-Name': user?.name,
          },
        }
      );
      setSuccessMessage('Предложение обмена успешно отправлено!');
      setSelectedBookId('');
    } catch (err) {
      setError('Ошибка при отправке предложения обмена. Попробуйте позже.');
    } finally {
      setSubmitting(false);
    }
  };

  if (loadingBook) return <p>Загрузка информации о книге...</p>;
  if (error) return <p style={{ color: 'red' }}>{error}</p>;
  if (!book) return <p>Книга не найдена.</p>;

  return (
    <div style={{ maxWidth: 600, margin: '20px auto', fontFamily: 'Arial, sans-serif' }}>
      <h1>{book.title}</h1>
      <h3>Автор: {book.author}</h3>
      <p>{book.description}</p>

      <p style={{ marginTop: 40, fontWeight: 'bold' }}>Вы хотите обменять на</p>

      {loadingUserBooks ? (
        <p>Загрузка ваших книг...</p>
      ) : userBooks.length === 0 ? (
        <p>У вас нет книг для обмена.</p>
      ) : (
        <select
          value={selectedBookId}
          onChange={handleSelectChange}
          style={{ width: '100%', padding: '8px', marginBottom: '20px' }}
        >
          <option value="">-- Выберите книгу --</option>
          {userBooks.map((b) => (
            <option key={b.id} value={b.id}>
              {b.title} — {b.author}
            </option>
          ))}
        </select>
      )}

      <button
        onClick={handleExchangeProposal}
        disabled={submitting || !selectedBookId}
        style={{ padding: '10px 20px', cursor: submitting ? 'not-allowed' : 'pointer' }}
      >
        {submitting ? 'Отправка...' : 'Предложить обмен'}
      </button>

      {successMessage && <p style={{ color: 'green', marginTop: 20 }}>{successMessage}</p>}
    </div>
  );
}

export default ExchangeBookPage;