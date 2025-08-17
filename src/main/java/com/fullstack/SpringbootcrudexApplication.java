package com.fullstack;

import java.time.LocalDateTime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.fullstack.util.DateTimeUtil;

@SpringBootApplication
public class SpringbootcrudexApplication {

	public static void main(String[] args) {
		LocalDateTime now = LocalDateTime.now();

        System.out.println(DateTimeUtil.formatDefault(now));      // yyyy-MM-dd HH:mm:ss
        System.out.println(DateTimeUtil.formatSlashDate(now));    // dd/MM/yyyy
        System.out.println(DateTimeUtil.formatMonthName(now));    // Aug 08, 2025
        System.out.println(DateTimeUtil.formatFullWithAmPm(now)); // yyyy-MM-dd hh:mm:ss AM/PM
		
		SpringApplication.run(SpringbootcrudexApplication.class, args);
	}

}
