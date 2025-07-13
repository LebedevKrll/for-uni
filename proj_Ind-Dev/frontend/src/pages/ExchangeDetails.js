import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';

function ExchangeDetails() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [exchange, setExchange] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    async function fetchExchange() {
      try {
        setLoading(true);
        setError(null);
        const response = await fetch(`/api/exchanges/${id}`);
        if (!response.ok) {
          throw new Error('Ошибка загрузки обмена');
        }
        const data = await response.json();
        setExchange(data);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    }

    fetchExchange();
  }, [id]);

  if (loading) return <p>Загрузка...</p>;
  if (error) return <p style={{ color: 'red' }}>Ошибка: {error}</p>;
  if (!exchange) return <p>Обмен не найден.</p>;

  return (
    <div style={{ padding: '20px', fontFamily: 'Arial, sans-serif' }}>
      <h1>{exchange.requesterName}</h1>
      <h3>Обменял {exchange.offeredBookTitle}</h3>
      <h1>{exchange.ownerName}</h1>
      <h3>Обменял {exchange.requestedBookTitle}</h3>
    </div>
  );
}

export default ExchangeDetails;
