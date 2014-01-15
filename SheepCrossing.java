import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import javax.imageio.ImageIO;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;


public class SheepCrossing{
  private JFrame frame= new JFrame();
  private JPanel panel= new JPanel();
  private JPanel sheepPanel= new JPanel();
  private GroupLayout gl= new GroupLayout(panel);
  private GroupLayout sl= new GroupLayout(sheepPanel);
  private JLayeredPane layeredPane= new JLayeredPane();
  private JLabel[] images;
  private int[] gap;
  private int width= 20;
  private int height= 10;
  private int gapFromLeft= (width*60-100)/2;
  private int gapFromTop= 560;
  private boolean gameOver= false;
  private Timer refresh= new Timer();
  private Timer[] timers= new Timer[height];//one timer for each car
  private Timer keyTimer= new Timer();
  private boolean keyT= false;
  private Timer keyTimer2= new Timer();
  private boolean keyT2= false;
  private int sheepSpeed= 75;
  public SheepCrossing(){
    setImages();
    panel.setLayout(gl);
    gap= new int[height];//number of gaps= number of car
    long time= 0;
    JTextField widthField= new JTextField();
    widthField.setColumns(2);
    Object[] dataIn= {"Width (12-24)", widthField};
    JOptionPane op= new JOptionPane(dataIn,JOptionPane.QUESTION_MESSAGE,JOptionPane.DEFAULT_OPTION,null,null);
    JDialog dialog= op.createDialog(op, "Settings");
    dialog.setVisible(true);
    try{
      width= Integer.parseInt(widthField.getText());
    }catch(Exception e){
      width= 20;
    }
    if(width>24){
      width= 24;
    }
    else if(width<12){
      width= 12;
    }
    for(int i= 0; i<height; i++){//for each, schedule a timer
      time= (long)(Math.random()*11+5);//interval after which to add 1-3 pixels to the gap
      final int q= i;
      timers[i]= new Timer();
      timers[i].schedule(new TimerTask(){//schedule a timer
        public void run(){
          int k= (int)(Math.random()*3+1);//how many pixels to add
          gap[q]= gap[q]+k;//increase the gap
          if(gap[q]>width*60-100){//if the car is going over the edge, make its position 0
            gap[q]= 0;
          }
        }
      }, time, time);//run after 'time', every 'time' milliseconds
    }
    SwingUtilities.invokeLater(drawBoard());//draw the board
    refresh.schedule(new TimerTask(){//schedule a timer
      public void run(){
        SwingUtilities.invokeLater(updateBoard());//refreshes the GUI
        if(checkIfCollision()||checkIfWon()){
          this.cancel();//if they have won or lost, cancel the refresh timer
        }
      }
    }, 0, 10);//run after 0, every 10 milliseconds
    frame.addKeyListener(new KeyListener(){//keyboard listeners
      public void keyPressed(KeyEvent e){
        if(!gameOver){//only register the key if the game is not over
          if(e.getKeyCode()==KeyEvent.VK_RIGHT){//restrictions on where the sheep can move
            TimerTask t1= new TimerTask(){
              public void run(){
                if(!gameOver&&gapFromLeft<(width*60-40)){
                  gapFromLeft= gapFromLeft+10;//move the sheep
                  SwingUtilities.invokeLater(updateSheepPanel());//update the panel
                }
              }
            };
            if(!keyT){
              keyTimer= new Timer();
              keyTimer.schedule(t1, 0, sheepSpeed);
              keyT= true;
            }
          }
          else if(e.getKeyCode()==KeyEvent.VK_LEFT){
            TimerTask t1= new TimerTask(){
              public void run(){
                if(!gameOver&&gapFromLeft>=10){
                  gapFromLeft= gapFromLeft-10;//move the sheep
                  SwingUtilities.invokeLater(updateSheepPanel());//update the panel
                }
              }
            };
            if(!keyT){
              keyTimer= new Timer();
              keyTimer.schedule(t1, 0, sheepSpeed);
              keyT= true;
            }
          }
          else if(e.getKeyCode()==KeyEvent.VK_UP){
            TimerTask t1= new TimerTask(){
              public void run(){
                if(!gameOver&&gapFromTop>=10){
                  gapFromTop= gapFromTop-10;
                  SwingUtilities.invokeLater(updateSheepPanel());
                }
              }
            };
            if(!keyT2){
              keyTimer2= new Timer();
              keyTimer2.schedule(t1, 0, sheepSpeed);
              keyT2= true;
            }
          }
          else if(e.getKeyCode()==KeyEvent.VK_DOWN&&gapFromTop<610){
            TimerTask t1= new TimerTask(){
              public void run(){
                if(!gameOver&&gapFromTop<610){
                  gapFromTop= gapFromTop+10;
                  SwingUtilities.invokeLater(updateSheepPanel());
                }
              }
            };
            if(!keyT2){
              keyTimer2= new Timer();
              keyTimer2.schedule(t1, 0, sheepSpeed);
              keyT2= true;
            }
          }
        }
      }
      public void keyReleased(KeyEvent e){
        if(e.getKeyCode()==KeyEvent.VK_R){
          frame.setVisible(false);
          refresh.cancel();
          for(int i= 0; i<height; i++){
            timers[i].cancel();
          }
          new SheepCrossing();
        }
        else if(e.getKeyCode()==KeyEvent.VK_Q){
          System.exit(0);
        }
        else if(e.getKeyCode()==KeyEvent.VK_LEFT||e.getKeyCode()==KeyEvent.VK_RIGHT){
          keyTimer.cancel();
          keyT= false;
        }
        else if(e.getKeyCode()==KeyEvent.VK_UP||e.getKeyCode()==KeyEvent.VK_DOWN){
          keyTimer2.cancel();
          keyT2= false;
        }
      }
      public void keyTyped(KeyEvent e){
      }
    });
  }
  public static void main(String[] args){
    new SheepCrossing();
  }
  public void setImages(){//reads the images into an array of JLabels
    images= new JLabel[14];
    URL[] url= new URL[5];
    url[0]= getClass().getResource("sheep.png");
    url[2]= getClass().getResource("background.png");
    url[1]= getClass().getResource("car3.png");
    url[3]= getClass().getResource("won.png");
    url[4]= getClass().getResource("lost.png");
    try{
      images[0]= new JLabel(new ImageIcon(ImageIO.read(url[0])));
      for(int i= 1; i<11; i++){
        images[i]= new JLabel(new ImageIcon(ImageIO.read(url[1])));
      }
      images[11]= new JLabel(new ImageIcon(ImageIO.read(url[2])));
      images[12]= new JLabel(new ImageIcon(ImageIO.read(url[3])));
      images[13]= new JLabel(new ImageIcon(ImageIO.read(url[4])));
    }catch(Exception e){}
  }
  public Runnable drawBoard(){
    Runnable r= new Runnable(){
      public void run(){
        gl.setAutoCreateGaps(true);
        gl.setAutoCreateContainerGaps(true);
        ParallelGroup horizontalGroup= gl.createParallelGroup(GroupLayout.Alignment.LEADING);
        for(int i= 0; i<height; i++){
          horizontalGroup.addGroup(gl.createSequentialGroup()
                                     .addGap(gap[i])//add the gap
                                     .addComponent(images[i+1])//add the car
                                  );
        }//horizontally, the items are parallel (not one after another)
        gl.setHorizontalGroup(horizontalGroup);
        SequentialGroup verticalGroup= gl.createSequentialGroup();
        for(int i= 0; i<height; i++){
          verticalGroup.addComponent(images[i+1]);//no vertical gaps, just add the car
        }//vertically, the cars are one after another
        gl.setVerticalGroup(verticalGroup);
        JPanel backPanel= new JPanel(new BorderLayout());//JPanel for he background
        backPanel.add(images[11], BorderLayout.NORTH);//add the bg image
        backPanel.setBounds(0, 0, width*60, 650);//set coordinates for backPanel (for use in JLayeredPane)
        frame.setSize(width*60, 650);
        layeredPane.setPreferredSize(new Dimension(width*60, 650));
        panel.setOpaque(false);
        panel.setBounds(0, 0, width*60, 650);
        drawSheepPanel();
        layeredPane.add(panel, 2);
        layeredPane.setLayer(panel, 2);
        layeredPane.add(backPanel, 1);//1 is an index, not a layer
        layeredPane.setLayer(backPanel, 1);
        frame.add(layeredPane, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
      }
    };
    return r;
  }
  public void drawSheepPanel(){
    sl= new GroupLayout(sheepPanel);
    sheepPanel.setLayout(sl);
    sl.setHorizontalGroup(sl.createParallelGroup()//one must be parallel and the other sequential
                            .addGroup(sl.createSequentialGroup()
                                        .addGap(gapFromLeft)
                                        .addComponent(images[0])
                                     )
                         );
    sl.setVerticalGroup(sl.createSequentialGroup()
                          .addGap(gapFromTop)
                          .addComponent(images[0])
                       );
    sheepPanel.setOpaque(false);
    sheepPanel.setBounds(0, 0, 1000, 650);//set bounds for JLayeredPane
    layeredPane.add(sheepPanel, 3);
    layeredPane.setLayer(sheepPanel, 3);
  }
  public Runnable updateBoard(){
    Runnable r= new Runnable(){
      public void run(){
        layeredPane.remove(panel);//remove the JPanel from the JLayeredPane
        panel= new JPanel();//reinitiate the JPanel
        gl= new GroupLayout(panel);
        panel.setLayout(gl);
        gl.setAutoCreateGaps(true);
        gl.setAutoCreateContainerGaps(true);
        ParallelGroup horizontalGroup= gl.createParallelGroup(GroupLayout.Alignment.LEADING);
        for(int i= 0; i<height; i++){
          horizontalGroup.addGroup(gl.createSequentialGroup()
                                     .addGap(gap[i])
                                     .addComponent(images[i+1])
                                  );
        }//horizontally, the items are parallel (not one after another)
        gl.setHorizontalGroup(horizontalGroup);
        SequentialGroup verticalGroup= gl.createSequentialGroup();
        for(int i= 0; i<height; i++){
          verticalGroup.addComponent(images[i+1]);
        }//vertically, the cars are one after another
        gl.setVerticalGroup(verticalGroup);
        panel.setOpaque(false);//sheep panel must be transparent
        panel.setBounds(0, 0, width*60, 650);//set bounds for JLayeredPane
        layeredPane.add(panel, 2);
        layeredPane.setLayer(panel, 2);
        panel.validate();
        frame.repaint();
      }
    };
    return r;
  }
  public Runnable updateSheepPanel(){
    Runnable r= new Runnable(){
      public void run(){
        layeredPane.remove(sheepPanel);
        sheepPanel= new JPanel();
        sl= new GroupLayout(sheepPanel);
        sheepPanel.setLayout(sl);
        sl.setHorizontalGroup(sl.createParallelGroup()
                                .addGroup(sl.createSequentialGroup()
                                            .addGap(gapFromLeft)
                                            .addComponent(images[0])
                                         )
                             );
        sl.setVerticalGroup(sl.createSequentialGroup()
                              .addGap(gapFromTop)
                              .addComponent(images[0])
                           );
        sheepPanel.setOpaque(false);
        sheepPanel.setBounds(0, 0, width*60, 650);
        layeredPane.add(sheepPanel, 3);
        layeredPane.setLayer(sheepPanel, 3);
        frame.pack();
      }
    };
    return r;
  }
  public boolean checkIfCollision(){
    int[] rCar= new int[4];
    int[] rSheep= {gapFromLeft, gapFromLeft+38, gapFromTop, gapFromTop+40};
    for(int i= 0; i<gap.length; i++){
      rCar[0]= gap[i];
      rCar[1]= gap[i]+89;
      rCar[2]= i*54+5;
      rCar[3]= (i+1)*54-2;
      if(rSheep[0]>=rCar[0]&&rSheep[0]<=rCar[1]&&rSheep[2]>=rCar[2]&&rSheep[2]<=rCar[3]){
        gameOver= true;
        JPanel lostPanel= new JPanel();//add new JPanel indicating that the user has lost
        lostPanel.add(images[13]);
        lostPanel.setOpaque(false);
        lostPanel.setBounds(0, 0, width*60, 650);
        layeredPane.add(lostPanel, 4);
        layeredPane.setLayer(lostPanel, 4);
        return true;
      }
      if(rSheep[1]>=rCar[0]&&rSheep[1]<=rCar[1]&&rSheep[2]>=rCar[2]&&rSheep[2]<=rCar[3]){
        gameOver= true;
        JPanel lostPanel= new JPanel();
        lostPanel.add(images[13]);
        lostPanel.setOpaque(false);
        lostPanel.setBounds(0, 0, width*60, 650);
        layeredPane.add(lostPanel, 4);
        layeredPane.setLayer(lostPanel, 4);
        return true;
      }
      if(rSheep[0]>=rCar[0]&&rSheep[0]<=rCar[1]&&rSheep[3]>=rCar[2]&&rSheep[3]<=rCar[3]){
        gameOver= true;
        JPanel lostPanel= new JPanel();
        lostPanel.add(images[13]);
        lostPanel.setOpaque(false);
        lostPanel.setBounds(0, 0, width*60, 650);
        layeredPane.add(lostPanel, 4);
        layeredPane.setLayer(lostPanel, 4);
        return true;
      }
      if(rSheep[1]>=rCar[0]&&rSheep[1]<=rCar[1]&&rSheep[3]>=rCar[2]&&rSheep[3]<=rCar[3]){
        gameOver= true;
        keyTimer.cancel();
        keyTimer2.cancel();
        JPanel lostPanel= new JPanel();
        lostPanel.add(images[13]);
        lostPanel.setOpaque(false);
        lostPanel.setBounds(0, 0, width*60, 650);
        layeredPane.add(lostPanel, 4);
        layeredPane.setLayer(lostPanel, 4);
        return true;
      }
    }
    return false;
  }
  public boolean checkIfWon(){
    if(gapFromTop<11){
      gameOver= true;
      keyTimer.cancel();
      keyTimer2.cancel();
      JPanel wonPanel= new JPanel();//add panel indicating that they have won
      wonPanel.add(images[12]);
      wonPanel.setOpaque(false);
      wonPanel.setBounds(0, 0, width*60, 650);
      layeredPane.add(wonPanel, 4);
      layeredPane.setLayer(wonPanel, 4);
      return true;
    }
    return false;
  }
}
