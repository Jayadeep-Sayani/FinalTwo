import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.io.FileWriter;

public class Main {

	public static void main(String[] args) {
// integer declarations
		int generations = 100;

		for (int i = 0; i < generations; i++) {

			// list declarations
			ArrayList<Person> people = new ArrayList<>();
			ArrayList<Course> classes = new ArrayList<>();
			ArrayList<Course> tempCourses = new ArrayList<>();
			ArrayList<Course> altCourses = new ArrayList<>();
			ArrayList<Course> outsides = new ArrayList<>();
			ArrayList<Course> linear = new ArrayList<>();

			String[] outsideTimetables = { "XC---09--L", "MDNC-09C-L", "MDNC-09M-L", "XBA--09J-L", "XLDCB09S-L",
					"YCPA-0AX-L", "MDNCM10--L", "YED--0BX-L", "MMUCC10--L", "YCPA-0AXE-", "MMUOR10S-L", "MDNC-10--L",
					"MIDS-0C---", "MMUJB10--L", "MDNC-11--L", "YCPA-1AX-L", "MDNCM11--L", "YCPA-1AXE-", "MGRPR11--L",
					"MGMT-12L--", "YED--1EX-L", "MWEX-2A--L", "MCMCC11--L", "MWEX-2B--L", "MIMJB11--L", "MMUOR11S-L",
					"MDNC-12--L", "YCPA-2AX-L", "MDNCM12--L", "YCPA-2AXE-", "MGRPR12--L", "MGMT-12L--", "YED--2DX-L",
					"YED--2FX-L", "MCMCC12--L", "MWEX-2A--L", "MIMJB12--L", "MWEX-2B--L", "MMUOR12S-" };

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

			for (Person person : people) {
				giveLinearCourses(person.getLinear(), person, globalTimetable, availableClasses, blockings);
			}

			for (Person person : people) {
				giveSequences(person.getCourses(), person, sequencing, globalTimetable, availableClasses, blockings);
			}
			
			for (Person person : people) {
				currBlock = giveAvailableCourses(person.getCourses(), person, currBlock, globalTimetable, availableClasses, blockings);
			}
//			
//			for (Person person : people) {
//				giveAltCourses(person.getAlts(), person, globalTimetable, availableClasses, blockings);
//			}
//			

			System.out.println(i);
			
			try (BufferedWriter writer = new BufferedWriter(new FileWriter("peoples.csv"))) {
	            // writing header or initializing the csv file

	            // creating a csv writer object
	            for (Person std : people) {
	                writer.write("ID: " + std.getId() + "\n----------\n");

	                for (int j = 0; j < 8; j++) {
	                    StringBuilder course = new StringBuilder();
	                    for (CourseSection block : std.getTimetable().get(j)) {
	                    	
	                        course.append(block);
	                    }
	                    writer.write((j / 4 + 1) + "" + (char) (j % 4 + 65) + ": " + course.toString() + "\n");
	                }
	                writer.write("\n");
	   
	            }
	        } catch (IOException e) {
	            System.err.println("Error writing to file: " + e.getMessage());
	        }
		}
	} // main

	@SuppressWarnings("unchecked")
	public static void giveSequences(ArrayList<Course> personRequests, Person person,
			HashMap<Course, ArrayList<Course>> sequencing, ArrayList<ArrayList<CourseSection>> globalTimetable,
			ArrayList<CourseSection> availableClasses, ArrayList<ArrayList<Course>> blockings) {

		for (int i = 0; i < personRequests.size(); i++) {
			Course request = personRequests.get(i);
			if (sequencing.keySet().contains(request)) {
				for (Course course : sequencing.get(request)) {
					if (personRequests.contains(course)) {
						ArrayList<Integer> firstSemester = new ArrayList<Integer>();
						ArrayList<Integer> secondSemester = new ArrayList<Integer>();
						for (int j = 4; j < 8; j++) {
							secondSemester.add(j);
						}
						for (int j = 0; j < 4; j++) {
							firstSemester.add(j);
						}
						if (giveCourse(request, firstSemester, person, globalTimetable, availableClasses, blockings)) {
							person.getCourses().remove(request);

							if (giveCourse(course, secondSemester, person, globalTimetable, availableClasses,
									blockings)) {
								person.getCourses().remove(course);
							}
							break;
						}
						if (giveCourse(request, secondSemester, person, globalTimetable, availableClasses, blockings)) {
							person.getCourses().remove(request);
						}
					}
				}
			}
		}
	}

