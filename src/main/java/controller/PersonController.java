package controller;

import org.hibernate.SessionFactory;

public class PersonController extends Controller {

    public PersonController(SessionFactory factory) {
        super(factory);
    }
}
