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
        User updateUser = users.get(user.getId());
        if (user.getName() != null && !user.getName().isEmpty()) {
            updateUser.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            updateUser.setEmail(user.getEmail());
        }
        users.put(updateUser.getId(), updateUser);
        return users.get(updateUser.getId());
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
