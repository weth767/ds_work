package model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Setter
@Getter
@Entity
@ToString
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 11, unique = true, nullable = false)
    private String cpf;

    @Temporal(TemporalType.DATE)
    private Date birthday;

    @OneToOne(mappedBy = "person")
    private User user;

    @OneToOne(mappedBy = "owner")
    private Account account;
}
