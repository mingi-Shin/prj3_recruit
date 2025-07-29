package kr.co.sist.corp.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;

import kr.co.sist.corp.dto.AllApplicantInfoDTO;
import kr.co.sist.corp.dto.CorpDTO;
import kr.co.sist.corp.dto.CorpEntity;
import kr.co.sist.corp.dto.JobPostingApplicantStatsDTO;
import kr.co.sist.corp.dto.JobPostingDTO;
import kr.co.sist.corp.dto.JobPostingEntity;
import kr.co.sist.corp.mapper.JobPostingCorpMapper;
import kr.co.sist.corp.mapper.JobPostingTechStackMapper;
import kr.co.sist.corp.repository.JobPostingRepository;
import kr.co.sist.globalController.Exceptions.LoginException;
import kr.co.sist.globalController.Exceptions.NotFoundException;
import kr.co.sist.login.CorpRepository;
import kr.co.sist.user.dto.PositionCodeDTO;
import kr.co.sist.user.dto.TechStackDTO;
import kr.co.sist.user.entity.UserEntity;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor //final 생성자주입 자동
public class JobPostingCorpService {

  private final JobPostingCorpMapper jpm;
  private final JobPostingTechStackMapper jptm;
  private final CorpRepository cRepository;
  private final JobPostingRepository jRepository;
  
  /**
   * 공고 새로 등록하기 
   * 연속적인 insert는 항상 서비스단에서 묶어서 처리하고, @Transactional을 걸어야 안전빵
   * @param jpDTO
   * @return
   */
  @Transactional 
  public void uploadJobPost(JobPostingDTO jpDTO) {
  	
  	try {
  		//1. 공고등록
      jpm.insertJobPost(jpDTO);
      
      /**
       * jpDTO.getJobPostingSeq() 왜 돼?
       * jpDTO는 참조변수
       * mapper내부에서 setter(.setJobPostingSeq(123)) 이런 setter를 호출했꼬, 
       *  (keyProperty="jobPostingSeq" => DTO의 해당 필드에 자동 주입)
       * 나는 그 참조변수를다시 가져와서 보는 것이니 매개변수때와 그 값이 다르다. 
       * 따라서 selectKey를 안쓰면 못가져와 
       */
      int jobPostingSeq = jpDTO.getJobPostingSeq();  
  		
      //2. 기술 스택등록(중간테이블)
      for(Integer techStackSeq : jpDTO.getTechStackSeqList()) {
      	jptm.insertjobPostingTechStack(jobPostingSeq, techStackSeq);
      }
      
		} catch (Exception e) {
			//로그출력 
			e.printStackTrace();
			//트랜잭션 롤백 유도 : RuntimeException 안던지면 롤백안됨 
			throw new RuntimeException("공고등록 중 예외 발생", e); 
		}
  } //end uploadJobPost()
  
  /**
   * 공고 수정하기 (공고수정 -> 기존 기술스택 리스트 삭제 -> 기술스택 새로 등록)
   */
  @Transactional
  public void updateJobPost(JobPostingDTO jpDTO) {
  	
  	try {
  		jpm.modifyJobPost(jpDTO); //공고 수정
  		
  		jptm.deleteJobPostingTechStack(jpDTO.getJobPostingSeq()); //기존 기술스택 데이터 모두 삭제 
  		
      for(Integer techStackSeq : jpDTO.getTechStackSeqList()) {
      	jptm.insertjobPostingTechStack(jpDTO.getJobPostingSeq(), techStackSeq); //기술스택 새로 등록 
      }
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("공고 수정중 예외 발생!", e);
		}
  	
  }
  
  
  
  /**
   * 사업자등록번호로 회사정보 가져오기 
   * @param corpNo
   * @return
   */
  public CorpDTO getCorpDTO(long corpNo) {
  	//@ControllerAdvice 로 NotFoundException을 제어중
  	CorpEntity cEntity = cRepository.findById(corpNo).orElseThrow(() -> new NotFoundException("해당 사용자가 존재하지 않습니다."));
  	
  	//Entity -> DTO 변환
  	CorpDTO cDTO = CorpDTO.from(cEntity);
  	//태그랑 사업자번호 안담았어. 태그는 필요하긴한데, 그건 조회할때 편하려고 하는건데 연수가 잘 할거니까 생략함
  	return cDTO;
  } //end getCorpDTO()
  

  /**
   * 키워드로 포지션 키워드 리스트 조회
   */
  public List<PositionCodeDTO> pDTOList(String keyword){
  	if(keyword == null || keyword.trim().isEmpty()) {
  		return Collections.emptyList(); //빈 리스트 반환, 이게 뭐지?
  	}
  	
  	List<PositionCodeDTO> pDTOList = new ArrayList<PositionCodeDTO>();
  	
  	pDTOList = jpm.selectPostionList(keyword);
  	
  	return pDTOList;
  }
  
  /**
   * 기술스택 키워드 리스트 조회 
   */
  public List<TechStackDTO> tsDTO(String keyword){
  	if(keyword == null || keyword.trim().isEmpty()) {
  		return Collections.emptyList(); //빈 리스트 반환, 이게 뭐지?
  	}
  	
  	List<TechStackDTO> tsDTOList = new ArrayList<TechStackDTO>();
  	
  	tsDTOList = jpm.selectTechStackList(keyword);
  	
  	return tsDTOList;
  }
  
