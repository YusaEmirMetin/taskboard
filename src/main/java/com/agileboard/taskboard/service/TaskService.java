package com.agileboard.taskboard.service;

import java.time.Duration;
import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.agileboard.taskboard.dto.TaskDTO;
import com.agileboard.taskboard.entity.Task;
import com.agileboard.taskboard.entity.User;
import com.agileboard.taskboard.exception.ResourceNotFoundException;
import com.agileboard.taskboard.mapper.TaskMapper;
import com.agileboard.taskboard.repository.TaskRepository;
import com.agileboard.taskboard.repository.UserRepository;
import com.agileboard.taskboard.search.SearchService;
import com.agileboard.taskboard.search.TaskDocument;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final SearchService searchService;

    private static final String CACHE_KEY = "all_tasks";

    public TaskService(TaskRepository taskRepository,
            UserRepository userRepository,
            SimpMessagingTemplate messagingTemplate,
            RedisTemplate<String, Object> redisTemplate,
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper,
            SearchService searchService) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
        this.redisTemplate = redisTemplate;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.searchService = searchService;
    }

    // CREATE
    public TaskDTO createTask(TaskDTO taskDTO) {
        Task task = TaskMapper.toEntity(taskDTO);

        // Kullanıcı eşleşmesi yap
        if (taskDTO.getUserId() != null) {
            User user = userRepository.findById(taskDTO.getUserId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Kullanıcı bulunamadı, ID: " + taskDTO.getUserId()));
            task.setUser(user);
        }

        Task savedTask = taskRepository.save(task);

        // 1. Redis önbelleğini temizle
        redisTemplate.delete(CACHE_KEY);

        // 2. WebSocket ile canlı yayın
        messagingTemplate.convertAndSend("/topic/tasks", TaskMapper.toDTO(savedTask));

        // 3. Kafka kuyruğuna fiş bırak
        try {
            String taskJson = objectMapper.writeValueAsString(TaskMapper.toDTO(savedTask));
            kafkaTemplate.send("task-events", taskJson);
            System.out.println("📨 Kafka'ya fiş bırakıldı: " + savedTask.getTitle());
        } catch (JsonProcessingException e) {
            System.err.println("Kafka fişi yazılamadı: " + e.getMessage());
        }

        // 4. Elasticsearch'e indeksle
        searchService.indexTask(new TaskDocument(
                savedTask.getId(), savedTask.getTitle(),
                savedTask.getDescription(), savedTask.getStatus()));

        return TaskMapper.toDTO(savedTask);
    }

    // READ (Redis önbellek ile hızlandırılmış!)
    @SuppressWarnings("unchecked")
    public List<TaskDTO> getAllTasks() {
        // 1. Önce Redis'e (Hızlı Bellek) sor: "Elimde görev listesi var mı?"
        List<TaskDTO> cachedTasks = (List<TaskDTO>) redisTemplate.opsForValue().get(CACHE_KEY);

        if (cachedTasks != null) {
            System.out.println("⚡ Redis önbelleğinden geldi (PostgreSQL'e gitmedi!)");
            return cachedTasks;
        }

        // 2. Redis'te yoksa mecburen yavaş olan PostgreSQL'e git
        List<Task> tasks = taskRepository.findAll();
        System.out.println("🐘 PostgreSQL'den çekildi");

        // 3. PostgreSQL'den aldığın veriyi DTO'ya çevir
        List<TaskDTO> taskDTOs = tasks.stream().map(TaskMapper::toDTO).toList();

        // 4. Bir dahaki sefere hızlı gelsin diye Redis'e de bir kopyasını at (60 saniye
        // ömrü var)
        redisTemplate.opsForValue().set(CACHE_KEY, taskDTOs, Duration.ofSeconds(60));

        return taskDTOs;
    }

    // UPDATE - Bir görevi güncelle (Başlık, Açıklama veya Durum)
    public TaskDTO updateTask(Long id, TaskDTO taskDTO) {
        // 1. Önce bu ID'li görev gerçekten var mı kontrol et
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Güncellenecek görev bulunamadı, ID: " + id));

        // 2. Yeni bilgileri setle
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setStatus(taskDTO.getStatus());

        // 3. Veritabanına kaydet
        Task savedTask = taskRepository.save(task);

        // --- CANLI SİSTEMLERİN SENKRONİZE EDİLMESİ ---

        // 4. Redis önbelleğini temizle (Eski liste geçersiz oldu)
        redisTemplate.delete(CACHE_KEY);

        // 5. WebSocket ile tüm bağlı ekranları anında güncelle
        messagingTemplate.convertAndSend("/topic/tasks", TaskMapper.toDTO(savedTask));

        // 6. Elasticsearch indeksini güncelle (Arama sonuçları değişsin)
        searchService.indexTask(new TaskDocument(
                savedTask.getId(), savedTask.getTitle(),
                savedTask.getDescription(), savedTask.getStatus()));

        return TaskMapper.toDTO(savedTask);
    }

    // DELETE - Bir görevi tamamen sil
    public void deleteTask(Long id) {
        // 1. Önce görevi bul
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Silinecek görev bulunamadı, ID: " + id));

        // 2. Veritabanından sil
        taskRepository.delete(task);

        // 3. Redis önbelleğini temizle
        redisTemplate.delete(CACHE_KEY);

        // 4. Elasticsearch'ten sil
        searchService.deleteTask(id);

        // 5. WebSocket ile diğer kullanıcılara silindiğini bildir
        // "DELETED" statusu göndererek frontend'in bunu listeden çıkarmasını
        // sağlayacağız
        TaskDTO deletedTaskDTO = TaskMapper.toDTO(task);
        deletedTaskDTO.setStatus("DELETED");
        messagingTemplate.convertAndSend("/topic/tasks", deletedTaskDTO);

        System.out.println("🗑️ Görev başarıyla silindi: " + task.getTitle());
    }
}
