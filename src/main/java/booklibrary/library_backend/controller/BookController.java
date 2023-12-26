/*
 * Copyright (c) wbq 2023.
 */

package booklibrary.library_backend.controller;

import booklibrary.library_backend.entity.RestBean;
import booklibrary.library_backend.entity.database_obj.Book;
import booklibrary.library_backend.entity.database_obj.Borrow;
import booklibrary.library_backend.entity.view_obj.request.BidViewObj;
import booklibrary.library_backend.entity.view_obj.request.BookViewObj;
import booklibrary.library_backend.entity.view_obj.request.BorrowBookViewObj;
import booklibrary.library_backend.service.BookService;
import booklibrary.library_backend.service.BorrowService;
import booklibrary.library_backend.utils.JsonUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @Description: 书籍相关接口
 * @Author: 王贝强
 * @Date: 2023/12/26
 */
@Validated
@RestController
@RequestMapping("/api/book")
public class BookController {
    @Resource
    BookService bookService;
    @Resource
    BorrowService borrowService;

    /**
     * @Description: 用户借阅书籍
     * @Param: [user_id, book_id]
     * @return: RestBean<String>
     * @Author: 王贝强
     * @Date: 2023/12/26
     */
    @PostMapping("borrowBook")
    public RestBean<String> borrowBookById(@RequestBody @Validated BorrowBookViewObj obj) {
        Borrow borrow = new Borrow();
        borrow.setBookId(Integer.valueOf(obj.getBid()))
                .setUserId(Integer.valueOf(obj.getUid()))
                .setBorrowTime(new Date());
        boolean save = borrowService.save(borrow);
        if (save) return RestBean.success("借阅成功");
        else return RestBean.failure(401, "服务器错误");
    }
    /**
    * @Description: 管理员添加书籍
    * @Param: [BookViewObj]
    * @return: RestBean<String>
    * @Author: 王贝强
    * @Date: 2023/12/28
    */
    @PostMapping("addBook")
    public RestBean<String> addBook(@RequestBody @Validated BookViewObj obj) {
        Book book=new Book()
                .setTitle(obj.getTitle())
                .setAuthor(obj.getAuthor())
                .setDesc(obj.getDesc())
                .setLabel(obj.getLabel());
        boolean save = bookService.save(book);
        if (save) return RestBean.success("书籍添加成功");
        else return RestBean.failure(401, "服务器错误");
    }
    /**
    * @Description: 管理员删除书籍
    * @Param: [bid]
    * @return: RestBean<String>
    * @Author: 王贝强
    * @Date: 2023/12/28
    */
    @PostMapping("deleteBook")
    public RestBean<String> deleteBook(@RequestBody @Validated BidViewObj obj) {
        boolean save = bookService.removeById(Integer.parseInt(obj.getBid()));
        if (save) return RestBean.success("书籍删除成功");
        else return RestBean.failure(401, "服务器错误");
    }
    /**
     * @Description: 用户归还书籍接口
     * @Param: [user_id, book_id]
     * @return: RestBean<String>
     * @Author: 王贝强
     * @Date: 2023/12/26
     */
    @PostMapping("returnBook")
    public RestBean<String> returnBookById(@RequestBody @Validated BorrowBookViewObj obj) {
        QueryWrapper<Borrow> wrapper = new QueryWrapper<>();
        wrapper.select().eq("userId", obj.getUid()).eq("bookId", obj.getBid());
        boolean remove = borrowService.remove(wrapper);
        if (!remove) return RestBean.failure(402, "借阅信息不存在");
        else return RestBean.success("书籍归还成功");
    }


    /**
     * @Description: 当前馆藏书籍数量
     * @Param: null
     * @return: RestBean<String>
     * @Author: 王贝强
     * @Date: 2023/12/26
     */
    @GetMapping("/bookCount")
    public RestBean<String> getBookCount() {
        return RestBean.success(bookService.getBookCount());
    }

    /**
     * @Description: 根据不同参数返回书籍列表信息
     * @Param: [type：book->书籍列表 c_borrow->user能够借阅的书籍列表]
     * @return: RestBean<String>
     * @Author: 王贝强
     * @Date: 2023/12/26
     */
    @GetMapping("/bookList")
    public RestBean<String> getBookListByType(@RequestParam @Pattern(regexp = "(book|c_borrow)") String type) {
        if (Objects.equals(type, "book")) {
            List<Book> bookList = bookService.list();
            return RestBean.success(JsonUtil.toJson(bookList));
        } else {
            List<Borrow> borrowsBookList = borrowService.query().list();
            List<Integer> borrows = borrowsBookList
                    .stream()
                    .map(Borrow::getBookId)
                    .toList();
            List<Book> bookList = bookService.list();
            List<Book> c_borrow = bookList
                    .stream()
                    .filter(book -> !borrows.contains(book.getBid()))
                    .toList();
            return RestBean.success(JsonUtil.toJson(c_borrow));
        }
    }
}