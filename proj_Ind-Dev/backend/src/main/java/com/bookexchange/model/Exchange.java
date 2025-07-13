package com.bookexchange.model;

import jakarta.persistence.*;

@Entity
@Table(name = "exchanges")
public class Exchange {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "requester_id")
    private Long requesterId;
    
    @Column(name = "requester_name")
    private String requesterName;
    
    @Column(name = "owner_id")
    private Long ownerId;
    
    @Column(name = "owner_name")
    private String ownerName;
    
    @Column(name = "offered_book_id")
    private Long offeredBookId;
    
    @Column(name = "offered_book_title")
    private String offeredBookTitle;
    
    @Column(name = "requested_book_id")
    private Long requestedBookId;
    
    @Column(name = "requested_book_title")
    private String requestedBookTitle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExchangeStatus status = ExchangeStatus.PENDING;
    
    public enum ExchangeStatus {
        PENDING, ACCEPTED, REJECTED, COMPLETED
    }
    
    public Exchange() {}
    
    public Exchange(Long requesterId, String requesterName, Long ownerId, 
                   String ownerName, Long offeredBookId, String offeredBookTitle,
                   Long requestedBookId, String requestedBookTitle) {
        this.requesterId = requesterId;
        this.requesterName = requesterName;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.offeredBookId = offeredBookId;
        this.offeredBookTitle = offeredBookTitle;
        this.requestedBookId = requestedBookId;
        this.requestedBookTitle = requestedBookTitle;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getRequesterId() { return requesterId; }
    public void setRequesterId(Long requesterId) { this.requesterId = requesterId; }
    
    public String getRequesterName() { return requesterName; }
    public void setRequesterName(String requesterName) { this.requesterName = requesterName; }
    
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
    
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    
    public Long getOfferedBookId() { return offeredBookId; }
    public void setOfferedBookId(Long offeredBookId) { this.offeredBookId = offeredBookId; }
    
    public String getRequestedBookTitle() { return requestedBookTitle; }
    public void setRequestedBookTitle(String requestedBookTitle) { this.requestedBookTitle = requestedBookTitle; }
    
    public Long getRequestedBookId() { return requestedBookId; }
    public void setRequestedBookId(Long requestedBookId) { this.requestedBookId = requestedBookId; }
    
    public String getOfferedBookTitle() { return offeredBookTitle; }
    public void setOfferedBookTitle(String offeredBookTitle) { this.offeredBookTitle = offeredBookTitle; }

    public ExchangeStatus getStatus() { return status; }
    public void setStatus(ExchangeStatus status) { this.status = status; }

}    
