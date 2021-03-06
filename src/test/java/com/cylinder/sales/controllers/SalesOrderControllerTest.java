package com.cylinder.sales.controllers;

import com.cylinder.ControllerTests;
import com.cylinder.accounts.model.AccountRepository;
import com.cylinder.contacts.model.ContactRepository;
import com.cylinder.products.model.ProductRepository;
import com.cylinder.sales.model.*;
import com.cylinder.sales.model.forms.SalesOrderForm;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;

import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class SalesOrderControllerTest extends ControllerTests {

    @InjectMocks
    SalesOrderController salesOrderController;

    @MockBean
    private SalesOrderRepository salesOrderRepository;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private QuoteRepository quoteRepository;

    @MockBean
    private ProductSalesOrderRepository productSalesOrderRepository;

    @MockBean
    private AccountRepository accountRepository;

    @MockBean
    private ContactRepository contactRepository;

    @MockBean
    private ContractRepository contractRepository;

    @MockBean
    private ArrayList<SalesOrderForm> salesOrderForms;


    private ArrayList<SalesOrder> mockSalesOrderListData() {
        ArrayList<SalesOrder> salesOrders = new ArrayList();
        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setSalesOrderId(new Long("1"));
        salesOrders.add(salesOrder);
        salesOrder = new SalesOrder();
        salesOrder.setSalesOrderId(new Long("2"));
        salesOrders.add(salesOrder);
        salesOrder = new SalesOrder();
        salesOrder.setSalesOrderId(new Long("3"));
        salesOrders.add(salesOrder);
        return salesOrders;
    }

    private SalesOrder mockSingleSalesOrderData() {
        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setSalesOrderId(new Long("1"));
        return salesOrder;
    }

    private ArrayList<Quote> mockQuoteData() {
        ArrayList<Quote> quotes = new ArrayList();
        Quote quote = new Quote();
        quote.setQuoteId(new Long("1"));
        quotes.add(quote);
        quote = new Quote();
        quote.setQuoteId(new Long("2"));
        quotes.add(quote);
        quote = new Quote();
        quote.setQuoteId(new Long("3"));
        quotes.add(quote);
        return quotes;
    }

    private ArrayList<ProductSalesOrder> mockProductSalesOrderData() {
        ArrayList<ProductSalesOrder> productSalesOrders = new ArrayList();
        ProductSalesOrder productSalesOrder = new ProductSalesOrder();
        productSalesOrder.setSalesOrderProductId(new Long("1"));
        productSalesOrders.add(productSalesOrder);
        productSalesOrder = new ProductSalesOrder();
        productSalesOrder.setSalesOrderProductId(new Long("2"));
        productSalesOrders.add(productSalesOrder);
        productSalesOrder = new ProductSalesOrder();
        productSalesOrder.setSalesOrderProductId(new Long("3"));
        productSalesOrders.add(productSalesOrder);
        return productSalesOrders;
    }

    private ArrayList<SalesOrderForm> mockSalesOrderFormListData(Iterable<SalesOrder> salesOrders) {
        ArrayList<SalesOrderForm> salesOrderForms = new ArrayList();
        SalesOrderForm salesOrderForm;
        for (SalesOrder salesOrder : salesOrders) {
            if (salesOrder != null) {
                salesOrderForm = new SalesOrderForm(salesOrder, null);
                salesOrderForms.add(salesOrderForm);
            }
        }
        return salesOrderForms;
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(salesOrderController)
                .apply(springSecurity(super.springSecurityFilterChain))
                .build();
        super.mockUserRepository();
        Iterable<SalesOrder> salesOrders = mockSalesOrderListData();
        Iterable<Quote> quotes = mockQuoteData();
        Iterable<ProductSalesOrder> productSalesOrders = mockProductSalesOrderData();
        salesOrderForms = mockSalesOrderFormListData(salesOrders);
        given(this.salesOrderRepository.findAll()).willReturn(salesOrders);
        given(this.salesOrderRepository.findOne(eq(new Long("1")))).willReturn(mockSingleSalesOrderData());
        given(this.salesOrderRepository.findOne(eq(new Long("1")))).willReturn(null);
        given(this.salesOrderRepository.existsById(new Long("1"))).willReturn(true);
        given(this.salesOrderRepository.existsById(new Long("5"))).willReturn(false);
        given(this.salesOrderRepository.save(any(SalesOrder.class))).willReturn(mockSingleSalesOrderData());
        given(this.quoteRepository.findAll()).willReturn(quotes);
        given(this.productSalesOrderRepository.findAll()).willReturn(productSalesOrders);
    }

    @Test
    @WithMockUser(username = "fake@mail.com", authorities = "USER")
    public void testIndex() throws Exception {
        this.mockMvc.perform(get("/salesorder"))
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    public void testIndexWithAnon() throws Exception {
        this.mockMvc.perform(get("/salesorder"))
                .andExpect(status().isFound());
    }

    @Test
    @WithMockUser(username = "fake@mail.com", authorities = "USER")
    public void testRecordWithExistantRecord() throws Exception {
        this.mockMvc.perform(get("/salesorder/records/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "fake@mail.com", authorities = "USER")
    public void testRecordWithNonExistantRecord() throws Exception {
        this.mockMvc.perform(get("/salesorder/records/5"))
                .andExpect(status().isNotFound());
    }


    @Test
    @WithMockUser(username = "fake@mail.com", authorities = "USER")
    public void testGetEditRecordWithExistantRecord() throws Exception {
        this.mockMvc.perform(get("/salesorder/edit/{id}", new Long("1")).with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "fake@mail.com", authorities = "USER")
    public void testGetEditRecordWithNonExistantRecord() throws Exception {
        this.mockMvc.perform(get("/salesorder/edit/{id}", new Long("5")).with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "fake@mail.com", authorities = "USER")
    public void testPostEditRecordWithExistantRecord() throws Exception {
        this.mockMvc.perform(post("/salesorder/edit/{id}", new Long("1"))
                .param("salesOrder.salesOrderId", "1")
                .param("salesOrder.shippingAddress.apartmentNumber", "null")
                .param("salesOrder.shippingAddress.city", "null")
                .param("salesOrder.shippingAddress.streetAddress", "null")
                .param("salesOrder.shippingAddress.stateProv", "null")
                .param("salesOrder.shippingAddress.country", "null")
                .param("salesOrder.shippingAddress.zipPostal", "null")
                .param("salesOrder.shippingAddress.poBox", "null")
                .param("salesOrder.billingAddress.apartmentNumber", "null")
                .param("salesOrder.billingAddress.city", "null")
                .param("salesOrder.billingAddress.streetAddress", "null")
                .param("salesOrder.billingAddress.stateProv", "null")
                .param("salesOrder.billingAddress.country", "null")
                .param("salesOrder.billingAddress.zipPostal", "null")
                .param("salesOrder.billingAddress.poBox", "null")
                .param("salesOrder.invoiceNumber", "5")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/salesorder/records/1"));
        verify(this.salesOrderRepository, times(1)).save(any(SalesOrder.class));
    }

    @Test
    @WithMockUser(username = "fake@mail.com", authorities = "USER")
    public void testPostEditRecordWithNonExistantRecord() throws Exception {
        this.mockMvc.perform(post("/salesorder/edit/{id}", new Long("5"))
                .param("salesOrder.salesOrderId", "5")
                .param("salesOrder.shippingAddress.apartmentNumber", "null")
                .param("salesOrder.shippingAddress.city", "null")
                .param("salesOrder.shippingAddress.streetAddress", "null")
                .param("salesOrder.shippingAddress.stateProv", "null")
                .param("salesOrder.shippingAddress.country", "null")
                .param("salesOrder.shippingAddress.zipPostal", "null")
                .param("salesOrder.shippingAddress.poBox", "null")
                .param("salesOrder.billingAddress.apartmentNumber", "null")
                .param("salesOrder.billingAddress.city", "null")
                .param("salesOrder.billingAddress.streetAddress", "null")
                .param("salesOrder.billingAddress.stateProv", "null")
                .param("salesOrder.billingAddress.country", "null")
                .param("salesOrder.billingAddress.zipPostal", "null")
                .param("salesOrder.billingAddress.poBox", "null")
                .param("salesOrder.invoiceNumber", "6")
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "fake@mail.com", authorities = "USER")
    public void testPostEditRecordWithExistantRecordInvalidData() throws Exception {
        this.mockMvc.perform(post("/salesorder/edit/{id}", new Long("1"))
                .param("salesOrder.salesOrderId", "1")
                .param("salesOrder.shippingAddress.apartmentNumber", "null")
                .param("salesOrder.shippingAddress.city", "null")
                .param("salesOrder.shippingAddress.streetAddress", "null")
                .param("salesOrder.shippingAddress.stateProv", "null")
                .param("salesOrder.shippingAddress.country", "null")
                .param("salesOrder.shippingAddress.zipPostal", "null")
                .param("salesOrder.shippingAddress.poBox", "null")
                .param("salesOrder.billingAddress.apartmentNumber", "null")
                .param("salesOrder.billingAddress.city", "null")
                .param("salesOrder.billingAddress.streetAddress", "null")
                .param("salesOrder.billingAddress.stateProv", "null")
                .param("salesOrder.billingAddress.country", "null")
                .param("salesOrder.billingAddress.zipPostal", "null")
                .param("salesOrder.billingAddress.poBox", "null")
                .param("salesOrder.invoiceNumber", "q")
                .with(csrf()))
                .andExpect(model().attributeHasFieldErrors("salesOrderData", "salesOrder.invoiceNumber"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "fake@mail.com", authorities = "USER")
    public void testNewRecord() throws Exception {
        this.mockMvc.perform(get("/salesorder/new/")
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "fake@mail.com", authorities = "USER")
    public void testPostNewRecordWithValidData() throws Exception {
        this.mockMvc.perform(post("/salesorder/new/")
                .param("salesOrder.salesOrderId", "1")
                .param("salesOrder.shippingAddress.apartmentNumber", "null")
                .param("salesOrder.shippingAddress.city", "null")
                .param("salesOrder.shippingAddress.streetAddress", "null")
                .param("salesOrder.shippingAddress.stateProv", "null")
                .param("salesOrder.shippingAddress.country", "null")
                .param("salesOrder.shippingAddress.zipPostal", "null")
                .param("salesOrder.shippingAddress.poBox", "null")
                .param("salesOrder.billingAddress.apartmentNumber", "null")
                .param("salesOrder.billingAddress.city", "null")
                .param("salesOrder.billingAddress.streetAddress", "null")
                .param("salesOrder.billingAddress.stateProv", "null")
                .param("salesOrder.billingAddress.country", "null")
                .param("salesOrder.billingAddress.zipPostal", "null")
                .param("salesOrder.billingAddress.poBox", "null")
                .param("salesOrder.invoiceNumber", "6")
                .with(csrf()))
                .andExpect(status().is3xxRedirection());
        verify(this.salesOrderRepository, times(1)).save(any(SalesOrder.class));
    }

    @Test
    @WithMockUser(username = "fake@mail.com", authorities = "USER")
    public void testPostNewRecordWithInvalidData() throws Exception {
        this.mockMvc.perform(post("/salesorder/new/")
                .param("salesOrder.salesOrderId", "1")
                .param("salesOrder.shippingAddress.apartmentNumber", "null")
                .param("salesOrder.shippingAddress.city", "null")
                .param("salesOrder.shippingAddress.streetAddress", "null")
                .param("salesOrder.shippingAddress.stateProv", "null")
                .param("salesOrder.shippingAddress.country", "null")
                .param("salesOrder.shippingAddress.zipPostal", "null")
                .param("salesOrder.shippingAddress.poBox", "null")
                .param("salesOrder.billingAddress.apartmentNumber", "null")
                .param("salesOrder.billingAddress.city", "null")
                .param("salesOrder.billingAddress.streetAddress", "null")
                .param("salesOrder.billingAddress.stateProv", "null")
                .param("salesOrder.billingAddress.country", "null")
                .param("salesOrder.billingAddress.zipPostal", "null")
                .param("salesOrder.billingAddress.poBox", "null")
                .param("salesOrder.invoiceNumber", "q")
                .with(csrf()))
                .andExpect(model().attributeHasFieldErrors("salesOrderData", "salesOrder.invoiceNumber"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "fake@mail.com", authorities = "USER")
    public void testAddRowToExisting() throws Exception {
        this.mockMvc.perform(get("/salesorder/edit/{id}", new Long("1"))
                .param("addRow", "addRow")
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "fake@mail.com", authorities = "USER")
    public void testAddRowToNew() throws Exception {
        this.mockMvc.perform(get("/salesorder/new/")
                .param("addRow", "addRow")
                .with(csrf()))
                .andExpect(status().isOk());
    }
}
