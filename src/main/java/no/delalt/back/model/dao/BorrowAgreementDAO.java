package no.delalt.back.model.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import no.delalt.back.configuration.validation.ValidID;

import java.time.LocalDate;

@Entity
@Table(
  name = "borrow_agreement",
  schema = "public",
  indexes = {
    @Index(name = "idx_is_completed", columnList = "is_completed"),
    @Index(name = "idx_item", columnList = "item_id"),
    @Index(
      name = "idx_owner_isCompleted",
      columnList = "owner_id, is_completed"
    ),
    @Index(
      name = "idx_borrower_isCompleted",
      columnList = "borrower_id, is_completed"
    ),
    @Index(
      name = "idx_item_endDate_startDate",
      columnList = "item_id, end_date, start_date"
    )
  }
)
public class BorrowAgreementDAO {
  @Id
  @ValidID
  @NotNull
  @Column(
    name = "borrow_agreement_id",
    columnDefinition = "VARCHAR(21)",
    nullable = false
  )
  private String borrowID;

  @NotNull
  @OneToOne
  @JoinColumn(name = "borrow_request_id", nullable = false)
  private BorrowRequestDAO borrowRequest;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "item_id", nullable = false)
  private ItemDAO item;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "borrower_id", nullable = false)
  private UserDAO borrower;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "owner_id", nullable = false)
  private UserDAO owner;

  @NotNull
  @Column(name = "created_at", nullable = false)
  private long createdAt;

  @NotNull
  @Column(name = "start_date", nullable = false)
  private LocalDate startDate;

  @NotNull
  @Column(name = "end_date", nullable = false)
  private LocalDate endDate;

  @Column(name = "return_time")
  private Long returnTime;

  @NotNull
  @Column(name = "status", nullable = false)
  private short status;

  @NotNull
  @Column(name = "is_completed", nullable = false)
  private boolean isCompleted;

  public BorrowAgreementDAO() {}

  public String getBorrowID() {
    return borrowID;
  }

  public void setBorrowID(String borrowID) {
    this.borrowID = borrowID;
  }

  public BorrowRequestDAO getBorrowRequest() {
    return borrowRequest;
  }

  public void setBorrowRequest(BorrowRequestDAO borrowRequest) {
    this.borrowRequest = borrowRequest;
  }

  public ItemDAO getItem() {
    return item;
  }

  public void setItem(ItemDAO item) {
    this.item = item;
  }

  public UserDAO getBorrower() {
    return borrower;
  }

  public void setBorrower(UserDAO borrower) {
    this.borrower = borrower;
  }

  public UserDAO getOwner() {
    return owner;
  }

  public void setOwner(UserDAO owner) {
    this.owner = owner;
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

  public Long getReturnTime() {
    return returnTime;
  }

  public void setReturnTime(Long returnTime) {
    this.returnTime = returnTime;
  }

  public short getStatus() {
    return status;
  }

  public void setStatus(short status) {
    this.status = status;
  }

  public long getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(long createdAt) {
    this.createdAt = createdAt;
  }

  public boolean getIsCompleted() {
    return isCompleted;
  }

  public void setIsCompleted(boolean isCompleted) {
    this.isCompleted = isCompleted;
  }
}
