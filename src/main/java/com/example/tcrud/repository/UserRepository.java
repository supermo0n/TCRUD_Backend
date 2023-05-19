package com.example.tcrud.repository;

import com.example.tcrud.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.username = :username")
    Optional<User> findByUsername(@Param("username") String username);

    boolean existsByUsernameAndDeleteYn(@Param("username") String username, @Param("deleteYn") String deleteYn);

    boolean existsByEmailAndDeleteYn(@Param("email") String email, @Param("deleteYn") String deleteYn);

    boolean existsByNicknameAndDeleteYn(@Param("nickname") String nickname, @Param("deleteYn") String deleteYn);


}





