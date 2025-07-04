import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import axios from 'axios';

const AuthContext = createContext();

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(localStorage.getItem('token'));
  const [loading, setLoading] = useState(true);

  const setAxiosHeaders = (userData, token) => {
    axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    axios.defaults.headers.common['X-User-Id'] = userData.id;
    axios.defaults.headers.common['X-User-Name'] = userData.name;
  };

  const clearAxiosHeaders = () => {
    delete axios.defaults.headers.common['Authorization'];
    delete axios.defaults.headers.common['X-User-Id'];
    delete axios.defaults.headers.common['X-User-Name'];
  };

  const validateToken = useCallback(async () => {
    if (!token) {
      setLoading(false);
      return;
    }

    try {
      const response = await axios.get('http://localhost:8081/auth/validate', {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      const userData = response.data;
      setUser(userData);
      setAxiosHeaders(userData, token);
    } catch (error) {
      console.error('Token validation failed:', error);
      logout();
    } finally {
      setLoading(false);
    }
  }, [token]);

  useEffect(() => {
    validateToken();
  }, [validateToken]);

  const login = async (email, password) => {
    try {
      const response = await axios.post('http://localhost:8081/auth/login', {
        email,
        password,
      });

      const { token: newToken, user: userData } = response.data;

      setToken(newToken);
      setUser(userData);
      localStorage.setItem('token', newToken);
      setAxiosHeaders(userData, newToken);

      return { success: true };
    } catch (error) {
      console.error('Login failed:', error);
      const message =
        error.response?.data?.message ||
        error.message ||
        'Login failed due to unknown error';
      return { success: false, message };
    }
  };

  const register = async (name, email, password) => {
    try {
      const response = await axios.post('http://localhost:8081/auth/register', {
        name,
        email,
        password,
      });

      const { token: newToken, user: userData } = response.data;

      setToken(newToken);
      setUser(userData);
      localStorage.setItem('token', newToken);
      setAxiosHeaders(userData, newToken);

      return { success: true };
    } catch (error) {
      console.error('Registration failed:', error);
      const message =
        error.response?.data?.message ||
        error.message ||
        'Registration failed due to unknown error';
      return { success: false, message };
    }
  };

  const logout = () => {
    setToken(null);
    setUser(null);
    localStorage.removeItem('token');
    clearAxiosHeaders();
  };

  const value = {
    user,
    token,
    login,
    register,
    logout,
    loading,
  };

  return (
    <AuthContext.Provider value={value}>
      {!loading && children}
    </AuthContext.Provider>
  );
};
