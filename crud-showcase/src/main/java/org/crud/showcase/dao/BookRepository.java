package org.crud.showcase.dao;

import org.crud.hibernate.SaveStrategy;
import org.crud.showcase.model.Book;
import org.crud.showcase.model.BookChapter;
import org.hibernate.Criteria;
import org.springframework.stereotype.Repository;

import javax.persistence.Entity;
import javax.transaction.Transactional;

@Repository
@Transactional
public class BookRepository extends EntityRepository<Book, Long> {

    @Override
    public <S extends Book> S save(S entity) {
        S res = super.save(entity);
        entity.getChapters().forEach(it -> it.setBook(res));
        updateCollection(entity.getChapters(), BookChapter.class);
        return res;
    }

    @Override
    protected Criteria criteria() {
        Criteria criteria = super.criteria();
        //Required to be able to query on author attributes
        criteria.createAlias("author", "author");
        return criteria;
    }

}
