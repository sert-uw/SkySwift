package com.example.softwaregamecliantandroid;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

//パーティクル全体の制御
public class ParticleSystem3D {
	//x, y, z軸を示す定数
	public static final int XAXIS = 0;
	public static final int YAXIS = 1;
	public static final int ZAXIS = 2;

	public GL10 gl;

	public GLRenderer renderer;

	//円周率
	public static final float PI = 3.14159265358979f;

	//重力加速度
	public static final float G = -0.04f;

	//重力を適用するかどうか
	boolean gravityFlag = true;

	//重力の方向(x, y, z軸のいずれか)
	int vector;

	//パーティクルシステムの中心点
	float x, y, z;

	//スケール変数
	float sx=1.0f, sy=1.0f, sz=1.0f;

	//パーティクルシステム全体の色
	float r=1.0f, g=1.0f, b=1.0f, alpha=1.0f;

	//パーティクル配列
	Particle3D[] p;

	//パーティクルの個数
	int cap;

	//パーティクルを個別で生成する個数
	int num;

	//パーティクルシステムの持続フレーム
	float span;

	//乱数生成オブジェクト
	Random rnd = new Random();

	//パーティクルシステムの状態
	boolean liveFlag;

	//パーティクルシステムが起動してからの時間
	float frame=0;

	//コンストラクタ
	public ParticleSystem3D(GL10 gl, GLRenderer renderer, int vector){
		this.vector = vector;
		this.gl = gl;
		this.renderer = renderer;
	}

	public ParticleSystem3D(GL10 gl, GLRenderer renderer){
		this.gl = gl;
		this.renderer = renderer;
	}

	//色のセット
	public void setColor(float r, float g, float b, float alpha){
		this.r = r; this.g = g; this.b = b; this.alpha = alpha;
	}

	public void setScale(float sx, float sy, float sz){
		this.sx = sx; this.sy = sy; this.sz = sz;
	}

	//爆発エフェクト
	public void explosion(int num, float span,
			float x, float y, float z,
			float first, float slow){
		this.x = x; this.y = y; this.z = z;
		this.span = span;
		liveFlag = true;
		frame = 0;

		float m = 1;
		float v0;
		float angle1 = 0.0f;
		float angle2 = 0.0f;
		cap = num;
		p = new Particle3D[cap];

		for(int i=0; i<num; i++){
			angle1 = -90 + rnd.nextInt(180);
			angle2 = rnd.nextInt(180);
			v0 = (rnd.nextFloat() % (first-slow) + slow) * 10;

			p[i] = new Particle3D(m, v0, 0,
					x, y, z, alpha, r, g, b,
					angle1/180f*PI, angle2/180f*PI);
			p[i].ingredient();
		}
	}

	//衝突、分解エフェクト
	public void collision(int num, float span,
			float x, float y, float z,
			float theta, float spread,
			float first, float slow){
		this.x = x; this.y = y; this.z = z;
		this.span = span;
		liveFlag = true;
		frame = 0;

		float m = 1;
		float v0;
		float angle = 0;
		float angle2 = 0.0f;


			cap = num;
			p = new Particle3D[cap];

			for(int i=0; i<num; i++){
				angle = theta - spread + rnd.nextInt((int)(spread*2));
				angle2 = rnd.nextInt((int)(spread*2));
				v0 = (rnd.nextFloat() % (first-slow) + slow) * 10;

				p[i] = new Particle3D(m, v0, 0,
						x, y, z, alpha, r, g, b,
						angle/180f*PI, angle2/180f*PI);
				p[i].ingredient();
			}
	}

	//反対方向に２つ衝突エフェクト
	public void collision2(boolean gravity, int num, float span,
			float x, float y, float z,
			float theta, float spread,
			float first, float slow){
		this.x = x; this.y = y; this.z = z;
		this.span = span;
		liveFlag = true;
		frame = 0;
		gravityFlag = gravity;

		float m = 1;
		float v0;
		float angle = 0;
		float angle2 = 0.0f;

		cap = num;
		p = new Particle3D[cap];

		for(int i=0; i<num; i++){
			if(rnd.nextInt(2) == 0)
				angle = theta - spread + rnd.nextInt((int)(spread*2));
			else
				angle = (theta+180) - spread + rnd.nextInt((int)(spread*2));
			angle2 = rnd.nextInt((int)(spread*2));
			v0 = (rnd.nextFloat() % (first-slow) + slow) * 10;

			p[i] = new Particle3D(m, v0, 0,
					x, y, z, alpha, r, g, b,
					angle/180f*PI, angle2/180f*PI);
			p[i].ingredient();
		}
	}

