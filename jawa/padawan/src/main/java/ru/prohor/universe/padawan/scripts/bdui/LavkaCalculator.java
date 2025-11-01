package ru.prohor.universe.padawan.scripts.bdui;

import jakarta.annotation.Nonnull;
import org.json.JSONArray;
import org.json.JSONObject;
import ru.prohor.universe.padawan.Padawan;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class LavkaCalculator {
    public static void main(String[] args) throws Exception {
        int rublesAvailable = 1000;
        calculateVariants(rublesAvailable);
    }

    private enum Products {
        POCKET("Упаковка", 12, 1, 1);

        public final String name;
        public final int price;
        public final int min;
        public final int max;

        Products(String name, int price, int min, int max) {
            this.name = name;
            this.price = price;
            this.min = min;
            this.max = max;
        }
    }

    private static void calculateVariants(int rublesAvailable) throws Exception {
        List<ProductsSet> list = new ArrayList<>();
        Products[] products = Products.values();
        int minIndex = 0;
        int maxIndex = products.length - 1;
        int index = minIndex;
        int[] productsCounts = new int[products.length];
        IntStream.range(0, products.length).forEach(i -> productsCounts[i] = products[i].min);
        int currentPrice = IntStream.range(0, products.length).map(i -> products[i].price * productsCounts[i]).sum();
        boolean priceMoreThanAvailable = false;

        while (index >= minIndex) {
            if (priceMoreThanAvailable || productsCounts[index] > products[index].max) {
                priceMoreThanAvailable = false;
                currentPrice -= (productsCounts[index] - products[index].min) * products[index].price;
                productsCounts[index] = products[index].min;
                if (--index < minIndex)
                    break;
                productsCounts[index]++;
                currentPrice += products[index].price;
                continue;
            }
            if (index < maxIndex) {
                if (currentPrice <= rublesAvailable)
                    index++;
                else
                    priceMoreThanAvailable = true;
                continue;
            }
            if (currentPrice <= rublesAvailable)
                list.add(ProductsSet.of(currentPrice, productsCounts));
            productsCounts[index]++;
            currentPrice += products[index].price;
        }

        System.out.println(
                "всего комбинаций: " +
                        Arrays.stream(products).mapToInt(x -> x.max - x.min + 1).reduce(1, (i1, i2) -> i1 * i2)
        );
        System.out.println("комбинаций дешевле " + rublesAvailable + ": " + list.size());
        List<ProductsSet> filtered = filterDominatedArrays(list);
        System.out.println("упор в лимит: " + filtered.size());

        filtered.sort(Comparator.comparing(ProductsSet::price).reversed());

        JSONArray variants = new JSONArray();
        for (ProductsSet productsSet : filtered) {
            int[] counts = productsSet.productsCounts();
            JSONObject variant = new JSONObject();
            variant.put("price", productsSet.price());
            JSONObject productsJson = new JSONObject();
            IntStream.range(0, products.length).forEach(i -> {
                if (counts[i] == 0)
                    return;
                productsJson.put(products[i].name, counts[i]);
            });
            variant.put("products", productsJson);
            variants.put(variant);
        }
        Files.writeString(Padawan.resolve("json.json"), variants.toString());
    }

    private static class ProductsSet implements Comparable<ProductsSet> {
        private final int price;
        private final int[] productsCounts;
        private final int sum;

        private ProductsSet(int price, int[] productsCounts) {
            this.price = price;
            this.productsCounts = productsCounts;
            this.sum = Arrays.stream(productsCounts).sum();
        }

        public int price() {
            return price;
        }

        public int[] productsCounts() {
            return productsCounts;
        }

        @Override
        public int compareTo(@Nonnull ProductsSet o) {
            return Integer.compare(o.sum, this.sum);
        }

        public static ProductsSet of(int price, int[] productsCounts) {
            return new ProductsSet(price, Arrays.copyOf(productsCounts, productsCounts.length));
        }

        public boolean dominates(ProductsSet o) {
            for (int i = 0; i < this.productsCounts.length; ++i)
                if (this.productsCounts[i] < o.productsCounts[i])
                    return false;
            return true;
        }
    }

    private static List<ProductsSet> filterDominatedArrays(List<ProductsSet> lists) {
        int checked = 0;
        Collections.sort(lists);
        List<ProductsSet> result = new ArrayList<>();
        for (ProductsSet candidate : lists) {
            boolean isDominated = false;
            for (ProductsSet existing : result) {
                checked++;
                if (existing.dominates(candidate)) {
                    isDominated = true;
                    break;
                }
            }
            if (!isDominated)
                result.add(candidate);
        }
        System.out.println("проверено: " + checked);
        return result;
    }
}
