package main

import (
    "bytes"
    "encoding/json"
    "net/http"
    "net/http/httptest"
    "testing"
    "golang.org/x/crypto/bcrypt"
    "github.com/stretchr/testify/assert"
    "gorm.io/driver/sqlite"
    "gorm.io/gorm"
)

func setupTestDB(t *testing.T) *gorm.DB {
    db, err := gorm.Open(sqlite.Open("file::memory:?cache=shared"), &gorm.Config{})
    if err != nil {
        t.Fatal(err)
    }
    err = db.AutoMigrate(&User{})
    if err != nil {
        t.Fatal(err)
    }
    return db
}

func setupTestServer(db *gorm.DB) http.Handler {
    dbGlobal := db
    db = dbGlobal

    mux := http.NewServeMux()
    mux.HandleFunc("/auth/register", registerHandler)
    mux.HandleFunc("/auth/login", loginHandler)
    mux.HandleFunc("/auth/validate", validateTokenHandler)
    mux.HandleFunc("/auth/refresh", refreshTokenHandler)
    return mux
}

func TestRegisterHandler_Success(t *testing.T) {
    db := setupTestDB(t)
    handler := setupTestServer(db)

    reqBody := RegisterRequest{
        Email:    "newuser@example.com",
        Name:     "New User",
        Password: "pass",
        Role:     "user",
    }
    bodyBytes, _ := json.Marshal(reqBody)

    req := httptest.NewRequest("POST", "/auth/register", bytes.NewReader(bodyBytes))
    req.Header.Set("Content-Type", "application/json")
    w := httptest.NewRecorder()

    handler.ServeHTTP(w, req)

    assert.Equal(t, http.StatusOK, w.Code)

    var resp AuthResponse
    err := json.NewDecoder(w.Body).Decode(&resp)
    assert.NoError(t, err)
    assert.Equal(t, reqBody.Email, resp.User.Email)
    assert.NotEmpty(t, resp.Token)
    assert.NotEmpty(t, resp.RefreshToken)
}

func TestLoginHandler_Success(t *testing.T) {
    db := setupTestDB(t)
    hashedPassword, _ := bcrypt.GenerateFromPassword([]byte("pass"), bcrypt.DefaultCost)
    user := User{Email: "example@example.com", Name: "Login User", Password: string(hashedPassword), Role: "USER"}
    db.Create(&user)

    handler := setupTestServer(db)

    reqBody := LoginRequest{
        Email:    "example@example.com",
        Password: "pass",
    }
    bodyBytes, _ := json.Marshal(reqBody)

    req := httptest.NewRequest("POST", "/auth/login", bytes.NewReader(bodyBytes))
    req.Header.Set("Content-Type", "application/json")
    w := httptest.NewRecorder()

    handler.ServeHTTP(w, req)

    assert.Equal(t, http.StatusOK, w.Code)

    var resp AuthResponse
    err := json.NewDecoder(w.Body).Decode(&resp)
    assert.NoError(t, err)
    assert.Equal(t, user.Email, resp.User.Email)
    assert.NotEmpty(t, resp.Token)
    assert.NotEmpty(t, resp.RefreshToken)
}
