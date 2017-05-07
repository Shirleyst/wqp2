package wqp2;

import java.util.Arrays;
import java.awt.image.*;
import java.io.*;

import javax.swing.*;
import javax.imageio.*;
import java.awt.*;

class Board{ int x,y,h,w;
    Color color;
    Color [] colorArray = {Color.pink, Color.red, Color.yellow, Color.blue, Color.black, Color.green, Color.cyan, Color.gray, Color.orange, Color.darkGray, Color.lightGray};
    int cellSize = 50;
    int cellEdge = 2;
    public String picDir = "wqp2/data/picture/";

    public Board(int x,int y,int h, int w, Color c){
        this.x = x;
        this.y = y;
        this.h = h;
        this.w = w;
        this.color = c;
    }
}

public class View extends Board{
    WelcomeBoard wb;
    Question que;
    GameBoard gb;
    Model model;
    WinPanel wp;
    DeadPanel dp;
    PassPanel pp;
    Wqp2 w2;

    public String waMsg;
    int state;
    int level;

    public View(int h, int w, Wqp2 w2){
        super(0,0,h,w,Color.white);
        double edgeRate = 0.02;
        double botRate = 0.20;
        wb = new WelcomeBoard(0,0,h,w, colorArray[0]);
        que = new Question((int)(edgeRate * w), (int)(edgeRate * h), (int)(botRate *(1-4*edgeRate)* h), (int)((1-2*edgeRate) * w), colorArray[1]);
        gb = new GameBoard((int)(edgeRate * w), (int)(botRate *(1-4*edgeRate) * h + 2 * edgeRate * h), (int)((1-botRate)*(1-4*edgeRate)* h), (int)((1-2*edgeRate) * w),colorArray[9], this);

        dp = new DeadPanel(0,0,h,w, colorArray[8]);
        wp = new WinPanel(0,0,h,w, colorArray[8]);
        pp = new PassPanel(0,0,h,w, colorArray[8]);

        this.w2 = w2;
    }

    public void paint(Graphics g){
        switch(state){
            case 0:
                wb.paint(g);
                break;
            case 1:
                que.level = level;
                que.paint(g);
                gb.paint(g);
                break;
            case 2:
                dp.msg = waMsg;
                dp.paint(g);
                break;
            case 3:
                dp.msg = "You meet Dead Loop";
                dp.paint(g);
                break;
            case 4:
                dp.msg = "You meet a break";
                dp.paint(g);
                break;
            case 5:
                wp.paint(g);
                break;
            case 6:
                pp.paint(g);
                break;
        }
    }

    public void viewNotify(){
    }
}

class WelcomeBoard extends Board{
    public WelcomeBoard(int x,int y ,int h,int w, Color c){
        super(x, y, h, w, c);
    }

    public void paint(Graphics g){
        g.setColor(Color.pink);
        g.fillRect(x,y,w,h);
        g.setFont(new Font("TimesRoman", Font.BOLD, 13));
        g.setColor(Color.black);
        g.drawString("Welcome to WQP2",  x+w/2, y+ h/2);
        g.drawString("press any KEY to start",  x+w/2, y+ h/2+20);
    }
}

class Question extends Board{
    int level;
    public Question(int x,int y ,int h,int w, Color c){
        super(x,y,h,w,c);
    }

    public void paint(Graphics g){
        g.setColor(color);
        g.fillRect(x,y,w,h);
        g.setFont(new Font("TimesRoman", Font.BOLD, 13));
        g.setColor(Color.black);
        g.drawString("questions ", x + w/2,  y + h /2);
        g.setFont(new Font("TimesRoman", Font.BOLD, 13));
        g.setColor(Color.black);
        g.drawString("Level " + Integer.toString(level), x + 20,  y + 20);
    }

}

class GameBoard extends Board{
    View v;

    int selectI = -1;
    int selectJ = -1;
    String selectMsg = "empty";
    public GameBoard(int x,int y,int h, int w, Color c, View v){
        super(x,y,h,w,c);
        this.v = v;
    }

