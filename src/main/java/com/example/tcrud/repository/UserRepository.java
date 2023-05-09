package com.example.tcrud.repository;

import com.example.tcrud.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {


    Optional<User> findByUsername(String username);

//    Boolean existsByUsername(String username);
//
//    Boolean existsByEmail(String email);
//
//    Boolean existsByNickname(String nickname);


    boolean existsByUsernameAndDeleteYn(@Param("username") String username, @Param("deleteYn") String deleteYn);

    boolean existsByEmailAndDeleteYn(@Param("email") String email, @Param("deleteYn") String deleteYn);

    boolean existsByNicknameAndDeleteYn(@Param("nickname") String nickname, @Param("deleteYn") String deleteYn);

//    @Query(value="select u.*, r.rid, r.name " +
//            "from tb_user u, " +
//            "     tb_role r, " +
//            "     tb_user_role ur " +
//            "where u.id = ur.user_id " +
//            "and   ur.role_id = r.rid " +
//            "and   u.delete_yn = 'N' " +
//            "and   r.delete_yn = 'N' " +
//            "and   u.username like %:username%",
//            countQuery = "select count(*) " +
//                    "from tb_user u, " +
//                    "     tb_role r, " +
//                    "     tb_user_role ur " +
//                    "where u.id = ur.user_id " +
//                    "and   ur.role_id = r.rid " +
//                    "and   u.delete_yn = 'N' " +
//                    "and   r.delete_yn = 'N' " +
//                    "and   u.username like %:username%",
//            nativeQuery = true)
//    Page<UserRoleDto> findAllByUsernameContaining(@Param("username") String username
//                                                    , Pageable pageable);
}





