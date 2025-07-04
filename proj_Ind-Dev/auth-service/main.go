package main

import (
    "log"
    "net/http"
    
    "github.com/gorilla/mux"
    "github.com/rs/cors"
)

func main() {
    r := mux.NewRouter()
    
    r.HandleFunc("/auth/register", registerHandler).Methods("POST")
    r.HandleFunc("/auth/login", loginHandler).Methods("POST")
    r.HandleFunc("/auth/validate", validateTokenHandler).Methods("GET")
    r.HandleFunc("/auth/refresh", refreshTokenHandler).Methods("POST")
    
    c := cors.New(cors.Options{
        AllowedOrigins: []string{"http://localhost:3000"},
        AllowedMethods: []string{"GET", "POST", "PUT", "DELETE", "OPTIONS"},
        AllowedHeaders: []string{"*"},
        AllowCredentials: true,
    })  
    
    handler := c.Handler(r)
    
    log.Println("Auth service starting on port 8081...")
    log.Fatal(http.ListenAndServe(":8081", handler))
}
