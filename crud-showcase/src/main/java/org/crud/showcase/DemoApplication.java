package org.crud.showcase;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.crud.core.data.CrudRepository;
import org.crud.core.transform.TransformService;
import org.crud.core.transform.TransformServiceImpl;
import org.crud.rest.mvc.CrudResourceController;
import org.crud.rest.resource.ResourceAction;
import org.crud.rest.resource.ResourceInfo;
import org.crud.rest.resource.ResourceInfoRegistry;
import org.crud.rest.resource.ResourceInfoSupplier;
import org.crud.showcase.dao.BookRepository;
import org.crud.showcase.dao.EntityRepository;
import org.crud.showcase.dao.PersonRepository;
import org.crud.showcase.dto.BookChapterDTO;
import org.crud.showcase.dto.BookDTO;
import org.crud.showcase.model.Book;
import org.crud.showcase.model.BookChapter;
import org.crud.showcase.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

@SpringBootApplication
public class DemoApplication implements ApplicationRunner {

    @Autowired
    List<EntityRepository> repositories;
    @Autowired
    BookRepository bookRepository;
    @Autowired
    PersonRepository personRepository;
    @Autowired
    ObjectMapper objectMapper;

    @Bean
    public TransformService transformService() {
        return new TransformServiceImpl();
    }

    @Bean
    public ResourceInfoSupplier resourceInfoSupplier() {
        ResourceInfoRegistry registry = new ResourceInfoRegistry();
        addResource(registry, Person.class, null, ResourceAction.CRUD);
        addResource(registry, Book.class, BookDTO.class, ResourceAction.CRUD);
        return registry;
    }

    @Bean
    public JacksonTransformer jacksonTransformer() {
        return new JacksonTransformer(objectMapper);
    }

    @Bean
    public CrudResourceController crudResourceController() {
        return new CrudResourceController();
    }

    private void addResource(ResourceInfoRegistry registry, Class eClass, Class dtoClass, Set<ResourceAction> actions) {
        CrudRepository repository = repositories.stream().filter(it -> it.getEntityClass().equals(eClass)).findFirst().get();
        ResourceInfo info = ResourceInfo.builder()
                .entityClass(eClass)
                .dtoClass(dtoClass)
                .idClass(Long.class)
                .name(eClass.getSimpleName())
                .actions(actions)
                .repository(repository)
                .build();
        registry.add(info);
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Override
    @Transactional
    public void run(ApplicationArguments applicationArguments) throws Exception {
        transformService().registerTransformer(byte[].class, Object.class, new JacksonTransformer(objectMapper));
        transformService().registerBeanPair(BookChapter.class, BookChapterDTO.class);
        transformService().registerBeanPair(Book.class, BookDTO.class);

        Person john = new Person(null, "John", "Doe");
        Person jane = new Person(null, "Jane", "Smith");
        personRepository.save(john);
        personRepository.save(jane);
        Book book = new Book();
        book.setAuthor(john);
        book.setTitle("My First Book");
        book.getChapters().add(new BookChapter(null, book, "First Chapter", 1));
        book.getChapters().add(new BookChapter(null, book, "Second Chapter", 2));
        bookRepository.save(book);
    }

}
