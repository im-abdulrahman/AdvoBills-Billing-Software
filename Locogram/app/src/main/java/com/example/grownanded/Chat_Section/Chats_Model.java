package com.example.grownanded.Chat_Section;

public class Chats_Model {
    String message, receiver, sender,timestamp, isSeen, deleteForMe, deleteForHis, emojiMy, emojiHis , msgType
            ;

    public Chats_Model() {
    }

    public Chats_Model(String message, String receiver, String sender, String timestamp, String isSeen, String deleteForMe, String deleteForHis, String emojiMy, String emojiHis, String msgType) {
        this.message = message;
        this.receiver = receiver;
        this.sender = sender;
        this.timestamp = timestamp;
        this.isSeen = isSeen;
        this.deleteForMe = deleteForMe;
        this.deleteForHis = deleteForHis;
        this.emojiMy = emojiMy;
        this.emojiHis = emojiHis;
        this.msgType = msgType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getIsSeen() {
        return isSeen;
    }

    public void setIsSeen(String isSeen) {
        this.isSeen = isSeen;
    }

    public String getDeleteForMe() {
        return deleteForMe;
    }

    public void setDeleteForMe(String deleteForMe) {
        this.deleteForMe = deleteForMe;
    }

    public String getDeleteForHis() {
        return deleteForHis;
    }

    public void setDeleteForHis(String deleteForHis) {
        this.deleteForHis = deleteForHis;
    }

    public String getEmojiMy() {
        return emojiMy;
    }

    public void setEmojiMy(String emojiMy) {
        this.emojiMy = emojiMy;
    }

    public String getEmojiHis() {
        return emojiHis;
    }

    public void setEmojiHis(String emojiHis) {
        this.emojiHis = emojiHis;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }
}

