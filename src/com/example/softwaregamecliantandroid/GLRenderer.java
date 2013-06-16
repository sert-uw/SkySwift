package com.example.softwaregamecliantandroid;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

//レンダラー
public class GLRenderer implements GLSurfaceView.Renderer {
	private FloatBuffer vertexBuffer;// 頂点バッファ
	private FloatBuffer textureBuffer;// テクスチャバッファ
	private FloatBuffer colorBuffer;//カラーバッファ

	private ByteBuffer indexBuffer; // インデックスバッファ

	double WIDTH;
	double HEIGHT;

	double aspect;

	private GLActivity activity;
	private GameThread game;
	private MyGLSurfaceView myGL;

	String myName="", otherName="";
	int myChar=0, otherChar=0, myNumber=0;

	private float[] baseColors = {
			1.0f, 1.0f, 1.0f, 1.0f,
			1.0f, 1.0f, 1.0f, 1.0f,
			1.0f, 1.0f, 1.0f, 1.0f,
			1.0f, 1.0f, 1.0f, 1.0f,
	};

	public GLRenderer(GLActivity activity, MyGLSurfaceView myGL,
			String myName, String otherName, int myChar,
			int otherChar, int myNumber){
		this.activity = activity;
		this.myGL = myGL;

		this.myName = myName;
		this.otherName = otherName;
		this.myChar = myChar;
		this.otherChar = otherChar;
		this.myNumber = myNumber;
	}

	// サーフェイス生成時に呼ばれる
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig eglConfig) {

		// 頂点配列の有効化
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

		// インデックスバッファの生成
		byte[] indexs = { 0, 1, 2, 1, 2, 3 };
		indexBuffer = makeByteBuffer(indexs);

		try{
			game = new GameThread(gl, activity, this, myName, otherName,
					myChar, otherChar, myNumber);
		}catch (Exception e){
			e.printStackTrace();
		}

		myGL.setGameThread(game);
	}

	// 画面サイズ変更時に呼ばれる
	@Override
	public void onSurfaceChanged(GL10 gl, int w, int h) {

		WIDTH = w / 2;
		HEIGHT = h / 2;

		System.out.println(w + " " + h);

		// 画面の表示領域の指定
		gl.glViewport(0, 0, w, h);

		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();

		aspect = WIDTH / HEIGHT;

		gl.glFrustumf(-(float)(150*aspect), (float)(150*aspect), -150, 150, 150, 450);

		game.setDisplaySize(w, h);
		game.setView((float)(300*aspect), 300);
	}

	// 毎フレーム描画時に呼ばれる
	@Override
	public void onDrawFrame(GL10 gl) {
		// 画面のクリア
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		try{
			game.subRenderer();
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	//ポリゴンの生成
	public void makePorigon(GL10 gl, float[] vertexs, float[] colors){
		if(colors == null)
			colors = baseColors;

		vertexBuffer = makeFloatBuffer(vertexs);
		colorBuffer = makeFloatBuffer(colors);

		gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorBuffer);

		// 頂点バッファの指定
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);

		// 四角形の描画
		gl.glDrawElements(GL10.GL_TRIANGLES, 6, GL10.GL_UNSIGNED_BYTE,
				indexBuffer);
	}

	//テクスチャーポリゴンの生成
	public void setTexture(GL10 gl, int textureId,
			float[] vertexs, float[] colors, float[] textures){

		if(colors == null)
			colors = baseColors;

		vertexBuffer = makeFloatBuffer(vertexs);
		textureBuffer = makeFloatBuffer(textures);
		colorBuffer = makeFloatBuffer(colors);

		gl.glActiveTexture(GL10.GL_TEXTURE0);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);

		gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorBuffer);

		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);

		// 頂点バッファの指定
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);

		// 四角形の描画
		gl.glDrawElements(GL10.GL_TRIANGLES, 6, GL10.GL_UNSIGNED_BYTE,
				indexBuffer);
	}

	// float配列→floatバッファ
	private FloatBuffer makeFloatBuffer(float[] array) {
		FloatBuffer fb = ByteBuffer.allocateDirect(array.length * 4)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		fb.put(array).position(0);
		return fb;
	}

	// float配列をFloatBufferに変換
	private ByteBuffer makeByteBuffer(byte[] array) {
		ByteBuffer bb = ByteBuffer.allocateDirect(array.length).order(
				ByteOrder.nativeOrder());
		bb.put(array).position(0);
		return bb;
	}

	public static final int loadTexture(GL10 gl, Bitmap bmp){
        //テクスチャメモリの確保
		int[] textureIds=new int[1];
		gl.glGenTextures(1,textureIds,0);
		//テクスチャへのビットマップ指定
		gl.glActiveTexture(GL10.GL_TEXTURE0);
		gl.glBindTexture(GL10.GL_TEXTURE_2D,textureIds[0]);
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D,0,bmp,0);
		//テクスチャフィルタの指定
		gl.glTexParameterf(GL10.GL_TEXTURE_2D,
				GL10.GL_TEXTURE_MIN_FILTER,GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D,
				GL10.GL_TEXTURE_MAG_FILTER,GL10.GL_NEAREST);
		return textureIds[0];
	}
}
