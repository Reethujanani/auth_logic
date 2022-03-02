package com.coherent.unnamed.logic.model;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@Table(name="attendance")

public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "hours")
    private long hours;

    @Column(name = "is_present")
    private String isPresent;

    @OneToOne(cascade= CascadeType.MERGE)
    @JoinColumn(name="user_id_fk", referencedColumnName = "id")
    private Users users;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "deleted_flag")
    private boolean deletedFlag;

    @Column(name = "created_by")
    private String createdBy;


    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "modified_at")
    private Timestamp modifiedAt;

    @Column(name = "modified_by")
    private String modifiedBy;

}
