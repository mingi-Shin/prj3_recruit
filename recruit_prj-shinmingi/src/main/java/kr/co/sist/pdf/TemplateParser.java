package kr.co.sist.pdf;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TemplateParser {

	private final SpringTemplateEngine templateEngine;
	
	public String  parseHtmlFileToString(String templateName, Map<String, Object> vars) {
		
		Context context = new Context();
		context.setVariables(vars);
		
		return templateEngine.process(templateName, context);
	}
}
