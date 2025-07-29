package kr.co.sist.corp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CorpMainController {

  @GetMapping("/corp/template")
  public String goCorpTemplate() {
    return "/corp/corp_template";
  }

}
