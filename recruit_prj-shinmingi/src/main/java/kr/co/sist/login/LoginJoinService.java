package kr.co.sist.login;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestAttribute;

import kr.co.sist.corp.dto.CorpEntity;
import kr.co.sist.globalController.Exceptions.LoginException;
import kr.co.sist.user.dto.UserDTO;
import kr.co.sist.user.entity.UserEntity;
import kr.co.sist.util.CipherUtil;

@Service
public class LoginJoinService {

    private final UserRepository ur;
    private final CorpRepository cr;
    private final CipherUtil cu;
    
    @Autowired
    private final BCryptPasswordEncoder passwordEncoder; 

    public LoginJoinService(UserRepository ur, CorpRepository cr, CipherUtil cu, BCryptPasswordEncoder passwordEncoder) {
        this.ur = ur;
        this.cr = cr;
        this.cu = cu;
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * 유저인증 (이메일, 비밀번호)
     */
    public UserDTO authenticate(String mail, String password) {
    	UserEntity user = ur.findById(mail).orElseThrow(() -> new LoginException("사용자가 존재하지 않음"));
    	if(!passwordEncoder.matches(password, user.getPassword())) {
    		throw new LoginException("비밀번호가 일치하지 않습니다. 어디로 가야하죠~ 아저씨 우는 손님이 처음인 가요~~");
    	}
    	
    	UserDTO uDTO = UserDTO.from(user); //Entity는 보안문제상 DTO로 변환하여 반환
    	return uDTO;
    }

    /**
     * 유저 회원가입
     * @param uDTO
     * @return
     */
    @Transactional
    public UserEntity registerUser(UserDTO uDTO) {
        UserEntity ue = new UserEntity();
        
        try {
            // 기본 정보 설정
            ue.setEmail(uDTO.getEmail());
            ue.setCorpEntity(null);
            ue.setPassword(cu.hashText(uDTO.getPassword()));
            ue.setName(uDTO.getName());
            ue.setRole("ROLE_USER");
            ue.setPhone(cu.encryptText(uDTO.getPhone()));
            
            // 현재 시간 설정
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            ue.setRegDt(now.format(dtf));
            
            // 로그인 관련 초기값 설정
            ue.setLoginFailCnt(0);
            ue.setIsLocked(0);
            
            // lockEndDt (Date -> String 변환, null 체크)
            if (uDTO.getLockEndDt() != null) {
                ue.setLockEndDt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(uDTO.getLockEndDt()));
            } else {
                ue.setLockEndDt(null);
            }
            
            // pwChangeDt (Date -> String 변환, null 체크)
            if (uDTO.getPwChangeDt() != null) {
                ue.setPwChangeDt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(uDTO.getPwChangeDt()));
            } else {
                ue.setPwChangeDt(null);
            }
            
            // lastLoginDt (Date -> String 변환, null 체크)
            if (uDTO.getLastLoginDt() != null) {
                ue.setLastLoginDt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(uDTO.getLastLoginDt()));
            } else {
                ue.setLastLoginDt(null);
            }
            
            ue.setLastLoginIp(null);
            
            // 주소 정보 설정
            ue.setZipcode(uDTO.getZipcode());
            ue.setRoadAddress(uDTO.getRoadAddress());
            ue.setDetailAddress(uDTO.getDetailAddress());
            
            // 생년월일 변환
            if (uDTO.getBirth() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String birthStr = sdf.format(uDTO.getBirth());
                ue.setBirth(birthStr);
            } else {
              ue.setBirth(null);
            }
            
            // 기타 정보 설정
            ue.setGender(uDTO.getGender());
            ue.setPwResetRequired(0);
            ue.setProfileImage(null);
            ue.setActiveStatus(0);
            
            // 저장 전 검증
           // System.out.println("저장 전 UserEntity: " + ue);
            //System.out.println("UserRepository 존재 여부: " + (ur != null));
            
            // DB 저장 시도
            //System.out.println("저장 시도 시작...");
            UserEntity savedEntity = ur.save(ue);
            //System.out.println("저장 완료!");
            
