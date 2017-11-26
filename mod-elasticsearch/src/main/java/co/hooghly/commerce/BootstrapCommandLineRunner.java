package co.hooghly.commerce;

import static org.elasticsearch.index.query.QueryBuilders.*;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ResultsExtractor;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Component;

import co.hooghly.commerce.domain.Book;
import co.hooghly.commerce.domain.BookCategory;
import co.hooghly.commerce.domain.Author;
import co.hooghly.commerce.service.BookService;
import lombok.extern.slf4j.Slf4j;

import static java.util.Arrays.*;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class BootstrapCommandLineRunner implements CommandLineRunner {

	@Autowired
	private BookService articleService;

	@Autowired
	private ElasticsearchTemplate elasticsearchTemplate;

	@Override
	public void run(String... args) throws Exception {
		log.info("## Delete all items.");
		articleService.deleteAll();

		BookCategory nosql = new BookCategory("nosql");
		BookCategory java = new BookCategory("java");

		Book article = new Book("Spring Data Elasticsearch 2.x");
		article.setAuthors(asList(new Author("John Smith"), new Author("John Doe")));
		article.setCategories(asList(nosql));
		articleService.save(article);

		log.info("Book 0 - {}", article);

		article = new Book("Spring Data Elasticsearch 5.x");
		article.setAuthors(asList(new Author("John Smith"), new Author("John Doe")));
		article.setCategories(asList(java));
		articleService.save(article);

		log.info("Book 1 - {}", article);

		elasticsearchTemplate.refresh("shop");

		//Thread.sleep(1000 * 20l);

		SearchQuery searchQuery = new NativeSearchQueryBuilder().withFilter(regexpQuery("title", ".*data.*")).build();
		List<Book> books = elasticsearchTemplate.queryForList(searchQuery, Book.class);

		log.info("books - {}", books);

		final Page<Book> articleByAuthorName = articleService.findByAuthorName("John Smith", new PageRequest(0, 10));

		log.info("books count? - {}", articleByAuthorName.getTotalElements());

		BoolQueryBuilder builder = boolQuery();
		builder.must(matchQuery("authors.name", "John Smith")

		);

		searchQuery = new NativeSearchQueryBuilder().withQuery(builder).build();

		books = elasticsearchTemplate.queryForList(searchQuery, Book.class);

		log.info("books count?? - {}", books.size());

		builder = boolQuery();
		builder.must(matchQuery("authors.name", "John Smith")).must(matchQuery("categories.name", "nosql"));

		searchQuery = new NativeSearchQueryBuilder().withQuery(builder).build();

		books = elasticsearchTemplate.queryForList(searchQuery, Book.class);

		log.info("books count??? - {}", books.size());

		// faceted search/aggregation

		BoolQueryBuilder builder2 = boolQuery();
		builder.must(matchQuery("authors.name", "John Smith")

		);

		searchQuery = new NativeSearchQueryBuilder().withQuery(builder2)
				.addAggregation(AggregationBuilders.terms("group_by_categories").field("categories.name")).build();

		Aggregations aggregations = elasticsearchTemplate.query(searchQuery, new ResultsExtractor<Aggregations>() {
			@Override
			public Aggregations extract(SearchResponse response) {
				log.info("Response - " + response);
				return response.getAggregations();
			}
		});
		
		
		
		log.info("agg - " +  aggregations.asMap().get("group_by_categories"));
		Map<String, Aggregation> results = aggregations.asMap();
		
	
	}

}
