package org.crud.hibernate;

import org.crud.core.data.Identifiable;
import org.hibernate.Session;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hibernate.criterion.Restrictions.in;
import static org.hibernate.criterion.Restrictions.not;

public class HibernateUtils {
    private Session session;

    public HibernateUtils(Session session) {
        this.session = session;
    }

    public <T extends Identifiable> void updateCollection(Collection<T> collection, Class<T> itemClass) {
        //delete items missing in the collection
        Set<?> ids = collection.stream().filter(it -> it.getId() != null).map(Identifiable::getId).collect(Collectors.toSet());
        List<?> toDelete = session.createCriteria(itemClass).add(not(in("id", ids))).list();
        toDelete.forEach(it -> session.delete(it));
        session.flush();

        //update all items
        collection.forEach(it -> session.saveOrUpdate(it));
    }
}
