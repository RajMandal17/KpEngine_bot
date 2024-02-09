package com.example.demo;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableScheduling
public class DemoApplication {

	private static String lastType = "buy";
	private static final int spread = 200;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	private double getRandomNumberForOrderSize(double min, double max) {
		return min + Math.random() * (max - min);
	}

	@Scheduled(fixedDelayString = "10000")
	public void cronvalue() throws IOException, InterruptedException {
		double max = 20;
		double min = 10;

		double price = 43000 + (Math.random() * ((max - min) + 1) + min);
		double randomOrderSize = getRandomNumberForOrderSize(0.001, 0.01);
		double amount = randomOrderSize;
		String[] textArray = {"3", "16"};
		String userId = textArray[new Random().nextInt(textArray.length)];
		String url = "http://3.94.244.225/api/orders/bot";
		String formData = "{\n" +
				"    \"clientOid\": \"0\",\n" +
				"    \"productId\": \"BTC-USDT\",\n" +
				"    \"size\": " + String.format("%.8f", amount) + ",\n" +
				"    \"funds\": " + String.format("%.8f", price * amount) + ",\n" +
				"    \"price\": " + String.format("%.8f", price) + ",\n" +
				"    \"side\": \"" + lastType + "\",\n" +
				"    \"type\": \"limit\",\n" +
				"    \"TimeInForce\": \"\"\n" +
				"}";

		HttpClient httpClient = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url + userId))
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(formData))
				.build();

		if (lastType.equals("buy")) {
			lastType = "sell";
		} else {
			lastType = "buy";
		}

		if (lastType.equals("buy")) {
			price -= (spread * 0.5);
		} else {
			price += (spread * 0.5);
		}

		System.out.println("Request URL: " + url + userId);
		System.out.println("Request Body: " + formData);

		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		System.out.println("Response: " + response.body());
	}
}
