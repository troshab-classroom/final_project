package org.example;
import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.junit.jupiter.params.ParameterizedTest.*;
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
        Product p3 = new Product("other",445,7,3);
        CRUDstatements.insertProduct(p1);
        CRUDstatements.insertGroup(fruits);
        CRUDstatements.insertGroup(vegies);
        CRUDstatements.insertProduct(p2);
        CRUDstatements.insertProduct(p3);
        assertThat(CRUDstatements.getByCriteria(pc)).containsExactlyElementsOf(expectedProducts);
        //System.out.println(CRUDstatements.getIdGroup("Vegies"));
        }

        private static Stream<Arguments> dataProvider(){
            return Stream.of(
                    Arguments.of(
                            new ProductCriteria("prod", null,null,null,null),
                            List.of(new Product("prod1", 38 , 34, 1),new Product("prod2",124,7,2))
                    ),
                    Arguments.of(
                            new ProductCriteria("prod", 35.0,null,null,null),
                            List.of(new Product("prod1", 38 , 34, 1),new Product("prod2",124,7,2))
                    ),
                    Arguments.of(
                            new ProductCriteria(null, 100.0,null,null,null),
                            List.of(new Product("prod2",124,7,2),new Product("other",445,7,3))
                    )
//                    Arguments.of(
//                            new ProductCriteria(null, null,null,null,null,"Fruits"),
//                            List.of(new Product("prod1", 38 , 34, 1))
//                    )
            );
        }



    }