	public static boolean giveCourse(Course request, ArrayList<Integer> periods, Person student,
			ArrayList<ArrayList<CourseSection>> globalTimetable, ArrayList<CourseSection> availableClasses,
			ArrayList<ArrayList<Course>> blockings) {

		for (int period : periods) {
			if (student.getTimetable().get(period).size() > 0) {
				continue;
			}

			for (CourseSection block : globalTimetable.get(period)) {
				if (!block.getCourses().contains(request)) {
					continue;
				}

				if (!availableClasses.contains(block)) {
					continue;
				}

				if (block.getStudentList().size() >= block.getMaxEnrollment()) {
					availableClasses.remove(block);
					continue;
				}

				student.getTimetable().get(period).add(block);
				block.getStudentList().add(student);
				return true;
			}
		}

		for (int i = periods.size() - 1; i >= 0; i--) {
			if (student.getTimetable().get(i).size() > 0) {
				periods.remove(periods.get(i));
				continue;
			}

			ArrayList<Course> tempBlockCourses = new ArrayList<Course>();
			tempBlockCourses.add(request);
			for (ArrayList<Course> blocking : blockings) {
				if (blocking.contains(request)) {
					tempBlockCourses = blocking;
					break;
				}

			}
			if (tempBlockCourses.size() > 0) {
				if (tempBlockCourses.get(0).getSections() >= tempBlockCourses.get(0).getMaxSections()) {
					continue;
				}

				for (int j = 0; j < tempBlockCourses.size(); j++) {
					tempBlockCourses.get(j).setSections(tempBlockCourses.get(j).getSections() + 1);
				}

				CourseSection newCourseSection = new CourseSection(tempBlockCourses);

				newCourseSection.getStudentList().add(student);
				student.getTimetable().get(periods.get(i)).add(newCourseSection);
				globalTimetable.get(periods.get(i)).add(newCourseSection);
				availableClasses.add(newCourseSection);
				return true;
			}

		}
		return false;
	}

