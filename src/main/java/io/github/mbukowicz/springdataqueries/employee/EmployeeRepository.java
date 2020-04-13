package io.github.mbukowicz.springdataqueries.employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>,
    JpaSpecificationExecutor, QuerydslPredicateExecutor<Employee>,
    EmployeeRepositoryCustom {

  List<Employee> findByNameContainingIgnoreCaseAndSalaryGreaterThanOrderBySalaryDesc(
      String name, BigDecimal salary);

  @Query(
      "SELECT e FROM Employee e " +
          "WHERE lower(e.name) LIKE lower(concat('%', :name, '%')) " +
          "AND e.salary > :salary " +
          "ORDER BY e.salary DESC"
  )
  List<Employee> findByNameLikeAndSalaryAbove(
      @Param("name") String name, @Param("salary") BigDecimal salary);

  @Query(
      value = "SELECT * FROM employee e " +
          "WHERE e.name ILIKE %:name% " +
          "AND e.salary > :salary " +
          "ORDER BY e.salary DESC",
      nativeQuery = true
  )
  List<Employee> findByNameLikeAndSalaryAboveNative(
      @Param("name") String name, @Param("salary") BigDecimal salary);
}
