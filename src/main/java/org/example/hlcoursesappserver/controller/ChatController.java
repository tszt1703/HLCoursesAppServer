package org.example.hlcoursesappserver.controller;

import org.example.hlcoursesappserver.dto.MessageDTO;
import org.example.hlcoursesappserver.model.Message;
import org.example.hlcoursesappserver.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatController {

//    private final ChatService chatService;
//
//    @Autowired
//    public ChatController(ChatService chatService) {
//        this.chatService = chatService;
//    }
//
//    @PostMapping
//    public Message sendMessage(@RequestBody MessageDTO messageDTO) {
//        return chatService.sendMessage(messageDTO);
//    }
//
//    @GetMapping("/{id}")
//    public Message getMessageById(@PathVariable Long id) {
//        return chatService.getMessageById(id).orElse(null);
//    }
//
//    @GetMapping
//    public List<Message> getAllMessages() {
//        return chatService.getAllMessages();
//    }
//
//    @DeleteMapping("/{id}")
//    public void deleteMessage(@PathVariable Long id) {
//        chatService.deleteMessage(id);
//    }
}
