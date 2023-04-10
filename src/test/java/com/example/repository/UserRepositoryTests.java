package com.example.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.user.Role;
import com.example.user.User;
import com.example.user.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

@DataJpaTest //Sólo comprueba la capa repositorio
@AutoConfigureTestDatabase(replace = Replace.NONE) //Utiliza otra base de datos externa para hacer las pruebas
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    private User user0;

    @BeforeEach
    void setUp() {
        user0 = User.builder()
        .firstName("Test User 0")
        .lastName("Agulló")
        .password("54321")
        .email("USER0@gmail.com")
        .role(Role.USER)
        .build();
    }

    @Test //Testea el metodo añadir usuario
    @DisplayName("Test para agregar un user")
    public void testAddUser() {

        /**
	 * Segun el enfoque: Una prueba unitaria se divide en tres partes
	 *
	 * 1. Arrange: Setting up the data that is required for this test case
	 * 2. Act: Calling a method or Unit that is being tested.
	 * 3. Assert: Verify that the expected result is right or wrong.
	 *
	 * Segun el enfoque BDD
	 *
	 * 1. given
	 * 2. when
	 * 3. then
     * 
	 * */  
    
     
        // given - dado que:

        User user1 = User.builder()
        .firstName("Test User 1")
        .lastName("Agulló")
        .password("12345")
        .email("elisabetaudiovisual@gmail.com")
        .role(Role.USER)
        .build();

        //when

        User userAdded = userRepository.save(user1);

        //then

        assertThat(userAdded).isNotNull();
        assertThat(userAdded.getId()).isGreaterThan(0L);

    }

    @DisplayName("Test para listar usuario")
    @Test
    public void testFindAllUsers() {

        //given

        User user1 = User.builder()
        .firstName("Test User 1")
        .lastName("Agulló")
        .password("12345")
        .email("elisabetaudiovisual@gmail.com")
        .role(Role.USER)
        .build();

        userRepository.save(user0);
        userRepository.save(user1);

        //when

        List<User> usuarios = userRepository.findAll();

        //then

        assertThat(usuarios).isNotNull();
        assertThat(usuarios.size()).isEqualTo(3);


    }
}
