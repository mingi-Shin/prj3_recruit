package kr.co.sist.pdf;

import java.io.InputStream;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.xhtmlrenderer.extend.FSImage;
import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.extend.ReplacedElementFactory;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.pdf.ITextFSImage;
import org.xhtmlrenderer.pdf.ITextImageElement;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.extend.FormSubmissionListener;

import com.lowagie.text.Image;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ImgReplaceElementFactory implements ReplacedElementFactory {
    
    private final ResourceLoader resourceLoader;
    
    @Override
    public ReplacedElement createReplacedElement(LayoutContext layoutContext, BlockBox blockBox,
            UserAgentCallback userAgentCallback, int cssWidth, int cssHeight) {
        
        Element e = blockBox.getElement();
        if (e == null) {
            return null;
        }
        
        String nodeName = e.getNodeName();
        if (nodeName.equals("img")) {
            String attribute = e.getAttribute("src");
            FSImage fsImage;
            
            try {
                fsImage = buildImage(attribute, userAgentCallback);
            } catch (Exception e1) {
                e1.printStackTrace();
                fsImage = null;
            }
            
            if(fsImage != null) {
                if(cssWidth != -1 || cssHeight != -1) {
                    fsImage.scale(cssWidth, cssHeight);
                }
                return new ITextImageElement(fsImage);
            }
        }
        
        return null;
    }

    @Override
    public void reset() {
        // Auto-generated method stub
    }

    @Override
    public void remove(Element e) {
        // Auto-generated method stub
    }

    @Override
    public void setFormSubmissionListener(FormSubmissionListener listener) {
        // Auto-generated method stub
    }
    
    private FSImage buildImage(String srcAttr, UserAgentCallback uac) {
        System.out.println("이미지 src 속성: " + srcAttr); // 디버깅용
        
        if (srcAttr == null || srcAttr.isEmpty()) {
            System.out.println("이미지 src가 null 또는 empty입니다.");
            return null;
        }
        
        try {
            String fileName = null;
            
            // 절대 경로로 시작하는 경우 처리
            if (srcAttr.startsWith("/images/profileImg/")) {
                fileName = srcAttr.substring("/images/profileImg/".length());
            }
            // 상대 경로인 경우
            else if (srcAttr.contains("/images/profileImg/")) {
                int index = srcAttr.indexOf("/images/profileImg/");
                fileName = srcAttr.substring(index + "/images/profileImg/".length());
            }
            // 파일명만 있는 경우
            else {
                fileName = srcAttr;
            }
            
            System.out.println("추출된 파일명: " + fileName); // 디버깅용
            return loadImageFromClasspath(fileName);
            
        } catch (Exception e) {
            System.err.println("이미지 로드 실패: " + srcAttr);
            e.printStackTrace();
            return null;
        }
    }
    
    private FSImage loadImageFromClasspath(String fileName) throws Exception {
        String resourcePath = "classpath:static/images/profileImg/" + fileName;
        Resource resource = resourceLoader.getResource(resourcePath);
        
        System.out.println("리소스 경로: " + resourcePath); // 디버깅용
        
        if (!resource.exists()) {
            System.err.println("이미지 파일을 찾을 수 없습니다: " + resourcePath);
            return null;
        }
        
        try (InputStream inputStream = resource.getInputStream()) {
            byte[] imageBytes = inputStream.readAllBytes();
            Image iTextImage = Image.getInstance(imageBytes);
            return new ITextFSImage(iTextImage);
        }
    }
}