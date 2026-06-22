package com.techmart.controller;

import com.techmart.dto.RegisterRequest;
import com.techmart.entity.User;
import com.techmart.util.PasswordHandler;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class UserController {

    @PersistenceContext
    private EntityManager em;

    public User getUserByEmail(String email) {
        return em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", email)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    public void createNewUser(RegisterRequest registerRequest) throws Exception {
//        hash the password
        String hashedPassword = PasswordHandler.hashPassword(registerRequest.getPassword());

        User newUser = new User(registerRequest.getEmail(), hashedPassword);
        em.persist(newUser);

    }

}
