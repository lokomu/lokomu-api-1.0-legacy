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

@Entity
@Table(
  name = "rating",
  schema = "public",
  indexes = { @Index(name = "idx_borrower", columnList = "borrower_id") }
)
public class RatingDAO {
  @Id
  @ValidID
  @NotNull
  @Column(name = "item_id", columnDefinition = "VARCHAR(21)", nullable = false)
  private String ratingID;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "borrower_id", nullable = false)
  private UserDAO borrower;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "owner_id", nullable = false)
  private UserDAO owner;

  @NotNull
  @OneToOne
  @JoinColumn(name = "borrow_agreement_id", nullable = false)
  private BorrowAgreementDAO borrowAgreement;

  @NotNull
  @Column(name = "status", nullable = false)
  private short status;

  public RatingDAO() {}

  public void setRatingID(String ratingID) {
    this.ratingID = ratingID;
  }

  public void setBorrower(UserDAO borrower) {
    this.borrower = borrower;
  }

  public void setOwner(UserDAO owner) {
    this.owner = owner;
  }

  public void setStatus(short status) {
    this.status = status;
  }

  public void setBorrowAgreement(BorrowAgreementDAO borrowAgreement) {
    this.borrowAgreement = borrowAgreement;
  }
}