    public void check(int ex,int ey){
        selectI = -1;
        selectJ = -1;
        int numY = v.model.height;
        int numX = v.model.width;
        int sx = (w - cellSize * numX + cellEdge * (numX - 1)) / 2 + x;
        int sy = (h - cellSize * numY + cellEdge * (numY - 1)) / 2 + y;
        for(int i = 0; i < numY; i++){
            for(int j = 0;j < numX; j++){
                if(v.model.board[i][j] == null){
                    continue;
                }
                int wx = sx + j*(cellSize + cellEdge);
                int wy = sy + i*(cellSize + cellEdge);
                if((v.model.board[i][j].name == '+' || v.model.board[i][j].name == '*') && ex >= wx && ex <= wx + cellSize && ey >= wy && ey <= wy+cellSize){
                    selectI = i;
                    selectJ = j;
                    selectMsg = v.model.board[i][j].msg;
                    return;
                }
            }
        }

    }

    public void paint(Graphics g){
        Model model = v.model;
        g.setColor(color);
        g.fillRect(x,y,w,h);
        // insert image
        //
        BufferedImage image;
        Image scaledImage ;
        try {
            String picName = picDir + "background.png";
            image = ImageIO.read(new File(picName));
            scaledImage = image.getScaledInstance(w, h, Image.SCALE_DEFAULT);
            g.drawImage(scaledImage, x, y, null);
        } catch (IOException ex) {}

        g.setFont(new Font("TimesRoman", Font.BOLD, 13));

        int numX = model.width;
        int numY = model.height;
        int sx = (w - cellSize * numX + cellEdge * (numX - 1)) / 2 + x;
        int sy = (h - cellSize * numY + cellEdge * (numY - 1)) / 2 + y;
        for(int i = 0; i < numY; i++){
            for(int j = 0;j < numX; j++){
                if(model.board[i][j] == null){
                    continue;
                }
                Color co = colorArray[model.board[i][j].color];
                g.setColor(co);
                //g.fillRect(sx + j*(cellSize + cellEdge), sy + i * (cellSize + cellEdge), cellSize , cellSize);

                int ix = sx + j*(cellSize + cellEdge);
                int iy = sy + i * (cellSize + cellEdge);
                try {
                    String picName = picDir + model.board[i][j].pic;
                    image = ImageIO.read(new File(picName));
                    scaledImage = image.getScaledInstance(cellSize, cellSize, Image.SCALE_DEFAULT);
                    g.drawImage(scaledImage, ix,iy, null);
                } catch (IOException ex) {}
            }
        }
        if(selectI == -1){
            return;
        }
        g.setColor(Color.pink);
        int ttx = sx + selectJ*(cellSize + cellEdge)+20;
        int tty = sy + selectI * (cellSize + cellEdge)-70;
        g.fillRect(ttx, tty, 200, 100);
        g.setFont(new Font("TimesRoman", Font.BOLD, 13));
        g.setColor(Color.black);
        g.drawString(selectMsg, ttx + 100,  tty + 50);
    }
}

class DeadPanel extends Board{
    public String msg;

    public DeadPanel(int x,int y,int h, int w, Color c){
        super(x,y,h,w,c);
    }

    public void paint(Graphics g){
        g.setColor(color);
        g.fillRect(x,y,w,h);
        g.setFont(new Font("TimesRoman", Font.BOLD, 20));
        g.setColor(Color.black);
        g.drawString("You Dead!", x + w/2,  y + h /2);
        g.setFont(new Font("TimesRoman", Font.BOLD, 13));
        g.setColor(Color.black);
        g.drawString(msg, x + w/2,  y + h /2 + 40);
        g.setFont(new Font("TimesRoman", Font.BOLD, 13));
        g.setColor(Color.black);
        g.drawString("press any key to restart(except Q)", x + w/2,  y + h /2 + 80);
    }
}

class WinPanel extends Board{
    public String msg;

    public WinPanel(int x,int y,int h, int w, Color c){
        super(x,y,h,w,c);
    }

    public void paint(Graphics g){
        g.setColor(color);
        g.fillRect(x,y,w,h);
        g.setFont(new Font("TimesRoman", Font.BOLD, 20));
        g.setColor(Color.black);
        g.drawString("You Win!", x + w/2,  y + h /2);
        g.setFont(new Font("TimesRoman", Font.BOLD, 20));
        g.setColor(Color.black);
        g.drawString("Press anykey to next level", x + w/2,  y + h /2 + 20);
    }
}

class PassPanel extends Board{
    public String msg;

    public PassPanel(int x,int y,int h, int w, Color c){
        super(x,y,h,w,c);
    }

    public void paint(Graphics g){
        g.setColor(color);
        g.fillRect(x,y,w,h);
        g.setFont(new Font("TimesRoman", Font.BOLD, 20));
        g.setColor(Color.black);
        g.drawString("You Pass the all level!", x + w/2,  y + h /2);
    }
}
