package com.agileboard.taskboard.mapper;

import com.agileboard.taskboard.dto.TaskDTO;
import com.agileboard.taskboard.entity.Task;

public class TaskMapper {

    // Entity -> DTO
    public static TaskDTO toDTO(Task task) {
        if (task == null) return null;
        return TaskDTO.builder()
                .id(task.getId())
                .taskCode(task.getTaskCode())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .userId(task.getUser() != null ? task.getUser().getId() : null)
                .ownerUsername(task.getUser() != null ? task.getUser().getUsername() : "Bilinmiyor")
                .build();
    }

    // DTO -> Entity
    public static Task toEntity(TaskDTO dto) {
        if (dto == null) return null;
        return Task.builder()
                .id(dto.getId())
                .taskCode(dto.getTaskCode())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .status(dto.getStatus())
                // Not: User nesnesi Service katmanında Repository'den çekilip setlenmelidir.
                .build();
    }
}
