package com.blue.hosting.controller;

import com.blue.hosting.entity.account.AccountInfoDTO;
import com.blue.hosting.entity.account.AccountInfoVO;
import com.blue.hosting.security.authentication.account.JwtCertificationToken;
import com.blue.hosting.utils.PageIndex;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class mainController {
	@RequestMapping("/")
	public String index(Model model, @AuthenticationPrincipal JwtCertificationToken jwtCertificationToken
			, HttpServletRequest request, HttpServletResponse response) {
		if(jwtCertificationToken != null) {
			model.addAttribute("username", jwtCertificationToken.getPrincipal());
		}

		return PageIndex.INDEX;
	}
}
