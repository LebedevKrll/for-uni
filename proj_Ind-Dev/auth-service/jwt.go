package main

import (
    "time"
    "errors"
    "github.com/dgrijalva/jwt-go"
)

var jwtSecret = []byte("eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiQWRtaW4iLCJJc3N1ZXIiOiJJc3N1ZXIiLCJVc2VybmFtZSI6IkphdmFJblVzZSIsImV4cCI6MTc1MTI5MTk0NSwiaWF0IjoxNzUxMj")

type Claims struct {
    UserID uint   `json:"user_id"`
    Email  string `json:"email"`
    Name   string `json:"name"`
    jwt.StandardClaims
}

func generateToken(user *User) (string, error) {
    expirationTime := time.Now().Add(24 * time.Hour)
    
    claims := &Claims{
        UserID: user.ID,
        Email:  user.Email,
        Name:   user.Name,
        StandardClaims: jwt.StandardClaims{
            ExpiresAt: expirationTime.Unix(),
            IssuedAt:  time.Now().Unix(),
        },
    }
    
    token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
    return token.SignedString(jwtSecret)
}

func generateRefreshToken(user *User) (string, error) {
    expirationTime := time.Now().Add(7 * 24 * time.Hour)
    
    claims := &Claims{
        UserID: user.ID,
        Email:  user.Email,
        Name:   user.Name,
        StandardClaims: jwt.StandardClaims{
            ExpiresAt: expirationTime.Unix(),
            IssuedAt:  time.Now().Unix(),
        },
    }
    
    token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
    return token.SignedString(jwtSecret)
}

func validateToken(tokenString string) (*Claims, error) {
    claims := &Claims{}
    
    token, err := jwt.ParseWithClaims(tokenString, claims, func(token *jwt.Token) (interface{}, error) {
        return jwtSecret, nil
    })
    
    if err != nil {
        return nil, err
    }
    
    if !token.Valid {
        return nil, errors.New("invalid token")
    }
    
    return claims, nil
}
