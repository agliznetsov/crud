package org.crud.hibernate;

import org.crud.hibernate.model.Document;
import org.crud.hibernate.model.DocumentItem;
import org.hibernate.Criteria;

public abstract class DocumentRepository extends HibernateRepository<Document, Integer> {

    public DocumentRepository() {
        useGenericTypes();
    }

    @Override
    public <S extends Document> S save(S entity) {
        S doc = super.save(entity);
        updateCollection(entity.getItems(), DocumentItem.class);
        return doc;
    }

    @Override
    protected Criteria criteria() {
        Criteria criteria = super.criteria();
        //Required to be able to filter on client attributes
        criteria.createAlias("client", "client");
        return criteria;
    }
}
