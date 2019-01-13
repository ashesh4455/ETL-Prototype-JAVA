package com.quertle.demo;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.quertle.demo.controller.GeneralController;
import com.quertle.demo.model.FierceNews;
import com.quertle.demo.model.General;
import com.quertle.demo.repository.FierceNewsRepository;
import com.quertle.demo.service.GeneralService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {

	@Mock
	private GeneralService generalService;
	
	@Mock
	private FierceNewsRepository repo;

	private static final Logger LOG = LoggerFactory.getLogger(DemoApplicationTests.class);

	@Test
	public void testMock() {
		General answer = new General();
		answer.setFirstName("First Name");
		answer.setLastName("Last Name");
		Mockito.when(generalService.getGeneral()).thenReturn(answer);

		System.out.println(generalService.getGeneral());
	}

	@Test
	public void contextLoads() {
		General answer = new General();
		answer.setFirstName("First Name");
		answer.setLastName("Last Name");
		Mockito.when(generalService.getGeneral()).thenReturn(answer);

		GeneralController generalController = new GeneralController(this.generalService);
		General general = generalController.getGeneral();
		Assert.assertEquals(general.getFirstName(), "First Name");
		Assert.assertEquals(general.getLastName(), "Last Name");
		
		//Trail
		Mockito.when(repo.getOne(1)).thenReturn(new FierceNews());
		Mockito.when(repo.findAll()).thenReturn(new ArrayList<FierceNews>());
	}

	/**
	 * To understand the interface
	 */
	@Test
	public void testI() {
		Calculate sum = new Sum();
		LOG.info("Sum - {} ", sum.calculate(1, 2));
		LOG.info("Multiply - {} ", sum.multiplyToPI(1));

		Calculate dif = new Subtract();
		LOG.info("{} ", dif.calculate(6, 2));
		LOG.info("Multiply - {} ", dif.multiplyToPI(1));
	}

}
