import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './contexts/AuthContext';
import Header from './components/Header';
import Home from './pages/Home';
import Login from './pages/Login';
import Register from './pages/Register';
import Books from './pages/Books';
import MyBooks from './pages/MyBooks.js';
import LogsPage from './pages/Logs';
import AddBook from './pages/AddBook';
import BookDetails from './pages/BookDetails';
import BookExchange from './pages/BookExchange';
import ProtectedRoute from './components/ProtectedRoute';
import ExchangeDetails from './pages/ExchangeDetails.js'
import './App.css';

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="App">
          <Header />
          <main className="main-content">
            <Routes>
              <Route path="/" element={<Home />} />
              <Route path="/login" element={<Login />} />
              <Route path="/register" element={<Register />} />
              <Route path="/books" element={<Books />} />
              <Route path="/books/:id" element={<BookDetails />} />
              <Route path="/books/:id/exchange" element={<BookExchange />} />
              <Route path="/my-books" element={
                <ProtectedRoute>
                  <MyBooks />
                </ProtectedRoute>
              } />
              <Route path="/add-book" element={
                <ProtectedRoute>
                  <AddBook />
                </ProtectedRoute>
              } />
              <Route path="/logs" element={
                <ProtectedRoute requiredRole="ADMIN">
                  <LogsPage />
                </ProtectedRoute>
              } />
              <Route path="/logs/:id" element={
                <ProtectedRoute requiredRole="ADMIN">
                  <ExchangeDetails />
                </ProtectedRoute>
              } />
            </Routes>
          </main>
        </div>
      </Router>
    </AuthProvider>
  );
}

export default App;
