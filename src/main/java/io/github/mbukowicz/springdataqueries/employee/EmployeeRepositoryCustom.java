package io.github.mbukowicz.springdataqueries.employee;

import java.math.BigDecimal;
import java.util.List;

public interface EmployeeRepositoryCustom {

  List<Employee> findByNameLikeAndSalaryCustom(String name, BigDecimal salary);

  List<Employee> findByNameLikeAndSalaryOldSchool(String name, BigDecimal salary);
}
