import React, { useState } from 'react';
import useExchangeStore from '../store/exchangeStore';

function Exchanges() {
  const exchanges = useExchangeStore(state => state.exchanges);
  const addExchange = useExchangeStore(state => state.addExchange);
  const clearExchanges = useExchangeStore(state => state.clearExchanges);

  const [description, setDescription] = useState('');
  const [error, setError] = useState('');

  const handleAdd = (e) => {
    e.preventDefault();

    if (!description.trim()) {
      setError('Описание обмена не может быть пустым');
      return;
    }

    addExchange({
      date: new Date().toLocaleString(),
      description: description.trim(),
    });

    setDescription('');
    setError('');
  };

  return (
    <div style={{ maxWidth: '600px', margin: '20px auto', fontFamily: 'Arial, sans-serif' }}>
      <h1>Логи обменов</h1>

      <form onSubmit={handleAdd} style={{ marginBottom: '20px' }}>
        <label htmlFor="desc">Добавить новый лог обмена:</label><br />
        <input
          id="desc"
          type="text"
          value={description}
          onChange={e => setDescription(e.target.value)}
          style={{ width: '100%', padding: '8px', marginTop: '5px' }}
          placeholder="Описание обмена"
        />
        {error && <div style={{ color: 'red', marginTop: '5px' }}>{error}</div>}
        <button type="submit" style={{ marginTop: '10px', padding: '8px 16px' }}>
          Добавить
        </button>
      </form>

      {exchanges.length === 0 ? (
        <p>Логи пусты.</p>
      ) : (
        <ul style={{ listStyle: 'none', padding: 0 }}>
          {exchanges.map((ex, idx) => (
            <li key={idx} style={{ borderBottom: '1px solid #ccc', padding: '10px 0' }}>
              <strong>{ex.date}</strong>: {ex.description}
            </li>
          ))}
        </ul>
      )}

      {exchanges.length > 0 && (
        <button
          onClick={clearExchanges}
          style={{ marginTop: '20px', padding: '8px 16px', backgroundColor: '#d9534f', color: 'white', border: 'none', cursor: 'pointer' }}
        >
          Очистить логи
        </button>
      )}
    </div>
  );
}

export default Exchanges;
