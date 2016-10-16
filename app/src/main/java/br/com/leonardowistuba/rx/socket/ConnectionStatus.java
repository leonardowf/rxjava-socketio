package br.com.leonardowistuba.rx.socket;

import java.util.ArrayList;

/**
 * Created by leonardowistuba on 10/15/16.
 */
public class ConnectionStatus {
    public enum Status {
        Connected,
        Connecting,
        ConnectionError
    }
    private Object[] mArgs;
    private Status mStatus;

    public ConnectionStatus(Object[] args, Status status) {
        mArgs = args;
        mStatus = status;
    }

    public ConnectionStatus(Status status) {
        mStatus = status;
    }

    public Object[] getArgs() {
        return mArgs;
    }

    public Status getStatus() {
        return mStatus;
    }
}

