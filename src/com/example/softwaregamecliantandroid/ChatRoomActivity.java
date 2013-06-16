package com.example.softwaregamecliantandroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.*;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;

//アクティビティ間のパラメータ渡し
public class ChatRoomActivity extends Activity implements View.OnClickListener {
	public final static String BR = System.getProperty("line.separator");

	private final static int WC = LinearLayout.LayoutParams.WRAP_CONTENT;
	private final static int MP = LinearLayout.LayoutParams.MATCH_PARENT;

	private final static int WC2 = TableLayout.LayoutParams.WRAP_CONTENT;
	private final static int MP2 = TableLayout.LayoutParams.MATCH_PARENT;

	public TextView missText;
	public static LinearLayout layout, chatRoomLayout;

	public TextView[] nameView = new TextView[2];
	public TextView[] imageView = new TextView[2];

	Button button1;

	public TextView chatView;
	public EditText inputName, inputCom, inputPass, chatText;
	RadioGroup radioGroup;

	private TCPConnect tcp;

	private String userName;
	private String roomName;
	private int selectRoom = 0;
	private int selectChar = 0;

	private boolean chatFlag;

	private boolean fightFlag;

	public boolean changeFlag;

	// 自身のオブジェクトを保存
	public static ChatRoomActivity activity;

