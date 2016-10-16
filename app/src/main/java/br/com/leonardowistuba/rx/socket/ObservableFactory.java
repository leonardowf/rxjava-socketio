package br.com.leonardowistuba.rx.socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by leonardowistuba on 10/15/16.
 */
public class ObservableFactory {
    public static ObservableFactory sharedInstance = new ObservableFactory();
    private Socket socket;
    protected ObservableFactory() {
        tryToConnect();
    };

    public Observable<SocketEvent> socketEventObservable = Observable.create(new Observable.OnSubscribe<SocketEvent>() {
        @Override
        public void call(final Subscriber<? super SocketEvent> subscriber) {
            ArrayList<String> eventNames = new ArrayList<String>();
            eventNames.add(Socket.EVENT_CONNECT);
            eventNames.add(Socket.EVENT_CONNECT_ERROR);
            eventNames.add(Socket.EVENT_CONNECTING);
            eventNames.add("number_of_clients");
            eventNames.add("number_of_clicks");

            for (final String eventName : eventNames) {
                socket.on(eventName, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(new SocketEvent(args, eventName));
                        }
                    }
                });
            }

            socket.connect();
        }
    });

    private void tryToConnect() {
        try {
            socket = IO.socket("http://ec2-52-67-92-102.sa-east-1.compute.amazonaws.com:63908");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public Observable<ConnectionStatus> connectionStatusObservable = socketEventObservable.filter(new Func1<SocketEvent, Boolean>() {
        @Override
        public Boolean call(SocketEvent socketEvent) {
            ArrayList eventNames = new ArrayList();
            eventNames.add(Socket.EVENT_CONNECT);
            eventNames.add(Socket.EVENT_CONNECT_ERROR);
            eventNames.add(Socket.EVENT_CONNECTING);

            return eventNames.contains(socketEvent.event);
        }
    }).map(new Func1<SocketEvent, ConnectionStatus>() {
        @Override
        public ConnectionStatus call(SocketEvent socketEvent) {
            if (socketEvent.event.equals(Socket.EVENT_CONNECT)) {
                return new ConnectionStatus(socketEvent.args, ConnectionStatus.Status.Connected);
            } else if (socketEvent.event.equals(Socket.EVENT_CONNECT_ERROR)) {
                return new ConnectionStatus(socketEvent.args, ConnectionStatus.Status.ConnectionError);
            } else if (socketEvent.event.equals(Socket.EVENT_CONNECTING)) {
                return new ConnectionStatus(socketEvent.args, ConnectionStatus.Status.Connecting);
            }

            return null;
        }
    });

    public Observable<Integer> connectedUsersObservable = socketEventObservable.filter(new Func1<SocketEvent, Boolean>() {
        @Override
        public Boolean call(SocketEvent socketEvent) {
            if (socketEvent.event.equals("number_of_clients")) {
                return true;
            }

            return false;
        }
    }).map(new Func1<SocketEvent, Integer>() {
        @Override
        public Integer call(SocketEvent socketEvent) {
            Object[] args = socketEvent.args;
            JSONObject jsonObject = (JSONObject) args[0];
            try {
                Integer value = new Integer(jsonObject.getString("value"));
                return value;
            } catch (JSONException e) {
                return 0;
            }
        }
    });

    public Action1 emitClickAction = new Action1() {
        @Override
        public void call(Object o) {
            socket.emit("click");
        }
    };

    public Observable<Integer> numberOfClicksObservable = socketEventObservable.filter(new Func1<SocketEvent, Boolean>() {
        @Override
        public Boolean call(SocketEvent socketEvent) {
            if (socketEvent.event.equals("number_of_clicks")) {
                return true;
            }

            return false;
        }
    }).map(new Func1<SocketEvent, Integer>() {
        @Override
        public Integer call(SocketEvent socketEvent) {
            Object[] args = socketEvent.args;
            JSONObject jsonObject = (JSONObject) args[0];
            try {
                Integer value = new Integer(jsonObject.getString("value"));
                return value;
            } catch (JSONException e) {
                return 0;
            }
        }
    });

}