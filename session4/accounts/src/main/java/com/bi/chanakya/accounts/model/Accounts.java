package com.bi.chanakya.accounts.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Accounts {
    @Id
    private long accountNumber;
    private int customerId;
    private String accountType;
    private String branchAddress;
    private LocalDate createDt;
}
