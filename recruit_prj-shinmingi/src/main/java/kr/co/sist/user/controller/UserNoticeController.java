package kr.co.sist.user.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import kr.co.sist.user.dto.NoticeDTO;
import kr.co.sist.user.service.UserNoticeService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UserNoticeController {
	
	private final UserNoticeService noticeService;
	
	@GetMapping("/user/job_posting/notice_list")
	public String getAllNotices(Model model) {
		
		List<NoticeDTO> allNotice=noticeService.getAllNotice();
		
		model.addAttribute("allNotice", allNotice);
		
		return "/user/job_posting/notice_list";
	}

}
