package game;

import javax.microedition.lcdui.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class GameCanvas extends Canvas implements BTStreamHandler {

    private static final int STEPLEN = 90;
    private int currentY = 1;
    private int w = this.getWidth();
    private int h = this.getHeight();
    private Image im1 = null, im2 = null, im3 = null, arrow = null;
    private int player = 0, winner = 0;
    private boolean gameOver = false;
    private ReadThread rt;
    private DataInputStream in;
    private DataOutputStream out;

    public int UserSelection = 0, OpponentSelection = 0;

    public GameCanvas(int player) {
        init(player);
    }

    public void init(int player) {
        this.player = player;
        gameOver = false;
        winner = -1; //-1 = no winner; 0 draw //1 winner
        UserSelection = 0;
        OpponentSelection = 0;
        repaint();
        if (rt == null) {
            rt = new ReadThread(this);
        }
    }

    protected void paint(Graphics g) {

        switch (UserSelection) {

            default:
                try {
                    im1 = Image.createImage("/1.png"); //stone
                    im2 = Image.createImage("/2.png"); //scissors
                    im3 = Image.createImage("/3.png"); // paper
                    arrow = Image.createImage("/arrow.png"); //arrow

                } catch (IOException e) {
                    throw new RuntimeException("Unable to load Image: " + e);
                }
                
                g.setColor(0xFFFFFF);
                g.fillRect(0, 0, w, h);
                g.drawImage(im1, 1, 1, Graphics.TOP | Graphics.LEFT);
                g.drawImage(im2, 1, 1 + STEPLEN, Graphics.TOP | Graphics.LEFT);
                g.drawImage(im3, 1, 1 + STEPLEN + STEPLEN, Graphics.TOP | Graphics.LEFT);
                g.drawImage(arrow, w, currentY, Graphics.TOP | Graphics.RIGHT);
                break;
            case 1:
                //stone
                g.setColor(0xFFFFFF);
                g.fillRect(0, 0, w, h);
                g.drawImage(im1, w / 2, h / 2, Graphics.HCENTER | Graphics.VCENTER);
                g.setColor(0x000000);
                if (gameOver) {
                    g.setColor(255, 0, 0);
                    g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_LARGE));
                    g.drawString(player2chose(), w / 2, 20, Graphics.HCENTER | Graphics.BASELINE);
                    g.drawString(WinnerIs(), w / 2, 40, Graphics.HCENTER | Graphics.BASELINE);
                }
                break;
            case 2:
                //scissors
                g.setColor(0xFFFFFF);
                g.fillRect(0, 0, w, h);
                g.drawImage(im2, w / 2, h / 2, Graphics.HCENTER | Graphics.VCENTER);
                g.setColor(0x000000);
                if (gameOver) {
                    g.setColor(255, 0, 0);
                    g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_LARGE));
                    g.drawString(player2chose(), w / 2, 20, Graphics.HCENTER | Graphics.BASELINE);
                    g.drawString(WinnerIs(), w / 2, 40, Graphics.HCENTER | Graphics.BASELINE);
                }
                break;
            case 3:
                //paper
                g.setColor(0xFFFFFF);
                g.fillRect(0, 0, w, h);
                g.drawImage(im3, w / 2, h / 2, Graphics.HCENTER | Graphics.VCENTER);
                if (gameOver) {
                    g.setColor(255, 0, 0);
                    g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_LARGE));
                    g.drawString(player2chose(), w / 2, 20, Graphics.HCENTER | Graphics.BASELINE);
                    g.drawString(WinnerIs(), w / 2, 40, Graphics.HCENTER | Graphics.BASELINE);
                }
                break;
        }
    }

    private String player2chose() {
        if (OpponentSelection == 1) {
            return "Dein Gegner hatte Stein,";
        } else if (OpponentSelection == 2) {
            return "Dein Gegner hatte Schere,";
        } else if (OpponentSelection == 3) {
            return "Dein Gegner hatte Papier,";
        } else {
            return "";
        }
    }

    private String WinnerIs() {
        if (winner == 1) {
            return "du hast gewonnen!";
        } else if (winner == 0) {
            return "daher unentschieden.";
        }

        return "du hast verloren!";
    }

    /** Win situation check
     *  @return boolean true = gameOver.
     **/
    private boolean isGameOver() {
        if (UserSelection != 0) {
            if (UserSelection == OpponentSelection) {
                winner = 0;//unentschieden
                endGame();
                return true;
            } else if (UserSelection == 1 && OpponentSelection == 2 || UserSelection == 2 && OpponentSelection == 3 || UserSelection == 3 && OpponentSelection == 1) {
                winner = 1;//SPieler 1 gewinnt
                endGame();
                return true;
            } else if (OpponentSelection == 1 && UserSelection == 2 || OpponentSelection == 2 && UserSelection == 3 || OpponentSelection == 3 && UserSelection == 1) {
                winner = 2;//spieler 2 gewinnt
                endGame();
                return true;
            }
        }

        return false;
    }

    /** Setzt das Spiel auf Ende so das keine weiteren Eingaben mehr m�glich sind
     **/
    private void endGame() {
        gameOver = true;
        repaint();
    }

    protected void keyPressed(int keyCode) {
        int gameAction = getGameAction(keyCode);
        switch (gameAction) {
            case UP:
                this.doUpAction();
                break;
            case DOWN:
                this.doDownAction();
                break;
            case FIRE:
                this.doFireAction(currentY);
                break;
        }
        repaint();

    }

    private void doUpAction() {
        if (gameOver == false) {
            currentY = currentY > 1 ? currentY - STEPLEN : 1;
        }

    }

    /** Bewegungen des COursers
     **/
    private void doDownAction() {
        if (gameOver == false) {
            currentY = currentY < STEPLEN * 2 ? currentY + STEPLEN : currentY;
            //2 == anzahl der icons-1
        }

    }

    private void doFireAction(int y) {
        if (gameOver == false) {
            if (y == 1) {
                UserSelection = 1;
            } else if (y == STEPLEN + 1) {//pos. 1 + schrittweite
                UserSelection = 2;
            } else if (y == 1 + (2 * STEPLEN)) {//pos. + 2x schrittweite
                UserSelection = 3;
            }

            try {
                this.out.writeInt(UserSelection); //Write to BT
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (isGameOver() == false) {
                repaint();
            } else {
                endGame();
            }
        }
    }

    /**
    * Read BT
    **/
    private void waitForOtherPlayer() {
        int eingang = -1;

        try {
            eingang = this.in.readInt();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if (eingang >= 0) {
            OpponentSelection = eingang;
            if (isGameOver() == false) {
                repaint();
            } else {
                endGame();
                repaint();
            }
        }
    }

    public void startReadThread() {
        rt.start();
    }

    public void readthreadCallback() {
        waitForOtherPlayer();
    }

    public void handleStream(DataInputStream in, DataOutputStream out) {
        this.in = in;
        this.out = out;
    }
}
