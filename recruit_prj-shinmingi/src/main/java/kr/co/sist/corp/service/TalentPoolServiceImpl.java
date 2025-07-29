package kr.co.sist.corp.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import kr.co.sist.corp.dto.InterviewOfferDTO;
import kr.co.sist.corp.dto.MessageDTO;
import kr.co.sist.corp.dto.ResumeScrapDTO;
import kr.co.sist.corp.dto.TalentFilterDTO;
import kr.co.sist.corp.dto.TalentPoolDTO;
import kr.co.sist.corp.mapper.TalentPoolMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TalentPoolServiceImpl implements TalentPoolService {
	
	@Autowired
	private final TalentPoolMapper talentPoolMapper;
	
	//스크랩 인재 리스트
  @Override
  public List<TalentPoolDTO> getScrappedTalents(Long corpNo, int offset, int size, String sortBy, String order) {
  	List<TalentPoolDTO> talents = talentPoolMapper.selectPaginatedScrappedTalents(corpNo, offset, size,sortBy,order);

    // 만 나이 계산
    for (TalentPoolDTO dto : talents) {
        String birthDateStr = dto.getBirth();  
        
        if (birthDateStr != null && !birthDateStr.isEmpty()) {
          try {
              // 날짜 형식에 맞는 패턴으로 파싱
              DateTimeFormatter formatter = birthDateStr.contains("-") 
                  ? DateTimeFormatter.ofPattern("yyyy-MM-dd") 
                  : DateTimeFormatter.ofPattern("yyyy.MM.dd");

              LocalDate birthDate = LocalDate.parse(birthDateStr, formatter);
              int age = Period.between(birthDate, LocalDate.now()).getYears();

              // 만 나이 표시
              dto.setBirth("만 " + age + "세");
          } catch (Exception e) {
              dto.setBirth(null);
          }
      } else {
          dto.setBirth(null);
      }//end else
        
        //학력 미기재시 중졸처리
        if (dto.getFinalEducation() == null || dto.getFinalEducation().isEmpty()) {
          dto.setFinalEducation("중학교 (졸업)");
      }//end if
        
     // 경력 처리
        if (dto.getTotalCareer() == null || dto.getTotalCareer().isEmpty() ) {
       	 		dto.setTotalCareer(null);
        } else {
            try {
                int totalMonths = Integer.parseInt(dto.getTotalCareer()); 
                int years = totalMonths / 12; 
                int months = totalMonths % 12;

                StringBuilder careerDisplay = new StringBuilder();
                if (years > 0) { careerDisplay.append(years).append("년");}
                if (months > 0) {
                    if (years > 0) {
                        careerDisplay.append(" ");
                    }
                    careerDisplay.append(months).append("개월");
                }//end if
                dto.setTotalCareer(careerDisplay.toString());
            } catch (NumberFormatException e) {
                dto.setTotalCareer(null); 
            }//end catch
        } // end if
        
       // educationType 필터링(대학나온 사람만 표시)
        String educationType = dto.getEducationType(); 
        if (educationType == null || !educationType.contains("대")) {
          dto.setEducationType(""); 
      }//end if
        
  }//end for
        return talents;
  }//getScrappedTalents
  
  //스크랩 인재 총 건수
  @Override
  public int getScrappedTalentsTotalCount(Long corpNo) {
      return talentPoolMapper.getScrappedTalentsTotalCount(corpNo);
  }//getScrappedTalentsCount
  
  
  //최근본인재탭
  @Override
  public List<Integer> getRecentlyViewedResumes(Long corpNo, int startRow, int endRow) {
      Map<String, Object> params = new HashMap<>();
      params.put("corpNo", corpNo);
      params.put("startRow", startRow);
      params.put("endRow", endRow);
      List<Integer> result = talentPoolMapper.getRecentlyViewedResumes(params);

      System.out.println("최근 열람 이력서 번호들 (corpNo=" + corpNo + "): " + result);

      return talentPoolMapper.getRecentlyViewedResumes(params);
  }
  //최근본 인재 리스트 인재 정보
  @Override
  public List<TalentPoolDTO> getResumeDetailsBySeqs(List<Integer> resumeSeqs, String sortBy, String order) {
    resumeSeqs = resumeSeqs.stream().distinct().collect(Collectors.toList());

    
    List<TalentPoolDTO> talents = talentPoolMapper.selectResumeMemberInfo(resumeSeqs, sortBy, order);
    
    System.out.println("이력서 상세 정보 조회 결과 개수: " + talents.size());
    System.out.println("입력된 resumeSeqs: " + resumeSeqs);
    
    // 만 나이 계산
    for (TalentPoolDTO dto : talents) {
        String birthDateStr = dto.getBirth(); 
        
        if (birthDateStr != null && !birthDateStr.isEmpty()) {
          try {
              // 날짜 형식에 맞는 패턴으로 파싱
              DateTimeFormatter formatter = birthDateStr.contains("-") 
                  ? DateTimeFormatter.ofPattern("yyyy-MM-dd") 
                  : DateTimeFormatter.ofPattern("yyyy.MM.dd");

              LocalDate birthDate = LocalDate.parse(birthDateStr, formatter);
              int age = Period.between(birthDate, LocalDate.now()).getYears();

              // 만 나이 표시
              dto.setBirth("만 " + age + "세");
          } catch (Exception e) {
              dto.setBirth(null);  
          }
      } else {
          dto.setBirth(null);
      }//end else
        
        //학력 미기재시 중졸처리
        if (dto.getFinalEducation() == null || dto.getFinalEducation().isEmpty()) {
          dto.setFinalEducation("중학교 (졸업)");
      }//end if
        
     // 경력 처리
        if (dto.getTotalCareer() == null || dto.getTotalCareer().isEmpty()) {
        	 dto.setTotalCareer(null);
        } else {
            try {
                int totalMonths = Integer.parseInt(dto.getTotalCareer()); 
                int years = totalMonths / 12; 
                int months = totalMonths % 12;

                StringBuilder careerDisplay = new StringBuilder();
                if (years > 0) { careerDisplay.append(years).append("년");}
                if (months > 0) {
                    if (years > 0) {
                        careerDisplay.append(" ");
                    }
                    careerDisplay.append(months).append("개월");
                }//end if
                dto.setTotalCareer(careerDisplay.toString());
            } catch (NumberFormatException e) {
                dto.setTotalCareer(null); 
            }//end catch
        } // end if
        
       // educationType 필터링(대학나온 사람만 표시)
        String educationType = dto.getEducationType(); 
        if (educationType == null || !educationType.contains("대")) {
          dto.setEducationType(""); 
      }//end if
        
  }//end for
        return talents;  
  }

  //최근본인재 총 건수
  @Override
  public int getRecentlyViewedTotalCount(Long corpNo) {
      return talentPoolMapper.getRecentlyViewedTotalCount(corpNo);
  }
  
//필터링된 전체 인재 리스트 (TalentFilterDTO 사용)
  @Override
  public List<TalentPoolDTO> getPaginatedTalents(TalentFilterDTO filterDTO) {
      List<TalentPoolDTO> talents = talentPoolMapper.selectAllTalents(filterDTO);

      // 만 나이, 학력, 경력 처리 로직 추가 (getScrappedTalents와 동일)
      for (TalentPoolDTO dto : talents) {
          String birthDateStr = dto.getBirth();
          if (birthDateStr != null && !birthDateStr.isEmpty()) {
              try {
                  DateTimeFormatter formatter = birthDateStr.contains("-")
                          ? DateTimeFormatter.ofPattern("yyyy-MM-dd")
                          : DateTimeFormatter.ofPattern("yyyy.MM.dd");
                  LocalDate birthDate = LocalDate.parse(birthDateStr, formatter);
                  int age = Period.between(birthDate, LocalDate.now()).getYears();
                  dto.setBirth("만 " + age + "세");
              } catch (Exception e) {
                  System.err.println("Error parsing birth date: " + birthDateStr + " - " + e.getMessage());
                  dto.setBirth(null);
              }
          } else {
              dto.setBirth(null);
          }

          if (dto.getFinalEducation() == null || dto.getFinalEducation().isEmpty()) {
              dto.setFinalEducation("중학교 (졸업)");
          }

          if (dto.getTotalCareer() == null || dto.getTotalCareer().isEmpty()) {
              dto.setTotalCareer(null);
          } else {
              try {
                  int totalMonths = Integer.parseInt(dto.getTotalCareer());
                  int years = totalMonths / 12;
                  int months = totalMonths % 12;

                  StringBuilder careerDisplay = new StringBuilder();
                  if (years > 0) { careerDisplay.append(years).append("년"); }
                  if (months > 0) {
                      if (years > 0) { careerDisplay.append(" "); }
                      careerDisplay.append(months).append("개월");
                  }
                  dto.setTotalCareer(careerDisplay.toString());
              } catch (NumberFormatException e) {
                  dto.setTotalCareer(null);
              }
          }

          String educationType = dto.getEducationType();
          if (educationType == null || !educationType.contains("대")) {
              dto.setEducationType("");
          }
      }
      return talents;
  }
  //전체인재 총 건수 
  @Override
  public int selectAllTalentTotalCount(TalentFilterDTO filterDTO) {
      return talentPoolMapper.selectAllTalentTotalCount(filterDTO);
  }
  
	//이력서 스크랩
	@Override
	public String scrapResume(Long resumeSeq, Long corpNo) {
	    int exists = talentPoolMapper.checkScrapExists(resumeSeq, corpNo);
	    
	    ResumeScrapDTO sDTO = new ResumeScrapDTO();
	    sDTO.setResumeSeq(resumeSeq);
	    sDTO.setCorpNo(corpNo);

	    int result;
	    if (exists > 0) {
	        result = talentPoolMapper.deleteScrap(resumeSeq, corpNo);
	        return result > 0 ? "scrap_cancel" : "scrap_failed";
	    } else {
	        sDTO.setIsScrapped("Y");
	        result = talentPoolMapper.insertScrap(sDTO);
	        return result > 0 ? "scrap_success" : "scrap_failed";
	    }
	}//scrapResume
	

	//면접 제안
  @Override
  public void sendInterviewOffer(InterviewOfferDTO ioDTO) {
      try {
          // 이력서 번호로 구직자 정보 조회
          TalentPoolDTO talent = talentPoolMapper.selectResumeInfo(ioDTO.getResumeSeq());
          String resumeEmail = talent.getEmail();

          ioDTO.setEmail(resumeEmail);
          //면접제안 메세지
          talentPoolMapper.insertInterviewProposal(ioDTO);

      } catch (Exception e) {
          throw new RuntimeException("면접 제안 전송 중 오류 발생", e);
      }
  }//sendInterviewOffer
  
  //면접 제안시 필요한 기업 정보 조회
  @Override
  public InterviewOfferDTO getCorpInfoByCorpNo(Long corpNo) {
      return talentPoolMapper.getCorpInfoByCorpNo(corpNo);
  }
  
  //이력서 상세보기
  @Override
  public TalentPoolDTO selectResumeDetail(int resumeNo) {
      TalentPoolDTO dto = talentPoolMapper.selectResumeDetail(resumeNo);
      return dto;
  }

  //이력서 열람
  @Override
  @Transactional
  public void viewResume(Long resumeSeq, Long corpNo) {
      try {
      	
          //이력서열람여부 확인
          int count = talentPoolMapper.checkResumeViewExist(resumeSeq, corpNo);

          if (count == 0) {
              //이력서 열람 기록
              talentPoolMapper.insertResumeViewLog(resumeSeq, corpNo);

              // 이력서 작성자 정보 조회
              TalentPoolDTO resumeOwner = talentPoolMapper.selectResumeInfo(resumeSeq);
              if (resumeOwner == null || resumeOwner.getEmail() == null) {
                  throw new IllegalArgumentException("이력서 작성자 정보를 찾을 수 없습니다.");
              }

              // 프로필 이미지 경로 처리
              String profileImgPath = resumeOwner.getProfileImg();
              if (profileImgPath == null || profileImgPath.trim().isEmpty()) {
                  profileImgPath = " ";
              } else {
                  if (!profileImgPath.startsWith("/images/")) {
                      profileImgPath = "/images/profileImg/" + profileImgPath;
                  }
              }
              
              InterviewOfferDTO corpInfo = talentPoolMapper.getCorpInfoByCorpNo(corpNo);
              if (corpInfo == null || corpInfo.getCorpName() == null) {
                throw new IllegalArgumentException("기업 정보가 잘못되었습니다.");
            }
              String corpName = corpInfo.getCorpName();
              String resumeTitle = resumeOwner.getTitle();
              String mailContent = resumeOwner.getName() + " 님, " + corpName +" 기업에서 귀하의 "+resumeTitle+" 이력서를 열람했습니다.";

              MessageDTO mail = new MessageDTO();
              mail.setCorpNo(corpNo);
              mail.setEmail(resumeOwner.getEmail());
              mail.setMessageTitle("["+corpName+"]에서 내 이력서를 열람하였습니다.");
              mail.setMessageContent(mailContent);
              mail.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
              mail.setIsRead("N");
              mail.setReadedAt("0");
              mail.setIsOffered("N");

              //열람 메세지(알람) 보내기
              talentPoolMapper.insertMessage(mail);
          }
      } catch (Exception e) {
          e.printStackTrace();
          throw new RuntimeException("이력서 열람 및 메일 발송 처리 중 오류가 발생했습니다.", e);
      }
  }
	
}//class
