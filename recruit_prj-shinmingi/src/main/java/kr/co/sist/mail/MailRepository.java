package kr.co.sist.mail;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MailRepository extends JpaRepository<MailVerificationEntity, Long> {

	public List<MailVerificationEntity> findAllByEmail(String Email);
	
}
