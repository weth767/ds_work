package controller;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import model.User;
import utils.Constants;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class UserController {

    public void save(User user) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            ArrayList<User> userList = findAll();
            if (userList.isEmpty()) {
                user.setId(1L);
            } else {
                user.setId(userList.get(userList.size() - 1).getId() + 1);
            }
            userList.add(user);
            ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
            writer.writeValue(Paths.get(Constants.userPathFile).toFile(), userList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<User> findAll() {
        ObjectMapper mapper = new ObjectMapper();
        if (Files.exists(Paths.get(Constants.userPathFile))) {
            try {
                return new ArrayList<>(Arrays.asList(mapper.readValue(Paths.get(Constants.userPathFile)
                        .toFile(), User[].class)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }

    public User findById(Long id) {
        return findAll().stream().filter(user -> user.getId().equals(id)).findFirst().orElse(null);
    }

    public User findByUserPassWord(String username, String password) {
        return findAll().stream().filter(user -> user.getUsername().equals(username)
                && user.getPassword().equals(password)).findFirst().orElse(null);
    }
}
