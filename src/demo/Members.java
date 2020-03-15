package demo;

import javax.sound.sampled.*;

import java.io.*;
import java.util.Vector;

class Node{
    int x;
    int y;
    int direct;
    public Node(int x,int y,int direct){
        this.x = x;
        this.y = y;
        this.direct = direct;
    }
}

//声音类
class AePlayWave extends Thread{
    private String filename;

    public AePlayWave(String wavefile){
        filename = wavefile;
    }

    public void run(){
        File SoundFile = new File(filename);

        AudioInputStream audioInputStream = null;

        try {
            audioInputStream = AudioSystem.getAudioInputStream(SoundFile);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        AudioFormat Format = audioInputStream.getFormat();
        SourceDataLine sdl = null;
        DataLine.Info info = new DataLine.Info(SourceDataLine.class,Format);

        try {
            sdl = (SourceDataLine)AudioSystem.getLine(info);
            sdl.open(Format);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        sdl.start();
        int nBytesRead = 0;
        //缓冲
        byte[] abData = new byte [1024];
        try {
            while(nBytesRead!=-1) {
                nBytesRead = audioInputStream.read(abData, 0, abData.length);
                if (nBytesRead >= 0) {
                    sdl.write(abData, 0, nBytesRead);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        } finally {
            sdl.drain();
            sdl.close();
        }
    }
}


//记录类
class Recorder{
    //记录敌人坦克的数量
    private static int EnNum = 10;
    //记录自己的坦克数量
    private static int myLife = 3;
    //记录总成绩
    private static int killEnemy = 0;
    //定义输入输出流
    private static FileWriter fw = null;
    private static BufferedWriter bw = null;
    private static FileReader fr = null;
    private static BufferedReader br = null;
    private Vector<EnemyTank> ets = new Vector<EnemyTank>();
    static Vector<Node> nodes = new Vector<Node>();

    public static Vector<Node> getRecAndEnemy(){
        try {
            fr = new FileReader("D:\\java项目\\TankFire\\Rec.txt");
            br = new BufferedReader(fr);
            String n = "";
            n = br.readLine();
            killEnemy = Integer.parseInt(n);

            while((n=br.readLine())!=null){
                String []xyz = n.split(" ");
                Node node = new Node(Integer.parseInt(xyz[0]),Integer.parseInt(xyz[1]),Integer.parseInt(xyz[2]));
                nodes.add(node);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return nodes;
    }

    public static void Rec(){
        File f = new File("D:\\java项目\\TankFire\\Rec.txt");
        f.delete();
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void getRec(){
        try {
            fr = new FileReader("D:\\java项目\\TankFire\\Rec.txt");
            br = new BufferedReader(fr);
            String n = br.readLine();
            killEnemy = Integer.parseInt(n);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void RecAndEnemyRec(){
        try {
            fw = new FileWriter("D:\\java项目\\TankFire\\Rec.txt");
            bw = new BufferedWriter(fw);
            bw.write(killEnemy+"\r\n");

            for(int i=0;i<ets.size();i++){
                EnemyTank em = ets.get(i);
                if(em.isLive){
                    String x = em.x+" "+em.y+" "+em.direct;
                    bw.write(x+"\r\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                bw.close();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static int getKillEnemy() {
        return killEnemy;
    }

    public static void setKillEnemy(int killEnemy) {
        Recorder.killEnemy = killEnemy;
    }

    public static int getEnNum() {
        return EnNum;
    }

    public static void setEnNum(int enNum) {
        EnNum = enNum;
    }

    public static int getMyLife() {
        return myLife;
    }

    public static void setMyLife(int myLife) {
        Recorder.myLife = myLife;
    }

    public Vector<EnemyTank> getEts() {
        return ets;
    }

    public void setEts(Vector<EnemyTank> ets) {
        this.ets = ets;
    }

    public static void reduceEnemy(){
        EnNum--;
    }

    public static void reduceMe(){
        myLife--;
    }

    public static void addkillEnemy(){
        killEnemy++;
    }

}

//子弹类
class Shot implements Runnable{
    int x;
    int y;
    int direct;
    int speed = 2;
    boolean isAlive = true;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Shot(int x, int y,int direct) {
        this.x = x;
        this.y = y;
        this.direct = direct;
    }

    public void run(){
        while(true){
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            switch (direct){
                case 0:
                    y -= speed;
                    break;
                case 1:
                    x += speed;
                    break;
                case 2:
                    y += speed;
                    break;
                case 3:
                    x -= speed;
                    break;
            }
            if(x<=0||x>=600||y>=400||y<=0){
                isAlive = false;
                break;
            }
//            System.out.println("x = "+x+" y = "+y);
        }
    }
}

//坦克类
class Tank {
    int x = 0;
    int y = 0;
    boolean isLive = true;

    //坦克颜色
    int color ;

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    //坦克的速度
    int speed = 2;
    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    //0表示上  1表示右  2表示下  3表示左
    int direct = 0;

    public int getDirect() {
        return direct;
    }

    public void setDirect(int direct) {
        this.direct = direct;
    }

    public Tank(int x,int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}

//我的坦克
class Hero extends Tank {
    Vector<Shot> ss = new Vector<Shot>();
    Shot s = null;

    public Hero(int x,int y)
    {
        super(x,y);
    }

    //向上移动
    public void moveUp(){
        if(y>0) {
            y -= speed;
        }
    }

    //向下移动
    public void moveDown(){
        if(y<370) {
            y += speed;
        }
    }

    //向左移动
    public void moveLeft(){
        if(x>0) {
            x -= speed;
        }
    }

    //向右移动
    public void moveRight(){
        if(x<570) {
            x += speed;
        }
    }

    public void shotEnemy(){
        switch (this.direct){
            case 0:
                //创建一颗子弹
                s = new Shot(x+10,y,0);
                //讲子弹加入到向量中
                ss.add(s);
                break;
            case 1:
                s = new Shot(x+30,y+10,1);
                ss.add(s);
                break;
            case 2:
                s = new Shot(x+10,y+30,2);
                ss.add(s);
                break;
            case 3:
                s = new Shot(x,y+10,3);
                ss.add(s);
                break;
        }
        Thread t = new Thread(s);
        t.start();
    }
}

//敌人坦克类
class EnemyTank extends Tank implements Runnable{
    //创建敌人坦克集合用于得到其他所有坦克的集合
    Vector<EnemyTank> ets = new Vector<EnemyTank>();
    //创建敌人的子弹集合,敌人添加子弹应当在刚创建坦克和子弹死亡后
    Vector<Shot> ss = new Vector<Shot>();
    //敌人坦克的速度
    int speed = 2;
    int times = 0;

    public EnemyTank (int x,int y){
        super(x,y);
    }

    public void getTanks(Vector<EnemyTank> em){
        this.ets = em;
    }

    //判断该敌人坦克是否与其他敌人坦克重叠
    public boolean isTouchOtherEnemyTank(){
        boolean b = false;

        switch (this.direct) {
            //该敌人坦克向上
            case 0:
                for (int i = 0; i < ets.size(); i++) {
                    //取出所有敌人坦克
                    EnemyTank et = ets.get(i);
                    if (et != this) {
                        //如果其他敌人坦克向上或向下
                        if (et.direct == 0 || et.direct == 2) {
                            if (this.x > et.x && this.x < et.x + 20 && this.y > et.y && this.y < et.y + 30) {
                                return true;
                            }
                            if (this.x + 20 > et.x && this.x + 20 < et.x + 20 && this.y > et.y && this.y < et.y + 30) {
                                return true;
                            }
                        }
                        //如果其他敌人坦克向左或向右
                        if (et.direct == 1 || et.direct == 3) {
                            if (this.x > et.x && this.x < et.x + 30 && this.y > et.y && this.y < et.y + 20) {
                                return true;
                            }
                            if (this.x + 20 > et.x && this.x + 20 < et.x + 30 && this.y > et.y && this.y < et.y + 20) {
                                return true;
                            }
                        }
                    }
                }
                break;
            case 1:
                for (int i = 0; i < ets.size(); i++) {
                    //取出所有敌人坦克
                    EnemyTank et = ets.get(i);
                    if (et != this) {
                        //如果其他敌人坦克向上或向下
                        if (et.direct == 0 || et.direct == 2) {
                            if (this.x + 30 > et.x && this.x + 30 < et.x + 20 && this.y > et.y && this.y < et.y + 30) {
                                return true;
                            }
                            if (this.x + 30 > et.x && this.x + 30 < et.x + 20 && this.y + 20 > et.y && this.y + 20 < et.y + 30) {
                                return true;
                            }
                        }
                        //如果其他敌人坦克向左或向右
                        if (et.direct == 1 || et.direct == 3) {
                            if (this.x + 30 > et.x && this.x + 30 < et.x + 30 && this.y > et.y && this.y < et.y + 20) {
                                return true;
                            }
                            if (this.x + 30 > et.x && this.x + 30 < et.x + 30 && this.y + 20 > et.y && this.y + 20 < et.y + 20) {
                                return true;
                            }
                        }
                    }
                }
                break;
            case 2:
                for (int i = 0; i < ets.size(); i++) {
                    //取出所有敌人坦克
                    EnemyTank et = ets.get(i);
                    if (et != this) {
                        //如果其他敌人坦克向上或向下
                        if (et.direct == 0 || et.direct == 2) {
                            if (this.x > et.x && this.x < et.x + 20 && this.y + 30 > et.y && this.y + 30 < et.y + 30) {
                                return true;
                            }
                            if (this.x + 20 > et.x && this.x + 20 < et.x + 20 && this.y > et.y && this.y < et.y + 30) {
                                return true;
                            }
                        }
                        //如果其他敌人坦克向左或向右
                        if (et.direct == 1 || et.direct == 3) {
                            if (this.x > et.x && this.x < et.x + 30 && this.y + 30 > et.y && this.y + 30 < et.y + 20) {
                                return true;
                            }
                            if (this.x + 20 > et.x && this.x + 20 < et.x + 30 && this.y + 30 > et.y && this.y + 30 < et.y + 20) {
                                return true;
                            }
                        }
                    }
                }
                break;
            case 3:
                for (int i = 0; i < ets.size(); i++) {
                    //取出所有敌人坦克
                    EnemyTank et = ets.get(i);
                    if (et != this) {
                        //如果其他敌人坦克向上或向下
                        if (et.direct == 0 || et.direct == 2) {
                            if (this.x > et.x && this.x < et.x + 20 && this.y > et.y && this.y < et.y + 30) {
                                return true;
                            }
                            if (this.x > et.x && this.x < et.x + 20 && this.y + 20 > et.y && this.y + 20 < et.y + 30) {
                                return true;
                            }
                        }
                        //如果其他敌人坦克向左或向右
                        if (et.direct == 1 || et.direct == 3) {
                            if (this.x > et.x && this.x < et.x + 30 && this.y > et.y && this.y < et.y + 20) {
                                return true;
                            }
                            if (this.x > et.x && this.x < et.x + 30 && this.y + 20 > et.y && this.y + 20 < et.y + 20) {
                                return true;
                            }
                        }
                    }
                }
                break;
        }
        return b;
    }

    public void run(){
        //让敌人坦克随机移动
        while (true){
            switch (this.direct){
                case 0:
                    for(int i=0;i<30;i++){
                        if(y>0&&!this.isTouchOtherEnemyTank()) {
                            y -= speed;
                        }
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case 1:
                    for(int i=0;i<30;i++){
                        if(x<570&&!this.isTouchOtherEnemyTank()) {
                            x += speed;
                        }
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case 2:
                    for(int i=0;i<30;i++){
                        if(y<370&&!this.isTouchOtherEnemyTank()) {
                            y += speed;
                        }
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case 3:
                    for(int i=0;i<30;i++){
                        if(x>0&&!this.isTouchOtherEnemyTank()) {
                            x -= speed;
                        }
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
            //随机产生一个新方向
            this.direct = (int)(Math.random()*4);

            if(this.isLive == false){
                break;
            }
            times++;
            //判断是否要给敌人坦克加入子弹
            if(times%2==0) {
                if (isLive == true) {
                    if (ss.size() < 5) {
                        Shot s = null;
                        //没有子弹，添加子弹
                        switch (direct) {
                            case 0:
                                //创建一颗子弹
                                s = new Shot(x + 10, y, 0);
                                //将子弹加入到向量中
                                ss.add(s);
                                break;
                            case 1:
                                s = new Shot(x + 30, y + 10, 1);
                                ss.add(s);
                                break;
                            case 2:
                                s = new Shot(x + 10, y + 30, 2);
                                ss.add(s);
                                break;
                            case 3:
                                s = new Shot(x, y + 10, 3);
                                ss.add(s);
                                break;
                        }
                        //启动线程
                        Thread t = new Thread(s);
                        t.start();
                    }
                }
            }
        }
    }
}

//炸弹类
class Bomb{
    int x;
    int y;
    //炸弹的生命
    int life = 9;
    boolean isLive = true;

    public Bomb(int x, int y) {
        this.x = x;
        this.y = y;
    }

    //减少生命值
    public void lifeDown(){
        if(life>0){
            life--;
        }else{
            this.isLive = false;
        }
    }
}