  /**
   * 나의 공고 전체,진행중,마감 갯수 가져오기 
   */
  public List<Map<String, Integer>> selectMyJobPostingCnt(Long corpNo){
  	
  	List<Map<String, Integer>> postCntList = jpm.selectMyPostingCount(corpNo);
  	
  	return postCntList;
  }
  /**
   * 나의 공고 리스트 가져오기 
   */
  public List<JobPostingDTO> selectMyJobPosting(JobPostingDTO jpDTO){
  	
  	List<JobPostingDTO> jpList = new ArrayList<JobPostingDTO>();
  	jpList = jpm.selectMyAllPosting(jpDTO);
  	
  	return jpList;
  }
  
  /**
   * 공고 강제(조기)마감 처리 
   */
  public void updateJobPotingToEnd(int jobPostingSeq) {
  	jpm.finishJobPosting(jobPostingSeq);
  	
  }
  
  /**
   * 나의 특정 공고 가져오기
   */
  public JobPostingDTO selectMyJobPostingOne(long corpNo, int jobPostingSeq) {
  	
  	JobPostingEntity jpEntity  = jRepository.findByCorpNo_CorpNoAndJobPostingSeq(corpNo, jobPostingSeq);
  	
  	JobPostingDTO jpDTO = JobPostingDTO.from(jpEntity);
  	
  	return jpDTO;
  }
  
  /**
   * 공고번호로 해당 공고의 지원자들의 통계자료 가져오기 
   */
  public JobPostingApplicantStatsDTO selectApplicantStats(int JobPostingSeq) {
  	
  	try {
  		JobPostingApplicantStatsDTO jpasDTO = jpm.selectApplicantStats(JobPostingSeq);
  		//System.out.println("[디버깅] 서비_ selectApplicantStats : " + jpasDTO);
  		return jpasDTO;
		} catch (DataAccessException e) {
			e.printStackTrace();
			throw new RuntimeException("지원자 통계 조회 중 오류가 발생했습니다.", e);
		}
  	
  }
  
  /**
   * 공고 번호로 해당 공고 정보 가져오기 
   */
  public JobPostingDTO selectJobPostingInfo(int JobPostingSeq) {
  	JobPostingEntity jpEntity = jRepository.findById(JobPostingSeq).orElseThrow(() -> new NotFoundException("해당 공고가 존재하지 않습니다."));
  	JobPostingDTO jpDTO = JobPostingDTO.from(jpEntity);
  	
  	return jpDTO;
  }
  
  /**
   * 공고의 지원자들 상세정보 엑셀 다운로드 (참조: https://adjh54.tistory.com/664)
   */
  public Resource excelDownload(int jobPostingSeq) {
  	
  	Workbook workbook = new XSSFWorkbook();
    Sheet sheet = workbook.createSheet("구직자 정보");

    // 헤더 행 생성
    Row headerRow = sheet.createRow(0);
    String[] headers = {
        "이름", "이메일", "도로명주소", "출생년도", "성별",
        "경력 직무/포지션", "경력 연차",
        "학교명", "전공", "학점", "학력 구분", "졸업일",
        "토익", "토플", "텝스", "토익스피킹", "오픽", "JPT", "HSK",
        "정보처리기사", "SQLD", "리눅스마스터", "OCP", "ADSP",
        "프로젝트 경험 유무",
        "자격증 개수"
    };
    for (int i = 0; i < headers.length; i++) {
        Cell cell = headerRow.createCell(i);
        cell.setCellValue(headers[i]);
    }

    // 데이터 행 생성 selectAllApplicantInfo
    List<AllApplicantInfoDTO> appInfoList = jpm.selectAllApplicantInfo(jobPostingSeq);
    
    System.out.println("[디버깅] 조회된 데이터 수: " + appInfoList.size()); 
    
    Object[][] data = new Object [appInfoList.size()][];
    for(int i = 0; i < data.length; i++) {
    	AllApplicantInfoDTO dto = appInfoList.get(i);
    	data[i] = new Object[] {
    			dto.getName(),
          dto.getEmail(),
          dto.getRoadAddress(),
          dto.getBirth4(),
          dto.getGender(),
          dto.getExp(),
          dto.getExpYear(),
          dto.getSchoolName(),
          dto.getMajor(),
          dto.getGrade(),
          dto.getEducationType(),
          dto.getGraduateDate(),
          dto.getHasToeic(),
          dto.getHasTofel(),
          dto.getHasTeps(),
          dto.getHasToeicSpeaking(),
          dto.getHasOpic(),
          dto.getHasJpt(),
          dto.getHasHsk(),
          dto.getHasEngineer(),
          dto.getHasSqld(),
          dto.getHasLinux(),
          dto.getHasOcp(),
          dto.getHasAdsp(),
          dto.getHasProject(),
          dto.getCertCount()
    	};
    }
    

    // Sheet 내에 헤더 / 데이터 행 구성
    for (int i = 0; i < data.length; i++) {
        Row row = sheet.createRow(i + 1); // +1 ?? 헤더 다음부터..
        for (int j = 0; j < data[i].length; j++) {
            Cell cell = row.createCell(j);

            if (data[i][j] != null) {
              cell.setCellValue(data[i][j].toString());
	          } else {
	              cell.setCellValue("-"); // null 처리
	          }
            
            // 문자 처리
            if (data[i][j] instanceof String) {
                cell.setCellValue((String) data[i][j]);
            }
            // 숫자 처리
            if (data[i][j] instanceof Integer) {
                cell.setCellValue((Integer) data[i][j]);
            }
        }
    }

    // 열 너비 자동 조정
    for (int i = 0; i < headers.length; i++) {
        sheet.autoSizeColumn(i);
    }

    // 파일 생성
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    try {
        workbook.write(outputStream);
        workbook.close();
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
    ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());
    return resource;

  }

  
  
}
