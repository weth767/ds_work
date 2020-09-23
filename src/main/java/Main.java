import connection.Connection;
import controller.PersonController;
import model.Person;
import org.hibernate.SessionFactory;

import java.time.Instant;
import java.util.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        SessionFactory sessionFactory = Connection.getConnection();
        PersonController personController = new PersonController(sessionFactory);
        /*Person person = new Person();
        person.setName("Leandrinho");
        person.setCpf("12345678910");
        person.setBirthday(Date.from(Instant.now()));
        personController.save(person);*/
        /*List<Object> personList = personController.findAll("Person");
        personList.forEach(o -> {
            Person p = (Person) o;
            System.out.println(p);
        });*/
        System.out.println(personController.findById(2L, "Person"));
    }
}
