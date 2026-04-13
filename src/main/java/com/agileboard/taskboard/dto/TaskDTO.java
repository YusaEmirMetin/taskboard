package com.agileboard.taskboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDTO {
    private Long id;
    private String taskCode; // Özel kısaltma kodu (örn: DEV-01)
    private String title;
    private String description;
    private String status;
    private Long userId; // Görevin sahibi olan kullanıcının ID'si
    private String ownerUsername; // Görevi oluşturanın/sahibinin adı
}
