import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';

function BookDetails() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [book, setBook] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    async function fetchBook() {
      try {
        setLoading(true);
        setError(null);
        const response = await fetch(`/api/books/${id}`);
        if (!response.ok) {
          throw new Error('Ошибка загрузки книги');
        }
        const data = await response.json();
        setBook(data);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    }

    fetchBook();
  }, [id]);

  if (loading) return <p>Загрузка...</p>;
  if (error) return <p style={{ color: 'red' }}>Ошибка: {error}</p>;
  if (!book) return <p>Книга не найдена.</p>;

  const handleExchangeClick = () => {
    navigate(`/books/${id}/exchange`);
  };

  return (
    <div style={{ padding: '20px', fontFamily: 'Arial, sans-serif' }}>
      <h1>{book.title}</h1>
      <h3>Автор: {book.author}</h3>
      <p>{book.description}</p>

      <button
        onClick={handleExchangeClick}
        style={{
          padding: '10px 20px',
          fontSize: '16px',
          cursor: 'pointer',
          backgroundColor: '#007bff8e',
          color: 'white',
          border: 'none',
          borderRadius: '4px',
          marginTop: '20px'
        }}
      >
        Предложить обмен
      </button>
    </div>
  );
}

export default BookDetails;