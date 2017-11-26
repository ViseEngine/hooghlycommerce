package co.hooghly.commerce.domain;


import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Document(indexName = "shop", type = "price")
@AllArgsConstructor
@NoArgsConstructor
public class BookPrice {
	@Field(type = FieldType.Double)
	private double price;
}
