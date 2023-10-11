package by.devtools.order.mapper;

import by.devtools.domain.OrderDto;
import by.devtools.order.dto.OrderCreate;
import by.devtools.order.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface OrderMapper {

    OrderDto orderToDto(Order order);

    Order createToOrder(OrderCreate request);
}
