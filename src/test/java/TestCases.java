import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import dao.*;
import entities.*;
import exceptions.*;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import utilities.*;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestCases {
	
	public static double marks = 0;
	
	static PersonDao dao;
	
	@Test
	@Order(1)
	void basicChecks() throws Exception {
		Assertions.assertAll(() -> {
			
			if(Class.forName("entities.Person").getDeclaredFields().length >= 3 && Class.forName("entities.Person").getDeclaredAnnotationsByType(Entity.class).length == 1)
				marks += 0.25;
			
			Exception.class.isAssignableFrom(Class.forName("exceptions.PersonException"));
			if(PersonDao.class.isAssignableFrom(Class.forName("dao.PersonDaoImpl"))) {	
				dao = (PersonDao) Class.forName("dao.PersonDaoImpl").getDeclaredConstructor().newInstance();
			}
			marks += 0.25;
		});
	}
    
    @Test
    @Order(1)
    void addPerson() throws Exception {
    	Assertions.assertAll(() -> {
	        EntityManager em = (EntityManager) Class.forName("utilities.EMUtil").getDeclaredMethod("provideEntityManager").invoke(EMUtil.class);
	        Person person = new Person();
	        person.getClass().getDeclaredMethod("setName", String.class).invoke(person,"ganesh");
	        person.getClass().getDeclaredMethod("setSalary", double.class).invoke(person, 25000);
			
	        Person person1 = null;
	        if(dao != null)
	        	person1 = (Person) dao.getClass().getDeclaredMethod("addPerson", Person.class).invoke(dao, person);
	        
	        if(person1 == null) 
	        	Assertions.fail("method is returning a null value");
	        
	        Person person2 = em.find(Person.class, person1.getClass().getDeclaredMethod("getId").invoke(person1));
	        if(person2 == null || !person2.getClass().getDeclaredMethod("getName").invoke(person2).equals(person.getClass().getDeclaredMethod("getName").invoke(person))) 
	        	Assertions.fail("method is not inserting the person defined data into the database correctly");
	        
	        marks += 0.5;
    	});
    }

    @Test
    @Order(2)
    void addPersonException() throws Exception {
        Assertions.assertAll(() -> {
        	Person person = new Person();
            person.getClass().getDeclaredMethod("setName", String.class).invoke(person,"lakshay");
            person.getClass().getDeclaredMethod("setSalary", double.class).invoke(person, 25000);

            Assertions.assertDoesNotThrow(() -> dao.getClass().getDeclaredMethod("addPerson", Person.class).invoke(dao, person), "Method is throwing exception even when it is saving an object while adding an person");
            
            Assertions.assertThrows(PersonException.class,() -> dao.addPerson(null), "method is not throwing Person Exception, even when we are passing null objects while adding an person");
            marks += 0.25;
        });
    }

    @Test
    @Order(3)
    void findPersonById() throws Exception {
    	Assertions.assertAll(() -> {
	    	EntityManager em = (EntityManager) Class.forName("utilities.EMUtil").getDeclaredMethod("provideEntityManager").invoke(EMUtil.class);
	        Person person = new Person();
	        person.getClass().getDeclaredMethod("setName", String.class).invoke(person,"raju");
	        person.getClass().getDeclaredMethod("setSalary", double.class).invoke(person, 12000);
	
	        em.getTransaction().begin();
	        person = em.merge(person);
	        em.getTransaction().commit();
	        
	        Assertions.assertEquals(person, dao.getClass().getDeclaredMethod("findPersonById", int.class).invoke(dao, person.getClass().getDeclaredMethod("getId").invoke(person)), "method is not finding the person by id correctly");
	        marks += 0.5;
    	});
    }

    @Test
    @Order(4)
    void findPersonByIdException() throws Exception{
    	Assertions.assertAll(() -> {
	    	EntityManager em = (EntityManager) Class.forName("utilities.EMUtil").getDeclaredMethod("provideEntityManager").invoke(EMUtil.class);
	        Person person = new Person();
	        person.getClass().getDeclaredMethod("setName", String.class).invoke(person,"hemant");
	        person.getClass().getDeclaredMethod("setSalary", double.class).invoke(person, 12000);
	        em.getTransaction().begin();
	        person = em.merge(person);
	        em.getTransaction().commit();
	
	        Person finalPerson = person;
	        Assertions.assertDoesNotThrow(() -> {
	            dao.getClass().getDeclaredMethod("findPersonById", int.class).invoke(dao, finalPerson.getClass().getDeclaredMethod("getId").invoke(finalPerson));
	        },"method is throwing exception even when we are passing the person id which is already there in the database while finding by id");
	
	        Assertions.assertThrows(PersonException.class,() -> dao.findPersonById(-1) ,"method is not throwing Person Exception when unknown id is passed while finding by id");
	        marks += 0.25;
    	});
    }


    @Test
    @Order(5)
    void deletePerson() throws Exception {
    	Assertions.assertAll(() -> {
	    	EntityManager em = (EntityManager) Class.forName("utilities.EMUtil").getDeclaredMethod("provideEntityManager").invoke(EMUtil.class);
	        Person person = new Person();
	        person.getClass().getDeclaredMethod("setName", String.class).invoke(person,"piyush");
	        person.getClass().getDeclaredMethod("setSalary", double.class).invoke(person, 19000);
	
	        em.getTransaction().begin();
	        person = em.merge(person);
	        em.getTransaction().commit();
	        
	        em.getTransaction().begin();
	        dao.getClass().getMethod("deletePerson", int.class).invoke(dao, person.getClass().getDeclaredMethod("getId").invoke(person));
	        em.getTransaction().commit();
	        em.clear();
	        Assertions.assertNull(em.find(Person.class, person.getClass().getDeclaredMethod("getId").invoke(person)),"method is not deleting the records with particular id correctly");
	        marks += 0.75;
    	});
    }

    @Test
    @Order(6)
    void deletePersonException() throws Exception{
    	Assertions.assertAll(() -> {
	    	EntityManager em = (EntityManager) Class.forName("utilities.EMUtil").getDeclaredMethod("provideEntityManager").invoke(EMUtil.class);
	        Person person = new Person();
	        person.getClass().getDeclaredMethod("setName", String.class).invoke(person,"parikshit");
	        person.getClass().getDeclaredMethod("setSalary", double.class).invoke(person, 12000);
	        em.getTransaction().begin();
	        person = em.merge(person);
	        em.getTransaction().commit();
	        
	        Person finalPerson = person;
	        Assertions.assertDoesNotThrow(() -> dao.getClass().getMethod("deletePerson", int.class).invoke(dao, finalPerson.getClass().getDeclaredMethod("getId").invoke(finalPerson)), "method is throwing exception even when a known id is passed while deleting");
	        Assertions.assertThrows(PersonException.class,() -> dao.deletePerson(34289),"method is not throwing a Person Exception when an unknown id is passed while deleting person");
	        marks += 0.25;
    	});
    }

    @Test
    @Order(7)
    void buildScore(){
        System.out.println("[MARKS] marks is " + marks);
    }
}
