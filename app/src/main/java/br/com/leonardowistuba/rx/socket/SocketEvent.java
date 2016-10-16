package br.com.leonardowistuba.rx.socket;

/**
 * Created by leonardowistuba on 10/15/16.
 */
public class SocketEvent {
    Object[] args;
    String event;

    public SocketEvent(Object[] args, String event) {
        this.args = args;
        this.event = event;
    }
}
