package com.agileboard.taskboard.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tasks")
@Data // Getter, Setter, toString, equals ve hashCode metodlarını otomatik üretir.
@NoArgsConstructor // Parametresiz constructor üretir (JPA için zorunlu).
@AllArgsConstructor // Tüm alanları içeren constructor üretir.
@Builder // Tasarım kalıbı (Design Pattern) olarak nesne üretmeyi kolaylaştırır.
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Görev için özel kod (Örn: DEV-01, SOFT-02)
    @Column(name = "task_code")
    private String taskCode;

    @Column(nullable = false)
    private String title;

    private String description;

    private String status;

    @jakarta.persistence.ManyToOne
    @jakarta.persistence.JoinColumn(name = "user_id")
    private User user;
}
