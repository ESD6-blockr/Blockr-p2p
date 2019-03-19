package com.javarticle.spring.rest;

import javax.websocket.*;

@ClientEndpoint//(encoders = SharedMessageEncoder.class, decoders = SharedMessageDecoder.class)
public class WebSocketClient {

    @OnOpen
    public void onOpen(Session session) {
        System.out.println(session);
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println(message);
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        //WebSocketClientWrapper.getWrapper().disconnect(session);
        System.out.println(session);
    }
}
