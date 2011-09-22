package ss.androidclub.SysuClassTable.SQL;


import java.util.Comparator;

public class MyCompare implements Comparator {
	@Override
	public int compare(Object object1, Object object2) {
		// TODO Auto-generated method stub
		Lesson lesson1 = (Lesson)object1;
		Lesson lesson2 = (Lesson)object2;
		
		return compareTime(lesson1.getTime(), lesson2.getTime());
	}
	
	/**
	 * 比较两个时间的先后
	 * 
	 * @param time1		时间1
	 * @param time2		时间2
	 * @return			若时间1先于时间2，返回true，否则返回false
	 */
	private int compareTime(String time1, String time2) {
		String[] sub1 = time1.split(":");
		String[] sub2 = time2.split(":");
		int hour1 = Integer.valueOf(sub1[0]);
		int hour2 = Integer.valueOf(sub2[0]);
		int min1 = Integer.valueOf(sub1[1].split(" ")[0]);
		int min2 = Integer.valueOf(sub2[1].split(" ")[0]);

		if(hour1 < hour2) {
			return -1;
		}
		else if(hour1 == hour2) {
			if(min1 < min2)
				return -1;
			else if(min1==min2)
			return 0;
		}
		return 1;
	}
}