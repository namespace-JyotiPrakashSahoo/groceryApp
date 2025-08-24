package com.example.groceryApp.services;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.groceryApp.dto.CustomerResponseDTO;
import com.example.groceryApp.dto.GroceryItemResponseDTO;
import com.example.groceryApp.dto.OrderItemRequestDTO;
import com.example.groceryApp.dto.OrderItemResponseDTO;
import com.example.groceryApp.dto.OrderRequestDTO;
import com.example.groceryApp.dto.OrderResponseDTO;
import com.example.groceryApp.exceptions.BadRequestException;
import com.example.groceryApp.exceptions.ResourceNotFoundException;
import com.example.groceryApp.models.Customer;
import com.example.groceryApp.models.GroceryItem;
import com.example.groceryApp.models.Order;
import com.example.groceryApp.models.OrderItem;
import com.example.groceryApp.repositories.CustomerRepository;
import com.example.groceryApp.repositories.GroceryItemRepository;
import com.example.groceryApp.repositories.OrderRepository;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private GroceryItemRepository groceryItemRepository;

    @Autowired
    private ModelMapper modelMapper;

    public OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO) {
        // Validate customer
        Customer customer = customerRepository.findById(orderRequestDTO.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with id: " + orderRequestDTO.getCustomerId()));

        // Create order
        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderDate(new Date());
        order.setTotalPrice(0.0);

        // Process order items
        double totalPrice = 0.0;

        for (OrderItemRequestDTO itemRequest : orderRequestDTO.getItems()) {
            GroceryItem groceryItem = groceryItemRepository.findById(itemRequest.getGroceryItemId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Grocery item not found with id: " + itemRequest.getGroceryItemId()));

            // Check stock
            if (groceryItem.getQuantity() < itemRequest.getQuantity()) {
                throw new BadRequestException("Insufficient stock for item: " + groceryItem.getName());
            }

            // Update stock
            groceryItem.setQuantity(groceryItem.getQuantity() - itemRequest.getQuantity());
            groceryItemRepository.save(groceryItem);

            // Create order item
            OrderItem orderItem = new OrderItem();
            orderItem.setGroceryItem(groceryItem);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.calculateValues(); // Calculate price and subtotal

            // Use helper method to maintain bidirectional relationship
            order.addOrderItem(orderItem);

            totalPrice += orderItem.getSubtotal();
        }

        // Set total price and save
        order.setTotalPrice(totalPrice);
        Order savedOrder = orderRepository.save(order);

        return convertToResponseDTO(savedOrder);
    }

    public List<OrderResponseDTO> getOrdersByCustomerId(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        List<Order> orders = orderRepository.findByCustomerIdWithItems(customerId);
        return orders.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<OrderResponseDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAllWithItems(); // Use the new method
        return orders.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public OrderResponseDTO getOrderById(Long id) {
        // Use a custom query to fetch order with items
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        // Force initialization of lazy collection
        if (order.getOrderItems() != null) {
            order.getOrderItems().size(); // This triggers lazy loading
        }

        return convertToResponseDTO(order);
    }

    private OrderResponseDTO convertToResponseDTO(Order order) {
        OrderResponseDTO responseDTO = new OrderResponseDTO();
        responseDTO.setId(order.getId());
        responseDTO.setOrderDate(order.getOrderDate());
        responseDTO.setTotalPrice(order.getTotalPrice());

        // Convert customer to DTO (without orders to avoid circular reference)
        CustomerResponseDTO customerDTO = new CustomerResponseDTO();
        customerDTO.setId(order.getCustomer().getId());
        customerDTO.setName(order.getCustomer().getName());
        customerDTO.setEmail(order.getCustomer().getEmail());
        customerDTO.setAddress(order.getCustomer().getAddress());
        customerDTO.setPhone(order.getCustomer().getPhone());
        responseDTO.setCustomer(customerDTO);

        // Convert order items to DTOs
        List<OrderItemResponseDTO> itemDTOs = order.getOrderItems().stream()
                .map(this::convertOrderItemToResponseDTO)
                .collect(Collectors.toList());
        responseDTO.setItems(itemDTOs);

        return responseDTO;
    }

    private OrderItemResponseDTO convertOrderItemToResponseDTO(OrderItem orderItem) {
        OrderItemResponseDTO itemDTO = new OrderItemResponseDTO();
        itemDTO.setQuantity(orderItem.getQuantity());
        itemDTO.setItemPrice(orderItem.getItemPrice());
        itemDTO.setSubtotal(orderItem.getSubtotal());

        // Convert grocery item to DTO
        GroceryItemResponseDTO groceryItemDTO = new GroceryItemResponseDTO();
        groceryItemDTO.setId(orderItem.getGroceryItem().getId());
        groceryItemDTO.setName(orderItem.getGroceryItem().getName());
        groceryItemDTO.setCategory(orderItem.getGroceryItem().getCategory());
        groceryItemDTO.setPrice(orderItem.getGroceryItem().getPrice());
        groceryItemDTO.setQuantity(orderItem.getGroceryItem().getQuantity());

        itemDTO.setGroceryItem(groceryItemDTO);

        return itemDTO;
    }

    public void deleteOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        // Restore grocery item quantities before deleting the order
        restoreGroceryItemQuantities(order);

        orderRepository.delete(order);
    }

    public void deleteOrdersByCustomerId(Long customerId) {
        List<Order> orders = orderRepository.findByCustomerId(customerId);

        if (orders.isEmpty()) {
            throw new ResourceNotFoundException("No orders found for customer ID: " + customerId);
        }

        // First, restore grocery item quantities before deleting orders
        for (Order order : orders) {
            restoreGroceryItemQuantities(order);
        }

        // Then delete all orders for this customer
        orderRepository.deleteAll(orders);
    }

    private void restoreGroceryItemQuantities(Order order) {
        if (order.getOrderItems() != null) {
            for (OrderItem orderItem : order.getOrderItems()) {
                GroceryItem groceryItem = orderItem.getGroceryItem();
                if (groceryItem != null) {
                    // Restore the quantity that was deducted when order was created
                    groceryItem.setQuantity(groceryItem.getQuantity() + orderItem.getQuantity());
                    groceryItemRepository.save(groceryItem);
                }
            }
        }
    }

    public boolean customerHasOrders(Long customerId) {
        return !orderRepository.findByCustomerId(customerId).isEmpty();
    }
}