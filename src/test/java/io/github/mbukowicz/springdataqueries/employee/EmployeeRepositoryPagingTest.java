package io.github.mbukowicz.springdataqueries.employee;

import static io.github.mbukowicz.springdataqueries.employee.EmployeeSpecs.hasSalaryBetween;
import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.Predicate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;

@DataJpaTest
@Sql(scripts = "/employees.sql")
public class EmployeeRepositoryPagingTest {

  @Autowired
  private EmployeeRepository repository;

  @Test
  public void shouldReturnSecondPageWithThreeEmployees() {
    Page<Employee> result = repository.findAll(
        PageRequest.of(1, 3, Sort.by(Direction.ASC, "id")));
    assertThat(result.getContent()).containsExactly(
        new Employee(4L, "Carol", new BigDecimal("4000.00")),
        new Employee(5L, "Chuck", new BigDecimal("4100.00")),
        new Employee(6L, "Craig", new BigDecimal("4200.00"))
    );
    assertThat(result.getNumberOfElements()).isEqualTo(3);
    assertThat(result.getTotalPages()).isEqualTo(4);
  }

  @Test
  public void shouldReturnSecondSliceWithThreeEmployees() {
    Slice<Employee> result = repository.findAllSliced(
        PageRequest.of(1, 3, Sort.by(Direction.ASC, "id")));
    assertThat(result.getContent()).containsExactly(
        new Employee(4L, "Carol", new BigDecimal("4000.00")),
        new Employee(5L, "Chuck", new BigDecimal("4100.00")),
        new Employee(6L, "Craig", new BigDecimal("4200.00"))
    );
    assertThat(result.getNumberOfElements()).isEqualTo(3);
    assertThat(result.hasNext()).isTrue();
    assertThat(result.hasPrevious()).isTrue();
  }

  @Test
  public void shouldReturnSecondPageOfDynamicQueryResultsUsingSpecification() {
    PageRequest pageRequest = PageRequest.of(1, 3, Sort.by(Direction.ASC, "id"));
    Specification<Employee> spec = hasSalaryBetween(
        new BigDecimal("4500"), new BigDecimal("11000"));
    Page<Employee> result = repository.findAll(spec, pageRequest);
    assertThat(result.getContent()).containsExactly(
        new Employee(9L, "Oscar", new BigDecimal("4500.00")),
        new Employee(10L, "Walter", new BigDecimal("4600.00"))
    );
    assertThat(result.getNumberOfElements()).isEqualTo(2);
    assertThat(result.getTotalPages()).isEqualTo(2);
  }

  @Test
  public void shouldReturnSecondPageOfDynamicQueryResultsUsingQuerydsl() {
    QEmployee employee = QEmployee.employee;
    Predicate predicate = employee.salary.between(
        new BigDecimal("4500"), new BigDecimal("11000"));
    PageRequest pageRequest = PageRequest.of(1, 3, Sort.by(Direction.ASC, "id"));
    Page<Employee> result = repository.findAll(predicate, pageRequest);
    assertThat(result.getContent()).containsExactly(
        new Employee(9L, "Oscar", new BigDecimal("4500.00")),
        new Employee(10L, "Walter", new BigDecimal("4600.00"))
    );
    assertThat(result.getNumberOfElements()).isEqualTo(2);
    assertThat(result.getTotalPages()).isEqualTo(2);
  }
}
