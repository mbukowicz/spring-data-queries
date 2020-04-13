package io.github.mbukowicz.springdataqueries.employee;

import static io.github.mbukowicz.springdataqueries.employee.EmployeeSpecs.hasNameLike;
import static io.github.mbukowicz.springdataqueries.employee.EmployeeSpecs.hasSalaryAbove;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.contains;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.List;

@DataJpaTest
@Sql(statements = {
    "INSERT INTO employee (id, name, salary) VALUES (1, 'Bob', 5000.00)",
    "INSERT INTO employee (id, name, salary) VALUES (2, 'Trent', 7500.00)",
    "INSERT INTO employee (id, name, salary) VALUES (3, 'Alice', 10000.00)"
})
public class EmployeeRepositoryTest {

  @Autowired
  private EmployeeRepository repository;

  @Test
  public void shouldReturnResultUsingMethodName() {
    List<Employee> result =
        repository.findByNameContainingIgnoreCaseAndSalaryGreaterThanOrderBySalaryDesc(
            "LIC", new BigDecimal("8000"));
    assertThatResultContainsOnlyAlice(result);
  }

  @Test
  public void shouldReturnResultUsingQuery() {
    List<Employee> result =
        repository.findByNameLikeAndSalaryAbove("LIC", new BigDecimal("8000"));
    assertThatResultContainsOnlyAlice(result);
  }

  @Test
  public void shouldReturnResultUsingExample() {
    // Examples API only supports starts/contains/ends/regex matching for strings
    // and exact matching for other property types
    // (it is not possible to implement matching on numbers,
    // like greater than, lower than or equal, etc.)
    Employee employee = new Employee(null, "LIC", null);
    ExampleMatcher matcher = ExampleMatcher.matching()
        .withMatcher("name", contains().ignoreCase());
    List<Employee> result = repository.findAll(Example.of(employee, matcher));
    assertThatResultContainsOnlyAlice(result);
  }

  @Test
  public void shouldReturnResultUsingSpecification() {
    List<Employee> result = repository.findAll(
        hasNameLike("LIC").and(hasSalaryAbove(new BigDecimal("8000"))));
    assertThatResultContainsOnlyAlice(result);
  }

  @Test
  public void shouldReturnResultUsingQuerydsl() {
    QEmployee employee = QEmployee.employee;
    Iterable<Employee> result = repository.findAll(
        employee.name.likeIgnoreCase("%LIC%")
            .and(employee.salary.gt(new BigDecimal("8000"))));
    assertThatResultContainsOnlyAlice(result);
  }

  @Test
  public void shouldReturnResultUsingNativeQuery() {
    List<Employee> result =
        repository.findByNameLikeAndSalaryAboveNative("LIC", new BigDecimal("8000"));
    assertThatResultContainsOnlyAlice(result);
  }

  @Test
  public void shouldReturnResultUsingCustomRepository() {
    List<Employee> result =
        repository.findByNameLikeAndSalaryCustom("LIC", new BigDecimal("8000"));
    assertThatResultContainsOnlyAlice(result);
  }

  @Test
  public void shouldReturnResultUsingOldSchoolApproach() {
    List<Employee> result =
        repository.findByNameLikeAndSalaryOldSchool("LIC", new BigDecimal("8000"));
    assertThatResultContainsOnlyAlice(result);
  }

  private void assertThatResultContainsOnlyAlice(Iterable<Employee> result) {
    assertThat(result)
        .containsExactly(
            new Employee(3L, "Alice", new BigDecimal("10000.00")));
  }
}
