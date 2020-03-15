/**
 * 功能：坦克大战 4.0
 * 1.画出坦克
 * 2.我的坦克可以移动
 * 3.坦克可以发射炮弹(炮弹可以连发，最多5发)
 * 4.炮弹击中敌人时敌人会死亡并有爆炸特效
 * 5.敌人坦克可以随机移动
 * 6.敌人坦克也可以发射子弹
 * 7.敌人坦克击中我的坦克时，我的坦克爆炸
 * 8.防止敌人坦克重叠
 * 9.可以分关(有开始界面和闪烁的效果)
 * 10.可以暂停和继续
 * 11.可以记录分数
 *    11.1可以记录玩家分数
 *    11.2可以记录敌人的坐标
 *    11.3可以存档退出和继续游戏
 * 12.可以处理声音文件
 */
package demo;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.util.Vector;

public class MyTankGame4 extends JFrame implements ActionListener {
    MyPanel mp = null;
    MyStartPanel msp = null;
    JMenuBar jmb = null;
    JMenu jm1 = null;
    JMenu jm2 = null;
    JMenu jm3 = null;
    JMenuItem jmi1 = null;
    JMenuItem jmi2 = null;
    JMenuItem jmi3 = null;
    JMenuItem jmi4 = null;
    public static int jud = 0;

    public static void main(String[] args) throws IOException {
        MyTankGame4 mtg = new MyTankGame4();
    }

    public MyTankGame4() throws IOException {
        jmb = new JMenuBar();
        jm1 = new JMenu("游戏（G）");
        jm2 = new JMenu("暂停（空格）");
        jm3 = new JMenu("开始（空格）");
        jmi1 = new JMenuItem("开始新游戏（N）");
        jmi2 = new JMenuItem("退出游戏（E）");
        jmi3 = new JMenuItem("存档退出");
        jmi4 = new JMenuItem("继续上局游戏");
        //注册监听
        jmi1.addActionListener(this);
        jmi1.setActionCommand("new game");
        jmi2.addActionListener(this);
        jmi2.setActionCommand("exit");
        jmi3.addActionListener(this);
        jmi3.setActionCommand("save and exit");
        jmi4.addActionListener(this);
        jmi4.setActionCommand("continue");

        //设置快捷方式
        jm1.setMnemonic('G');
        jmi1.setMnemonic('N');
        jmi2.setMnemonic('E');
        jm1.add(jmi1);
        jm1.add(jmi2);
        jm1.add(jmi3);
        jm1.add(jmi4);
        jmb.add(jm1);
        jmb.add(jm2);
        jmb.add(jm3);
        this.setJMenuBar(jmb);

        msp = new MyStartPanel();
        this.add(msp);
        Thread t = new Thread(msp);
        t.start();

        this.setSize(600,500);
        this.setLocation(500,220);
        this.setVisible(true);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("new game")){
            jud = 1;
            this.remove(msp);
            try {
                mp = new MyPanel(0);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            this.add(mp);
            Thread t = new Thread(mp);
            t.start();
            this.addKeyListener(mp);
            this.setVisible(true);
            this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        }else if(e.getActionCommand().equals("exit")){
            Recorder.Rec();
            System.exit(0);
        }else if(e.getActionCommand().equals("save and exit")){
            Recorder rd = new Recorder();
            rd.setEts(mp.ets);
            rd.RecAndEnemyRec();
            System.exit(0);
        }else if(e.getActionCommand().equals("continue")){
            this.remove(msp);
            try {
                mp = new MyPanel(1);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            this.add(mp);
            Thread t = new Thread(mp);
            t.start();
            this.addKeyListener(mp);
            this.setVisible(true);
            this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        }
    }
}

//我的开始面板
class MyStartPanel extends JPanel implements Runnable{
    int times = 0;

    public MyStartPanel() throws IOException {
        MyPanel mp= new MyPanel(0);
        mp.palyAudio();
        MyPanel.playtime = 2;
    }

    public void paint(Graphics g){
        super.paint(g);
        g.fillRect(0,0,600,500);
        if(times%2==0) {
            g.setColor(Color.yellow);
            Font myFont = new Font("华文新魏", Font.BOLD, 30);
            g.setFont(myFont);
            g.drawString("Stage 1!", 220, 180);
        }
    }
    @Override
    public void run() {
        while (true){
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
            times++;
            this.repaint();
        }
    }
}

//我的面板
class MyPanel extends JPanel implements KeyListener,Runnable {
    //定义一个我的坦克
    Hero hero = null;

