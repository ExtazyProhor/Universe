package ru.prohor.universe.jocasta.core.collections;

import java.util.List;

public class PaginatorTest { // TODO убрать _
    //@Test
    public void testPaginate_FirstPage() {
        List<Integer> list = List.of(1, 2, 3, 4, 5);
        List<Integer> page = Paginator.paginate(list, 0, 2);
        //Assertions.assertEquals(List.of(1, 2), page);
    }

    //@Test
    public void testPaginate_SecondPage() {
        List<Integer> list = List.of(1, 2, 3, 4, 5);
        List<Integer> page = Paginator.paginate(list, 1, 2);
        //Assertions.assertEquals(List.of(3, 4), page);
    }

    //@Test
    public void testPaginate_LastPageIncomplete() {
        List<Integer> list = List.of(1, 2, 3, 4, 5);
        List<Integer> page = Paginator.paginate(list, 2, 2);
        //Assertions.assertEquals(List.of(5), page);
    }

    //@Test
    public void testPaginate_EmptyList() {
        List<Integer> list = List.of();
        List<Integer> page = Paginator.paginate(list, 0, 5);
        //Assertions.assertTrue(page.isEmpty());
    }

    //@Test
    public void testPaginateOrLastPage_ValidPage() {
        List<Integer> list = List.of(10, 20, 30, 40, 50);
        List<Integer> page = Paginator.paginateOrLastPage(list, 0, 2);
        //Assertions.assertEquals(List.of(10, 20), page);
    }

    //@Test
    public void testPaginateOrLastPage_PageTooHigh_ReturnsLast() {
        List<Integer> list = List.of(10, 20, 30, 40, 50);
        List<Integer> page = Paginator.paginateOrLastPage(list, 10, 2);
        //Assertions.assertEquals(List.of(50), page);
    }

    //@Test
    public void testPaginateOrLastPage_EmptyList_ReturnsEmpty() {
        List<Integer> list = List.of();
        List<Integer> page = Paginator.paginateOrLastPage(list, 5, 2);
        //Assertions.assertTrue(page.isEmpty());
    }

    //@Test
    public void testCheck_PageNegative_ThrowsException() {
        List<Integer> list = List.of(1, 2, 3);
        //Assertions.assertThrows(IllegalArgumentException.class, () -> Paginator.paginate(list, -1, 2));
    }

    //@Test
    public void testCheck_SizeZero_ThrowsException() {
        List<Integer> list = List.of(1, 2, 3);
        //Assertions.assertThrows(IllegalArgumentException.class, () -> Paginator.paginate(list, 0, 0));
    }

    //@Test
    public void testPaginate_PageBeyondRange_ReturnsEmptyList() {
        List<Integer> list = List.of(1, 2, 3);
        List<Integer> page = Paginator.paginate(list, 5, 2);
        //Assertions.assertTrue(page.isEmpty());
    }

    //@Test
    public void testPaginate_LargePageAndSize_NoOverflow() {
        List<Integer> list = List.of(1, 2, 3);
        List<Integer> page = Paginator.paginate(list, 1_000_000, 1000);
        //Assertions.assertTrue(page.isEmpty());
    }
}
