package ss.androidclub.SysuClassTable.SQL;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 方便对数据库进行open操作
 * 
 * @author YONG
 *
 */
public class SQLHelper extends SQLiteOpenHelper {
	private String tableName = "lessons";
	private String CreateTable = "CREATE TABLE IF NOT EXISTS " + tableName + " (" + "NAME TEXT PRIMARY KEY," +
			"TIME TEXT," + "CLASSROOM TEXT," + "WEEKS TEXT," + "DSZ TEXT," + "WEEKPOS TEXT)";
	
	public SQLHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CreateTable);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		if(oldVersion == 1 && newVersion == 2) {
			//String addCol = "ALTER TABLE "+tableName+" ADD KEY TEXT";
		//	db.execSQL(addCol);
		}
	}
}