package main

type User struct {
    ID        uint      `json:"id" gorm:"primaryKey"`
    Email     string    `json:"email" gorm:"unique;not null"`
    Name      string    `json:"name" gorm:"not null"`
    Password  string    `json:"-" gorm:"not null"`
    Role      string    `json:"role"`
}

type LoginRequest struct {
    Email    string `json:"email" validate:"required,email"`
    Password string `json:"password" validate:"required,min=6"`
}

type RegisterRequest struct {
    Email    string `json:"email" validate:"required,email"`
    Name     string `json:"name" validate:"required,min=2"`
    Password string `json:"password" validate:"required,min=6"`
    Role     string `json:"role"`
}

type AuthResponse struct {
    Token        string `json:"token"`
    RefreshToken string `json:"refresh_token"`
    User         UserResponse `json:"user"`
}

type UserResponse struct {
    ID    uint   `json:"id"`
    Email string `json:"email"`
    Name  string `json:"name"`
    Role  string `json:"role"`
}