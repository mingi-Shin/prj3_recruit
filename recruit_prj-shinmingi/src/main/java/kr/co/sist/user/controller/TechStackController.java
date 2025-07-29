package kr.co.sist.user.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kr.co.sist.user.dto.TechStackDTO;
import kr.co.sist.user.service.TechStackService;

@Controller
public class TechStackController {

	private final TechStackService tss;
	
	public TechStackController(TechStackService tss) {
		this.tss = tss;
	}
	
	@GetMapping("/techStack/search")
    public ResponseEntity<List<TechStackDTO>> search(@RequestParam String keyword) {
        List<TechStackDTO> results = tss.searchInputTechStack(keyword);
        return ResponseEntity.ok(results);
    }
}
