import React from 'react';
import useExchangeStore from '../store/exchangeStore';

function ExchangesLog() {
  const exchanges = useExchangeStore(state => state.exchanges);
  const clearExchanges = useExchangeStore(state => state.clearExchanges);

  return (
    <div style={{ padding: '20px' }}>
      <h2>Логи обменов</h2>
      {exchanges.length === 0 ? (
        <p>Логи пусты.</p>
      ) : (
        <ul>
          {exchanges.map((ex, idx) => (
            <li key={idx}>
              <strong>{ex.date}</strong>: {ex.description}
            </li>
          ))}
        </ul>
      )}
      <button onClick={clearExchanges}>Очистить логи</button>
    </div>
  );
}

export default ExchangesLog;
