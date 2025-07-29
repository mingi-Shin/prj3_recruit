package kr.co.sist.ai;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
public class GoogleAiController {

    private final OpenAiProperties openAiProperties;
    private final RestClient restClient;

    public GoogleAiController(OpenAiProperties openAiProperties, RestClient.Builder builder) {
        this.openAiProperties = openAiProperties;
        String baseUrl = openAiProperties.getChatBaseUrl();
        
        if (baseUrl == null) {
            throw new IllegalArgumentException("chatBaseUrl is not configured in properties");
        }
        
        String baseUrlWithSlash = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
        this.restClient = builder.baseUrl(baseUrlWithSlash).build();
    }

    @GetMapping("/models")
    public Map<String, Object> getModels() {
        ResponseEntity<Map> response = restClient.get()
                .uri("v1beta/openai/models")
                .header("Authorization", "Bearer " + openAiProperties.getApiKey())
                .retrieve()
                .toEntity(Map.class);
        return response.getBody();
    }

    //자기소개서 분석
    @PostMapping("/resume/analyze")
    public Map<String, Object> analyzeResume(@RequestBody Map<String, Object> resumeData) {
        // 사용자가 보낸 자소서 내용
        String userResume = (String) resumeData.get("content");
        
        // AI에게 실제로 보낼 명령어 (사용자는 이걸 모름)
        String aiPrompt = "다음 자기소개서를 전문적으로 분석하고 첨삭해주세요. " +
                         "1. 강점과 약점을 구체적으로 지적해주세요. " +
                         "2. 개선 방향을 제시해주세요. " +
                         "3. 더 매력적인 표현으로 수정 제안을 해주세요.\n\n" +
                         "자기소개서 내용:\n" + userResume;
        
        Map<String, Object> request = Map.of(
            "model", "gemini-2.0-flash",
            "messages", List.of(
                Map.of("role", "user", "content", aiPrompt)
            )
        );
        
        ResponseEntity<Map> response = restClient.post()
                .uri("v1beta/openai/chat/completions")
                .header("Authorization", "Bearer " + openAiProperties.getApiKey())
                .header("Content-Type", "application/json")
                .body(request)
                .retrieve()
                .toEntity(Map.class);
        
        return response.getBody();
    }
}