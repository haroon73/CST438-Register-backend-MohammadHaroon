package com.cst438;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import com.cst438.controller.StudentController;
import com.cst438.domain.Student;
import com.cst438.domain.StudentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@ContextConfiguration(classes = { StudentController.class })
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest
public class JunitTestStudentController {

	Integer s_id = 2345;
	String s_name = "Khan";
	String s_email = "kj2@csumb.edu";

	@MockBean
	StudentRepository studentRepository;

	@Autowired
	private MockMvc vc;

	@Test
	public void createStudent() throws Exception {
		MockHttpServletResponse res;

		Student s1 = new Student();

		s1.setName(s_name);
		s1.setEmail(s_email);

		when(studentRepository.findByEmail(s_email)).thenReturn(null); // do not need to put this because it will return
																		// null
		when(studentRepository.save(s1)).thenReturn(s1);

		res = vc.perform(post("/student").contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"Khan\", \"email\":\"kj2@csumb.edu\"}").accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();

		assertEquals(200, res.getStatus());

		Student result = fromJsonString(res.getContentAsString(), Student.class);
		assertEquals(0, result.getStudent_id());
		verify(studentRepository).save(any(Student.class));

	}

	private static String asJsonString(final Object obj) {
		try {

			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static <T> T fromJsonString(String str, Class<T> valueType) {
		try {
			return new ObjectMapper().readValue(str, valueType);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
