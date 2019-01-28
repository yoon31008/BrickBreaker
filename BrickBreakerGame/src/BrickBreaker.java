import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class BrickBreaker {

	public static void main(String[] args) 
	{
		System.out.println("BrickBreaker Start!");
		new BrickBreaker_frame();
	}	
}
class BrickBreaker_frame extends Frame implements KeyListener, Runnable{
	
	GameScreen gamescreen;
	
	Thread mainwork;//������ ��ü
	boolean roof=true;//������ ���� ����
	
	int gScreenWidth=640;//���� ȭ�� �ʺ�
	int gScreenHeight=480;//���� ȭ�� ����
	
	long pretime;//���� ������ �����ϱ� ���� �ð� üũ��
	int delay;//���� ������. 1/1000�� ����.
	int cnt;//���� ����� ��Ʈ�� ����

	boolean leftPressed = false;
	boolean rightPressed = false;

	int myx;
	int myWidth;
	
	int ballX, ballY;
	int ballXV, ballYV;

	boolean ballDeparted;
	
	BrickBreaker_frame(){
		//�⺻���� ������ ���� ����. ���Ӱ� �������� ����� ���� ���� ������ ���� â�� �غ��ϴ� ����.
		setIconImage(makeImage("./rsc/icon.png"));
		setBackground(new Color(0x000000));//������ �⺻ ���� ���� (R=ff, G=ff, B=ff : �Ͼ��)
		setTitle("BrickBreaker");//������ �̸� ����
		setLayout(null);//�������� ���̾ƿ��� ������ ����
		setBounds(100,100,640,480);//�������� ���� ��ġ�� �ʺ� ���� ����
		setLocationRelativeTo(null);// �����츦 ������� �߾�����...
		setResizable(false);//�������� ũ�⸦ ������ �� ����
		setVisible(true);//������ ǥ��
		
		addKeyListener(this);//Ű �Է� �̺�Ʈ ������ Ȱ��ȭ
		addWindowListener(new MyWindowAdapter());//�������� �ݱ� ��ư Ȱ��ȭ

		gamescreen=new GameScreen(this);//ȭ�� ��ȭ�� ���� ĵ���� ��ü
		gamescreen.setBounds(0,0,gScreenWidth,gScreenHeight);
		add(gamescreen);//Canvas ��ü�� �����ӿ� �ø���
		
		initialize();
		systeminit(); //status�� �ʱⰪ�� 0�̱⶧���� systeminit()�� �ǳʶپ� �ٷ� initialize()�� ���� titleȭ���� �����Եȴ�.
	}
	
	public void systeminit(){//���α׷� �ʱ�ȭ

		mainwork = new Thread(this);
		mainwork.start();
	}
	
