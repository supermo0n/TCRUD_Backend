package com.example.tcrud.model;

import lombok.Getter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {

    //    생성일자
    @Column(name = "INSERT_TIME")
//    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime insertTime;
    //    수정일자
    @Column(name = "UPDATE_TIME")
//    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updateTime;
    //    Soft Delete Switch
    @Column(name = "DELETE_YN")
    private String deleteYn;
    //    삭제일자
    @Column(name = "DELETE_TIME")
//    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime deleteTime;

    @PrePersist
    void onPrePersist() {
        this.insertTime = LocalDateTime.now();
        this.deleteYn = "N";
    }

    @PreUpdate
    void onPreUpdate() {
        this.updateTime = LocalDateTime.now();
        this.deleteYn = "N";
    }

    public LocalDateTime getInsertTime() {
        return this.insertTime;
    }

    public void setDelete(){
        this.deleteYn = "Y";
        this.deleteTime = LocalDateTime.now();
    }

    public String getDeleteSw()
    {
        return this.deleteYn;
    }
}









