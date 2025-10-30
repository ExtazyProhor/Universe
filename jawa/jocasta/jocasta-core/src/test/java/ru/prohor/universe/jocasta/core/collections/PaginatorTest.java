package ru.prohor.universe.jocasta.core.collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class PaginatorTest {
    @Test
    public void testPaginateFirstPage() {
        List<Integer> list = List.of(1, 2, 3, 4, 5);
        List<Integer> page = Paginator.paginate(list, 0, 2);
        Assertions.assertEquals(List.of(1, 2), page);
    }

    @Test
    public void testPaginateSecondPage() {
        List<Integer> list = List.of(1, 2, 3, 4, 5);
        List<Integer> page = Paginator.paginate(list, 1, 2);
        Assertions.assertEquals(List.of(3, 4), page);
    }

    @Test
    public void testPaginateLastPageIncomplete() {
        List<Integer> list = List.of(1, 2, 3, 4, 5);
        List<Integer> page = Paginator.paginate(list, 2, 2);
        Assertions.assertEquals(List.of(5), page);
    }

    @Test
    public void testPaginateEmptyList() {
        List<Integer> list = List.of();
        List<Integer> page = Paginator.paginate(list, 0, 5);
        Assertions.assertTrue(page.isEmpty());
    }

    @Test
    public void testPaginateOrLastPageValidPage() {
        List<Integer> list = List.of(10, 20, 30, 40, 50);
        List<Integer> page = Paginator.paginateOrLastPage(list, 0, 2);
        Assertions.assertEquals(List.of(10, 20), page);
    }

    @Test
    public void testPaginateOrLastPagePageTooHighReturnsLast() {
        List<Integer> list = List.of(10, 20, 30, 40, 50);
        List<Integer> page = Paginator.paginateOrLastPage(list, 10, 2);
        Assertions.assertEquals(List.of(50), page);
    }

    @Test
    public void testPaginateOrLastPageEmptyListReturnsEmpty() {
        List<Integer> list = List.of();
        List<Integer> page = Paginator.paginateOrLastPage(list, 5, 2);
        Assertions.assertTrue(page.isEmpty());
    }

    @Test
    public void testCheckPageNegativeThrowsException() {
        List<Integer> list = List.of(1, 2, 3);
        Assertions.assertThrows(IllegalArgumentException.class, () -> Paginator.paginate(list, -1, 2));
    }

    @Test
    public void testCheckSizeZeroThrowsException() {
        List<Integer> list = List.of(1, 2, 3);
        Assertions.assertThrows(IllegalArgumentException.class, () -> Paginator.paginate(list, 0, 0));
    }

    @Test
    public void testPaginatePageBeyondRangeReturnsEmptyList() {
        List<Integer> list = List.of(1, 2, 3);
        List<Integer> page = Paginator.paginate(list, 5, 2);
        Assertions.assertTrue(page.isEmpty());
    }

    @Test
    public void testPaginateLargePageAndSizeNoOverflow() {
        List<Integer> list = List.of(1, 2, 3);
        List<Integer> page = Paginator.paginate(list, 1000000, 1000);
        Assertions.assertTrue(page.isEmpty());
    }
}
