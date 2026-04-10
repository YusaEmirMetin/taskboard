package com.agileboard.taskboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.agileboard.taskboard.entity.Task;

@Repository // 1️⃣ Bu sınıfın "Veritabanıyla konuşan işçi" olduğunu belirttik.
public interface TaskRepository extends JpaRepository<Task, Long> {
    // 2️⃣ İçine HİÇBİR ŞEY YAZMIYORUZ! 
    // Spring Data JPA arka planda kaydet (save()), bul (findById()), sil (delete()) 
    // gibi onca tonla SQL sorgusunu bizim yerimize sadece bu satır sayesinde yazar.
}
