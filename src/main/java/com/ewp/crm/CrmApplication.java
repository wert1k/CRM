package com.ewp.crm;

import com.ewp.crm.configs.initializer.DataInitializer;
import com.ewp.crm.models.Client;
import com.ewp.crm.utils.converters.IncomeStringToClient;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

//@SpringBootApplication
public class CrmApplication {

	public static void main(String[] args) {
		IncomeStringToClient incomeStringToClient = new IncomeStringToClient();
		String parser = "<br>Страница: <b>http://www.java-mentor.com/index.html</b> <br />Форма: <b>Java Test</b> <br />1: <b>2</b> <br />2: <b>3</b> <br />3: <b>3</b> <br />4: <b>3</b> <br />5: <b>2</b> <br />6: <b>3</b> <br />Имя: <b>testew</b> <br />Social 2: <b>https://vk.com/andiovermide</b> <br />Phone 6: <b>89110812259</b> <br />City 6: <b>Россия</b> <br />Email 2: <b>mcdncrnc73@gmail.com</b> <br />";
		String parser2 = "Страница: http://www.java-mentor.com/index.html Форма: Java Test 1: 2 2: 2 3: 3 4: 2 5: 3 6: 4 Имя: testew Social 2: https://vk.com/andiovermide Phone 6: 89110812259 City 6: Россия Email 2: mcdncrnc73@gmail.com";
		Client client = incomeStringToClient.convert(parser);
		if (client != null) {
			//--------------------------
			if (parser2.contains("Java Test")) {
				Long list = validatorTestResult(parser2);
				if (list > 0) {
					System.out.println("Java-Mentor.ru" + client.getEmail() + "Test complete!" + "Поздравляем! Вы ответили правильно на: \n\n" + list + "% вопросов!");
				} else {
					System.out.println("Java-Mentor.ru" + client.getEmail() + "Test complete!" + "К сожалению вы не ответили ни на один вопрос верно...");
				}
			}
			//--------------------------
		}
//		SpringApplication.run(CrmApplication.class, args);
	}

	private static Long validatorTestResult(String parseContent) {
		String parseTest = parseContent;
		String indexQuery = parseTest.substring(parseTest.indexOf(" Java Test") + 1, parseTest.indexOf("6:") + 5)
                .replace("Java Test ", "")
				.replaceAll(": \\d+ ", "");

        String listQuery = parseTest.substring(parseTest.indexOf(" Java Test") + 1, parseTest.indexOf("6:") + 5)
                .replace("Java Test", "")
                .replaceAll(" \\d+: ", "");

        List<Integer> result = Arrays.asList(3, 3, 1, 2, 3, 4);
        List<Integer> resultList = Arrays.asList(indexQuery.split("\\s*\\s*")).stream().map(s -> Integer.parseInt(s.trim())).collect(Collectors.toList());
		List<Integer> resultTest = Arrays.asList(listQuery.split("\\s*\\s*")).stream().map(s -> Integer.parseInt(s.trim())).collect(Collectors.toList());

        System.out.println(resultList);
        System.out.println(resultTest);

        for (int q = 1, count = 0, i = 0; i < result.size(); i++) {
			if (resultList.get(i).equals(i + q)) {
				if (resultList.get(i + 1).equals(i + q + 1)) {
					if (result.get(i).equals(resultTest.get(i))) {
						count++;
						System.out.println(count);
						if ((resultTest.size()) - i == 1) {
							return (long) ((count * 100) / result.size());
						}
					}
					if ((result.size()) - i == 1 & count == 0) {
						return null;
					}
				} else {
					q++;
				}

			}

        }
        return 0L;
	}

	@Bean(initMethod = "init")
	public DataInitializer initTestData() {
		return new DataInitializer();
	}
}