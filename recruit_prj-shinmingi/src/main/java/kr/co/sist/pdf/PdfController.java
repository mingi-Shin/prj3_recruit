package kr.co.sist.pdf;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.pdf.BaseFont;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PdfController {

	private final PdfService pdfService;

	@RequestMapping("/index")
	public void index(HttpServletResponse response) {

		Map<String, Object> map = Map.of("title", "Yuki Coding Hello", "message", "안녕하세요~~");

		try {

			String processHtml = pdfService.createPdf("index", map);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ITextRenderer renderer = new ITextRenderer();

			renderer.getFontResolver().addFont(new ClassPathResource("/static/font/NanumBarunGothic.ttf").getURL().toString(),
					BaseFont.IDENTITY_H, BaseFont.EMBEDDED

			);
			renderer.setDocumentFromString(processHtml);

			renderer.layout();
			renderer.createPDF(outputStream);
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", " attachment; filename=\"yuki.pdf\"");

			response.getOutputStream().write(outputStream.toByteArray());
			response.getOutputStream().flush();

			renderer.finishPDF();
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
