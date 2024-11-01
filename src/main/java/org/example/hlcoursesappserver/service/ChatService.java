package org.example.hlcoursesappserver.service;

import org.example.hlcoursesappserver.dto.MessageDTO;
import org.example.hlcoursesappserver.model.Listener;
import org.example.hlcoursesappserver.model.Message;
import org.example.hlcoursesappserver.model.Specialist;
import org.example.hlcoursesappserver.repository.ListenerRepository;
import org.example.hlcoursesappserver.repository.MessageRepository;
import org.example.hlcoursesappserver.repository.SpecialistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChatService {
//
//    private final MessageRepository messageRepository;
//    private final ListenerRepository listenerRepository;
//    private final SpecialistRepository specialistRepository;
//
//    @Autowired
//    public ChatService(MessageRepository messageRepository, ListenerRepository listenerRepository, SpecialistRepository specialistRepository) {
//        this.messageRepository = messageRepository;
//        this.listenerRepository = listenerRepository;
//        this.specialistRepository = specialistRepository;
//    }
//
//    public Message sendMessage(MessageDTO messageDTO) {
//        Message message = new Message();
//        Listener sender = listenerRepository.findById(messageDTO.getSenderId())
//                .orElseThrow(() -> new IllegalArgumentException("Sender (Listener) not found"));
//        Specialist receiver = specialistRepository.findById(messageDTO.getReceiverId())
//                .orElseThrow(() -> new IllegalArgumentException("Receiver (Specialist) not found"));
//
//        message.setSender(sender);
//        message.setReceiver(receiver);
//        message.setSender(messageDTO.getContent());
//        message.setSender(messageDTO.getTimestamp());
//
//        return messageRepository.save(message);
//    }
//
//    public Optional<Message> getMessageById(Long id) {
//        return messageRepository.findById(id);
//    }
//
//    public List<Message> getAllMessages() {
//        return messageRepository.findAll();
//    }
//
//    public void deleteMessage(Long id) {
//        messageRepository.deleteById(id);
//    }
}
