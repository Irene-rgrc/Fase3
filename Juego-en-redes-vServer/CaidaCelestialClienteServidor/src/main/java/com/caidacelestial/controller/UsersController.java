package com.caidacelestial.controller;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.caidacelestial.entity.User;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@RestController
@RequestMapping("/users")
public class UsersController {

    ConcurrentHashMap<Long, User> users = new ConcurrentHashMap<>();
    long nextId = 1; // Initial value

    @GetMapping(value = "/")
    public Collection<User> usuarios() {
        return users.values();
    }

    @PostMapping(value = "/")
    @ResponseStatus(HttpStatus.CREATED)
    public User usuario(@RequestBody User usuario) throws IOException {
        long id = nextId++;
        usuario.setId(id);
        usuario.setRecord(300);
        users.put(id, usuario);
        guardarUsuarios();
        return usuario;
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> actualizaUser(@PathVariable long id, @RequestBody User userActualizado) throws IOException {
    	    User savedUser = users.get(id);
    	    if (savedUser != null) {
    	        savedUser.setUsername(userActualizado.getUsername());
    	        savedUser.setPassword(userActualizado.getPassword());
    	        savedUser.setRecord(userActualizado.getRecord()); 

    	        guardarUsuarios();
    	        return new ResponseEntity<>(savedUser, HttpStatus.OK);
    	    } else {
    	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	    }
    	}

    @DeleteMapping("/{id}")
    public ResponseEntity<User> eliminarUser(@PathVariable long id) throws IOException {
        User savedUser = users.get(id);
        if (savedUser != null) {
            users.remove(id);
            guardarUsuarios();
            return new ResponseEntity<>(savedUser, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> loggearUser(@PathVariable long id) {
        User savedUser = users.get(id);
        if (savedUser != null) {
            return new ResponseEntity<>(savedUser, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<User> getUserByUsername(@RequestParam String username) {
        for (User user : users.values()) {
            if (user.getUsername().equals(username)) {
                return new ResponseEntity<>(user, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
    @GetMapping("/searchByRecord")
    public ResponseEntity<User> getRecordByUsername(@RequestParam long record) {
        for (User user : users.values()) {
            if (user.getRecord() == record) {
                return new ResponseEntity<>(user, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostConstruct
    public void cargarUsuarios() throws IOException, ClassNotFoundException {
        try (FileInputStream fileInputStream = new FileInputStream("src/main/resources/users.txt");
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            ConcurrentHashMap<Long, User> usersEnFichero = (ConcurrentHashMap<Long, User>) objectInputStream.readObject();
            users = usersEnFichero;
            if (!users.isEmpty()) {
                nextId = users.keySet().stream().max(Long::compare).get() + 1;
            }
        } catch (IOException | ClassNotFoundException e) {
            // Handle exceptions (file not found, empty file, etc.)
        }
    }

    @PreDestroy
    public void guardarUsuarios() throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream("src/main/resources/users.txt");
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            objectOutputStream.writeObject(users);
        }
    }
}