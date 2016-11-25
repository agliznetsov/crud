package org.crud.showcase.dao;

import org.crud.core.transform.TransformService;
import org.crud.hibernate.CriteriaBuilder;
import org.crud.hibernate.HibernateRepository;
import org.hibernate.Session;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.io.Serializable;

@Transactional
public class EntityRepository<T, ID extends Serializable> extends HibernateRepository<T, ID> implements InitializingBean {

    @Autowired
    EntityManager em;
    @Autowired
    TransformService transformService;

    @Override
    protected Session session() {
        return em.unwrap(Session.class);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        useGenericTypes();
        setCriteriaBuilder(new CriteriaBuilder(entityClass, transformService));
    }
}