            if (savedEntity != null) {
                //System.out.println("저장 후 UserEntity: " + savedEntity);
                return savedEntity;
                //Entity보다 DTO를 반환하는게 맞는데, 귀찮으니까 나중에 
            } else {
                //System.err.println("저장 후 반환된 Entity가 null입니다!");
                throw new RuntimeException("저장 후 Entity가 null로 반환됨");
            }
            
        } catch (Exception e) {
            //System.err.println("=== 회원가입 오류 상세 정보 ===");
            //System.err.println("오류 메시지: " + e.getMessage());
            //System.err.println("오류 타입: " + e.getClass().getSimpleName());
            
            if (e.getCause() != null) {
                System.err.println("원인: " + e.getCause().getMessage());
            }
            
            e.printStackTrace();
            
            // 더 구체적인 오류 정보 출력
            if (e.getMessage() != null) {
                if (e.getMessage().contains("constraint")) {
                    System.err.println("제약조건 위반 가능성이 있습니다.");
                } else if (e.getMessage().contains("duplicate")) {
                    System.err.println("중복 데이터 오류 가능성이 있습니다.");
                } else if (e.getMessage().contains("null")) {
                    System.err.println("NULL 값 오류 가능성이 있습니다.");
                }
            }
            //runtime오류를 내야 트랜잭션이 롤백된다.
            throw new RuntimeException("회원 저장 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * 기업회원 회원가입
     * 예외가 발생했을 때 메서드 밖으로 던져져야 Spring이 트랜잭션을 롤백할 수 있다. try catch 안 돼!
     * IOException, SQLException, Exception 같은 예외는 @Transactional이 자동 롤백하지 않음.
     * @param ucDTO
     * @param bizCertName
     * @return
     */
    @Transactional
    public CorpEntity registerCorp(@RequestAttribute UserCorpDTO ucDTO, String bizCertName) {

    	//사업자등록번호 중복 조회
    	if(cr.existsById(ucDTO.getCorpNo())){
    		//가입 내역이 이미 존재한다.
    		throw new IllegalStateException("이미 등록된 사업자 등록번호입니다.");
    	}
    	
        // 기업정보 기입
        CorpEntity ce = new CorpEntity();
        ce.setCorpNo(ucDTO.getCorpNo()); //사업자번호
        ce.setCorpNm(ucDTO.getCorpNm()); //회사이름
        ce.setCorpCeo(ucDTO.getCorpCeo()); //대표이름
        ce.setIndustry(ucDTO.getIndustry()); //업종
        ce.setCorpCreatedAt(ucDTO.getCorpCreatedAt());
        //ce.setCorpAnnualRevenue(ucDTO.getCorpAnnualRevenue()); //연매출
        //ce.setCorpAvgSal(ucDTO.getCorpAvgSal()); //연봉
        //ce.setCorpEmpCnt(ucDTO.getCorpEmpCnt()); //사원수
        ce.setBizCert(bizCertName); //사업자등록증명원 파일 이름
        ce.setCorpAiActive("N"); //AI기능 지원 여부인데, 나중에 결제시스템 도입하면 유의미해짐
        
        // 저장 전 검증
        //System.out.print("저장 전 CorpEntity: ");
        //System.out.println(ce);
        //System.out.println("CorpRepository 존재 여부: " + (cr != null));
        
        // DB 저장 - try-catch 제거
        cr.save(ce);
        //System.out.println("corpEntity 저장 완료!");
        
        // 기본정보 기입
        UserEntity ue = new UserEntity();
        
        if(ur.existsById(ucDTO.getEmail())) {
        	throw new IllegalStateException("이미 등록된 이메일입니다.");
        }
        
        ue.setEmail(ucDTO.getEmail());
        
        //corpNo로 기업객체 찾기
        CorpEntity corp = cr.findById(ucDTO.getCorpNo()).orElseThrow(() -> new IllegalArgumentException("기업이 존재하지 않습니다."));
        ue.setCorpEntity(corp);
        
        //디버깅
        //System.out.println(ue.getCorpEntity());
        
        ue.setPassword(cu.hashText(ucDTO.getPassword()));
        //ue.setName(cu.cipherText(ucDTO.getName()));
        ue.setRole("ROLE_CORP");
        //ue.setPhone(cu.cipherText(ucDTO.getPhone()));
        
        // 현재 시간 설정
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ue.setRegDt(now.format(dtf));
        
        // 로그인 관련 초기값 설정
        ue.setLoginFailCnt(0);
        ue.setIsLocked(0);
        
        // lockEndDt (Date -> String 변환, null 체크)
        if (ucDTO.getLockEndDt() != null) {
            ue.setLockEndDt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ucDTO.getLockEndDt()));
        } else {
            ue.setLockEndDt(null);
        }
        
        // pwChangeDt (Date -> String 변환, null 체크)
        if (ucDTO.getPwChangeDt() != null) {
            ue.setPwChangeDt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ucDTO.getPwChangeDt()));
        } else {
            ue.setPwChangeDt(null);
        }
        
        // lastLoginDt (Date -> String 변환, null 체크)
        if (ucDTO.getLastLoginDt() != null) {
            ue.setLastLoginDt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ucDTO.getLastLoginDt()));
        } else {
            ue.setLastLoginDt(null);
        }
        
        ue.setLastLoginIp(null);
        
        // 주소 정보 설정
        ue.setZipcode(ucDTO.getRoadAddress().split(", ")[0]);
        ue.setRoadAddress(ucDTO.getRoadAddress().split(", ")[1]);
        ue.setDetailAddress(ucDTO.getDetailAddress());
        
        // 기타 정보 설정
        ue.setPwResetRequired(0);
        ue.setActiveStatus(0);
        
        // 저장 전 검증
        //System.out.println("저장 전 UserEntity: " + ue);
        //System.out.println("UserRepository 존재 여부: " + (ur != null));
        
        // DB 저장 - try-catch 제거
        UserEntity savedEntity = ur.save(ue);
        //System.out.println("ue 저장 완료!");
        
        return ce;
    }
    
     
    //이메일 중복 체크
    public boolean chkEmailDupl(String email) {
    	return ur.existsById(email);
    }
    
    public AccountRecoveryDTO findByNameAndPhone(String name, String phone) {
      // UserRepository에서 이름과 전화번호로 조회 (암호화된 phone 처리 필요할 수 있음)
      Optional<UserEntity> userOpt = ur.findByNameAndPhone(name, phone);

      if (userOpt.isPresent()) {
          UserEntity user = userOpt.get();
          AccountRecoveryDTO dto = new AccountRecoveryDTO();
          dto.setEmail(user.getEmail());
          // 필요한 필드 추가 설정
          return dto;
      }
      return null;
  }
    @Transactional
    public UserEntity updatePassword(UserDTO uDTO) {
        // 이메일로 유저 조회
        UserEntity user = ur.findById(uDTO.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

        // 새 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(uDTO.getPassword());

        // 비밀번호 업데이트
        user.setPassword(encodedPassword);

        // 필요 시 pwChangeDt 등 다른 필드도 갱신 가능

        // 저장
        return ur.save(user);
    }
    
}