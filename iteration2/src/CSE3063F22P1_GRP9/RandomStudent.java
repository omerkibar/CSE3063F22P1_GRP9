package CSE3063F22P1_GRP9;

import java.util.ArrayList;
import java.util.Random;

public class RandomStudent {
	private ArrayList<String> names;
	private ArrayList<String> lastNames;
	private ArrayList<Advisor> advisors;
    private ArrayList<ArrayList<Course>> courses;
    private double probabilityOfPassingCourse; 
	RandomStudent(InputJSON input){
		this.names = input.getFirstNames();
		this.lastNames = input.getLastNames();
		this.advisors = input.getAdvisors();
		this.courses = input.getCourses();
		this.probabilityOfPassingCourse = input.getProbabilityOfPassingCourse();
	}
	
	public Student createRandomStudent(int semester,int order) {
		Student student = new Student();
		student.setID(getRandomId(semester, order));
		student.setFirstName(getRandomFirstName());
		student.setLastName(getRandomLastName());
		student.setSemester(semester);
		student.setTranscript(getRandomTranscript(semester,student));
		student.setAdvisor(getRandomAdvisor());
		return student;
	}
	
	private String getRandomId(int semester, int order) {
		int Id = 150118000+order;
		Id += 4000 - (Math.floor((semester+1)/2)*1000);
		return Id+"";
	}
	
	private String getRandomFirstName() {
		return names.get((int)(Math.random()*399));
	}
	
	private String getRandomLastName() {
		return lastNames.get((int)(Math.random()*399));
	}
	
	private Advisor getRandomAdvisor() {
		return advisors.get(0);
	}
	
	private Transcript getRandomTranscript(int semester,Student student) {
		Transcript transcript = new Transcript();
		String currentSemester = "Fall";
		String nextSemester = "Spring";
		for(int i = 1;i<=semester+1;i++) {
			student.setRequestedCourses(new ArrayList<Course>());
			ArrayList<TakenCourse> takenCourses = transcript.getTakenCourses();
			ArrayList<SelectionProblem> selectionProblems = transcript.getSelectionProblems();
			for(int j = 0;j<takenCourses.size();j++) {
				TakenCourse takenCourse = takenCourses.get(j);
				String s = takenCourse.getCourse().getSemester();
				if(takenCourse.getTakenCourseStatus()=="Failed" && (s.equals(currentSemester) ||s.equals("Both"))) {
					student.addRequestedCourse((takenCourse.getCourse()));
				}
			}
			for(int j = 0;j<selectionProblems.size();j++) {
				Course notRegisteredCourse = selectionProblems.get(j).getNotRegisteredCourse();
				if(notRegisteredCourse.getSemester().equals(currentSemester)) {
					student.addRequestedCourse(notRegisteredCourse);
				}
			}	
			appendCoursesAtSemester(i,student.getRequestedCourses());
			if(i-1 == semester) {
				break;
			}
			ArrayList<Course> registeredCourses = registerRequestedCourses(student.getRequestedCourses(),transcript);
			ArrayList<TakenCourse> simulatedGrades = simulateGrades(registeredCourses);
			for(int k = 0;k<simulatedGrades.size();k++) {
				transcript.addTakenCourse(simulatedGrades.get(k));
			}
			String temp = currentSemester;
			currentSemester=nextSemester;
			nextSemester=temp;
		}
		transcript.setSelectionProblems(new ArrayList<SelectionProblem>());
		return transcript;
	}

	private ArrayList<TakenCourse> simulateGrades(ArrayList<Course> registeredCourses) {
		ArrayList<TakenCourse> takenCourses = new ArrayList<TakenCourse>();
		for(int i = 0;i<registeredCourses.size();i++) {
			String status = "Passed";
			float grade = 1;
			Random p = new Random();
			if(p.nextDouble()>this.probabilityOfPassingCourse)
				status = "Failed";
			else
				grade = p.nextInt(3)+2;
			TakenCourse takenCourse = new TakenCourse(registeredCourses.get(i),grade,status);
			takenCourses.add(takenCourse);
		}
		return takenCourses;
	}
	
	private ArrayList<Course> registerRequestedCourses(ArrayList<Course> requestedCourses, Transcript transcript) {
		ArrayList<Course> registeredCourses = new ArrayList<Course>();
		for (Course course : requestedCourses) {
			if(course.getPrerequisite()==null) {
				registeredCourses.add(course);
				continue;
			}
			TakenCourse prerequisiteInTranscript = transcript.findCourse(course.getPrerequisite());
			if(prerequisiteInTranscript==null || !prerequisiteInTranscript.getTakenCourseStatus().equals("Passed")) {
				SelectionProblem sp = new SelectionProblem();
				sp.setNotRegisteredCourse(course);
				transcript.addSelectionProblem(sp);
			}
			else {
				registeredCourses.add(course);
			}		
		}
		return registeredCourses;
	}
	
	private void appendCoursesAtSemester(int semester, ArrayList<Course> requestedCourses) {
		ArrayList<Course> semesterCourses = courses.get(semester-1);
		for(int i = 0;i<semesterCourses.size();i++) {
			requestedCourses.add(semesterCourses.get(i));
		}
	}
}
