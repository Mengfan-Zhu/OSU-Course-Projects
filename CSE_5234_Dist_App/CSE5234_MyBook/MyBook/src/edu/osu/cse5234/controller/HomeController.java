package edu.osu.cse5234.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class HomeController {
	@RequestMapping(method = RequestMethod.GET)
	public String printHome() throws Exception {
		return "home";
	}
	
	@RequestMapping(path="/AboutUs", method = RequestMethod.GET)
	public String aboutUs() throws Exception {
		return "AboutUs";
	}
	
	@RequestMapping(path="/ContactUs", method = RequestMethod.GET)
	public String contactUs() throws Exception {
		return "ContactUs";
	}
}
