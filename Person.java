import java.util.ArrayList;
import java.util.List;

// Person Class
public class Person {
    private int id;
    private ArrayList<Course> courses;
    private ArrayList<Course> mainRequests;
    private ArrayList<Course> alts;
    private ArrayList<Course> altRequests;
    private ArrayList<Course> outsides;
    private ArrayList<Course> linear;
    private List<List<CourseSection>> timetable;

    // Constructor
    public Person(int id, ArrayList<Course> courses, ArrayList<Course> alts, ArrayList<Course> outsides, ArrayList<Course> linear) {
        this.id = id;
        this.courses = courses;
        this.mainRequests = courses;
        this.alts = alts;
        this.altRequests = new ArrayList<>(alts);
        this.outsides = outsides;
        this.linear = linear;
        this.timetable = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            this.timetable.add(new ArrayList<>());
        }
    }

    // Getters and Setters (Java convention)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Course> getCourses() {
        return courses;
    }

    public void setCourses(ArrayList<Course> courses) {
        this.courses = courses;
    }

    public ArrayList<Course> getMainRequests() {
        return mainRequests;
    }

    public void setMainRequests(ArrayList<Course> mainRequests) {
        this.mainRequests = mainRequests;
    }

    public ArrayList<Course> getAlts() {
        return alts;
    }

    public void setAlts(ArrayList<Course> alts) {
        this.alts = alts;
    }

    public ArrayList<Course> getAltRequests() {
        return altRequests;
    }

    public void setAltRequests(ArrayList<Course> altRequests) {
        this.altRequests = altRequests;
    }

    public ArrayList<Course> getOutsides() {
        return outsides;
    }

    public void setOutsides(ArrayList<Course> outsides) {
        this.outsides = outsides;
    }

    public ArrayList<Course> isLinear() {
        return linear;
    }

    public void setLinear(ArrayList<Course> linear) {
        this.linear = linear;
    }
    
    public ArrayList<Course> getLinear() {
        return this.linear;
    }


    public List<List<CourseSection>> getTimetable() {
        return timetable;
    }

    public void setTimetable(List<List<CourseSection>> timetable) {
        this.timetable = timetable;
    }

    @Override
    public String toString() {
        return "\n" + id;
    }

}
