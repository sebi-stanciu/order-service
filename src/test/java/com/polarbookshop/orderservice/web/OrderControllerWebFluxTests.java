package com.polarbookshop.orderservice.web;

import com.polarbookshop.orderservice.domain.Order;
import com.polarbookshop.orderservice.domain.OrderService;
import com.polarbookshop.orderservice.domain.OrderStatus;
import com.polarbookshop.orderservice.order.web.OrderController;
import com.polarbookshop.orderservice.order.web.OrderRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest(OrderController.class)
public class OrderControllerWebFluxTests {

    @MockBean
    private OrderService orderService;

    @Autowired
    private WebTestClient webClient;

    @Test
    void whenBookNotAvailableThenRejectOrder() {
        var orderRequest = new OrderRequest("1234567890", 3);
        var expectedOrder = OrderService.buildRejectedOrder(
                orderRequest.isbn(), orderRequest.quantity());
        when(orderService.submitOrder(
                orderRequest.isbn(), orderRequest.quantity())
        ).thenReturn(Mono.just(expectedOrder));

        webClient
                .post()
                .uri("/orders/")
                .bodyValue(orderRequest)
                .exchange()
                .expectStatus().is2xxSuccessful()
      .expectBody(Order.class).value(actualOrder -> {
            assertThat(actualOrder).isNotNull();
            assertThat(actualOrder.status()).isEqualTo(OrderStatus.REJECTED);
        });
    }

}
