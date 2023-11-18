package org.rent.circle.document.api.dto;


import io.quarkus.test.junit.QuarkusTest;
import org.force66.beantester.BeanTester;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class FormDataTest {

    @Test
    public void FormData_SettersAndGetters_ShouldWork() {
        // Arrange
        BeanTester beanTester = new BeanTester();

        // Act
        beanTester.testBean(FormData.class);

        // Assert

    }
}
