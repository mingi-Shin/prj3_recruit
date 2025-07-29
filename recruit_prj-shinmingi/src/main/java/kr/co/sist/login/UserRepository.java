package kr.co.sist.login;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.co.sist.user.entity.UserEntity;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {

    // 이름 + 핸드폰번호로 이메일 찾기
    Optional<UserEntity> findByNameAndPhone(String name, String phone);

    // 이메일로 사용자 존재 여부 확인
    boolean existsByEmail(String email);

    // 이메일로 사용자 조회
    Optional<UserEntity> findByEmail(String email);
}
