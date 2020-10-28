package controller;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import model.Account;
import model.User;
import utils.Constants;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.OptionalInt;
import java.util.stream.IntStream;

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
            writer.writeValue(Paths.get(Constants.USER_PATH_FILE).toFile(), userList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update(User user) {
        ArrayList<User> users = findAll();
        OptionalInt indexOpt = IntStream.range(0, users.size())
                .filter(i -> user.getId().equals(users.get(i).getId()))
                .findFirst();
        if (indexOpt.isPresent()) {
            users.remove(indexOpt.getAsInt());
            users.add(indexOpt.getAsInt(), user);
            try {
                ObjectMapper mapper = new ObjectMapper();
                ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
                writer.writeValue(Paths.get(Constants.USER_PATH_FILE).toFile(), users);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<User> findAll() {
        ObjectMapper mapper = new ObjectMapper();
        if (Files.exists(Paths.get(Constants.USER_PATH_FILE))) {
            try {
                return new ArrayList<>(Arrays.asList(mapper.readValue(Paths.get(Constants.USER_PATH_FILE).toFile(), User[].class)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }

    public User findByCpf(String cpf) {
        return findAll().stream().filter(user -> user.getCpf().equals(cpf)).findFirst().orElse(null);
    }

    public User findById(Long id) {
        return findAll().stream().filter(user -> user.getId().equals(id)).findFirst().orElse(null);
    }

    public User findByUserPassWord(String username, String password) {
        return findAll().stream().filter(user -> user.getUsername().equals(username)
                && user.getPassword().equals(password)).findFirst().orElse(null);
    }
}
