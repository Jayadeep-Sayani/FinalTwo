public class Course {
    private String courseID;
    private String courseName;
    private int maxEnrollment;
    private int maxSections;
    private boolean outsideTimetable;
    private int sections;
    private boolean isLinear;
    
    public Course(String courseID, String courseName, int maxEnrollment, int sections) {
        this.courseID = courseID;
        this.courseName = courseName;
        this.maxEnrollment = maxEnrollment;
        this.maxSections = sections;
        this.outsideTimetable = false;
        this.sections = 0;
        this.isLinear = false;
    }
    
    public String getCourseID() {
        return courseID;
    }
    
    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }
    
    public String getCourseName() {
        return courseName;
    }
    
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
    
    
    
    public void setMaxEnrollment(int maxEnrollment) {
        this.maxEnrollment = maxEnrollment;
    }
    
    public int getMaxSections() {
        return maxSections;
    }
    
    public void setMaxSections(int maxSections) {
        this.maxSections = maxSections;
    }
    
    public boolean isOutsideTimetable() {
        return outsideTimetable;
    }
    
    public void setOutsideTimetable(boolean outsideTimetable) {
        this.outsideTimetable = outsideTimetable;
    }
    
    public int getSections() {
        return sections;
    }
    
    public void setSections(int sections) {
        this.sections = sections;
    }
    
    public boolean isLinear() {
        return isLinear;
    }
    
    public void setLinear(boolean isLinear) {
        this.isLinear = isLinear;
    }
    
    @Override
    public String toString() {
        return "\n>>>>>>>>>>>>>\n" +
                courseID + ": " + courseName + "\n" +
                " Max: " + maxEnrollment + "\n" +
                ">>>>>>>>>>>>>\n";
    }

	public int getMaxEnrollment() {
		return maxEnrollment;
	}
}
