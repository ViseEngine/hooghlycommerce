package co.hooghly.commerce.domain;

import static org.springframework.data.elasticsearch.annotations.FieldIndex.not_analyzed;
import static org.springframework.data.elasticsearch.annotations.FieldType.Nested;
import static org.springframework.data.elasticsearch.annotations.FieldType.String;


import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;

import lombok.Data;

@Data
@Document(indexName = "shop", type = "book")
public class Book {
	
	public Book(){}
	
	public Book(String title){
		this.title = title;
	}

    @Id
    private String id;

    @MultiField(mainField = @Field(type = String), otherFields = { @InnerField(index = not_analyzed, suffix = "verbatim", type = String) })
    private String title;

    @Field(type = Nested, includeInParent=true)
    private List<Author> authors;

    @Field(type = String, index = not_analyzed)
    private String[] tags;
    
    @Field(type = Nested, includeInParent=true)
    private List<BookCategory> categories;
    
    
}