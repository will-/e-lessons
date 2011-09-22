package ss.androidclub.SysuClassTable;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import ss.androidclub.SysuClassTable.Request.Requests;
import ss.androidclub.SysuClassTable.SQL.Lesson;
import ss.androidclub.SysuClassTable.SQL.MyCompare;
import ss.androidclub.SysuClassTable.SQL.SQLUtility;
import ss.androidclub.SysuClassTable.SQL.TableInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ShowLessons extends Activity {
	private SQLUtility sqlUtility;
	private String jsonString;
	private List<Lesson> lessons;
	private int weekPos;
	
	private final String[] week = {"", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期天"};
	
	//界面控件
	private TextView weekPosView;
	private ListView lessonListView;
	private Button prevDayButton;
	private Button nextDayButton;
	private ProgressDialog progressDialog;

	//版本信息
	private static boolean hasShownUpdate = false;
	public static int versionCode;
	public static String versionName;
	public static String newVersionName;
	public static String newVersionInfo;
	private String newVersionPath;
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				progressDialog.cancel();
				
				Intent intent = new Intent();
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setAction(android.content.Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.fromFile(new File("/sdcard/e-lessons" + newVersionName + ".apk")),
						"application/vnd.android.package-archive");
				startActivity(intent);
				break;
			case 1:
				chack4Update();
				break;
			default:
				break;
			}
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lessons_detail);
		
		getCurrentVersion();
		
		if(!hasShownUpdate) {
			hasShownUpdate = true;
			new Thread(new Runnable() {
			
				@Override
				public void run() {
					// TODO Auto-generated method stub
					newVersionPath = Requests.GetNewVersionName();
				
					Message msg = new Message();
					msg.what = 1;
					handler.sendMessage(msg);
				}
			}).start();
			
		}
		
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("正在更新，请等待...");
		
		//获得上一界面发送过来的json字符串
		jsonString = getIntent().getStringExtra("JSON");
		
		//每次更新就删除之前的数据库
		
			storeToSQLite();
		
			getWeekPos();
			if(sqlUtility.checkDataBase())
			{
			lessons = sqlUtility.getLessonsByWeekPos("" + weekPos);
			sortList();
			initViews();
			}
			else firstUseTip();
	}
	
	/**
	 * 获得当前版本号
	 */
	private void getCurrentVersion() {
		try {
			PackageInfo packageInfo = this.getPackageManager().getPackageInfo(
					this.getPackageName(), 0);
			versionCode = packageInfo.versionCode;
			versionName = packageInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 弹出对话框，提示更新
	 */
	private void chack4Update() {
		if(newVersionName != null && !newVersionName.equals(versionName)) {
			versionName = newVersionName;
			Dialog dialog = new AlertDialog.Builder(this).setTitle("版本更新提示:")
					.setMessage("有新版本，是否更新？")
					.setPositiveButton("是", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							progressDialog.show();
							new Thread(new Runnable() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
									Requests.DownloadNewVersion(newVersionPath);
									
									Message msg = new Message();
									msg.what = 0;
									handler.sendMessage(msg);
								}
							}).start();
						}
					})
					.setNegativeButton("否", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							
						}
					}).create();
			dialog.show();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = new MenuInflater(this);
		inflater.inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);  
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		
		switch (item.getItemId())
		{
		case R.id.update:
			Intent intent = new Intent();
			intent.setClass(ShowLessons.this, LoginDialog.class);
			startActivity(intent);
			finish();
			return true;
		case R.id.about:
			new AlertDialog.Builder(ShowLessons.this)
			.setTitle(getResources().getString(R.string.about_title))
			.setMessage(getResources().getString(R.string.about_text))
			.setPositiveButton("确定", null)
			.show();
			return true;
		case R.id.quit:
			finish();
			return true;
			default:
				return super.onOptionsItemSelected(item);  
				
		}
		
	}
	
	/**
	 * 检查是否第一次使用
	 * 
	 */
	
	
	/*
	 * 用户选择是否更新课表
	 * 
	 */
	private void firstUseTip(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this)
		.setTitle("提示")
		.setMessage("检测到您可能是第一次使用，是否立即更新课表？")
		.setPositiveButton("是", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(ShowLessons.this, LoginDialog.class);
		startActivity(intent);
		finish();
			}
		})
		.setNegativeButton("稍后再说", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		})
		;
		
		builder.create().show();
	}
	/**
	 * 初始化界面
	 */
	private void initViews() {
		weekPosView = (TextView)findViewById(R.id.week_pos);
		lessonListView = (ListView)findViewById(R.id.lesson_list);
		prevDayButton = (Button)findViewById(R.id.prev_day);
		nextDayButton = (Button)findViewById(R.id.next_day);
		
		weekPosView.setText(week[weekPos]);

		lessonListView.setCacheColorHint(0);
		
		lessonListView.setAdapter(myAdapter);
		
		
		
		//监听向前按钮
		prevDayButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				weekPos--;
				if(weekPos == 0) {
					weekPos = 7;
				}
				
				weekPosView.setText(week[weekPos]);
				lessons = sqlUtility.getLessonsByWeekPos("" + weekPos);
				sortList();
				myAdapter.notifyDataSetChanged();
			}
		});
		
		//监听向后按钮
		nextDayButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				weekPos++;
				if(weekPos == 8) {
					weekPos = 1;
				}
				
				weekPosView.setText(week[weekPos]);
				lessons = sqlUtility.getLessonsByWeekPos("" + weekPos);
				sortList();
				myAdapter.notifyDataSetChanged();
			}
		});
	};
	
	/**
	 * 列表的适配器，就是列表中每一项的布局
	 */
	private BaseAdapter myAdapter = new BaseAdapter() {
		
		@Override
		public View getView(int position, View arg1, ViewGroup parent) {
			// TODO Auto-generated method stub
			LayoutInflater inflater = LayoutInflater.from(ShowLessons.this);
			View view = inflater.inflate(R.layout.list_item, null);
			Lesson lesson = lessons.get(position);
			
			TextView timeTextView = (TextView)view.findViewById(R.id.time);
			timeTextView.setText(lesson.getTime());
			
			TextView nameTextView = (TextView)view.findViewById(R.id.name);
			nameTextView.setText(lesson.getName());
			
			TextView classroomTextView = (TextView)view.findViewById(R.id.classroom);
			classroomTextView.setText(lesson.getClassRoom());
			
			TextView weeksTextView = (TextView)view.findViewById(R.id.weeks);
			weeksTextView.setText(lesson.getWeeks());
			
			arg1 = view;
			
			return arg1;
		}
		
		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}
		
		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return lessons.get(arg0);
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return lessons.size();
		}
	};
	
	/**
	 * 从JSON中获取课程，保存到SQLite中，方便以后获取
	 */
	private void storeToSQLite() {
		sqlUtility = new SQLUtility(this);
		if(jsonString != null) {
			//先清除数据表
			if(sqlUtility.checkDataBase())
			sqlUtility.cleanDB();
			
			sqlUtility.createTable();
			TableInfo tableInfo = new TableInfo(jsonString);
    		List<Lesson> lessons = tableInfo.getLessons();
    	
    		boolean result = true;
    		for(int i = 0; i < lessons.size(); i++) {
    			if(!sqlUtility.addLesson(lessons.get(i))) {
    				result = false;
    				break;
    			}
    		}
    		if(!result) {
    			Toast.makeText(this, "Insert to db faild", Toast.LENGTH_LONG).show();
    		}
		}
    }
	
	/**
	 * 获得今天是星期几
	 */
	private void getWeekPos() {
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		weekPos = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		if(weekPos == 0) {
			weekPos = 7;
		}
	}
	
	/**
	 * 对课程进行排序，根据时间
	 */
	private void sortList() {
		if(lessons.size() > 0) {
			Collections.sort(lessons, new MyCompare());
		}
		
	}
	
	@Override
	public void onStop() {
		super.onStop();
		sqlUtility.closeDB();
	}
}