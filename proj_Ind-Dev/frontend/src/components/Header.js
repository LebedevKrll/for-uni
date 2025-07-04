import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import './Header.css';

const Header = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <header className="header">
      <div className="container">
        <Link to="/" className="logo">
          <h1>BookExchange</h1>
        </Link>
        
        <nav className="nav">
          <Link to="/books" className="nav-link">Книги</Link>
          
          {user ? (
            <>
              <Link to="/my-books" className="nav-link">Мои книги</Link>
              <Link to="/add-book" className="nav-link">Добавить книгу</Link>
              <Link to="/exchanges" className="nav-link">Обмены</Link>
              <div className="user-info">
                <span>Привет, {user.name}!</span>
                <button onClick={handleLogout} className="logout-btn">
                  Выйти
                </button>
              </div>
            </>
          ) : (
            <div className="auth-links">
              <Link to="/login" className="nav-link">Войти</Link>
              <Link to="/register" className="nav-link">Регистрация</Link>
            </div>
          )}
        </nav>
      </div>
    </header>
  );
};

export default Header;
