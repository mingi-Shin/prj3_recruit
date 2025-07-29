package kr.co.sist.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.sist.user.entity.AttachmentEntity;

public interface AttachmentRepository extends JpaRepository<AttachmentEntity, Integer> {

}
