package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tangocard.raas.RaasClient;
import com.tangocard.raas.models.AccountModel;
import com.tangocard.raas.models.AccountSummaryModel;
import com.tangocard.raas.models.CatalogModel;
import com.tangocard.raas.models.CreateAccountRequestModel;
import com.tangocard.raas.models.CreateCustomerRequestModel;
import com.tangocard.raas.models.CreateOrderRequestModel;
import com.tangocard.raas.models.CustomerModel;
import com.tangocard.raas.models.GetOrdersInput;
import com.tangocard.raas.models.GetOrdersResponseModel;
import com.tangocard.raas.models.NameEmailModel;
import com.tangocard.raas.models.OrderModel;
import com.tangocard.raas.models.ResendOrderResponseModel;
import com.tangocard.raas.models.SystemStatusResponseModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
@Log4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class Example {

    private final RaasClient raasClient;
    private final ObjectMapper objectMapper;

    @EventListener(ContextRefreshedEvent.class)
    public void run() throws Throwable {
        Long timestamp = Instant.now().getEpochSecond();

        log.info("Testing API at " + timestamp);

        SystemStatusResponseModel systemStatusResponseModel = raasClient.getStatus().getSystemStatus();
        log.info("Get System Status: " + json(systemStatusResponseModel));

        CatalogModel catalogModel = raasClient.getCatalog().getCatalog();
        log.info("Get Catalog: " + json(catalogModel));

        String customerIdentifier = "customer" + timestamp;
        CreateCustomerRequestModel createCustomerRequestModel = new CreateCustomerRequestModel();
        createCustomerRequestModel.setCustomerIdentifier(customerIdentifier);
        createCustomerRequestModel.setDisplayName(customerIdentifier);
        log.info("Create Customer: " + json(createCustomerRequestModel));
        CustomerModel createdCustomerModel = raasClient.getCustomers().createCustomer(createCustomerRequestModel);
        log.info("Created Customer: " + json(createdCustomerModel));

        List<CustomerModel> customersModel = raasClient.getCustomers().getAllCustomers();
        log.info("List Customers: " + json(customersModel));

        CustomerModel customerModel = raasClient.getCustomers().getCustomer(customerIdentifier);
        log.info("Get Customer: " + json(customerModel));

        String accountIdentifier = "account" + timestamp;
        CreateAccountRequestModel createAccountRequestModel = new CreateAccountRequestModel();
        createAccountRequestModel.setAccountIdentifier(accountIdentifier);
        createAccountRequestModel.setDisplayName(accountIdentifier);
        log.info("Create Account: " + json(createAccountRequestModel));
        AccountModel accountModel = raasClient.getAccounts().createAccount(customerIdentifier, createAccountRequestModel);
        log.info("Created Account: " + json(accountModel));

        List<AccountModel> accountModelList = raasClient.getAccounts().getAllAccounts();
        log.info("List Accounts: " + json(accountModelList));

        List<AccountSummaryModel> accountSummaryModelList = raasClient.getAccounts().getAccountsByCustomer(customerIdentifier);
        log.info("List Account Summary by Customer: " + json(accountSummaryModelList));

        // For creating an order, we need to use a funded account
        String fundedCustomerIdentifier = "boisetest";
        String fundedAccountIdentifier = "boisetest";

        String externalRefId = UUID.randomUUID().toString();

        NameEmailModel recipientNameEmailModel = new NameEmailModel();
        recipientNameEmailModel.setFirstName("Java");
        recipientNameEmailModel.setLastName("Tester");
        recipientNameEmailModel.setEmail("brady@tangocard.com");

        CreateOrderRequestModel createOrderRequestModel = new CreateOrderRequestModel();
        createOrderRequestModel.setCustomerIdentifier(fundedCustomerIdentifier);
        createOrderRequestModel.setAccountIdentifier(fundedAccountIdentifier);
        createOrderRequestModel.setRecipient(recipientNameEmailModel);
        createOrderRequestModel.setSendEmail(true);
//        createOrderRequestModel.setUtid("U666425"); // Amazon.com Variable item
//        createOrderRequestModel.setAmount(1.00);

        createOrderRequestModel.setUtid("U106098"); // 1-800 Flowers Fixed $50 item
        createOrderRequestModel.setExternalRefID(externalRefId);

        log.info("Create Order: " + json(createOrderRequestModel));
        OrderModel createdOrderModel = raasClient.getOrders().createOrder(createOrderRequestModel);
        log.info("Created Order: " + json(createdOrderModel));

        ResendOrderResponseModel resendOrderResponseModel =
            raasClient.getOrders().createResendOrder(createdOrderModel.getReferenceOrderID());
        log.info("Resend Order: " + json(resendOrderResponseModel));

        OrderModel orderModel = raasClient.getOrders().getOrder(createdOrderModel.getReferenceOrderID());
        log.info("Get Order: " + json(orderModel));

        GetOrdersInput getOrdersInput = new GetOrdersInput();
        GetOrdersResponseModel getOrdersResponseModel = raasClient.getOrders().getOrders(getOrdersInput);
        log.info("Get Orders: " + json(getOrdersResponseModel));

        GetOrdersInput getOrdersInputByExternalRefId = new GetOrdersInput();
        getOrdersInputByExternalRefId.setExternalRefID(externalRefId);

        GetOrdersResponseModel getOrdersByExternalRefIdResponseModel
            = raasClient.getOrders().getOrders(getOrdersInputByExternalRefId);
        log.info("Get Orders by externalRefId: " + json(getOrdersByExternalRefIdResponseModel));
    }

    private String json(Object object) throws Exception {
        return objectMapper.writeValueAsString(object);
    }

}
