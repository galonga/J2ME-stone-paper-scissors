package game;

public class ReadThread extends Thread {

    private GameCanvas m;
    private boolean stopped = false;

    public ReadThread(GameCanvas m) {
        this.m = m;
    }

    public void anhalten() {
        this.stopped = true;
    }

    public void run() {
        do {

            m.readthreadCallback();
            // Thread.sleep(30);

        } while (!stopped);
    }
}
