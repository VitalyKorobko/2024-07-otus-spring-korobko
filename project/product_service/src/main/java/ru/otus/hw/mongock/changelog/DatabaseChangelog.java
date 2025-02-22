package ru.otus.hw.mongock.changelog;


import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoDatabase;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.model.Product;
import ru.otus.hw.repository.ProductRepository;

import java.util.List;

@ChangeLog(order = "001")
@RequiredArgsConstructor
public class DatabaseChangelog {
//    private static final int COUNT_OF_PRODUCTS = 100;

//    private final ProductFactory productFactory;

    @ChangeSet(order = "000", id = "dropDb", author = "korobko", runAlways = true)
    public void dropDb(MongoDatabase db) {
        db.drop();
    }

    private String id;

    private String title;

    private String ref;

    private String image;

    private String description;

    private int price;

    private long sellerId;

    @ChangeSet(order = "001", id = "2025-02-13-001-products", author = "korobko")
    public void insertProducts(ProductRepository productRepository) {
        List<Product> products = List.of(
                new Product(
                        "1",
                        "name1",
                        "A123456",
                        "https://s3.stroi-news.ru/img/kartinki-dlya-oblozhki-knigi-2.jpg",
                        "description1",
                        200,
                        2),
                new Product(
                        "2",
                        "name2",
                        "A223456",
                        "https://s3.stroi-news.ru/img/kartinki-dlya-oblozhki-knigi-2.jpg",
                        "description2",
                        300,
                        2),
                new Product(
                        "3",
                        "name3",
                        "A323456",
                        "https://s3.stroi-news.ru/img/kartinki-dlya-oblozhki-knigi-2.jpg",
                        "description2",
                        400,
                        2)
        );
        productRepository.saveAll(products).blockLast();
    }

}
