package com.nva;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.nva.core.Book;
import com.nva.core.DependencyFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import java.util.Collections;


/**
 * Handler for requests to Lambda function.
 */
public class PutItemHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    static final int STATUS_CODE_NO_CONTENT = 204;
    static final int STATUS_CODE_CREATED = 201;
    private final DynamoDbEnhancedClient dbClient;
    private final String tableName;
    private final TableSchema<Book> bookTableSchema;

    public PutItemHandler() {
        dbClient = DependencyFactory.dynamoDbEnhancedClient();
        tableName = DependencyFactory.tableName();;
        bookTableSchema = TableSchema.fromBean(Book.class);
    }

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        int statusCode = STATUS_CODE_NO_CONTENT;
        String body = input.getBody();
        Book book = new Gson().fromJson(body, Book.class);

        DynamoDbTable<Book> booksTable = dbClient.table(tableName, bookTableSchema);
        booksTable.putItem(book);
        statusCode = STATUS_CODE_CREATED;

        return new APIGatewayProxyResponseEvent().withStatusCode(statusCode)
                .withIsBase64Encoded(Boolean.FALSE)
                .withHeaders(Collections.emptyMap());


    }


}
