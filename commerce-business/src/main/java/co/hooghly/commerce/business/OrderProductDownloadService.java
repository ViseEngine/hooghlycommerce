package co.hooghly.commerce.business;

import java.util.List;


import org.springframework.stereotype.Service;

import co.hooghly.commerce.domain.OrderProductDownload;
import co.hooghly.commerce.repository.OrderProductDownloadRepository;

@Service
public class OrderProductDownloadService  extends SalesManagerEntityServiceImpl<Long, OrderProductDownload>  {

    

    private final OrderProductDownloadRepository orderProductDownloadRepository;

    
    public OrderProductDownloadService(OrderProductDownloadRepository orderProductDownloadRepository) {
        super(orderProductDownloadRepository);
        this.orderProductDownloadRepository = orderProductDownloadRepository;
    }
    
    
    public List<OrderProductDownload> getByOrderId(Long orderId) {
    	return orderProductDownloadRepository.findByOrderId(orderId);
    }


}
