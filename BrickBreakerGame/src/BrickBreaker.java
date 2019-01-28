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
	
	Thread mainwork;//스레드 객체
	boolean roof=true;//스레드 루프 정보
	
	int gScreenWidth=640;//게임 화면 너비
	int gScreenHeight=480;//게임 화면 높이
	
	long pretime;//루프 간격을 조절하기 위한 시간 체크값
	int delay;//루프 딜레이. 1/1000초 단위.
	int cnt;//루프 제어용 컨트롤 변수

	boolean leftPressed = false;
	boolean rightPressed = false;

	int myx;
	int myWidth;
	
	int ballX, ballY;
	int ballXV, ballYV;

	boolean ballDeparted;
	
	BrickBreaker_frame(){
		//기본적인 윈도우 정보 세팅. 게임과 직접적인 상관은 없이 게임 실행을 위한 창을 준비하는 과정.
		setIconImage(makeImage("./rsc/icon.png"));
		setBackground(new Color(0x000000));//윈도우 기본 배경색 지정 (R=ff, G=ff, B=ff : 하얀색)
		setTitle("BrickBreaker");//윈도우 이름 지정
		setLayout(null);//윈도우의 레이아웃을 프리로 설정
		setBounds(100,100,640,480);//윈도우의 시작 위치와 너비 높이 지정
		setLocationRelativeTo(null);// 윈도우를 모니터의 중앙으로...
		setResizable(false);//윈도우의 크기를 변경할 수 없음
		setVisible(true);//윈도우 표시
		
		addKeyListener(this);//키 입력 이벤트 리스너 활성화
		addWindowListener(new MyWindowAdapter());//윈도우의 닫기 버튼 활성화

		gamescreen=new GameScreen(this);//화면 묘화를 위한 캔버스 객체
		gamescreen.setBounds(0,0,gScreenWidth,gScreenHeight);
		add(gamescreen);//Canvas 객체를 프레임에 올린다
		
		initialize();
		systeminit(); //status의 초기값이 0이기때문에 systeminit()를 건너뛰어 바로 initialize()로 가서 title화면이 나오게된다.
	}
	
	public void systeminit(){//프로그램 초기화

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

				gamescreen.repaint();//화면에 그리기. 화면 리페인트(paint함수를 호출하며 paint함수가 그리기를 담당함)
				process();//각종 처리

				if(System.currentTimeMillis()-pretime<delay) Thread.sleep(delay-System.currentTimeMillis()+pretime);
					//게임 루프를 처리하는데 걸린 시간을 체크해서 딜레이값에서 차감하여 딜레이를 일정하게 유지한다.
					//루프 실행 시간이 딜레이 시간보다 크다면 게임 속도가 느려지게 된다.

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
			ballX = myx + (myWidth/2 - 8)*10000;//8은 공의 너비/2
			ballY = (420 - 15)*10000;
		}else if(ballDeparted == true) {
			ballX += ballXV;
			ballY += ballYV;
			
			if(ballX+15*10000 >= 640*10000) {// 공이 오른쪽 벽에 맞았을때
				ballXV = -ballXV;
			}else if(ballX <= 0) {//공이 왼쪽 벽에 맞았을때
				ballXV = -ballXV;
			}else if(ballY+15*10000 >= 480*10000) {//공이 아래 벽에 맞았을때
				ballYV = -ballYV;
			}else if(ballY-15*10000 <= 0) {//공이 위쪽 벽에 맞았을때
				ballYV = -ballYV;
			}else if(ballY + 15*10000 > 420 * 10000 && ballX + 15*10000 > myx && ballX < myx + myWidth) {//공이 바에 맞으면
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
			//여기부터
			MediaTracker mt = new MediaTracker(this);
			mt.addImage(img, 0);
			mt.waitForID(0);
			//여기까지, getImage로 읽어들인 이미지가 로딩이 완료됐는지 확인하는 부분
		} catch (Exception ee) {
			ee.printStackTrace();
			return null;
		}	
		return img;
	}
	
}

class GameScreen extends Canvas{
	
	BrickBreaker_frame main;
	
	Image dblbuff;//더블버퍼링용 백버퍼
	Graphics gc;//더블버퍼링용 그래픽 컨텍스트
	
	GameScreen(BrickBreaker_frame main){
		this.main=main;
	}
	
	public void paint(Graphics g){
		if(gc==null) {
			dblbuff=createImage(main.gScreenWidth,main.gScreenHeight);//더블 버퍼링용 오프스크린 버퍼 생성. 필히 paint 함수 내에서 해 줘야 한다. 그렇지 않으면 null이 반환된다.
			if(dblbuff==null) System.out.println("오프스크린 버퍼 생성 실패");
			else gc=dblbuff.getGraphics();//오프스크린 버퍼에 그리기 위한 그래픽 컨텍스트 획득
			return;
		}
		update(g);
	}
	public void update(Graphics g){//화면 깜박거림을 줄이기 위해, paint에서 화면을 바로 묘화하지 않고 update 메소드를 호출하게 한다.
		//cnt=main.cnt;
		//gamecnt=main.gamecnt;
		if(gc==null) return;
		dblpaint();//오프스크린 버퍼에 그리기
		g.drawImage(dblbuff,0,0,this);//오프스크린 버퍼를 메인화면에 그린다.
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
	// 윈도우를 닫기 위한 부가 클래스. 실제 닫는 동작은
	// setVisible(false);
	// dispose();
	// System.exit(0);
	// 이상 세 라인으로 이루어진다.
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