package org.keedio.nifi.controllers;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.controller.ControllerService;
import org.apache.nifi.processor.util.StandardValidators;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;

/**
 * Created by luca on 31/03/16.
 */
@Tags({ "Azure", "Blob", "shared", "connection", "service" })
@CapabilityDescription("Provides Azure Blob Storage Conectivity.")
public interface AzureBlobConnectionService extends ControllerService {
    PropertyDescriptor AZURE_STORAGE_CONNECTION_STRING = new PropertyDescriptor.Builder()
            .name("Azure Storage container connection string")
            .description("The connection string for the storage container as provided by Azure portal. Example: DefaultEndpointsProtocol=https;AccountName=my_storage_account_name;AccountKey=my_storage_account_key;BlobEndpoint=https://my_storage_account.blob.core.windows.net/")
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .required(true)
            .build();

    PropertyDescriptor AZURE_STORAGE_CONTAINER_NAME = new PropertyDescriptor.Builder()
                    .name("Azure Storage container name")
                    .description("The name of the storage container as provided by Azure portal.")
                    .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
                    .required(true)
                    .build();

    CloudStorageAccount getStorageAccountFromConnectionString() throws IllegalArgumentException, URISyntaxException, InvalidKeyException;
    CloudBlobContainer getCloudBlobContainerReference() throws URISyntaxException, InvalidKeyException, StorageException;

    String getContainerName();
}
