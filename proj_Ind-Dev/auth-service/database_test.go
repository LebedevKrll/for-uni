package main

import (
    "errors"
    "testing"

    "github.com/stretchr/testify/assert"
    "gorm.io/gorm"
)

type DBInterface interface {
    Create(value interface{}) *gorm.DB
}

var tdb DBInterface

type mockDB struct {
    CreateFunc func(value interface{}) *gorm.DB
}

func (m *mockDB) Create(value interface{}) *gorm.DB {
    return m.CreateFunc(value)
}

func TestSaveUser_Success(t *testing.T) {
    user := &User{Email: "test@example.com", Name: "Test", Password: "pass", Role: "USER"}

    tdb = &mockDB{
        CreateFunc: func(value interface{}) *gorm.DB {
            return &gorm.DB{Error: nil}
        },
    }

    err := SaveUser(user)
    assert.Nil(t, err)
}

func TestSaveUser_Failure(t *testing.T) {
    user := &User{Email: "fail@example.com", Name: "Fail", Password: "fail", Role: "USER"}

    tdb = &mockDB{
        CreateFunc: func(value interface{}) *gorm.DB {
            return &gorm.DB{Error: errors.New("db error")}
        },
    }

    err := SaveUser(user)
    assert.NotNil(t, err)
    assert.EqualError(t, err, "db error")
}

func TestGenerateToken_And_ValidateToken(t *testing.T) {
    user := &User{ID: 1, Email: "test@example.com", Name: "Test User", Role: "USER"}

    token, err := generateToken(user)
    assert.NoError(t, err)
    assert.NotEmpty(t, token)

    claims, err := validateToken(token)
    assert.NoError(t, err)
    assert.Equal(t, user.ID, claims.UserID)
    assert.Equal(t, user.Email, claims.Email)
    assert.Equal(t, user.Name, claims.Name)
    assert.Equal(t, user.Role, claims.Role)
}
