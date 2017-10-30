package com.redkix.automation.helper;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.redkix.automation.model.EmailServiceType;
import com.redkix.automation.model.User;
import com.redkix.automation.utils.Commons;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class UserHelper {
    private static final String USERS_FILE = "users.json";
    private static List<User> users = new ArrayList<>();

    public static User getUser() {
        List<User> users = listUsers();
        return users.get(Commons.getRandom(0, users.size() - 1));
    }

    public static User getUser(EmailServiceType serviceType) {
        List<User> users = listUsers().stream().
                filter(u -> u.getServiceType() == serviceType).
                collect(Collectors.toList());
        return users.get(Commons.getRandom(0, users.size() - 1));
    }

    public static User[] getUsers(EmailServiceType serviceType, int quantity) {
        List<User> users = listUsers().stream().
                filter(u -> u.getServiceType() == serviceType).
                collect(Collectors.toList());

        if (users.size() < quantity) {
            throw new RuntimeException(
                    String.format("Not enough users of requested service. Requested - %d, available - %d", quantity,
                            users.size()));
        }

        Collections.shuffle(users);

        return users.subList(0, quantity).toArray(new User[quantity]);
    }

    public static User getUser(EmailServiceType type, User... exclude) {
        List<String> excludedEmails = Arrays.asList(exclude).stream().
                map(User::getEmail).
                collect(Collectors.toList());
        return listUsers().stream().
                filter(u -> !excludedEmails.contains(u.getEmail())).
                filter(u -> u.getServiceType() == type).
                findFirst().
                orElseThrow(() -> new NoSuchElementException("No user match provided params"));
    }

    public static User getUserByEmail(String email) {
        return listUsers().stream().filter(u -> email.equals(u.getEmail())).
                findFirst().
                orElseThrow(() -> new NoSuchElementException("User with email " + email + " doesn't exist"));
    }

    public static User getExternalUser() {
        return new User("qa.test.rdx4@gmail.com", "Redkix111").
                setServiceType(EmailServiceType.GMAIL);
    }

    private static List<User> listUsers() {
        if (users.isEmpty()) {
            InputStream inputStream = UserHelper.class.getClassLoader().getResourceAsStream(USERS_FILE);
            users = new Gson().fromJson(new InputStreamReader(inputStream), new TypeToken<List<User>>(){}.getType());
            try {
                inputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return users.stream().
                map(u -> new User(u.getEmail(), u.getPassword()).setServiceType(u.getServiceType())).
                collect(Collectors.toList());
    }

}
