package com.example.softwaregamecliantandroid;

import javax.microedition.khronos.opengles.GL10;
import android.app.Activity;
import android.graphics.*;
import android.graphics.Bitmap.Config;
import android.view.MotionEvent;

public class GameThread extends Thread {

	//円周率
	public static final float PI = 3.14159265358979f;

	private GLActivity activity;
	private GL10 gl;
	private GLRenderer renderer;

	public boolean startFlag;

	float[] vertexs = new float[12];
	float[] textures = new float[8];
	float[] colors = new float[16];

	private AttackMotion[][] attackMotion = new AttackMotion[2][4];

	// パーティクルシステム配列
	public static ParticleSystem3D[] ps = new ParticleSystem3D[4];

	public Charactor[] player = new Charactor[2];

	public UDPConnect udp;

	private float WIDTH;
	private float HEIGHT;

	private float w;
	private float h;

	private int[] nameLength = new int[4];

	private double moveLength;

	// アナログパッド制御
	private boolean analogFlag;
	private double analogSin;
	private double analogCos;
	private int analogID = 10;

	// テクスチャーＩＤ
	private int[] background = new int[4];
	private int[] countDownImage = new int[3];
	private int[] targetImage = new int[2];
	private int field_circle;

	private int button;
	private int[] circle = new int[2];

	public int[][] player_im = new int[2][9];
	public int[][] div = new int[2][9];

	public int[] charWin_im = new int[4];

	public int[][] direction_im = new int[2][2];

	public int hp_bar;
	public int[] nameTexture = new int[4];

	public int numberBase;
	public int[] runkTexture = new int[2];
	public int afterHP;

	public int[][] awake_efe = new int[2][3];
	public int[][] swordSlash = new int[2][4];
	public int[] magicBase = new int[2];
	public int[] magicStar = new int[2];
	public int effectBase;
	public int[] sara = new int[2];
	public int[] brush = new int[2];
	public int[] ougi = new int[2];
	public int[] sonic = new int[2];
	public int[] fuda = new int[2];
	public int[] cloud = new int[2];
	public int[] thander = new int[2];

	// ゲーム制御
	public static final int CONNECT = 0;
	public static final int COUNT = 1;
	public static final int GAME = 2;
	public static final int GAME_FINISH = 3;
	public static final int RESULT = 4;

	public int init;

	// 全クライアントの情報
	private String[] userName = new String[2];
	private int[] selectChar = new int[2];

	// フィールドの中心の絶対座標
	private int fieldCenter_x;
	private int fieldCenter_y;

	// ディスプレイの古い中心の絶対座標
	public int displayCenter_x;
	public int displayCenter_y;
	public float displayCenter_z;

	// ディスプレイの新しい中心座標
	private int newDisplayCenter_x;
	private int newDisplayCenter_y;
	private float newDisplayCenter_z;

	// ディスプレイの新旧座標における三角関数
	private double displaySin;
	private double displayCos;

	// 送信データ
	public String send;
	public String sendX;
	public String sendY;

	// プレイヤーナンバー
	public int myNumber;
	public int otherNumber;

	// FPS計測用
	private long time;
	private long timeDelta;
	private long fps;

	double slash;

	int newDisplay_height;

	double longDistance;

	int countDown = 0;

	boolean hitFlag = true;

	private int targetNumber = 0;
	public int aimNumber;

	private int dethCount = 0;

	private int timeCount = 0;

	private int runkCount;

	private long startTime;

	private long finishTime;

	long countStartTime = 0;

	int xMax, xMin;
	int yMax, yMin;

