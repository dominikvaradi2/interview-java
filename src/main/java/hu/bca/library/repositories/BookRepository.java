package hu.bca.library.repositories;

import hu.bca.library.models.Book;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Collection;

public interface BookRepository extends CrudRepository<Book, Long> {
    @Query("""
            select distinct b from Book b inner join b.authors authors
            where upper(authors.country) = upper(:authorCountryCode) and (:firstReleaseYearFrom is null or b.year >= :firstReleaseYearFrom)
            order by b.year asc""")
    Collection<Book> findAllByAuthorCountryCodeAndOptionallyFirstReleaseYearAfter(@Param("authorCountryCode") @NonNull String authorCountryCode, @Param("firstReleaseYearFrom") @Nullable Integer firstReleaseYearFrom);
}
