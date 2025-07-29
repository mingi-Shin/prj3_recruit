package kr.co.sist.corp.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import kr.co.sist.corp.dto.CorpEntity;
import kr.co.sist.login.CorpRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CorpImageService {

    private final CorpRepository corpRepos;

    // 파일 업로드 경로 (application.properties에서 설정 가능, 기본값 설정)
		/*
		 * @Value("${file.upload.logo.path:src/main/resources/static/images/corplogo}")
		 * private String logoUploadPath;
		 * 
		 * @Value("${file.upload.img.path:src/main/resources/static/images/corpimg}")
		 * private String imgUploadPath;
		 */
    
    //상대경로로 설정----------------------------------
    private String projectPath = new File("").getAbsolutePath();
    private String imagePath = projectPath + "/src/main/resources/static/images/corpimg";
    private String logoPath = projectPath + "/src/main/resources/static/images/corplogo";
    //--------------------------------------------

    // 허용되는 이미지 파일 확장자
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
        "jpg", "jpeg", "png", "gif", "bmp", "webp"
    );

    // 최대 파일 크기 (5MB)
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    /**
     * 회사 정보 조회
     */
    public CorpEntity getCorpInfo(Long corpNo) {
        return corpRepos.findById(corpNo).orElse(null);
    }

    /**
     * 로고 이미지 업로드/수정
     */
    public String uploadLogo(Long corpNo, MultipartFile logoFile) throws IOException {
        CorpEntity corp = corpRepos.findById(corpNo)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회사입니다."));

        validateImageFile(logoFile);

        // 기존 로고 파일 삭제
        if (StringUtils.hasText(corp.getCorpLogo())) {
            deleteExistingFile(corp.getCorpLogo(), logoPath);
        }

        // 새 파일 저장
        String savedFileName = saveFile(logoFile, "logo", logoPath);

        // DB 업데이트
        corp.setCorpLogo(savedFileName);
        corpRepos.save(corp);

        return savedFileName;
    }

    /**
     * 회사 이미지 업로드/수정
     */
    public String uploadCompanyImage(Long corpNo, MultipartFile imageFile) throws IOException {
        CorpEntity corp = corpRepos.findById(corpNo)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회사입니다."));

        validateImageFile(imageFile);

        // 기존 회사 이미지 삭제
        if (StringUtils.hasText(corp.getCorpImg())) {
            deleteExistingFile(corp.getCorpImg(), imagePath);
        }

        // 새 파일 저장
        String savedFileName = saveFile(imageFile, "company", imagePath);

        // DB 업데이트
        corp.setCorpImg(savedFileName);
        corpRepos.save(corp);

        return savedFileName;
    }

    /**
     * 로고 삭제
     */
    public void deleteLogo(Long corpNo) {
        CorpEntity corp = corpRepos.findById(corpNo)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회사입니다."));

        if (StringUtils.hasText(corp.getCorpLogo())) {
            deleteExistingFile(corp.getCorpLogo(), logoPath);
            corp.setCorpLogo(null);
            corpRepos.save(corp);
        }
    }

    /**
     * 회사 이미지 삭제
     */
    public void deleteCompanyImage(Long corpNo) {
        CorpEntity corp = corpRepos.findById(corpNo)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회사입니다."));

        if (StringUtils.hasText(corp.getCorpImg())) {
            deleteExistingFile(corp.getCorpImg(), imagePath);
            corp.setCorpImg(null);
            corpRepos.save(corp);
        }
    }

    /**
     * 이미지 파일 유효성 검사
     */
    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 선택되지 않았습니다.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("파일 크기는 5MB 이하여야 합니다.");
        }

        String originalFileName = file.getOriginalFilename();
        if (!StringUtils.hasText(originalFileName)) {
            throw new IllegalArgumentException("유효하지 않은 파일명입니다.");
        }

        String extension = getFileExtension(originalFileName).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("허용되지 않는 파일 형식입니다. (허용: jpg, jpeg, png, gif, bmp, webp)");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.");
        }
    }

    /**
     * 파일 저장
     */
    private String saveFile(MultipartFile file, String prefix, String directoryPath) throws IOException {
        createUploadDirectory(directoryPath);

        String originalFileName = file.getOriginalFilename();
        String extension = getFileExtension(originalFileName);
        String savedFileName = prefix + "_" + UUID.randomUUID().toString() + "." + extension;

        Path filePath = Paths.get(directoryPath, savedFileName);
        Files.copy(file.getInputStream(), filePath);

        return savedFileName;
    }

    /**
     * 기존 파일 삭제
     */
    private void deleteExistingFile(String fileName, String directoryPath) {
        if (StringUtils.hasText(fileName)) {
            try {
                Path filePath = Paths.get(directoryPath, fileName);
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                System.err.println("파일 삭제 실패: " + fileName);
            }
        }
    }

    /**
     * 업로드 디렉토리 생성
     */
    private void createUploadDirectory(String directoryPath) throws IOException {
        Path uploadDir = Paths.get(directoryPath);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
    }

    /**
     * 파일 확장자 추출
     */
    private String getFileExtension(String fileName) {
        if (StringUtils.hasText(fileName) && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        }
        return "";
    }
}