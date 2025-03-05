package org.example.hlcoursesappserver.model;

import jakarta.persistence.*;

// Таблица для сообщений (чат между специалистами и слушателями)
@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    private Long senderId;

    private Long receiverId;

    private String senderRole;

    private String receiverRole;

    private String messageText;

    // Getters and Setters

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public String getSenderRole() {
        return senderRole;
    }

    public void setSenderRole(String senderRole) {
        this.senderRole = senderRole;
    }

    public String getReceiverRole() {
        return receiverRole;
    }

    public void setReceiverRole(String receiverRole) {
        this.receiverRole = receiverRole;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public Message() {
    }

    public Message(Long senderId, Long receiverId, String senderRole, String receiverRole, String messageText) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.senderRole = senderRole;
        this.receiverRole = receiverRole;
        this.messageText = messageText;
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageId=" + messageId +
                ", senderId=" + senderId +
                ", receiverId=" + receiverId +
                ", senderRole='" + senderRole + '\'' +
                ", receiverRole='" + receiverRole + '\'' +
                ", messageText='" + messageText + '\'' +
                '}';
    }
}
