package com.agileboard.taskboard.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // Bu sınıfa "Sen internetten gelen istekleri karşılayan bir sunucusun" dedik.
public class HelloController {

    @GetMapping("/merhaba") // Kullanıcı tarayıcıda adresin sonuna "/merhaba" yazarsa bu metod çalışacak.
    public String sayHello() {
        return "Merhaba Dünya! Antigravity ile Ilk Spring Boot API noktam basariyla calisiyor. 🚀";
    }
}
