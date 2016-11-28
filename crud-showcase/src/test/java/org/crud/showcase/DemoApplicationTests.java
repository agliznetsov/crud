package org.crud.showcase;

import io.swagger.models.Swagger;
import org.crud.core.data.EntityProxy;
import org.crud.core.util.MapUtils;
import org.crud.showcase.dto.BookChapterDTO;
import org.crud.showcase.dto.BookDTO;
import org.crud.showcase.model.Person;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DemoApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void test_resources() {
		List resources = restTemplate.getForObject("/resources", List.class);
		assertEquals(2, resources.size());
	}

	@Test
	public void swagger() throws Exception {
		Map swagger = restTemplate.getForObject("/swagger?title=Resources", Map.class);
		List tags = (List) swagger.get("tags");
		assertEquals(2, tags.size());
	}

	@Test
	public void create_person() throws Exception {
		Long id = savePerson();
		Person person = restTemplate.getForObject("/resources/Person/" + id, Person.class);
		assertEquals("test", person.getLastName());
	}

	private Long savePerson() {
		Person person = new Person(null, "test", "test", null);
		Map response = restTemplate.postForObject("/resources/Person", person, Map.class);
		Object id = response.get("id");
		assertNotNull(id);
		return Long.parseLong(id.toString());
	}

	@Test
	public void find_book_by_author_name() throws Exception {
		Long id = savePerson();
		BookDTO book = new BookDTO();
		book.setAuthor(new EntityProxy<>(id));
		restTemplate.postForObject("/resources/Book", book, Map.class);

		List books = restTemplate.getForObject("/resources/Book?author.lastName=test", List.class);
		assertEquals(1, books.size());

		List books2 = restTemplate.getForObject("/resources/Book?author.lastName=Smith", List.class);
		assertEquals(0, books2.size());
	}

	@Test
	public void create_update_book() throws Exception {
		Object id;
		{
			BookDTO book = new BookDTO();
			book.setAuthor(new EntityProxy<>(1L));
			book.getChapters().add(new BookChapterDTO(null, "test", 1));
			Map response = restTemplate.postForObject("/resources/Book", book, Map.class);
			id = response.get("id");
		}
		{
			BookDTO book = restTemplate.getForObject("/resources/Book/" + id, BookDTO.class);
			assertEquals(1, book.getChapters().size());
			assertEquals(1L, book.getAuthor().getId().longValue());
			book.getChapters().clear();
			book.getChapters().add(new BookChapterDTO(null, "new chapter", 1));
			book.setTitle("new book title");
			restTemplate.put("/resources/Book/" + id, book, Map.class);
		}
		{
			BookDTO book = restTemplate.getForObject("/resources/Book/" + id, BookDTO.class);
			assertEquals("new book title", book.getTitle());
			assertEquals(1, book.getChapters().size());
			assertEquals("new chapter", book.getChapters().get(0).getTitle());
		}
	}
}
