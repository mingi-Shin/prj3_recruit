package kr.co.sist.admin.faq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import kr.co.sist.admin.faq.FaqEntity;

public interface FaqRepository extends JpaRepository<FaqEntity,Integer>{
		
	
}