	public static void giveLinearCourses(ArrayList<Course> linearCourses, Person student,
			ArrayList<ArrayList<CourseSection>> globalTimetable, ArrayList<CourseSection> availableClasses,
			ArrayList<ArrayList<Course>> courseBlockings) {
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

			for (int x = linearCourses.size() - 1; x >= 0; x--) {
				Course course = linearCourses.get(x);
				for (CourseSection block : globalTimetable.get(i)) {

					if (!availableClasses.contains(block)) {
						continue;
					}

					if (block.getCourses().contains(course)) {
						student.getTimetable().get(i).add(block);
						student.getTimetable().get(altPeriod).add(block);
						block.getStudentList().add(student);
						linearCourses.remove(course);

						for (Course lincourse : block.getCourses()) {
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

				if (foundCourse)
					break;
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

			if (student.getTimetable().get(availableBlocks.get(rand)).size() > 0
					|| student.getTimetable().get(otherPeriod).size() > 0) {
				availableBlocks.remove(availableBlocks.get(rand));
				continue;
			}

			ArrayList<Course> tempBlockCourses = new ArrayList<Course>();
			tempBlockCourses.add(linearCourses.get(0));

			for (ArrayList<Course> blocking : courseBlockings) {
				if (blocking.contains(linearCourses.get(0))) {
					tempBlockCourses = blocking;
					break;
				}
			}

			if (tempBlockCourses.get(0).getSections() >= tempBlockCourses.get(0).getMaxSections()) {
				linearCourses.remove(linearCourses.get(0));
				for (Course lincourse : tempBlockCourses) {
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

			for (Course course : tempBlockCourses) {
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
			for (Course lincourse : tempBlockCourses) {
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

	public static ArrayList<Integer> getLeastBlocks(ArrayList<ArrayList<CourseSection>> globalTimetable) {
        ArrayList<Integer> lengths = new ArrayList<Integer>();
        int x = 0;
        

        while(lengths.size() < 8) {
            
            for (int i = 0; i < 8; i++) {
                if (globalTimetable.get(i).size() == x) {
                    lengths.add(i);
                }
        	}
            x = x + 1;
        }
        return lengths;
	}
	public static void giveAltCourses(ArrayList<Course> altCourses, Person student,
			ArrayList<ArrayList<CourseSection>> globalTimetable, ArrayList<CourseSection> availableClasses,
			ArrayList<ArrayList<Course>> courseBlocking) {
		for (int period = 0; period < 8; period++) {
			boolean foundCourse = false;

			if (student.getTimetable().get(period).size() > 0) {
				continue;
			}

			for (Course course : altCourses) {
				for (CourseSection block : globalTimetable.get(period)) {

					if (!availableClasses.contains(block)) {
						continue;
					}

					if (block.getStudentList().size() >= block.getMaxEnrollment()) {
						availableClasses.remove(block);
						continue;
					}

					if (block.getCourses().contains(course)) {
						student.getTimetable().get(period).add(block);
						block.getStudentList().add(student);
						altCourses.remove(course);
						foundCourse = true;
						break;
					}
				}

				if (foundCourse) {
					break;
				}
			}
		}
		


		ArrayList<Integer> availableBlocks = new ArrayList<Integer>();

		for (int k = 0; k < 8; k++) {
			availableBlocks.add(k);
		}

		while (altCourses.size() > 0 && availableBlocks.size() > 0) {
			int rand = (int) (Math.random() * availableBlocks.size());

			if (student.getTimetable().get(availableBlocks.get(rand)).size() > 0) {
				availableBlocks.remove(availableBlocks.get(rand));
				continue;
			}


			ArrayList<Course> tempBlockCourses = new ArrayList<Course>();
			tempBlockCourses.add(altCourses.get(0));

			for (ArrayList<Course> blocking : courseBlocking) {
				if (blocking.contains(altCourses.get(0))) {
					tempBlockCourses = blocking;
					break;
				}
			}

			if (tempBlockCourses.get(0) != null) {
				if (tempBlockCourses.get(0).getSections() >= tempBlockCourses.get(0).getMaxSections()) {
					altCourses.remove(altCourses.get(0));
					continue;
				}

				for (int l = 0; l < tempBlockCourses.size(); l++) {
					if (tempBlockCourses.get(l) != null) {
						tempBlockCourses.get(l).setSections(tempBlockCourses.get(l).getSections() + 1);
					}
				}

				CourseSection newSection = new CourseSection(tempBlockCourses);
				newSection.getStudentList().add(student);
				student.getTimetable().get(availableBlocks.get(rand)).add(newSection);
				globalTimetable.get(availableBlocks.get(rand)).add(newSection);
				availableClasses.add(newSection);
				altCourses.remove(altCourses.get(0));
			} else {
				break;
			}
		}
		
		
	}

	public static int giveAvailableCourses(ArrayList<Course> requestedCourses, Person student, int currBlock,
			ArrayList<ArrayList<CourseSection>> globalTimetable, ArrayList<CourseSection> availableClasses,
			ArrayList<ArrayList<Course>> blockings) {
		
		for (Course requestedCourse : requestedCourses) {
			boolean blockFound = false;

			for (int i = 0; i < 8; i++) {
				if (student.getTimetable().get(i).size() > 0) {
					continue;
				}
				for (int j = 0; j < globalTimetable.get(i).size(); j++) {

					if (globalTimetable.get(i).get(j).getCourses().contains(requestedCourse)) {
						if (!availableClasses.contains(globalTimetable.get(i).get(j))) {
							continue;
						}

						if (globalTimetable.get(i).get(j).getStudentList().size() + 1 <= globalTimetable.get(i).get(j)
								.getMaxEnrollment()) {
							globalTimetable.get(i).get(j).getStudentList().add(student);
							student.getTimetable().get(i).add(globalTimetable.get(i).get(j));
							blockFound = true;
							break;
						} else {
							availableClasses.remove(globalTimetable.get(i).get(j));
						}
					}
				}

				if (blockFound) {
					break;
				}
			}

			if (!blockFound) {
				for (int blockWithLeastCourses : getLeastBlocks(globalTimetable)) {
					if (student.getTimetable().get(blockWithLeastCourses).size() > 0) {
						continue;
					}

					ArrayList<Course> tempBlockCourses = new ArrayList<Course>();
					tempBlockCourses.add(requestedCourse);
					for (ArrayList<Course> blocking : blockings) {
						if (blocking.contains(requestedCourse)) {
							tempBlockCourses = blocking;
							break;
						}
					}
					
					if (tempBlockCourses.get(0) != null) {
						if (tempBlockCourses.get(0).getSections() >= tempBlockCourses.get(0).getMaxSections()) {
							break;
						}
					

						for (Course course : tempBlockCourses) {
							if (course != null) {
								course.setSections(course.getSections() + 1);
							}
						}

						CourseSection newCourseSection = new CourseSection(tempBlockCourses);
						newCourseSection.getStudentList().add(student);
						student.getTimetable().get(blockWithLeastCourses).add(newCourseSection);
						globalTimetable.get(blockWithLeastCourses).add(newCourseSection);
						availableClasses.add(newCourseSection);
						break;
					}
				}
			}
		}

		return currBlock;
	}


	public static void processCourses(String csvFilePath, ArrayList<Course> classes, ArrayList<Course> linearCourses,
			String[] outsideTheTimetable) {
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

	public static void processRequests(ArrayList<Course> classes, ArrayList<Course> outsides, ArrayList<Course> linear,
			ArrayList<Person> people, ArrayList<Course> tempCourses, ArrayList<Course> altCourses) {
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
	
	public static ArrayList<Double> getMetrics(ArrayList<Person> students) {
		ArrayList<Double> stats = new ArrayList<Double>();
		
		int numStudents = 0;
		int numCourseRequests = 0;
		int numRequestsPlaced = 0;
		
		int numEightRequests = 0;
		int numEightOutOfEight = 0;
		int numEightOutOfEightWithAlts = 0;
		
		int numOneOrTwoSpares = 0;
		int numThreeToEightSpares = 0;
		
		for (Person student : students) {
			numStudents++;
			
			int numRequests = student.getMainRequests().size();
			int numMainPlaced = 0;
			int numMainOrAltPlaced = 0;
			int numSpares = 0;
			
			for (List<CourseSection> block : student.getTimetable()) {
				if (block.size() == 0) {
					numSpares++;
				}
				outerloop:
				for (CourseSection subBlock : block) {
					for (Course course : subBlock.getCourses()) {
						if (student.getMainRequests().contains(course)) {
							numMainPlaced++;
							numMainOrAltPlaced++;
							break outerloop;
						} else if (student.getAltRequests().contains(course)) {
							numMainOrAltPlaced++;
							break outerloop;
						}
						
					}
				}
			}
			numCourseRequests += numRequests;
			numRequestsPlaced += numMainPlaced;
			
			if (numRequests == 8) {
				numEightRequests++;
				if (numMainPlaced == 8) {
					numEightOutOfEight++;
					numEightOutOfEightWithAlts++;
				}
				if (numMainOrAltPlaced == 8) {
					numEightOutOfEightWithAlts++;
				}
			}
			
			if (numSpares == 1 || numSpares == 2) {
				numOneOrTwoSpares++;
			} else if (numSpares > 2) {
				numThreeToEightSpares++;
			}
		}
		
		stats.add((double) numRequestsPlaced / numCourseRequests);
		stats.add((double) numEightOutOfEight / numEightRequests);
		stats.add((double) numEightOutOfEightWithAlts / numEightRequests);
		stats.add((double) numOneOrTwoSpares / numStudents);
		stats.add((double) numThreeToEightSpares / numStudents);
		return stats;
	}

	public static void processSequences(ArrayList<Course> classes, HashMap<Course, ArrayList<Course>> sequencing) {
		String csvFile = "data/Course Sequencing Rules.csv";
		try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] row = line.split(",");
				Course tempKey = getCourse(classes, row[0]);
				ArrayList<Course> tempValues = new ArrayList<Course>();

				for (String subseq : row[1].split(" ")) {
					tempValues.add(getCourse(classes, subseq));

					sequencing.put(tempKey, tempValues);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
