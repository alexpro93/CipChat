package com.stevecavallin.cipchat;

/**
 * Created by Steve on 24/07/14.
 */
public class ChatMessage {

    public boolean left;
    public String message;
    public String dataora;

    public ChatMessage(boolean left, String message, String dataora) {
        super();
        this.left = left;
        this.message = message;
        this.dataora=dataora;
    }

}
