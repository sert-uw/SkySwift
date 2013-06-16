package com.example.softwaregamecliantandroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

//アクティビティ間のパラメータ渡し
public class StartActivity extends Activity
    implements View.OnClickListener {
    private final static int WC = LinearLayout.LayoutParams.WRAP_CONTENT;

    public TextView missText;
    public EditText userId, userPass;
    public static LinearLayout layout, layout2, layout3;

    private String userName;
    private int selectChar = 0;

    //自身のオブジェクトを保存
    public static StartActivity activity;

    public boolean changeFlag;

    //アクティビティ起動時に呼ばれる
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        activity = this;

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //レイアウトの生成
        layout = new LinearLayout(getApplicationContext());
        LinearLayout subLayout1 = new LinearLayout(getApplicationContext());
        LinearLayout subLayout2 = new LinearLayout(getApplicationContext());
        LinearLayout subLayout3 = new LinearLayout(getApplicationContext());
        LinearLayout subLayout4 = new LinearLayout(getApplicationContext());
        LinearLayout subLayout5 = new LinearLayout(getApplicationContext());

        layout.setOrientation(LinearLayout.VERTICAL);
        subLayout1.setOrientation(LinearLayout.HORIZONTAL);
        subLayout2.setOrientation(LinearLayout.HORIZONTAL);
        subLayout3.setOrientation(LinearLayout.HORIZONTAL);
        subLayout4.setOrientation(LinearLayout.HORIZONTAL);
        subLayout5.setOrientation(LinearLayout.HORIZONTAL);

        setContentView(layout);

        layout.setBackgroundResource(R.drawable.title_back);

        subLayout1.setBackgroundColor(Color.argb(0, 0, 0, 0));
        subLayout2.setBackgroundColor(Color.argb(120, 0, 0, 0));
        subLayout3.setBackgroundColor(Color.argb(120, 0, 0, 0));
        subLayout4.setBackgroundColor(Color.argb(0, 0, 0, 0));
        subLayout5.setBackgroundColor(Color.argb(0, 0, 0, 0));

        //テキストビューの生成
        TextView titleView = new TextView(getApplicationContext());
        titleView.setText("");
        titleView.setTextSize(32);
        titleView.setTextColor(Color.rgb(180,0,255));
        titleView.setLayoutParams(new LinearLayout.LayoutParams(WC,WC));
        subLayout1.addView(titleView);

        //テキストビューの生成
        TextView idText = new TextView(getApplicationContext());
        idText.setText("User ID:");
        idText.setTextSize(24);
        idText.setTextColor(Color.rgb(180,0,255));
        idText.setLayoutParams(new LinearLayout.LayoutParams(WC,WC));
        subLayout2.addView(idText);

        //エディットテキストの生成
        userId = new EditText(getApplicationContext());
        userId.setText("yourID");
        userId.setTextColor(Color.rgb(180,0,255));
        userId.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
        subLayout2.addView(userId);

        //テキストビューの生成
        TextView passText = new TextView(getApplicationContext());
        passText.setText("password:");
        passText.setTextSize(24);
        passText.setTextColor(Color.rgb(180,0,255));
        passText.setLayoutParams(new LinearLayout.LayoutParams(WC,WC));
        subLayout3.addView(passText);

        //エディットテキストの生成
        userPass = new EditText(getApplicationContext());
        userPass.setText("yourPass");
        userPass.setTextColor(Color.rgb(180,0,255));
        userPass.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
        subLayout3.addView(userPass);

        //テキストビューの生成
        missText = new TextView(getApplicationContext());
        missText.setText("");
        missText.setTextSize(18);
        missText.setTextColor(Color.rgb(255,0,0));
        missText.setLayoutParams(new LinearLayout.LayoutParams(WC,WC));
        subLayout4.addView(missText);

        //ボタンの生成
        Button button1 = new Button(getApplicationContext());
        button1.setTag("0");
        button1.setOnClickListener(this);
        button1.setTextColor(Color.rgb(180,0,255));
        button1.setText("ログイン");
        button1.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
        subLayout5.addView(button1);

        Space sp2 = new Space(getApplicationContext());
        sp2.setLayoutParams(new LinearLayout.LayoutParams(100, 50));
        subLayout5.addView(sp2);

        //ボタンの生成
        Button button2 = new Button(getApplicationContext());
        button2.setTag("1");
        button2.setOnClickListener(this);
        button2.setTextColor(Color.rgb(180,0,255));
        button2.setText("新規登録");
        button2.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
        subLayout5.addView(button2);

        layout.addView(subLayout1, new LinearLayout.LayoutParams(WC, WC));

        Space sp3 = new Space(getApplicationContext());
        sp3.setLayoutParams(new LinearLayout.LayoutParams(20, 50));
        layout.addView(sp3);

        layout.addView(subLayout2, new LinearLayout.LayoutParams(WC, WC));

        layout.addView(subLayout3, new LinearLayout.LayoutParams(WC, WC));

        layout.addView(subLayout4, new LinearLayout.LayoutParams(WC, WC));

        Space sp4 = new Space(getApplicationContext());
        sp4.setLayoutParams(new LinearLayout.LayoutParams(50, 50));
        layout.addView(sp4);

        layout.addView(subLayout5, new LinearLayout.LayoutParams(WC, WC));

        layout.setGravity(android.view.Gravity.CENTER);


        //レイアウトの生成
        layout2 = new LinearLayout(getApplicationContext());
        LinearLayout subLayout6 = new LinearLayout(getApplicationContext());
        LinearLayout subLayout7 = new LinearLayout(getApplicationContext());
        LinearLayout subLayout8 = new LinearLayout(getApplicationContext());

        layout2.setBackgroundColor(Color.WHITE);
        subLayout6.setBackgroundColor(Color.WHITE);
        subLayout7.setBackgroundColor(Color.WHITE);
        subLayout8.setBackgroundColor(Color.WHITE);

        layout2.setOrientation(LinearLayout.VERTICAL);
        subLayout6.setOrientation(LinearLayout.HORIZONTAL);
        subLayout7.setOrientation(LinearLayout.HORIZONTAL);
        subLayout8.setOrientation(LinearLayout.HORIZONTAL);

        layout2.setBackgroundResource(R.drawable.char_back0);

        subLayout6.setBackgroundColor(Color.argb(0, 0, 0, 0));
        subLayout7.setBackgroundColor(Color.argb(0, 0, 0, 0));
        subLayout8.setBackgroundColor(Color.argb(0, 0, 0, 0));

        //テキストビューの生成
        TextView textView = new TextView(getApplicationContext());
        textView.setText("キャラクターセレクト");
        textView.setTextSize(32);
        textView.setTextColor(Color.rgb(180,0,255));
        textView.setLayoutParams(new LinearLayout.LayoutParams(WC,WC));
        subLayout6.addView(textView);

        //イメージボタンの生成
        subLayout7.addView(makeButton(res2bmp(this,R.drawable.char1),"2"));

        Space sp5 = new Space(getApplicationContext());
        sp5.setLayoutParams(new LinearLayout.LayoutParams(50, 50));
        subLayout7.addView(sp5);

        subLayout7.addView(makeButton(res2bmp(getApplicationContext(),R.drawable.char2),"3"));

        Space sp6 = new Space(getApplicationContext());
        sp6.setLayoutParams(new LinearLayout.LayoutParams(50, 50));
        subLayout7.addView(sp6);

        subLayout7.addView(makeButton(res2bmp(getApplicationContext(),R.drawable.char3),"4"));

        Space sp7 = new Space(getApplicationContext());
        sp7.setLayoutParams(new LinearLayout.LayoutParams(50, 50));
        subLayout7.addView(sp7);

        subLayout7.addView(makeButton(res2bmp(getApplicationContext(),R.drawable.char4),"5"));

        //ボタンの生成
        Button button3 = new Button(getApplicationContext());
        button3.setTag("6");
        button3.setOnClickListener(this);
        button3.setTextColor(Color.rgb(180,0,255));
        button3.setText("決定");
        button3.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
        subLayout8.addView(button3);

        Space sp8 = new Space(getApplicationContext());
        sp8.setLayoutParams(new LinearLayout.LayoutParams(100, 50));
        subLayout8.addView(sp8);

        //ボタンの生成
        Button button4 = new Button(getApplicationContext());
        button4.setTag("7");
        button4.setTextColor(Color.rgb(180,0,255));
        button4.setText("ログアウト");
        button4.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
        subLayout8.addView(button4);

        layout2.addView(subLayout6, new LinearLayout.LayoutParams(WC, WC));

        Space sp9 = new Space(getApplicationContext());
        sp9.setLayoutParams(new LinearLayout.LayoutParams(20, 50));
        layout2.addView(sp9);

        layout2.addView(subLayout7, new LinearLayout.LayoutParams(WC, WC));

        Space sp10 = new Space(getApplicationContext());
        sp10.setLayoutParams(new LinearLayout.LayoutParams(50, 50));
        layout2.addView(sp10);

        layout2.addView(subLayout8, new LinearLayout.LayoutParams(WC, WC));

		layout2.setGravity(android.view.Gravity.CENTER);
    }

    //イメージボタンの生成(3)
    private ImageButton makeButton(Bitmap bmp,String tag) {
        Bitmap pressed=filteringBmp(bmp,Color.LTGRAY,PorterDuff.Mode.MULTIPLY);
        ImageButton button=new ImageButton(getApplicationContext());
        button.setTag(tag);
        button.setOnClickListener(this);
        StateListDrawable drawables=new StateListDrawable();
        int statePressed=android.R.attr.state_pressed;
        drawables.addState(new int[]{statePressed},new BitmapDrawable(pressed));
        drawables.addState(new int[]{            },new BitmapDrawable(bmp));
        button.setBackgroundDrawable(drawables);
        button.setLayoutParams(new LinearLayout.LayoutParams(
            bmp.getWidth(),bmp.getHeight()));
        return button;
    }

    //ビットマップのフィルタリング(4)
    private static Bitmap filteringBmp(Bitmap bmp,int color,PorterDuff.Mode mode) {
        int w=bmp.getWidth();
        int h=bmp.getHeight();
        Bitmap result=Bitmap.createBitmap(w,h,Config.ARGB_8888);
        BitmapDrawable bd=new BitmapDrawable(bmp);
        bd.setBounds(0,0,w,h);
        bd.setColorFilter(color,mode);
        Canvas c=new Canvas(result);
        bd.draw(c);
        return result;
    }

    //リソース→ビットマップ
    public static Bitmap res2bmp(Context context,int resID) {
        return BitmapFactory.decodeResource(
            context.getResources(),resID);
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

	//ダイアログ
	private static void showDialog(Context context, String title, String text, DialogInterface.OnClickListener listener){
		AlertDialog.Builder ad = new AlertDialog.Builder(context);
		ad.setTitle(title);
		ad.setMessage(text);
		ad.setPositiveButton("Yes", listener);
		ad.setNegativeButton("No", listener);
		ad.show();
	}

    //ボタンクリック時に呼ばれる(2)
    public void onClick(View view) {
        int tag=Integer.parseInt((String)view.getTag());

        if (tag == 0) {
        	missText.setText("認証中...");
        	String sendData = "login," + userId.getText() + "," + userPass.getText();

        	userName = userId.getText().toString();

        	if(TCPConnect.tcp == null){
        		TCPConnect.tcp = new TCPConnect(this, sendData);
        		return;
        	}

        	TCPConnect.tcp.send(sendData);

        } else if(tag == 1) {
        	byte[] b = userId.getText().toString().getBytes();

        	if((b[0] >= 'A' && b[0] <= 'Z') || (b[0] >= 'a' && b[0] <= 'z')){
	        	missText.setText("登録中...");
	        	String sendData = "new," + userId.getText() + "," + userPass.getText();

	        	if(TCPConnect.tcp == null){
	        		TCPConnect.tcp = new TCPConnect(this, sendData);
	        		return;
	        	}

	        	TCPConnect.tcp.send(sendData);

        	}else {
        		missText.setText("IDは[A-Z][a-z]から始まる文字列のみです");
        	}

        } else if(tag == 2){
        	layout2.setBackgroundResource(R.drawable.char_back1);
        	selectChar = 1;

        } else if(tag == 3) {
        	layout2.setBackgroundResource(R.drawable.char_back2);
        	selectChar = 2;

        } else if(tag == 4){
        	layout2.setBackgroundResource(R.drawable.char_back3);
        	selectChar = 3;

        } else if(tag == 5) {
        	layout2.setBackgroundResource(R.drawable.char_back4);
        	selectChar = 4;

        } else if(tag == 6){
        	if(selectChar != 0){
        		this.changeActivity();
        	}

        } else if(tag == 7) {

        }
    }

    //アクティビティ変更メソッド
    public void changeActivity() {
        //アプリ内のアクティビティを呼び出すインテントの生成
        Intent intent = new Intent(this,
        		com.example.softwaregamecliantandroid.RoomActivity.class);

        changeFlag = true;

        try{
	        //インテントへのパラメータ指定
	        intent.putExtra("userName", userName);
	        intent.putExtra("select", selectChar);

	        //アクティビティの呼び出し
	        this.startActivity(intent);
	        finish();
        }catch (Exception e){
        	e.printStackTrace();
        }
    }

    //アクティビティ停止時に呼ばれる
    @Override
    public void onStop(){
    	super.onStop();
    }
}