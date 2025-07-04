package com.bookexchange.repository;

import com.bookexchange.model.Exchange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExchangeRepository extends JpaRepository<Exchange, Long> {
    
    List<Exchange> findByRequesterId(Long requesterId);
    
    List<Exchange> findByOwnerId(Long ownerId);
    
    List<Exchange> findByBookId(Long bookId);
    
    @Query("SELECT COUNT(e) FROM Exchange e WHERE e.status = 'COMPLETED'")
    Long countCompletedExchanges();
    
    @Query("SELECT e.status, COUNT(e) FROM Exchange e GROUP BY e.status")
    List<Object[]> getExchangeStatistics();
}
