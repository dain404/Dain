package com.example.bakery_shop.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationController {

    @MessageMapping("/ping")
    @SendTo("/topic/don-hang") // Hoặc bất kỳ topic nào, thực tế có thể không cần @SendTo nếu client subscribe đúng kênh pong, nhưng bài yêu cầu trả về "pong"
    public String ping() {
        return "pong";
    }
}
