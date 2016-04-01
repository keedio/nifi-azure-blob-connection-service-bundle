package org.keedio.nifi.controllers;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.annotation.lifecycle.OnEnabled;
import org.apache.nifi.components.AbstractConfigurableComponent;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.controller.ConfigurationContext;
import org.apache.nifi.controller.ControllerServiceInitializationContext;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.reporting.InitializationException;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by luca on 31/03/16.
 */
@Tags({ "Azure", "Blob", "shared", "connection", "service" })
@CapabilityDescription("Provides Azure Blob Storage Conectivity.")
public class AzureBlobConnectionServiceImpl extends AbstractConfigurableComponent implements AzureBlobConnectionService {

    private String identifier;
    private ComponentLog logger;
    private ControllerServiceInitializationContext context;

    private String storageConnectionString;
    private String containerName;

    private static final List<PropertyDescriptor> properties;
    static {
        final List<PropertyDescriptor> props = new ArrayList<>();

        props.add(AZURE_STORAGE_CONTAINER_NAME);
        props.add(AZURE_STORAGE_CONNECTION_STRING);

        properties = Collections.unmodifiableList(props);
    }

    @Override
    public CloudStorageAccount getStorageAccountFromConnectionString() throws IllegalArgumentException, URISyntaxException, InvalidKeyException {
        CloudStorageAccount storageAccount;
        try {
            storageAccount = CloudStorageAccount.parse(storageConnectionString);
        } catch (IllegalArgumentException | URISyntaxException e) {
            logger.error("Connection string specifies an invalid URI. Please confirm the connection string is in the Azure connection string format (http://msdn.microsoft.com/library/azure/ee758697.aspx). ", e);
            throw e;
        } catch (InvalidKeyException e) {
            logger.error("Connection string specifies an invalid key. Please confirm the AccountName and AccountKey in the connection string are valid.", e);
            throw e;
        }

        return storageAccount;
    }

    @Override
    public CloudBlobContainer getCloudBlobContainerReference() throws URISyntaxException, InvalidKeyException, StorageException {
        // Retrieve storage account information from connection string.
        CloudStorageAccount storageAccount = getStorageAccountFromConnectionString();

        // Create a blob client for interacting with the blob service
        CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

        // Create a new container
        CloudBlobContainer container = blobClient.getContainerReference(containerName);

        if (!container.exists()) {
            throw new IllegalStateException(String.format("Container with name \"%s\" does not exists.", containerName));
        }

        return container;
    }

    @Override
    public void initialize(ControllerServiceInitializationContext context) throws InitializationException {
        this.identifier = context.getIdentifier();
        this.logger = context.getLogger();
        this.context = context;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
        return properties;
    }

    @OnEnabled
    public void onConfigured(final ConfigurationContext context) throws InitializationException {
        storageConnectionString = context.getProperty(AZURE_STORAGE_CONNECTION_STRING).getValue();
        containerName = context.getProperty(AZURE_STORAGE_CONTAINER_NAME).getValue();
    }

    @Override
    public String getContainerName(){
        return containerName;
    }
}
