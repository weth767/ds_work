package model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class User implements Serializable {
    private Long id;
    private String username;
    private String password;
    private String name;
    private String cpf;
    private boolean logged;
    private Date birthday;

    public User() {
        this.logged = false;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", cpf='" + cpf + '\'' +
                ", birthday=" + birthday +
                '}';
    }
}
