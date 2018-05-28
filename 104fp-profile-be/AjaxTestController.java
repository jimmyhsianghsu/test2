package com.e104.profile.be.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;

@RestController
@RequestMapping("/test")
public class AjaxTestController {
	private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	private final HttpUrl labUrl = HttpUrl.parse("https://c1.profile.104-dev.com.tw/");

	@RequestMapping(value="/lab", method= RequestMethod.POST)
	public Object ajaxLab(@RequestParam String url, @RequestParam String type, @RequestParam final int pid, @RequestBody(required = false) String json){
		OkHttpClient client = new OkHttpClient.Builder().cookieJar(new CookieJar(){
			@Override
			public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {}
			@Override
			public List<Cookie> loadForRequest(HttpUrl url) {
				List<Cookie> cookies = new ArrayList<>();
				cookies.add(new Cookie.Builder().hostOnlyDomain(labUrl.host()).name("PI").value(Integer.toString(pid)).build());
				return cookies;
			}
		}).build();
		Builder builder = new Request.Builder().url(labUrl + url);
		Request request;
		if ("POST".equals(type)){
			request = builder.post(okhttp3.RequestBody.create(JSON, json)).build();
		} else if ("PUT".equals(type)){
			request = builder.put(okhttp3.RequestBody.create(JSON, json)).build();
		} else if ("DELETE".equals(type)){
			request = builder.delete().build();
		} else {
			request = builder.get().build();
		}
		try {
			return new ObjectMapper().readTree(client.newCall(request).execute().body().string());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@javax.annotation.Resource
	private com.e104.profile.be.saml.SamlMessageProccessor samlMessageProccessor;
	@RequestMapping("/saml")
	public Object saml(@RequestParam("SAMLResponse") String SAMLResponse, @CookieValue("CS") String cs){
		Map<String, Object> map = new HashMap<>();
		map.put("SAMLResponse", SAMLResponse);
		map.put("CS", cs);
		map.put("pid", samlMessageProccessor.processResponseMessage(SAMLResponse).getSubject());
		return map;
	}

	@RequestMapping(value="/shaHex", method= RequestMethod.GET)
	public Object shaHex(@RequestParam String filePath){
		File file = new File(filePath);
		if (!file.exists())
			file = new File(".", filePath);
		return shaHex8192(file.getAbsolutePath());
	}
	private String shaHex8192(String filePath){
		java.security.MessageDigest sha = null;
		java.io.FileInputStream fis = null;
		java.io.BufferedInputStream bis = null;
		try {
			sha = java.security.MessageDigest.getInstance("SHA1");
			fis = new java.io.FileInputStream(filePath);
			bis = new java.io.BufferedInputStream(fis);
			byte[] temp = new byte[8192];
			int len;
			sha.update("blob ".getBytes());
			sha.update(Integer.toString(bis.available()).getBytes());
			sha.update("\0".getBytes());
			while ((len = bis.read(temp)) != -1)
				sha.update(temp, 0, len);
			return byteArrayToHexString(sha.digest());
		} catch (java.security.NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (bis != null)
				try {
					bis.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			try {
				fis.available();
				throw new RuntimeException("fis not closed!");
			} catch (IOException e) {
				System.out.println(String.format("Stream Closed = %s, %s", "Stream Closed".equals(e.getLocalizedMessage()), filePath));
			}
		}
	}
	private String byteArrayToHexString(byte[] b){
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < b.length; i++)
			builder.append(Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1));
		return builder.toString();
	}
}
