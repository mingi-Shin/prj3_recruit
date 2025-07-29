package kr.co.sist.corp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CorpProductController {

	@GetMapping("/goCorpInfoPage")
	public String getCorpInfoPage() {
		return "corp/mingi/getCorpInfo";
	}
}