	//無重力
	public void floating(int num, float span,
			float x, float y, float z,
			float first, float slow){
		gravityFlag = false;

		explosion(num, span, x, y, z, first, slow);
	}

	//ジェットエフェクト
	public void jet(int num, float allSpan, float span){
		this.num = num;
		this.span = allSpan;
		liveFlag = true;

		cap = (int)(num * span);
		p = new Particle3D[cap];

		p[0] = new Particle3D();
		p[0].span = span;
	}

	//パーティクルシステムの更新
	//一度にすべてのパーティクルを生成している場合
	public void upDate(){
		frame += 1;

		for(int i=0; i<cap; i++){
			p[i].f += 1;

			p[i].x = x + linearMotion(p[i].vx, p[i].f);

			if(gravityFlag)
				p[i].y = y + acceMotion(p[i].vy, G, p[i].f);
			else
				p[i].y = y + linearMotion(p[i].vy, p[i].f);
			//p[i].z = z + linearMotion(p[i].vz, p[i].f);

			//p[i].g = p[i].f/span;
			//p[i].b = p[i].f/span;
			p[i].alpha = 1.0f - p[i].f/span;

			p[i].setScale(sx, sy, sz);
			p[i].draw(gl, renderer);
		}

		if(frame >= span)
			liveFlag = false;
	}

	public void jetUpDate(float x, float y, float z,
			float theta, float spread,
			float first, float slow){
		float m = 1;
		float v0;
		float angle = 0;
		float angle2 = 0.0f;
		int count=0;

		frame += 1;

		for(int i=0; i<cap; i++){
			if(p[i] == null && count < num && liveFlag){
				angle = theta - spread + rnd.nextInt((int)(spread*2));
				angle2 = rnd.nextInt((int)(spread*2));
				v0 = (rnd.nextFloat() % (first-slow) + slow) * 10;

				p[i] = new Particle3D(m, v0, 0,
						x, y, z, alpha, r, g, b,
						angle/180f*PI, angle2/180f*PI);
				p[i].liveFlag = true;
				p[i].ingredient();

				count++;

			}else if(p[i] != null){
				if(p[i].liveFlag){
					p[i].f += 1;

					if(p[i].f >= p[0].span){
						p[i].liveFlag = false;
						continue;
					}

					p[i].x = x + linearMotion(p[i].vx, p[i].f);
					p[i].y = y + acceMotion(p[i].vy, G, p[i].f);
					//p[i].z = z + linearMotion(p[i].vz, p[i].f);

					//p[i].g = p[i].f/p[0].span;
					//p[i].b = p[i].f/p[0].span;
					p[i].alpha = 1.0f - p[i].f/p[0].span;

					p[i].setScale(sx, sy, sz);
					p[i].draw(gl, renderer);

				}else if(!p[i].liveFlag && count < num  && liveFlag){
					angle = theta - spread + rnd.nextInt((int)(spread*2));
					angle2 = rnd.nextInt((int)(spread*2));
					v0 = (rnd.nextFloat() % (first-slow) + slow) * 10;

					p[i].setSpeed(v0);
					p[i].setLocation(x, y, z);
					p[i].setColor(r, g, b, alpha);
					p[i].setRad(angle/180f*PI, angle2/180f*PI);
					p[i].f = 0;
					p[i].liveFlag = true;
					p[i].ingredient();

					count++;
				}
			}

			if(frame >= span)
				liveFlag = false;
		}
	}

	//速度計算
	public float speed(float v0, float a, float f){
		float v;

		v = v0 + a*f;

		return v;
	}

	//等加速度運動
	public float acceMotion(float v0, float a, float f){
		float h;

		h = v0*f + a*f*f/2;

		return h;
	}

	//等速直線運動
	public float linearMotion(float v0, float f){
		float l;

		l = v0*f;

		return l;
	}
}
