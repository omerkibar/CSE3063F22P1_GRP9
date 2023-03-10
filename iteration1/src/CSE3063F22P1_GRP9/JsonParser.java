package CSE3063F22P1_GRP9;

import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class JsonParser {

	private JSONObject input;
	private ArrayList<ArrayList<Course>> courses;
	public JsonParser() throws Exception{
		Reader reader = new FileReader("parameters.json");
		JSONParser parser = new JSONParser();
		this.input = (JSONObject) parser.parse(reader);
		courses = new ArrayList<ArrayList<Course>>();
		readCourses();
	}

	private void readCourses() throws Exception{
		JSONObject coursesJSON = (JSONObject) input.get("courses");
		for(int i = 1;i<9;i++) {
			ArrayList<Course> semesterCourses = new ArrayList<Course>();
			JSONArray semester = (JSONArray)coursesJSON.get("s"+i);
			Iterator<JSONObject> iterator = semester.iterator();
			while (iterator.hasNext()) {
                Course c = getCourseObject(iterator.next());
                semesterCourses.add(c);
            }
			courses.add(semesterCourses);
		}
		
		for(int i = 0;i<courses.size();i++) {
			for(int j = 0;j<courses.get(i).size();j++) {
				Course currCourse = courses.get(i).get(j);
				if(currCourse.getPrerequisiteId()!=null) {;
					currCourse.setPrerequisite(findCourse(currCourse.getPrerequisiteId()));
				}
			}
		}
	}
	
	private Course getCourseObject(JSONObject courseJSON) throws Exception{
		Course course = new Course();
		course.setID(courseJSON.get("Course Code").toString());
		course.setName(courseJSON.get("Course Name").toString());
		if(courseJSON.get("Prerequisite")!=null) {
			course.setPrerequisiteId(courseJSON.get("Prerequisite").toString());
		}
		course.setQuota(Integer.parseInt(courseJSON.get("Quota").toString()));
		course.setCredit(Integer.parseInt(courseJSON.get("Credit").toString()));
		course.setSemester(courseJSON.get("Semester").toString());
		return course;
	}
	
	private Course findCourse(String courseId) {
		for(int i = 0;i<courses.size();i++) {
			for(int j = 0;j<courses.get(i).size();j++) {;
				if(courses.get(i).get(j).getID().equals(courseId))
					return courses.get(i).get(j);
			}
		}
		return null;
	}

	public ArrayList<String> getFirstNames() {
		ArrayList<String> names = new ArrayList<String>();
		JSONArray namesJSON =(JSONArray)input.get("firstNames");
		Iterator<String> iterator = namesJSON.iterator();
		while (iterator.hasNext()) {
            names.add(iterator.next());
        };
        return names;
	}
	
	public ArrayList<String> getLastNames(){
		ArrayList<String> lastNames = new ArrayList<String>();
		JSONArray lastNamesJSON =(JSONArray)input.get("lastNames");
		Iterator<String> iterator = lastNamesJSON.iterator();
		while (iterator.hasNext()) {
            lastNames.add(iterator.next());
        };
        return lastNames;
	}
	
	public ArrayList<String> getAdvisors(){
		ArrayList<String> advisors = new ArrayList<String>();
		JSONArray advisorsJSON =(JSONArray)input.get("advisors");
		Iterator<String> iterator = advisorsJSON.iterator();
		while (iterator.hasNext()) {
            advisors.add(iterator.next());
        };
        return advisors;
	};
	
	public ArrayList<ArrayList<Course>> getCourses() {
		return courses;
	}
	
	public String getSemester() {
		return input.get("semester").toString();
	}
}
