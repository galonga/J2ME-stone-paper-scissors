package game;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class SimpleStreamThread extends Thread {
    private DataInputStream in;
    private DataOutputStream out;
    private final BTStreamHandler handler;

    public SimpleStreamThread(BTStreamHandler handler, DataInputStream in, DataOutputStream out) {
        this.handler = handler;
        this.in = in;
        this.out = out;
    }

    public void run() {
        this.handler.handleStream(this.in, this.out);
    }
}
