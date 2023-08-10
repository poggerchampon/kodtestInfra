package com.infrasight.kodtest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ApiCaller {

	public String authenticate() {
		String ret = "";
		try {
			String apiUrl = "http://localhost:8080/api/auth";
			String jsonBody = "{\"user\": \"apiUser\",\"password\": \"apiPassword!\"}";

			URL url = new URL(apiUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			// Set headers
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setDoOutput(true);

			// Write JSON data to request body
			try (OutputStream os = connection.getOutputStream()) {
				byte[] input = jsonBody.getBytes("utf-8");
				os.write(input, 0, input.length);
			}

			int responseCode = connection.getResponseCode();
			System.out.println("Response Code: " + responseCode);

			BufferedReader in = new BufferedReader(
					new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
			String inputLine;
			StringBuilder response = new StringBuilder();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			ret = response.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}

	public String getData(Integer skip, Integer take, String filter, String endpoint) {
		String ret = "";
		try {
			String params = addParams(skip, take, filter);

			HttpURLConnection connection = (HttpURLConnection) new URI("http://localhost:8080" + endpoint + params)
					.toURL().openConnection();
			String token = tokenExtractor(authenticate());
			connection.setRequestProperty("accept", "application/json");
			connection.setRequestProperty("Authorization", "Bearer " + token);

			// Set request method (GET)
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			// Get the response code
			int responseCode = connection.getResponseCode();

			BufferedReader in = new BufferedReader(
					new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
			String inputLine;
			StringBuilder response = new StringBuilder();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			ret = response.toString();
			in.close();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}

	private String addParams(Integer skip, Integer take, String filter) {
		String params = "";

		if (skip != null || take != null || filter != null) {
			params += "?";

			if (skip != null) {
				params += "skip=" + skip;
			}
			if (take != null) {
				params += "take=" + take;
			}
			if (filter != null) {
				params += "filter=" + filter.replace("=", "%3D");
			}
		}
		return params;
	}

	private String tokenExtractor(String token) {
		int startIndex = token.indexOf(":\"") + 2;
		int endIndex = token.lastIndexOf("\"");

		return token.substring(startIndex, endIndex);
	}
}
