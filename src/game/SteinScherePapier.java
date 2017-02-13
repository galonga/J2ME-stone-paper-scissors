package game;

import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;

public class SteinScherePapier extends MIDlet implements CommandListener {

    BTClient btClient;
    private BTServer btServer;
    private GameCanvas myCanvas;
    Command cmdExit, cmdStart, cmdNew;
    private Alert startScreen;
    private boolean isServer = false;

    public void startApp() {
        btClient = new BTClient();
        btClient.findServer(getMyCanvas());
        
        while (this.btClient.isServerFound() == 2) {
        }

        if (this.btClient.isServerFound() == 0) {
            this.btServer = new BTServer();
            isServer = true;
            this.btServer.acceptClient(getMyCanvas());
        }

        Display.getDisplay(this).setCurrent(getStartScreen());
    }

    private Alert getStartScreen() {
        if (startScreen == null) {
            this.cmdStart = new Command("Start", Command.OK, 1);
            startScreen = new Alert("Stein, Schere, Papier");

            if (isServer) {
                startScreen.setString("          Du bist Spieler 1");
            } else {
                startScreen.setString("          Du bist Spieler 2");
            }

            startScreen.setCommandListener(this);
            startScreen.setTimeout(Alert.FOREVER);
            startScreen.addCommand(cmdStart);
        }

        return startScreen;
    }

    private GameCanvas getMyCanvas() {
        this.cmdExit = new Command("Ende", Command.EXIT, 1);
        this.cmdNew = new Command("Neues Spiel", Command.OK, 2);

        if (isServer) {
            myCanvas = new GameCanvas(1);
            myCanvas.setTitle("Spieler 1");
        } else {
            myCanvas = new GameCanvas(2);
            myCanvas.setTitle("Spieler 2");
        }

        myCanvas.addCommand(cmdExit);
        myCanvas.addCommand(cmdNew);
        myCanvas.setCommandListener(this);

        return myCanvas;
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
    }

    public void commandAction(Command c, Displayable d) {
        if (d == this.myCanvas && c == this.cmdNew) {
            //wenn Server dann als Player 1 starten ansonsten Player 2
            if (isServer) {
                myCanvas.init(1);
            } else {
                myCanvas.init(2);
            }
        }

        if (d == this.myCanvas && c == this.cmdExit) {
            this.notifyDestroyed();
        }

        if (d == this.startScreen && c == this.cmdStart) {
            Display.getDisplay(this).setCurrent(this.myCanvas);//Spielfeld Anzeigen
            myCanvas.startReadThread(); //  readthread starten
        }
    }
}
