package kr.co.sist.admin;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class AdminDTO {
    private String adminId;
    private String password;
    private String name;
    private String dept;
    private String job;
    private String tel;

    private String deptRole;  // 화면 제어용 권한
    private String jobRole;   // 기능 제어용 권한
    
    
  	
    // Entity → DTO 변환 메서드
    public static AdminDTO from(AdminEntity entity) {
    	
      return AdminDTO.builder()
          .adminId(entity.getAdminId())
          .name(entity.getName())
          .password(entity.getPassword())
          .dept(entity.getDept())
          .job(entity.getJob())
          .tel(entity.getTel())
          .deptRole(entity.getDeptRole())
          .jobRole(entity.getJobRole())
          .build();
    }


}
