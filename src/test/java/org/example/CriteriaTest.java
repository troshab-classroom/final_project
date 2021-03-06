package org.example;
import org.example.entities.Group;
import org.example.entities.Product;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class CriteriaTest {

    @ParameterizedTest
    @MethodSource("dataProvider")
    public void shouldSelectByFilters(ProductCriteria pc, List<Product> expectedProducts){

        DataBase.connect();
        CRUDstatements.create();
        CRUDstatements.deleteFromProductAll();
        CRUDstatements.deleteFromGroupAll();
        Group fruits = new Group("Fruits", "sweet");
        Group vegies = new Group("Vegies", "healthy");
        Product p1 = new Product("prod1", 38 , 34, 1);
        Product p2 = new Product("prod2",124,7,2);
        Product p3 = new Product("other",445,15,3);
        CRUDstatements.insertProduct(p1);
        CRUDstatements.insertGroup(fruits);
        CRUDstatements.insertGroup(vegies);
        CRUDstatements.insertProduct(p2);
        CRUDstatements.insertProduct(p3);
        assertThat(CRUDstatements.getByCriteria(pc)).containsExactlyElementsOf(expectedProducts);
        }

//        private static Stream<Arguments> dataProvider(){
//            return Stream.of(
//                    Arguments.of(
//                            new ProductCriteria("prod", null,null,null,null),
//                            List.of(new Product("prod1", 38 , 34, 1),new Product("prod2",124,7,2))
//                    ),
//                    Arguments.of(
//                            new ProductCriteria("prod", 35.0,null,null,null),
//                            List.of(new Product("prod1", 38 , 34, 1),new Product("prod2",124,7,2))
//                    ),
//                    Arguments.of(
//                            new ProductCriteria(null, 100.0,null,null,null),
//                            List.of(new Product("prod2",124,7,2),new Product("other",445,15,3))
//                    ),
//                    Arguments.of(
//                            new ProductCriteria(null, null,null,30.0, null),
//                            List.of(new Product("prod1", 38 , 34, 1))
//                    )
//            );
//        }



    }

