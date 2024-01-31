package no.delalt.back.model.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import no.delalt.back.configuration.validation.ValidID;

import java.time.LocalDate;

@Entity
@Table(
  name = "borrow_request",
  schema = "public",
  indexes = {
    @Index(
      name = "idx_item_startDate_endDate",
      columnList = "item_id, start_date, end_date"
    ),
    @Index(
      name = "idx_owner_isProcessed",
      columnList = "owner_id, is_processed"
    ),
    @Index(
      name = "idx_requester_isProcessed",
      columnList = "requester_id, is_processed"
    )
  }
)
public class BorrowRequestDAO {
  @Id
  @ValidID
  @NotNull
  @Column(
    name = "borrow_request_id",
    columnDefinition = "VARCHAR(21)",
    nullable = false
  )
  private String borrowRequestID;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "item_id", nullable = false)
  private ItemDAO item;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "requester_id", nullable = false)
  private UserDAO requester;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "owner_id", nullable = false)
  private UserDAO owner;

  @Column(name = "message")
  private String message;

  @NotNull
  @Column(name = "created_at", nullable = false)
  private long createdAt;

  @NotNull
  @Column(name = "start_date", nullable = false)
  private LocalDate startDate;

  @NotNull
  @Column(name = "end_date", nullable = false)
  private LocalDate endDate;

  @NotNull
  @Column(name = "status", nullable = false)
  private short status;

  @NotNull
  @Column(name = "is_processed", nullable = false)
  private boolean isProcessed;

  public BorrowRequestDAO() {}

  public String getBorrowRequestID() {
    return borrowRequestID;
  }

  public void setBorrowRequestID(String borrowRequestID) {
    this.borrowRequestID = borrowRequestID;
  }

  public long getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(long createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDate getStartDate() {
    return startDate;
  }

  public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
  }

  public LocalDate getEndDate() {
    return endDate;
  }

  public void setEndDate(LocalDate endDate) {
    this.endDate = endDate;
  }

  public ItemDAO getItem() {
    return item;
  }

  public void setItem(ItemDAO item) {
    this.item = item;
  }

  public UserDAO getRequester() {
    return requester;
  }

  public void setRequester(UserDAO requester) {
    this.requester = requester;
  }

  public UserDAO getOwner() {
    return owner;
  }

  public void setOwner(UserDAO owner) {
    this.owner = owner;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public short getStatus() {
    return status;
  }

  public void setStatus(short status) {
    this.status = status;
  }

  public boolean getIsProcessed() {
    return isProcessed;
  }

  public void setIsProcessed(boolean isProcessed) {
    this.isProcessed = isProcessed;
  }
}
