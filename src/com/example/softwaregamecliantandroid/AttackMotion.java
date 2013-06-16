package com.example.softwaregamecliantandroid;

import java.util.Random;

public class AttackMotion {

	// 円周率
	public static final double PI = 3.14159265358979323846;

	// 重力加速度
	public static final float G = -0.04f;

	// 重力のx,y成分
	float gx, gy;

	// 初速度
	float v0, v0x, v0y, vx, vy;

	// 図形種別
	public static final int RECT = 1;
	public static final int CIRCLE = 2;

	// 描画する図形
	int drawFigure;

	// 攻撃種別
	public static final int SWORD1 = 1;
	public static final int SWORD2 = 2;
	public static final int MAID1 = 3;
	public static final int MAID2 = 4;
	public static final int MAGIC1 = 5;
	public static final int MAGIC2 = 6;
	public static final int SHRINE1 = 7;
	public static final int SHRINE2 = 8;

	// モーションの攻撃種別
	int motionNumber;

	// スタイル種別
	int motionStyle;

	boolean chargeFlag;

	int combo = 0;

	Charactor char1, char2;

	double x1, y1, x2, y2;

	float[][][] attackDetails;

	float[][] attackPosition;

	double motionCenter_x;
	double motionCenter_y;

	double theta, theta2;
	float[] angle;
	double rad;

	float attackLength;
	int motionNum;
	int moveNum;
	int Radius;
	int divisionNum;
	int frameCount = 0;
	int moveCount = 0;

	// アニメーション
	int animeState = 0;
	int aFrameCount = 0;

	// AttackMotionオブジェクトがすべて生きているかどうか
	boolean allLiveFlag;

	// AttackMotionのパーツ一つ一つが生きているかどうか
	boolean[] liveFlag;

	double[] sin;
	double[] cos;
	double r;

	// 乱数生成オブジェクト
	Random rnd = new Random();

	// コンストラクタ

	public AttackMotion() {
	}

	public AttackMotion(int charNumber, Charactor char1, Charactor char2) {
		x1 = char1.x;
		y1 = char1.y;

		x2 = char2.x;
		y2 = char2.y;

		this.char1 = char1;
		this.char2 = char2;

		allLiveFlag = true;

		char1.aMotionFlag = true;
		char1.lockFlag = true;
		char1.lockCount = 0;
		char1.aFrameCount = 0;
		char1.animeState = 0;
		char1.dashFlag = false;

		if (!char1.styleChange) {
			if (char1.attackFlag1)
				char1.state = 5;
			else if (char1.attackFlag2)
				char1.state = 6;
		} else {
			if (char1.attackFlag1)
				char1.state = 7;
			else if (char1.attackFlag2)
				char1.state = 8;
		}

		switch (charNumber) {

		case 1:
			char1.lockFlag = false;
			if (char1.attackFlag1)
				swordMotion1(char1.styleChange);
			else if (char1.attackFlag2)
				swordMotion2(char1.styleChange);

			break;

		case 2:
			if (char1.attackFlag1)
				maidMotion1(char1.styleChange);
			else if (char1.attackFlag2)
				maidMotion2(char1.styleChange);

			break;

		case 3:
			if (char1.attackFlag1)
				magicianMotion1(char1.styleChange);
			else if (char1.attackFlag2)
				magicianMotion2(char1.styleChange);

			break;

		case 4:
			if (char1.attackFlag1)
				shrineMotion1(char1.styleChange);
			else if (char1.attackFlag2)
				shrineMotion2(char1.styleChange);

			break;
		}
	}

	// 剣士の弱攻撃
	public void swordMotion1(boolean style) {
		if (!style) {
			drawFigure = RECT;
			motionNumber = SWORD1;
			motionStyle = 1;

			motionNum = 10;
			moveNum = 0;

			animeState = 0;
			aFrameCount = 0;

			attackPosition = new float[motionNum][2];
			liveFlag = new boolean[motionNum];
			angle = new float[motionNum];
			sin = new double[motionNum];
			cos = new double[motionNum];
			attackDetails = new float[motionNum][2][4];

			liveFlag[0] = true;
			attackLength = 50;

			r = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
			sin[0] = (y1 - y2) / r;
			cos[0] = (x1 - x2) / r;
			angle[0] = (float) Math.atan2(sin[0], cos[0]);

			theta = 45.0 / 180.0 * PI;

			attackPosition[0][0] = (float) (x1 - 30 * cos[0]);
			attackPosition[0][1] = (float) (y1 - 30 * sin[0]);

			attackDetails[0][0][0] = (float) (attackPosition[0][0] + (double) (attackLength * Math
					.cos(angle[0] + (PI - theta))));
			attackDetails[0][0][1] = (float) (attackPosition[0][0] + (double) (attackLength * Math
					.cos(angle[0] - (PI - theta))));
			attackDetails[0][0][2] = (float) (attackPosition[0][0] + (double) (attackLength * Math
					.cos(angle[0] - theta)));
			attackDetails[0][0][3] = (float) (attackPosition[0][0] + (double) (attackLength * Math
					.cos(angle[0] + theta)));

			attackDetails[0][1][0] = (float) (attackPosition[0][1] + (double) (attackLength * Math
					.sin(angle[0] + (PI - theta))));
			attackDetails[0][1][1] = (float) (attackPosition[0][1] + (double) (attackLength * Math
					.sin(angle[0] - (PI - theta))));
			attackDetails[0][1][2] = (float) (attackPosition[0][1] + (double) (attackLength * Math
					.sin(angle[0] - theta)));
			attackDetails[0][1][3] = (float) (attackPosition[0][1] + (double) (attackLength * Math
					.sin(angle[0] + theta)));

			for (int i = 1; i < motionNum; i++) {
				liveFlag[i] = true;
				theta2 = i * 10 / 180.0 * PI;

				attackPosition[i][0] = attackPosition[0][0];
				attackPosition[i][1] = attackPosition[0][1];

				attackDetails[i][0][0] = (float) (attackPosition[0][0] + (double) ((attackLength - 15) * Math
						.cos(angle[0] + theta2 + (PI - theta))));
				attackDetails[i][0][1] = (float) (attackPosition[0][0] + (double) ((attackLength - 15) * Math
						.cos(angle[0] + theta2 - (PI - theta))));
				attackDetails[i][0][2] = (float) (attackPosition[0][0] + (double) ((attackLength - 15) * Math
						.cos(angle[0] + theta2 - theta)));
				attackDetails[i][0][3] = (float) (attackPosition[0][0] + (double) ((attackLength - 15) * Math
						.cos(angle[0] + theta2 + theta)));

				attackDetails[i][1][0] = (float) (attackPosition[0][1] + (double) ((attackLength - 15) * Math
						.sin(angle[0] + theta2 + (PI - theta))));
				attackDetails[i][1][1] = (float) (attackPosition[0][1] + (double) ((attackLength - 15) * Math
						.sin(angle[0] + theta2 - (PI - theta))));
				attackDetails[i][1][2] = (float) (attackPosition[0][1] + (double) ((attackLength - 15) * Math
						.sin(angle[0] + theta2 - theta)));
				attackDetails[i][1][3] = (float) (attackPosition[0][1] + (double) ((attackLength - 15) * Math
						.sin(angle[0] + theta2 + theta)));

			}

		} else {
			drawFigure = RECT;
			motionNumber = SWORD1;
			motionStyle = 2;

			motionNum = 10;
			moveNum = 0;

			animeState = 0;
			aFrameCount = 0;

			attackPosition = new float[motionNum][2];
			liveFlag = new boolean[motionNum];
			angle = new float[motionNum];
			sin = new double[motionNum];
			cos = new double[motionNum];
			attackDetails = new float[motionNum][2][4];

			liveFlag[0] = true;
			attackLength = 50;

			r = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
			sin[0] = (y1 - y2) / r;
			cos[0] = (x1 - x2) / r;
			angle[0] = (float) Math.atan2(sin[0], cos[0]);

			theta = 45.0 / 180.0 * PI;

			attackPosition[0][0] = (float) (x1 - 30 * cos[0]);
			attackPosition[0][1] = (float) (y1 - 30 * sin[0]);

			attackDetails[0][0][0] = (float) (attackPosition[0][0] + (double) (attackLength * Math
					.cos(angle[0] + (PI - theta))));
			attackDetails[0][0][1] = (float) (attackPosition[0][0] + (double) (attackLength * Math
					.cos(angle[0] - (PI - theta))));
			attackDetails[0][0][2] = (float) (attackPosition[0][0] + (double) (attackLength * Math
					.cos(angle[0] - theta)));
			attackDetails[0][0][3] = (float) (attackPosition[0][0] + (double) (attackLength * Math
					.cos(angle[0] + theta)));

			attackDetails[0][1][0] = (float) (attackPosition[0][1] + (double) (attackLength * Math
					.sin(angle[0] + (PI - theta))));
			attackDetails[0][1][1] = (float) (attackPosition[0][1] + (double) (attackLength * Math
					.sin(angle[0] - (PI - theta))));
			attackDetails[0][1][2] = (float) (attackPosition[0][1] + (double) (attackLength * Math
					.sin(angle[0] - theta)));
			attackDetails[0][1][3] = (float) (attackPosition[0][1] + (double) (attackLength * Math
					.sin(angle[0] + theta)));

			for (int i = 1; i < motionNum; i++) {
				liveFlag[i] = true;
				theta2 = i * 10 / 180.0 * PI;

				attackPosition[i][0] = attackPosition[0][0];
				attackPosition[i][1] = attackPosition[0][1];

				attackDetails[i][0][0] = (float) (attackPosition[0][0] + (double) ((attackLength - 15) * Math
						.cos(angle[0] + theta2 + (PI - theta))));
				attackDetails[i][0][1] = (float) (attackPosition[0][0] + (double) ((attackLength - 15) * Math
						.cos(angle[0] + theta2 - (PI - theta))));
				attackDetails[i][0][2] = (float) (attackPosition[0][0] + (double) ((attackLength - 15) * Math
						.cos(angle[0] + theta2 - theta)));
				attackDetails[i][0][3] = (float) (attackPosition[0][0] + (double) ((attackLength - 15) * Math
						.cos(angle[0] + theta2 + theta)));

				attackDetails[i][1][0] = (float) (attackPosition[0][1] + (double) ((attackLength - 15) * Math
						.sin(angle[0] + theta2 + (PI - theta))));
				attackDetails[i][1][1] = (float) (attackPosition[0][1] + (double) ((attackLength - 15) * Math
						.sin(angle[0] + theta2 - (PI - theta))));
				attackDetails[i][1][2] = (float) (attackPosition[0][1] + (double) ((attackLength - 15) * Math
						.sin(angle[0] + theta2 - theta)));
				attackDetails[i][1][3] = (float) (attackPosition[0][1] + (double) ((attackLength - 15) * Math
						.sin(angle[0] + theta2 + theta)));

			}

		}
	}

