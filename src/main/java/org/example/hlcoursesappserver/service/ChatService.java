package org.example.hlcoursesappserver.service;

import org.springframework.stereotype.Service;

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