    //定义敌人坦克集合
    Vector<EnemyTank> ets = new Vector<EnemyTank>();

    //定义一个炸弹集合
    Vector<Bomb> bombs = new Vector<Bomb>();

    int enSize = 10;
    public static int playtime = 1;
    public static int pressReflect = 1;

    Image image1 = null;
    Image image2 = null;
    Image image3 = null;

    //构造函数
    public MyPanel(int flag) throws IOException {
        Vector<Node> nodes = new Vector<Node>();
        if(MyTankGame4.jud==1) {
            System.out.println("asfodafes");

            File f = new File("D:\\java项目\\TankFire\\Rec.txt");
            FileWriter fw = new FileWriter("D:\\java项目\\TankFire\\Rec.txt");
            BufferedWriter bw = new BufferedWriter(fw);
            f.delete();
            try {
                f.createNewFile();
                bw.write(0+"");
            } catch (Exception e1) {
                e1.printStackTrace();
            }finally {
                try {
                    bw.close();
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        nodes = Recorder.getRecAndEnemy();
        Recorder.getRec();
        hero = new Hero(100,100);

        if(flag==0) {
            //初始化敌人坦克
            for (int i = 0; i < enSize; i++) {
                EnemyTank et = new EnemyTank((i + 1) * 50, 0);
                et.setColor(1);
                et.setDirect(2);
                //启动坦克
                Thread t = new Thread(et);
                t.start();
                //添加敌人子弹
                Shot s = new Shot(et.x + 10, et.y + 30, 2);
                et.ss.add(s);
                //启动子弹线程
                Thread t1 = new Thread(s);
                t1.start();
                ets.add(et);
                //将其他敌人坦克交给该敌人坦克
                et.getTanks(ets);
            }
        }else{
            //初始化敌人坦克
            for (int i = 0; i < nodes.size(); i++) {
                Node node = nodes.get(i);
                EnemyTank et = new EnemyTank(node.x, node.y);
                et.setColor(1);
                et.setDirect(2);
                //启动坦克
                Thread t = new Thread(et);
                t.start();
                //添加敌人子弹
                Shot s = new Shot(et.x + 10, et.y + 30, 2);
                et.ss.add(s);
                //启动子弹线程
                Thread t1 = new Thread(s);
                t1.start();
                ets.add(et);
                //将其他敌人坦克交给该敌人坦克
                et.getTanks(ets);
            }
        }

        //播放音乐
        palyAudio();

        //初始化图片
//        image1 = Toolkit.getDefaultToolkit().getImage("bomb1.png");
//        image2 = Toolkit.getDefaultToolkit().getImage("bomb2.png");
//        image3 = Toolkit.getDefaultToolkit().getImage("bomb3.png");

        try {
            image1 = ImageIO.read(new File("bomb1.png"));
            image2 = ImageIO.read(new File("bomb2.png"));
            image3 = ImageIO.read(new File("bomb3.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void palyAudio(){
        //播放音乐
        AePlayWave apw = null;
        AePlayWave apw2 = null;
        AePlayWave apw3 = null;
        AePlayWave apw4 = null;
        AePlayWave shot = null;
        apw = new AePlayWave("D:\\java项目\\TankFire\\Source\\Sound\\start.wav");
        apw2 = new AePlayWave("D:\\java项目\\TankFire\\Source\\Sound\\background.wav");
        apw3 = new AePlayWave("D:\\java项目\\TankFire\\Source\\Sound\\explosion.wav");
        apw4 = new AePlayWave("D:\\java项目\\TankFire\\Source\\Sound\\explosion2.wav");
        shot = new AePlayWave("D:\\java项目\\TankFire\\Source\\Sound\\shot.wav");

        if(playtime==1){
            apw.start();
        }else if(playtime==2){
            apw2.start();
        }if(playtime==3){
            apw3.start();
        }if(playtime==4){
            apw4.start();
        }if(playtime==5){
            shot.start();
        }
    }

    //画出提示信息
    public void showInfo(Graphics g){
        //画出提示信息坦克（该坦克不参与战斗）
        this.drawTank(100,405,g,1,0);
        g.setColor(Color.black);
        g.drawString(Recorder.getEnNum()+"",130,425);
        this.drawTank(500,405,g,0,0);
        g.setColor(Color.black);
        g.drawString(Recorder.getMyLife()+"",530,425);
        Font myFont = new Font("宋体",Font.BOLD,15);
        g.setFont(myFont);
        g.drawString("你的总成绩：",200,425);
        this.drawTank(290,405,g,1,0);
        g.setColor(Color.black);
        g.drawString(Recorder.getKillEnemy()+"",320,425);
    }

    //重新paint
    public void paint(Graphics g) {
        super.paint(g);
        g.fillRect(0,0,600,400);
        //画出提示信息
        showInfo(g);

        //画出自己的坦克和子弹
        if(hero.isLive) {
            //画坦克
            this.drawTank(hero.getX(), hero.getY(), g, 0, this.hero.direct);
        }
        //画子弹
        for(int i=0;i<hero.ss.size();i++) {
            Shot myShot = hero.ss.get(i);//遍历向量中的每颗子弹

            //画出每颗子弹
            if (myShot != null && myShot.isAlive == true) {
                g.draw3DRect(myShot.getX(), myShot.getY(), 1, 1, false);
            }
            //从向量中去掉死亡的子弹
            if(myShot.isAlive == false){
                hero.ss.remove(myShot);
            }
        }
        //画出敌人的坦克和子弹
        for (int i = 0; i < ets.size(); i++) {
            EnemyTank et = ets.get(i);
            if(et.isLive==true) {
                //画坦克
                this.drawTank(et.getX(), et.getY(), g, 1, et.getDirect());
            }
            //画子弹
            for(int j=0;j<et.ss.size();j++){
                Shot enemyShot = et.ss.get(j);
                if(enemyShot.isAlive == true){
                    g.draw3DRect(enemyShot.getX(), enemyShot.getY(), 1, 1, false);
                }else {
                    et.ss.remove(enemyShot);
                }
            }

        }

        //画出炸弹
        for(int i=0;i<bombs.size();i++){
            Bomb b = bombs.get(i);

            if(b.life>6){
                g.drawImage(image1,b.x,b.y,30,30,this);
//                System.out.println("第1张图已画出");
            } else if(b.life>3){
                g.drawImage(image2,b.x,b.y,30,30,this);
//                System.out.println("第2张图已画出");
            } else{
                g.drawImage(image3,b.x,b.y,30,30,this);
//                System.out.println("第3张图已画出");
            }
            //生命值减小
            b.lifeDown();
            //如果炸弹的生命值为0，将该炸弹从向量中剔除
            if(b.life==0){
                bombs.remove(b);
            }
        }
    }
    //判断敌人坦克是否击中我
    public void hitMe(){
        for(int i=0;i<ets.size();i++){
            EnemyTank et = ets.get(i);
            for(int j=0;j<et.ss.size();j++){
                Shot s = et.ss.get(j);
                if(hero.isLive) {
                    hitTank(s,hero);
                }
            }
        }
    }

    //判断我的子弹是否击中敌人
    public void hitEnemyTank(){
        //判断是否子弹是否打中坦克
        for(int i=0;i<hero.ss.size();i++){
            //取出每颗子弹
            Shot myShot = hero.ss.get(i);
            if(myShot.isAlive){
                for(int j=0;j<ets.size();j++){
                    //遍历每个敌人坦克
                    EnemyTank et = ets.get(j);
                    if(et.isLive==true) {
                        hitTank(myShot, et);
                    }
                }
            }
        }
    }

    //判断坦克是否击中敌人坦克
    public void hitTank(Shot s, Tank et){
        switch (et.direct){
            case 0:
            case 2:
                if(s.getX()<et.getX()+20&&s.getX()>et.getX()&&s.getY()>et.getY()&&s.getY()<et.getY()+30){//击中敌军，子弹死亡，敌人坦克死亡
                    s.isAlive = false;
                    et.isLive = false;
                    if(et.color==1) {
                        playtime = 3;
                        palyAudio();
                        Recorder.reduceEnemy();
                        Recorder.addkillEnemy();
                    }
                    if(et.color==0){
                        playtime = 4;
                        palyAudio();
                        Recorder.reduceMe();
                    }
                    Bomb b = new Bomb(et.getX(),et.getY());
                    bombs.add(b);
                }
                break;
            case 1:
            case 3:
                if(s.getX()<et.getX()+30&&s.getX()>et.getX()&&s.getY()>et.getY()&&s.getY()<et.getY()+20){//击中敌军，子弹死亡，敌人坦克死亡
                    s.isAlive = false;
                    et.isLive = false;
                    if(et.color==1) {
                        playtime = 3;
                        Recorder.reduceEnemy();
                        Recorder.addkillEnemy();
                        palyAudio();
                    }
                    if(et.color==0){
                        playtime = 4;
                        Recorder.reduceMe();
                        palyAudio();
                    }
                    Bomb b = new Bomb(et.getX(),et.getY());
                    bombs.add(b);
                }
                break;
        }
    }

    //画出坦克
    public void drawTank(int x,int y,Graphics g,int type,int direct) {
        //判断坦克类型
        switch (type) {
            case 0:
                g.setColor(Color.cyan);
                break;
            case 1:

                g.setColor(Color.yellow);
                break;
            default:
                break;
        }

        //判断方向
        switch (direct) {
            //向上
            case 0:
                g.fill3DRect(x,y,5,30,false);
                g.fill3DRect(x+15,y,5,30,false);
                g.fill3DRect(x+5,y+5,10,20,false);
                g.fillOval(x+5,y+10,10,10);
                g.drawLine(x+10,y+15,x+10,y);
                break;
            //向右
            case 1:
                g.fill3DRect(x,y,30,5,false);
                g.fill3DRect(x,y+15,30,5,false);
                g.fill3DRect(x+5,y+5,20,10,false);
                g.fillOval(x+10,y+5,10,10);
                g.drawLine(x+15,y+10,x+30,y+10);
                break;
            //向下
            case 2:
                g.fill3DRect(x,y,5,30,false);
                g.fill3DRect(x+15,y,5,30,false);
                g.fill3DRect(x+5,y+5,10,20,false);
                g.fillOval(x+5,y+10,10,10);
                g.drawLine(x+10,y+15,x+10,y+30);
                break;
            //向左
            case 3:
                g.fill3DRect(x,y,30,5,false);
                g.fill3DRect(x,y+15,30,5,false);
                g.fill3DRect(x+5,y+5,20,10,false);
                g.fillOval(x+10,y+5,10,10);
                g.drawLine(x+15,y+10,x,y+10);
                break;
            default:
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    //wasd控制方向上下左右
    @Override
    public void keyPressed(KeyEvent e) {
        //设置我的坦克方向
        if(e.getKeyCode() == KeyEvent.VK_W) {
            this.hero.setDirect(0);
            this.hero.moveUp();
        }if(e.getKeyCode() == KeyEvent.VK_A){
            this.hero.setDirect(3);
            this.hero.moveLeft();
        }if(e.getKeyCode() == KeyEvent.VK_S){
            this.hero.setDirect(2);
            this.hero.moveDown();
        }if(e.getKeyCode() == KeyEvent.VK_D){
            this.hero.setDirect(1);
            this.hero.moveRight();
        }
        if(e.getKeyCode() == KeyEvent.VK_J){
            if(hero.ss.size()<=4) {
                this.hero.shotEnemy();
                playtime = 5;
                palyAudio();
            }
        }
        if(e.getKeyCode() == KeyEvent.VK_SPACE){
            pressReflect++;
            if(pressReflect%2==0) {
                speedTozero();
            }else{
                speedRefresh();
            }
        }

        this.repaint();
    }

    public void speedTozero(){
        for(int i=0;i<ets.size();i++){
            EnemyTank et = ets.get(i);
            et.speed = 0;
            for(int j=0;j<et.ss.size();j++){
                Shot s = et.ss.get(j);
                s.speed = 0;
            }
        }
        hero.speed = 0;
    }

    public void speedRefresh(){
        for(int i=0;i<ets.size();i++){
            EnemyTank et = ets.get(i);
            et.speed = 3;
            for(int j=0;j<et.ss.size();j++){
                Shot s = et.ss.get(j);
                s.speed = 3;
            }
        }
        hero.speed = 3;
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    public void run(){
        while (true){
            try {
                Thread.sleep(50);
                playtime++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //判断我是否击中敌人坦克
            this.hitEnemyTank();
            //判断敌人坦克是否击中我
            this.hitMe();
            if(pressReflect%2==1) {
                this.repaint();
            }
        }
    }
}
 