	// 剣士の強攻撃
	public void swordMotion2(boolean style) {
		if (!style) {
			drawFigure = RECT;
			motionNumber = SWORD2;
			motionStyle = 1;

			motionNum = 1;
			moveNum = 10;

			animeState = 0;
			aFrameCount = 0;

			attackPosition = new float[motionNum][2];
			liveFlag = new boolean[motionNum];
			angle = new float[motionNum];
			sin = new double[motionNum];
			cos = new double[motionNum];
			attackDetails = new float[motionNum][2][4];

			liveFlag[0] = true;
			attackLength = 40;

			r = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
			sin[0] = (y1 - y2) / r;
			cos[0] = (x1 - x2) / r;
			angle[0] = (float) Math.atan2(sin[0], cos[0]);

			theta = 45.0 / 180.0 * PI;

			attackPosition[0][0] = (float) (x1 - 20 * cos[0]);
			attackPosition[0][1] = (float) (y1 - 20 * sin[0]);

			attackDetails[0][0][0] = (float) (attackPosition[0][0] + (double) (attackLength * Math
					.cos(angle[0] + (PI - theta))));
			attackDetails[0][0][1] = (float) (attackPosition[0][0] + (double) (attackLength * Math
					.cos(angle[0] - (PI - theta))));
			attackDetails[0][0][2] = (float) (attackPosition[0][0] + (double) (attackLength * Math
					.cos(angle[0] - theta)));
			attackDetails[0][0][3] = (float) (attackPosition[0][0] + (double) (attackLength * Math
					.cos(angle[0] + theta)));

			attackDetails[0][1][0] = (float) (attackPosition[0][1] + (double) (attackLength * Math
					.sin(angle[0] + (PI - theta))));
			attackDetails[0][1][1] = (float) (attackPosition[0][1] + (double) (attackLength * Math
					.sin(angle[0] - (PI - theta))));
			attackDetails[0][1][2] = (float) (attackPosition[0][1] + (double) (attackLength * Math
					.sin(angle[0] - theta)));
			attackDetails[0][1][3] = (float) (attackPosition[0][1] + (double) (attackLength * Math
					.sin(angle[0] + theta)));

		} else {
			drawFigure = RECT;
			motionNumber = SWORD2;
			motionStyle = 2;

			motionNum = 10;
			moveNum = 0;

			animeState = 0;
			aFrameCount = 0;

			attackPosition = new float[motionNum][2];
			liveFlag = new boolean[motionNum];
			angle = new float[motionNum];
			sin = new double[motionNum];
			cos = new double[motionNum];
			attackDetails = new float[motionNum][2][4];

			liveFlag[0] = true;
			attackLength = 50;

			r = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
			sin[0] = (y1 - y2) / r;
			cos[0] = (x1 - x2) / r;
			angle[0] = (float) Math.atan2(sin[0], cos[0]);

			theta = 45.0 / 180.0 * PI;

			attackPosition[0][0] = (float) (x1 - 30 * cos[0]);
			attackPosition[0][1] = (float) (y1 - 30 * sin[0]);

			attackDetails[0][0][0] = (float) (attackPosition[0][0] + (double) (attackLength * Math
					.cos(angle[0] + (PI - theta))));
			attackDetails[0][0][1] = (float) (attackPosition[0][0] + (double) (attackLength * Math
					.cos(angle[0] - (PI - theta))));
			attackDetails[0][0][2] = (float) (attackPosition[0][0] + (double) (attackLength * Math
					.cos(angle[0] - theta)));
			attackDetails[0][0][3] = (float) (attackPosition[0][0] + (double) (attackLength * Math
					.cos(angle[0] + theta)));

			attackDetails[0][1][0] = (float) (attackPosition[0][1] + (double) (attackLength * Math
					.sin(angle[0] + (PI - theta))));
			attackDetails[0][1][1] = (float) (attackPosition[0][1] + (double) (attackLength * Math
					.sin(angle[0] - (PI - theta))));
			attackDetails[0][1][2] = (float) (attackPosition[0][1] + (double) (attackLength * Math
					.sin(angle[0] - theta)));
			attackDetails[0][1][3] = (float) (attackPosition[0][1] + (double) (attackLength * Math
					.sin(angle[0] + theta)));

			for (int i = 1; i < motionNum; i++) {
				liveFlag[i] = true;
				theta2 = i * 10 / 180.0 * PI;

				attackPosition[i][0] = attackPosition[0][0];
				attackPosition[i][1] = attackPosition[0][1];

				attackDetails[i][0][0] = (float) (attackPosition[0][0] + (double) ((attackLength - 15) * Math
						.cos(angle[0] + theta2 + (PI - theta))));
				attackDetails[i][0][1] = (float) (attackPosition[0][0] + (double) ((attackLength - 15) * Math
						.cos(angle[0] + theta2 - (PI - theta))));
				attackDetails[i][0][2] = (float) (attackPosition[0][0] + (double) ((attackLength - 15) * Math
						.cos(angle[0] + theta2 - theta)));
				attackDetails[i][0][3] = (float) (attackPosition[0][0] + (double) ((attackLength - 15) * Math
						.cos(angle[0] + theta2 + theta)));

				attackDetails[i][1][0] = (float) (attackPosition[0][1] + (double) ((attackLength - 15) * Math
						.sin(angle[0] + theta2 + (PI - theta))));
				attackDetails[i][1][1] = (float) (attackPosition[0][1] + (double) ((attackLength - 15) * Math
						.sin(angle[0] + theta2 - (PI - theta))));
				attackDetails[i][1][2] = (float) (attackPosition[0][1] + (double) ((attackLength - 15) * Math
						.sin(angle[0] + theta2 - theta)));
				attackDetails[i][1][3] = (float) (attackPosition[0][1] + (double) ((attackLength - 15) * Math
						.sin(angle[0] + theta2 + theta)));

			}

		}
	}

	// メイドの弱攻撃
	public void maidMotion1(boolean style) {
		if (!style) {
			drawFigure = RECT;
			motionNumber = MAID1;
			motionStyle = 1;

			motionNum = 10;
			moveNum = 0;

			attackPosition = new float[motionNum][2];
			liveFlag = new boolean[motionNum];
			angle = new float[motionNum];
			sin = new double[motionNum];
			cos = new double[motionNum];
			attackDetails = new float[motionNum][2][4];

			liveFlag[0] = true;
			attackLength = 50;

			r = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
			sin[0] = (y1 - y2) / r;
			cos[0] = (x1 - x2) / r;
			angle[0] = (float) Math.atan2(sin[0], cos[0]);

			theta = 45.0 / 180.0 * PI;

			attackPosition[0][0] = (float) (x1 - 30 * cos[0]);
			attackPosition[0][1] = (float) (y1 - 30 * sin[0]);

			attackDetails[0][0][0] = (float) (attackPosition[0][0] + (double) (attackLength * Math
					.cos(angle[0] + (PI - theta))));
			attackDetails[0][0][1] = (float) (attackPosition[0][0] + (double) (attackLength * Math
					.cos(angle[0] - (PI - theta))));
			attackDetails[0][0][2] = (float) (attackPosition[0][0] + (double) (attackLength * Math
					.cos(angle[0] - theta)));
			attackDetails[0][0][3] = (float) (attackPosition[0][0] + (double) (attackLength * Math
					.cos(angle[0] + theta)));

			attackDetails[0][1][0] = (float) (attackPosition[0][1] + (double) (attackLength * Math
					.sin(angle[0] + (PI - theta))));
			attackDetails[0][1][1] = (float) (attackPosition[0][1] + (double) (attackLength * Math
					.sin(angle[0] - (PI - theta))));
			attackDetails[0][1][2] = (float) (attackPosition[0][1] + (double) (attackLength * Math
					.sin(angle[0] - theta)));
			attackDetails[0][1][3] = (float) (attackPosition[0][1] + (double) (attackLength * Math
					.sin(angle[0] + theta)));

			for (int i = 1; i < motionNum; i++) {
				liveFlag[i] = true;
				theta2 = i * 10 / 180.0 * PI;

				attackPosition[i][0] = attackPosition[0][0];
				attackPosition[i][1] = attackPosition[0][1];

				attackDetails[i][0][0] = (float) (attackPosition[0][0] + (double) ((attackLength - 15) * Math
						.cos(angle[0] + theta2 + (PI - theta))));
				attackDetails[i][0][1] = (float) (attackPosition[0][0] + (double) ((attackLength - 15) * Math
						.cos(angle[0] + theta2 - (PI - theta))));
				attackDetails[i][0][2] = (float) (attackPosition[0][0] + (double) ((attackLength - 15) * Math
						.cos(angle[0] + theta2 - theta)));
				attackDetails[i][0][3] = (float) (attackPosition[0][0] + (double) ((attackLength - 15) * Math
						.cos(angle[0] + theta2 + theta)));

				attackDetails[i][1][0] = (float) (attackPosition[0][1] + (double) ((attackLength - 15) * Math
						.sin(angle[0] + theta2 + (PI - theta))));
				attackDetails[i][1][1] = (float) (attackPosition[0][1] + (double) ((attackLength - 15) * Math
						.sin(angle[0] + theta2 - (PI - theta))));
				attackDetails[i][1][2] = (float) (attackPosition[0][1] + (double) ((attackLength - 15) * Math
						.sin(angle[0] + theta2 - theta)));
				attackDetails[i][1][3] = (float) (attackPosition[0][1] + (double) ((attackLength - 15) * Math
						.sin(angle[0] + theta2 + theta)));

			}
		} else {
			drawFigure = RECT;
			motionNumber = MAID1;
			motionStyle = 2;

			motionNum = 1;
			moveNum = 15;

			animeState = 0;
			aFrameCount = 0;

			attackPosition = new float[motionNum][2];
			liveFlag = new boolean[motionNum];
			angle = new float[motionNum];
			sin = new double[motionNum];
			cos = new double[motionNum];
			attackDetails = new float[motionNum][2][4];

			liveFlag[0] = true;
			attackLength = 20;

			r = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
			sin[0] = (y1 - y2) / r;
			cos[0] = (x1 - x2) / r;
			angle[0] = (float) Math.atan2(sin[0], cos[0]);

			theta = 45.0 / 180.0 * PI;

			attackPosition[0][0] = (float) (x1 - 20 * cos[0]);
			attackPosition[0][1] = (float) (y1 - 20 * sin[0]);

			attackDetails[0][0][0] = (float) (attackPosition[0][0] + (double) (attackLength * Math
					.cos(angle[0] + (PI - theta))));
			attackDetails[0][0][1] = (float) (attackPosition[0][0] + (double) (attackLength * Math
					.cos(angle[0] - (PI - theta))));
			attackDetails[0][0][2] = (float) (attackPosition[0][0] + (double) (attackLength * Math
					.cos(angle[0] - theta)));
			attackDetails[0][0][3] = (float) (attackPosition[0][0] + (double) (attackLength * Math
					.cos(angle[0] + theta)));

			attackDetails[0][1][0] = (float) (attackPosition[0][1] + (double) (attackLength * Math
					.sin(angle[0] + (PI - theta))));
			attackDetails[0][1][1] = (float) (attackPosition[0][1] + (double) (attackLength * Math
					.sin(angle[0] - (PI - theta))));
			attackDetails[0][1][2] = (float) (attackPosition[0][1] + (double) (attackLength * Math
					.sin(angle[0] - theta)));
			attackDetails[0][1][3] = (float) (attackPosition[0][1] + (double) (attackLength * Math
					.sin(angle[0] + theta)));
		}
	}

