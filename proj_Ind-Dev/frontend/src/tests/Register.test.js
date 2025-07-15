import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import Register from './Register';
import { useAuth } from '../contexts/AuthContext';

jest.mock('../contexts/AuthContext');

describe('Register', () => {
  const mockRegister = jest.fn();

  beforeEach(() => {
    useAuth.mockReturnValue({
      register: mockRegister,
    });
  });

  test('валидация формы и вызов register', () => {
    render(<Register />);

    fireEvent.change(screen.getByLabelText(/имя пользователя/i), { target: { value: 'User' } });
    fireEvent.change(screen.getByLabelText(/email/i), { target: { value: 'user@example.com' } });
    fireEvent.change(screen.getByLabelText(/^пароль:/i), { target: { value: 'password123' } });
    fireEvent.change(screen.getByLabelText(/подтвердите пароль/i), { target: { value: 'password123' } });

    fireEvent.click(screen.getByRole('button', { name: /зарегистрироваться/i }));

    expect(mockRegister).toHaveBeenCalledWith('User', 'user@example.com', 'password123', 'USER');
  });

  test('показывает ошибку при несовпадении паролей', () => {
    render(<Register />);

    fireEvent.change(screen.getByLabelText(/^пароль:/i), { target: { value: 'password1' } });
    fireEvent.change(screen.getByLabelText(/подтвердите пароль/i), { target: { value: 'password2' } });

    fireEvent.click(screen.getByRole('button', { name: /зарегистрироваться/i }));

    expect(screen.getByText(/пароли не совпадают/i)).toBeInTheDocument();
    expect(mockRegister).not.toHaveBeenCalled();
  });
});
