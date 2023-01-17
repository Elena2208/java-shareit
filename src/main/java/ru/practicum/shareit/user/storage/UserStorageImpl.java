package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserStorageImpl implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long id = 1;

    @Override
    public User create(User user) {
        user.setId(id++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public void delete(long idUser) {
        users.remove(idUser);
    }

    @Override
    public User getId(long idUser) {
        User user = users.get(idUser);
        if (user == null) {
            throw new NotFoundException("User not found.");
        }
        return user;
    }

    @Override
    public List<User> getList() {
        return new ArrayList<>(users.values());
    }
}
