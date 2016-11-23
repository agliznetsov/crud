package org.crud.hibernate;

import org.crud.core.data.*;
import org.crud.core.transform.TransformServiceImpl;
import org.crud.hibernate.model.Document;
import org.crud.hibernate.model.DocumentItem;
import org.crud.hibernate.model.Product;
import org.hibernate.Session;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.annotation.Resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class RepositoryTest {

    @Rule
    public final SessionFactoryRule sf = new SessionFactoryRule();

    private HibernateRepository<Document, Integer> repository;

    Product product;

    @Before
    public void setUp() throws Exception {
        repository = new HibernateRepository<Document, Integer>() {
            @Override
            public <S extends Document> S save(S entity) {
                S doc = super.save(entity);
                new HibernateUtils(session()).updateCollection(entity.getItems(), DocumentItem.class);
                return doc;
            }

            @Override
            protected Session session() {
                return sf.getSession();
            }
        };
        repository.useGenericTypes();
        repository.setCriteriaBuilder(new CriteriaBuilder(repository.getEntityClass(), new TransformServiceImpl()));

        product = new Product();
        product.setName("product_name");
        sf.getSession().persist(product);
        sf.flush();
    }

    @Test
    public void insert_get_update() throws Exception {
        Integer id;
        {
            //insert
            Document document = createDocument();
            repository.save(document);
            assertNotNull(document.getId());
            id = document.getId();
            sf.flush();
        }

        {
            //update
            assertEquals(1, repository.findAll().size());

            Document document = repository.getOne(id);
            assertEquals(2, document.getItems().size());
            sf.flush();

            DocumentItem[] items = document.getItems().toArray(new DocumentItem[0]);
            document.getItems().clear();
            items[0].setAmount(5);
            document.getItems().add(items[0]);
            document.getItems().add(new DocumentItem(null, document, product, 30));
            repository.save(document);
            sf.flush();
        }

        {
            Document document = repository.getOne(id);
            assertEquals(2, document.getItems().size());
            sf.flush();
        }
    }

    @Test
    public void query() throws Exception {
        {
            repository.save(createDocument());
            repository.save(createDocument());
            sf.flush();
        }
        {
            ResourceQuery query = new ResourceQuery();
            query.setFilter(new CompositeFilter(CompositeOperator.AND, new PropertyFilter(FilterOperator.EQ, "name", "test"), new PropertyFilter(FilterOperator.GTE, "id", "1")));
            ResourceResponse response = repository.query(query);
            assertNull(response.getCount());
            assertEquals(2, response.getItems().size());
        }
        {
            ResourceQuery query = new ResourceQuery();
            query.setCount(true);
            query.setSkip(1);
            query.setMax(2);
            ResourceResponse response = repository.query(query);
            assertEquals(2, response.getCount().longValue());
            assertEquals(1, response.getItems().size());
        }
    }

    private Document createDocument() {
        Document document = new Document();
        document.setName("test");
        document.getItems().add(new DocumentItem(null, document, product, 10));
        document.getItems().add(new DocumentItem(null, document, product, 20));
        return document;
    }
}