	public GameThread(GL10 gl, GLActivity activity, GLRenderer renderer,
			String myName, String otherName, int myChar, int otherChar,
			int myNumber) {
		int mRand = 0;

		this.gl = gl;
		this.activity = activity;
		this.renderer = renderer;

		this.myNumber = myNumber;

		aimNumber = myNumber + 1;

		runkCount = 2;

		if (aimNumber == 2) {
			aimNumber = 0;
		}

		if (myNumber == 0) {
			otherNumber = 1;

			userName[0] = myName;
			userName[1] = otherName;

			selectChar[0] = myChar;
			selectChar[1] = otherChar;
		} else if (myNumber == 1) {
			otherNumber = 0;

			userName[0] = otherName;
			userName[1] = myName;

			selectChar[0] = otherChar;
			selectChar[1] = myChar;
		}

		init = CONNECT;

		fieldCenter_x = 500;
		fieldCenter_y = 500;

		for (int i = 0; i < 2; i++) {
			switch (i) {

			case 0:
				player[i] = new Charactor(i, selectChar[i],
						fieldCenter_x - 100, fieldCenter_y);
				break;

			case 1:
				player[i] = new Charactor(i, selectChar[i],
						fieldCenter_x + 100, fieldCenter_y);
				break;

			}
		}

		try{
			this.loadTexture(gl);
		}catch (Exception e){
			e.printStackTrace();
		}

		player[myNumber].aimNum = aimNumber;

		// ディスプレイの中心はキャラクター同士の中央に設定
		displayCenter_x = (player[0].x + player[1].x) / 2;
		displayCenter_y = (player[0].y + player[1].y) / 2;

		for (int i = 0; i < 2; i++) {
			player[i].firstSet();

			mRand += selectChar[i];

			ps[i] = new ParticleSystem3D(gl, renderer);
			ps[i].setColor(0.0f, 0.0f, 0.0f, 1.0f);
		}

		displayCenter_z = -160;

		time = System.currentTimeMillis();
		fps = 0;

		try {
			this.start();
			this.udp = UDPConnect.udp;
			udp.setObject(this);
			udp.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// 画面のサイズをセット
	public void setDisplaySize(float w, float h) {
		this.WIDTH = w;
		this.HEIGHT = h;
	}

	// 視体積の範囲をセット
	public void setView(float w, float h) {
		this.w = w;
		this.h = h;
	}

	public void run() {
		long error = 0;
		int baseFps = 60;
		long idealSleep = (1000 << 16) / baseFps;
		long oldTime;
		long newTime = System.currentTimeMillis() << 16;

		startFlag = true;

		while (startFlag) {
			oldTime = newTime;

			if (init == CONNECT) {
				if (udp != null) {
					if (udp.success) {
						countStartTime = System.currentTimeMillis();
						init = COUNT;
						countDown = 3;
					}
				}

			} else if (init == COUNT) {

				if (System.currentTimeMillis() > countStartTime + 4000
						- countDown * 1000) {
					countDown--;

					if (countDown == 0) {
						init = GAME;
						startTime = System.currentTimeMillis();
					}
				}

			} else if (init == GAME) {
				myCharMove();

				for (int i = 0; i < 2; i++) {
					if (i != myNumber) {
						if (player[i].btsFlag && player[i].recvByte.length > 5) {
							String str = new String(player[i].recvByte, 0,
									player[i].recvByte.length);

							try {
								// Integer.parseInt(文字列)で文字列をint型へ変換
								if (str.length() < 1)
									continue;

								if (str.indexOf("E") != -1) {
									// サーバから送られてきた各クライアントの座標をキャラクターオブジェクトのx,
									// yにセットする
									player[otherNumber].nx = player[i].recvByte[0]
											* 10 + player[i].recvByte[1];
									player[otherNumber].ny = player[i].recvByte[2]
											* 10 + player[i].recvByte[3];
									player[otherNumber].hp = player[i].recvByte[4];

									if (str.substring(str.indexOf("S") + 1,
											str.indexOf("T")).equals("0")) {
										player[otherNumber].styleChange = false;

									} else if (str.substring(
											str.indexOf("S") + 1,
											str.indexOf("T")).equals("1")) {
										player[otherNumber].styleChange = true;
										if(player[otherNumber].selectChar == 1)
			                				player[otherNumber].awakeFlag = true;
									}

									player[otherNumber].aimNum = Integer
											.parseInt(str.substring(
													str.indexOf('T') + 1,
													str.indexOf('D')));

					                if(str.substring(str.indexOf("D")+1, str.indexOf("D")+2).equals("0")){
			                			player[otherNumber].dashFlag = false;

			                		}else if(str.substring(str.indexOf("D")+1, str.indexOf("D")+2).equals("1")){
			                			player[otherNumber].dashFlag = true;
			                		}

									player[otherNumber].recvData = str
											.substring(str.indexOf("D") + 2);

									player[i].btsFlag = false;
								}

							} catch (StringIndexOutOfBoundsException e) {
								e.printStackTrace();
							}
						}

						player[i].attackData2[0] = player[i].recvData
								.substring(0,
										player[i].recvData.indexOf("A") + 3);
						player[i].attackData2[1] = player[i].recvData
								.substring(
										player[i].recvData.indexOf("A") + 3,
										player[i].recvData
												.indexOf(
														"A",
														player[i].recvData
																.indexOf("A") + 3) + 3);
						player[i].attackData2[2] = player[i].recvData
								.substring(
										player[i].recvData
												.indexOf(
														"A",
														player[i].recvData
																.indexOf("A") + 3) + 3,
										player[i].recvData.lastIndexOf("E"));

						for (int j = 0; j < 3; j++) {
							if (player[i].attackData2[j].substring(0,
									player[i].attackData2[j].indexOf("A") + 3)
									.equals("noDataA  "))
								continue;

							else {
								if (player[i].doCount < Integer
										.parseInt(player[i].attackData2[j]
												.substring(
														0,
														player[i].attackData2[j]
																.indexOf("A")))) {

									if (player[i].attackData2[j]
											.substring(
													player[i].attackData2[j]
															.indexOf("A") + 1,
													player[i].attackData2[j]
															.indexOf("A") + 2)
											.equals("1")) {
										player[i].attackFlag1 = true;

									} else if (player[i].attackData2[j]
											.substring(
													player[i].attackData2[j]
															.indexOf("A") + 1,
													player[i].attackData2[j]
															.indexOf("A") + 2)
											.equals("2")) {
										player[i].attackFlag2 = true;
									}

									if (player[i].attackData2[j]
											.substring(
													player[i].attackData2[j]
															.indexOf("A") + 2,
													player[i].attackData2[j]
															.indexOf("A") + 3)
											.equals("1")) {
										player[i].chargeFlag = true;
									}

									player[i].doCount++;
									break;

								} else
									continue;
							}
						}
					}

					if(!player[i].aMotionFlag){
						if (player[i].attackFlag1 || player[i].attackFlag2) {
							for (int j = 0; j < 2; j++) {
								if (attackMotion[i][j] == null) {
									attackMotion[i][j] = new AttackMotion(
											selectChar[i], player[i],
											player[player[i].aimNum]);
									break;

								} else if (!attackMotion[i][j].allLiveFlag) {
									attackMotion[i][j] = new AttackMotion(
											selectChar[i], player[i],
											player[player[i].aimNum]);
									break;
								}
							}

							if (player[i].attackFlag1)
								player[i].attackFlag1 = false;
							else if (player[i].attackFlag2)
								player[i].attackFlag2 = false;

						}
					}

					if (player[i].nockBackFlag) {
						player[i].countFrame++;

						if(player[i].countFrame == 20){
							player[i].state = 0;

						}else if (player[i].countFrame == 60) {
							player[i].nockBackFlag = false;
							player[i].countFrame = 0;
						}
					}

					if(player[i].sNockBackFlag){
						player[i].countFrame++;

						if (player[i].countFrame == 10) {
							player[i].sNockBackFlag = false;
							player[i].countFrame = 0;
							player[i].state = 0;
						}
					}

					player[i].move();
					charCheck(i, player[i]);
					player[i].reRotate(player[player[i].aimNum]);

					if (i == 0) {
						xMax = player[i].x;
						xMin = xMax;
						yMax = player[i].y;
						yMin = yMax;

						longDistance = Math.sqrt(Math.pow(
								(player[i].x - displayCenter_x), 2)
								+ Math.pow((player[i].y - displayCenter_y), 2));

					} else {
						if (xMax < player[i].x) {
							xMax = player[i].x;
						} else if (xMin > player[i].x) {
							xMin = player[i].x;
						}

						if (yMax < player[i].y) {
							yMax = player[i].y;
						} else if (yMin > player[i].y) {
							yMin = player[i].y;
						}

						if (longDistance < Math.sqrt(Math.pow(
								(player[i].x - displayCenter_x), 2)
								+ Math.pow((player[i].y - displayCenter_y), 2))) {
							longDistance = Math.sqrt(Math.pow(
									(player[i].x - displayCenter_x), 2)
									+ Math.pow((player[i].y - displayCenter_y),
											2));
						}
					}

				}

				hpCheck();

				// ディスプレイの中心はキャラクター同士の中央に設定
				newDisplayCenter_x = (xMax + xMin) / 2;
				newDisplayCenter_y = (yMax + yMin) / 2;

				slash = Math.sqrt(Math.pow(
						(newDisplayCenter_x - displayCenter_x), 2)
						+ Math.pow((newDisplayCenter_y - displayCenter_y), 2));

				if (slash != 0) {

					displayCos = (newDisplayCenter_x - displayCenter_x) / slash;

					displaySin = (newDisplayCenter_y - displayCenter_y) / slash;
				}

				displayCenter_x += (slash / 15) * displayCos;
				displayCenter_y += (slash / 15) * displaySin;

				newDisplay_height = (int) (-displayCenter_z * 2 * (longDistance / ((-displayCenter_z * 2 - 100) / 2)));

				if (newDisplay_height <= 300) {
					newDisplay_height = 300;

				} else if (newDisplay_height >= 900) {
					newDisplay_height = 900;
				}

				newDisplayCenter_z = (float) (displayCenter_z - (newDisplay_height + displayCenter_z * 2) / 2);

				if (newDisplayCenter_z < -440) {
					newDisplayCenter_z = -440;

				} else if (newDisplayCenter_z > -160) {
					newDisplayCenter_z = -160;
				}

				displayCenter_z += (newDisplayCenter_z - displayCenter_z) / 10;

				send = "S" + ((player[myNumber].styleChange) ? 1 : 0)
						+ "T" + player[myNumber].aimNum
						+ "D" + ((player[myNumber].dashFlag) ? 1 : 0)
						+ player[myNumber].attackData[0]
						+ player[myNumber].attackData[1]
						+ player[myNumber].attackData[2] + "E";
				udp.send(player[myNumber].x, player[myNumber].y,
						player[myNumber].hp, send);

			} else if (init == GAME_FINISH) {
				if (timeCount == 2) {
					timeCount = 0;
					init = RESULT;
					finishTime = System.currentTimeMillis();
				}

			} else if (init == RESULT) {
				if(System.currentTimeMillis() >= finishTime + 8000){
					startFlag = false;
					activity.changeActivity();
				}
			}

			if (fps % 10 == 0) {
				targetNumber++;
				if (targetNumber == 2) {
					targetNumber = 0;
				}
			}

			try {
				newTime = System.currentTimeMillis() << 16;
				long sleepTime = idealSleep - (newTime - oldTime) - error; // 休止できる時間
				if (sleepTime < 0x20000)
					sleepTime = 0x20000; // 最低でも2msは休止
				oldTime = newTime;
				Thread.sleep(sleepTime >> 16); // 休止
				newTime = System.currentTimeMillis() << 16;
				error = newTime - oldTime - sleepTime; // 休止時間の誤差
			} catch (Exception e) {
				e.printStackTrace();
			}

			calcFps();
		}
	}

	// キャラクターの状態チェック
	public void charCheck(int pNumber, Charactor charactor) {
		int num = 0;

		charactor.reCalc(fieldCenter_x, fieldCenter_y);

		// 戦闘エリア外に出ている場合は位置調整
		if (charactor.distanceFromCenter >= fieldCenter_y - 30) {
			charactor.x = fieldCenter_x
					+ (int) (charactor.cos * (fieldCenter_y - 30));
			charactor.nx = charactor.x;
			charactor.y = fieldCenter_y
					+ (int) (charactor.sin * (fieldCenter_y - 30));
			charactor.ny = charactor.y;
		}

		charactor.aFrameCount++;

		if(charactor.lockFlag){
			charactor.lockCount++;
			if(charactor.lockCount == 10){
				charactor.lockFlag = false;
				charactor.lockCount = 0;
			}
		}

		if(!charactor.aMotionFlag){
			if(charactor.aFrameCount >= 10){
				charactor.animeState++;
				charactor.aFrameCount = 0;
			}

			if(charactor.animeState >= div[pNumber][charactor.state])
				charactor.animeState = 0;

		}else if(charactor.aMotionFlag){
			if(selectChar[pNumber] == 1)
				num = 4;

			else if(selectChar[pNumber] == 2){
				if(charactor.state <= 4){
					if(div[pNumber][charactor.state] == 1)
						num = 10;
					else if(div[pNumber][charactor.state] <= 4)
						num = 8;
					else if(div[pNumber][charactor.state] <= 6)
						num = 10;
					else if(div[pNumber][charactor.state] <= 9)
						num = 4;
				}else if(charactor.state == 5)
					num = 4;
				else if(charactor.state == 6)
					num = 6;
				else if(charactor.state == 7)
					num = 8;
				else if(charactor.state == 8)
					num = 6;

			}else {
				if(div[pNumber][charactor.state] == 1)
					num = 10;
				else if(div[pNumber][charactor.state] <= 4)
					num = 8;
				else if(div[pNumber][charactor.state] <= 6)
					num = 10;
				else if(div[pNumber][charactor.state] <= 9)
					num = 4;
			}

			if(charactor.aFrameCount >= num){
				charactor.animeState++;
				charactor.aFrameCount = 0;
			}

			if(charactor.state != 4){
				if(charactor.animeState >= div[pNumber][charactor.state]){
					charactor.animeState = 0;
					charactor.state = 0;
					charactor.aMotionFlag = false;
				}
			}
		}
	}

	// HPチェック
	public void hpCheck() {
		for (int i = 0; i < 2; i++) {
			if (player[i].hp <= 0 && !player[i].dethFlag) {
				player[i].hp = 0;
				runkCount--;
				player[i].myRunk = runkCount;
				dethCount++;
				player[i].dethFlag = true;
				player[i].endTime = System.currentTimeMillis();
				player[i].pastTime = (player[i].endTime - startTime) / 1000;
			}
		}

		if (dethCount == 1) {
			for (int i = 0; i < 2; i++) {
				if (!player[i].dethFlag) {
					player[i].myRunk = 0;

				} else {
					player[i].liveTime[0] = (int) (player[i].pastTime / 60);
					player[i].liveTime[1] = ':';
					if (player[i].pastTime % 60 < 10) {
						player[i].liveTime[2] = 0;
						player[i].liveTime[3] = (int) (player[i].pastTime % 60);
					} else {
						player[i].liveTime[2] = (int) ((player[i].pastTime % 60) / 10);
						player[i].liveTime[3] = (int) (player[i].pastTime % 10);
					}
				}
			}
			timeCount = 0;
			init = GAME_FINISH;
		}
	}

	// FPSの計算
	private void calcFps() {
		long now = System.currentTimeMillis();

		// 前のフレームからの経過時間を求め、timeDelta に足す
		timeDelta += now - time;

		time = now;

		if (1000 <= timeDelta) {
			// timeDelta が 1 秒を上回ったら、FPS をリセットする
			timeDelta -= 1000;

			fps = 1;
			timeCount++;

			for(int i=0; i<2; i++){
				if(selectChar[i] == 1){
					if(!player[i].awakeFlag){
						player[i].awake_num++;
						if(player[i].awake_num >= 15)
							player[i].awake_num = 15;

					}else {
						player[i].awake_num -= 2;
						if(player[i].awake_num <= 0){
							player[i].awake_num = 0;
							player[i].awakeFlag = false;
							player[i].styleChange = false;
						}
					}
				}
			}

		} else {
			fps++;
		}
	}

	// タッチイベントの処理
	public void touchEvent(MotionEvent event) {
		try {
			float left = displayCenter_x - w / 2;
			float right = displayCenter_x + w / 2;
			float bottom = displayCenter_y - h / 2 + 40;

			float defaltCenter_x = left + 50;
			float defaltCenter_y = bottom + 50;

			int index = event.getActionIndex();
			int pointerID = event.getPointerId(index);
			int action = event.getAction();

			float newTouch_x = (float)(left + w * event.getX(pointerID) / WIDTH);
			float newTouch_y = (float)(bottom + h * (HEIGHT - event.getY(pointerID))
					/ HEIGHT);

			for(int i=0; i<event.getPointerCount(); i++){
				float touch_x = left + w * event.getX(i) / WIDTH;
				float touch_y = bottom + h * (HEIGHT - event.getY(i))
						/ HEIGHT;

				switch (action & MotionEvent.ACTION_MASK) {

					case MotionEvent.ACTION_DOWN:
					case MotionEvent.ACTION_POINTER_DOWN:
						if (touch_x < displayCenter_x) {
							if (touch_x >= left + 20 && touch_x <= left + 80
									&& touch_y >= bottom + 20 && touch_y <= bottom + 80) {
								analogFlag = true;
								analogID = i;

								moveLength = Math.sqrt(Math.pow(
										(touch_x - defaltCenter_x), 2)
										+ Math.pow((touch_y - defaltCenter_y), 2));

								analogSin = (touch_y - defaltCenter_y) / moveLength;
								analogCos = (touch_x - defaltCenter_x) / moveLength;

								break;
							}
						}else if (touch_x >= displayCenter_x) {
							player[myNumber].dashFlag = false;

							// ボタン１の処理
							if (touch_x > right - 80 && touch_x < right - 40
									&& touch_y > bottom + 80 && touch_y < bottom + 120) {
								if (!player[myNumber].attackFlag2) {
									player[myNumber].attackFlag2 = true;

									player[myNumber].attackCount++;

									player[myNumber].attackData[0] = player[myNumber].attackData[1];
									player[myNumber].attackData[1] = player[myNumber].attackData[2];
									player[myNumber].attackData[2] = String
											.valueOf(player[myNumber].attackCount)
											+ "A"
											+ 2
											+ ((player[myNumber].chargeFlag) ? 1 : 0);
								}

								// ボタン２の処理
							} else if (touch_x > right - 40 && touch_x < right
									&& touch_y > bottom + 40 && touch_y < bottom + 80) {
								if (!player[myNumber].attackFlag1) {
									player[myNumber].attackFlag1 = true;
									player[myNumber].chargeFrame = 0;

									player[myNumber].attackCount++;

									player[myNumber].attackData[0] = player[myNumber].attackData[1];
									player[myNumber].attackData[1] = player[myNumber].attackData[2];
									player[myNumber].attackData[2] = String
											.valueOf(player[myNumber].attackCount)
											+ "A"
											+ 1
											+ ((player[myNumber].chargeFlag) ? 1 : 0);
								}

								// ボタン３の処理
							} else if (touch_x > right - 80 && touch_x < right - 40
									&& touch_y > bottom && touch_y < bottom + 40) {
								player[myNumber].dashFlag = true;

								// ボタン４の処理
							} else if (touch_x > right - 120 && touch_x < right - 80
									&& touch_y > bottom + 40 && touch_y < bottom + 80) {

								if(player[myNumber].selectChar != 1){
									if(!player[myNumber].styleChange){
										player[myNumber].styleChange = true;
									}else {
										player[myNumber].styleChange = false;
									}

								}else {
									if(!player[myNumber].awakeFlag){
										player[myNumber].awakeFlag = true;
										player[myNumber].styleChange = true;
									}
								}
							}
						}

						break;

					case MotionEvent.ACTION_UP:
					case MotionEvent.ACTION_POINTER_UP:
						if (newTouch_x < displayCenter_x) {
							analogFlag = false;
							analogID = 10;
							player[myNumber].dashFlag = false;
						}else {
							if (newTouch_x > right - 80 && newTouch_x < right - 40
									&& newTouch_y > bottom && newTouch_y < bottom + 40) {
								player[myNumber].dashFlag = false;
							}
						}

						break;

					case MotionEvent.ACTION_MOVE:
						if (analogFlag) {
							if (touch_x < displayCenter_x) {
								analogID = i;
								moveLength = Math.sqrt(Math.pow(
										(touch_x - defaltCenter_x), 2)
										+ Math.pow((touch_y - defaltCenter_y), 2));

								analogSin = (touch_y - defaltCenter_y) / moveLength;
								analogCos = (touch_x - defaltCenter_x) / moveLength;

							}
						}
						break;
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 自キャラの移動処理
	public void myCharMove() {
		if (analogFlag) {
			if (moveLength >= 10) {
				double speed = 5;

				if(selectChar[myNumber] == 1){
					if(player[myNumber].awakeFlag){
						if(player[myNumber].dashFlag)
							speed = 13;
						else
							speed = 8;
					}else{
						if(player[myNumber].dashFlag)
							speed = 10;
						else
							speed = 5;
					}
				}else if(selectChar[myNumber] == 2){
					if(player[myNumber].dashFlag || player[myNumber].tDashFlag)
						speed = 10;
					else
						speed = 5;
				}else{
					if(player[myNumber].dashFlag)
						speed = 10;
					else
						speed = 5;
				}

				player[myNumber].nx += (int) (speed * analogCos);
				player[myNumber].ny += (int) (speed * analogSin);
			}
		}
	}

	// ゲーム画面の描画処理
	public void subRenderer() {
		if (init == CONNECT) {
			drawPicture(background[0], 0, 1000, 1000, 0, 0, null);

		} else if (init == COUNT) {
			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glFrustumf(-(float) (150 * renderer.aspect),
					(float) (150 * renderer.aspect), -150, 150, 150, 450);
			gl.glTranslatef((float) -displayCenter_x,
					(float) -(displayCenter_y + 40), displayCenter_z);

			gl.glMatrixMode(GL10.GL_MODELVIEW);

			drawPicture(background[1], -500, 1500, 1500, -500, 0, null);
			drawPicture(field_circle, 0, 1000, 1000, 0, 0, null);

			for (int i = 0; i < 2; i++) {
				drawChar(i, player[i]);

				if (!player[i].styleChange)
					drawPicture(direction_im[i][0], player[i].x - 25,
							player[i].x + 55, player[i].y + 35,
							player[i].y + 15, 0, null);
				else
					drawPicture(direction_im[i][1], player[i].x - 25,
							player[i].x + 55, player[i].y + 35,
							player[i].y + 15, 0, null);
			}

			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glOrthof(-(float) (150 * renderer.aspect),
					(float) (150 * renderer.aspect), -150, 150, 150, 450);
			gl.glTranslatef((float) -500, (float) -540, displayCenter_z);
			drawCircle();
			drawButton();
			drawName();
			drawHP();

			gl.glMatrixMode(GL10.GL_MODELVIEW);

			if (countDown - 1 >= 0)
				drawPicture(countDownImage[countDown - 1],
						displayCenter_x - 200, displayCenter_x + 200,
						displayCenter_y + 240, displayCenter_y - 160, 0, null);

		} else if (init == GAME) {
			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glFrustumf(-(float) (150 * renderer.aspect),
					(float) (150 * renderer.aspect), -150, 150, 150, 450);
			gl.glTranslatef((float) -displayCenter_x,
					(float) -(displayCenter_y + 40), displayCenter_z);

			gl.glMatrixMode(GL10.GL_MODELVIEW);

			drawPicture(background[1], -500, 1500, 1500, -500, 0, null);
			drawPicture(field_circle, 0, 1000, 1000, 0, 0, null);

			drawPicture(targetImage[targetNumber], player[aimNumber].x - 30,
					player[aimNumber].x + 30, player[aimNumber].y + 30,
					player[aimNumber].y - 30, 0, null);

			for (int i = 0; i < 2; i++) {

				if(selectChar[i] == 1){
					if(player[i].awakeFlag){
						player[i].awakeAnimeCount++;
						if(player[i].awakeAnimeCount == 18)
							player[i].awakeAnimeCount = 0;

						drawPicture(awake_efe[i][player[i].awakeAnimeCount/6], player[i].x-20, player[i].x+20,
								player[i].y+50, player[i].y-30, 0, null);
					}

					drawAwake(i, player[i].x, player[i].y);
				}

				drawChar(i, player[i]);

				if (!player[i].styleChange)
					drawPicture(direction_im[i][0], player[i].x - 25,
							player[i].x + 55, player[i].y + 35,
							player[i].y + 15, 0, null);
				else
					drawPicture(direction_im[i][1], player[i].x - 25,
							player[i].x + 55, player[i].y + 35,
							player[i].y + 15, 0, null);
			}

			drawAttack();

			for(int i=0; i<2; i++){
				if(ps[i].liveFlag)
					ps[i].upDate();
			}

			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glOrthof(-(float) (150 * renderer.aspect),
					(float) (150 * renderer.aspect), -150, 150, 150, 450);
			gl.glTranslatef((float) -500, (float) -540, displayCenter_z);
			drawCircle();
			drawButton();
			drawName();
			drawHP();

			gl.glMatrixMode(GL10.GL_MODELVIEW);

		} else if (init == GAME_FINISH) {
			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glFrustumf(-(float) (150 * renderer.aspect),
					(float) (150 * renderer.aspect), -150, 150, 150, 450);
			gl.glTranslatef((float) -displayCenter_x,
					(float) -(displayCenter_y + 40), displayCenter_z);

			gl.glMatrixMode(GL10.GL_MODELVIEW);

			drawPicture(background[1], -500, 1500, 1500, -500, 0, null);
			drawPicture(field_circle, 0, 1000, 1000, 0, 0, null);

			drawPicture(targetImage[targetNumber], player[aimNumber].x - 30,
					player[aimNumber].x + 30, player[aimNumber].y + 30,
					player[aimNumber].y - 30, 0, null);

			for (int i = 0; i < 2; i++) {
				drawChar(i, player[i]);

				if (!player[i].styleChange)
					drawPicture(direction_im[i][0], player[i].x - 25,
							player[i].x + 55, player[i].y + 35,
							player[i].y + 15, 0, null);
				else
					drawPicture(direction_im[i][1], player[i].x - 25,
							player[i].x + 55, player[i].y + 35,
							player[i].y + 15, 0, null);
			}

			drawAttack();

			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glOrthof(-(float) (150 * renderer.aspect),
					(float) (150 * renderer.aspect), -150, 150, 150, 450);
			gl.glTranslatef((float) -500, (float) -540, displayCenter_z);
			drawCircle();
			drawButton();
			drawName();
			drawHP();

			drawPicture(background[2], 350, 650, 690, 390, 0, null);

			gl.glMatrixMode(GL10.GL_MODELVIEW);

		} else if (init == RESULT) {
			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glOrthof(-(float) (500 * renderer.aspect),
					(float) (500 * renderer.aspect), -500, 500, 150, 450);
			gl.glTranslatef((float) -500, (float) -540, displayCenter_z);

			drawPicture(background[3], -500, 1500, 1050, 50, 0, null);
			for (int i = 0; i < 2; i++) {
				switch (selectChar[i]) {
				case 1:
				case 2:
				case 4:
					if (i == 0)
						drawPicture(charWin_im[selectChar[i] - 1],
								(float) (1400 - 500 * renderer.aspect),
								(float) (500 - 500 * renderer.aspect), 940, 40,
								0, null);
					else if (i == 1)
						drawPicture(charWin_im[selectChar[i] - 1],
								(float) (-400 + 500 * renderer.aspect),
								(float) (500 + 500 * renderer.aspect), 940, 40,
								0, null);

					break;

				case 3:
					if (i == 0)
						drawPicture(charWin_im[selectChar[i] - 1],
								(float) (950 - 500 * renderer.aspect),
								(float) (500 - 500 * renderer.aspect), 940, 40,
								0, null);
					else if (i == 1)
						drawPicture(charWin_im[selectChar[i] - 1],
								(float) (50 + 500 * renderer.aspect),
								(float) (500 + 500 * renderer.aspect), 940, 40,
								0, null);

					break;
				}
			}

			drawName();

			for (int i = 0; i < 2; i++) {
				if (timeCount >= 1) {
					if (i == 0)
						drawPicture(runkTexture[player[i].myRunk], 200, 400,
								750, 650, 0, null);
					else if (i == 1)
						drawPicture(runkTexture[player[i].myRunk], 600, 800,
								750, 650, 0, null);
				}

				if (timeCount >= 2) {
					if (player[i].dethFlag) {
						for (int j = 0; j < 4; j++) {
							if (i == 0)
								drawPicture(numberBase, 4, 4,
										player[i].liveTime[j], 200 + 50 * j,
										250 + 50 * j, 650, 550, 0, null);
							else if (i == 1)
								drawPicture(numberBase, 4, 4,
										player[i].liveTime[j], 600 + 50 * j,
										650 + 50 * j, 650, 550, 0, null);
						}

					} else {
						if (timeCount >= 3) {
							if (i == 0){
								drawPicture(afterHP, 200, 400, 650, 550, 0, null);

								if (player[i].hp == 100) {
									drawPicture(numberBase, 4, 4, 1, 250, 300, 550,
											450, 0, null);
									drawPicture(numberBase, 4, 4, 0, 300, 350, 550,
											450, 0, null);
									drawPicture(numberBase, 4, 4, 0, 350, 400, 550,
											450, 0, null);
								} else if (player[i].hp >= 10 && player[i].hp < 100) {
									drawPicture(numberBase, 4, 4,
											player[i].hp / 10, 300, 350, 550, 450,
											0, null);
									drawPicture(numberBase, 4, 4,
											player[i].hp % 10, 350, 400, 550, 450,
											0, null);
								} else if (player[i].hp < 10 && player[i].hp > 0) {
									drawPicture(numberBase, 4, 4, player[i].hp,
											350, 400, 550, 450, 0, null);
								}

							}else if (i == 1){
								drawPicture(afterHP, 600, 800, 650, 550, 0, null);

								if (player[i].hp == 100) {
									drawPicture(numberBase, 4, 4, 1, 650, 700, 550,
											450, 0, null);
									drawPicture(numberBase, 4, 4, 0, 700, 750, 550,
											450, 0, null);
									drawPicture(numberBase, 4, 4, 0, 750, 800, 550,
											450, 0, null);
								} else if (player[i].hp >= 10 && player[i].hp < 100) {
									drawPicture(numberBase, 4, 4,
											player[i].hp / 10, 700, 750, 550, 450,
											0, null);
									drawPicture(numberBase, 4, 4,
											player[i].hp % 10, 750, 800, 550, 450,
											0, null);
								} else if (player[i].hp < 10 && player[i].hp > 0) {
									drawPicture(numberBase, 4, 4, player[i].hp,
											750, 800, 550, 450, 0, null);
								}
							}


						}
					}
				}
			}
		}
	}

	//キャラクターの描画
	private void drawChar(int cNumber, Charactor charactor){
		int textureId = 0;
		int d=0, dxNum=0, dyNum=0, a;

		textureId = player_im[cNumber][charactor.state];
		d = div[cNumber][charactor.state];

		if(d == 1)
			dxNum = 1;
		else if(d <= 4)
			dxNum = 2;
		else if(d <= 16)
			dxNum = 4;


		if(d <= 2)
			dyNum = 1;
		else if(d <= 8)
			dyNum = 2;
		else if(d <= 16)
			dyNum = 4;

		a = charactor.animeState;

		drawPicture(textureId, dxNum, dyNum, a,
				charactor.x1, charactor.x2, charactor.x3, charactor.x4,
				charactor.y1, charactor.y2, charactor.y3, charactor.y4,
				0, null);

	}

	// アナログスティックの描画
	public void drawCircle() {
		float left = 500 - w / 2;
		float bottom = 500 - h / 2 + 40;

		double moveNum = moveLength;

		if (!analogFlag) {
			moveNum = 0;
		}

		if (moveNum >= 30)
			moveNum = 30;

		drawPicture(circle[0], left, left + 100, bottom + 100, bottom, 0, null);

		drawPicture(circle[1], (float) (left + 50 + moveNum * analogCos - 25),
				(float) (left + 50 + moveNum * analogCos + 25), (float) (bottom
						+ 50 + moveNum * analogSin + 25), (float) (bottom + 50
						+ moveNum * analogSin - 25), 0, null);
	}

	// ボタンの描画
	public void drawButton() {
		float right = 500 + w / 2;
		float bottom = 500 - h / 2 + 40;

		drawPicture(button, 2, 2, 0, right - 80, right - 40, bottom + 120,
				bottom + 80, 0, null);

		drawPicture(button, 2, 2, 1, right - 40, right, bottom + 80,
				bottom + 40, 0, null);

		drawPicture(button, 2, 2, 2, right - 80, right - 40, bottom + 40,
				bottom, 0, null);

		drawPicture(button, 2, 2, 3, right - 120, right - 80, bottom + 80,
				bottom + 40, 0, null);
	}

	// プレイヤー名の描画
	private void drawName() {
		float left, right, top, bottom;
		float z = 0;

		int baseNum = 0;

		top = 500 + h / 2 + 30;

		if (init == RESULT) {
			top = 850;
			baseNum = 2;
		}

		left = 500 - nameLength[baseNum] - 20;
		right = left + 120;
		bottom = top - 30;

		if (init == RESULT) {
			left = 500 - nameLength[baseNum] - 20;
			right = left + 400;
			bottom = top - 100;
		}

		drawPicture(nameTexture[baseNum], left, right, top, bottom, z, null);

		left = 500 + 20;
		right = 500 + 140;

		if (init == RESULT) {
			left = 500 + 60;
			right = left + 400;
		}

		drawPicture(nameTexture[baseNum + 1], left, right, top, bottom, z, null);
	}

	// HPの描画
	public void drawHP() {
		float left = 500 - w / 2;
		float right = 500 + w / 2;
		float top = 500 + h / 2 + 40;

		colors[0] = (1 - player[0].hp / 100.0f);
		colors[1] = (player[0].hp / 350.0f);
		colors[2] = (player[0].hp / 100.0f);
		colors[3] = 1.0f;
		colors[4] = 1.0f;
		colors[5] = 0.0f;
		colors[6] = 0.0f;
		colors[7] = 1.0f;
		colors[8] = (1 - player[0].hp / 100.0f);
		colors[9] = (player[0].hp / 350.0f);
		colors[10] = (player[0].hp / 100.0f);
		colors[11] = 1.0f;
		colors[12] = 1.0f;
		colors[13] = 0.0f;
		colors[14] = 0.0f;
		colors[15] = 1.0f;

		drawPicture(hp_bar, left + 10, left + 30, top - 110 + player[0].hp,
				top - 110, 0, colors);

		colors[0] = (1 - player[1].hp / 100.0f);
		colors[1] = (player[1].hp / 350.0f);
		colors[2] = (player[1].hp / 100.0f);
		colors[3] = 1.0f;
		colors[4] = 1.0f;
		colors[5] = 0.0f;
		colors[6] = 0.0f;
		colors[7] = 1.0f;
		colors[8] = (1 - player[1].hp / 100.0f);
		colors[9] = (player[1].hp / 350.0f);
		colors[10] = (player[1].hp / 100.0f);
		colors[11] = 1.0f;
		colors[12] = 1.0f;
		colors[13] = 0.0f;
		colors[14] = 0.0f;
		colors[15] = 1.0f;

		drawPicture(hp_bar, right - 30, right - 10, top - 110 + player[1].hp,
				top - 110, 0, colors);
	}

	//覚醒ゲージの表示
	public void drawAwake(int cNumber, float x, float y){
		float left = x - 15;
		float right = x + 15;
		float top = y - 20;
		float bottom = y - 27;

		drawPicture(hp_bar, left, right, top, bottom, 0, null);

		if(player[cNumber].awake_num == 0)
			return;

		if (cNumber == 0) {
			colors[0] = 0.0f;
			colors[1] = 0.0f;
			colors[2] = 1.0f;
			colors[3] = 1.0f;
			colors[4] = 0.0f;
			colors[5] = 0.0f;
			colors[6] = 1.0f;
			colors[7] = 1.0f;
			colors[8] = 0.0f;
			colors[9] = 0.0f;
			colors[10] = 1.0f;
			colors[11] = 1.0f;
			colors[12] = 0.0f;
			colors[13] = 0.0f;
			colors[14] = 1.0f;
			colors[15] = 1.0f;

		} else if (cNumber == 1) {
			colors[0] = 1.0f;
			colors[1] = 0.0f;
			colors[2] = 0.0f;
			colors[3] = 1.0f;
			colors[4] = 1.0f;
			colors[5] = 0.0f;
			colors[6] = 0.0f;
			colors[7] = 1.0f;
			colors[8] = 1.0f;
			colors[9] = 0.0f;
			colors[10] = 0.0f;
			colors[11] = 1.0f;
			colors[12] = 1.0f;
			colors[13] = 0.0f;
			colors[14] = 0.0f;
			colors[15] = 1.0f;
		}

		right = x-15+(player[cNumber].awake_num*2);

		drawPicture(hp_bar, left, right, top, bottom, 0, colors);
	}


	// 画像の分割描画
	public void drawPicture(int textureId, float divX, float divY, int number,
			float left, float right, float top, float bottom, float z,
			float[] colors) {

		drawPicture(textureId, divX, divY, number,
				left, left, right, right,
				top, bottom, bottom, top,
				z, colors);
	}

	// 画像の分割描画
	public void drawPicture(int textureId, float divX, float divY, int number,
			float x1, float x2, float x3, float x4,
			float y1, float y2, float y3, float y4,
			float z, float[] colors) {

		float texLeft;
		float texRight;
		float texTop;
		float texBottom;

		if (number == ':') {
			texLeft = 0.5f;
			texTop = 0.5f;
		} else {
			texLeft = (number % divX * (1.0f / divX));
			texTop = ((int)(number / divX) * (1.0f / divY));
		}

		texRight = (texLeft + (1.0f / divX));
		texBottom = (texTop + (1.0f / divY));

		vertexs[0] = x1;
		vertexs[1] = y1;
		vertexs[2] = z;
		vertexs[3] = x2;
		vertexs[4] = y2;
		vertexs[5] = z;
		vertexs[6] = x4;
		vertexs[7] = y4;
		vertexs[8] = z;
		vertexs[9] = x3;
		vertexs[10] = y3;
		vertexs[11] = z;

		textures[0] = texLeft;
		textures[1] = texTop;
		textures[2] = texLeft;
		textures[3] = texBottom;
		textures[4] = texRight;
		textures[5] = texTop;
		textures[6] = texRight;
		textures[7] = texBottom;

		renderer.setTexture(gl, textureId, vertexs, colors, textures);
	}

	//エフェクトの描画
	public void drawEffect(int number, float x, float y, float angle){
		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glTranslatef(x, y, 0);
		gl.glRotatef((float)(angle*180/PI), 0.0f, 0.0f, 1.0f);

		if (number == 0) {
			colors[0] = 0.0f;
			colors[1] = 0.0f;
			colors[2] = 1.0f;
			colors[3] = 1.0f;
			colors[4] = 0.0f;
			colors[5] = 0.0f;
			colors[6] = 1.0f;
			colors[7] = 1.0f;
			colors[8] = 0.0f;
			colors[9] = 0.0f;
			colors[10] = 1.0f;
			colors[11] = 1.0f;
			colors[12] = 0.0f;
			colors[13] = 0.0f;
			colors[14] = 1.0f;
			colors[15] = 1.0f;

		} else if (number == 1) {
			colors[0] = 1.0f;
			colors[1] = 0.0f;
			colors[2] = 0.0f;
			colors[3] = 1.0f;
			colors[4] = 1.0f;
			colors[5] = 0.0f;
			colors[6] = 0.0f;
			colors[7] = 1.0f;
			colors[8] = 1.0f;
			colors[9] = 0.0f;
			colors[10] = 0.0f;
			colors[11] = 1.0f;
			colors[12] = 1.0f;
			colors[13] = 0.0f;
			colors[14] = 0.0f;
			colors[15] = 1.0f;
		}

		this.drawPicture(effectBase, 0, 120, 30, -30, 0, colors);

    	gl.glPopMatrix();
	}

	// 攻撃モーションの描画
	private void drawAttack() {
		float x1, x2, x3, x4, y1, y2, y3, y4;
		int textureId = 0;

		int d=0, dxNum=0, dyNum=0, a;

		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				if (attackMotion[i][j] != null) {
					if (attackMotion[i][j].allLiveFlag) {

						attackMotion[i][j].attackMove();

						switch (attackMotion[i][j].drawFigure) {

						case AttackMotion.RECT:
							for (int k = 0; k < attackMotion[i][j].motionNum; k++) {
								if (!attackMotion[i][j].liveFlag[k])
									continue;

								if (attackMotion[i][j].motionNumber == AttackMotion.SWORD1) {
									if(attackMotion[i][j].motionStyle == 1){
										textureId = swordSlash[i][0];
										d = 9;

									}else if(attackMotion[i][j].motionStyle == 2){
										textureId = swordSlash[i][2];
										d = 9;
									}

								}else if(attackMotion[i][j].motionNumber == AttackMotion.SWORD2) {
									if(attackMotion[i][j].motionStyle == 1){
										textureId = swordSlash[i][1];
										d = 4;

									}else if(attackMotion[i][j].motionStyle == 2){
										textureId = swordSlash[i][3];
										d = 1;
									}

								}else if(attackMotion[i][j].motionNumber == AttackMotion.MAID1) {
									if(attackMotion[i][j].motionStyle == 1){
										break;

									}else if(attackMotion[i][j].motionStyle == 2){
										textureId = sara[i];
									}

								}else if(attackMotion[i][j].motionNumber == AttackMotion.MAID2) {
									if(attackMotion[i][j].motionStyle == 1){
										break;

									}else if(attackMotion[i][j].motionStyle == 2){
										textureId = brush[i];
									}

								} else if(attackMotion[i][j].motionNumber == AttackMotion.MAGIC1){
									drawEffect(i, attackMotion[i][j].attackPosition[k][0],
											attackMotion[i][j].attackPosition[k][1],
											attackMotion[i][j].angle[k]);
									textureId = magicBase[i];

								} else if(attackMotion[i][j].motionNumber == AttackMotion.SHRINE1){
									if(attackMotion[i][j].motionStyle == 1){
										textureId = fuda[i];
									}else if(attackMotion[i][j].motionStyle == 2){
										textureId = sonic[i];
									}

								} else if(attackMotion[i][j].motionNumber == AttackMotion.SHRINE2){
									if(attackMotion[i][j].motionStyle == 1){
										textureId = ougi[i];
									}else if(attackMotion[i][j].motionStyle == 2){
										if(k == 0)
											textureId = cloud[i];
										else if(k == 1){
											if(attackMotion[i][j].animeState == 0)
												break;
											textureId = thander[i];
										}else if(k == 2){
											break;
										}
									}
								}

								if(selectChar[i] == 1 && k != 0)
									break;

								x1 = attackMotion[i][j].attackDetails[k][0][0];
								x2 = attackMotion[i][j].attackDetails[k][0][1];
								x3 = attackMotion[i][j].attackDetails[k][0][2];
								x4 = attackMotion[i][j].attackDetails[k][0][3];

								y1 = attackMotion[i][j].attackDetails[k][1][0];
								y2 = attackMotion[i][j].attackDetails[k][1][1];
								y3 = attackMotion[i][j].attackDetails[k][1][2];
								y4 = attackMotion[i][j].attackDetails[k][1][3];

								if(selectChar[i] == 1){

									if(d == 1)
										dxNum = 1;
									else if(d <= 4)
										dxNum = 2;
									else if(d <= 16)
										dxNum = 4;


									if(d <= 2)
										dyNum = 1;
									else if(d <= 8)
										dyNum = 2;
									else if(d <= 16)
										dyNum = 4;

									a = attackMotion[i][j].animeState;

									drawPicture(textureId, dxNum, dyNum, a,
											x1, x2, x3, x4,
											y1, y2, y3, y4,
											0, null);

								}else{
									drawPicture(textureId, x1, x2, x3, x4, y1, y2,
											y3, y4, 0, null);
								}

							}
							break;

						case AttackMotion.CIRCLE:
							// 円を描画
							for (int k = 0; k < attackMotion[i][j].motionNum; k++) {
								if (!attackMotion[i][j].liveFlag[k])
									continue;

								textureId = magicStar[i];

								x1 = attackMotion[i][j].attackPosition[k][0] - 10;
								y1 = attackMotion[i][j].attackPosition[k][1] + 10;
								x2 = x1 + 20;
								y2 = y1 - 20;

								drawPicture(textureId, x1, x2, y1, y2, 0,
										null);
							}
						}
					}

					if (i != myNumber)
						attackMotion[i][j].hitJudge(true, player[myNumber], ps[myNumber]);
					else if(i == myNumber){
						attackMotion[i][j].hitJudge(false,
								player[(myNumber+1)%2],
								ps[(myNumber+1)%2]);
					}

					attackMotion[i][j]
							.removeCheck(fieldCenter_x, fieldCenter_y);
				}
			}
		}
	}

	// 画像の描画
	public void drawPicture(int textureId, float left, float right, float top,
			float bottom, float z, float[] colors) {
		drawPicture(textureId, left, left, right, right, top, bottom, bottom,
				top, z, colors);
	}

	// 画像の描画
	public void drawPicture(int textureId, float x1, float x2, float x3,
			float x4, float y1, float y2, float y3, float y4, float z,
			float[] colors) {

		vertexs[0] = x1;
		vertexs[1] = y1;
		vertexs[2] = z;
		vertexs[3] = x2;
		vertexs[4] = y2;
		vertexs[5] = z;
		vertexs[6] = x4;
		vertexs[7] = y4;
		vertexs[8] = z;
		vertexs[9] = x3;
		vertexs[10] = y3;
		vertexs[11] = z;

		textures[0] = 0.0f;
		textures[1] = 0.0f;
		textures[2] = 0.0f;
		textures[3] = 1.0f;
		textures[4] = 1.0f;
		textures[5] = 0.0f;
		textures[6] = 1.0f;
		textures[7] = 1.0f;

		renderer.setTexture(gl, textureId, vertexs, colors, textures);
	}

	// 画像を読み込みテクスチャーを生成する
	public void loadTexture(GL10 gl) {
		background[0] = GLRenderer.loadTexture(gl,
				BitmapFactory.decodeResource(activity.getResources(),
						R.drawable.background1));
		background[1] = GLRenderer.loadTexture(gl,
				BitmapFactory.decodeResource(activity.getResources(),
						R.drawable.background2));
		background[2] = GLRenderer.loadTexture(gl, BitmapFactory
				.decodeResource(activity.getResources(), R.drawable.finish));
		background[3] = GLRenderer.loadTexture(gl, BitmapFactory
				.decodeResource(activity.getResources(), R.drawable.result));

		targetImage[0] = GLRenderer.loadTexture(gl, BitmapFactory
				.decodeResource(activity.getResources(), R.drawable.target1));
		targetImage[1] = GLRenderer.loadTexture(gl, BitmapFactory
				.decodeResource(activity.getResources(), R.drawable.target2));

		countDownImage[0] = GLRenderer.loadTexture(gl, BitmapFactory
				.decodeResource(activity.getResources(), R.drawable.count1));
		countDownImage[1] = GLRenderer.loadTexture(gl, BitmapFactory
				.decodeResource(activity.getResources(), R.drawable.count2));
		countDownImage[2] = GLRenderer.loadTexture(gl, BitmapFactory
				.decodeResource(activity.getResources(), R.drawable.count3));

		button = GLRenderer.loadTexture(gl, BitmapFactory.decodeResource(
				activity.getResources(), R.drawable.button));

		circle[0] = GLRenderer.loadTexture(gl, BitmapFactory.decodeResource(
				activity.getResources(), R.drawable.circle1));

		circle[1] = GLRenderer.loadTexture(gl, BitmapFactory.decodeResource(
				activity.getResources(), R.drawable.circle2));

		for (int i = 0; i < 2; i++) {
			switch (selectChar[i]) {
			case 1:
				player_im[i][0] = GLRenderer.loadTexture(gl, BitmapFactory
						.decodeResource(activity.getResources(),
								R.drawable.yue_s));
				player_im[i][1] = GLRenderer.loadTexture(gl, BitmapFactory
						.decodeResource(activity.getResources(),
								R.drawable.yue_mf));
				player_im[i][2] = GLRenderer.loadTexture(gl, BitmapFactory
						.decodeResource(activity.getResources(),
								R.drawable.yue_mb));
				player_im[i][3] = GLRenderer.loadTexture(gl, BitmapFactory
						.decodeResource(activity.getResources(),
								R.drawable.yue_md));
				player_im[i][4] = GLRenderer.loadTexture(gl, BitmapFactory
						.decodeResource(activity.getResources(),
								R.drawable.yue_dm));
				player_im[i][5] = GLRenderer.loadTexture(gl, BitmapFactory
						.decodeResource(activity.getResources(),
								R.drawable.yue_a11));
				player_im[i][6] = GLRenderer.loadTexture(gl, BitmapFactory
						.decodeResource(activity.getResources(),
								R.drawable.yue_a12));
				player_im[i][7] = GLRenderer.loadTexture(gl, BitmapFactory
						.decodeResource(activity.getResources(),
								R.drawable.yue_a21));
				player_im[i][8] = GLRenderer.loadTexture(gl, BitmapFactory
						.decodeResource(activity.getResources(),
								R.drawable.yue_a22));

				div[i][0] = 3;
				div[i][1] = 3;
				div[i][2] = 3;
				div[i][3] = 3;
				div[i][4] = 1;
				div[i][5] = 9;
				div[i][6] = 5;
				div[i][7] = 9;
				div[i][8] = 3;

				break;

			case 2:
				player_im[i][0] = GLRenderer.loadTexture(gl, BitmapFactory
						.decodeResource(activity.getResources(),
								R.drawable.miru_s));
				player_im[i][1] = GLRenderer.loadTexture(gl, BitmapFactory
						.decodeResource(activity.getResources(),
								R.drawable.miru_mf));
				player_im[i][2] = GLRenderer.loadTexture(gl, BitmapFactory
						.decodeResource(activity.getResources(),
								R.drawable.miru_mb));
				player_im[i][3] = GLRenderer.loadTexture(gl, BitmapFactory
						.decodeResource(activity.getResources(),
								R.drawable.miru_md));
				player_im[i][4] = GLRenderer.loadTexture(gl, BitmapFactory
						.decodeResource(activity.getResources(),
								R.drawable.miru_dm));
				player_im[i][5] = GLRenderer.loadTexture(gl, BitmapFactory
						.decodeResource(activity.getResources(),
								R.drawable.miru_a11));
				player_im[i][6] = GLRenderer.loadTexture(gl, BitmapFactory
						.decodeResource(activity.getResources(),
								R.drawable.miru_a12));
				player_im[i][7] = GLRenderer.loadTexture(gl, BitmapFactory
						.decodeResource(activity.getResources(),
								R.drawable.miru_a21));

				if(i == 0)
					player_im[i][8] = GLRenderer.loadTexture(gl, BitmapFactory
							.decodeResource(activity.getResources(),
									R.drawable.miru_a22_1));
				else
					player_im[i][8] = GLRenderer.loadTexture(gl, BitmapFactory
							.decodeResource(activity.getResources(),
									R.drawable.miru_a22_2));

				div[i][0] = 3;
				div[i][1] = 3;
				div[i][2] = 3;
				div[i][3] = 3;
				div[i][4] = 1;
				div[i][5] = 4;
				div[i][6] = 6;
				div[i][7] = 2;
				div[i][8] = 3;

				break;

			case 3:
				player_im[i][0] = GLRenderer.loadTexture(gl, BitmapFactory
						.decodeResource(activity.getResources(),
								R.drawable.noi_s));
				player_im[i][1] = GLRenderer.loadTexture(gl, BitmapFactory
						.decodeResource(activity.getResources(),
								R.drawable.noi_mf));
				player_im[i][2] = GLRenderer.loadTexture(gl, BitmapFactory
						.decodeResource(activity.getResources(),
								R.drawable.noi_mb));
				player_im[i][3] = GLRenderer.loadTexture(gl, BitmapFactory
						.decodeResource(activity.getResources(),
								R.drawable.noi_md));
				player_im[i][4] = GLRenderer.loadTexture(gl, BitmapFactory
						.decodeResource(activity.getResources(),
								R.drawable.noi_dm));
				player_im[i][5] = GLRenderer.loadTexture(gl, BitmapFactory
						.decodeResource(activity.getResources(),
								R.drawable.noi_a11));
				player_im[i][6] = GLRenderer.loadTexture(gl, BitmapFactory
						.decodeResource(activity.getResources(),
								R.drawable.noi_a12));
				player_im[i][7] = GLRenderer.loadTexture(gl, BitmapFactory
						.decodeResource(activity.getResources(),
								R.drawable.noi_a11));
				player_im[i][8] = GLRenderer.loadTexture(gl, BitmapFactory
						.decodeResource(activity.getResources(),
								R.drawable.noi_a12));

				div[i][0] = 3;
				div[i][1] = 3;
				div[i][2] = 3;
				div[i][3] = 2;
				div[i][4] = 1;
				div[i][5] = 2;
				div[i][6] = 3;
				div[i][7] = 2;
				div[i][8] = 3;

				break;

			case 4:
				player_im[i][0] = GLRenderer.loadTexture(gl, BitmapFactory
						.decodeResource(activity.getResources(),
								R.drawable.karin_s));
				player_im[i][1] = GLRenderer.loadTexture(gl, BitmapFactory
						.decodeResource(activity.getResources(),
								R.drawable.karin_mf));
				player_im[i][2] = GLRenderer.loadTexture(gl, BitmapFactory
						.decodeResource(activity.getResources(),
								R.drawable.karin_mb));
				player_im[i][3] = GLRenderer.loadTexture(gl, BitmapFactory
						.decodeResource(activity.getResources(),
								R.drawable.karin_md));
				player_im[i][4] = GLRenderer.loadTexture(gl, BitmapFactory
						.decodeResource(activity.getResources(),
								R.drawable.karin_dm));
				player_im[i][5] = GLRenderer.loadTexture(gl, BitmapFactory
						.decodeResource(activity.getResources(),
								R.drawable.karin_a11));
				player_im[i][6] = GLRenderer.loadTexture(gl, BitmapFactory
						.decodeResource(activity.getResources(),
								R.drawable.karin_a12));
				player_im[i][7] = GLRenderer.loadTexture(gl, BitmapFactory
						.decodeResource(activity.getResources(),
								R.drawable.karin_a21));
				player_im[i][8] = GLRenderer.loadTexture(gl, BitmapFactory
						.decodeResource(activity.getResources(),
								R.drawable.karin_a22));

				div[i][0] = 3;
				div[i][1] = 3;
				div[i][2] = 3;
				div[i][3] = 3;
				div[i][4] = 1;
				div[i][5] = 1;
				div[i][6] = 2;
				div[i][7] = 3;
				div[i][8] = 6;

				break;
			}
		}

		charWin_im[0] = GLRenderer.loadTexture(gl, BitmapFactory
				.decodeResource(activity.getResources(), R.drawable.char_win1));
		charWin_im[1] = GLRenderer.loadTexture(gl, BitmapFactory
				.decodeResource(activity.getResources(), R.drawable.char_win2));
		charWin_im[2] = GLRenderer.loadTexture(gl, BitmapFactory
				.decodeResource(activity.getResources(), R.drawable.char_win3));
		charWin_im[3] = GLRenderer.loadTexture(gl, BitmapFactory
				.decodeResource(activity.getResources(), R.drawable.char_win4));

		direction_im[0][0] = GLRenderer.loadTexture(gl, BitmapFactory
				.decodeResource(activity.getResources(), R.drawable.p11));
		direction_im[0][1] = GLRenderer.loadTexture(gl, BitmapFactory
				.decodeResource(activity.getResources(), R.drawable.p12));
		direction_im[1][0] = GLRenderer.loadTexture(gl, BitmapFactory
				.decodeResource(activity.getResources(), R.drawable.p21));
		direction_im[1][1] = GLRenderer.loadTexture(gl, BitmapFactory
				.decodeResource(activity.getResources(), R.drawable.p22));

		field_circle = GLRenderer.loadTexture(gl, BitmapFactory.decodeResource(
				activity.getResources(), R.drawable.field_circle));

		hp_bar = GLRenderer.loadTexture(gl, BitmapFactory.decodeResource(
				activity.getResources(), R.drawable.hp_bar));

		runkTexture[0] = GLRenderer.loadTexture(gl, BitmapFactory
				.decodeResource(activity.getResources(), R.drawable.runk1));

		runkTexture[1] = GLRenderer.loadTexture(gl, BitmapFactory
				.decodeResource(activity.getResources(), R.drawable.runk2));

		numberBase = GLRenderer.loadTexture(gl, BitmapFactory.decodeResource(
				activity.getResources(), R.drawable.number_base));

		afterHP = GLRenderer.loadTexture(gl, BitmapFactory.decodeResource(
				activity.getResources(), R.drawable.after_hp));

		effectBase = GLRenderer.loadTexture(gl, BitmapFactory.decodeResource(
				activity.getResources(), R.drawable.particle1));

		awake_efe[0][0] = GLRenderer.loadTexture(gl, BitmapFactory
				.decodeResource(activity.getResources(), R.drawable.awake_efe11));

		awake_efe[0][1] = GLRenderer.loadTexture(gl, BitmapFactory
				.decodeResource(activity.getResources(), R.drawable.awake_efe12));

		awake_efe[0][2] = GLRenderer.loadTexture(gl, BitmapFactory
				.decodeResource(activity.getResources(), R.drawable.awake_efe13));

		awake_efe[1][0] = GLRenderer.loadTexture(gl, BitmapFactory
				.decodeResource(activity.getResources(), R.drawable.awake_efe21));

		awake_efe[1][1] = GLRenderer.loadTexture(gl, BitmapFactory
				.decodeResource(activity.getResources(), R.drawable.awake_efe22));

		awake_efe[1][2] = GLRenderer.loadTexture(gl, BitmapFactory
				.decodeResource(activity.getResources(), R.drawable.awake_efe23));

		swordSlash[0][0] = GLRenderer.loadTexture(gl, BitmapFactory
				.decodeResource(activity.getResources(), R.drawable.slash11));

		swordSlash[0][1] = GLRenderer.loadTexture(gl, BitmapFactory
				.decodeResource(activity.getResources(), R.drawable.slash21));

		swordSlash[0][2] = GLRenderer.loadTexture(gl, BitmapFactory
				.decodeResource(activity.getResources(), R.drawable.slash31));

		swordSlash[0][3] = GLRenderer.loadTexture(gl, BitmapFactory
				.decodeResource(activity.getResources(), R.drawable.slash41));

		swordSlash[1][0] = GLRenderer.loadTexture(gl, BitmapFactory
				.decodeResource(activity.getResources(), R.drawable.slash12));

		swordSlash[1][1] = GLRenderer.loadTexture(gl, BitmapFactory
				.decodeResource(activity.getResources(), R.drawable.slash22));

		swordSlash[1][2] = GLRenderer.loadTexture(gl, BitmapFactory
				.decodeResource(activity.getResources(), R.drawable.slash32));

		swordSlash[1][3] = GLRenderer.loadTexture(gl, BitmapFactory
				.decodeResource(activity.getResources(), R.drawable.slash42));

		sara[0] = GLRenderer.loadTexture(gl, BitmapFactory
				.decodeResource(activity.getResources(), R.drawable.sara1));
		sara[1] = GLRenderer.loadTexture(gl, BitmapFactory
				.decodeResource(activity.getResources(), R.drawable.sara2));

		brush[0] = GLRenderer.loadTexture(gl, BitmapFactory
				.decodeResource(activity.getResources(), R.drawable.brush1));
		brush[1] = GLRenderer.loadTexture(gl, BitmapFactory
				.decodeResource(activity.getResources(), R.drawable.brush2));

		magicBase[0] = GLRenderer.loadTexture(gl, BitmapFactory
				.decodeResource(activity.getResources(), R.drawable.magic_base1));
		magicBase[1] = GLRenderer.loadTexture(gl, BitmapFactory
				.decodeResource(activity.getResources(), R.drawable.magic_base2));

		magicStar[0] = GLRenderer.loadTexture(gl, BitmapFactory
				.decodeResource(activity.getResources(), R.drawable.star1));
		magicStar[1] = GLRenderer.loadTexture(gl, BitmapFactory
				.decodeResource(activity.getResources(), R.drawable.star2));

		sonic[0] = GLRenderer.loadTexture(gl, BitmapFactory
				.decodeResource(activity.getResources(), R.drawable.sonic1));
		sonic[1] = GLRenderer.loadTexture(gl, BitmapFactory
				.decodeResource(activity.getResources(), R.drawable.sonic2));

		ougi[0] = GLRenderer.loadTexture(gl, BitmapFactory
				.decodeResource(activity.getResources(), R.drawable.ougi1));
		ougi[1] = GLRenderer.loadTexture(gl, BitmapFactory
				.decodeResource(activity.getResources(), R.drawable.ougi2));

		fuda[0] = GLRenderer.loadTexture(gl, BitmapFactory
				.decodeResource(activity.getResources(), R.drawable.fuda1));
		fuda[1] = GLRenderer.loadTexture(gl, BitmapFactory
				.decodeResource(activity.getResources(), R.drawable.fuda2));

		cloud[0] = GLRenderer.loadTexture(gl, BitmapFactory
				.decodeResource(activity.getResources(), R.drawable.cloud1));
		cloud[1] = GLRenderer.loadTexture(gl, BitmapFactory
				.decodeResource(activity.getResources(), R.drawable.cloud2));

		thander[0] = GLRenderer.loadTexture(gl, BitmapFactory
				.decodeResource(activity.getResources(), R.drawable.thander1));
		thander[1] = GLRenderer.loadTexture(gl, BitmapFactory
				.decodeResource(activity.getResources(), R.drawable.thander2));

		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setTypeface(Typeface.SANS_SERIF);
		paint.setTextSize(24);
		paint.setColor(Color.BLUE);

		nameLength[0] = (int) paint.measureText(userName[0]);
		nameLength[1] = (int) paint.measureText(userName[1]);

		Bitmap bmp1 = Bitmap.createBitmap(128, 32, Config.ARGB_8888);
		Bitmap bmp2 = Bitmap.createBitmap(128, 32, Config.ARGB_8888);
		Bitmap bmp3 = Bitmap.createBitmap(512, 128, Config.ARGB_8888);
		Bitmap bmp4 = Bitmap.createBitmap(512, 128, Config.ARGB_8888);

		Canvas canvas = new Canvas(bmp1);
		canvas.drawText(userName[0], 0, 20, paint);
		nameTexture[0] = GLRenderer.loadTexture(gl, bmp1);

		canvas = new Canvas(bmp2);
		paint.setColor(Color.RED);
		canvas.drawText(userName[1], 0, 20, paint);
		nameTexture[1] = GLRenderer.loadTexture(gl, bmp2);

		paint.setTextSize(96);
		paint.setColor(Color.BLUE);

		nameLength[2] = (int) paint.measureText(userName[0]);
		nameLength[3] = (int) paint.measureText(userName[1]);

		canvas = new Canvas(bmp3);
		canvas.drawText(userName[0], 0, 100, paint);
		nameTexture[2] = GLRenderer.loadTexture(gl, bmp3);

		canvas = new Canvas(bmp4);
		paint.setColor(Color.RED);
		canvas.drawText(userName[1], 0, 100, paint);
		nameTexture[3] = GLRenderer.loadTexture(gl, bmp4);

		canvas = null;
	}
}
