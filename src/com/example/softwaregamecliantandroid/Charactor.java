package com.example.softwaregamecliantandroid;

public class Charactor {

	public static final double PI = 3.14159265358979323846;

	//キャラクターの斜辺
	private static final float charSize = 28.2842f;
	private static final float charSize2 = 44.72135f;

	//キャラクターのHP
	public int hp;

	public int aimNum = 0;

	//プレイヤーナンバー
	public int playerNumber = 0;

	public int selectChar = 0;

	//キャラクターのシールド判定
	public boolean shieldFlag;

	//ダッシュしているかどうかの判定
	public boolean dashFlag;

	//スタイルチェンジしているかどうかの判定
	public boolean styleChange;

	//攻撃しているかどうかの判定
	public boolean attackFlag1;
	public boolean attackFlag2;

	//チャージフレーム数
	public int chargeFrame = 0;

	//チャージフラグ
	public boolean chargeFlag;

	//キャラクターの中心座標
	public int x;
	public int y;

	//キャラクターの移動後座標
	public int nx;
	public int ny;

	//キャラクターの四方の座礁
	public float[][] charRect = new float[2][2];

	//キャラクターとフィールドの中心との距離
	public double distanceFromCenter;

	//キャラクターとフィールドの中心とがなす角の三角関数
	public double sin;
	public double cos;

	//キャラクター描画先の指定
	public float x1, x2, x3, x4;
	public float y1, y2, y3, y4;

	//キャラクターがどの方向を向いているか判断
	public int dir;

	//キャラクターがどの常態かを判断
	public int state;

	//アニメーションの状態
	public int animeState;
	public int aFrameCount;

	//攻撃モーションフラグ
	public boolean aMotionFlag;

	public double angle;

	public double theta = 45.0  / 180 * PI;
	public static final double theta2 = 26.565051  / 180 * PI;

	//ノックバックフラグ
	public boolean nockBackFlag;
	public boolean sNockBackFlag;

	//ダメージ後の経過フレーム
	public int countFrame = 0;

	public String[] attackData;

	public String[] attackData2;

	public String recvData;

	public byte[] recvByte;

	public boolean btsFlag;

	public int attackCount = 0;

	public int doCount = 0;

	public boolean lockFlag;
	public int lockCount = 0;

	public boolean dethFlag;

	public int myRunk;

	public long endTime;
	public long pastTime;

	public int[] liveTime = new int[4];

	//メイド回転
	public boolean tDashFlag;

	//剣士用の覚醒ゲージ
	public int awake_num = 0;
	public boolean awakeFlag;
	public int awakeAnimeCount = 0;

	//コンストラクタ
	public Charactor(int playerNumber, int selectChar, int x0, int y0){
		hp = 100;
		x = x0;
		y = y0;
		nx = x;
		ny = y;

		dir = 0;
		dashFlag = false;
		shieldFlag = false;
		animeState = 0;
		aFrameCount = 0;
		attackData = new String[3];
		attackData2 = new String[3];
		this.playerNumber = playerNumber;
		this.selectChar = selectChar;

		recvData = "noDataA  noDataA  noDataA  E";

		for(int i=0; i<3; i++){
			attackData[i] = "noDataA  ";
		}
	}

	//位置調整メソッド
	public void firstSet(){
		x1 = x - 20;
		x2 = x1;
		x3 = x + 20;
		x4 = x3;

		y1 = y + 20;
		y2 = y - 20;
		y3 = y2;
		y4 = y1;

		charRect[0][0] = x - 15;
		charRect[0][1] = x + 15;
		charRect[1][0] = y + 15;
		charRect[1][1] = y - 15;
	}

