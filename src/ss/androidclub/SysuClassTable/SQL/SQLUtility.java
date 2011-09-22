package ss.androidclub.SysuClassTable.SQL;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 处理数据库的主要的类
 * 
 * @author YONG
 *
 */
public class SQLUtility {
	private static final String TAG = "db";
	private static final String DB_NAME = "lessons.db";
	private static final String DB_TABLE = "lessons";
	private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS lessons (" + "NAME TEXT ," +
			"TIME TEXT," + "CLASSROOM TEXT," + "WEEKS TEXT," + "DSZ TEXT," + "WEEKPOS TEXT)" ;
	private static final int DB_VERSION = 2;
	private Context mContext;
	private SQLHelper helper;
	private SQLiteDatabase db;
	
	public SQLUtility(Context context) {
		mContext = context;
		helper = new SQLHelper(context, DB_NAME, null, DB_VERSION);
		db = helper.getWritableDatabase();
	}
	
	/**
	 * 建表
	 */
	public void createTable() {
		db.execSQL(CREATE_TABLE);
	}
	
	/**
	 * 先把原来的表清掉，再去添加数据
	 */
	public void cleanDB() {
		db.execSQL("DROP TABLE " + DB_TABLE);
	}
	
	/**
	 * 添加Lesson到数据库中
	 * 
	 * @param lesson	要添加的课程
	 * @return			添加成功或者已存在该课程，返回true， 否则返回false
	 */
	public boolean addLesson(Lesson lesson) {
		String sql = "";
		
		//查看是否已经有该记录，此处也可以对课程信息进行更新
		/*
		sql = "select * from " + DB_TABLE + " where NAME='" + lesson.getName() + "'";
		Cursor cursor = db.rawQuery(sql, null);
		if(cursor.getCount() > 0) {
			return true;
		}
		*/
		//进行插入操作
		try {
			sql = "insert into " + DB_TABLE +" values ('" + lesson.getName() + "','" + lesson.getTime() + "','" +
					lesson.getClassRoom() + "','" + lesson.getWeeks() + "','" + lesson.getDanShuangZhou() + "','" +
					lesson.getWeekPosition() + "')";
			db.execSQL(sql);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			Log.e(TAG, "insert error, sql=" + sql);
			return false;
		}
	}
	
	/**
	 * 通过星期几来获取当天的所有课程
	 * 
	 * @param weekpos	星期几，格式:星期一为1，星期二为2，依此类推
	 * @return			返回星期X的所有课程信息
	 */
	public ArrayList<Lesson> getLessonsByWeekPos(String weekpos) {
		ArrayList<Lesson> lessons = new ArrayList<Lesson>();
		
		String sql = "select * from " + DB_TABLE + " where WEEKPOS='" + weekpos + "'";
		Cursor cursor = db.rawQuery(sql, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Lesson lesson = new Lesson(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5));
			lessons.add(lesson);
			cursor.moveToNext();
		}
		
		return lessons;
	}

	/**
	 * 查询所有的课程
	 * 
	 * @return	返回课表中的所有课程
	 */
	public ArrayList<Lesson> getAllLessons() {
		ArrayList<Lesson> lessons = new ArrayList<Lesson>();
		
		String sql = "select * from " + DB_TABLE;
		Cursor cursor = db.rawQuery(sql, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Lesson lesson = new Lesson(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5));
			lessons.add(lesson);
			cursor.moveToNext();
		}
		
		return lessons;
	}
	
	/*
	 * 检查是否存在数据
	 * 
	 * @return 存在数据返回真，否则返回假
	 */
	public boolean checkDataBase(){
		boolean result = false;
		String sql = "select count(*) as c from sqlite_master  where type='table' and name ='" + DB_TABLE+"'";
		Cursor cursor = db.rawQuery(sql, null);
		if(cursor.moveToNext())
		{
			int count = cursor.getInt(0);
			if(count>0)
			result = true;
		}
			
		return result;
	}
	
	/**
	 * 关闭数据库
	 */
	public void closeDB() {
		db.close();
	}
	
}