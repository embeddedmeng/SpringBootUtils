package com.embeddedmeng.easywork.dto;

public class KafkaMessage {

    private String messageType;
    private String messageContent;

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    @Override
    public String toString() {
        return "KafkaMessage{" +
                "messageType='" + messageType + '\'' +
                ", messageContent='" + messageContent + '\'' +
                '}';
    }
}