	// アクティビティ起動時に呼ばれる
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		activity = this;

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			userName = extras.getString("userName");
			roomName = extras.getString("roomName");
			selectRoom = extras.getInt("selectRoom");
			selectChar = extras.getInt("selectChar");
		}

		System.out.println(selectChar);

		tcp = TCPConnect.tcp;
		tcp.addChatRoomActivity(this);

		System.out.println(userName + " " + roomName);

		// レイアウトの生成
		layout = new LinearLayout(getApplicationContext());

		TableLayout table = new TableLayout(getApplicationContext());

		layout.setBackgroundResource(R.drawable.char_back0);
		table.setBackgroundColor(Color.argb(0, 0, 0, 0));

		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setGravity(Gravity.CENTER);
		table.setGravity(Gravity.CENTER);

		setContentView(layout);

		// テキストビューの生成
		TextView titleView = new TextView(getApplicationContext());
		titleView.setText(roomName);
		titleView.setTextSize(24);
		titleView.setTextColor(Color.rgb(180, 0, 255));
		titleView.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
		layout.addView(titleView);

		Space sp1 = new Space(getApplicationContext());
		sp1.setLayoutParams(new LinearLayout.LayoutParams(20, 30));
		layout.addView(sp1);

		// ボタンの生成
		button1 = new Button(getApplicationContext());
		button1.setTag("0");
		button1.setOnClickListener(this);
		button1.setTextColor(Color.rgb(0, 255, 0));
		button1.setText("対戦開始");
		button1.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
		layout.addView(button1);

		Space sp3 = new Space(getApplicationContext());
		sp3.setLayoutParams(new LinearLayout.LayoutParams(20, 30));
		layout.addView(sp3);

		// 行の生成
		TableRow row = new TableRow(getApplicationContext());
		row.setLayoutParams(new TableLayout.LayoutParams(MP2, WC2));
		row.setGravity(Gravity.CENTER);// 中央寄せ
		table.addView(row);

		nameView[0] = new TextView(getApplicationContext());
		nameView[0].setText("NoName");
		nameView[0].setTextSize(18);
		nameView[0].setTextColor(Color.rgb(0, 255, 0));
		nameView[0].setBackgroundColor(Color.rgb(0, 0, 0));
		row.addView(nameView[0]);

		TextView spaceView = new TextView(getApplicationContext());
		spaceView.setText("　　　　　");
		spaceView.setTextSize(18);
		row.addView(spaceView);

		nameView[1] = new TextView(getApplicationContext());
		nameView[1].setText("NoName");
		nameView[1].setTextSize(18);
		nameView[1].setTextColor(Color.rgb(0, 255, 0));
		nameView[1].setBackgroundColor(Color.rgb(0, 0, 0));
		row.addView(nameView[1]);

		// 行の生成
		TableRow row3 = new TableRow(getApplicationContext());
		row3.setLayoutParams(new TableLayout.LayoutParams(MP2, WC2));
		row3.setGravity(Gravity.CENTER);// 中央寄せ
		table.addView(row3);

		imageView[0] = new TextView(getApplicationContext());
		imageView[0].setBackgroundResource(R.drawable.no_image);
		row3.addView(imageView[0]);

		TextView vsImageView = new TextView(getApplicationContext());
		vsImageView.setBackgroundResource(R.drawable.vs);
		row3.addView(vsImageView);

		imageView[1] = new TextView(getApplicationContext());
		imageView[1].setBackgroundResource(R.drawable.no_image);
		row3.addView(imageView[1]);

		layout.addView(table, new LinearLayout.LayoutParams(WC, WC));

		Space sp4 = new Space(getApplicationContext());
		sp4.setLayoutParams(new LinearLayout.LayoutParams(20, 30));
		layout.addView(sp4);

		// ボタンの生成
		Button button2 = new Button(getApplicationContext());
		button2.setTag("1");
		button2.setOnClickListener(this);
		button2.setTextColor(Color.rgb(0, 255, 0));
		button2.setText("チャット");
		button2.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
		layout.addView(button2);

		// レイアウトの生成
		chatRoomLayout = new LinearLayout(getApplicationContext());
		LinearLayout subLayout = new LinearLayout(getApplicationContext());
		ScrollView scroll = new ScrollView(getApplicationContext());

		chatRoomLayout.setOrientation(LinearLayout.VERTICAL);
		subLayout.setOrientation(LinearLayout.HORIZONTAL);

		chatRoomLayout.setBackgroundResource(R.drawable.char_back0);
		subLayout.setBackgroundColor(Color.argb(150, 255, 255, 255));

		scroll.setBackgroundColor(Color.argb(120, 0, 0, 0));

		// ボタンの生成
		Button button3 = new Button(getApplicationContext());
		button3.setTag("2");
		button3.setOnClickListener(this);
		button3.setTextColor(Color.rgb(180, 0, 255));
		button3.setText("送信");
		button3.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
		subLayout.addView(button3);

		// エディットテキストの生成
		chatText = new EditText(getApplicationContext());
		chatText.setText("", TextView.BufferType.NORMAL);
		chatText.setTextColor(Color.rgb(0, 0, 0));
		chatText.setLayoutParams(new LinearLayout.LayoutParams(MP, WC));
		subLayout.addView(chatText);

		// テキストビューの生成
		chatView = new TextView(getApplicationContext());
		chatView.setText("");
		chatView.setTextSize(16);
		chatView.setTextColor(Color.rgb(0, 255, 0));
		chatView.setLayoutParams(new LinearLayout.LayoutParams(MP, WC));
		scroll.addView(chatView);

		chatRoomLayout
				.addView(subLayout, new LinearLayout.LayoutParams(MP, WC));

		chatRoomLayout.addView(scroll, new LinearLayout.LayoutParams(MP, MP));

		tcp.send("RS" + selectRoom);
		tcp.send("chatSys");

	}

	public void addUserData(int playerNumber, String name, int selectChar) {

		nameView[playerNumber].setText(name);

		switch (selectChar) {
		case 0:
			imageView[playerNumber].setBackgroundResource(R.drawable.no_image);
			break;
		case 1:
			imageView[playerNumber].setBackgroundResource(R.drawable.char1);
			break;
		case 2:
			imageView[playerNumber].setBackgroundResource(R.drawable.char2);
			break;
		case 3:
			imageView[playerNumber].setBackgroundResource(R.drawable.char3);
			break;
		case 4:
			imageView[playerNumber].setBackgroundResource(R.drawable.char4);
			break;
		}
	}

	//バックキーが押されたときの処理
	@Override
	public boolean dispatchKeyEvent(KeyEvent e){
		if(e.getAction() == KeyEvent.ACTION_DOWN){
			if(e.getKeyCode() == KeyEvent.KEYCODE_BACK){

				if(chatFlag){
					this.setContentView(layout);
					chatFlag = false;
					return true;
				}

				showDialog(this, "終了しますか？", "Yes/Noを選択",
						new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(which == DialogInterface.BUTTON_POSITIVE){
							if(TCPConnect.tcp != null){
								TCPConnect.tcp.send("exit");
								TCPConnect.tcp.disconnect();
							}
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

		if(changeFlag)
			return;

		if(TCPConnect.tcp != null){
			TCPConnect.tcp.send("exit");
			TCPConnect.tcp.disconnect();
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

	// ボタンクリック時に呼ばれる
	public void onClick(View view) {
		int tag = Integer.parseInt((String) view.getTag());

		if (tag == 0) {
			if(!fightFlag){
				tcp.send("fight");
				fightFlag = true;
				button1.setText("準備完了");
			}else{
				tcp.send("canselFight");
				fightFlag = false;
				button1.setText("対戦開始");
			}

		} else if (tag == 1) {
			this.setContentView(chatRoomLayout);
			chatFlag = true;

		} else if (tag == 2) {
			tcp.send("chat" + chatText.getText().toString());
			chatText.setText("");

		} else if (tag == 3) {

		}
	}

	// アクティビティ変更メソッド
	public void changeActivity(String otherName, int otherChar,
			int myNumber) {
		// アプリ内のアクティビティを呼び出すインテントの生成
		Intent intent = new Intent(this,
				com.example.softwaregamecliantandroid.GLActivity.class);

		changeFlag = true;

		intent.putExtra("myName", userName);
		intent.putExtra("otherName", otherName);
		intent.putExtra("myChar", selectChar);
		intent.putExtra("otherChar", otherChar);
		intent.putExtra("myNumber", myNumber);

		// アクティビティの呼び出し
		this.startActivity(intent);
		finish();
	}

	// アクティビティ停止時に呼ばれる
	@Override
	public void onStop() {
		super.onStop();

	}
}
