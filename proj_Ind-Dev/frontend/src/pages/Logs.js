import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';

const Logs = () => {
  const [exchanges, setExchanges] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    fetchExchanges();
  }, [currentPage]);

  const fetchExchanges = async () => {
    try {
      setLoading(true);
      const params = {
        page: currentPage,
        size: 12,
      };

      const response = await axios.get('/api/exchanges', { params });

      setExchanges(response.data.content);
      setTotalPages(response.data.totalPages);
      setError('');
    } catch (err) {
      console.error('Ошибка загрузки логов обменов:', err);
      setError('Не удалось загрузить логи обменов');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div className="loading">Загрузка логов обменов...</div>;
  }

  return (
    <div className="books-page">
      <div className="container">
        <h1>Логи обменов</h1>

        {error && <div style={{ color: 'red', marginBottom: '15px' }}>{error}</div>}

        {exchanges.length === 0 ? (
          <div className="no-books">
            <p>Логи обменов не найдены</p>
          </div>
        ) : (
          <div className="books-grid">
            {exchanges.map((ex) => (
              <div key={ex.id} className="book-card">
                <div className="book-info">
                  <p><strong>{ex.requesterName}</strong> Обменялся с <strong>{ex.ownerName}</strong></p>
                </div>
                <div className="book-actions">
                  <Link to={`/logs/${ex.id}`} className="view-btn">
                    Подробнее
                  </Link>
                </div>
              </div>
            ))}
          </div>
        )}

        {totalPages > 1 && (
          <div className="pagination" style={{ marginTop: '20px' }}>
            <button
              onClick={() => setCurrentPage(prev => Math.max(0, prev - 1))}
              disabled={currentPage === 0}
            >
              Предыдущая
            </button>
            <span style={{ margin: '0 10px' }}>
              Страница {currentPage + 1} из {totalPages}
            </span>
            <button
              onClick={() => setCurrentPage(prev => Math.min(totalPages - 1, prev + 1))}
              disabled={currentPage >= totalPages - 1}
            >
              Следующая
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

export default Logs;
