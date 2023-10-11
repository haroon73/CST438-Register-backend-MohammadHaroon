package com.cst438.service;


import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cst438.domain.FinalGradeDTO;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentDTO;
import com.cst438.domain.EnrollmentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@ConditionalOnProperty(prefix = "gradebook", name = "service", havingValue = "mq")
public class GradebookServiceMQ implements GradebookService {

	@Autowired
	RabbitTemplate rabbitTemplate;

	@Autowired
	EnrollmentRepository enrollmentRepository;

	Queue gradebookQueue = new Queue("gradebook-queue", true);
	@Bean
	public Queue createQueue() {
		return new Queue("registration_queue", true);
	}
	// send message to grade book service about new student enrollment in course
	@Override
	public void enrollStudent(String student_email, String student_name, int course_id) {
		System.out.println("Start Message "+ student_email +" " + course_id);
		// create EnrollmentDTO, convert to JSON string and send to gradebookQueue
		// TODO
		// Create an EnrollmentDTO with the provided data
		EnrollmentDTO enrollmentDTO = new EnrollmentDTO(0, student_email, student_name, course_id);

		// Convert the EnrollmentDTO to a JSON string using your JSON utility class
		String jsonEnrollment = asJsonString(enrollmentDTO);

		// Use RabbitTemplate to send the JSON message to the gradebook-queue
		rabbitTemplate.convertAndSend(gradebookQueue.getName(), jsonEnrollment);
	}

	@RabbitListener(queues = "registration-queue")
	@Transactional
	public void receive(String message) {
		System.out.println("Receive grades :" + message);

		FinalGradeDTO[] finalGrades = fromJsonString(message, FinalGradeDTO[].class);

		for (FinalGradeDTO grade : finalGrades) {
			// Find the student enrollment entity based on student email and course ID
			Enrollment enrollment = enrollmentRepository.findByEmailAndCourseId(grade.studentEmail(), grade.courseId());

			if (enrollment != null) {
				// Update the enrollment record with the final grade
				enrollment.setCourseGrade(grade.grade());

				// Save the updated enrollment record to the database
				enrollmentRepository.save(enrollment);
			} else {
				// Handle the case where the enrollment record was not found
				System.out.println("Enrollment record not found for student " + grade.studentEmail() +
						" and course " + grade.courseId());
			}
		}

	}

	private static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static <T> T  fromJsonString(String str, Class<T> valueType ) {
		try {
			return new ObjectMapper().readValue(str, valueType);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