	// メイドの強攻撃
	public void maidMotion2(boolean style) {
		if (!style) {
			drawFigure = RECT;
			motionNumber = MAID2;
			motionStyle = 1;

			motionNum = 10;
			moveNum = 0;

			attackPosition = new float[motionNum][2];
			liveFlag = new boolean[motionNum];
			angle = new float[motionNum];
			sin = new double[motionNum];
			cos = new double[motionNum];
			attackDetails = new float[motionNum][2][4];

			liveFlag[0] = true;
			attackLength = 60;

			r = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
			sin[0] = (y1 - y2) / r;
			cos[0] = (x1 - x2) / r;
			angle[0] = (float) Math.atan2(sin[0], cos[0]);

			theta = 45.0 / 180.0 * PI;

			attackPosition[0][0] = (float) x1;
			attackPosition[0][1] = (float) y1;

			attackDetails[0][0][0] = (float) (attackPosition[0][0] + (double) (attackLength * Math
					.cos(angle[0] + (PI - theta))));
			attackDetails[0][0][1] = (float) (attackPosition[0][0] + (double) (attackLength * Math
					.cos(angle[0] - (PI - theta))));
			attackDetails[0][0][2] = (float) (attackPosition[0][0] + (double) (attackLength * Math
					.cos(angle[0] - theta)));
			attackDetails[0][0][3] = (float) (attackPosition[0][0] + (double) (attackLength * Math
					.cos(angle[0] + theta)));

			attackDetails[0][1][0] = (float) (attackPosition[0][1] + (double) (attackLength * Math
					.sin(angle[0] + (PI - theta))));
			attackDetails[0][1][1] = (float) (attackPosition[0][1] + (double) (attackLength * Math
					.sin(angle[0] - (PI - theta))));
			attackDetails[0][1][2] = (float) (attackPosition[0][1] + (double) (attackLength * Math
					.sin(angle[0] - theta)));
			attackDetails[0][1][3] = (float) (attackPosition[0][1] + (double) (attackLength * Math
					.sin(angle[0] + theta)));

			for (int i = 1; i < motionNum; i++) {
				liveFlag[i] = true;
				theta2 = i * 10 / 180.0 * PI;

				attackPosition[i][0] = (float) (attackPosition[0][0] + 10 * Math
						.cos(theta2 + ((20 * i) / 180.0 * PI)));
				attackPosition[i][1] = (float) (attackPosition[0][1] + 10 * Math
						.cos(theta2 + ((20 * i) / 180.0 * PI)));

				attackDetails[i][0][0] = (float) (attackPosition[0][0] + (double) ((attackLength - 20) * Math
						.cos(angle[0] + theta2 + (PI - theta))));
				attackDetails[i][0][1] = (float) (attackPosition[0][0] + (double) ((attackLength - 20) * Math
						.cos(angle[0] + theta2 - (PI - theta))));
				attackDetails[i][0][2] = (float) (attackPosition[0][0] + (double) ((attackLength - 20) * Math
						.cos(angle[0] + theta2 - theta)));
				attackDetails[i][0][3] = (float) (attackPosition[0][0] + (double) ((attackLength - 20) * Math
						.cos(angle[0] + theta2 + theta)));

				attackDetails[i][1][0] = (float) (attackPosition[0][1] + (double) ((attackLength - 20) * Math
						.sin(angle[0] + theta2 + (PI - theta))));
				attackDetails[i][1][1] = (float) (attackPosition[0][1] + (double) ((attackLength - 20) * Math
						.sin(angle[0] + theta2 - (PI - theta))));
				attackDetails[i][1][2] = (float) (attackPosition[0][1] + (double) ((attackLength - 20) * Math
						.sin(angle[0] + theta2 - theta)));
				attackDetails[i][1][3] = (float) (attackPosition[0][1] + (double) ((attackLength - 20) * Math
						.sin(angle[0] + theta2 + theta)));

			}

		} else {
			drawFigure = RECT;
			motionNumber = MAID2;
			motionStyle = 2;

			motionNum = 1;
			moveNum = 25;

			animeState = 0;
			aFrameCount = 0;

			attackPosition = new float[motionNum][2];
			liveFlag = new boolean[motionNum];
			angle = new float[motionNum];
			sin = new double[motionNum];
			cos = new double[motionNum];
			attackDetails = new float[motionNum][2][4];

			liveFlag[0] = true;
			attackLength = 10;

			r = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
			sin[0] = (y1 - y2) / r;
			cos[0] = (x1 - x2) / r;
			angle[0] = (float) Math.atan2(sin[0], cos[0]);

			theta = 45.0 / 180.0 * PI;

			attackPosition[0][0] = (float) (x1 - 20 * cos[0]);
			attackPosition[0][1] = (float) (y1 - 20 * sin[0]);

			attackDetails[0][0][0] = (float) (attackPosition[0][0] + (double) (attackLength * Math
					.cos(angle[0] + (PI - theta))));
			attackDetails[0][0][1] = (float) (attackPosition[0][0] + (double) (attackLength * Math
					.cos(angle[0] - (PI - theta))));
			attackDetails[0][0][2] = (float) (attackPosition[0][0] + (double) (attackLength * Math
					.cos(angle[0] - theta)));
			attackDetails[0][0][3] = (float) (attackPosition[0][0] + (double) (attackLength * Math
					.cos(angle[0] + theta)));

			attackDetails[0][1][0] = (float) (attackPosition[0][1] + (double) (attackLength * Math
					.sin(angle[0] + (PI - theta))));
			attackDetails[0][1][1] = (float) (attackPosition[0][1] + (double) (attackLength * Math
					.sin(angle[0] - (PI - theta))));
			attackDetails[0][1][2] = (float) (attackPosition[0][1] + (double) (attackLength * Math
					.sin(angle[0] - theta)));
			attackDetails[0][1][3] = (float) (attackPosition[0][1] + (double) (attackLength * Math
					.sin(angle[0] + theta)));
		}
	}

	// 魔法使いの弱攻撃
	public void magicianMotion1(boolean style) {
		if (!style) {
			drawFigure = RECT;
			motionNumber = MAGIC1;
			motionStyle = 1;

			if (!char1.chargeFlag) {
				motionNum = 1;
				moveNum = 20;

				attackPosition = new float[motionNum][2];
				attackPosition[0][0] = (float) x1;
				attackPosition[0][1] = (float) y1;

				liveFlag = new boolean[motionNum];
				liveFlag[0] = true;

				angle = new float[motionNum];
				sin = new double[motionNum];
				cos = new double[motionNum];

				attackLength = 20f;
				attackDetails = new float[motionNum][2][4];

				r = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
				sin[0] = (y1 - y2) / r;
				cos[0] = (x1 - x2) / r;

				angle[0] = (float) Math.atan2(sin[0], cos[0]);

				theta = 18.43495 / 180 * PI;

				attackDetails[0][0][0] = (float) (x1 + (double) (attackLength * Math
						.cos(angle[0] + (PI - theta))));
				attackDetails[0][0][1] = (float) (x1 + (double) (attackLength * Math
						.cos(angle[0] - (PI - theta))));
				attackDetails[0][0][2] = (float) (x1 + (double) (attackLength * Math
						.cos(angle[0] - theta)));
				attackDetails[0][0][3] = (float) (x1 + (double) (attackLength * Math
						.cos(angle[0] + theta)));

				attackDetails[0][1][0] = (float) (y1 + (double) (attackLength * Math
						.sin(angle[0] + (PI - theta))));
				attackDetails[0][1][1] = (float) (y1 + (double) (attackLength * Math
						.sin(angle[0] - (PI - theta))));
				attackDetails[0][1][2] = (float) (y1 + (double) (attackLength * Math
						.sin(angle[0] - theta)));
				attackDetails[0][1][3] = (float) (y1 + (double) (attackLength * Math
						.sin(angle[0] + theta)));

			} else {

				chargeFlag = true;
				motionNum = 3;
				moveNum = 20;

				attackPosition = new float[motionNum][2];

				liveFlag = new boolean[motionNum];

				angle = new float[motionNum];
				sin = new double[motionNum];
				cos = new double[motionNum];

				attackLength = 16f;
				attackDetails = new float[motionNum][2][4];

				for (int i = 0; i < 3; i++) {

					attackPosition[i][0] = (float) x1;
					attackPosition[i][1] = (float) y1;

					liveFlag[i] = true;

					r = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
					sin[i] = (y1 - y2) / r;
					cos[i] = (x1 - x2) / r;

					switch (i) {
					case 0:
						angle[i] = (float) Math.atan2(sin[i], cos[i]);
						break;

					case 1:
						angle[i] = (float) Math.atan2(sin[i], cos[i]);
						angle[i] += (float) (20.0 / 180 * PI);
						sin[i] = Math.sin(angle[i]);
						cos[i] = Math.cos(angle[i]);
						break;

					case 2:
						angle[i] = (float) Math.atan2(sin[i], cos[i]);
						angle[i] -= (float) (20.0 / 180 * PI);
						sin[i] = Math.sin(angle[i]);
						cos[i] = Math.cos(angle[i]);
						break;
					}

					theta = 18.43495 / 180 * PI;

					attackDetails[i][0][0] = (float) (x1 + (double) (attackLength * Math
							.cos(angle[i] + (PI - theta))));
					attackDetails[i][0][1] = (float) (x1 + (double) (attackLength * Math
							.cos(angle[i] - (PI - theta))));
					attackDetails[i][0][2] = (float) (x1 + (double) (attackLength * Math
							.cos(angle[i] - theta)));
					attackDetails[i][0][3] = (float) (x1 + (double) (attackLength * Math
							.cos(angle[i] + theta)));

					attackDetails[i][1][0] = (float) (y1 + (double) (attackLength * Math
							.sin(angle[i] + (PI - theta))));
					attackDetails[i][1][1] = (float) (y1 + (double) (attackLength * Math
							.sin(angle[i] - (PI - theta))));
					attackDetails[i][1][2] = (float) (y1 + (double) (attackLength * Math
							.sin(angle[i] - theta)));
					attackDetails[i][1][3] = (float) (y1 + (double) (attackLength * Math
							.sin(angle[i] + theta)));
				}
			}

			char1.chargeFlag = false;
			char1.chargeFrame = 0;

			char1.nx += 10 * cos[0];
			char1.ny += 10 * sin[0];

		} else {
			int standard;

			drawFigure = RECT;
			motionNumber = MAGIC1;
			motionStyle = 2;

			if (!char1.chargeFlag) {
				motionNum = 7;
				standard = 3;
			} else {
				chargeFlag = true;
				motionNum = 13;
				standard = 7;
			}

			moveNum = 10;

			attackPosition = new float[motionNum][2];

			liveFlag = new boolean[motionNum];

			angle = new float[motionNum];
			sin = new double[motionNum];
			cos = new double[motionNum];

			attackLength = 16f;
			attackDetails = new float[motionNum][2][4];

			r = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));