	//移動メソッド
	public void move(){
		double sin, cos, angle, r;

		if(nx-x == 0 && ny-y == 0)
			return;

		r = Math.sqrt(Math.pow(nx-x, 2) + Math.pow(ny-y, 2));
		sin = (ny-y)/r;
		cos = (nx-x)/r;

		if(r <= 2)
			return;

		x += r/2 * cos;
		y += r/2 * sin;

		angle = (float)Math.atan2(sin, cos);

		if(dashFlag){
			state = 3;

			if(angle >= PI/2 || angle <= -PI/2){
				x2 = (float)(x + (double)(charSize * Math.cos(angle+(PI-theta))));
				x1 = (float)(x + (double)(charSize * Math.cos(angle-(PI-theta))));
				x4 = (float)(x + (double)(charSize * Math.cos(angle-theta)));
				x3 = (float)(x + (double)(charSize * Math.cos(angle+theta)));

				y2 = (float)(y + (double)(charSize * Math.sin(angle+(PI-theta))));
				y1 = (float)(y + (double)(charSize * Math.sin(angle-(PI-theta))));
				y4 = (float)(y + (double)(charSize * Math.sin(angle-theta)));
				y3 = (float)(y + (double)(charSize * Math.sin(angle+theta)));

			}else if(angle < PI/2 && angle > -PI/2){
				x1 = (float)(x + (double)(charSize * Math.cos(angle+(PI-theta))));
				x2 = (float)(x + (double)(charSize * Math.cos(angle-(PI-theta))));
				x3 = (float)(x + (double)(charSize * Math.cos(angle-theta)));
				x4 = (float)(x + (double)(charSize * Math.cos(angle+theta)));

				y1 = (float)(y + (double)(charSize * Math.sin(angle+(PI-theta))));
				y2 = (float)(y + (double)(charSize * Math.sin(angle-(PI-theta))));
				y3 = (float)(y + (double)(charSize * Math.sin(angle-theta)));
				y4 = (float)(y + (double)(charSize * Math.sin(angle+theta)));
			}

		}else {
			if(!aMotionFlag && !nockBackFlag){
				if(angle < 0)
					angle += PI*2;

				if(this.angle < 0)
					this.angle += PI*2;

				if(Math.abs(nx-x) <= 1 && Math.abs(ny-y) <=1)
					state = 0;
				else{
					if(Math.abs(this.angle-angle) <= PI/4){
						state = 2;

					}else{
						if(Math.abs(this.angle-angle) >= PI*3/4 && Math.abs(this.angle-angle) <= PI*5/4){
								state = 1;
						}else
							state = 0;
					}
				}
			}
		}
	}

