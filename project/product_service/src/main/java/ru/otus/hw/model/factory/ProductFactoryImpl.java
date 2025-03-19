//package ru.otus.hw.model.factory;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//import ru.otus.hw.model.Product;
//
//import java.util.Random;
//import java.util.UUID;
//
//@Component
//@RequiredArgsConstructor
//public class ProductFactoryImpl implements ProductFactory {
//    private static int num = 0;
//
//    private final Random random;
//
//    @Override
//    public Product create() {
//        return new Product(
//                UUID.randomUUID().toString(),
//                "name" + ++num,
//                "description" + num,
//                random.nextInt(50, 500)
//        );
//    }
//}