			sin[standard] = (y1 - y2) / r;
			cos[standard] = (x1 - x2) / r;
			angle[standard] = (float) Math.atan2(sin[standard], cos[standard]);

			for (int i = 0; i < motionNum; i++) {

				attackPosition[i][0] = (float) x1;
				attackPosition[i][1] = (float) y1;

				liveFlag[i] = false;

				if (i == 0) {
					angle[i] = (float) (angle[standard] - 60.0 / 180 * PI);
					sin[i] = Math.sin(angle[i]);
					cos[i] = Math.cos(angle[i]);
					liveFlag[i] = true;

				} else if (i == standard) {

				} else {
					angle[i] = (float) (angle[i - 1] + (120.0 / (motionNum - 1))
							/ 180 * PI);
					sin[i] = Math.sin(angle[i]);
					cos[i] = Math.cos(angle[i]);
				}

				theta = 10.0 / 180 * PI;

				attackDetails[i][0][0] = (float) (x1 + (double) (attackLength * Math
						.cos(angle[i] + (PI - theta))));
				attackDetails[i][0][1] = (float) (x1 + (double) (attackLength * Math
						.cos(angle[i] - (PI - theta))));
				attackDetails[i][0][2] = (float) (x1 + (double) (attackLength * Math
						.cos(angle[i] - theta)));
				attackDetails[i][0][3] = (float) (x1 + (double) (attackLength * Math
						.cos(angle[i] + theta)));

				attackDetails[i][1][0] = (float) (y1 + (double) (attackLength * Math
						.sin(angle[i] + (PI - theta))));
				attackDetails[i][1][1] = (float) (y1 + (double) (attackLength * Math
						.sin(angle[i] - (PI - theta))));
				attackDetails[i][1][2] = (float) (y1 + (double) (attackLength * Math
						.sin(angle[i] - theta)));
				attackDetails[i][1][3] = (float) (y1 + (double) (attackLength * Math
						.sin(angle[i] + theta)));
			}

