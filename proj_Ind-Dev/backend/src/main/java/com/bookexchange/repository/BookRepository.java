package com.bookexchange.repository;

import com.bookexchange.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    List<Book> findByOwnerIdAndIsAvailableTrue(Long ownerId);
    
    List<Book> findByIsAvailableTrue();
                                           
    
    @Query("SELECT DISTINCT b.genre FROM Book b WHERE b.isAvailable = true ORDER BY b.genre")
    List<String> findAllAvailableGenres();
    
    List<Book> findByGenreAndIsAvailableTrueOrderByCreatedAtDesc(String genre);
}
