import java.util.ArrayList;
import java.util.List;

// Assuming Course and its attributes are defined in another class

public class CourseSection {
    private List<Course> courses;
    private int maxEnrollment;
    private List<Person> studentList; // Assuming Student class is defined

    public CourseSection(List<Course> courses) {
        this.setCourses(courses);
        this.setMaxEnrollment(courses.get(0).getMaxEnrollment());
        this.setStudentList(new ArrayList<>());
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Course course : getCourses()) {
            sb.append(course.getCourseName()).append(", ");
        }
        return sb.toString();
    }

	public List<Course> getCourses() {
		return courses;
	}

	public void setCourses(List<Course> courses) {
		this.courses = courses;
	}

	public List<Person> getStudentList() {
		return studentList;
	}

	public void setStudentList(List<Person> studentList) {
		this.studentList = studentList;
	}

	public int getMaxEnrollment() {
		return maxEnrollment;
	}

	public void setMaxEnrollment(int maxEnrollment) {
		this.maxEnrollment = maxEnrollment;
	}
}