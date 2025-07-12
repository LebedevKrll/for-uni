import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';

const Books = () => {
  const [books, setBooks] = useState([]);
  const [genres, setGenres] = useState([]);
  const [filters, setFilters] = useState({
    title: '',
    author: '',
    genre: ''
  });
  const [loading, setLoading] = useState(true);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    fetchBooks();
    fetchGenres();
  }, [filters, currentPage]);

  const fetchBooks = async () => {
    try {
      setLoading(true);
      const params = {
        page: currentPage,
        size: 12,
        ...Object.fromEntries(
          Object.entries(filters).filter(([_, value]) => value !== '')
        )
      };
      
      const response = await axios.get('/api/books', { params });
      setBooks(response.data.content);
      setTotalPages(response.data.totalPages);
    } catch (error) {
      console.error('Error fetching books:', error);
    } finally {
      setLoading(false);
    }
  };

  const fetchGenres = async () => {
    try {
      const response = await axios.get('/api/books/genres');
      setGenres(response.data);
    } catch (error) {
      console.error('Error fetching genres:', error);
    }
  };

  const handleFilterChange = (e) => {
    const { name, value } = e.target;
    setFilters(prev => ({
      ...prev,
      [name]: value
    }));
    setCurrentPage(0);
  };

  const resetFilters = () => {
    setFilters({
      title: '',
      author: '',
      genre: ''
    });
    setCurrentPage(0);
  };

  if (loading) {
    return <div className="loading">Загрузка книг...</div>;
  }

  return (
    <div className="books-page">
      <div className="container">
        <h1>Доступные книги для обмена</h1>
        
        <div className="filters">
          <div className="filter-group">
            <input
              type="text"
              name="title"
              placeholder="Поиск по названию"
              value={filters.title}
              onChange={handleFilterChange}
              className="filter-input"
            />
            
            <input
              type="text"
              name="author"
              placeholder="Поиск по автору"
              value={filters.author}
              onChange={handleFilterChange}
              className="filter-input"
            />
            
            <select
              name="genre"
              value={filters.genre}
              onChange={handleFilterChange}
              className="filter-select"
            >
              <option value="">Все жанры</option>
              {genres.map(genre => (
                <option key={genre} value={genre}>{genre}</option>
              ))}
            </select>
            
            <button onClick={resetFilters} className="reset-btn">
              Сбросить фильтры
            </button>
          </div>
        </div>

        <div className="books-grid">
          {books.map(book => (
            <div key={book.id} className="book-card">
              <div className="book-info">
                <h3>{book.title}</h3>
                <p className="author">Автор: {book.author}</p>
                <p className="genre">Жанр: {book.genre}</p>
                {book.publicationYear && (
                  <p className="year">Год: {book.publicationYear}</p>
                )}
                {book.description && (
                  <p className="description">
                    {book.description.length > 100 
                      ? `${book.description.substring(0, 100)}...` 
                      : book.description
                    }
                  </p>
                )}
              </div>
              <div className="book-actions">
                <Link to={`/books/${book.id}`} className="view-btn">
                  Подробнее
                </Link>
              </div>
            </div>
          ))}
        </div>

        {books.length === 0 && (
          <div className="no-books">
            <p>Книги не найдены</p>
          </div>
        )}

        {totalPages > 1 && (
          <div className="pagination">
            <button 
              onClick={() => setCurrentPage(prev => Math.max(0, prev - 1))}
              disabled={currentPage === 0}
            >
              Предыдущая
            </button>
            <span>Страница {currentPage + 1} из {totalPages}</span>
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

export default Books;
