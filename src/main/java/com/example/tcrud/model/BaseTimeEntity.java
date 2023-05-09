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
    private String insertTime;
//    수정일자
    @Column(name = "UPDATE_TIME")
    private String updateTime;
    //    Soft Delete Switch
    @Column(name = "DELETE_YN")
    private String deleteYn;
    @Column(name = "DELETE_TIME")
    private String deleteTime;

    @PrePersist
    void onPrePersist() {
        this.insertTime = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.deleteYn = "N";
    }

    @PreUpdate
    void onPreUpdate() {
        this.updateTime = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//        this.insertTime = this.updateTime;
        this.deleteYn = "N";
    }

    public String getInsertTime()
    {
        return this.insertTime;
    }
}









