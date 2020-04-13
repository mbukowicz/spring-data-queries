package io.github.mbukowicz.springdataqueries.employee;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class EmployeeRepositoryCustomImpl implements EmployeeRepositoryCustom {

  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public List<Employee> findByNameLikeAndSalaryCustom(String name, BigDecimal salary) {
    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Employee> query = builder.createQuery(Employee.class);
    Root<Employee> employee = query.from(Employee.class);
    Predicate criteria = builder.conjunction();
    if (name != null) {
      Predicate nameLike = builder.like(
          builder.lower(employee.get("name")),
          "%" + name.toLowerCase() + "%");
      criteria = builder.and(criteria, nameLike);
    }
    if (salary != null) {
      Predicate salaryAbove = builder.greaterThan(employee.get("salary"), salary);
      criteria = builder.and(criteria, salaryAbove);
    }
    query.select(employee).where(criteria);
    return entityManager.createQuery(query).getResultList();
  }

  @Override
  public List<Employee> findByNameLikeAndSalaryOldSchool(String name, BigDecimal salary) {
    Map<String, Object> params = new HashMap<>();
    StringBuilder sql = new StringBuilder();

    sql.append("SELECT * FROM employee e WHERE 1=1 ");
    if (name != null) {
      sql.append("AND e.name ILIKE :name ");
      params.put("name", "%" + name + "%");
    }
    if (salary != null) {
      sql.append("AND e.salary > :salary ");
      params.put("salary", salary);
    }
    sql.append("ORDER BY e.salary DESC");

    Query query = entityManager.createNativeQuery(sql.toString(), Employee.class);
    for (Entry<String, Object> param : params.entrySet()) {
      query.setParameter(param.getKey(), param.getValue());
    }
    return query.getResultList();
  }
}
