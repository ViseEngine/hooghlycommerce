package co.hooghly.commerce.domain;


import static org.springframework.data.elasticsearch.annotations.FieldType.String;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Document(indexName = "shop", type = "authors")
public class Author {
	 @Field(type = String)
    private String name;

   
}