package hello.springtx.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("select o.username from Order o where o.username = ?1")
    void selectByName(String username);
}
