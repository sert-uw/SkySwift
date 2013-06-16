package com.example.softwaregamecliantandroid;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

public class GLActivity extends Activity {
	private MyGLSurfaceView glView;

	private GLActivity activity;

	// アクティビティ生成時に呼ばれる
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		if(TCPConnect.tcp != null){
			TCPConnect.tcp.send("exit");
			TCPConnect.tcp.disconnect();
		}

		activity = this;

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		String myName="", otherName="";
		int myChar=0, otherChar=0, myNumber=0;

		Bundle extras = getIntent().getExtras();
		if(extras != null){
			myName = extras.getString("myName");
			otherName = extras.getString("otherName");
			myChar = extras.getInt("myChar");
			otherChar = extras.getInt("otherChar");
			myNumber = extras.getInt("myNumber");
		}

		// GLサーフェイスビューの生成
		glView = new MyGLSurfaceView(this);
		glView.setRenderer(new GLRenderer(this, glView, myName, otherName,
				myChar, otherChar, myNumber));
		setContentView(glView);
	}

	// アクティビティレジューム時に呼ばれる
	@Override
	public void onResume() {
		super.onResume();
		glView.onResume();
	}

	// アクティビティポーズ時に呼ばれる
	@Override
	public void onPause() {
		super.onPause();
		glView.onPause();
	}

	//バックキーが押されたときの処理
	@Override
	public boolean dispatchKeyEvent(KeyEvent e){
		if(e.getAction() == KeyEvent.ACTION_DOWN){
			if(e.getKeyCode() == KeyEvent.KEYCODE_BACK){
				showDialog(this, "終了しますか？", "Yes/Noを選択",
						new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(which == DialogInterface.BUTTON_POSITIVE){

							if(UDPConnect.udp != null){
								UDPConnect.udp.disconnect();
							}

							glView.game.startFlag = false;

							activity.finish();

						}else if(which == DialogInterface.BUTTON_NEGATIVE){
							return;
						}
					}
				});
			}
		}

		return super.dispatchKeyEvent(e);
	}

	@Override
	public void onUserLeaveHint(){
		//ホームボタンが押された時や、他のアプリが起動した時に呼ばれる
		//戻るボタンが押された場合には呼ばれない

		if(UDPConnect.udp != null){
			UDPConnect.udp.disconnect();
		}
		activity.finish();
	}

	//ダイアログ
	private static void showDialog(Context context, String title, String text, DialogInterface.OnClickListener listener){
		AlertDialog.Builder ad = new AlertDialog.Builder(context);
		ad.setTitle(title);
		ad.setMessage(text);
		ad.setPositiveButton("Yes", listener);
		ad.setNegativeButton("No", listener);
		ad.show();
	}

	// アクティビティ変更メソッド
	public void changeActivity() {

		if(UDPConnect.udp != null){
			UDPConnect.udp.disconnect();
		}

		// アプリ内のアクティビティを呼び出すインテントの生成
		Intent intent = new Intent(this,
				com.example.softwaregamecliantandroid.StartActivity.class);

		// アクティビティの呼び出し
		this.startActivity(intent);
	}
}
