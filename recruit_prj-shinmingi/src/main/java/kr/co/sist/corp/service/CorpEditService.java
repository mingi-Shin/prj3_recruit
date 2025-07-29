package kr.co.sist.corp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import kr.co.sist.corp.dto.CorpEntity;
import kr.co.sist.corp.repository.CorpEditRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

@Service
@Transactional
public class CorpEditService {

    @Autowired
    private CorpEditRepository corpRepository;

    private static final String IMAGE_DIR = "/path/to/your/upload/directory"; // 파일이 저장될 디렉토리 경로 설정

    // 기업정보 조회
    public CorpEntity getCorpInfo(Long corpNo) {
        Optional<CorpEntity> corpEntity = corpRepository.findById(corpNo);
        return corpEntity.orElse(null);
    }

    // 기업정보 수정
    public void updateCorpInfo(CorpEntity corpEntity) {
        // 기존 정보 조회
        Optional<CorpEntity> existingCorp = corpRepository.findById(corpEntity.getCorpNo());
        
        if (existingCorp.isPresent()) {
            CorpEntity existing = existingCorp.get();
            
            // 수정 가능한 필드만 업데이트 (읽기 전용 필드는 제외)
            if (corpEntity.getCorpInfo() != null) {
                existing.setCorpInfo(corpEntity.getCorpInfo());
            }
            if (corpEntity.getCorpUrl() != null) {
                existing.setCorpUrl(corpEntity.getCorpUrl());
            }
            if (corpEntity.getCorpAvgSal() != null) {
                existing.setCorpAvgSal(corpEntity.getCorpAvgSal());
            }
            if (corpEntity.getCorpAnnualRevenue() != null) {
                existing.setCorpAnnualRevenue(corpEntity.getCorpAnnualRevenue());
            }
            if (corpEntity.getCorpCreatedAt() != null) {
                existing.setCorpCreatedAt(corpEntity.getCorpCreatedAt());
            }
            if (corpEntity.getCorpEmpCnt() != null) {
                existing.setCorpEmpCnt(corpEntity.getCorpEmpCnt());
            }
            if (corpEntity.getCorpAiActive() != null) {
                existing.setCorpAiActive(corpEntity.getCorpAiActive());
            }
            if (corpEntity.getIndustry() != null) {
                existing.setIndustry(corpEntity.getIndustry());
            }
            if (corpEntity.getCompanySize() != null) {
                existing.setCompanySize(corpEntity.getCompanySize());
            }
            
            // 읽기 전용 필드들은 업데이트하지 않음
            // corpNm, bizCert, corpCeo는 수정 불가
            
            corpRepository.save(existing);
        } else {
            throw new RuntimeException("기업 정보를 찾을 수 없습니다.");
        }
    }

    // 기업정보 존재 여부 확인
    public boolean existsCorpInfo(Long corpNo) {
        return corpRepository.existsById(corpNo);
    }

    // 기업명으로 검색
    public CorpEntity findByCorpName(String corpName) {
        return corpRepository.findByCorpNm(corpName);
    }

    // 사업자등록번호로 검색
    public CorpEntity findByBizCert(String bizCert) {
        return corpRepository.findByBizCert(bizCert);
    }

    // 기업 로고 업데이트
    public void updateCorpLogo(Long corpNo, String savedFileName) {
        CorpEntity corp = corpRepository.findById(corpNo)
            .orElseThrow(() -> new RuntimeException("해당 기업이 존재하지 않습니다."));
        corp.setCorpLogo(savedFileName);
        corpRepository.save(corp);
    }

    // 기업 이미지 업데이트
    public void updateCorpImg(Long corpNo, String savedFileName) {
        CorpEntity corp = corpRepository.findById(corpNo)
            .orElseThrow(() -> new RuntimeException("해당 기업이 존재하지 않습니다."));
        corp.setCorpImg(savedFileName);
        corpRepository.save(corp);
    }

    // 기업 로고와 이미지를 동시에 업데이트
    public void updateCorpLogoAndImg(Long corpNo, String logoFileName, String imageFileName) {
        CorpEntity corp = corpRepository.findById(corpNo)
            .orElseThrow(() -> new RuntimeException("해당 기업이 존재하지 않습니다."));
        
        // 로고와 이미지를 동시에 업데이트
        corp.setCorpLogo(logoFileName);
        corp.setCorpImg(imageFileName);
        
        corpRepository.save(corp);
    }

    // 이미지 저장 메서드 (로고와 일반 이미지 모두 저장)
    public String saveImage(MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            // 파일 이름 생성 (UUID를 사용하거나, 실제 파일명을 사용할 수도 있음)
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            
            // 파일을 지정된 디렉토리에 저장
            File targetFile = new File(IMAGE_DIR, fileName);
            file.transferTo(targetFile); // 파일 저장

            return fileName; // 저장된 파일 이름을 반환
        }
        return null;
    }

    // 기업 이미지 삭제
    public void deleteCorpImage(Long corpNo,Long imageId) {
        // 이미지 파일을 삭제하는 로직 구현 (파일 시스템에서 삭제)
        CorpEntity corp = corpRepository.findById(corpNo)
            .orElseThrow(() -> new RuntimeException("해당 기업이 존재하지 않습니다."));

        // 이미지가 존재하는지 확인하고 삭제
        if (corp.getCorpImg() != null) {
            File file = new File(IMAGE_DIR, corp.getCorpImg());
            if (file.exists()) {
                file.delete(); // 실제 파일 삭제
            }
            corp.setCorpImg(null); // DB에서 이미지 정보 삭제
        }
        
        // 로고 이미지도 삭제하려면 동일한 방식으로 처리
        if (corp.getCorpLogo() != null) {
            File file = new File(IMAGE_DIR, corp.getCorpLogo());
            if (file.exists()) {
                file.delete(); // 실제 파일 삭제
            }
            corp.setCorpLogo(null); // DB에서 로고 이미지 정보 삭제
        }

        corpRepository.save(corp);
    }
}
