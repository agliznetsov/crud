package org.crud.showcase.dao;

import org.crud.showcase.model.Person;
import org.springframework.stereotype.Repository;

@Repository
public class PersonRepository extends EntityRepository<Person, Long> {
}
