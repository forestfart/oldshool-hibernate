import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class Main {

	Session session;

	public static void main(String[] args) {
		Main main = new Main();
		main.addNewData();
		main.printSchools();
		//main.printClasses();
		main.close();
	}

	public Main() {
		session = HibernateUtil.getSessionFactory().openSession();
	}

	public void close() {
		session.close();
		HibernateUtil.shutdown();
	}

	private void printSchools() {
		Criteria crit = session.createCriteria(School.class);
		List<School> schools = crit.list();

		System.out.println("### Schools");
		for (School s : schools) {
			System.out.println(s);
			for (SchoolClass sc : s.getClasses()) {
				System.out.println(sc);
				for (Student student : sc.getStudents()) {
					System.out.println(student);
				}
			}
		}
	}
	
	private void printClasses() {
		Criteria crit = session.createCriteria(SchoolClass.class);
		List<SchoolClass> classes = crit.list();

		System.out.println("### Classes");
		for (SchoolClass s : classes) {
			System.out.println(s);
		}
	}
	
	private void addNewData() {
		
		School newSchool = new School();
		newSchool.setName("UJ");
		newSchool.setAddress("Go³êbia 24");
		
		SchoolClass newSchoolClass = new SchoolClass();
		newSchoolClass.setProfile("Biochemistry");
		newSchoolClass.setStartYear(2001);
		newSchoolClass.setCurrentYear(2018);
		
		Set<SchoolClass> newSchoolClassSet = new HashSet<>();
		newSchoolClassSet.add(newSchoolClass);
		
		Student newStudent = new Student();
		newStudent.setName("Anonymous");
		newStudent.setSurname("Anonymous");
		newStudent.setPesel("84215512255");
		
		Set<Student> newStudentsSet = new HashSet<>();
		newStudentsSet.add(newStudent);
		
		newSchoolClass.setStudents(newStudentsSet);
		newSchool.setClasses(newSchoolClassSet);;
		
		

		Transaction transaction = session.beginTransaction();
		session.save(newSchool); // gdzie newSchool to instancja nowej szko³y
		transaction.commit();

	}

	private void jdbcTest() {
		Connection conn = null;
		Statement stmt = null;
		try {
			// STEP 2: Register JDBC driver
			Class.forName("org.sqlite.JDBC");

			// STEP 3: Open a connection
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection("jdbc:sqlite:school.db", "", "");

			// STEP 4: Execute a query
			System.out.println("Creating statement...");
			stmt = conn.createStatement();
			String sql;
			sql = "SELECT * FROM schools";
			ResultSet rs = stmt.executeQuery(sql);

			// STEP 5: Extract data from result set
			while (rs.next()) {
				// Retrieve by column name
				String name = rs.getString("name");
				String address = rs.getString("address");

				// Display values
				System.out.println("Name: " + name);
				System.out.println(" address: " + address);
			}
			// STEP 6: Clean-up environment
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		} finally {
			// finally block used to close resources
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException se2) {
			} // nothing we can do
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			} // end finally try
		} // end try
		System.out.println("Goodbye!");
	}// end jdbcTest

}
