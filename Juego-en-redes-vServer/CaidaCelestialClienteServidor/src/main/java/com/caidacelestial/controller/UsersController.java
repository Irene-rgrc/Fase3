package com.caidacelestial.controller;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.caidacelestial.entity.User;
import com.caidacelestial.entity.Message;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@RestController
@RequestMapping("/users")
public class UsersController {

    ConcurrentHashMap<Long, User> users = new ConcurrentHashMap<>();
    private List<Message> chatMessages = new ArrayList<>(); // Lista para almacenar los mensajes
    long nextId = 1; // Initial value

    // Obtener todos los usuarios
    @GetMapping(value = "/")
    public Collection<User> usuarios() {
        return users.values();
    }

    // Crear un nuevo usuario
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

    // Actualizar un usuario existente
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

    // Eliminar un usuario
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

    // Obtener un usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<User> loggearUser(@PathVariable long id) {
        User savedUser = users.get(id);
        if (savedUser != null) {
            return new ResponseEntity<>(savedUser, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Buscar un usuario por nombre de usuario
    @GetMapping("/search")
    public ResponseEntity<User> getUserByUsername(@RequestParam String username) {
        for (User user : users.values()) {
            if (user.getUsername().equals(username)) {
                return new ResponseEntity<>(user, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Buscar un usuario por record
    @GetMapping("/searchByRecord")
    public ResponseEntity<User> getRecordByUsername(@RequestParam long record) {
        for (User user : users.values()) {
            if (user.getRecord() == record) {
                return new ResponseEntity<>(user, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Endpoint para enviar un mensaje de chat
    @PostMapping("/chat")
    @ResponseStatus(HttpStatus.CREATED)
    public Message enviarMensaje(@RequestBody Message mensaje) throws IOException {
        User user = users.get(mensaje.getUserId());
        if (user != null) {
            mensaje.setUsername(user.getUsername());
        } else {
            mensaje.setUsername("Anonimo");
        }
        chatMessages.add(mensaje); // Agregar el mensaje a la lista de chat
        System.out.println(mensaje.getUsername() + ": " + mensaje.getMessage());
        return mensaje;
    }

    // Endpoint para obtener todos los mensajes del chat
    @GetMapping("/obtener-mensajes")
    public List<Message> obtenerMensajes() {
        return chatMessages; // Retornar todos los mensajes almacenados
    }

    // Cargar usuarios desde un archivo al iniciar el servidor
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

    // Guardar usuarios en un archivo antes de apagar el servidor
    @PreDestroy
    public void guardarUsuarios() throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream("src/main/resources/users.txt");
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            objectOutputStream.writeObject(users);
        }
    }
}