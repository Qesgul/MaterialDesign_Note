package com.trendmicro.materialdesign_note.act;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.trendmicro.materialdesign_note.Adapter.Message;
import com.trendmicro.materialdesign_note.Adapter.MyDatabaseHelper;
import com.trendmicro.materialdesign_note.R;
import com.trendmicro.materialdesign_note.Utils.RSAUtils;
import com.trendmicro.materialdesign_note.Utils.TimeUtils;

/**
 * Created by Axes on 2018/2/20.
 */

public class EditActivity extends BaseActivity {
	public static final int RETURN_CODE = 1;
	Message message;
	public Toolbar toolbar;
	private EditText title,password;
	private TextView memoinfo,time;
	private Button save;
	private MyDatabaseHelper dbHelper;
	SQLiteDatabase db;
	public int REQUEST_CODE=404;
	private LinearLayout memoBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit);
		init();
	}

	// 需要传递数据和返回数据的方式启动这个activity
	public static void startEditActivityForResult(Activity context, Message msg) {
		Intent intent = new Intent(context, EditActivity.class);
		if(msg!=null)intent.putExtra("editMsg",new Gson().toJson(msg));
		context.startActivityForResult(intent, RETURN_CODE);
	}

	public void init(){
		toolbar= findViewById(R.id.toolbar_edit);
		toolbar.setNavigationOnClickListener(new NavigationListener());

		title=findViewById(R.id.title);
		password=findViewById(R.id.passWord);
		memoinfo=findViewById(R.id.memo);
		time=findViewById(R.id.timeTextView);
		memoBack=findViewById(R.id.memoBack);
		memoBack.setOnClickListener(new memoBackListener());
		save=findViewById(R.id.save);
		save.setOnClickListener(new SaveListener());

		dbHelper = new MyDatabaseHelper(this, "Safe.db", null, 1);
		db = dbHelper.getWritableDatabase();

		String stringEditInfo;
		Intent intent = getIntent();
		stringEditInfo = intent.getStringExtra("editMsg");// 取出传过来的Diary对象
		if (stringEditInfo == null) {
			toolbar.setTitle("添加");
			time.setText("创建于："+ TimeUtils.getNowTime());
			message=new Message();
		}else {
			message = new Gson().fromJson(stringEditInfo, Message.class);
			title.setText(message.getTitle());
			memoinfo.setText(message.getMemoInfo());
			time.setText(message.getTime());
			try {
				String decryptPass=RSAUtils.decryptData(message.getPassword(),Key2);
				password.setText(decryptPass);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	class memoBackListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			memoinfo.setFocusable(true);
			memoinfo.setFocusableInTouchMode(true);
			memoinfo.requestFocus();
			EditActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		}

	}

	class NavigationListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			setResult(REQUEST_CODE,null);
			finish();
		}

	}

	class SaveListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			try {
				String encryptPass=RSAUtils.encryptData(password.getText().toString(),Key1);
				message.setPassword(encryptPass);
			} catch (Exception e) {
				e.printStackTrace();
			}
			message.setTitle(title.getText().toString());
			message.setMemoInfo(memoinfo.getText().toString());
//			String tv_time=time.getText().toString();
			ContentValues values = new ContentValues();
			if(toolbar.getTitle().equals("添加")){
				values.put("title", message.getTitle());
				values.put("password",message.getPassword());
				values.put("memoInfo",message.getMemoInfo());
				values.put("time", time.getText().toString());
				db.insert("PasswodSafe", null, values); // 插入第一条数据
				values.clear();
				message.setTime(time.getText().toString());
				REQUEST_CODE=200;
			}else {
				values.put("title", message.getTitle());
				values.put("password",message.getPassword());
				values.put("memoInfo",message.getMemoInfo());
				db.update("PasswodSafe", values, "time = ?", new String[] { message.getTime() });
				values.clear();
				REQUEST_CODE=100;
			}
			Intent intent = new Intent();
			intent.putExtra("edit_return", new Gson().toJson(message));
			setResult(REQUEST_CODE, intent);
			Toast.makeText(EditActivity.this,String.valueOf(REQUEST_CODE), Toast.LENGTH_SHORT).show();
			finish();
		}

	}

	@Override
	protected void onDestroy(){
		super.onDestroy();
		setResult(REQUEST_CODE,null);
	}
}