	public void initialize() {
		myx = (getWidth()/2 - 35)*10000;
		myWidth = 70;
		
		ballXV = 3;
		ballYV = -3;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try
		{
			while(roof){
				pretime=System.currentTimeMillis();

				gamescreen.repaint();//ȭ�鿡 �׸���. ȭ�� ������Ʈ(paint�Լ��� ȣ���ϸ� paint�Լ��� �׸��⸦ �����)
				process();//���� ó��

				if(System.currentTimeMillis()-pretime<delay) Thread.sleep(delay-System.currentTimeMillis()+pretime);
					//���� ������ ó���ϴµ� �ɸ� �ð��� üũ�ؼ� �����̰����� �����Ͽ� �����̸� �����ϰ� �����Ѵ�.
					//���� ���� �ð��� ������ �ð����� ũ�ٸ� ���� �ӵ��� �������� �ȴ�.

				cnt++;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void process() {
		process_MY();
		process_BALL();
	}

	private void process_MY() {
		// TODO Auto-generated method stub
		
		if(leftPressed == true) {
			myx -= 5; 
		}
		
		if(rightPressed == true) {
			myx += 5;
		}
		
		if(myx<0) myx=0;
		if(myx>6400000 - myWidth*10000) myx=6400000 - myWidth*10000;
	}
	
	private void process_BALL() {
		if(ballDeparted == false) {
			ballX = myx + (myWidth/2 - 8)*10000;//8�� ���� �ʺ�/2
			ballY = (420 - 15)*10000;
		}else if(ballDeparted == true) {
			ballX += ballXV;
			ballY += ballYV;
			
			if(ballX+15*10000 >= 640*10000) {// ���� ������ ���� �¾�����
				ballXV = -ballXV;
			}else if(ballX <= 0) {//���� ���� ���� �¾�����
				ballXV = -ballXV;
			}else if(ballY+15*10000 >= 480*10000) {//���� �Ʒ� ���� �¾�����
				ballYV = -ballYV;
			}else if(ballY-15*10000 <= 0) {//���� ���� ���� �¾�����
				ballYV = -ballYV;
			}else if(ballY + 15*10000 > 420 * 10000 && ballX + 15*10000 > myx && ballX < myx + myWidth) {//���� �ٿ� ������
				ballYV = -ballYV;
			}
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		switch(e.getKeyCode()) {
		
		case KeyEvent.VK_LEFT:
			leftPressed = true;
			break;
		case KeyEvent.VK_RIGHT:
			rightPressed = true;
			break;
		case KeyEvent.VK_SPACE:
			ballDeparted = true;
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		switch(e.getKeyCode()) {
		
		case KeyEvent.VK_LEFT:
			leftPressed = false;
			break;
			
		case KeyEvent.VK_RIGHT:
			rightPressed = false;
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public Image makeImage(String furl){
		Image img;
		Toolkit tk=Toolkit.getDefaultToolkit();
		img=tk.getImage(furl);
		try {
			//�������
			MediaTracker mt = new MediaTracker(this);
			mt.addImage(img, 0);
			mt.waitForID(0);
			//�������, getImage�� �о���� �̹����� �ε��� �Ϸ�ƴ��� Ȯ���ϴ� �κ�
		} catch (Exception ee) {
			ee.printStackTrace();
			return null;
		}	
		return img;
	}
	
}

class GameScreen extends Canvas{
	
	BrickBreaker_frame main;
	
	Image dblbuff;//������۸��� �����
	Graphics gc;//������۸��� �׷��� ���ؽ�Ʈ
	
	GameScreen(BrickBreaker_frame main){
		this.main=main;
	}
	
	public void paint(Graphics g){
		if(gc==null) {
			dblbuff=createImage(main.gScreenWidth,main.gScreenHeight);//���� ���۸��� ������ũ�� ���� ����. ���� paint �Լ� ������ �� ��� �Ѵ�. �׷��� ������ null�� ��ȯ�ȴ�.
			if(dblbuff==null) System.out.println("������ũ�� ���� ���� ����");
			else gc=dblbuff.getGraphics();//������ũ�� ���ۿ� �׸��� ���� �׷��� ���ؽ�Ʈ ȹ��
			return;
		}
		update(g);
	}
	public void update(Graphics g){//ȭ�� ���ڰŸ��� ���̱� ����, paint���� ȭ���� �ٷ� ��ȭ���� �ʰ� update �޼ҵ带 ȣ���ϰ� �Ѵ�.
		//cnt=main.cnt;
		//gamecnt=main.gamecnt;
		if(gc==null) return;
		dblpaint();//������ũ�� ���ۿ� �׸���
		g.drawImage(dblbuff,0,0,this);//������ũ�� ���۸� ����ȭ�鿡 �׸���.
	}
	
	public void dblpaint() {
		gc.clearRect(0, 0, main.gScreenWidth, main.gScreenHeight);
		Draw_MY();
		Draw_Ball();
	}
	
	public void Draw_MY() {
		int myx;
		myx = main.myx/10000;
		gc.setColor(Color.GREEN);
		gc.fillRect(myx, 420, main.myWidth, 10);
	}
	
	public void Draw_Ball() {
		int ballX, ballY;
		ballX = main.ballX/10000;
		ballY = main.ballY/10000;
		gc.setColor(Color.GRAY);
		gc.fillOval(ballX, ballY, 15, 15);
	}
}

class MyWindowAdapter extends WindowAdapter
{
	// �����츦 �ݱ� ���� �ΰ� Ŭ����. ���� �ݴ� ������
	// setVisible(false);
	// dispose();
	// System.exit(0);
	// �̻� �� �������� �̷������.
	//
	MyWindowAdapter(){
	}
	public void windowClosing(WindowEvent e) {
		Window wnd = e.getWindow();
		wnd.setVisible(false);
		wnd.dispose();
		System.exit(0);
	}
}