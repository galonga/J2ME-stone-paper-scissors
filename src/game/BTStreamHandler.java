package game;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public interface BTStreamHandler {
    public void handleStream(DataInputStream in, DataOutputStream out);
}
