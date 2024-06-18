import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Main {
	
	public static void main(String[] args) {
		// integer declarations
		int generations = 1000;
		
		for (int i = 0; i < generations; i++) {
	    
		    // list declarations
			ArrayList<Person> people = new ArrayList<>();   
		    ArrayList<Course> classes = new ArrayList<>();  
		    ArrayList<Course> tempCourses = new ArrayList<>();  
		    ArrayList<Course> altCourses = new ArrayList<>(); 
		    ArrayList<Course> outsides = new ArrayList<>(); 
		    ArrayList<Course> linear = new ArrayList<>(); 
		    
		    String[] outsideTimetables = {
		            "XC---09--L", "MDNC-09C-L", "MDNC-09M-L", "XBA--09J-L", "XLDCB09S-L", "YCPA-0AX-L",
		            "MDNCM10--L", "YED--0BX-L", "MMUCC10--L", "YCPA-0AXE-", "MMUOR10S-L", "MDNC-10--L",
		            "MIDS-0C---", "MMUJB10--L", "MDNC-11--L", "YCPA-1AX-L", "MDNCM11--L", "YCPA-1AXE-",
		            "MGRPR11--L", "MGMT-12L--", "YED--1EX-L", "MWEX-2A--L", "MCMCC11--L", "MWEX-2B--L",
		            "MIMJB11--L", "MMUOR11S-L", "MDNC-12--L", "YCPA-2AX-L", "MDNCM12--L", "YCPA-2AXE-",
		            "MGRPR12--L", "MGMT-12L--", "YED--2DX-L", "YED--2FX-L", "MCMCC12--L", "MWEX-2A--L",
		            "MIMJB12--L", "MWEX-2B--L", "MMUOR12S-"
		        };
		    
		    HashMap<Course, ArrayList<Course>> sequencing = new HashMap<Course, ArrayList<Course>>();
		    ArrayList<ArrayList<CourseSection>> globalTimetable = new ArrayList<>();
		    ArrayList<ArrayList<Course>> blockings = new ArrayList<>();
		    ArrayList<CourseSection> availableClasses = new ArrayList<CourseSection>();
		    // Process data
			processCourses("data/Course Information.csv", classes, linear, outsideTimetables);
			processRequests(classes, outsides, linear, people, tempCourses, altCourses);
			processBlockings(blockings, classes);
			processSequences(classes, sequencing);
			
			for (int j = 0; j < 8; j++) {
				globalTimetable.add(new ArrayList<CourseSection>());
			}
			
			int currBlock = 0;
			Collections.shuffle(people);
			
			for (Person person: people) {
				giveLinearCourses(person.getLinear(), person, globalTimetable, availableClasses, blockings);
			}
			
			for (Person person: people) {
				giveSequences(person.getCourses(), person, sequencing);
			}
		}
	} // main
	
	public static void giveSequences(ArrayList<Course> personRequests, Person person, HashMap<Course, ArrayList<Course>> sequencing) {

		for (Course request: personRequests) {
            if (sequencing.keySet().contains(request)) {
                for (Course course: sequencing.get(request)) {
                    if (personRequests.contains(course)) {
                        if (giveCourse(request, {0, 1, 2, 3}, person)) {
                            person.getCourses().remove(request);
                            if (giveCourse(course, [4, 5, 6, 7], person)) {
                                person.getCourses().remove(course);
                            }
                            break;
                        }                        
                        if(giveCourse(request, [4, 5, 6, 7], person)) {
                            person.getCourses().remove(request)
                        }
                    }
                }
            }
		}
	}
	
	
//	 def giveCourse(request, periods, student):
//	        # random.shuffle(periods)
//	        
//	        # loop through periods
//	        for period in periods:
//
//	            # check if period is available (if the student does not already have a course in it)
//	            if (len(student.timetable[period]) > 0):
//	                continue
//
//	            # loop through blocks in globalTimetable[currPeriod]
//	            for block in globalTimetable[period]:
//	                
//	                # check if requested course is in this block
//	                if (request not in block.courses):
//	                    continue
//	                        
//	                
//	                # check if max enrolment reached (or remove from availableClasses)
//	                if (block not in availableClasses):
//	                    continue
//
//	                if (len(block.studentList) >= int(block.maxEnrollment)): 
//	                    availableClasses.remove(block)
//	                    continue
//	                
//	                # add student to block
//	                student.timetable[period].append(block)
//	                block.studentList.append(student)
//	                return True
//
//	        # loop through periods (and shuffle?)
//	        # random.shuffle(periods)
//	        
//	        for period in periods:
//	            
//	            # check if period is available and remove period if not?
//	            if (len(student.timetable[period]) > 0):
//	                periods.remove(period)
//	                continue
//	            
//	            # create block
//	            tempBlockCourses = [request]
//	            for blocking in courseBlocking:
//	                if request in blocking:
//	                    tempBlockCourses = blocking
//	                    break
//
//	            # check if max sections reached
//	            if tempBlockCourses[0].sections >= tempBlockCourses[0].maxSections:
//	                continue
//
//	            # update number of sections for each course in block
//	            for course in tempBlockCourses:
//	                course.sections = course.sections + 1
//
//	            # create block
//	            newBlock = Block(tempBlockCourses)
//
//	            # add student to block, add block to globalTimetable
//	            newBlock.studentList.append(student)
//	            student.timetable[period].append(newBlock)
//	            globalTimetable[period].append(newBlock)
//	            availableClasses.append(newBlock)
//	            return True
//	        return False
	
	public static void giveLinearCourses(ArrayList<Course> linearCourses, Person student, ArrayList<ArrayList<CourseSection>> globalTimetable, ArrayList<CourseSection> availableClasses, ArrayList<ArrayList<Course>> courseBlockings) {
        for (int i = 0; i < 8; i++) {
            int altPeriod = -1;
            if (i >= 4) {
            	altPeriod = i - 4;
            } else {
            	altPeriod = i + 4;
            }
            boolean foundCourse = false;
            
            if (student.getTimetable().get(i).size() > 0) {
                continue;
            }
            
            for (Course course: linearCourses) {
                for (CourseSection block: globalTimetable.get(i)) {
                    
                	if (!availableClasses.contains(block)) {
                        continue;
                	}
                	
                    if (block.getCourses().contains(course)) {
                        student.getTimetable().get(i).add(block);
                        student.getTimetable().get(altPeriod).add(block);
                        block.getStudentList().add(student);
                        linearCourses.remove(course);
                        
                        for (Course lincourse: block.getCourses()) {
                            if (student.getCourses().contains(lincourse)) {
                                student.getCourses().remove(lincourse);
                            } else if (student.getAlts().contains(lincourse)) {
                                student.getAlts().remove(lincourse);
                            } else if (linearCourses.contains(lincourse)) {
                                linearCourses.remove(lincourse);
                            }
                        }
                        foundCourse = true;
                        break;
                    }
                }   
                
                if (foundCourse) break;
            }
        }
        
        ArrayList<Integer> availableBlocks = new ArrayList<Integer>();
        for (int i = 0; i < 8; i++) {
            availableBlocks.add(i);
        }

        while (linearCourses.size() > 0 && availableBlocks.size() > 0) {
        	int rand = (int) Math.floor(Math.random() * availableBlocks.size());
            int otherPeriod = -1;
            if (availableBlocks.get(rand) >= 4) {
            	otherPeriod = availableBlocks.get(rand) - 4;
            } else {
            	otherPeriod = availableBlocks.get(rand) + 4;
            }

            if (student.getTimetable().get(availableBlocks.get(rand)).size() > 0 || student.getTimetable().get(otherPeriod).size() > 0) {
                availableBlocks.remove(availableBlocks.get(rand));
                continue; 
            }
            
            ArrayList<Course> tempBlockCourses = new ArrayList<Course>();
            tempBlockCourses.add(linearCourses.get(0));
            
            for (ArrayList<Course> blocking: courseBlockings) {
                if (blocking.contains(linearCourses.get(0))) {
                    tempBlockCourses = blocking;
                    break;
                }
            }
            
            if (tempBlockCourses.get(0).getSections() >= tempBlockCourses.get(0).getMaxSections()) {
                linearCourses.remove(linearCourses.get(0));
                for (Course lincourse: tempBlockCourses) {
                    if (student.getCourses().contains(lincourse)) {
                        student.getCourses().remove(lincourse);
                    } else if (student.getAlts().contains(lincourse)) {
                        student.getAlts().remove(lincourse);                        
                    } else if (linearCourses.contains(lincourse)) {
                            linearCourses.remove(lincourse);
                    }
                }
                continue;
            }

            for (Course course: tempBlockCourses) {
                course.setSections(course.getSections() + 1);
            }
            
            CourseSection newBlock = new CourseSection(tempBlockCourses);
            newBlock.getStudentList().add(student);
            student.getTimetable().get(availableBlocks.get(rand)).add(newBlock);
            globalTimetable.get(availableBlocks.get(rand)).add(newBlock);
            
            globalTimetable.get(otherPeriod).add(newBlock);
            student.getTimetable().get(otherPeriod).add(newBlock);

            availableClasses.add(newBlock);
            linearCourses.remove(linearCourses.get(0));
            for (Course lincourse: tempBlockCourses) {
                if (student.getCourses().contains(lincourse)) {
                    student.getCourses().remove(lincourse);
            	} else if (student.getAlts().contains(lincourse)) {
                    student.getAlts().remove(lincourse);
            	} else if (linearCourses.contains(lincourse)) {
                    linearCourses.remove(lincourse);
            	}
            }
        }
	}
	
	// Process Course Information from the CSV file
	public static void processCourses(String csvFilePath, ArrayList<Course> classes, ArrayList<Course> linearCourses, String[] outsideTheTimetable) {
	    String line = "";
	    String csvSplitBy = ",";
	    
	    try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
	    	br.readLine();
	        while ((line = br.readLine()) != null) {
	        	
	            String[] row = line.split(csvSplitBy);
	            if (row.length >= 14 && !row[1].isEmpty() && !row[2].isEmpty()) {
	                Course newCourse = new Course(row[1], row[2], Integer.parseInt(row[11]), Integer.parseInt(row[13]));
	                
	                // Check if newCourse's ID is in outsideTheTimetable array
	                boolean foundInOutside = false;
	                for (String id : outsideTheTimetable) {
	                    if (id.equals(newCourse.getCourseID())) {
	                        newCourse.setOutsideTimetable(true);
	                        foundInOutside = true;
	                        break;
	                    } // if
	                } // for
	                
	                // Check if newCourse's ID ends with "L"
	                if (!foundInOutside && newCourse.getCourseID().endsWith("L")) {
	                    newCourse.setLinear(true);
	                    linearCourses.add(newCourse); // Add the Course object itself, not just its ID
	                } // if
	                classes.add(newCourse);
	            } // if
	        } // while
	    } catch (IOException e) {
	        e.printStackTrace();
	    } // catch

	} // processCourses

	
	public static void processBlockings(ArrayList<ArrayList<Course>> courseBlocking, ArrayList<Course> classes) {
		try (BufferedReader file = new BufferedReader(new FileReader("data/Course Blocking Rules.csv"))) {
            String line;
            while ((line = file.readLine()) != null) {
                String[] row = line.split(",");
                    ArrayList<Course> temp = new ArrayList<Course>();
                    for (String course : row) {
                        temp.add(getCourse(classes, course));
                    }
                    courseBlocking.add(temp);
                }
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public static void processRequests(ArrayList<Course> classes, ArrayList<Course> outsides, ArrayList<Course> linear, ArrayList<Person> people, ArrayList<Course> tempCourses, ArrayList<Course> altCourses) {
		String csvFile = "data/Cleaned Student Requests.csv";
		boolean first = true;
		int id = 0;
				
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] row = line.split(",");
                
                if (first) {
                    id = Integer.parseInt(row[1]);
                    first = false;
                    continue;
                }
                if ("Course".equals(row[0])) {
                    continue;
                }
                if ("ID".equals(row[0])) {
                    Person person = new Person(id, tempCourses, altCourses, outsides, linear);
                    people.add(person);
                    id = Integer.parseInt(row[1]);
                    tempCourses = new ArrayList<>();
                    altCourses = new ArrayList<>();
                    outsides = new ArrayList<>();
                    linear = new ArrayList<>();
                } else if (getCourse(classes, row[0]) == null) {
                    continue;
                } else if (getCourse(classes, row[0]).isOutsideTimetable()) {
                    outsides.add(getCourse(classes, row[0]));
                } else if (getCourse(classes, row[0]).isLinear()) {
                    linear.add(getCourse(classes, row[0]));
                } else if ("Y".equals(row[11])) {
                    altCourses.add(getCourse(classes, row[0]));
                } else {
                    tempCourses.add(getCourse(classes, row[0]));
                }
            }
            
            // Add the last person after loop completes
            Person person = new Person(id, tempCourses, altCourses, outsides, linear);
            people.add(person);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	// Gets the course object based on its course ID
	public static Course getCourse(List<Course> classes, String courseID) {
        for (Course course : classes) {
            if (course.getCourseID().equals(courseID)) {
                return course;
            } // if
        } // for
        return null;
    } // getCourse
	
	// gets student based on their ID
	public static Person getStudent(List<Person> people, int id) {
        Person student = null;
        for (Person person : people) {
            if (person.getId() == id) {
                student = person;
                break; // No need to continue once found
            } // if
        } // for
        return student;
    } // getStudent
	
	public static void processSequences(ArrayList<Course> classes, HashMap<Course, ArrayList<Course>> sequencing) {
		String csvFile = "data/Course Sequencing Rules.csv";
		try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = br.readLine()) != null) {
            	String[] row = line.split(",");
	             Course tempKey = getCourse(classes, row[0]);
	             ArrayList<Course> tempValues = new ArrayList<Course>();

                for (String subseq: row[1].split(" ")) {
                    tempValues.add(getCourse(classes, subseq));

	                sequencing.put(tempKey, tempValues);
                }
            }
		} catch(Exception e) {
			e.printStackTrace();
		}
	}	
}

