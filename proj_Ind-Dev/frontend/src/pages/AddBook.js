import React, { useState } from 'react';
import axios from 'axios';
import { useAuth } from '../contexts/AuthContext';

const AddBook = () => {
  const { token } = useAuth();
  const [formData, setFormData] = useState({
    title: '',
    author: '',
    description: '',
    genre: '',
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');

  const handleChange = (e) => {
    setFormData(prev => ({
      ...prev,
      [e.target.name]: e.target.value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccessMessage('');
    setLoading(true);

    try {
      const response = await axios.post(
        'http://localhost:8080/api/books',
        formData,
        {
          headers: {
            Authorization: `Bearer ${token}`,
            'Content-Type': 'application/json',
          },
        }
      );
      setSuccessMessage('Книга успешно добавлена!');
      setFormData({
        title: '',
        author: '',
        description: '',
        genre: '',
      });
    } catch (err) {
      console.error('Ошибка при добавлении книги:', err);
      setError(
        err.response?.data?.message ||
        'Ошибка при добавлении книги, попробуйте позже.'
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: '600px', margin: '20px auto', fontFamily: 'Arial, sans-serif' }}>
      <h2>Добавить книгу</h2>
      {error && <div style={{ color: 'red', marginBottom: '10px' }}>{error}</div>}
      {successMessage && <div style={{ color: 'green', marginBottom: '10px' }}>{successMessage}</div>}
      <form onSubmit={handleSubmit}>
        <div style={{ marginBottom: '10px' }}>
          <label>Название книги:</label><br />
          <input
            type="text"
            name="title"
            value={formData.title}
            onChange={handleChange}
            required
            style={{ width: '100%', padding: '8px' }}
          />
        </div>

        <div style={{ marginBottom: '10px' }}>
          <label>Автор:</label><br />
          <input
            type="text"
            name="author"
            value={formData.author}
            onChange={handleChange}
            required
            style={{ width: '100%', padding: '8px' }}
          />
        </div>

        <div style={{ marginBottom: '10px' }}>
          <label>Описание:</label><br />
          <textarea
            name="description"
            value={formData.description}
            onChange={handleChange}
            rows="5"
            style={{ width: '100%', padding: '8px' }}
          />
        </div>

        <div style={{ marginBottom: '10px' }}>
          <label>Жанр:</label><br />
          <input
            type="text"
            name="genre"
            value={formData.genre}
            onChange={handleChange}
            required
            style={{ width: '100%', padding: '8px' }}
          />
        </div>

        <button type="submit" disabled={loading} style={{ padding: '10px 20px' }}>
          {loading ? 'Добавление...' : 'Добавить книгу'}
        </button>
      </form>
    </div>
  );
};

export default AddBook;
