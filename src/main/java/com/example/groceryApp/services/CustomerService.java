package com.example.groceryApp.services;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.groceryApp.dto.CustomerDTO;
import com.example.groceryApp.dto.CustomerResponseDTO;
import com.example.groceryApp.dto.GroceryItemResponseDTO;
import com.example.groceryApp.dto.OrderItemResponseDTO;
import com.example.groceryApp.dto.OrderResponseDTO;
import com.example.groceryApp.exceptions.BadRequestException;
import com.example.groceryApp.exceptions.ResourceNotFoundException;
import com.example.groceryApp.models.Customer;
import com.example.groceryApp.models.Order;
import com.example.groceryApp.models.OrderItem;
import com.example.groceryApp.repositories.CustomerRepository;
import com.example.groceryApp.repositories.OrderRepository;


@Service
@Transactional
public class CustomerService {
    
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService; 
    
    @Autowired
    private ModelMapper modelMapper;
    
    public CustomerResponseDTO createCustomer(CustomerDTO customerDTO) {
        if (customerRepository.existsByEmail(customerDTO.getEmail())) {
            throw new BadRequestException("Email already exists: " + customerDTO.getEmail());
        }
        
        Customer customer = modelMapper.map(customerDTO, Customer.class);
        Customer savedCustomer = customerRepository.save(customer);
        
        return convertToResponseDTO(savedCustomer);
    }
    
    public CustomerResponseDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        
        return convertToResponseDTO(customer);
    }
    
    public List<CustomerResponseDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    private CustomerResponseDTO convertToResponseDTO(Customer customer) {
        CustomerResponseDTO responseDTO = modelMapper.map(customer, CustomerResponseDTO.class);
        
        // Convert orders to DTOs
        if (customer.getOrders() != null) {
            List<OrderResponseDTO> orderDTOs = customer.getOrders().stream()
                    .map(this::convertOrderToResponseDTO)
                    .collect(Collectors.toList());
            responseDTO.setOrders(orderDTOs);
        }
        
        return responseDTO;
    }
    
    private OrderResponseDTO convertOrderToResponseDTO(Order order) {
        OrderResponseDTO orderDTO = new OrderResponseDTO();
        orderDTO.setId(order.getId());
        orderDTO.setOrderDate(order.getOrderDate());
        orderDTO.setTotalPrice(order.getTotalPrice());
        
        // Convert order items to DTOs
        List<OrderItemResponseDTO> itemDTOs = order.getOrderItems().stream()
                .map(this::convertOrderItemToResponseDTO)
                .collect(Collectors.toList());
        orderDTO.setItems(itemDTOs);
        
        return orderDTO;
    }
    
    private OrderItemResponseDTO convertOrderItemToResponseDTO(OrderItem orderItem) {
        OrderItemResponseDTO itemDTO = new OrderItemResponseDTO();
        itemDTO.setQuantity(orderItem.getQuantity());
        itemDTO.setItemPrice(orderItem.getItemPrice());
        itemDTO.setSubtotal(orderItem.getSubtotal());
        
        // Convert grocery item to DTO
        GroceryItemResponseDTO groceryItemDTO = modelMapper.map(orderItem.getGroceryItem(), GroceryItemResponseDTO.class);
        itemDTO.setGroceryItem(groceryItemDTO);
        
        return itemDTO;
    }

    public CustomerResponseDTO updateCustomer(Long id, CustomerDTO customerDTO) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        
        // Check if email is being changed and if new email already exists
        if (!existingCustomer.getEmail().equals(customerDTO.getEmail()) && 
            customerRepository.existsByEmail(customerDTO.getEmail())) {
            throw new BadRequestException("Email already exists: " + customerDTO.getEmail());
        }
        
        // Update the existing customer with new values
        existingCustomer.setName(customerDTO.getName());
        existingCustomer.setEmail(customerDTO.getEmail());
        existingCustomer.setAddress(customerDTO.getAddress());
        existingCustomer.setPhone(customerDTO.getPhone());
        
        Customer updatedCustomer = customerRepository.save(existingCustomer);
        return convertToResponseDTO(updatedCustomer);
    }

    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        
        // Check if customer has existing orders
        boolean hasOrders = orderService.customerHasOrders(id);
        
        if (hasOrders) {
            // Option 1: Throw exception (current behavior)
            throw new BadRequestException("Cannot delete customer with ID " + id + 
                                        " because they have existing orders. Delete orders first.");
            
            // Option 2: Auto-delete orders (uncomment if you want this behavior)
            // orderService.deleteOrdersByCustomerId(id);
        }
        
        customerRepository.delete(customer);
    }

    public void deleteCustomerWithOrders(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        
        // This will automatically delete orders due to CascadeType.ALL or orphanRemoval
        customerRepository.delete(customer);
    }
}