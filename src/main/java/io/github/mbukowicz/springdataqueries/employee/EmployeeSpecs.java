package io.github.mbukowicz.springdataqueries.employee;

import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class EmployeeSpecs {

  public static Specification<Employee> hasNameLike(String name) {
    return (Specification<Employee>) (employee, query, builder) ->
        builder.like(builder.lower(employee.get("name")), "%" + name.toLowerCase() + "%");
  }

  public static Specification<Employee> hasSalaryAbove(BigDecimal salary) {
    return (Specification<Employee>) (employee, query, builder) ->
        builder.greaterThan(employee.get("salary"), salary);
  }
}
