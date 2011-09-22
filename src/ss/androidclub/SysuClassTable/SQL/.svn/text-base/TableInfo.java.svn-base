package ss.androidclub.SysuClassTable.SQL;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.widget.Toast;

/**
 * 通过一个JSON字符串，构建一个JSON对象，然后获得课程列表
 * 
 * @author YONG
 *
 */
public class TableInfo {
	/**
	 * JSON对象
	 */
	private JSONObject jsonObject;
	
	private final String[] starttime = {"","08:00","08:55","09:50","10:45","11:40","12:35","13:30","14:25","15:20","16:15","17:10","18:05","19:00","19:55","20:50"};
	private final String[] endtime = {"","08:45","09:40","10:35","11:30","12:25","13:20","14:15","15:10","16:05","17:00","17:55","18:50","19:45","20:40","21:35"};
	/**
	 * 所有的课程
	 */
	private ArrayList<Lesson> lessons;
	
	public TableInfo(String json) {
		lessons = new ArrayList<Lesson>();
		
		try {
			jsonObject = new JSONObject(json);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//取出一个个的object，构建Lesson，添加到数组中
		if(jsonObject != null) {
			try {
				JSONArray array = jsonObject.getJSONArray("tableinfo");
				for(int i = 0; i < array.length(); i++) {
					JSONObject lessonObject = (JSONObject)array.get(i);
					Lesson lesson = new Lesson();
					
					lesson.setTime(convertFromJcToTime(lessonObject.getString("jc")));
					lesson.setName(lessonObject.getString("kcmc"));
					lesson.setClassRoom(lessonObject.getString("dd"));
					lesson.setWeeks(lessonObject.getString("zfw"));
					lesson.setDanShuangZhou(lessonObject.getString("dsz"));
					lesson.setWeekPosition(lessonObject.getString("weekpos"));
					
					lessons.add(lesson);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 获取所有的课程
	 * 
	 * @return	所有的课程
	 */
	public List<Lesson> getLessons() {
		return lessons;
	}
	
	
	/*
	 * 将节次转化为时间
	 */
	private String convertFromJcToTime(String jc){
		
		String time = "";
		int start = Integer.parseInt(jc.substring(0,jc.indexOf("-")));
		int end = Integer.parseInt(jc.substring(jc.indexOf("-")+1,jc.indexOf("节")));
		time += starttime[start];
		time += " - ";
		time += endtime[end];
		return time;
		
	
	}
}