package com.example.softwaregamecliantandroid;

import javax.microedition.khronos.opengles.GL10;

//パーティクル一つの情報
public class Particle3D {
	//円周率
	public static final float PI = 3.14159265358979f;

	float[] vertexs = {
			-1.0f, 1.0f, 0.0f,
			-.10f, -1.0f, 0.0f,
			1.0f, -1.0f, 0.0f,
			1.0f, 1.0f, 0.0f};

	float[] colors = {
			1.0f, 1.0f, 1.0f, 1.0f,
			1.0f, 1.0f, 1.0f, 1.0f,
			1.0f, 1.0f, 1.0f, 1.0f,
			1.0f, 1.0f, 1.0f, 1.0f};

	//質量
	float m;

	//初速度(d/f) 単位：ドット/フレーム
	float v0;

	//速度(d/f)
	float v;

	//速度の各座標成分
	float vx, vy, vz;

	//加速度(d^2/f)
	float a;

	//時間(f) 単位：フレーム
	float f = 0;

	//水平面との角度
	double rad1;

	//水平面のどちらかの軸との角度
	double rad2;

	//座標
	float x, y, z;

	//スケール変数
	float sx=1.0f, sy=1.0f, sz=1.0f;

	//色
	float r, g, b, alpha;

	//このパーティクルが使用されているかどうか
	boolean liveFlag;

	//このパーティクルの持続時間
	float span=0f;

	//コンストラクタ
	public Particle3D(float m, float v0, float a,
			float x, float y, float z,
			 float alpha, float r, float g, float b,
			float rad1, float rad2){
		this.m = m;
		this.v0 = v0; this.a = a;
		setLocation(x, y, z);
		setColor(r, g, b, alpha);
		setRad(rad1, rad2);
	}

	public Particle3D(){}

	//パラメータの変更
	public void setSpeed(float v0){
		this.v0 = v0;
	}

	public void setLocation(float x, float y, float z){
		this.x = x; this.y = y; this.z = z;
	}

	public void setColor(float r, float g, float b, float alpha){
		this.r = r; this.g = g; this.b = b; this.alpha = alpha;

		colors[0] = r; colors[1] = g; colors[2] = b; colors[3] = alpha;
		colors[4] = r; colors[5] = g; colors[6] = b; colors[7] = alpha;
		colors[8] = r; colors[9] = g; colors[10] = b; colors[11] = alpha;
		colors[12] = r; colors[13] = g; colors[14] = b; colors[15] = alpha;
	}

	public void setRad(float rad1, float rad2){
		this.rad1 = rad1; this.rad2 = rad2;
	}

	public void setScale(float sx, float sy, float sz){
		this.sx = sx; this.sy = sy; this.sz = sz;
	}

	//速度を座標成分に分解
	public void ingredient(){
		vx = (float)(v0 * Math.cos(rad1) * Math.sin(rad2));
		vy = (float)(v0 * Math.sin(rad1));
		vx = (float)(v0 * Math.cos(rad1) * Math.cos(rad2));
	}

	public void draw(GL10 gl, GLRenderer renderer){
		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glTranslatef(x, y, -z);
		gl.glColor4f(r, g, b, alpha);
		gl.glScalef(sx, sy, sz);

		renderer.makePorigon(gl, vertexs, colors);

		gl.glPopMatrix();

		gl.glEnable(GL10.GL_TEXTURE_2D);
	}
}
