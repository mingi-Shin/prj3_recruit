package kr.co.sist.user.service;

import java.util.List;

import org.springframework.stereotype.Service;

import kr.co.sist.user.entity.PositionCodeEntity;
import kr.co.sist.user.repository.PositionCodeRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor  
public class PositionCodeService {

	private final PositionCodeRepository pcr;
	
	/**
	 * 모든 포지션 코드 찾기
	 * @return
	 */
	public List<PositionCodeEntity> searchAllPositionCode(){
		return pcr.findAll();
	}
	
	/**
	 * id값과 일치하는 포지션 코드 찾기
	 * @param positionSeq
	 * @return
	 */
	public PositionCodeEntity searchOnePosition(int positionSeq) {
		
		PositionCodeEntity pce = pcr.findById(positionSeq).orElse(null);
		
		return pce;
	}
	
	
	
}
