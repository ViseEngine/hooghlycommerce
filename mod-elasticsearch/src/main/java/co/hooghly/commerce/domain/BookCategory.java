package co.hooghly.commerce.domain;

import static org.springframework.data.elasticsearch.annotations.FieldType.String;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Document(indexName = "shop", type = "categories")
@AllArgsConstructor
@NoArgsConstructor
public class BookCategory {
	@Field(type = String)
	private String name;
}
