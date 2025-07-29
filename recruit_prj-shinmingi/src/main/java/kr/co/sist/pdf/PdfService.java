package kr.co.sist.pdf;

import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PdfService {

	private final TemplateParser templateParser;
	
	public String createPdf(String thymeleafHtml, Map<String, Object> map) {
		String processHtml = templateParser.parseHtmlFileToString(thymeleafHtml, map);
		
		return processHtml;
	}
}
