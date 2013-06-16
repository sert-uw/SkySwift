package com.example.softwaregamecliantandroid;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class MyGLSurfaceView extends GLSurfaceView{

	public GameThread game;

	public MyGLSurfaceView(Context context) {
		super(context);
	}

	public void setGameThread(GameThread game){
		this.game = game;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event){
		if(game != null){
			if(game.init == GameThread.GAME){
				game.touchEvent(event);
			}
		}
		return true;
	}
}