	//キャラの回転角調整
	public void reRotate(Charactor charactor){
		double sin, cos, r;
		int x, y;

		x = this.x;
		y = this.y;

		r = Math.sqrt((this.x-charactor.x)*(this.x-charactor.x) + (this.y-charactor.y)*(this.y-charactor.y));
		sin = (this.y-charactor.y)/r;
		cos = (this.x-charactor.x)/r;

		angle = (float)Math.atan2(sin, cos);

		if(angle < PI / 2 && angle > -PI / 2){
			dir = 1;
		}else {
			dir = 2;
		}

		if(!dashFlag){
			if(selectChar == 1 && state >= 5){
				x -= 20*Math.cos(angle);
				y -= 20*Math.sin(angle);

				if(angle >= PI/2 || angle <= -PI/2){
					x2 = (float)(x + (double)(charSize2 * Math.cos(angle+(PI-theta2))));
					x1 = (float)(x + (double)(charSize2 * Math.cos(angle-(PI-theta2))));
					x4 = (float)(x + (double)(charSize2 * Math.cos(angle-theta2)));
					x3 = (float)(x + (double)(charSize2 * Math.cos(angle+theta2)));

					y2 = (float)(y + (double)(charSize2 * Math.sin(angle+(PI-theta2))));
					y1 = (float)(y + (double)(charSize2 * Math.sin(angle-(PI-theta2))));
					y4 = (float)(y + (double)(charSize2 * Math.sin(angle-theta2)));
					y3 = (float)(y + (double)(charSize2 * Math.sin(angle+theta2)));

				}else if(angle < PI/2 && angle > -PI/2){
					x1 = (float)(x + (double)(charSize2 * Math.cos(angle+(PI-theta2))));
					x2 = (float)(x + (double)(charSize2 * Math.cos(angle-(PI-theta2))));
					x3 = (float)(x + (double)(charSize2 * Math.cos(angle-theta2)));
					x4 = (float)(x + (double)(charSize2 * Math.cos(angle+theta2)));

					y1 = (float)(y + (double)(charSize2 * Math.sin(angle+(PI-theta2))));
					y2 = (float)(y + (double)(charSize2 * Math.sin(angle-(PI-theta2))));
					y3 = (float)(y + (double)(charSize2 * Math.sin(angle-theta2)));
					y4 = (float)(y + (double)(charSize2 * Math.sin(angle+theta2)));
				}

			}else if(selectChar == 2 && (state == 5 || state == 6 || state == 8)){
				if(state == 5){
					x -= 20*Math.cos(angle);
					y -= 20*Math.sin(angle);
				}

				if(angle >= PI/2 || angle <= -PI/2){
					x2 = (float)(x + (double)(charSize * 2 * Math.cos(angle+(PI-theta))));
					x1 = (float)(x + (double)(charSize * 2 * Math.cos(angle-(PI-theta))));
					x4 = (float)(x + (double)(charSize * 2 * Math.cos(angle-theta)));
					x3 = (float)(x + (double)(charSize * 2 * Math.cos(angle+theta)));

					y2 = (float)(y + (double)(charSize * 2 * Math.sin(angle+(PI-theta))));
					y1 = (float)(y + (double)(charSize * 2 * Math.sin(angle-(PI-theta))));
					y4 = (float)(y + (double)(charSize * 2 * Math.sin(angle-theta)));
					y3 = (float)(y + (double)(charSize * 2 * Math.sin(angle+theta)));

				}else if(angle < PI/2 && angle > -PI/2){
					x1 = (float)(x + (double)(charSize * 2 * Math.cos(angle+(PI-theta))));
					x2 = (float)(x + (double)(charSize * 2 * Math.cos(angle-(PI-theta))));
					x3 = (float)(x + (double)(charSize * 2 * Math.cos(angle-theta)));
					x4 = (float)(x + (double)(charSize * 2 * Math.cos(angle+theta)));

					y1 = (float)(y + (double)(charSize * 2 * Math.sin(angle+(PI-theta))));
					y2 = (float)(y + (double)(charSize * 2 * Math.sin(angle-(PI-theta))));
					y3 = (float)(y + (double)(charSize * 2 * Math.sin(angle-theta)));
					y4 = (float)(y + (double)(charSize * 2 * Math.sin(angle+theta)));
				}

			}else {
				if(angle >= PI/2 || angle <= -PI/2){
					x2 = (float)(x + (double)(charSize * Math.cos(angle+(PI-theta))));
					x1 = (float)(x + (double)(charSize * Math.cos(angle-(PI-theta))));
					x4 = (float)(x + (double)(charSize * Math.cos(angle-theta)));
					x3 = (float)(x + (double)(charSize * Math.cos(angle+theta)));

					y2 = (float)(y + (double)(charSize * Math.sin(angle+(PI-theta))));
					y1 = (float)(y + (double)(charSize * Math.sin(angle-(PI-theta))));
					y4 = (float)(y + (double)(charSize * Math.sin(angle-theta)));
					y3 = (float)(y + (double)(charSize * Math.sin(angle+theta)));

				}else if(angle < PI/2 && angle > -PI/2){
					x1 = (float)(x + (double)(charSize * Math.cos(angle+(PI-theta))));
					x2 = (float)(x + (double)(charSize * Math.cos(angle-(PI-theta))));
					x3 = (float)(x + (double)(charSize * Math.cos(angle-theta)));
					x4 = (float)(x + (double)(charSize * Math.cos(angle+theta)));

					y1 = (float)(y + (double)(charSize * Math.sin(angle+(PI-theta))));
					y2 = (float)(y + (double)(charSize * Math.sin(angle-(PI-theta))));
					y3 = (float)(y + (double)(charSize * Math.sin(angle-theta)));
					y4 = (float)(y + (double)(charSize * Math.sin(angle+theta)));
				}
			}
		}

		charRect[0][0] = x - 15;
		charRect[0][1] = x + 15;
		charRect[1][0] = y + 15;
		charRect[1][1] = y - 15;
	}

	//フィールドの中心とキャラクターの中心との距離を再計算するメソッド
	public void reCalc(int fieldCenter_x, int fieldCenter_y){
		int dx, dy;

		dx = x - fieldCenter_x;
		dy = y - fieldCenter_y;

		distanceFromCenter = Math.sqrt(dx * dx + dy * dy);

		sin = dy / distanceFromCenter;
		cos = dx / distanceFromCenter;
	}
}