			char1.chargeFlag = false;
			char1.chargeFrame = 0;
		}
	}

	// 魔法使いの強攻撃
	public void magicianMotion2(boolean style) {
		if (!style) {
			drawFigure = CIRCLE;
			motionNumber = MAGIC2;
			motionStyle = 1;

			Radius = 60;

			motionNum = 6;
			moveNum = 10;
			divisionNum = 30;

			attackPosition = new float[motionNum][2];
			liveFlag = new boolean[motionNum];
			sin = new double[motionNum];
			cos = new double[motionNum];

			for (int i = 0; i < 6; i++) {
				theta = i * 60.0 / 180.0 * PI;
				attackPosition[i][0] = (float) (x1 + Radius * Math.cos(theta));
				attackPosition[i][1] = (float) (y1 + Radius * Math.sin(theta));
				liveFlag[i] = true;
			}

		} else {
			drawFigure = CIRCLE;
			motionNumber = MAGIC2;
			motionStyle = 2;

			Radius = 60;

			motionNum = 6;
			moveNum = 10;
			divisionNum = 30;

			attackPosition = new float[motionNum][2];
			liveFlag = new boolean[motionNum];
			sin = new double[motionNum];
			cos = new double[motionNum];
			angle = new float[1];

			motionCenter_x = x1;
			motionCenter_y = y1;
			angle[0] = 0;

			for (int i = 0; i < 6; i++) {
				theta = (i * 60.0 + angle[0]) / 180.0 * PI;
				sin[i] = Math.sin(theta);
				cos[i] = Math.cos(theta);
				attackPosition[i][0] = (float) (x1 + Radius * cos[i]);
				attackPosition[i][1] = (float) (y1 + Radius * sin[i]);
				liveFlag[i] = true;
			}
		}
	}

	// 巫女の弱攻撃
	public void shrineMotion1(boolean style) {
		double m, obl;
		double sin0, cos0, angle0;

		if (!style) {
			drawFigure = RECT;
			motionNumber = SHRINE1;
			motionStyle = 1;

			if (char1.chargeFlag)
				motionNum = 5;
			else
				motionNum = 3;

			moveNum = 10;
			attackPosition = new float[motionNum][2];
			liveFlag = new boolean[motionNum];
			sin = new double[motionNum];
			cos = new double[motionNum];
			angle = new float[motionNum + 1];

			motionCenter_x = x1;
			motionCenter_y = y1;

			r = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
			sin0 = (y2 - y1) / r;
			cos0 = (x2 - x1) / r;

			angle0 = (float) Math.atan2(sin0, cos0);

			attackLength = 10f;
			attackDetails = new float[motionNum][2][4];

			frameCount = 0;

			theta = 30.0 / 180.0 * PI;

			for (int i = 0; i < motionNum; i++) {
				liveFlag[i] = true;
				attackPosition[i][0] = (float) (x1 + 30 * Math.cos(angle0 - PI
						/ 3 + rnd.nextInt(120) / 180f * PI));
				attackPosition[i][1] = (float) (y1 + 30 * Math.sin(angle0 - PI
						/ 3 + rnd.nextInt(120) / 180f * PI));
				sin[i] = (float) (Math.sin(angle0 - PI / 3 + rnd.nextInt(120)
						/ 180f * PI));
				cos[i] = (float) (Math.cos(angle0 - PI / 3 + rnd.nextInt(120)
						/ 180f * PI));
				angle[i] = (float) Math.atan2(sin[i], cos[i]);

				attackDetails[i][0][0] = (float) (x1 + (double) (attackLength * Math
						.cos(angle[0] + (PI - theta))));
				attackDetails[i][0][1] = (float) (x1 + (double) (attackLength * Math
						.cos(angle[0] - (PI - theta))));
				attackDetails[i][0][2] = (float) (x1 + (double) (attackLength * Math
						.cos(angle[0] - theta)));
				attackDetails[i][0][3] = (float) (x1 + (double) (attackLength * Math
						.cos(angle[0] + theta)));

				attackDetails[i][1][0] = (float) (y1 + (double) (attackLength * Math
						.sin(angle[0] + (PI - theta))));
				attackDetails[i][1][1] = (float) (y1 + (double) (attackLength * Math
						.sin(angle[0] - (PI - theta))));
				attackDetails[i][1][2] = (float) (y1 + (double) (attackLength * Math
						.sin(angle[0] - theta)));
				attackDetails[i][1][3] = (float) (y1 + (double) (attackLength * Math
						.sin(angle[0] + theta)));
			}

			char1.chargeFlag = false;
			char1.chargeFrame = 0;

		} else {
			drawFigure = RECT;
			motionNumber = SHRINE1;
			motionStyle = 2;

			motionNum = 1;
			moveNum = 20;
			attackPosition = new float[motionNum][2];
			liveFlag = new boolean[motionNum];
			sin = new double[motionNum];
			cos = new double[motionNum];
			angle = new float[motionNum + 1];

			liveFlag[0] = true;

			motionCenter_x = x1;
			motionCenter_y = y1;

			attackPosition[0][0] = (float) x1;
			attackPosition[0][1] = (float) y1;

			attackLength = 20f;
			attackDetails = new float[motionNum][2][4];

			r = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
			sin[0] = (y2 - y1) / r;
			cos[0] = (x2 - x1) / r;

			angle[0] = (float) Math.atan2(sin[0], cos[0]);

			theta = 60.0 / 180.0 * PI;

			v0 = (float) Math
					.sqrt((-G * r)
							/ (2 * (Math.cos(45 / 180f * PI) * Math
									.sin(45 / 180f * PI))));

			if (rnd.nextInt(2) == 0) {
				gx = (float) (-G * Math.sin(angle[0]));
				gy = (float) (G * Math.cos(angle[0]));
				v0x = (float) (v0 * Math.cos(45 / 180f * PI));
				v0y = (float) (v0 * Math.sin(45 / 180f * PI));
				vx = (float) (v0 * Math.cos(angle[0] + 45 / 180f * PI));
				vy = (float) (v0 * Math.sin(angle[0] + 45 / 180f * PI));
			} else {
				gx = (float) (G * Math.sin(angle[0]));
				gy = (float) (-G * Math.cos(angle[0]));
				v0x = (float) (v0 * Math.cos(-45 / 180f * PI));
				v0y = (float) (v0 * Math.sin(-45 / 180f * PI));
				vx = (float) (v0 * Math.cos(angle[0] - 45 / 180f * PI));
				vy = (float) (v0 * Math.sin(angle[0] - 45 / 180f * PI));
			}

			frameCount = 0;

			if (v0x >= 0) {
				if (v0y >= 0) {
					m = G * frameCount + v0y;
					obl = Math.sqrt(1 + Math.pow(m, 2));
					rad = Math.atan2(m / obl, 1 / obl);
				} else if (v0y < 0) {
					m = -G * frameCount + v0y;
					obl = Math.sqrt(1 + Math.pow(m, 2));
					rad = Math.atan2(m / obl, 1 / obl);
				}
			} else if (v0x < 0) {
				if (v0y >= 0) {
					m = G * frameCount + v0y;
					obl = Math.sqrt(1 + Math.pow(m, 2));
					rad = -Math.atan2(m / obl, 1 / obl);
				} else if (v0y < 0) {
					m = -G * frameCount + v0y;
					obl = Math.sqrt(1 + Math.pow(m, 2));
					rad = -Math.atan2(m / obl, 1 / obl);
				}
			}

			attackDetails[0][0][0] = (float) (x1 + (double) (attackLength * Math
					.cos(angle[0] + rad + (PI - theta))));
			attackDetails[0][0][1] = (float) (x1 + (double) (attackLength * Math
					.cos(angle[0] + rad - (PI - theta))));
			attackDetails[0][0][2] = (float) (x1 + (double) (attackLength * Math
					.cos(angle[0] + rad - theta)));
			attackDetails[0][0][3] = (float) (x1 + (double) (attackLength * Math
					.cos(angle[0] + rad + theta)));

			attackDetails[0][1][0] = (float) (y1 + (double) (attackLength * Math
					.sin(angle[0] + rad + (PI - theta))));
			attackDetails[0][1][1] = (float) (y1 + (double) (attackLength * Math
					.sin(angle[0] + rad - (PI - theta))));
			attackDetails[0][1][2] = (float) (y1 + (double) (attackLength * Math
					.sin(angle[0] + rad - theta)));
			attackDetails[0][1][3] = (float) (y1 + (double) (attackLength * Math
					.sin(angle[0] + rad + theta)));
		}
	}

	// 巫女の強攻撃
	public void shrineMotion2(boolean style) {
		if (!style) {
			drawFigure = RECT;
			motionNumber = SHRINE2;
			motionStyle = 1;

			motionNum = 1;
			moveNum = 10;
			attackPosition = new float[motionNum][2];
			liveFlag = new boolean[motionNum];
			sin = new double[motionNum];
			cos = new double[motionNum];
			angle = new float[motionNum];

			liveFlag[0] = true;

			motionCenter_x = x1;
			motionCenter_y = y1;

			attackPosition[0][0] = (float) x1;
			attackPosition[0][1] = (float) y1;

			attackLength = 40f;
			attackDetails = new float[motionNum][2][4];

			r = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
			sin[0] = (y1 - y2) / r;
			cos[0] = (x1 - x2) / r;

			angle[0] = (float) Math.atan2(sin[0], cos[0]);

			theta = 45.0 / 180.0 * PI;

			attackDetails[0][0][0] = (float) (x1 + (double) (attackLength * Math
					.cos(angle[0] + (PI - theta))));
			attackDetails[0][0][1] = (float) (x1 + (double) (attackLength * Math
					.cos(angle[0] - (PI - theta))));
			attackDetails[0][0][2] = (float) (x1 + (double) (attackLength * Math
					.cos(angle[0] - theta)));
			attackDetails[0][0][3] = (float) (x1 + (double) (attackLength * Math
					.cos(angle[0] + theta)));

			attackDetails[0][1][0] = (float) (y1 + (double) (attackLength * Math
					.sin(angle[0] + (PI - theta))));
			attackDetails[0][1][1] = (float) (y1 + (double) (attackLength * Math
					.sin(angle[0] - (PI - theta))));
			attackDetails[0][1][2] = (float) (y1 + (double) (attackLength * Math
					.sin(angle[0] - theta)));
			attackDetails[0][1][3] = (float) (y1 + (double) (attackLength * Math
					.sin(angle[0] + theta)));

		} else {
			drawFigure = RECT;
			motionNumber = SHRINE2;
			motionStyle = 2;

			motionNum = 2;
			moveNum = 0;

			animeState = 0;
			aFrameCount = 0;

			attackPosition = new float[motionNum][2];
			liveFlag = new boolean[motionNum];
			angle = new float[motionNum];
			sin = new double[motionNum];
			cos = new double[motionNum];
			attackDetails = new float[motionNum][2][4];

			liveFlag[0] = true;
			liveFlag[1] = true;

			attackLength = 55.9016f;

			theta = 26.565051 / 180.0 * PI;

			attackPosition[0][0] = (float) x2;
			attackPosition[0][1] = (float) (y2 + 100);

			attackDetails[0][0][0] = (float) (attackPosition[0][0] + (double) (attackLength * Math
					.cos(PI - theta)));
			attackDetails[0][0][1] = (float) (attackPosition[0][0] + (double) (attackLength * Math
					.cos(PI + theta)));
			attackDetails[0][0][2] = (float) (attackPosition[0][0] + (double) (attackLength * Math
					.cos(-theta)));
			attackDetails[0][0][3] = (float) (attackPosition[0][0] + (double) (attackLength * Math
					.cos(theta)));

			attackDetails[0][1][0] = (float) (attackPosition[0][1] + (double) (attackLength * Math
					.sin(PI - theta)));
			attackDetails[0][1][1] = (float) (attackPosition[0][1] + (double) (attackLength * Math
					.sin(PI + theta)));
			attackDetails[0][1][2] = (float) (attackPosition[0][1] + (double) (attackLength * Math
					.sin(-theta)));
			attackDetails[0][1][3] = (float) (attackPosition[0][1] + (double) (attackLength * Math
					.sin(theta)));

			attackPosition[1][0] = (float) x2;
			attackPosition[1][1] = (float) (y2 + 200);

			attackDetails[1][0][0] = attackDetails[0][0][0];
			attackDetails[1][0][1] = attackDetails[1][0][0];
			attackDetails[1][0][2] = attackDetails[0][0][2];
			attackDetails[1][0][3] = attackDetails[1][0][2];

			attackDetails[1][1][0] = attackDetails[0][1][1] + 20;
			attackDetails[1][1][1] = attackDetails[1][1][0] - 400;
			attackDetails[1][1][2] = attackDetails[1][1][1];
			attackDetails[1][1][3] = attackDetails[1][1][0];
		}
	}

	// モーションの移動
	synchronized public void attackMove() {
		double m, obl;

		if (motionNumber == SWORD1) {
			aFrameCount++;

			if (aFrameCount == 4) {
				animeState++;
				aFrameCount = 0;

				if (animeState == 9) {
					animeState = 0;
				}
			}

			r = Math.sqrt((char1.x - char2.x) * (char1.x - char2.x)
					+ (char1.y - char2.y) * (char1.y - char2.y));
			sin[0] = (char1.y - char2.y) / r;
			cos[0] = (char1.x - char2.x) / r;
			angle[0] = (float) Math.atan2(sin[0], cos[0]);

			attackPosition[0][0] = (float) (char1.x - 30 * cos[0]);
			attackPosition[0][1] = (float) (char1.y - 30 * sin[0]);

			attackDetails[0][0][0] = (float) (attackPosition[0][0] + (attackLength * Math
					.cos(angle[0] + (PI - theta))));
			attackDetails[0][0][1] = (float) (attackPosition[0][0] + (attackLength * Math
					.cos(angle[0] - (PI - theta))));
			attackDetails[0][0][2] = (float) (attackPosition[0][0] + (attackLength * Math
					.cos(angle[0] - theta)));
			attackDetails[0][0][3] = (float) (attackPosition[0][0] + (attackLength * Math
					.cos(angle[0] + theta)));

			attackDetails[0][1][0] = (float) (attackPosition[0][1] + (attackLength * Math
					.sin(angle[0] + (PI - theta))));
			attackDetails[0][1][1] = (float) (attackPosition[0][1] + (attackLength * Math
					.sin(angle[0] - (PI - theta))));
			attackDetails[0][1][2] = (float) (attackPosition[0][1] + (attackLength * Math
					.sin(angle[0] - theta)));
			attackDetails[0][1][3] = (float) (attackPosition[0][1] + (attackLength * Math
					.sin(angle[0] + theta)));

			for (int i = 1; i < motionNum; i++) {
				liveFlag[i] = true;
				theta2 = i * 10 / 180.0 * PI;

				attackDetails[i][0][0] = (float) (attackPosition[0][0] + (double) ((attackLength - 15) * Math
						.cos(angle[0] + theta2 + (PI - theta))));
				attackDetails[i][0][1] = (float) (attackPosition[0][0] + (double) ((attackLength - 15) * Math
						.cos(angle[0] + theta2 - (PI - theta))));
				attackDetails[i][0][2] = (float) (attackPosition[0][0] + (double) ((attackLength - 15) * Math
						.cos(angle[0] + theta2 - theta)));
				attackDetails[i][0][3] = (float) (attackPosition[0][0] + (double) ((attackLength - 15) * Math
						.cos(angle[0] + theta2 + theta)));

				attackDetails[i][1][0] = (float) (attackPosition[0][1] + (double) ((attackLength - 15) * Math
						.sin(angle[0] + theta2 + (PI - theta))));
				attackDetails[i][1][1] = (float) (attackPosition[0][1] + (double) ((attackLength - 15) * Math
						.sin(angle[0] + theta2 - (PI - theta))));
				attackDetails[i][1][2] = (float) (attackPosition[0][1] + (double) ((attackLength - 15) * Math
						.sin(angle[0] + theta2 - theta)));
				attackDetails[i][1][3] = (float) (attackPosition[0][1] + (double) ((attackLength - 15) * Math
						.sin(angle[0] + theta2 + theta)));

			}

		} else if (motionNumber == SWORD2) {
			if (motionStyle == 1) {
				aFrameCount++;
				if (aFrameCount == 4) {
					animeState++;
					aFrameCount = 0;

					if (animeState == 4)
						animeState = 0;
				}

				attackPosition[0][0] -= moveNum * cos[0];
				attackPosition[0][1] -= moveNum * sin[0];

				attackDetails[0][0][0] = (float) (attackPosition[0][0] + (attackLength * Math
						.cos(angle[0] + (PI - theta))));
				attackDetails[0][0][1] = (float) (attackPosition[0][0] + (attackLength * Math
						.cos(angle[0] - (PI - theta))));
				attackDetails[0][0][2] = (float) (attackPosition[0][0] + (attackLength * Math
						.cos(angle[0] - theta)));
				attackDetails[0][0][3] = (float) (attackPosition[0][0] + (attackLength * Math
						.cos(angle[0] + theta)));

				attackDetails[0][1][0] = (float) (attackPosition[0][1] + (attackLength * Math
						.sin(angle[0] + (PI - theta))));
				attackDetails[0][1][1] = (float) (attackPosition[0][1] + (attackLength * Math
						.sin(angle[0] - (PI - theta))));
				attackDetails[0][1][2] = (float) (attackPosition[0][1] + (attackLength * Math
						.sin(angle[0] - theta)));
				attackDetails[0][1][3] = (float) (attackPosition[0][1] + (attackLength * Math
						.sin(angle[0] + theta)));

			} else if (motionStyle == 2) {
				r = Math.sqrt((char1.x - char2.x) * (char1.x - char2.x)
						+ (char1.y - char2.y) * (char1.y - char2.y));
				sin[0] = (char1.y - char2.y) / r;
				cos[0] = (char1.x - char2.x) / r;
				angle[0] = (float) Math.atan2(sin[0], cos[0]);

				attackPosition[0][0] = (float) (char1.x - 30 * cos[0]);
				attackPosition[0][1] = (float) (char1.y - 30 * sin[0]);

				attackDetails[0][0][0] = (float) (attackPosition[0][0] + (attackLength * Math
						.cos(angle[0] + (PI - theta))));
				attackDetails[0][0][1] = (float) (attackPosition[0][0] + (attackLength * Math
						.cos(angle[0] - (PI - theta))));
				attackDetails[0][0][2] = (float) (attackPosition[0][0] + (attackLength * Math
						.cos(angle[0] - theta)));
				attackDetails[0][0][3] = (float) (attackPosition[0][0] + (attackLength * Math
						.cos(angle[0] + theta)));

				attackDetails[0][1][0] = (float) (attackPosition[0][1] + (attackLength * Math
						.sin(angle[0] + (PI - theta))));
				attackDetails[0][1][1] = (float) (attackPosition[0][1] + (attackLength * Math
						.sin(angle[0] - (PI - theta))));
				attackDetails[0][1][2] = (float) (attackPosition[0][1] + (attackLength * Math
						.sin(angle[0] - theta)));
				attackDetails[0][1][3] = (float) (attackPosition[0][1] + (attackLength * Math
						.sin(angle[0] + theta)));

				for (int i = 1; i < motionNum; i++) {
					liveFlag[i] = true;
					theta2 = i * 10 / 180.0 * PI;

					attackDetails[i][0][0] = (float) (attackPosition[0][0] + (double) ((attackLength - 15) * Math
							.cos(angle[0] + theta2 + (PI - theta))));
					attackDetails[i][0][1] = (float) (attackPosition[0][0] + (double) ((attackLength - 15) * Math
							.cos(angle[0] + theta2 - (PI - theta))));
					attackDetails[i][0][2] = (float) (attackPosition[0][0] + (double) ((attackLength - 15) * Math
							.cos(angle[0] + theta2 - theta)));
					attackDetails[i][0][3] = (float) (attackPosition[0][0] + (double) ((attackLength - 15) * Math
							.cos(angle[0] + theta2 + theta)));

					attackDetails[i][1][0] = (float) (attackPosition[0][1] + (double) ((attackLength - 15) * Math
							.sin(angle[0] + theta2 + (PI - theta))));
					attackDetails[i][1][1] = (float) (attackPosition[0][1] + (double) ((attackLength - 15) * Math
							.sin(angle[0] + theta2 - (PI - theta))));
					attackDetails[i][1][2] = (float) (attackPosition[0][1] + (double) ((attackLength - 15) * Math
							.sin(angle[0] + theta2 - theta)));
					attackDetails[i][1][3] = (float) (attackPosition[0][1] + (double) ((attackLength - 15) * Math
							.sin(angle[0] + theta2 + theta)));

				}

			}

		} else if (motionNumber == MAID1) {
			if (motionStyle == 1) {
				r = Math.sqrt((char1.x - char2.x) * (char1.x - char2.x)
						+ (char1.y - char2.y) * (char1.y - char2.y));
				sin[0] = (char1.y - char2.y) / r;
				cos[0] = (char1.x - char2.x) / r;
				angle[0] = (float) Math.atan2(sin[0], cos[0]);

				attackPosition[0][0] = (float) (char1.x - 30 * cos[0]);
				attackPosition[0][1] = (float) (char1.y - 30 * sin[0]);

				attackDetails[0][0][0] = (float) (attackPosition[0][0] + (attackLength * Math
						.cos(angle[0] + (PI - theta))));
				attackDetails[0][0][1] = (float) (attackPosition[0][0] + (attackLength * Math
						.cos(angle[0] - (PI - theta))));
				attackDetails[0][0][2] = (float) (attackPosition[0][0] + (attackLength * Math
						.cos(angle[0] - theta)));
				attackDetails[0][0][3] = (float) (attackPosition[0][0] + (attackLength * Math
						.cos(angle[0] + theta)));

				attackDetails[0][1][0] = (float) (attackPosition[0][1] + (attackLength * Math
						.sin(angle[0] + (PI - theta))));
				attackDetails[0][1][1] = (float) (attackPosition[0][1] + (attackLength * Math
						.sin(angle[0] - (PI - theta))));
				attackDetails[0][1][2] = (float) (attackPosition[0][1] + (attackLength * Math
						.sin(angle[0] - theta)));
				attackDetails[0][1][3] = (float) (attackPosition[0][1] + (attackLength * Math
						.sin(angle[0] + theta)));

				for (int i = 1; i < motionNum; i++) {
					liveFlag[i] = true;
					theta2 = i * 10 / 180.0 * PI;

					attackDetails[i][0][0] = (float) (attackPosition[0][0] + (double) ((attackLength - 15) * Math
							.cos(angle[0] + theta2 + (PI - theta))));
					attackDetails[i][0][1] = (float) (attackPosition[0][0] + (double) ((attackLength - 15) * Math
							.cos(angle[0] + theta2 - (PI - theta))));
					attackDetails[i][0][2] = (float) (attackPosition[0][0] + (double) ((attackLength - 15) * Math
							.cos(angle[0] + theta2 - theta)));
					attackDetails[i][0][3] = (float) (attackPosition[0][0] + (double) ((attackLength - 15) * Math
							.cos(angle[0] + theta2 + theta)));

					attackDetails[i][1][0] = (float) (attackPosition[0][1] + (double) ((attackLength - 15) * Math
							.sin(angle[0] + theta2 + (PI - theta))));
					attackDetails[i][1][1] = (float) (attackPosition[0][1] + (double) ((attackLength - 15) * Math
							.sin(angle[0] + theta2 - (PI - theta))));
					attackDetails[i][1][2] = (float) (attackPosition[0][1] + (double) ((attackLength - 15) * Math
							.sin(angle[0] + theta2 - theta)));
					attackDetails[i][1][3] = (float) (attackPosition[0][1] + (double) ((attackLength - 15) * Math
							.sin(angle[0] + theta2 + theta)));

				}

			} else if (motionStyle == 2) {
				attackPosition[0][0] -= moveNum * cos[0];
				attackPosition[0][1] -= moveNum * sin[0];

				attackDetails[0][0][0] = (float) (attackPosition[0][0] + (attackLength * Math
						.cos(angle[0] + (PI - theta))));
				attackDetails[0][0][1] = (float) (attackPosition[0][0] + (attackLength * Math
						.cos(angle[0] - (PI - theta))));
				attackDetails[0][0][2] = (float) (attackPosition[0][0] + (attackLength * Math
						.cos(angle[0] - theta)));
				attackDetails[0][0][3] = (float) (attackPosition[0][0] + (attackLength * Math
						.cos(angle[0] + theta)));

				attackDetails[0][1][0] = (float) (attackPosition[0][1] + (attackLength * Math
						.sin(angle[0] + (PI - theta))));
				attackDetails[0][1][1] = (float) (attackPosition[0][1] + (attackLength * Math
						.sin(angle[0] - (PI - theta))));
				attackDetails[0][1][2] = (float) (attackPosition[0][1] + (attackLength * Math
						.sin(angle[0] - theta)));
				attackDetails[0][1][3] = (float) (attackPosition[0][1] + (attackLength * Math
						.sin(angle[0] + theta)));
			}

		} else if (motionNumber == MAID2) {
			if (motionStyle == 1) {
				char1.tDashFlag = true;

				r = Math.sqrt((char1.x - char2.x) * (char1.x - char2.x)
						+ (char1.y - char2.y) * (char1.y - char2.y));
				sin[0] = (char1.y - char2.y) / r;
				cos[0] = (char1.x - char2.x) / r;
				angle[0] = (float) Math.atan2(sin[0], cos[0]);

				attackPosition[0][0] = (float) char1.x;
				attackPosition[0][1] = (float) char1.y;

				attackDetails[0][0][0] = (float) (attackPosition[0][0] + (attackLength * Math
						.cos(angle[0] + (PI - theta))));
				attackDetails[0][0][1] = (float) (attackPosition[0][0] + (attackLength * Math
						.cos(angle[0] - (PI - theta))));
				attackDetails[0][0][2] = (float) (attackPosition[0][0] + (attackLength * Math
						.cos(angle[0] - theta)));
				attackDetails[0][0][3] = (float) (attackPosition[0][0] + (attackLength * Math
						.cos(angle[0] + theta)));

				attackDetails[0][1][0] = (float) (attackPosition[0][1] + (attackLength * Math
						.sin(angle[0] + (PI - theta))));
				attackDetails[0][1][1] = (float) (attackPosition[0][1] + (attackLength * Math
						.sin(angle[0] - (PI - theta))));
				attackDetails[0][1][2] = (float) (attackPosition[0][1] + (attackLength * Math
						.sin(angle[0] - theta)));
				attackDetails[0][1][3] = (float) (attackPosition[0][1] + (attackLength * Math
						.sin(angle[0] + theta)));

				for (int i = 1; i < motionNum; i++) {
					liveFlag[i] = true;
					theta2 = i * 10 / 180.0 * PI;

					attackPosition[i][0] = (float) (attackPosition[0][0] + 10 * Math
							.cos(theta2 + ((20 * i) / 180.0 * PI)));
					attackPosition[i][1] = (float) (attackPosition[0][1] + 10 * Math
							.cos(theta2 + ((20 * i) / 180.0 * PI)));

					attackDetails[i][0][0] = (float) (attackPosition[0][0] + (double) ((attackLength - 20) * Math
							.cos(angle[0] + theta2 + (PI - theta))));
					attackDetails[i][0][1] = (float) (attackPosition[0][0] + (double) ((attackLength - 20) * Math
							.cos(angle[0] + theta2 - (PI - theta))));
					attackDetails[i][0][2] = (float) (attackPosition[0][0] + (double) ((attackLength - 20) * Math
							.cos(angle[0] + theta2 - theta)));
					attackDetails[i][0][3] = (float) (attackPosition[0][0] + (double) ((attackLength - 20) * Math
							.cos(angle[0] + theta2 + theta)));

					attackDetails[i][1][0] = (float) (attackPosition[0][1] + (double) ((attackLength - 20) * Math
							.sin(angle[0] + theta2 + (PI - theta))));
					attackDetails[i][1][1] = (float) (attackPosition[0][1] + (double) ((attackLength - 20) * Math
							.sin(angle[0] + theta2 - (PI - theta))));
					attackDetails[i][1][2] = (float) (attackPosition[0][1] + (double) ((attackLength - 20) * Math
							.sin(angle[0] + theta2 - theta)));
					attackDetails[i][1][3] = (float) (attackPosition[0][1] + (double) ((attackLength - 20) * Math
							.sin(angle[0] + theta2 + theta)));

				}

			} else if (motionStyle == 2) {
				attackPosition[0][0] -= moveNum * cos[0];
				attackPosition[0][1] -= moveNum * sin[0];

				attackDetails[0][0][0] = (float) (attackPosition[0][0] + (attackLength * Math
						.cos(angle[0] + (PI - theta))));
				attackDetails[0][0][1] = (float) (attackPosition[0][0] + (attackLength * Math
						.cos(angle[0] - (PI - theta))));
				attackDetails[0][0][2] = (float) (attackPosition[0][0] + (attackLength * Math
						.cos(angle[0] - theta)));
				attackDetails[0][0][3] = (float) (attackPosition[0][0] + (attackLength * Math
						.cos(angle[0] + theta)));

				attackDetails[0][1][0] = (float) (attackPosition[0][1] + (attackLength * Math
						.sin(angle[0] + (PI - theta))));
				attackDetails[0][1][1] = (float) (attackPosition[0][1] + (attackLength * Math
						.sin(angle[0] - (PI - theta))));
				attackDetails[0][1][2] = (float) (attackPosition[0][1] + (attackLength * Math
						.sin(angle[0] - theta)));
				attackDetails[0][1][3] = (float) (attackPosition[0][1] + (attackLength * Math
						.sin(angle[0] + theta)));
			}

		} else if (motionNumber == MAGIC1) {

			if (motionStyle == 2) {
				if (frameCount < 36)
					frameCount++;

				if (frameCount % (36 / (motionNum - 1)) == 0)
					liveFlag[frameCount / (36 / (motionNum - 1))] = true;

				if (frameCount == 36) {
					frameCount = 37;
				}
			}

			for (int i = 0; i < motionNum; i++) {

				if (liveFlag[i]) {
					attackPosition[i][0] -= moveNum * cos[i];
					attackPosition[i][1] -= moveNum * sin[i];

					attackDetails[i][0][0] = (float) (attackPosition[i][0] + (attackLength * Math
							.cos(angle[i] + (PI - theta))));
					attackDetails[i][0][1] = (float) (attackPosition[i][0] + (attackLength * Math
							.cos(angle[i] - (PI - theta))));
					attackDetails[i][0][2] = (float) (attackPosition[i][0] + (attackLength * Math
							.cos(angle[i] - theta)));
					attackDetails[i][0][3] = (float) (attackPosition[i][0] + (attackLength * Math
							.cos(angle[i] + theta)));

					attackDetails[i][1][0] = (float) (attackPosition[i][1] + (attackLength * Math
							.sin(angle[i] + (PI - theta))));
					attackDetails[i][1][1] = (float) (attackPosition[i][1] + (attackLength * Math
							.sin(angle[i] - (PI - theta))));
					attackDetails[i][1][2] = (float) (attackPosition[i][1] + (attackLength * Math
							.sin(angle[i] - theta)));
					attackDetails[i][1][3] = (float) (attackPosition[i][1] + (attackLength * Math
							.sin(angle[i] + theta)));
				}
			}

		} else if (motionNumber == MAGIC2) {
			if (motionStyle == 1) {
				if (frameCount < 120)
					frameCount++;

				if (frameCount % 20 == 0) {
					moveCount++;

					r = Math.sqrt(Math
							.pow((char2.x - attackPosition[(frameCount / 20) - 1][0]),
									2)
							+ Math.pow(
									(char2.y - attackPosition[(frameCount / 20) - 1][1]),
									2));
					sin[(frameCount / 20) - 1] = (char2.y - attackPosition[(frameCount / 20) - 1][1])
							/ r;
					cos[(frameCount / 20) - 1] = (char2.x - attackPosition[(frameCount / 20) - 1][0])
							/ r;
				}

				if (moveCount > 6) {
					moveCount = 6;
					frameCount = 121;
				}

				for (int i = 0; i < moveCount; i++) {
					if (liveFlag[i]) {
						attackPosition[i][0] += moveNum * cos[i];
						attackPosition[i][1] += moveNum * sin[i];
					}
				}

			} else if (motionStyle == 2) {
				if (frameCount < 30)
					frameCount++;

				angle[0] += 2;
				Radius += 4;

				if (frameCount == 30) {
					frameCount = 31;
				}

				for (int i = 0; i < 6; i++) {
					theta = (i * 60.0 + angle[0]) / 180.0 * PI;
					sin[i] = Math.sin(theta);
					cos[i] = Math.cos(theta);
					attackPosition[i][0] = (float) (x1 + Radius * cos[i]);
					attackPosition[i][1] = (float) (y1 + Radius * sin[i]);
				}
			}

		} else if (motionNumber == SHRINE1) {
			if (motionStyle == 1) {
				frameCount++;

				if (frameCount >= 15) {
					for (int i = 0; i < motionNum; i++)
						liveFlag[i] = false;
					return;
				}

				for (int i = 0; i < motionNum; i++) {
					attackPosition[i][0] = (float) (attackPosition[i][0] + moveNum
							* cos[i]);
					attackPosition[i][1] = (float) (attackPosition[i][1] + moveNum
							* sin[i]);

					attackDetails[i][0][0] = (float) (attackPosition[i][0] + (attackLength * Math
							.cos(angle[i] + (PI - theta))));
					attackDetails[i][0][1] = (float) (attackPosition[i][0] + (attackLength * Math
							.cos(angle[i] - (PI - theta))));
					attackDetails[i][0][2] = (float) (attackPosition[i][0] + (attackLength * Math
							.cos(angle[i] - theta)));
					attackDetails[i][0][3] = (float) (attackPosition[i][0] + (attackLength * Math
							.cos(angle[i] + theta)));

					attackDetails[i][1][0] = (float) (attackPosition[i][1] + (attackLength * Math
							.sin(angle[i] + (PI - theta))));
					attackDetails[i][1][1] = (float) (attackPosition[i][1] + (attackLength * Math
							.sin(angle[i] - (PI - theta))));
					attackDetails[i][1][2] = (float) (attackPosition[i][1] + (attackLength * Math
							.sin(angle[i] - theta)));
					attackDetails[i][1][3] = (float) (attackPosition[i][1] + (attackLength * Math
							.sin(angle[i] + theta)));
				}

			} else if (motionStyle == 2) {
				for (int i = 0; i < motionNum; i++) {

					if (liveFlag[i]) {
						frameCount += 3;
						attackPosition[i][0] = (float) (motionCenter_x + acceMotion(
								vx, gx, frameCount));
						attackPosition[i][1] = (float) (motionCenter_y + acceMotion(
								vy, gy, frameCount));

						if (v0x >= 0) {
							if (v0y >= 0) {
								m = G * frameCount + v0y;
								obl = Math.sqrt(1 + Math.pow(m, 2));
								rad = Math.atan2(m / obl, 1 / obl);
							} else if (v0y < 0) {
								m = -G * frameCount + v0y;
								obl = Math.sqrt(1 + Math.pow(m, 2));
								rad = Math.atan2(m / obl, 1 / obl);
							}
						} else if (v0x < 0) {
							if (v0y >= 0) {
								m = G * frameCount + v0y;
								obl = Math.sqrt(1 + Math.pow(m, 2));
								rad = -Math.atan2(m / obl, 1 / obl);
							} else if (v0y < 0) {
								m = -G * frameCount + v0y;
								obl = Math.sqrt(1 + Math.pow(m, 2));
								rad = -Math.atan2(m / obl, 1 / obl);
							}
						}

						attackDetails[i][0][0] = (float) (attackPosition[i][0] + (attackLength * Math
								.cos(angle[i] + rad + (PI - theta))));
						attackDetails[i][0][1] = (float) (attackPosition[i][0] + (attackLength * Math
								.cos(angle[i] + rad - (PI - theta))));
						attackDetails[i][0][2] = (float) (attackPosition[i][0] + (attackLength * Math
								.cos(angle[i] + rad - theta)));
						attackDetails[i][0][3] = (float) (attackPosition[i][0] + (attackLength * Math
								.cos(angle[i] + rad + theta)));

						attackDetails[i][1][0] = (float) (attackPosition[i][1] + (attackLength * Math
								.sin(angle[i] + rad + (PI - theta))));
						attackDetails[i][1][1] = (float) (attackPosition[i][1] + (attackLength * Math
								.sin(angle[i] + rad - (PI - theta))));
						attackDetails[i][1][2] = (float) (attackPosition[i][1] + (attackLength * Math
								.sin(angle[i] + rad - theta)));
						attackDetails[i][1][3] = (float) (attackPosition[i][1] + (attackLength * Math
								.sin(angle[i] + rad + theta)));
					}
				}
			}

		} else if (motionNumber == SHRINE2) {
			if (motionStyle == 1) {
				for (int i = 0; i < motionNum; i++) {

					if (liveFlag[i]) {
						attackPosition[i][0] -= moveNum * cos[i];
						attackPosition[i][1] -= moveNum * sin[i];

						angle[i] += 0.2;

						attackDetails[i][0][0] = (float) (attackPosition[i][0] + (attackLength * Math
								.cos(angle[i] + (PI - theta))));
						attackDetails[i][0][1] = (float) (attackPosition[i][0] + (attackLength * Math
								.cos(angle[i] - (PI - theta))));
						attackDetails[i][0][2] = (float) (attackPosition[i][0] + (attackLength * Math
								.cos(angle[i] - theta)));
						attackDetails[i][0][3] = (float) (attackPosition[i][0] + (attackLength * Math
								.cos(angle[i] + theta)));

						attackDetails[i][1][0] = (float) (attackPosition[i][1] + (attackLength * Math
								.sin(angle[i] + (PI - theta))));
						attackDetails[i][1][1] = (float) (attackPosition[i][1] + (attackLength * Math
								.sin(angle[i] - (PI - theta))));
						attackDetails[i][1][2] = (float) (attackPosition[i][1] + (attackLength * Math
								.sin(angle[i] - theta)));
						attackDetails[i][1][3] = (float) (attackPosition[i][1] + (attackLength * Math
								.sin(angle[i] + theta)));
					}
				}

			} else if (motionStyle == 2) {
				aFrameCount++;

				if (aFrameCount == 30) {
					animeState++;
					aFrameCount = 0;

					if (animeState == 2) {
						animeState = 0;
					}
				}
			}
		}
	}

	// 等加速度運動
	public float acceMotion(float v0, float a, float f) {
		float h;

		h = v0 * f + a * f * f / 2;

		return h;
	}

	// 攻撃モーションの位置チェック
	public void removeCheck(int fieldCenter_x, int fieldCenter_y) {
		double dx, dy;
		double distanceFromCenter;
		int count = 0;

		for (int i = 0; i < motionNum; i++) {
			dx = attackPosition[i][0] - fieldCenter_x;
			dy = attackPosition[i][1] - fieldCenter_y;

			distanceFromCenter = Math.sqrt(dx * dx + dy * dy);

			// 戦闘エリア外に出ている場合
			if (distanceFromCenter >= fieldCenter_y + 50) {
				liveFlag[i] = false;

				if(motionNumber == SHRINE2 && motionStyle == 2)
					liveFlag[i] = true;
			}

			if (!liveFlag[i]) {
				count++;
			}
		}

		if (count == motionNum) {
			allLiveFlag = false;

			if(motionNumber == SHRINE2 && motionStyle == 2)
				allLiveFlag = true;
		}

		if (char1.selectChar == 1) {
			if (motionNumber == SWORD1) {
				if (!char1.aMotionFlag) {
					allLiveFlag = false;
					for (int i = 0; i < motionNum; i++)
						liveFlag[i] = false;
				}
			} else if (motionNumber == SWORD2) {
				if (motionStyle == 2) {
					if (!char1.aMotionFlag) {
						allLiveFlag = false;
						for (int i = 0; i < motionNum; i++)
							liveFlag[i] = false;
					}
				}
			}

		} else if (char1.selectChar == 2) {
			if (motionNumber == MAID1) {
				if (motionStyle == 1) {
					if (!char1.aMotionFlag) {
						allLiveFlag = false;
						for (int i = 0; i < motionNum; i++)
							liveFlag[i] = false;
					}
				}

			} else if (motionNumber == MAID2) {
				if (motionStyle == 1) {
					if (!char1.aMotionFlag) {
						allLiveFlag = false;
						char1.tDashFlag = false;
						for (int i = 0; i < motionNum; i++)
							liveFlag[i] = false;
					}
				}
			}

		} else if (char1.selectChar == 4) {
			if (motionNumber == SHRINE2) {
				if (motionStyle == 2) {
					if (!char1.aMotionFlag) {
						allLiveFlag = false;
						for (int i = 0; i < motionNum; i++) {
							liveFlag[i] = false;
						}
					}
				}
			}
		}

	}

	// 攻撃のあたり判定
	public void hitJudge(boolean damageFlag, Charactor charactor,
			ParticleSystem3D ps) {
		double crossProduct;
		float[] x = new float[4];
		float[] y = new float[4];

		if (drawFigure == RECT) {
			for (int i = 0; i < motionNum; i++) {
				int count = 0;

				if (liveFlag[i] && !charactor.nockBackFlag
						&& !charactor.dethFlag && !charactor.sNockBackFlag) {
					if (motionNumber == SHRINE2 && motionStyle == 2) {
						if (animeState == 0)
							if (i == 0)
								break;

						if (charactor.charRect[0][0] >= attackDetails[i][0][0]
								&& charactor.charRect[0][0] <= attackDetails[i][0][2]) {
							if (charactor.charRect[1][0] >= attackDetails[i][1][1]
									&& charactor.charRect[1][0] <= attackDetails[i][1][0]) {
								count = 1;
							}
						}

						if (charactor.charRect[0][1] >= attackDetails[i][0][0]
								&& charactor.charRect[0][1] <= attackDetails[i][0][2]) {
							if (charactor.charRect[1][0] >= attackDetails[i][1][1]
									&& charactor.charRect[1][0] <= attackDetails[i][1][0]) {
								count = 1;
							}
						}

						if (charactor.charRect[0][0] >= attackDetails[i][0][0]
								&& charactor.charRect[0][0] <= attackDetails[i][0][2]) {
							if (charactor.charRect[1][1] >= attackDetails[i][1][1]
									&& charactor.charRect[1][1] <= attackDetails[i][1][0]) {
								count = 1;
							}
						}

						if (charactor.charRect[0][1] >= attackDetails[i][0][0]
								&& charactor.charRect[0][1] <= attackDetails[i][0][2]) {
							if (charactor.charRect[1][1] >= attackDetails[i][1][1]
									&& charactor.charRect[1][1] <= attackDetails[i][1][0]) {
								count = 1;
							}
						}

						if (count == 1) {
							if (damageFlag) {
								if (i == 0)
									charactor.hp -= 2;
								if (i == 1)
									charactor.hp -= 4;

								charactor.nx -= 20 * cos[i];
								charactor.ny -= 20 * sin[i];
							}

							charactor.state = 4;
							charactor.animeState = 0;
							charactor.aFrameCount = 0;
							charactor.aMotionFlag = false;

							charactor.nockBackFlag = true;

							count = 0;
						}

					} else {
						for (int j = 0; j < 5; j++) {
							if (!liveFlag[i] || charactor.nockBackFlag
									|| charactor.dethFlag
									|| charactor.sNockBackFlag)
								break;

							if (j == 4) {
								crossProduct = (charactor.charRect[0][0] - charactor.charRect[0][0])
										* (attackPosition[i][1] - charactor.charRect[1][0])
										- (attackPosition[i][0] - charactor.charRect[0][0])
										* (charactor.charRect[1][1] - charactor.charRect[1][0]);

								if (crossProduct > 0)
									count++;

								crossProduct = (charactor.charRect[0][1] - charactor.charRect[0][0])
										* (attackPosition[i][1] - charactor.charRect[1][1])
										- (attackPosition[i][0] - charactor.charRect[0][0])
										* (charactor.charRect[1][1] - charactor.charRect[1][1]);

								if (crossProduct > 0)
									count++;

								crossProduct = (charactor.charRect[0][1] - charactor.charRect[0][1])
										* (attackPosition[i][1] - charactor.charRect[1][1])
										- (attackPosition[i][0] - charactor.charRect[0][1])
										* (charactor.charRect[1][0] - charactor.charRect[1][1]);

								if (crossProduct > 0)
									count++;

								crossProduct = (charactor.charRect[0][0] - charactor.charRect[0][1])
										* (attackPosition[i][1] - charactor.charRect[1][0])
										- (attackPosition[i][0] - charactor.charRect[0][1])
										* (charactor.charRect[1][0] - charactor.charRect[1][0]);

								if (crossProduct > 0)
									count++;

							} else {
								crossProduct = (charactor.charRect[0][0] - charactor.charRect[0][0])
										* (attackDetails[i][1][j] - charactor.charRect[1][0])
										- (attackDetails[i][0][j] - charactor.charRect[0][0])
										* (charactor.charRect[1][1] - charactor.charRect[1][0]);

								if (crossProduct > 0)
									count++;

								crossProduct = (charactor.charRect[0][1] - charactor.charRect[0][0])
										* (attackDetails[i][1][j] - charactor.charRect[1][1])
										- (attackDetails[i][0][j] - charactor.charRect[0][0])
										* (charactor.charRect[1][1] - charactor.charRect[1][1]);

								if (crossProduct > 0)
									count++;

								crossProduct = (charactor.charRect[0][1] - charactor.charRect[0][1])
										* (attackDetails[i][1][j] - charactor.charRect[1][1])
										- (attackDetails[i][0][j] - charactor.charRect[0][1])
										* (charactor.charRect[1][0] - charactor.charRect[1][1]);

								if (crossProduct > 0)
									count++;

								crossProduct = (charactor.charRect[0][0] - charactor.charRect[0][1])
										* (attackDetails[i][1][j] - charactor.charRect[1][0])
										- (attackDetails[i][0][j] - charactor.charRect[0][1])
										* (charactor.charRect[1][0] - charactor.charRect[1][0]);

								if (crossProduct > 0)
									count++;
							}

							if (count == 4) {
								if (damageFlag) {
									switch (motionNumber) {
									case SWORD1:
										if (motionStyle == 1) {
											charactor.hp -= 1;

										} else if (motionStyle == 2) {
											charactor.hp -= 3;

										}
										break;
									case SWORD2:
										if (motionStyle == 1) {
											charactor.hp -= 1;

											charactor.nx -= 10 * cos[i];
											charactor.ny -= 10 * sin[i];

										} else if (motionStyle == 2) {
											charactor.hp -= 3;
										}
										break;

									case MAID1:
										if (motionStyle == 1) {
											charactor.hp -= 3;

										} else if (motionStyle == 2) {
											charactor.hp -= 2;

										}
										break;

									case MAID2:
										if (motionStyle == 1) {
											charactor.hp -= 2;

										} else if (motionStyle == 2) {
											charactor.hp -= 1;

										}
										break;

									case MAGIC1:
										if(motionStyle == 1){
											if (chargeFlag)
												charactor.hp -= 3;
											else
												charactor.hp--;
										}else if(motionStyle == 2){
											charactor.hp -= 1;
										}

										charactor.nx -= 10 * cos[i];
										charactor.ny -= 10 * sin[i];
										break;

									case SHRINE1:
										if (motionStyle == 1) {
											charactor.hp -= 2;

										} else if (motionStyle == 2) {
											charactor.hp -= 1;

											charactor.nx -= 10 * cos[i];
											charactor.ny -= 10 * sin[i];
										}
										break;

									case SHRINE2:
										charactor.hp -= 2;

										charactor.nx -= 20 * cos[i];
										charactor.ny -= 20 * sin[i];
										break;
									}

								}

								charactor.state = 4;
								charactor.animeState = 0;
								charactor.aFrameCount = 0;

								switch (motionNumber) {
								case SWORD1:
									charactor.sNockBackFlag = true;
									break;

								case SWORD2:
									if (motionStyle == 1) {
										liveFlag[i] = false;
										charactor.lockFlag = true;
										charactor.lockCount = 0;
										charactor.nockBackFlag = true;
									}else if(motionStyle == 2)
										charactor.sNockBackFlag = true;
									break;

								case MAID1:
									if (motionStyle == 2)
										liveFlag[i] = false;
									charactor.nockBackFlag = true;
									break;

								case MAID2:
									if(motionStyle == 1){
										charactor.sNockBackFlag = true;

									}else if (motionStyle == 2){
										liveFlag[i] = false;
										charactor.nockBackFlag = true;
									}
									break;

								case MAGIC1:
									liveFlag[i] = false;
									charactor.nockBackFlag = true;
									break;

								case SHRINE1:
									liveFlag[i] = false;
									charactor.nockBackFlag = true;
									break;

								case SHRINE2:
									if (motionStyle == 1)
										liveFlag[i] = false;

									charactor.nockBackFlag = true;
									break;
								}

							}

							count = 0;
						}
					}
				}
			}
		} else if (drawFigure == CIRCLE) {
			for (int i = 0; i < motionNum; i++) {
				int count = 0;

				if (liveFlag[i] && !charactor.nockBackFlag
						&& !charactor.dethFlag) {

					x[0] = attackPosition[i][0] - 10;
					x[1] = attackPosition[i][0] - 10;
					x[2] = attackPosition[i][0] + 10;
					x[3] = attackPosition[i][0] + 10;

					y[0] = attackPosition[i][1] + 10;
					y[1] = attackPosition[i][1] - 10;
					y[2] = attackPosition[i][1] - 10;
					y[3] = attackPosition[i][1] + 10;

					for (int j = 0; j < 5; j++) {
						if (j == 4) {
							crossProduct = (charactor.charRect[0][0] - charactor.charRect[0][0])
									* (attackPosition[i][1] - charactor.charRect[1][0])
									- (attackPosition[i][0] - charactor.charRect[0][0])
									* (charactor.charRect[1][1] - charactor.charRect[1][0]);

							if (crossProduct > 0)
								count++;

							crossProduct = (charactor.charRect[0][1] - charactor.charRect[0][0])
									* (attackPosition[i][1] - charactor.charRect[1][1])
									- (attackPosition[i][0] - charactor.charRect[0][0])
									* (charactor.charRect[1][1] - charactor.charRect[1][1]);

							if (crossProduct > 0)
								count++;

							crossProduct = (charactor.charRect[0][1] - charactor.charRect[0][1])
									* (attackPosition[i][1] - charactor.charRect[1][1])
									- (attackPosition[i][0] - charactor.charRect[0][1])
									* (charactor.charRect[1][0] - charactor.charRect[1][1]);

							if (crossProduct > 0)
								count++;

							crossProduct = (charactor.charRect[0][0] - charactor.charRect[0][1])
									* (attackPosition[i][1] - charactor.charRect[1][0])
									- (attackPosition[i][0] - charactor.charRect[0][1])
									* (charactor.charRect[1][0] - charactor.charRect[1][0]);

							if (crossProduct > 0)
								count++;

						} else {
							crossProduct = (charactor.charRect[0][0] - charactor.charRect[0][0])
									* (y[j] - charactor.charRect[1][0])
									- (x[j] - charactor.charRect[0][0])
									* (charactor.charRect[1][1] - charactor.charRect[1][0]);

							if (crossProduct > 0)
								count++;

							crossProduct = (charactor.charRect[0][1] - charactor.charRect[0][0])
									* (y[j] - charactor.charRect[1][1])
									- (x[j] - charactor.charRect[0][0])
									* (charactor.charRect[1][1] - charactor.charRect[1][1]);

							if (crossProduct > 0)
								count++;

							crossProduct = (charactor.charRect[0][1] - charactor.charRect[0][1])
									* (y[j] - charactor.charRect[1][1])
									- (x[j] - charactor.charRect[0][1])
									* (charactor.charRect[1][0] - charactor.charRect[1][1]);

							if (crossProduct > 0)
								count++;

							crossProduct = (charactor.charRect[0][0] - charactor.charRect[0][1])
									* (y[j] - charactor.charRect[1][0])
									- (x[j] - charactor.charRect[0][1])
									* (charactor.charRect[1][0] - charactor.charRect[1][0]);

							if (crossProduct > 0)
								count++;
						}

						if (count == 4) {
							if (damageFlag) {
								charactor.hp -= 2;
								charactor.nx += 10 * cos[i];
								charactor.ny += 10 * sin[i];
							}

							charactor.nockBackFlag = true;
							charactor.state = 4;
							charactor.animeState = 0;
							charactor.aFrameCount = 0;
							charactor.aMotionFlag = false;
							liveFlag[i] = false;
						}

						count = 0;
					}
				}
			}
		}
	}
}
