package com.cst438;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class RegistrationEndToEndTest {

    private static final String CHROME_DRIVER_FILE_LOCATION = "/Users/zainabzoya/Downloads/chromedriver-mac-x64/chromedriver";
    private static final String URL = "http://localhost:3000"; // Replace with your actual application URL
    private WebDriver driver;

    @Before
    public void setup() {
        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    @After
    public void cleanup() {
        driver.quit();
    }

    @Test
    public void testAddStudent() {
        try {
            driver.get(URL);
            Thread.sleep(1000);

            // Your test logic for adding a student
            WebElement addMain = driver.findElement(By.id("AddButton"));
            addMain.click();
            Thread.sleep(1000);

            driver.findElement(By.name("sName")).sendKeys("Sam");
            driver.findElement(By.name("sEmail")).sendKeys("s@csumb.edu");
            driver.findElement(By.id("Addstudnet")).click();
            Thread.sleep(1000);

            List<WebElement> students = driver.findElements(By.id("studentAdded"));
            boolean isStudentAdded = false;

            for (WebElement student : students) {
                if (student.getText().contains("Sam")) {
                    isStudentAdded = true;
                    break;
                }
            }

            assertThat(isStudentAdded).withFailMessage("The student is not added.").isTrue();

        } catch (Exception ex) {
            // Handle or log the exception as needed
            ex.printStackTrace();
        }
    }

    @Test
    public void updateStudentTest() {
        try {
            driver.get(URL);
            Thread.sleep(1000);

            // Your test logic for updating a student
            WebElement updateButton = driver.findElement(By.id("mainUpdate"));
            updateButton.click();
            Thread.sleep(1000);

            driver.findElement(By.name("name")).sendKeys("Sam");
            driver.findElement(By.name("email")).sendKeys("s@csumb.edu");
            driver.findElement(By.id("updatestudent")).click();
            Thread.sleep(1000);

            List<WebElement> students = driver.findElements(By.id("studentAdded"));
            boolean isStudentUpdated = false;

            for (WebElement student : students) {
                if (student.getText().contains("Sam")) {
                    isStudentUpdated = true;
                    break;
                }
            }

            assertThat(isStudentUpdated).withFailMessage("The student is not updated.").isTrue();

        } catch (Exception ex) {
            // Handle or log the exception as needed
            ex.printStackTrace();
        }
    }

    @Test
    public void deleteStudentTest() {
        try {
            driver.get(URL);
            Thread.sleep(1000);

            List<WebElement> students = driver.findElements(By.id("studentAdded"));
            boolean isStudentFoundBeforeDeletion = false;

            for (WebElement student : students) {
                if (student.getText().contains("Sam")) {
                    isStudentFoundBeforeDeletion = true;
                    break;
                }
            }

            assertThat(isStudentFoundBeforeDeletion).withFailMessage("The student is not found before deletion.").isTrue();

            driver.get(URL + "/deleteStudent");
            Thread.sleep(1000);

            WebElement deleteButton = driver.findElement(By.id("delete"));
            deleteButton.click();
            Thread.sleep(1000);

            List<WebElement> remainingStudents = driver.findElements(By.id("studentAdded"));
            boolean isStudentDeleted = true;

            for (WebElement student : remainingStudents) {
                if (student.getText().equals("Sam")) {
                    isStudentDeleted = false;
                    break;
                }
            }

            assertThat(isStudentDeleted).withFailMessage("The student is not deleted.").isTrue();

        } catch (Exception ex) {
            // Handle or log the exception as needed
            ex.printStackTrace();
        }
    }
}