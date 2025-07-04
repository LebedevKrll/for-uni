package main

import (
    "log"
    "os"
    "gorm.io/driver/postgres"
    "gorm.io/gorm"
)

var db *gorm.DB

func initDatabase() {
    dsn := os.Getenv("DATABASE_URL")
    if dsn == "" {
        dsn = "host=db user=postgres password=postgres dbname=bookexchange port=5432 sslmode=disable TimeZone=UTC"
    }
    
    var err error
    db, err = gorm.Open(postgres.Open(dsn), &gorm.Config{})
    if err != nil {
        log.Fatal("Failed to connect to database:", err)
    }
    
    err = db.AutoMigrate(&User{})
    if err != nil {
        log.Fatal("Failed to migrate database:", err)
    }
    
    log.Println("Database connected and migrated successfully")
}

func SaveUser(user *User) error {
    result := db.Create(user)
    return result.Error
}