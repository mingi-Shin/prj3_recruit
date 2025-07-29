// DTO: kr.co.sist.corp.dto.TalentFilterDTO.java
package kr.co.sist.corp.dto;

import lombok.Data;
import lombok.NoArgsConstructor; // 기본 생성자 추가 (Lombok @Data는 기본 생성자를 포함하지만 명시적으로 추가해도 좋음)

@Data
@NoArgsConstructor // Lombok 사용 시 기본 생성자 자동 추가
public class TalentFilterDTO {
    private String keyword;
    private String gender;
    private String career;
    private Integer minCareerMonths;
    private Integer maxCareerMonths;
    private String address;
    private String education;
    private String techStack;
    private String position;

    private String sortBy;
    private String order;
    private int offset;
    private int size;
    private long corpNo; // <-- 여기를 Long 타입으로 변경!

    // career 필드 setter 오버라이드 (이전과 동일하게 유지)
    public void setCareer(String career) {
        this.career = career;
        if (career != null && !career.isEmpty()) {
            String[] parts = career.split("-");
            if (parts.length == 2) {
                try {
                    this.minCareerMonths = Integer.parseInt(parts[0]);
                    this.maxCareerMonths = Integer.parseInt(parts[1]);
                } catch (NumberFormatException e) {
                    this.minCareerMonths = null;
                    this.maxCareerMonths = null;
                    System.err.println("Invalid career format: " + career);
                }
            } else if (parts.length == 1 && career.equals("0-0")) {
                this.minCareerMonths = 0;
                this.maxCareerMonths = 0;
            }
        } else {
            this.minCareerMonths = null;
            this.maxCareerMonths = null;
        }
    }
}