package main

import (
    "encoding/json"
    "net/http"
    "strings"
    "log"
    "golang.org/x/crypto/bcrypt"
    "github.com/go-playground/validator/v10"
)

var validate = validator.New()

func init() {
    initDatabase()
}

func registerHandler(w http.ResponseWriter, r *http.Request) {
    var req RegisterRequest
    if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
        http.Error(w, "Invalid request body", http.StatusBadRequest)
        return
    }
    
    if err := validate.Struct(req); err != nil {
        http.Error(w, "Validation failed", http.StatusBadRequest)
        return
    }
    
    var existingUser User
    if err := db.Where("email = ?", req.Email).First(&existingUser).Error; err == nil {
        http.Error(w, "User already exists", http.StatusConflict)
        return
    }
    
    hashedPassword, err := bcrypt.GenerateFromPassword([]byte(req.Password), bcrypt.DefaultCost)
    if err != nil {
        http.Error(w, "Error hashing password", http.StatusInternalServerError)
        return
    }
    
    role := strings.ToUpper(req.Role)
    if role != "ADMIN" {
        role = "USER"
    }
    
    user := User{
        Email:    req.Email,
        Name:     req.Name,
        Password: string(hashedPassword),
        Role:     role,
    }
    
    if err := db.Create(&user).Error; err != nil {
        http.Error(w, "Error creating user", http.StatusInternalServerError)
        return
    }
    
    token, err := generateToken(&user)
    if err != nil {
        http.Error(w, "Error generating token", http.StatusInternalServerError)
        return
    }
    
    refreshToken, err := generateRefreshToken(&user)
    if err != nil {
        http.Error(w, "Error generating refresh token", http.StatusInternalServerError)
        return
    }
    
    log.Printf("Пользователь зарегистрирован: ID=%d, Email=%s, Name=%s, Role=%s", user.ID, user.Email, user.Name, user.Role)

    response := AuthResponse{
        Token:        token,
        RefreshToken: refreshToken,
        User: UserResponse{
            ID:    user.ID,
            Email: user.Email,
            Name:  user.Name,
            Role:  user.Role,
        },
    }
    
    w.Header().Set("Content-Type", "application/json")
    json.NewEncoder(w).Encode(response)
}

func loginHandler(w http.ResponseWriter, r *http.Request) {
    var req LoginRequest
    if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
        http.Error(w, "Invalid request body", http.StatusBadRequest)
        return
    }
    
    if err := validate.Struct(req); err != nil {
        http.Error(w, "Validation failed", http.StatusBadRequest)
        return
    }
    
    var user User
    if err := db.Where("email = ?", req.Email).First(&user).Error; err != nil {
        http.Error(w, "Invalid credentials", http.StatusUnauthorized)
        return
    }
    
    if err := bcrypt.CompareHashAndPassword([]byte(user.Password), []byte(req.Password)); err != nil {
        http.Error(w, "Invalid credentials", http.StatusUnauthorized)
        return
    }
    
    token, err := generateToken(&user)
    if err != nil {
        http.Error(w, "Error generating token", http.StatusInternalServerError)
        return
    }
    
    refreshToken, err := generateRefreshToken(&user)
    if err != nil {
        http.Error(w, "Error generating refresh token", http.StatusInternalServerError)
        return
    }
    
    response := AuthResponse{
        Token:        token,
        RefreshToken: refreshToken,
        User: UserResponse{
            ID:    user.ID,
            Email: user.Email,
            Name:  user.Name,
            Role:  user.Role,
        },
    }
    
    w.Header().Set("Content-Type", "application/json")
    json.NewEncoder(w).Encode(response)
}

func validateTokenHandler(w http.ResponseWriter, r *http.Request) {
    authHeader := r.Header.Get("Authorization")
    if authHeader == "" {
        http.Error(w, "Authorization header required", http.StatusUnauthorized)
        return
    }
    
    tokenString := strings.TrimPrefix(authHeader, "Bearer ")
    if tokenString == authHeader {
        http.Error(w, "Bearer token required", http.StatusUnauthorized)
        return
    }
    
    claims, err := validateToken(tokenString)
    if err != nil {
        http.Error(w, "Invalid token", http.StatusUnauthorized)
        return
    }
    
    w.Header().Set("X-User-Id", string(rune(claims.UserID)))
    w.Header().Set("X-User-Name", claims.Name)
    w.Header().Set("X-User-Email", claims.Email)
    w.Header().Set("X-User-Role", claims.Role)
    
    response := UserResponse{
        ID:    claims.UserID,
        Email: claims.Email,
        Name:  claims.Name,
        Role:  claims.Role,
    }
    
    w.Header().Set("Content-Type", "application/json")
    json.NewEncoder(w).Encode(response)
}

func refreshTokenHandler(w http.ResponseWriter, r *http.Request) {
    var request struct {
        RefreshToken string `json:"refresh_token"`
    }
    
    if err := json.NewDecoder(r.Body).Decode(&request); err != nil {
        http.Error(w, "Invalid request body", http.StatusBadRequest)
        return
    }
    
    claims, err := validateToken(request.RefreshToken)
    if err != nil {
        http.Error(w, "Invalid refresh token", http.StatusUnauthorized)
        return
    }
    
    var user User
    if err := db.First(&user, claims.UserID).Error; err != nil {
        http.Error(w, "User not found", http.StatusNotFound)
        return
    }
    
    newToken, err := generateToken(&user)
    if err != nil {
        http.Error(w, "Error generating token", http.StatusInternalServerError)
        return
    }
    
    newRefreshToken, err := generateRefreshToken(&user)
    if err != nil {
        http.Error(w, "Error generating refresh token", http.StatusInternalServerError)
        return
    }
    
    response := AuthResponse{
        Token:        newToken,
        RefreshToken: newRefreshToken,
        User: UserResponse{
            ID:    user.ID,
            Email: user.Email,
            Name:  user.Name,
        },
    }
    
    w.Header().Set("Content-Type", "application/json")
    json.NewEncoder(w).Encode(response)
}
