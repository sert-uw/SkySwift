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
public class RoomActivity extends Activity implements View.OnClickListener {
	private final static int WC = LinearLayout.LayoutParams.WRAP_CONTENT;
	private final static int MP = LinearLayout.LayoutParams.MATCH_PARENT;

	private final static int WC2 = TableLayout.LayoutParams.WRAP_CONTENT;
	private final static int MP2 = TableLayout.LayoutParams.MATCH_PARENT;

	public final static int ROOM_NUM = 30;

	public TextView missText;
	public static LinearLayout layout, layout2, makeRoomLayout;

	public TextView[] nameView = new TextView[ROOM_NUM];
	public TextView[] passView = new TextView[ROOM_NUM];
	public TextView[] comView = new TextView[ROOM_NUM];

	public EditText inputName, inputCom, inputPass, passEdit;
	RadioGroup radioGroup;

	private TCPConnect tcp;

	private String userName;
	private int selectChar = 0;

	public int selectRoom = 0;
	public String selectRoomName = "";

	// 自身のオブジェクトを保存
	public static RoomActivity activity;

	public boolean changeFlag;

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
			selectChar = extras.getInt("select");
		}

		tcp = TCPConnect.tcp;
		tcp.addRoomActivity(this);

		System.out.println(userName + " " + selectChar);

		// レイアウトの生成
		layout = new LinearLayout(getApplicationContext());

		LinearLayout subLayout = new LinearLayout(getApplicationContext());
		ScrollView scroll = new ScrollView(getApplicationContext());
		TableLayout table = new TableLayout(getApplicationContext());

		layout.setBackgroundResource(R.drawable.char_back0);
		scroll.setBackgroundColor(Color.argb(120, 0, 0, 0));
		table.setBackgroundColor(Color.argb(0, 0, 0, 0));

		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setGravity(Gravity.CENTER_HORIZONTAL);
		subLayout.setGravity(Gravity.RIGHT);

		setContentView(layout);

		TextView[] roomNumberView = new TextView[ROOM_NUM];

		// テキストビューの生成
		TextView titleView = new TextView(getApplicationContext());
		titleView.setText("ルーム一覧");
		titleView.setTextSize(26);
		titleView.setTextColor(Color.rgb(180, 0, 255));
		titleView.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
		layout.addView(titleView);

		// ボタンの生成
		Button button1 = new Button(getApplicationContext());
		button1.setTag("0");
		button1.setOnClickListener(this);
		button1.setTextColor(Color.rgb(180, 0, 255));
		button1.setText("ルーム作成");
		button1.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
		subLayout.addView(button1);

		for (int i = 0; i < ROOM_NUM+1; i++) {
			// 行の生成
			TableRow row = new TableRow(getApplicationContext());
			row.setLayoutParams(new TableLayout.LayoutParams(MP2, WC2));
			row.setGravity(Gravity.CENTER);// 中央寄せ
			table.addView(row);
			if (i == 0) {

				TextView numberTitleView = new TextView(getApplicationContext());
				numberTitleView.setText("NO");
				numberTitleView.setTextSize(24);
				numberTitleView.setTextColor(Color.rgb(180, 0, 255));
				numberTitleView.setBackgroundResource(R.drawable.border);
				row.addView(numberTitleView);

				TextView nameTitleView = new TextView(getApplicationContext());
				nameTitleView.setText("ルームネーム　");
				nameTitleView.setTextSize(24);
				nameTitleView.setTextColor(Color.rgb(180, 0, 255));
				nameTitleView.setBackgroundResource(R.drawable.border);
				row.addView(nameTitleView);

				TextView passTitleView = new TextView(getApplicationContext());
				passTitleView.setText("パス");
				passTitleView.setTextSize(24);
				passTitleView.setTextColor(Color.rgb(180, 0, 255));
				passTitleView.setBackgroundResource(R.drawable.border);
				row.addView(passTitleView);

				TextView comTitleView = new TextView(getApplicationContext());
				comTitleView.setText("コメント　　　　　　　");
				comTitleView.setTextSize(24);
				comTitleView.setTextColor(Color.rgb(180, 0, 255));
				comTitleView.setBackgroundResource(R.drawable.border);
				row.addView(comTitleView);

			} else {

				roomNumberView[i - 1] = new TextView(getApplicationContext());
				roomNumberView[i - 1].setText(String.valueOf(i));
				roomNumberView[i - 1].setTextSize(20);
				roomNumberView[i - 1].setTextColor(Color.rgb(0, 255, 0));
				roomNumberView[i - 1].setBackgroundResource(R.drawable.border);
				row.addView(roomNumberView[i - 1]);

				nameView[i - 1] = new TextView(getApplicationContext());
				nameView[i - 1].setText("");
				nameView[i - 1].setTextSize(20);
				nameView[i - 1].setTextColor(Color.rgb(0, 255, 0));
				nameView[i - 1].setBackgroundResource(R.drawable.border);
				nameView[i - 1].setOnClickListener(this);
				nameView[i - 1].setTag(String.valueOf(i + 10));
				row.addView(nameView[i - 1]);

				passView[i - 1] = new TextView(getApplicationContext());
				passView[i - 1].setText("");
				passView[i - 1].setTextSize(20);
				passView[i - 1].setTextColor(Color.rgb(0, 255, 0));
				passView[i - 1].setBackgroundResource(R.drawable.border);
				row.addView(passView[i - 1]);

				comView[i - 1] = new TextView(getApplicationContext());
				comView[i - 1].setText("");
				comView[i - 1].setTextSize(20);
				comView[i - 1].setTextColor(Color.rgb(0, 255, 0));
				comView[i - 1].setBackgroundResource(R.drawable.border);
				row.addView(comView[i - 1]);
			}
		}

		layout.addView(subLayout, new LinearLayout.LayoutParams(MP, WC));

		Space sp3 = new Space(getApplicationContext());
		sp3.setLayoutParams(new LinearLayout.LayoutParams(20, 50));
		layout.addView(sp3);

		scroll.addView(table);

		layout.addView(scroll, new LinearLayout.LayoutParams(WC, WC));

		// レイアウトの生成
		makeRoomLayout = new LinearLayout(getApplicationContext());
		LinearLayout subLayout1 = new LinearLayout(getApplicationContext());
		LinearLayout subLayout2 = new LinearLayout(getApplicationContext());
		LinearLayout subLayout3 = new LinearLayout(getApplicationContext());
		LinearLayout subLayout4 = new LinearLayout(getApplicationContext());
		LinearLayout subLayout5 = new LinearLayout(getApplicationContext());

		makeRoomLayout.setOrientation(LinearLayout.VERTICAL);
		subLayout1.setOrientation(LinearLayout.HORIZONTAL);
		subLayout2.setOrientation(LinearLayout.HORIZONTAL);
		subLayout3.setOrientation(LinearLayout.HORIZONTAL);
		subLayout4.setOrientation(LinearLayout.HORIZONTAL);
		subLayout5.setOrientation(LinearLayout.HORIZONTAL);

		makeRoomLayout.setBackgroundResource(R.drawable.char_back0);

		makeRoomLayout.setGravity(android.view.Gravity.CENTER);

		subLayout1.setBackgroundColor(Color.argb(120, 0, 0, 0));
		subLayout2.setBackgroundColor(Color.argb(120, 0, 0, 0));
		subLayout3.setBackgroundColor(Color.argb(120, 0, 0, 0));
		subLayout4.setBackgroundColor(Color.argb(120, 0, 0, 0));
		subLayout5.setBackgroundColor(Color.argb(0, 0, 0, 0));

		// テキストビューの生成
		TextView textView = new TextView(getApplicationContext());
		textView.setText("新規ルーム作成");
		textView.setTextSize(28);
		textView.setTextColor(Color.rgb(180, 0, 255));
		textView.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
		subLayout1.addView(textView);

		// テキストビューの生成
		TextView roomName = new TextView(getApplicationContext());
		roomName.setText("ルーム名:");
		roomName.setTextSize(20);
		roomName.setTextColor(Color.rgb(180, 0, 255));
		roomName.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
		subLayout2.addView(roomName);

		// エディットテキストの生成
		inputName = new EditText(getApplicationContext());
		inputName.setText("roomName");
		inputName.setTextColor(Color.rgb(0, 255, 0));
		inputName.setBackgroundColor(Color.BLACK);
		inputName.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
		subLayout2.addView(inputName);

		// テキストビューの生成
		TextView comView = new TextView(getApplicationContext());
		comView.setText("コメント:");
		comView.setTextSize(20);
		comView.setTextColor(Color.rgb(180, 0, 255));
		comView.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
		subLayout3.addView(comView);

		// エディットテキストの生成
		inputCom = new EditText(getApplicationContext());
		inputCom.setText("comment");
		inputCom.setTextColor(Color.rgb(0, 255, 0));
		inputCom.setBackgroundColor(Color.BLACK);
		inputCom.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
		subLayout3.addView(inputCom);

		RadioButton radio1 = new RadioButton(getApplicationContext());
		radio1.setId(0);
		radio1.setText("パス有り　　");
		radio1.setTextSize(20);
		radio1.setTextColor(Color.rgb(180, 0, 255));

		RadioButton radio2 = new RadioButton(getApplicationContext());
		radio2.setId(1);
		radio2.setText("パス無し");
		radio2.setTextSize(20);
		radio2.setTextColor(Color.rgb(180, 0, 255));

		radioGroup = new RadioGroup(getApplicationContext());
		radioGroup.addView(radio1);
		radioGroup.addView(radio2);
		radioGroup.check(0);
		radioGroup.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
		subLayout4.addView(radioGroup);

		// テキストビューの生成
		TextView passView = new TextView(getApplicationContext());
		passView.setText("パスワード:");
		passView.setTextSize(20);
		passView.setTextColor(Color.rgb(180, 0, 255));
		passView.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
		subLayout4.addView(passView);

		// エディットテキストの生成
		inputPass = new EditText(getApplicationContext());
		inputPass.setText("write pass");
		inputPass.setTextColor(Color.rgb(0, 255, 0));
		inputPass.setBackgroundColor(Color.BLACK);
		inputPass.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
		subLayout4.addView(inputPass);

		// ボタンの生成
		Button button2 = new Button(getApplicationContext());
		button2.setTag("1");
		button2.setOnClickListener(this);
		button2.setTextColor(Color.rgb(180, 0, 255));
		button2.setText("ルーム作成");
		button2.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
		subLayout5.addView(button2);

		// ボタンの生成
		Button button3 = new Button(getApplicationContext());
		button3.setTag("2");
		button3.setOnClickListener(this);
		button3.setTextColor(Color.rgb(180, 0, 255));
		button3.setText("キャンセル");
		button3.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
		subLayout5.addView(button3);

		makeRoomLayout.addView(subLayout1,
				new LinearLayout.LayoutParams(WC, WC));

		Space sp4 = new Space(getApplicationContext());
		sp4.setLayoutParams(new LinearLayout.LayoutParams(20, 30));
		makeRoomLayout.addView(sp4);

		makeRoomLayout.addView(subLayout2,
				new LinearLayout.LayoutParams(WC, WC));

		makeRoomLayout.addView(subLayout3,
				new LinearLayout.LayoutParams(WC, WC));

		Space sp6 = new Space(getApplicationContext());
		sp6.setLayoutParams(new LinearLayout.LayoutParams(50, 30));
		makeRoomLayout.addView(sp6);

		makeRoomLayout.addView(subLayout4,
				new LinearLayout.LayoutParams(WC, WC));

		Space sp7 = new Space(getApplicationContext());
		sp7.setLayoutParams(new LinearLayout.LayoutParams(50, 30));
		makeRoomLayout.addView(sp7);

		makeRoomLayout.addView(subLayout5,
				new LinearLayout.LayoutParams(WC, WC));

		tcp.send("CS" + selectChar);
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

	// ボタンクリック時に呼ばれる
	public void onClick(View view) {
		int tag = Integer.parseInt((String) view.getTag());

		if (tag == 0) {
			this.setContentView(makeRoomLayout);

		} else if (tag == 1) {
			if (radioGroup.getCheckedRadioButtonId() == 0) {
				tcp.send("makeRoom1" + inputName.getText().toString() + "C"
						+ inputCom.getText().toString() + "P"
						+ inputPass.getText().toString());

			} else if (radioGroup.getCheckedRadioButtonId() == 1) {
				tcp.send("makeRoom2" + inputName.getText().toString() + "C"
						+ inputCom.getText().toString());
			}

		} else if (tag == 2) {
			this.setContentView(layout);

		}else if(tag >= 11){
			selectRoom = tag - 10;
			if(nameView[tag-11].getText().equals(""))
				return;

			if(passView[tag-11].getText().toString().equals("無")){
				showDialog(this, nameView[tag-11].getText()+"に入室しますか？", "Yes/Noを選択",
						new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(which == DialogInterface.BUTTON_POSITIVE){
							selectRoomName = nameView[selectRoom - 1].getText().toString();
							activity.changeActivity();

						}else if(which == DialogInterface.BUTTON_NEGATIVE){
							return;
						}
					}
				});

			}else if(passView[tag-11].getText().toString().equals("有")){
				passEdit = new EditText(this);

				textDialog(this, nameView[tag-11].getText()+"のパスワードを入力", passEdit,
						new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(which == DialogInterface.BUTTON_POSITIVE){
							tcp.send("pass" + (selectRoom-1) + "P" + passEdit.getText().toString());

						}else if(which == DialogInterface.BUTTON_NEGATIVE){
							return;
						}
					}
				});
			}
		}
	}

	public void passMissSwho(){
		showMissDialog(this, "Infomation", "パスワードが違います。",
				new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(which == DialogInterface.BUTTON_POSITIVE){
					return;
				}
			}
		});
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

	//テキストダイアログ
	private static void textDialog(Context context, String title, EditText editText, DialogInterface.OnClickListener listener){
		AlertDialog.Builder ad = new AlertDialog.Builder(context);
		ad.setTitle(title);
		ad.setView(editText);
		ad.setPositiveButton("OK", listener);
		ad.setNegativeButton("Cancel", listener);
		ad.show();
	}

	//ダイアログ
	public static void showMissDialog(Context context, String title, String text, DialogInterface.OnClickListener listener){
		AlertDialog.Builder ad = new AlertDialog.Builder(context);
		ad.setTitle(title);
		ad.setMessage(text);
		ad.setPositiveButton("OK", listener);
		ad.show();
	}

	// アクティビティ変更メソッド
	public void changeActivity() {
		// アプリ内のアクティビティを呼び出すインテントの生成
		Intent intent = new Intent(this,
				com.example.softwaregamecliantandroid.ChatRoomActivity.class);

		changeFlag = true;

		intent.putExtra("selectRoom", selectRoom);
		intent.putExtra("roomName", selectRoomName);
		intent.putExtra("selectChar", selectChar);
		intent.putExtra("userName", userName);

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