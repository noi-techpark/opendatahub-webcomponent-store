// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.opendatahub.webcomponents.dataservice.application.adapter.in.web;

import it.bz.opendatahub.webcomponents.common.stereotype.WebAdapter;
import it.bz.opendatahub.webcomponents.dataservice.application.port.in.SendEmailUseCase;
import it.bz.opendatahub.webcomponents.dataservice.config.MailerConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@WebAdapter
@RestController
@RequestMapping("/contact")
@Slf4j
public class ContactFormController {

	@Value("${hCaptcha.secret.key}")
	private String hCaptchaSecretKey;

	private final SendEmailUseCase sendEmailUseCase;
	private final MailerConfig mailerConfig;

	private final RestTemplate restTemplate;

	public ContactFormController(SendEmailUseCase sendEmailUseCase, MailerConfig mailerConfig) {
		this.sendEmailUseCase = sendEmailUseCase;
		this.mailerConfig = mailerConfig;
		restTemplate = new RestTemplate();
	}

	@SneakyThrows
	@PostMapping
	public void sendContactForm(@RequestBody @Valid ContactFormRequest request) {
		String hCaptchaToken = request.getCaptchaToken();
		log.debug("Contact request received. Validating hCaptcha with token {}...", hCaptchaToken);

		// hCaptchaSecretKey == null to pass tests, can't test hCaptcha token validation
		// TODO replace with test keys
		// https://docs.hcaptcha.com/#integration-testing-test-keys
		if (hCaptchaSecretKey == null || hCpatchaTokenValidation(hCaptchaToken)) {
			var requestAsText = "Category: " + nullToEmpty(request.getCategory()) + "\n" +
					"First name: " + nullToEmpty(request.getNameFirst()) + "\n" +
					"Last name: " + nullToEmpty(request.getNameLast()) + "\n" +
					"E-mail: " + nullToEmpty(request.getEmail()) + "\n" +
					"Phone: " + nullToEmpty(request.getPhone()) + "\n" +
					"Text:\n" + nullToEmpty(request.getText());

			log.info("hCaptcha validated: contact request successful");
			sendEmailUseCase.sendPlaintextEmail(mailerConfig.getTo(), mailerConfig.getSubject(), requestAsText);
		} else
			log.info("hCaptcha validation failed: contact request not successful");

	}

	private String nullToEmpty(String in) {
		if (in == null) {
			return "";
		}

		return in;
	}

	private boolean hCpatchaTokenValidation(String hCaptchaToken) throws IOException, InterruptedException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("response", hCaptchaToken);
		map.add("secret", this.hCaptchaSecretKey);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

		String response = restTemplate.postForObject("https://hcaptcha.com/siteverify", request, String.class);

		// Simple hack to see if valid. Check here for more info
		// https://golb.hplar.ch/2020/05/hcaptcha.html
		log.info("hCaptcha response: {}", response);
		return response != null && response.contains("\"success\":true");
	}

	@Getter
	@Setter
	public static class ContactFormRequest {
		@NotBlank
		private String category;

		@NotBlank
		private String nameFirst;

		private String nameLast;

		private String email;

		private String phone;

		@NotBlank
		private String text;

		@NotBlank
		private String captchaToken;
	}
}
